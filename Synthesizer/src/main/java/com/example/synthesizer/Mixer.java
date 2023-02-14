package com.example.synthesizer;

import java.util.ArrayList;

public class Mixer implements AudioComponent{
    ArrayList<AudioClip> clips_ = new ArrayList<>();

    @Override
    public AudioClip getClip() {
        AudioClip result = new AudioClip();
        for (int numSample = 0; numSample < clips_.get(0).sampleData.length/2; numSample++ ){ // fixed num 44100
            int sumValue = 0;
            for ( int numClip = 0; numClip < clips_.size(); numClip++){
                sumValue +=  clips_.get(numClip).getSample(numSample) ;
            }
            result.setSample(numSample, clamping(sumValue));
        }
        return result;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {
        this.clips_.add(input.getClip());
    }
}
