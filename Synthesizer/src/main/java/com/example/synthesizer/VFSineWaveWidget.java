package com.example.synthesizer;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class VFSineWaveWidget extends AudioComponentWidgetBase {
    VFSineWaveWidget(AudioComponent ac, AnchorPane parent){
        super(parent);
        super.ac_ = ac;
        Label title = new Label("VF Sine Wave\n (linear ramp stop: 5000)");
        Slider slider = new Slider(0,10000,5000);
        slider.setOnMouseDragged( e -> handleSlider(e, slider, title) ); // modify title and frequency
        super.setEachWidget( title, slider );
        super.addToParent();
    }
    protected void handleSlider(MouseEvent e, Slider slider, Label title) {
        int stopValue = (int) slider.getValue();
        // modify title
        title.setText("VF Sine Wave\n (linear ramp stop:" + stopValue + ")");
        // modify value
        LinearRamp linearRamp = new LinearRamp(0, (float)stopValue);
        VFSineWave vfSineWave = new VFSineWave();
        vfSineWave.connectInput(linearRamp);
        ac_ = vfSineWave;
    }
}
