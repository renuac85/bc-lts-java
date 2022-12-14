//
// Created  on 7/6/2022.
//

#include <emmintrin.h>
#include <wmmintrin.h>
#include <stdexcept>
#include <cstring>
#include <iostream>
#include "AesCFB128Wide.h"
#include "CFB128Wide.h"


namespace intel {
    namespace cfb {



        //
        // AES CFB128Wide 128 Encryption
        //

        AesCFB128Enc::AesCFB128Enc() : CFB128Wide() {

        }

        AesCFB128Enc::~AesCFB128Enc() = default;

        void AesCFB128Enc::encryptBlock(__m128i in, __m128i &out) {

            auto tmp = in;
            tmp = _mm_xor_si128(tmp, roundKeys[0]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[1]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[2]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[3]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[4]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[5]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[6]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[7]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[8]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[9]);
            out = _mm_aesenclast_si128(tmp, roundKeys[10]);
        }

        unsigned char AesCFB128Enc::processByte(unsigned char in) {
            if (byteCount == 0) {
                encryptBlock(feedback, feedback);
            }

            auto *fb = (unsigned char *) (&feedback);

            unsigned char out;


            out = fb[byteCount] ^ in;
            fb[byteCount++] = out;

            if (byteCount == CFB_BLOCK_SIZE) {
                byteCount = 0;
            }
            return out;
        }

        size_t AesCFB128Enc::processBytes(unsigned char *src, size_t len, unsigned char *dest) {
            unsigned char *end = src + len;
            unsigned char *destStart = dest;

            auto *fb = (unsigned char *) (&feedback);

            for (auto ptr = src; ptr < end;) {

                if (byteCount == 0) {
                    encryptBlock(feedback, feedback);
                }

                if (byteCount >= 0 || end - ptr < CFB_BLOCK_SIZE) {
                    *dest = fb[byteCount] ^ *ptr;
                    fb[byteCount++] = *dest;
                    dest++;
                    ptr++;
                    if (byteCount == CFB_BLOCK_SIZE) {
                        byteCount = 0;
                    }
                } else {

                    auto data = _mm_loadu_si128((__m128i *) ptr);
                    feedback = _mm_xor_si128(data, feedback);
                    _mm_storeu_si128((__m128i *) dest, feedback);

                    dest += CFB_BLOCK_SIZE;
                    ptr += CFB_BLOCK_SIZE;
                }
            }


            return dest - destStart;
        }


        void AesCFB192Enc::encryptBlock(__m128i in, __m128i &out) {
            auto tmp = in;
            tmp = _mm_xor_si128(tmp, roundKeys[0]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[1]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[2]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[3]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[4]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[5]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[6]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[7]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[8]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[9]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[10]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[11]);
            out = _mm_aesenclast_si128(tmp, roundKeys[12]);
        }

        void AesCFB256Enc::encryptBlock(__m128i in, __m128i &out) {
            auto tmp = in;
            tmp = _mm_xor_si128(tmp, roundKeys[0]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[1]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[2]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[3]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[4]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[5]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[6]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[7]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[8]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[9]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[10]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[11]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[12]);
            tmp = _mm_aesenc_si128(tmp, roundKeys[13]);
            out = _mm_aesenclast_si128(tmp, roundKeys[14]);
        }


        AesCFB192Enc::AesCFB192Enc(): AesCFB128Enc() {};

        AesCFB192Enc::~AesCFB192Enc() = default;

        AesCFB256Enc::AesCFB256Enc():AesCFB128Enc(){}

        AesCFB256Enc::~AesCFB256Enc() = default;

    }
}

