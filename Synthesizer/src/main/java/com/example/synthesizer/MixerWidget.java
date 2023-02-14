package com.example.synthesizer;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;

public class MixerWidget extends AudioComponentWidgetBase {
    public MixerWidget(AudioComponent mixer, AnchorPane parent) {
        super(parent);
        ac_ = mixer;
        // 有wave連上後，創AudioComponent mixer = new Mixer(newAc);
//        super.ac_ = volume;
        Label title = new Label("Mixer Widget");
        super.setEachWidget( title, null );
        super.addToParent();
    }
}
