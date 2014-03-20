/*
 * Copyright (C) 2008 The Android Open Source Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the 
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

//#include "inttypes.h"
#include <stdio.h>
#include <stdlib.h>
//#include <string.h>
#include "sha.h"
#include "common.h"

#define rol(bits, value) (((value) << (bits)) | ((value) >> (32 - (bits))))

static void SHA1_transform(SHA_CTX *ctx) {
    uint32_t W[80];
    uint32_t A, B, C, D, E;
    uint8_t *p = ctx->buf;
    int t;

    for(t = 0; t < 16; ++t) {
        uint32_t tmp =  *p++ << 24;
        tmp |= *p++ << 16;
        tmp |= *p++ << 8;
        tmp |= *p++;
        W[t] = tmp;
    }

    for(; t < 80; t++) {
        W[t] = rol(1,W[t-3] ^ W[t-8] ^ W[t-14] ^ W[t-16]);
    }

    A = ctx->state[0];
    B = ctx->state[1];
    C = ctx->state[2];
    D = ctx->state[3];
    E = ctx->state[4];

    for(t = 0; t < 80; t++) {
        uint32_t tmp = rol(5,A) + E + W[t];

        if (t < 20)
            tmp += (D^(B&(C^D))) + 0x5A827999;
        else if ( t < 40)
            tmp += (B^C^D) + 0x6ED9EBA1;
        else if ( t < 60)
            tmp += ((B&C)|(D&(B|C))) + 0x8F1BBCDC;
        else
            tmp += (B^C^D) + 0xCA62C1D6;

        E = D;
        D = C;
        C = rol(30,B);
        B = A;
        A = tmp;
    }

    ctx->state[0] += A;
    ctx->state[1] += B;
    ctx->state[2] += C;
    ctx->state[3] += D;
    ctx->state[4] += E;
}

void SHA_init(SHA_CTX *ctx) {
    ctx->state[0] = 0x67452301;
    ctx->state[1] = 0xEFCDAB89;
    ctx->state[2] = 0x98BADCFE;
    ctx->state[3] = 0x10325476;
    ctx->state[4] = 0xC3D2E1F0;
    ctx->count = 0;
}

void SHA_update(SHA_CTX *ctx, const void *data, int len) {
    int i = ctx->count % sizeof(ctx->buf);
    const uint8_t* p = (const uint8_t*)data;

    ctx->count += len;

    while (len--) {
        ctx->buf[i++] = *p++;
        if (i == sizeof(ctx->buf)) {
            SHA1_transform(ctx);
            i = 0;
        }
    }
}
const uint8_t *SHA_final(SHA_CTX *ctx) {
    uint8_t *p = ctx->buf;
    uint64_t cnt = ctx->count * 8;
    int i;

    SHA_update(ctx, (uint8_t*)"\x80", 1);
    while ((ctx->count % sizeof(ctx->buf)) != (sizeof(ctx->buf) - 8)) {
        SHA_update(ctx, (uint8_t*)"\0", 1);
    }
    for (i = 0; i < 8; ++i) {
        uint8_t tmp = cnt >> ((7 - i) * 8);
        SHA_update(ctx, &tmp, 1);
    }

    for (i = 0; i < 5; i++) {
        uint32_t tmp = ctx->state[i];
        *p++ = tmp >> 24;
        *p++ = tmp >> 16;
        *p++ = tmp >> 8;
        *p++ = tmp >> 0;
    }

    return ctx->buf;
}

/* Convenience function */
const uint8_t* SHA(const void *data, int len, uint8_t *digest) {
    const uint8_t *p;
    int i;
    SHA_CTX ctx;
    SHA_init(&ctx);
    SHA_update(&ctx, data, len);
    p = SHA_final(&ctx);
    for (i = 0; i < SHA_DIGEST_SIZE; ++i) {
        digest[i] = *p++;
    }
    return digest;
}

#define BUFFER_SIZE 4096

int sha_verify(FILE *f,  uint8_t*sha0, size_t signed_len)
{
	SHA_CTX ctx;
	SHA_init(&ctx);
	//FILE *f = fopen(name,"r");
	if(NULL==f){
		DEBUG("can not do sha verify with NULL file handle\n");
		return -1;
	}
	
	unsigned char* buffer = malloc(BUFFER_SIZE);
	if (NULL==buffer){
		DEBUG("failed to alloc memory for sha1 buffer\n");
		//fclose(f);
		return -1; //VERIFY_FAILURE;
	}
	
	double frac = -1.0;
	size_t so_far = 0;
	//   fseek(f, 0, SEEK_SET);
	while (so_far < signed_len) {
		size_t size = BUFFER_SIZE;
		if (signed_len - so_far < size)
			size = signed_len - so_far;
		if (fread(buffer, 1, size, f) != size) {
			DEBUG("failed to read data: %s\n", strerror(errno));
			//         fclose(f);
			free(buffer);
			buffer = NULL;
			return -1;//VERIFY_FAILURE;
		}
		SHA_update(&ctx, buffer, size);
		so_far += size;
		double df = so_far / (double)signed_len;
		if (df > frac + 0.02 || size == so_far) {
			frac = df;
		}
	}
	// fclose(f);
	free(buffer);
	
	const uint8_t* sha1 = SHA_final(&ctx);
	unsigned int i = 0;// = sizeof(sha1);
	printf("\n sha1 longth is [%d]\n",sizeof(ctx.buf));
	for(i=0; i<sizeof(ctx.buf); i++)
	{ 
		//printf("%.2x",ctx.buf[i]);   
		if(*sha1++ != *sha0++)
		{
			DEBUG("sha verify failed, not return when -1 currently\n");
			return -1;
		}
		else
			DEBUG("sha verify success\n");
	}
	printf("\n");
	return 0;
}

#if 0    
int main()
{
 uint8_t buf[64];
 sha_verify("update_update.bin",buf,3844971);//"update.bin", buf, 3843075);  
 sha_verify("update.bin", buf, 3843075);
 return 0;
}
#endif
