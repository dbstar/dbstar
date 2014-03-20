/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/mount.h>  // for _IOW, _IOR, mount()
#include <sys/stat.h>
#include <mtd/mtd-user.h>
#undef NDEBUG
#include <assert.h>

#include "mtdutils.h"
#include "softdmx.h"

struct MtdPartition {
    int device_index;
    long long size;
    long long erase_size;
    char *name;
};

struct MtdReadContext {
    const MtdPartition *partition;
    char *buffer;
    size_t consumed;
    int fd;
};

struct MtdWriteContext {
    const MtdPartition *partition;
    char *buffer;
    size_t stored;
    int fd;

    off_t* bad_block_offsets;
    int bad_block_alloc;
    int bad_block_count;
};

typedef struct {
    MtdPartition *partitions;
    int partitions_allocd;
    int partition_count;
} MtdState;

static MtdState g_mtd_state = {
    NULL,   // partitions
    0,      // partitions_allocd
    -1      // partition_count
};

#define MTD_PROC_FILENAME   "/proc/mtd"

int
mtd_scan_partitions()
{
    char buf[2048];
    const char *bufp;
    int fd;
    int i;
    ssize_t nbytes;

    if (g_mtd_state.partitions == NULL) {
        const int nump = 32;
        MtdPartition *partitions = malloc(nump * sizeof(*partitions));
        if (partitions == NULL) {
            errno = ENOMEM;
            return -1;
        }
        g_mtd_state.partitions = partitions;
        g_mtd_state.partitions_allocd = nump;
        memset(partitions, 0, nump * sizeof(*partitions));
    }
    g_mtd_state.partition_count = 0;

    /* Initialize all of the entries to make things easier later.
     * (Lets us handle sparsely-numbered partitions, which
     * may not even be possible.)
     */
    for (i = 0; i < g_mtd_state.partitions_allocd; i++) {
        MtdPartition *p = &g_mtd_state.partitions[i];
        if (p->name != NULL) {
            free(p->name);
            p->name = NULL;
        }
        p->device_index = -1;
    }

    /* Open and read the file contents.
     */
    fd = open(MTD_PROC_FILENAME, O_RDONLY);
    if (fd < 0) {
        goto bail;
    }
    nbytes = read(fd, buf, sizeof(buf) - 1);
    close(fd);
    if (nbytes < 0) {
        goto bail;
    }
    buf[nbytes] = '\0';

    /* Parse the contents of the file, which looks like:
     *
     *     # cat /proc/mtd
     *     dev:    size   erasesize  name
     *     mtd0: 00080000 00020000 "bootloader"
     *     mtd1: 00400000 00020000 "mfg_and_gsm"
     *     mtd2: 00400000 00020000 "0000000c"
     *     mtd3: 00200000 00020000 "0000000d"
     *     mtd4: 04000000 00020000 "system"
     *     mtd5: 03280000 00020000 "userdata"
     */
    bufp = buf;
    while (nbytes > 0) {
        int mtdnum;
	long long mtdsize, mtderasesize;
        int matches;
        char mtdname[64];
        mtdname[0] = '\0';
        mtdnum = -1;

        matches = sscanf(bufp, "mtd%d: %llx %llx \"%63[^\"]",
                &mtdnum, &mtdsize, &mtderasesize, mtdname);
        /* This will fail on the first line, which just contains
         * column headers.
         */
        if (matches == 4) {
            MtdPartition *p = &g_mtd_state.partitions[mtdnum];
            p->device_index = mtdnum;
            p->size = mtdsize;
            p->erase_size = mtderasesize;
            p->name = strdup(mtdname);
            if (p->name == NULL) {
                errno = ENOMEM;
                goto bail;
            }
            g_mtd_state.partition_count++;
        }

        /* Eat the line.
         */
        while (nbytes > 0 && *bufp != '\n') {
            bufp++;
            nbytes--;
        }
        if (nbytes > 0) {
            bufp++;
            nbytes--;
        }
    }

    return g_mtd_state.partition_count;

bail:
    // keep "partitions" around so we can free the names on a rescan.
    g_mtd_state.partition_count = -1;
    return -1;
}

const MtdPartition *
mtd_find_partition_by_name(const char *name)
{
    if (g_mtd_state.partitions != NULL) {
        int i;
        for (i = 0; i < g_mtd_state.partitions_allocd; i++) {
            MtdPartition *p = &g_mtd_state.partitions[i];
            if (p->device_index >= 0 && p->name != NULL) {
                if (strcmp(p->name, name) == 0) {
                    return p;
                }
            }
        }
    }
    return NULL;
}

int
mtd_get_index_by_name(const char *name)
{
    if (g_mtd_state.partitions != NULL) {
        int i;
        for (i = 0; i < g_mtd_state.partitions_allocd; i++) {
            MtdPartition *p = &g_mtd_state.partitions[i];
            if (p->device_index >= 0 && p->name != NULL) {
                if (strcmp(p->name, name) == 0) {
                    return p->device_index;
                }
            }
        }
    }
    return -1;
}

int
mtd_mount_partition(const MtdPartition *partition, const char *mount_point,
        const char *filesystem, int read_only)
{
    const unsigned long flags = MS_NOATIME | MS_NODEV | MS_NODIRATIME;
    char devname[64];
    int rv = -1;

    sprintf(devname, "/dev/block/mtdblock%d", partition->device_index);
    if (!read_only) {
        rv = mount(devname, mount_point, filesystem, flags, NULL);
    }
    if (read_only || rv < 0) {
        rv = mount(devname, mount_point, filesystem, flags | MS_RDONLY, 0);
        if (rv < 0) {
            printf("Failed to mount %s on %s: %s\n",
                    devname, mount_point, strerror(errno));
        } else {
            printf("Mount %s on %s read-only\n", devname, mount_point);
        }
    }
#if 1   //TODO: figure out why this is happening; remove include of stat.h
    if (rv >= 0) {
        /* For some reason, the x bits sometimes aren't set on the root
         * of mounted volumes.
         */
        struct stat st;
        rv = stat(mount_point, &st);
        if (rv < 0) {
            return rv;
        }
        mode_t new_mode = st.st_mode | S_IXUSR | S_IXGRP | S_IXOTH;
        if (new_mode != st.st_mode) {
printf("Fixing execute permissions for %s\n", mount_point);
            rv = chmod(mount_point, new_mode);
            if (rv < 0) {
                printf("Couldn't fix permissions for %s: %s\n",
                        mount_point, strerror(errno));
            }
        }
    }
#endif
    return rv;
}

int
mtd_partition_info(const MtdPartition *partition,
        size_t *total_size, size_t *erase_size, size_t *write_size)
{
    char mtddevname[32];
    sprintf(mtddevname, "/dev/mtd/mtd%d", partition->device_index);
    int fd = open(mtddevname, O_RDONLY);
    if (fd < 0) return -1;

    struct mtd_info_user mtd_info;
    int ret = ioctl(fd, MEMGETINFO, &mtd_info);
    close(fd);
    if (ret < 0) return -1;

    if (total_size != NULL) *total_size = mtd_info.size;
    if (erase_size != NULL) *erase_size = mtd_info.erasesize;
    if (write_size != NULL) *write_size = mtd_info.writesize;
    return 0;
}

MtdReadContext *mtd_read_partition(const MtdPartition *partition)
{
    MtdReadContext *ctx = (MtdReadContext*) malloc(sizeof(MtdReadContext));
    if (ctx == NULL) return NULL;

    ctx->buffer = malloc(partition->erase_size);
    if (ctx->buffer == NULL) {
        free(ctx);
        return NULL;
    }

    char mtddevname[32];
    sprintf(mtddevname, "/dev/mtd/mtd%d", partition->device_index);
    ctx->fd = open(mtddevname, O_RDONLY);
    if (ctx->fd < 0) {
        free(ctx->buffer);
        free(ctx);
        return NULL;
    }

    ctx->partition = partition;
    ctx->consumed = partition->erase_size;
    return ctx;
}

// Seeks to a location in the partition.  Don't mix with reads of
// anything other than whole blocks; unpredictable things will result.
void mtd_read_skip_to(const MtdReadContext* ctx, size_t offset) {
    lseek64(ctx->fd, offset, SEEK_SET);
}

static int read_block(const MtdPartition *partition, int fd, char *data)
{
    struct mtd_ecc_stats before, after;
    if (ioctl(fd, ECCGETSTATS, &before)) {
        fprintf(stderr, "mtd: ECCGETSTATS error (%s)\n", strerror(errno));
        return -1;
    }

    loff_t pos = lseek64(fd, 0, SEEK_CUR);

    ssize_t size = partition->erase_size;
    int mgbb;

    while (pos + size <= (int) partition->size) {
		if ((mgbb = ioctl(fd, MEMGETBADBLOCK, &pos))) {
            fprintf(stderr, "mtd: MEMGETBADBLOCK returned %d at 0x%08llx (errno=%d)\n",
                    mgbb, pos, errno);
			ioctl(fd, ECCGETSTATS, &after);
			before = after;
			pos += partition->erase_size;
			continue;
        }
        if (lseek64(fd, pos, SEEK_SET) != pos || read(fd, data, size) != size) {
            fprintf(stderr, "mtd: read error at 0x%08llx (%s)\n",
                    pos, strerror(errno));
        } else if (ioctl(fd, ECCGETSTATS, &after)) {
            fprintf(stderr, "mtd: ECCGETSTATS error (%s)\n", strerror(errno));
            return -1;
        } else if (after.failed != before.failed) {
            fprintf(stderr, "mtd: ECC errors (%d soft, %d hard) at 0x%08llx\n",
                    after.corrected - before.corrected,
                    after.failed - before.failed, pos);
            // copy the comparison baseline for the next read.
            memcpy(&before, &after, sizeof(struct mtd_ecc_stats));
        } else {
            return 0;  // Success!
        }

        before = after;
        pos += partition->erase_size;
    }

    errno = ENOSPC;
    return -1;
}

ssize_t mtd_read_data(MtdReadContext *ctx, char *data, size_t len)
{
    ssize_t read = 0;
    while (read < (int) len) {
        if (ctx->consumed < ctx->partition->erase_size) {
            size_t avail = ctx->partition->erase_size - ctx->consumed;
            size_t copy = len - read < avail ? len - read : avail;
            memcpy(data + read, ctx->buffer + ctx->consumed, copy);
            ctx->consumed += copy;
            read += copy;
        }

        // Read complete blocks directly into the user's buffer
        while (ctx->consumed == ctx->partition->erase_size &&
               len - read >= ctx->partition->erase_size) {
            if (read_block(ctx->partition, ctx->fd, data + read)) return -1;
            read += ctx->partition->erase_size;
        }

        if (read >= len) {
            return read;
        }

        // Read the next block into the buffer
        if (ctx->consumed == ctx->partition->erase_size && read < (int) len) {
            if (read_block(ctx->partition, ctx->fd, ctx->buffer)) return -1;
            ctx->consumed = 0;
        }
    }

    return read;
}

void mtd_read_close(MtdReadContext *ctx)
{
    close(ctx->fd);
    free(ctx->buffer);
    free(ctx);
}

MtdWriteContext *mtd_write_partition(const MtdPartition *partition)
{
    MtdWriteContext *ctx = (MtdWriteContext*) malloc(sizeof(MtdWriteContext));
    if (ctx == NULL) return NULL;

    ctx->bad_block_offsets = NULL;
    ctx->bad_block_alloc = 0;
    ctx->bad_block_count = 0;

    ctx->buffer = malloc(partition->erase_size);
    if (ctx->buffer == NULL) {
        free(ctx);
        return NULL;
    }

    char mtddevname[32];
    sprintf(mtddevname, "/dev/mtd/mtd%d", partition->device_index);
    ctx->fd = open(mtddevname, O_RDWR);
    if (ctx->fd < 0) {
        free(ctx->buffer);
        free(ctx);
        return NULL;
    }

    ctx->partition = partition;
    ctx->stored = 0;
    return ctx;
}

static void add_bad_block_offset(MtdWriteContext *ctx, off_t pos) {
    if (ctx->bad_block_count + 1 > ctx->bad_block_alloc) {
        ctx->bad_block_alloc = (ctx->bad_block_alloc*2) + 1;
        ctx->bad_block_offsets = realloc(ctx->bad_block_offsets,
                                         ctx->bad_block_alloc * sizeof(off_t));
    }
    ctx->bad_block_offsets[ctx->bad_block_count++] = pos;
}

static int write_block(MtdWriteContext *ctx, const char *data)
{
    const MtdPartition *partition = ctx->partition;
    int fd = ctx->fd;
    char *verify = NULL;

    off_t pos = lseek(fd, 0, SEEK_CUR);
    if (pos == (off_t) -1) return 1;

    ssize_t size = partition->erase_size;
    while (pos + size <= (int) partition->size) {
        loff_t bpos = pos;
        int ret = ioctl(fd, MEMGETBADBLOCK, &bpos);
        if (ret != 0 && !(ret == -1 && errno == EOPNOTSUPP)) {
            add_bad_block_offset(ctx, pos);
            fprintf(stderr,
                    "mtd: not writing bad block at 0x%08lx (ret %d errno %d)\n",
                    pos, ret, errno);
            pos += partition->erase_size;
            continue;  // Don't try to erase known factory-bad blocks.
        }

        struct erase_info_user erase_info;
        erase_info.start = pos;
        erase_info.length = size;
        int retry;
        for (retry = 0; retry < 2; ++retry) {
            if (ioctl(fd, MEMERASE, &erase_info) < 0) {
                fprintf(stderr, "mtd: erase failure at 0x%08lx (%s)\n",
                        pos, strerror(errno));
                continue;
            }
            if (lseek(fd, pos, SEEK_SET) != pos ||
                write(fd, data, size) != size) {
                fprintf(stderr, "mtd: write error at 0x%08lx (%s)\n",
                        pos, strerror(errno));
            }

            verify = malloc(size);
            if (verify == NULL) {
                fprintf(stderr, "mtd: failed to malloc size=%lu (%s)\n", size, strerror(errno));
                return -1;
            }
            if (lseek(fd, pos, SEEK_SET) != pos ||
                read(fd, verify, size) != size) {
                fprintf(stderr, "mtd: re-read error at 0x%08lx (%s)\n",
                        pos, strerror(errno));
                if (verify)
                    free(verify);
                continue;
            }
            if (memcmp(data, verify, size) != 0) {
                fprintf(stderr, "mtd: verification error at 0x%08lx (%s)\n",
                        pos, strerror(errno));
                if (verify)
                    free(verify);
                continue;
            }

            if (retry > 0) {
                fprintf(stderr, "mtd: wrote block after %d retries\n", retry);
            }
            fprintf(stderr, "mtd: successfully wrote block at %lu\n", pos);
            if (verify)
                free(verify);
            return 0;  // Success!
        }

        // Try to erase it once more as we give up on this block
        add_bad_block_offset(ctx, pos);
        fprintf(stderr, "mtd: skipping write block at 0x%08lx\n", pos);
        ioctl(fd, MEMERASE, &erase_info);
        pos += partition->erase_size;
    }

    // Ran out of space on the device
    errno = ENOSPC;
    return -1;
}

ssize_t mtd_write_data(MtdWriteContext *ctx, const char *data, size_t len)
{
    size_t wrote = 0;
    while (wrote < len) {
        // Coalesce partial writes into complete blocks
        if (ctx->stored > 0 || len - wrote < ctx->partition->erase_size) {
            size_t avail = ctx->partition->erase_size - ctx->stored;
            size_t copy = len - wrote < avail ? len - wrote : avail;
            memcpy(ctx->buffer + ctx->stored, data + wrote, copy);
            ctx->stored += copy;
            wrote += copy;
        }

        // If a complete block was accumulated, write it
        if (ctx->stored == ctx->partition->erase_size) {
            if (write_block(ctx, ctx->buffer)) return -1;
            ctx->stored = 0;
        }

        // Write complete blocks directly from the user's buffer
        while (ctx->stored == 0 && len - wrote >= ctx->partition->erase_size) {
            if (write_block(ctx, data + wrote)) return -1;
            wrote += ctx->partition->erase_size;
        }
    }

    return wrote;
}

off_t mtd_erase_blocks(MtdWriteContext *ctx, int blocks)
{
    // Zero-pad and write any pending data to get us to a block boundary
    if (ctx->stored > 0) {
        size_t zero = ctx->partition->erase_size - ctx->stored;
        memset(ctx->buffer + ctx->stored, 0, zero);
        if (write_block(ctx, ctx->buffer)) return -1;
        ctx->stored = 0;
    }

    off_t pos = lseek(ctx->fd, 0, SEEK_CUR);
    if ((off_t) pos == (off_t) -1) return pos;

    const int total = (ctx->partition->size - pos) / ctx->partition->erase_size;
    if (blocks < 0) blocks = total;
    if (blocks > total) {
        errno = ENOSPC;
        return -1;
    }

    // Erase the specified number of blocks
    while (blocks-- > 0) {
        loff_t bpos = pos;
        if (ioctl(ctx->fd, MEMGETBADBLOCK, &bpos) > 0) {
            fprintf(stderr, "mtd: not erasing bad block at 0x%08lx\n", pos);
            pos += ctx->partition->erase_size;
            continue;  // Don't try to erase known factory-bad blocks.
        }

        struct erase_info_user erase_info;
        erase_info.start = pos;
        erase_info.length = ctx->partition->erase_size;
        if (ioctl(ctx->fd, MEMERASE, &erase_info) < 0) {
            fprintf(stderr, "mtd: erase failure at 0x%08lx\n", pos);
        }
        pos += ctx->partition->erase_size;
    }

    return pos;
}

int mtd_write_close(MtdWriteContext *ctx)
{
    int r = 0;
    // Make sure any pending data gets written
    if (mtd_erase_blocks(ctx, 0) == (off_t) -1) r = -1;
    if (close(ctx->fd)) r = -1;
    free(ctx->bad_block_offsets);
    free(ctx->buffer);
    free(ctx);
    return r;
}

/* Return the offset of the first good block at or after pos (which
 * might be pos itself).
 */
off_t mtd_find_write_start(MtdWriteContext *ctx, off_t pos) {
    int i;
    for (i = 0; i < ctx->bad_block_count; ++i) {
        if (ctx->bad_block_offsets[i] == pos) {
            pos += ctx->partition->erase_size;
        } else if (ctx->bad_block_offsets[i] > pos) {
            return pos;
        }
    }
    return pos;
}

#define FILE_COPY_BUFFER_SIZE	0x100000

int file_copy_from_partition(const char *dest_path, const char *partition_type, const char *partition, ssize_t offset, ssize_t file_size)
{
	int result = -1;
	FILE* f_dest = NULL;


	if(!strcmp(partition_type, "MTD"))
	{
		MtdReadContext* ctx = NULL;

		mtd_scan_partitions();


	    const MtdPartition* mtd = mtd_find_partition_by_name(partition);
	    if (mtd == NULL) {
	        fprintf(stderr, "mtd partition \"%s\" not found!\n", partition);
	        return -1;
	    }

	    ctx = mtd_read_partition(mtd);
	    if (ctx == NULL) {
	        fprintf(stderr, "failed to initialize read of mtd partition \"%s\"\n",
	               partition);
	        return -1;
	    }

	    void *buffer = malloc(ctx->partition->erase_size);
	    
		f_dest = fopen(dest_path, "wb+");
	    if (f_dest == NULL) {
	        fprintf(stderr, "can't open %s: %s\n", dest_path, strerror(errno));
	        result = errno;
	        goto done_MTD;
	    }

		lseek64(ctx->fd, offset, SEEK_SET);

		ssize_t read = 0;
	    while (read < file_size)
		{
			if (ctx->consumed < ctx->partition->erase_size) {
	            size_t avail = ctx->partition->erase_size - ctx->consumed;
	            size_t copy = file_size - read < avail ? file_size - read : avail;

				if(copy != fwrite(ctx->buffer + ctx->consumed, 1, copy, f_dest))
				{
					fprintf(stderr, "can't write %s: %s\n", dest_path, strerror(errno));
				}
	            ctx->consumed += copy;
	            read += copy;
	        }

	        // Read complete blocks directly into the user's buffer
	        while (ctx->consumed == ctx->partition->erase_size &&
	               file_size - read >= ctx->partition->erase_size)
			{
				if (read_block(ctx->partition, ctx->fd, buffer)) return -1;
				if(ctx->partition->erase_size != fwrite(buffer, 1, ctx->partition->erase_size, f_dest))
				{
					fprintf(stderr, "can't write %s: %s\n", dest_path, strerror(errno));
				}
	            read += ctx->partition->erase_size;
	        }

	        if (read >= file_size)
			{
	            result = read;
				goto done_MTD;
	        }

	        // Read the next block into the buffer
	        if (ctx->consumed == ctx->partition->erase_size && read < (int) file_size)
			{
				if (read_block(ctx->partition, ctx->fd, ctx->buffer)) return -1;
	            ctx->consumed = 0;
	        }
	    }

done_MTD:
		mtd_read_close(ctx);
		if(buffer)
		{
			free(buffer);
			buffer = NULL;
		}
		if(f_dest)
		{
			fclose(f_dest);
			f_dest = NULL;
		}
	}
	else if(!strcmp(partition_type, "eMMC"))
	{
		FILE* f_src = NULL;
		void *buffer = NULL;
		f_src = fopen(partition, "rb");
	    if (f_src == NULL) {
	        fprintf(stderr, "can't open eMMC parition %s: %s\n", dest_path, strerror(errno));
	        result = errno;
	        goto done_eMMC;
	    }
		
		f_dest = fopen(dest_path, "wb+");
	    if (f_dest == NULL) {
	        fprintf(stderr, "can't open %s: %s\n", dest_path, strerror(errno));
	        result = errno;
	        goto done_eMMC;
	    }
	    
		buffer = malloc(FILE_COPY_BUFFER_SIZE);
		ssize_t read = 0;
		ssize_t copy = 0;
	    while (read < file_size)
		{
			if((read+FILE_COPY_BUFFER_SIZE) < file_size)
			{
				copy = FILE_COPY_BUFFER_SIZE;
			}
			else
			{
				copy = file_size - read;
			}
			if(copy != fread(buffer, 1, copy, f_src))
			{
				fprintf(stderr, "can't read %s: %s\n", partition, strerror(errno));
				result = errno;
				goto done_eMMC;
			}
			if(copy != fwrite(buffer, 1, copy, f_dest))
			{
				fprintf(stderr, "can't read %s: %s\n", dest_path, strerror(errno));
				result = errno;
				goto done_eMMC;
			}
			read += copy;
			if (read >= file_size)
			{
				result = read;
				goto done_eMMC;
	        }
		}

		done_eMMC:
			
		if(buffer)
		{
			free(buffer);
			buffer = NULL;
		}
		if(f_src)
		{
			fclose(f_src);
			f_src = NULL;
		}

		if(f_dest)
		{
			fclose(f_dest);
			f_dest = NULL;
		}
	}
	printf("file_copy_from_partition finish! result=%d\n", result);
	return result;
}

//=======================liukevin add interface for mtd read write
#define LOADER_NAME "loader"
static const int LOADER_PAGES = 3;         // number of pages to save
static const int LOADER_INFO_PAGE = 1;  // bootloader command is this page

int get_loader_message(unsigned char *mark, LoaderInfo_t *out) //, const Volume *v) 
{
    size_t write_size;
    mtd_scan_partitions();
    const MtdPartition *part = mtd_find_partition_by_name(LOADER_NAME);//v->device);
    if (part == NULL || mtd_partition_info(part, NULL, NULL, &write_size)) {
        //LOGE("Can't find %s\n", v->device);
        return -1;
    }

    MtdReadContext *read = mtd_read_partition(part);
    if (read == NULL) {
        //LOGE("Can't open %s\n(%s)\n", v->device, strerror(errno));
        return -1;
    }

    const ssize_t size = write_size * LOADER_PAGES;
    char data[size];
    ssize_t r = mtd_read_data(read, data, size);
    if (r != size) printf("Can't read %s\n(%s)\n", LOADER_NAME, strerror(errno));
    mtd_read_close(read);
    if (r != size) return -1;

    memcpy(out, &data[write_size * LOADER_INFO_PAGE], sizeof(*out));
    *mark = data[0];
    return 0;
}

int set_loader_reboot_mark(unsigned char mark) //, const Volume* v) 
{
    size_t write_size;
    mtd_scan_partitions();
    const MtdPartition *part = mtd_find_partition_by_name(LOADER_NAME); //v->device);
    if (part == NULL || mtd_partition_info(part, NULL, NULL, &write_size)) {
        //LOGE("Can't find %s\n", v->device);
        return -1;
    }
printf("liukevin find the partition\n");
    MtdReadContext *read = mtd_read_partition(part);
    if (read == NULL) {
        //LOGE("Can't open %s\n(%s)\n", v->device, strerror(errno));
        return -1;
    }
    printf("liukevin read the partition\n");
    ssize_t size = write_size * LOADER_PAGES;
    char data[size];
    ssize_t r = mtd_read_data(read, data, size);

	printf("liukevin write size = [%lu][%d]\n",size, write_size);
	    if (r != size) printf("Can't read %s\n(%s)\n",LOADER_NAME, strerror(errno));
    mtd_read_close(read);
    if (r != size) return -1;

    //memcpy(&data[write_size * LOADER_INFO_PAGE], in, sizeof(*in));
//printf("liukevin write the partion\n");
    MtdWriteContext *write = mtd_write_partition(part);
    if (write == NULL) {
        //LOGE("Can't open %s\n(%s)\n", v->device, strerror(errno));
        return -1;
    }
    data[0]=mark;
/*data[1]=0x34;
data[2]=0x56;
data[3]=0x78;*/
    if (mtd_write_data(write, data, size) != size) {
        //LOGE("Can't write %s\n(%s)\n", v->device, strerror(errno));
        mtd_write_close(write);
        return -1;
    }
    if (mtd_write_close(write)) {
        //LOGE("Can't finish %s\n(%s)\n", v->device, strerror(errno));
        return -1;
    }
printf("liukevin wirte partion successful\n");
    //LOGI("Set boot command \"%s\"\n", in->command[0] != 255 ? in->command : "");
    return 0;
}


int set_loader_message(unsigned char mark, const LoaderInfo_t *in) //,const Volume* v) {
{
    size_t write_size;
    mtd_scan_partitions();
    const MtdPartition *part = mtd_find_partition_by_name(LOADER_NAME);//v->device);
    if (part == NULL || mtd_partition_info(part, NULL, NULL, &write_size)) {
        //LOGE("Can't find %s\n", v->device);
        return -1;
    }
printf("liukevin find the partition\n");
    MtdReadContext *read = mtd_read_partition(part);
    if (read == NULL) {
        //LOGE("Can't open %s\n(%s)\n", v->device, strerror(errno));
        return -1;
    }
printf("liukevin read the partition\n");
    ssize_t size = write_size * LOADER_PAGES;
    char data[size];
    ssize_t r = mtd_read_data(read, data, size);

printf("liukevin write size = [%lu][%d]\n",size, write_size);
    if (r != size) 
    {
        printf("Can't read %s\n(%s)\n",LOADER_NAME, strerror(errno));
    }
    mtd_read_close(read);
    if (r != size) return -1;

    memcpy(&data[write_size * LOADER_INFO_PAGE], in, sizeof(*in));
printf("liukevin write the partion\n");
    MtdWriteContext *write = mtd_write_partition(part);
    if (write == NULL) {
        //LOGE("Can't open %s\n(%s)\n", v->device, strerror(errno));
        return -1;
    }
/*{
int i;

for (i=0;i<size;i++)
    data[i]=0xa5;

}*/
data[0]=mark;
/*data[1]=0x34;
data[2]=0x56;
data[3]=0x78;*/
    if (mtd_write_data(write, data, size) != size) {
        //LOGE("Can't write %s\n(%s)\n", v->device, strerror(errno));
        mtd_write_close(write);
        return -1;
    }
    if (mtd_write_close(write)) {
        //LOGE("Can't finish %s\n(%s)\n", v->device, strerror(errno));
        return -1;
    }
printf("liukevin wirte partion successful\n");
    //LOGI("Set boot command \"%s\"\n", in->command[0] != 255 ? in->command : "");
    return 0;
}
#if 0
int set_loader_message(unsigned char mark, const LoaderInfo_t *in) {
    Volume* v = volume_for_path("/loader");
    if (v == NULL) {
      //LOGE("Cannot load volume /misc!\n");
      return -1;
    }
printf("liukevin setloadermessage find volume\n");
    if (strcmp(v->fs_type, "mtd") == 0) {
        return set_loader_message_mtd(mark, in, v);
    } 
    //LOGE("unknown misc partition fs_type \"%s\"\n", v->fs_type);
    return -1;
}

int set_loader_reboot_mark(unsigned char mark) 
{
    Volume* v = volume_for_path("/loader");
    if (v == NULL) {
      //LOGE("Cannot load volume /misc!\n");
      return -1;
    }
printf("liukevin setloadermessage find volume\n");
    if (strcmp(v->fs_type, "mtd") == 0) {
        return set_loader_reboot_mark_mtd(mark, v);
    }
    //LOGE("unknown misc partition fs_type \"%s\"\n", v->fs_type);
    return -1;
}

int get_loader_message(unsigned char *mark, LoaderInfo_t *out) 
{
    Volume* v = volume_for_path("/loader");
    if (v == NULL) {
      //LOGE("Cannot load volume /misc!\n");
      return -1;
    }
    if (strcmp(v->fs_type, "mtd") == 0) {
        return get_loader_message_mtd(mark, out, v);
    }
    //LOGE("unknown misc partition fs_type \"%s\"\n", v->fs_type);
    return -1;
}
#endif
