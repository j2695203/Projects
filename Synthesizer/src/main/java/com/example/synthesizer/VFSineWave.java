package com.example.synthesizer;

public class VFSineWave implements AudioComponent{
    AudioClip original_;
    @Override
    public AudioClip getClip() {
        AudioClip result = new AudioClip();
        int numSamples = original_.getData().length / 2 ;
        double phase = 0;
        for( int i = 0; i < numSamples; i++){
            phase += ( 2 * Math.PI * original_.getSample(i) )/ result.sampleRate;
            result.setSample(i, clamping ((int) (Short.MAX_VALUE * Math.sin(phase))));
        }
        return result;
    }

    @Override
    public boolean hasInput() { return false; }

    @Override
    public void connectInput(AudioComponent input) {
        this.original_ = input.getClip();
    }
}
