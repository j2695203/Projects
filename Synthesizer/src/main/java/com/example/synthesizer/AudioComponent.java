package com.example.synthesizer;

import java.util.ArrayList;

public interface AudioComponent {
    AudioClip getClip();
    boolean hasInput();
    void connectInput(AudioComponent input);
    default short clamping(int value){
        if (value > Short.MAX_VALUE){
            return Short.MAX_VALUE;
        }else if(value < Short.MIN_VALUE){
            return Short.MIN_VALUE;
        }else{
            return (short) (value);
        }
    }

}
