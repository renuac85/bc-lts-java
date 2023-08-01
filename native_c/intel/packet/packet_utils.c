#include "packet_utils.h"

int get_aead_output_size(bool encryption, int len, int macSize) {
    if (encryption) {
        return len + macSize;
    } else if (len < macSize) {
        return -1;
    } else {
        return len - macSize;
    }
}

int get_output_size(bool encryption, int len) {
    if (encryption) {
        return len + ((len & 15) ? BLOCK_SIZE : 0);
    } else if (len & 15) {
        return -1;
    } else {
        return len;
    }
}

void packet_err_free(packet_err *err) {
    if (err != NULL) {
        free(err);
    }
}

packet_err *make_packet_error(const char *msg, int type) {
    packet_err *err = calloc(1, sizeof(packet_err));
    assert(err != NULL);
    err->msg = msg;
    err->type = type;
    return err;
}

uint32_t generate_key(bool encryption, uint8_t *key, __m128i *roundKeys, size_t keyLen) {
    uint32_t num_rounds;
    memset(roundKeys, 0, sizeof(__m128i) * 15);
    switch (keyLen) {
        case 16:
            num_rounds = ROUNDS_128;
            init_128(roundKeys, key, encryption);
            break;
        case 24:
            num_rounds = ROUNDS_192;
            init_192(roundKeys, key, encryption);
            break;
        case 32:
            num_rounds = ROUNDS_256;
            init_256(roundKeys, key, encryption);
            break;
        default:
            assert(0);
    }
    return num_rounds;
}

static inline void
packet_encrypt(__m128i *d0, const __m128i chainblock, __m128i *roundKeys, const uint32_t num_rounds) {
    *d0 = _mm_xor_si128(*d0, chainblock);
    *d0 = _mm_xor_si128(*d0, roundKeys[0]);
    *d0 = _mm_aesenc_si128(*d0, roundKeys[1]);
    *d0 = _mm_aesenc_si128(*d0, roundKeys[2]);
    *d0 = _mm_aesenc_si128(*d0, roundKeys[3]);
    *d0 = _mm_aesenc_si128(*d0, roundKeys[4]);
    *d0 = _mm_aesenc_si128(*d0, roundKeys[5]);
    *d0 = _mm_aesenc_si128(*d0, roundKeys[6]);
    *d0 = _mm_aesenc_si128(*d0, roundKeys[7]);
    *d0 = _mm_aesenc_si128(*d0, roundKeys[8]);
    *d0 = _mm_aesenc_si128(*d0, roundKeys[9]);
    if (num_rounds == ROUNDS_128) {
        *d0 = _mm_aesenclast_si128(*d0, roundKeys[10]);
    } else if (num_rounds == ROUNDS_192) {
        *d0 = _mm_aesenc_si128(*d0, roundKeys[10]);
        *d0 = _mm_aesenc_si128(*d0, roundKeys[11]);
        *d0 = _mm_aesenclast_si128(*d0, roundKeys[12]);
    } else if (num_rounds == ROUNDS_256) {
        *d0 = _mm_aesenc_si128(*d0, roundKeys[10]);
        *d0 = _mm_aesenc_si128(*d0, roundKeys[11]);
        *d0 = _mm_aesenc_si128(*d0, roundKeys[12]);
        *d0 = _mm_aesenc_si128(*d0, roundKeys[13]);
        *d0 = _mm_aesenclast_si128(*d0, roundKeys[14]);
    } else {
        assert(0);
    }
}

size_t cbc_pc_encrypt(unsigned char *src, uint32_t blocks, unsigned char *dest, __m128i *chainblock, __m128i *roundKeys,
                      uint32_t num_rounds) {
    unsigned char *destStart = dest;
    __m128i d0;
    __m128i tmpCb = *chainblock;
    while (blocks > 0) {
        d0 = _mm_loadu_si128((__m128i *) src);
        packet_encrypt(&d0, tmpCb, roundKeys, num_rounds);
        _mm_storeu_si128((__m128i *) dest, d0);
        blocks--;
        src += BLOCK_SIZE;
        dest += BLOCK_SIZE;
        tmpCb = d0;
    }
    *chainblock = tmpCb;
    return (size_t) (dest - destStart);
}