package com.example.synthesizer;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;


public class SineWaveWidget extends AudioComponentWidgetBase {
    SineWaveWidget(AudioComponent ac, AnchorPane parent){
        super(parent);
        super.ac_ = ac;
        Label title = new Label("Sine Wave (440 Hz)");  // should be modified with slider
        Slider slider = new Slider(220,880,440);
        slider.setOnMouseDragged( e -> handleSlider(e, slider, title) ); // modify title and frequency
        super.setEachWidget( title, slider );
        super.addToParent();
    }
    protected void handleSlider(MouseEvent e, Slider slider, Label title) {
        int value = (int) slider.getValue();
        // modify title
        title.setText("Sine Wave (" + value + " Hz)");
        // modify frequency
        ac_ = new SineWave(value);
//        ( (SineWave) ac_ ).setFrequency( value );
    }

}
