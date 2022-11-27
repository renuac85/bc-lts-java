//
// Created by meganwoods on 11/25/22.
//

#ifndef BCN_SHA256_H
#define BCN_SHA256_H


#include <cwchar>
#include <cstdint>
#include <emmintrin.h>

#include "Digest.h"

#define BUF_SIZE_SHA256 64
#define SHA256_SIZE 32

namespace intel {
    namespace digest {

        typedef struct SHA256StateType {
            uint8_t type;
            size_t bufPtr;
            uint8_t buf[BUF_SIZE_SHA256];
            __m128i s0;
            __m128i s1;
            uint64_t byteCount;
        } SHA256StateType;

        class Sha256 : public Digest {

        private:
            size_t bufPtr;
            unsigned char buf[BUF_SIZE_SHA256];
            __m128i s0, s1;
            uint32_t state[8];
            const __m128i mask = _mm_set_epi64x(0x0c0d0e0f08090a0bULL, 0x0405060700010203ULL);
            size_t byteCount;

            void hashBlock(unsigned char *block);

            constexpr static unsigned char padBlock[64] = {
                    0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
            };

        protected:
            int processLength(size_t length) override;

        public:
            Sha256();

            ~Sha256();

            void update(unsigned char b) override;

            void update(unsigned char *val, size_t start, size_t len) override;

            void digest(unsigned char *out, size_t outOff) override;

            void reset() override;

            int getDigestSize() override;

            int getByteLength() override;

            void setState(unsigned char *rawState, size_t rawStateLen) override;

            void encodeState(unsigned char *rawStateBuffer, size_t &length) override;
        };

    }
}

#endif // BCN_SHA256_H



