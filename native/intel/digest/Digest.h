//
// Created by meganwoods on 11/25/22.
//

#ifndef BCN_DIGEST_H
#define BCN_DIGEST_H

#include <cstddef>

namespace intel {
    namespace digest {


        class Digest {
        protected:
            /**
             * Called as part of padding to encode the number of bits into the final buffer.
             * @param length The length in bits.
             * @return number of bytes encoded.
             */
            virtual int processLength(size_t length) = 0;

        public:


           virtual  ~Digest() = 0;

            /**
             * Digest size in bytes.
             * @return
             */
            virtual int getDigestSize() = 0;

            /**
             * Length of output in bytes.
             * @return
             */
            virtual int getByteLength() = 0;

            /**
             * Update with single byte.
             * @param b  the byte.
             */
            virtual void update(unsigned char b) = 0;

            /**
             * Update from array of bytes.
             * @param val Pointer to start of array
             * @param start  offset from start
             * @param len  length
             */
            virtual void update(unsigned char *val, size_t start, size_t len) = 0;

            /**
             * Complete the digest returning calculated result to out.
             * Out must be long enough.
             * @param out destination array
             * @param outOff offset from start of the array.
             */
            virtual void digest(unsigned char *out, size_t outOff) = 0;

            /**
             * reset state of digest
             */
            virtual void reset() = 0;

            /**
             * Set state from previously encoded state, digest specific.
             * @param rawState The raw state
             * @param rawStateLen Length of raw state.
             */
            virtual void setState(unsigned char *rawState, size_t rawStateLen) = 0;

            /**
             * Encodes the state into the raw buffer setting the length. If rawStateBuffer
             * is null it sets the length and returns. Calls this with a null buffer to determine
             * length of required array.
             * @param rawStateBuffer The buffer to encode the state to.
             * @param length holds length upon return.
             */
            virtual void encodeState(unsigned char *rawStateBuffer, size_t &length) = 0;

        };

    }

    }


#endif //BCN_DIGEST_H
