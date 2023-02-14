package com.example.synthesizer;

public class LinearRamp implements AudioComponent {
    float start_;
    float stop_;
    LinearRamp(float start, float stop){
        start_ = start;
        stop_ = stop;
    }
    @Override
    public AudioClip getClip() {
        AudioClip adClip = new AudioClip();
        int numSamples = adClip.getData().length / 2 ;
        for (int i = 0; i < numSamples ; i++){
            adClip.setSample(i, (short) ((start_ * (numSamples - i) + stop_ * i) / numSamples));
        }
        return adClip;
    }

    @Override
    public boolean hasInput() { return false; }

    @Override
    public void connectInput(AudioComponent input) {

    }

}
