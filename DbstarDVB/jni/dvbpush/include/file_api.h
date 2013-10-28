#ifndef _FILE_API_H_
#define _FILE_API_H_

typedef long long off_t64;

struct tc_buf {
	unsigned char *_base;
	int	_size;
};

typedef	struct tc_FILE {
	unsigned char *_p;	
	int	_r;		
	int	_w;		
	short	_flags;		
	short	_file;		
	struct	tc_buf _bf;	
	int	_lbfsize;	

	/* operations */
	void	*_cookie;	
	int	(*_close)(void *);
	int	(*_read)(void *, char *, int);
	off_t64	(*_seek)(void *, off_t64, int);
	int	(*_write)(void *, const char *, int);

	/* extension data, to avoid further ABI breakage */
	struct	tc_buf _ext;
	/* data for long sequences of ungetc() */
	unsigned char *_up;	/* saved _p when _p is doing ungetc data */
	int	_ur;		/* saved _r when _r is counting ungetc data */

	/* tricks to meet minimum requirements even when malloc() fails */
	unsigned char _ubuf[3];	/* guarantee an ungetc() buffer */
	unsigned char _nbuf[1];	/* guarantee a getc() buffer */

	/* separate buffer for fgetln() when line crosses buffer boundary */
	struct	tc_buf _lb;	/* buffer for fgetln() */

	/* Unix stdio files get aligned to block boundaries on fseek() */
	int	_blksize;	/* stat.st_blksize (may be != _bf._size) */
	off_t64	_offset;	/* current lseek offset */
        int reserv;
} FILE64;

extern "C" {

FILE64 *tc_fopen1(const char * path,const char * mode);
int tc_fclose1(FILE64 *stream);
size_t tc_fwrite1(const void* buffer, size_t size, size_t count, FILE64 *stream);
int tc_fseeko1(FILE64 *stream, off_t64 offset, int whence);
off_t64 tc_ftello1(FILE64 *stream);
int tc_fgetpos1(FILE64 *stream, off_t64 *pos);
int tc_fsetpos1(FILE64 *stream, const off_t64 *pos);
size_t tc_fread1(void *buf, size_t size, size_t count, FILE64 *fp);
int tc_fflush(FILE64 *fp);

}

#endif

