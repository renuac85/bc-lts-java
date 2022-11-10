package org.bouncycastle.crypto.engines;

import junit.framework.TestCase;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class NativeLimitTests
    extends TestCase
{


    public void testCBCInit()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/CBC"))
        {
            System.out.println("Skipping CBC Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeCBC()
        {
            {
                //
                // Passing null iv causes some failure.
                //
                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[0])
                    {
                        @Override
                        public byte[] getIV()
                        {
                            return null;
                        }
                    };
                    init(true, piv);
                    fail("accepted null iv");
                }
                catch (Exception ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                //
                // Passing null key causes some failure
                //


                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[0])
                    {
                        @Override
                        public byte[] getKey()
                        {
                            return null;
                        }
                    }, new byte[16]);
                    init(true, piv);
                    fail("accepted null key");
                }
                catch (Exception ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }


                //
                // Pass invalid iv size
                //
                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[0]);
                    init(true, piv);
                    fail("accepted invalid iv size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("same length as block size"));
                }

                //
                // Pass invalid key size
                //
                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[15]), new byte[16]);
                    init(true, piv);
                    fail("accepted invalid key size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("key must be only"));
                }


                //
                // Key changing
                //

                ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]);
                init(true, piv);
                init(true, new KeyParameter(new byte[16]));
                init(true, null);


                piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]);
                init(false, piv);
                init(false, new KeyParameter(new byte[16]));


                try
                {
                    init(true, null);
                    fail("change state without key");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("cannot change encrypting state"));
                }


            }

        };
    }


    public void testCBCProcessBlock()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/CBC"))
        {
            System.out.println("Skipping CBC Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeCBC()
        {
            {

                //
                // null input array
                //
                try
                {
                    processBlock(null, 0, new byte[16], 0);
                    fail("accepted null input array");
                }
                catch (Throwable ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                //
                // null output array
                //
                try
                {
                    processBlock(new byte[16], 0, null, 0);
                    fail("accepted null output array");
                }
                catch (Throwable ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                //
                // negative input offset
                //

                try
                {
                    processBlock(new byte[0], -1, new byte[0], 0);
                    fail("accepted negative in offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("inOff is negative"));
                }

                //
                // Negative output offset
                //

                try
                {
                    processBlock(new byte[0], 0, new byte[0], -1);
                    fail("accepted negative out offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("outOff is negative"));
                }


                //
                // input buffer too short
                //
                try
                {
                    processBlock(new byte[15], 0, new byte[0], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }


                //
                // input buffer too short for offset
                //
                try
                {
                    processBlock(new byte[16], 1, new byte[0], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }


                //
                // Output buffer to short
                //
                try
                {
                    processBlock(new byte[16], 0, new byte[15], 0);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }


                //
                // Output buffer too short for offset
                //
                try
                {
                    processBlock(new byte[16], 0, new byte[16], 1);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }


                //
                // Not initialized
                //
                try
                {
                    processBlock(new byte[16], 0, new byte[16], 0);
                    fail("not initialized");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("not initialized"));
                }
            }

        };
    }


    public void testCBCProcessBlocks()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/CBC"))
        {
            System.out.println("Skipping CBC Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeCBC()
        {
            {

                //
                // Null input array
                //
                try
                {
                    processBlocks(null, 0, 1, new byte[16], 0);
                    fail("accepted null input array");
                }
                catch (Throwable ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }


                //
                // Null output array
                //
                try
                {
                    processBlocks(new byte[16], 0, 1, null, 0);
                    fail("accepted null output array");
                }
                catch (Throwable ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                //
                // Negative input offset.
                //
                try
                {
                    processBlocks(new byte[0], -1, 1, new byte[0], 0);
                    fail("accepted negative in offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("inOff is negative"));
                }

                //
                // Negative block count.
                //
                try
                {
                    processBlocks(new byte[0], 0, -1, new byte[0], 0);
                    fail("accepted negative block count ");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("blockCount is negative"));
                }

                //
                // Negative output offset
                //
                try
                {
                    processBlocks(new byte[0], 0, 1, new byte[0], -1);
                    fail("accepted negative out offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("outOff is negative"));
                }


                //
                // Two short input buffer for block, offset 0
                //
                try
                {
                    processBlocks(new byte[15], 0, 1, new byte[0], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }


                //
                // Too short input buffer with offset of one, last byte read would be outside
                // of byte array.
                //
                try
                {
                    processBlocks(new byte[16], 1, 1, new byte[0], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }

                //
                //Multiblock, too short input array offset 0
                //
                try
                {
                    processBlocks(new byte[31], 0, 2, new byte[32], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }

                //
                // Multiblock, too short input array, last byte read would be outside of input array
                //
                try
                {
                    processBlocks(new byte[32], 1, 2, new byte[32], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }


                //
                // Too short output buffer.
                //
                try
                {
                    processBlocks(new byte[16], 0, 1, new byte[15], 0);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }

                //
                // Too short output buffer for output offset, last byte written would be outside of
                // array.
                //
                try
                {
                    processBlocks(new byte[16], 0, 1, new byte[16], 1);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }

                //
                // multiblock too short output buffer, last byte written would outside of array
                //
                try
                {
                    processBlocks(new byte[32], 0, 2, new byte[31], 0);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }

                //
                // multiblock, too short output buffer for output offset, last byte written would
                // be outside of output buffer.
                //
                try
                {
                    processBlocks(new byte[32], 0, 2, new byte[32], 1);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }


                //
                // Valid inputs but not initialised.
                //
                try
                {
                    processBlocks(new byte[16], 0, 1, new byte[16], 0);
                    fail("not initialized");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("not initialized"));
                }
            }

        };
    }


    public void testECBInit()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/ECB"))
        {
            System.out.println("Skipping ECB Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeEngine()
        {
            {
                //
                // Passing null key causes some failure.
                //
                try
                {
                    KeyParameter piv = new KeyParameter(new byte[0])
                    {
                        @Override
                        public byte[] getKey()
                        {
                            return null;
                        }
                    };
                    init(true, piv);
                    fail("accepted null key");
                }
                catch (Exception ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }


                //
                // Pass invalid key size
                //
                try
                {
                    KeyParameter piv = new KeyParameter(new byte[15]);
                    init(true, piv);
                    fail("accepted invalid key size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("key must be"));
                }
            }

        };
    }


    public void testECBProcessBlock()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/ECB"))
        {
            System.out.println("Skipping ECB Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeEngine()
        {
            {

                //
                // null input array
                //
                try
                {
                    processBlock(null, 0, new byte[16], 0);
                    fail("accepted null input array");
                }
                catch (Throwable ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                //
                // null output array
                //
                try
                {
                    processBlock(new byte[16], 0, null, 0);
                    fail("accepted null output array");
                }
                catch (Throwable ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                //
                // negative input offset
                //

                try
                {
                    processBlock(new byte[0], -1, new byte[0], 0);
                    fail("accepted negative in offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("inOff is negative"));
                }

                //
                // Negative output offset
                //

                try
                {
                    processBlock(new byte[0], 0, new byte[0], -1);
                    fail("accepted negative out offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("outOff is negative"));
                }


                //
                // input buffer too short
                //
                try
                {
                    processBlock(new byte[15], 0, new byte[0], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }


                //
                // input buffer too short for offset
                //
                try
                {
                    processBlock(new byte[16], 1, new byte[0], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }


                //
                // Output buffer to short
                //
                try
                {
                    processBlock(new byte[16], 0, new byte[15], 0);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }


                //
                // Output buffer too short for offset
                //
                try
                {
                    processBlock(new byte[16], 0, new byte[16], 1);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }


                //
                // Not initialized
                //
                try
                {
                    processBlock(new byte[16], 0, new byte[16], 0);
                    fail("not initialized");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("not initialized"));
                }
            }

        };
    }


    public void testECBProcessBlocks()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/ECB"))
        {
            System.out.println("Skipping CBC Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeEngine()
        {
            {

                //
                // Null input array
                //
                try
                {
                    processBlocks(null, 0, 1, new byte[16], 0);
                    fail("accepted null input array");
                }
                catch (Throwable ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }


                //
                // Null output array
                //
                try
                {
                    processBlocks(new byte[16], 0, 1, null, 0);
                    fail("accepted null output array");
                }
                catch (Throwable ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                //
                // Negative input offset.
                //
                try
                {
                    processBlocks(new byte[0], -1, 1, new byte[0], 0);
                    fail("accepted negative in offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("inOff is negative"));
                }

                //
                // Negative block count.
                //
                try
                {
                    processBlocks(new byte[0], 0, -1, new byte[0], 0);
                    fail("accepted negative block count ");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("blockCount is negative"));
                }

                //
                // Negative output offset
                //
                try
                {
                    processBlocks(new byte[0], 0, 1, new byte[0], -1);
                    fail("accepted negative out offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("outOff is negative"));
                }


                //
                // Two short input buffer for block, offset 0
                //
                try
                {
                    processBlocks(new byte[15], 0, 1, new byte[0], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }


                //
                // Too short input buffer with offset of one, last byte read would be outside
                // of byte array.
                //
                try
                {
                    processBlocks(new byte[16], 1, 1, new byte[0], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }

                //
                //Multiblock, too short input array offset 0
                //
                try
                {
                    processBlocks(new byte[31], 0, 2, new byte[32], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }

                //
                // Multiblock, too short input array, last byte read would be outside of input array
                //
                try
                {
                    processBlocks(new byte[32], 1, 2, new byte[32], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }


                //
                // Too short output buffer.
                //
                try
                {
                    processBlocks(new byte[16], 0, 1, new byte[15], 0);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }

                //
                // Too short output buffer for output offset, last byte written would be outside of
                // array.
                //
                try
                {
                    processBlocks(new byte[16], 0, 1, new byte[16], 1);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }

                //
                // multiblock too short output buffer, last byte written would outside of array
                //
                try
                {
                    processBlocks(new byte[32], 0, 2, new byte[31], 0);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }

                //
                // multiblock, too short output buffer for output offset, last byte written would
                // be outside of output buffer.
                //
                try
                {
                    processBlocks(new byte[32], 0, 2, new byte[32], 1);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }


                //
                // Valid inputs but not initialised.
                //
                try
                {
                    processBlocks(new byte[16], 0, 1, new byte[16], 0);
                    fail("not initialized");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("not initialized"));
                }
            }

        };
    }


    public void testGCMInitParamWithIV()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/GCM"))
        {
            System.out.println("Skipping GCM Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeGCM()
        {
            {
                //
                // Passing null iv causes some failure.
                //
                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[0])
                    {
                        @Override
                        public byte[] getIV()
                        {
                            return null;
                        }
                    };
                    init(true, piv);
                    fail("accepted null iv");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("IV must be at least 1 byte"));
                }

                //
                // Passing null key causes some failure
                //
                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[0])
                    {
                        @Override
                        public byte[] getKey()
                        {
                            return null;
                        }
                    }, new byte[1]);
                    init(true, piv);
                    fail("accepted null key");
                }
                catch (Exception ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }


                //
                // Pass invalid iv size
                //
                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[0]);
                    init(true, piv);
                    fail("accepted invalid iv size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("IV must be at least 1 byte"));
                }

                //
                // Pass invalid key size
                //
                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[15]), new byte[16]);
                    init(true, piv);
                    fail("accepted invalid key size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("key must be only"));
                }

                // Wrong param type.
                try
                {
                    init(true, new KeyParameter(new byte[16]));
                    fail("accepted invalid parameters");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("invalid parameters"));
                }

                // Null params/
                try
                {
                    init(true, null);
                    fail("accepted invalid parameters");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("invalid parameters"));
                }


                //
                // Key changing
                //

                ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]);
                init(true, piv);
                try
                {
                    init(true, new ParametersWithIV(null, new byte[16]));
                    fail("nonce reuse encryption");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("cannot reuse nonce for GCM encryption"));
                }

                try
                {
                    init(true, piv);
                    fail("nonce reuse encryption");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("cannot reuse nonce for GCM encryption"));
                }
                piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]);
                init(false, piv);
            }

        };
    }

    public void testGCMInitAEADParams()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/GCM"))
        {
            System.out.println("Skipping GCM Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeGCM()
        {
            {
                //
                // Passing null iv causes some failure.
                //
                try
                {
                    AEADParameters piv = new AEADParameters(new KeyParameter(new byte[16]), 128, new byte[0])
                    {

                        @Override
                        public byte[] getNonce()
                        {
                            return null;
                        }
                    };
                    init(true, piv);
                    fail("accepted null iv");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("IV must be at least 1 byte"));
                }


                //
                // Passing null key causes some failure
                //
                try
                {
                    AEADParameters piv = new AEADParameters(new KeyParameter(new byte[0])
                    {
                        @Override
                        public byte[] getKey()
                        {
                            return null;
                        }
                    }, 128, new byte[1]);
                    init(true, piv);
                    fail("accepted null key");
                }
                catch (Exception ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                //
                // Null associated text is valid.
                //
                {
                    AEADParameters piv = new AEADParameters(new KeyParameter(new byte[16]), 128, new byte[1], null);
                    init(true, piv);
                }


                //
                // Pass invalid iv size
                //
                try
                {
                    AEADParameters piv = new AEADParameters(new KeyParameter(new byte[16]), 128, new byte[0]);
                    init(true, piv);
                    fail("accepted invalid iv size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("IV must be at least 1 byte"));
                }

                //
                // Pass invalid key size
                //
                try
                {
                    AEADParameters piv = new AEADParameters(new KeyParameter(new byte[15]), 128, new byte[16]);
                    init(true, piv);
                    fail("accepted invalid key size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("key must be only"));
                }

                // Wrong param type.
                try
                {
                    init(true, new KeyParameter(new byte[16]));
                    fail("accepted invalid parameters");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("invalid parameters"));
                }

                // Null params/
                try
                {
                    init(true, null);
                    fail("accepted invalid parameters");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("invalid parameters"));
                }


                //
                // Key changing
                //

                AEADParameters piv = new AEADParameters(new KeyParameter(new byte[16]), 128, new byte[16]);
                init(true, piv);
                try
                {
                    init(true, new AEADParameters(null, 128, new byte[16]));
                    fail("nonce reuse encryption");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("cannot reuse nonce for GCM encryption"));
                }

                try
                {
                    init(true, piv);
                    fail("nonce reuse encryption");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("cannot reuse nonce for GCM encryption"));
                }

                // Should pass.
                piv = new AEADParameters(new KeyParameter(new byte[16]), 128, new byte[16]);
                init(false, piv);

                try
                {
                    init(true, new AEADParameters(null, 127, new byte[16]));
                    fail("invalid mac size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("invalid value for MAC size"));
                }

                try
                {
                    init(true, new AEADParameters(null, 16, new byte[16]));
                    fail("invalid mac size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("invalid value for MAC size"));
                }

                try
                {
                    init(true, new AEADParameters(null, 129, new byte[16]));
                    fail("invalid mac size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("invalid value for MAC size"));
                }


            }

        };
    }


    public void testGCMAADBytes()
        throws Exception
    {
        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/GCM"))
        {
            System.out.println("Skipping CBC Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeGCM()
        {
            {
                // Null aad array
                try
                {
                    processAADBytes(null, 0, 0);
                    fail("null aad array");
                }
                catch (Exception ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                // negative inOff
                try
                {
                    processAADBytes(new byte[0], -1, 0);
                    fail("negative aad offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("inOff is negative"));
                }

                // negative len
                try
                {
                    processAADBytes(new byte[0], 0, -1);
                    fail("negative aad len");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("len is negative"));
                }

                try
                {
                    processAADBytes(new byte[10], 1, 10);
                    fail("len + offset too long");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("inOff + len past end o"));
                }

                try
                {
                    processAADBytes(new byte[10], 0, 11);
                    fail("len too long");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("inOff + len past end o"));
                }

                try
                {
                    processAADBytes(new byte[10], 0, 10);
                    fail("negative aad len");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("GCM is uninitialized"));
                }

            }
        };
    }


    public void testGCMProcessByte()
        throws Exception
    {
        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/GCM"))
        {
            System.out.println("Skipping GCM Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeGCM()
        {
            {

                byte b = (byte)1;
                // Null out array
                try
                {
                    processByte(b, null, 0);
                    fail("null out array");
                }
                catch (Exception ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                // negative outOff
                try
                {
                    processByte(b, new byte[0], -1);
                    fail("negative out offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("outOff is negative"));
                }


                try
                {
                    processByte(b, new byte[0], 1);
                    fail("offset past end of array");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("offset past end"));
                }

                try
                {
                    processByte(b, new byte[10], 10);
                    fail("not initialized");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("uninitialized"));
                }

            }
        };
    }


    public void testGCMProcessBytes()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/GCM"))
        {
            System.out.println("Skipping GCM Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeGCM()
        {
            {

                //
                // Null input array
                //
                try
                {
                    processBytes(null, 0, 1, new byte[16], 0);
                    fail("accepted null input array");
                }
                catch (Throwable ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }


                //
                // Negative input offset.
                //
                try
                {
                    processBytes(new byte[0], -1, 1, new byte[0], 0);
                    fail("accepted negative in offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("inOff is negative"));
                }

                //
                // Negative block count.
                //
                try
                {
                    processBytes(new byte[0], 0, -1, new byte[0], 0);
                    fail("accepted negative block count ");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("len is negative"));
                }

                //
                // Negative output offset
                //
                try
                {
                    processBytes(new byte[0], 0, 1, new byte[0], -1);
                    fail("accepted negative out offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("outOff is negative"));
                }

                //
                // Valid inputs but not initialised.
                //
                try
                {
                    processBytes(new byte[16], 0, 1, new byte[16], 0);
                    fail("not initialized");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("uninitialized"));
                }


            }

        };
    }

    /**
     * Test GCM with combinations of zero length output and null output arrays.
     * This can be valid input especially when the caller has erroneously determined they
     * do not expect to get any output.
     *
     * @throws Exception
     */
    public void testGCMOutputVariations()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/GCM"))
        {
            System.out.println("Skipping GCM Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        { // Zero length output array
            AESNativeGCM nativeGCM = new AESNativeGCM();
            nativeGCM.init(true, new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]));
            byte[] in = new byte[32];
            byte[] out = new byte[0];

            // Passes because 32 bytes will not trigger any output.
            nativeGCM.processBytes(in, 0, in.length, out, 0);
        }

        { // nonzero output array but offset at end.
            AESNativeGCM nativeGCM = new AESNativeGCM();
            nativeGCM.init(true, new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]));
            byte[] in = new byte[32];
            byte[] out = new byte[32];

            // Passes because 32 bytes will not trigger any output.
            nativeGCM.processBytes(in, 0, in.length, out, 32);
        }

        { // null output array
            AESNativeGCM nativeGCM = new AESNativeGCM();
            nativeGCM.init(true, new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]));
            byte[] in = new byte[32];
            byte[] out = null;

            // Passes because 32 bytes will not trigger any output.
            nativeGCM.processBytes(in, 0, in.length, out, 0);
        }


        try
        { // zero length output array but output generated

            AESNativeGCM nativeGCM = new AESNativeGCM();
            nativeGCM.init(true, new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]));
            byte[] in = new byte[65];
            byte[] out = new byte[0];

            // Passes because 32 bytes will not trigger any output.
            nativeGCM.processBytes(in, 0, in.length, out, 0);
            fail("zero len but output");
        }
        catch (Exception ex)
        {
            assertTrue(ex.getMessage().contains("output len too short"));
        }


        try
        { // null output array but output generated

            AESNativeGCM nativeGCM = new AESNativeGCM();
            nativeGCM.init(true, new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]));
            byte[] in = new byte[65];
            byte[] out = null;

            // Passes because 32 bytes will not trigger any output.
            nativeGCM.processBytes(in, 0, in.length, out, 0);
            fail("null output");
        }
        catch (Exception ex)
        {
            assertTrue(ex.getMessage().contains("output len too short"));
        }

        try
        { // long enough output array but offset at array.len

            AESNativeGCM nativeGCM = new AESNativeGCM();
            nativeGCM.init(true, new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]));
            byte[] in = new byte[65];
            byte[] out = new byte[65];

            // Passes because 32 bytes will not trigger any output.
            nativeGCM.processBytes(in, 0, in.length, out, 65);
            fail("not enough output");
        }
        catch (Exception ex)
        {
            assertTrue(ex.getMessage().contains("output len too short"));
        }

        try
        { // long enough output array but offset in the middle

            AESNativeGCM nativeGCM = new AESNativeGCM();
            nativeGCM.init(true, new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]));
            byte[] in = new byte[65];
            byte[] out = new byte[65];

            // Passes because 32 bytes will not trigger any output.
            nativeGCM.processBytes(in, 0, in.length, out, 32);
            fail("not enough output");
        }
        catch (Exception ex)
        {
            assertTrue(ex.getMessage().contains("output len too short"));
        }

    }


    public void testCFBInit()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/CFB"))
        {
            System.out.println("Skipping CFB Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeCFB()
        {
            {
                //
                // Passing null iv causes some failure.
                //
                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[0])
                    {
                        @Override
                        public byte[] getIV()
                        {
                            return null;
                        }
                    };
                    init(true, piv);
                    fail("accepted null iv");
                }
                catch (Exception ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                //
                // Passing null key causes some failure
                //


                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[0])
                    {
                        @Override
                        public byte[] getKey()
                        {
                            return null;
                        }
                    }, new byte[16]);
                    init(true, piv);
                    fail("accepted null key");
                }
                catch (Exception ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }


                //
                // Pass invalid iv size
                //
                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[0]);
                    init(true, piv);
                    fail("accepted invalid iv size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("between one and block size length"));
                }

                //
                // Pass invalid iv size
                //
                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[17]);
                    init(true, piv);
                    fail("accepted invalid iv size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("between one and block size length"));
                }


                //
                // Pass invalid key size
                //
                try
                {
                    ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[15]), new byte[16]);
                    init(true, piv);
                    fail("accepted invalid key size");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("key must be only"));
                }


                //
                // Key changing
                //

                ParametersWithIV piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]);
                init(true, piv);
                init(true, new KeyParameter(new byte[16]));


                piv = new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]);
                init(false, piv);
                init(false, new KeyParameter(new byte[16]));


                try
                {
                    init(true, null);
                    fail("change state without key");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("cannot change encrypting state"));
                }


            }

        };
    }


    public void testCFBProcessBlock()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/CFB"))
        {
            System.out.println("Skipping CFB Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeCFB()
        {
            {

                //
                // null input array
                //
                try
                {
                    processBlock(null, 0, new byte[16], 0);
                    fail("accepted null input array");
                }
                catch (Throwable ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                //
                // null output array
                //
                try
                {
                    processBlock(new byte[16], 0, null, 0);
                    fail("accepted null output array");
                }
                catch (Throwable ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }

                //
                // negative input offset
                //

                try
                {
                    processBlock(new byte[0], -1, new byte[0], 0);
                    fail("accepted negative in offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("inOff is negative"));
                }

                //
                // Negative output offset
                //

                try
                {
                    processBlock(new byte[0], 0, new byte[0], -1);
                    fail("accepted negative out offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("outOff is negative"));
                }


                //
                // input buffer too short
                //
                try
                {
                    processBlock(new byte[15], 0, new byte[0], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }


                //
                // input buffer too short for offset
                //
                try
                {
                    processBlock(new byte[16], 1, new byte[0], 0);
                    fail("accepted invalid input");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too short"));
                }


                //
                // Output buffer to short
                //
                try
                {
                    processBlock(new byte[16], 0, new byte[15], 0);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }


                //
                // Output buffer too short for offset
                //
                try
                {
                    processBlock(new byte[16], 0, new byte[16], 1);
                    fail("accepted invalid output");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too short"));
                }

                //
                // Not initialized
                //
                try
                {
                    processBlock(new byte[16], 0, new byte[16], 0);
                    fail("not initialized");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("not initialized"));
                }
            }

        };
    }

    public void testCFBProcessBytes()
        throws Exception
    {

        if (!CryptoServicesRegistrar.getNativeServices().hasFeature("AES/CFB"))
        {
            System.out.println("Skipping CFB Limit Test: " + CryptoServicesRegistrar.getNativeStatus());
            return;
        }

        new AESNativeCFB()
        {
            {

                //
                // Null input array
                //
                try
                {
                    processBytes(null, 0, 1, new byte[16], 0);
                    fail("accepted null input array");
                }
                catch (Throwable ex)
                {
                    assertTrue(ex instanceof NullPointerException);
                }


                //
                // Negative input offset.
                //
                try
                {
                    processBytes(new byte[0], -1, 1, new byte[0], 0);
                    fail("accepted negative in offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("inOff is negative"));
                }

                //
                // Negative block count.
                //
                try
                {
                    processBytes(new byte[0], 0, -1, new byte[0], 0);
                    fail("accepted negative block count ");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("len is negative"));
                }

                //
                // Negative output offset
                //
                try
                {
                    processBytes(new byte[0], 0, 1, new byte[0], -1);
                    fail("accepted negative out offset");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("outOff is negative"));
                }

                //
                // Valid inputs but not initialised.
                //
                try
                {
                    processBytes(new byte[16], 0, 1, new byte[16], 0);
                    fail("not initialized");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("not initialized"));
                }


                //
                // Would attempt to process 16 bytes but offset would put it past
                // end of input array.
                //
                try
                {
                    processBytes(new byte[16], 1, 16, new byte[16], 0);
                    fail("input past end");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("input buffer too small"));
                }


                //
                // Would attempt to process 16 bytes but output offset would put it past
                // end of output array.
                //
                try
                {
                    processBytes(new byte[16], 0, 16, new byte[16], 1);
                    fail("output past end");
                }
                catch (Exception ex)
                {
                    assertTrue(ex.getMessage().contains("output buffer too small"));
                }
            }

            {
                //
                //  Zero length write
                //

                AESNativeCFB engine = new AESNativeCFB();
                engine.init(true, new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]));
                byte[] in = new byte[16];
                byte[] out = new byte[16];
                TestCase.assertEquals(0, engine.processBytes(in, 16, 0, out, 0));
                TestCase.assertTrue(Arrays.areAllZeroes(out, 0, out.length));

            }

            {
                //
                // Should only write 8 bytes even though there is capacity for 16 in output buffer
                //
                AESNativeCFB engine = new AESNativeCFB();
                engine.init(true, new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]));
                byte[] in = new byte[16];
                byte[] out = new byte[16];
                TestCase.assertEquals(8, engine.processBytes(in, 8, 8, out, 0));
                TestCase.assertTrue(Arrays.areAllZeroes(out, 8, 8));
            }


            {
                //
                // Should only write 8 bytes because of output offset
                //
                AESNativeCFB engine = new AESNativeCFB();
                engine.init(true, new ParametersWithIV(new KeyParameter(new byte[16]), new byte[16]));
                byte[] in = new byte[16];
                byte[] out = new byte[16];
                TestCase.assertEquals(8, engine.processBytes(in, 0, 8, out, 8));
                TestCase.assertTrue(Arrays.areAllZeroes(out, 0, 8));
            }


        };
    }

}