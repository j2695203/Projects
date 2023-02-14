package com.example.synthesizer;

import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;

public class AudioComponentWidgetBase extends Pane {
    protected AudioComponent ac_;
    protected AnchorPane parent_; // = mainCanvas
    private HBox baseLayout_;
    private VBox leftPanel_;
    private VBox centralPanel_;
    private VBox rightPanel_;
    protected Label title_;
    protected Slider slider_;
    private double mouseStartDragX_, mouseStartDragY_, widgetStartDragX_, widgetStartDragY_;
    private Line outputLine_ = null;
    private Circle outputDot;
    protected Circle inputDot;
    protected ArrayList<AudioComponentWidgetBase> inputConnectedWidgets = new ArrayList<>();
    private AudioComponentWidgetBase outputConnectedWidget = null;

    AudioComponentWidgetBase(AnchorPane parent){
        parent_ = parent;

        // WIDGET FRAME
        baseLayout_ = new HBox();
        baseLayout_.setStyle("-fx-border-color: black; -fx-border-image-width: 8; -fx-background-color: bisque");

        // LEFT PANEL
        leftPanel_= new VBox();
        inputDot = new Circle(10);
        inputDot.setFill( Color.DARKSALMON);
        // add items to panel
        leftPanel_.getChildren().add(inputDot);


        // CENTRAL PANEL
        centralPanel_ = new VBox();
        // items added by clicking buttons
        // ...
        // drag the widget to move
        centralPanel_.setOnMousePressed(e -> startDrag( e ) );
        centralPanel_.setOnMouseDragged(e -> handleDrag( e, outputDot ) );


        // RIGHT PANEL
        rightPanel_ = new VBox();
        // items
        Button closeBtn = new Button("X");
        closeBtn.setOnAction( e -> closeWidget(e) );
        outputDot = new Circle(10);
        outputDot.setFill( Color.CHOCOLATE);
        // draw a line when connected
        outputDot.setOnMousePressed( e -> startConnection( e, outputDot ) ); // cancel an existed line or draw a new line
        outputDot.setOnMouseDragged( e -> moveConnection( e ) ); // draw a line to the dragging mouse's position
        outputDot.setOnMouseReleased( e -> endConnection( e ) ); // delete the line or draw a connection line to speaker
        // add items to panel
        rightPanel_.getChildren().add(closeBtn);
        rightPanel_.getChildren().add(outputDot);


        // ADJUST LAYOUT
        centralPanel_.setAlignment(Pos.CENTER);
        centralPanel_.setPadding( new Insets(5) );
        centralPanel_.setSpacing( 5 );
        rightPanel_.setAlignment(Pos.CENTER);
        rightPanel_.setPadding( new Insets(5) );
        rightPanel_.setSpacing( 5 );
        leftPanel_.setPadding( new Insets(5) );
        leftPanel_.setSpacing( 5 );
        // adjust widget's location, not overlap when creating
        this.setLayoutX(50+15*SynthesizeApplication.allWidgets.size());
        this.setLayoutY(100+15*SynthesizeApplication.allWidgets.size());

    }

    protected void addToParent(){
        // add to parent
        centralPanel_.getChildren().add(title_);
        if( slider_ != null ){
            centralPanel_.getChildren().add(slider_);
        }
        baseLayout_.getChildren().add(leftPanel_);
        baseLayout_.getChildren().add(centralPanel_);
        baseLayout_.getChildren().add(rightPanel_);
        // ADD TO PARENT
        this.getChildren().add(baseLayout_);
        parent_.getChildren().add( this );
    }

    private void startDrag(MouseEvent e) { // save the original position
        mouseStartDragX_ = e.getSceneX();
        mouseStartDragY_ = e.getSceneY();
        widgetStartDragX_ = this.getLayoutX();
        widgetStartDragY_ = this.getLayoutY();
    }

    private void handleDrag(MouseEvent e, Circle outputDot) {
        // modify widget's position
        double mouseDelX = e.getSceneX() - mouseStartDragX_;
        double mouseDelY = e.getSceneY() - mouseStartDragY_;
        this.relocate(widgetStartDragX_ + mouseDelX, widgetStartDragY_ + mouseDelY);
        // modify line's position
        // outputDot's line
        if ( outputLine_ != null){
            Bounds parentBounds = parent_.getBoundsInParent(); // this code doesn't have parent bound issue
            Bounds bounds = this.outputDot.localToScene( this.outputDot.getBoundsInLocal() );
            outputLine_.setStartX( bounds.getCenterX() - parentBounds.getMinX() );
            outputLine_.setStartY( bounds.getCenterY() - parentBounds.getMinY() );
        }
        // inputDot's line 要寫成通用的！！！！
        // 若是volume且sineWave有line, 則sineWave line 的 end point 要隨著 volume 的 inputDot 移動
        if (  ( this.inputConnectedWidgets.size() > 0 ) ){
            Bounds parentBounds = parent_.getBoundsInParent(); // this code doesn't have parent bound issue
            Bounds bounds = this.inputDot.localToScene( this.inputDot.getBoundsInLocal() );
            for( AudioComponentWidgetBase w: this.inputConnectedWidgets){
                w.outputLine_.setEndX( bounds.getCenterX() - parentBounds.getMinX() );
                w.outputLine_.setEndY( bounds.getCenterY() - parentBounds.getMinY() );
            }
        }
    }

    private void closeWidget(ActionEvent e) {

        // delete output line and output connected widgets
        parent_.getChildren().remove(outputLine_);
        outputLine_ = null;
        if( outputConnectedWidget != null ){
            this.outputConnectedWidget.inputConnectedWidgets.remove(this);
        }
        SynthesizeApplication.speakerConnectedWidgets.remove(this);

        // delete input line and input connected widgets
        for( AudioComponentWidgetBase icw: this.inputConnectedWidgets){
            icw.parent_.getChildren().remove(icw.outputLine_);
            icw.outputLine_ = null;
            icw.outputConnectedWidget = null;
        }
        this.inputConnectedWidgets.clear();

        // delete widget
        parent_.getChildren().remove(this);
        SynthesizeApplication.allWidgets.remove(this);

        // if it is volume widget
        if ( this.title_.getText().contains("Volume") ){
            SynthesizeApplication.volumeWidget = null;
        }

        System.out.println("num widgets: " + SynthesizeApplication.allWidgets.size());
    }

    private void startConnection(MouseEvent e, Circle outputDot) { // remove or create a line
        if ( outputLine_ != null ){
            parent_.getChildren().remove(outputLine_);
            outputConnectedWidget = null;
            SynthesizeApplication.speakerConnectedWidgets.remove(this );
//            this.inputConnectedWidgets.remove(this ); // 應是下面連接的inputConnected
        }else{
            Bounds parentBounds = parent_.getBoundsInParent(); // this code doesn't have parent bound issue
            Bounds bounds = outputDot.localToScene( outputDot.getBoundsInLocal() );
            outputLine_ = new Line();
            outputLine_.setStrokeWidth( 4 );
            outputLine_.setStartX( bounds.getCenterX() - parentBounds.getMinX() );
            outputLine_.setStartY( bounds.getCenterY() - parentBounds.getMinY() );
            outputLine_.setEndX( e.getSceneX() );
            outputLine_.setEndY( e.getSceneY() );
            parent_.getChildren().add(outputLine_);
        }
    }

    private void moveConnection(MouseEvent e ) {
        Bounds parentBounds = parent_.getBoundsInParent(); // this code doesn't have parent bound issue
        outputLine_.setEndX( e.getSceneX() - parentBounds.getMinX() );
        outputLine_.setEndY( e.getSceneY() - parentBounds.getMinY() );
    }

    private void endConnection(MouseEvent e ) {
        Circle speaker = SynthesizeApplication.speakerCircle;
        Bounds speakerBounds = speaker.localToScene( speaker.getBoundsInLocal() );
        double distanceToSpeaker = Math.sqrt( Math.pow( speakerBounds.getCenterX() - e.getSceneX(), 2.0 ) + Math.pow( speakerBounds.getCenterY() - e.getSceneY(), 2.0 ) );
        // connect to speaker
        if( distanceToSpeaker < 13 ){
            outputLine_.setEndX( speakerBounds.getCenterX() );
            outputLine_.setEndY( speakerBounds.getCenterY() );
            SynthesizeApplication.speakerConnectedWidgets.add( this );

        }
        // 要改！！！

        // connect to volume
        else if( SynthesizeApplication.volumeWidget != null ){
            Circle volumeInput = SynthesizeApplication.volumeWidget.inputDot;
            Bounds volumeInputBounds = volumeInput.localToScene( volumeInput.getBoundsInLocal() );
            double distanceToVolume = Math.sqrt( Math.pow( volumeInputBounds.getCenterX() - e.getSceneX(), 2.0 ) + Math.pow( volumeInputBounds.getCenterY() - e.getSceneY(), 2.0 ) );
            if( distanceToVolume < 13 ){
                outputLine_.setEndX( volumeInputBounds.getCenterX() );
                outputLine_.setEndY( volumeInputBounds.getCenterY() );
                outputConnectedWidget = SynthesizeApplication.volumeWidget;
                SynthesizeApplication.volumeWidget.inputConnectedWidgets.add(this);
                SynthesizeApplication.volumeWidget.ac_.connectInput(this.ac_);
            }else{
                parent_.getChildren().remove(outputLine_);
                outputLine_ = null;
            }
        }
        // connect to mixer
        else if( SynthesizeApplication.mixerWidget != null ){
            Circle mixerInput = SynthesizeApplication.mixerWidget.inputDot;
            Bounds mixerInputBounds = mixerInput.localToScene( mixerInput.getBoundsInLocal() );
            double distanceToMixer = Math.sqrt( Math.pow( mixerInputBounds.getCenterX() - e.getSceneX(), 2.0 ) + Math.pow( mixerInputBounds.getCenterY() - e.getSceneY(), 2.0 ) );
            if( distanceToMixer < 13 ){
                outputLine_.setEndX( mixerInputBounds.getCenterX() );
                outputLine_.setEndY( mixerInputBounds.getCenterY() );
                outputConnectedWidget = SynthesizeApplication.mixerWidget;
                SynthesizeApplication.mixerWidget.inputConnectedWidgets.add(this);
                outputConnectedWidget.ac_.connectInput(this.ac_);
//                SynthesizeApplication.mixerWidget.ac_.connectInput(this.ac_);
            }else{
                parent_.getChildren().remove(outputLine_);
                outputLine_ = null;
            }
        }
        // 本身是Wave音檔且有volume存在，則可以連到"單一"volume // 因為怕sine 連sine
//        else if( this.title_.getText().contains("Wave") && SynthesizeApplication.volumeWidget != null ){
//            Circle volumeInput = SynthesizeApplication.volumeWidget.inputDot;
//            Bounds volumeInputBounds = volumeInput.localToScene( volumeInput.getBoundsInLocal() );
//            double distanceToVolume = Math.sqrt( Math.pow( volumeInputBounds.getCenterX() - e.getSceneX(), 2.0 ) + Math.pow( volumeInputBounds.getCenterY() - e.getSceneY(), 2.0 ) );
//            if( distanceToVolume < 13 ){
//                outputLine_.setEndX( volumeInputBounds.getCenterX() );
//                outputLine_.setEndY( volumeInputBounds.getCenterY() );
//                outputConnectedWidget = SynthesizeApplication.volumeWidget;
//                SynthesizeApplication.volumeWidget.inputConnectedWidgets.add(this);
//                SynthesizeApplication.volumeWidget.ac_.connectInput(this.ac_);
////                Mixer volumeMixer = new Mixer();
////                SynthesizeApplication.volumeWidget.ac_ = volumeMixer.g volumeMixer.connectInput(this.ac_);
//            }else{
//                parent_.getChildren().remove(outputLine_);
//                outputLine_ = null;
//            }
//        }
        // no connection
        else{
            parent_.getChildren().remove(outputLine_);
            outputLine_ = null;
        }
    }

    public AudioComponent getAc_() {
        return ac_;
    }

    protected void setEachWidget( Label title, Slider slider){
        title_ = title;
        slider_ = slider;
    }

    // 如何簡化貼到這？
//    protected void handleSlider(MouseEvent e, Slider slider, Label title) {
//        int value = (int) slider.getValue();
//        // modify title
//        title.setText("Sine Wave (" + value + " Hz)");
//        // modify frequency
//        ac_ = new SineWave(value);
//    }

}
