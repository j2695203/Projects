package com.example.synthesizer;

import java.util.ArrayList;

public class Filter implements AudioComponent{
    double scale_;
    AudioClip original_;
    Filter(double scale){
        this.scale_ = scale;
    }

    @Override
    public AudioClip getClip() {
        AudioClip result = new AudioClip();
        int value;
        for (int i = 0; i < original_.sampleData.length/2; i++ ){
            value = (int) (original_.getSample(i)*scale_);
            result.setSample(i, clamping(value));
        }
        return result;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {
        this.original_ = input.getClip();
    }

    public void setScale(double scale){
        scale_ = scale;
    }
}