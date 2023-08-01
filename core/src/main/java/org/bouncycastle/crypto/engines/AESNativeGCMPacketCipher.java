package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PacketCipherEngine;
import org.bouncycastle.crypto.PacketCipherException;
import org.bouncycastle.crypto.modes.AESGCMModePacketCipher;

public class AESNativeGCMPacketCipher
    extends PacketCipherEngine
    implements AESGCMModePacketCipher
{
    public static AESGCMModePacketCipher newInstance()
    {
        return new AESNativeGCMPacketCipher();
    }

    private AESNativeGCMPacketCipher()
    {
    }

    @Override
    public int getOutputSize(boolean encryption, CipherParameters parameters, int len)
    {
        return 0;
    }

    @Override
    public int processPacket(boolean encryption, CipherParameters parameters, byte[] input, int inOff, int len, byte[] output, int outOff)
        throws PacketCipherException
    {
        return 0;
    }
}
