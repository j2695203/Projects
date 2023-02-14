package com.example.synthesizer;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class VolumeWidget extends AudioComponentWidgetBase {
    public VolumeWidget(AudioComponent volume, AnchorPane parent) {
        super(parent);
        super.ac_ = volume;
        Label title = new Label("Volume Widget");
        Slider slider = new Slider(0.0,2.0,1.0);
        slider.setOnMouseDragged( e -> handleSlider(e, slider) );
        super.setEachWidget( title, slider );
        super.addToParent();
    }

    protected void handleSlider(MouseEvent e, Slider slider) {
        double value = slider.getValue();
        ( (Filter) ac_ ).setScale( value );
    }

}
