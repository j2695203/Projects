package com.example.synthesizer;
import java.lang.Math;
import java.util.ArrayList;
public class SineWave implements AudioComponent{
    int frequency_;

   SineWave(int frequency){
        this.frequency_ = frequency;
    }

    @Override
    public AudioClip getClip() {
        AudioClip adClip = new AudioClip();
        for (int i = 0; i < adClip.sampleData.length/2; i++ ){
            adClip.setSample(i, (short) (Short.MAX_VALUE * Math.sin( 2*Math.PI*frequency_ * i / adClip.sampleRate )));
        }
        return adClip;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {
        assert( false ); // ERROR: doesn't use input
    }

    public void setFrequency(int frequency){
        frequency_ = frequency;
    }

}
