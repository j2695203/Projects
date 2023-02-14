package com.example.synthesizer;

import java.util.Arrays;

public class AudioClip {
    static double duration = 2.0;
    static int sampleRate = 44100;
    byte[] sampleData = new byte[ 44100*2*2 ]; // rate * duration * (byte)short

    public int getSample(int i) {
        int lower = Byte.toUnsignedInt(sampleData[ 2 * i ]);
        int upper = sampleData[ ( 2 * i ) + 1 ] << 8;
        return upper | lower;
    }

    public void setSample(int i, short value){
        sampleData[2 * i] = (byte) (value & 0xFF);
        sampleData[( 2 * i ) + 1] = (byte) (value >> 8);
    }

    public byte[] getData(){
        return Arrays.copyOf(sampleData, sampleData.length);
    }
}

