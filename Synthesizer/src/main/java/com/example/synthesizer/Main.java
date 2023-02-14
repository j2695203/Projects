package com.example.synthesizer;

import org.junit.jupiter.api.Assertions;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.util.ArrayList;

public class Main {
    static void SampleTest(){
        AudioClip a1 = new AudioClip();
        for (short i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++){
            a1.setSample(0, i);
            Assertions.assertEquals(i, a1.getSample(0));
        }
    }

    public static void main(String[] args) throws LineUnavailableException {
        SampleTest();
        // Get properties from the system about samples rates, etc.
        // AudioSystem is a class from the Java standard library.


    }
}
