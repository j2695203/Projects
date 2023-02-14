package com.example.synthesizer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.ArrayList;

public class SynthesizeApplication extends Application {
    private AnchorPane mainCanvas_ = new AnchorPane();
    static ArrayList<AudioComponentWidgetBase> allWidgets = new ArrayList<>();// 宣告在哪？
    static ArrayList<AudioComponentWidgetBase> speakerConnectedWidgets = new ArrayList<>();
    static AudioComponentWidgetBase mixerWidget = null;
    static AudioComponentWidgetBase volumeWidget = null;
    static Circle speakerCircle;

    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        Scene scene = new Scene( root, 1200, 500);

        stage.setTitle("Jinny's Synth");

        // CENTRAL PANEL
        mainCanvas_.setStyle("-fx-background-color: darkgray");
        // speaker
        speakerCircle = new Circle(800,250,15);
        speakerCircle.setFill( Color.BLACK );
        // add to parent
        mainCanvas_.getChildren().add( speakerCircle );

        // RIGHT PANEL
        VBox rightPanel = new VBox();
        rightPanel.setPadding( new Insets(40) );
        rightPanel.setStyle("-fx-background-color: lightyellow");
        // sineWaveBtn
        Button sineWaveBtn = new Button("Create Sine Wave");
        sineWaveBtn.setOnAction( e -> createSineWaveComponent(e, allWidgets));
        // VFSineBtn
        Button vfSineWaveBtn = new Button("Create VF Sine Wave");
        vfSineWaveBtn.setOnAction( e -> createVFSineWaveComponent(e, allWidgets));
        // MixerBtn
        Button mixerBtn = new Button("Create Mixer Widget");
        mixerBtn.setOnAction( e -> createMixerComponent(e, allWidgets));
        // volumeBtn
        Button volumeBtn = new Button("Create Volume Widget");
        volumeBtn.setOnAction( e -> createVolumeComponent(e, allWidgets));
        // adjust layout
        rightPanel.setSpacing( 10 );
        // add to Parent
        rightPanel.getChildren().add(sineWaveBtn);
        rightPanel.getChildren().add(vfSineWaveBtn);
        rightPanel.getChildren().add(mixerBtn);
        rightPanel.getChildren().add(volumeBtn);

        // BOTTOM PANEL
        HBox bottomPanel = new HBox();
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(10));
        // play button
        Button playBtn = new Button("Play");
        playBtn.setOnAction( e -> handlePlay(e) );
        // add to Parent
        bottomPanel.getChildren().add(playBtn);

        // add to BorderPane root
        root.setCenter( mainCanvas_ );
        root.setRight( rightPanel );
        root.setBottom( bottomPanel );

        // show on window
        stage.setScene(scene);
        stage.show();
    }

    private void handlePlay(ActionEvent e) {
        // when no connected widgets
        if( speakerConnectedWidgets.size() == 0 ){
            return;
        }
        // when there are connected widgets
        else{

            Clip c = null; // Note, this is different from our AudioClip class.
            AudioListener listener = new AudioListener(c);
            try {
                c = AudioSystem.getClip();
                // This is the format that we're following, 44.1 KHz mono audio, 16 bits per sample.
                AudioFormat format16 = new AudioFormat( 44100, 16, 1, true, false );

                if(mixerWidget != null) {
                    for (AudioComponentWidgetBase acwb : mixerWidget.inputConnectedWidgets) {
                        mixerWidget.ac_.connectInput(acwb.ac_);
                    }
                }

                if(volumeWidget !=  null && volumeWidget.inputConnectedWidgets.size() > 0){
                    volumeWidget.ac_.connectInput(volumeWidget.inputConnectedWidgets.get(0).ac_);
                }

                // put connectedWidgets into mixer
                Mixer mixer = new Mixer();
                for( AudioComponentWidgetBase w: speakerConnectedWidgets){
                    mixer.connectInput( w.getAc_() );
                }

                // Data to be played
                byte[] playData = mixer.getClip().getData();


                // Play method by library
                c.open( format16, playData, 0, playData.length ); // Reads data from our byte array to play it.
                System.out.println( "About to play..." );
//                System.out.println( ((Filter)SynthesizeApplication.volumeWidget.ac_).scale_);
                c.start(); // Plays it.
                c.loop( 1 ); // Plays it 2 more times if desired, so 6 seconds total

                // Makes sure the program doesn't quit before the sound plays.
                c.addLineListener(listener); // c.close() inside this method
                System.out.println( "Done." );

            } catch (LineUnavailableException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void createSineWaveComponent(Object e, ArrayList<AudioComponentWidgetBase> allWidgets) {
        AudioComponent sineWave = new SineWave(440);
        AudioComponentWidgetBase acw = new SineWaveWidget(sineWave, mainCanvas_);
        allWidgets.add(acw);
    }
    private void createVFSineWaveComponent(ActionEvent e, ArrayList<AudioComponentWidgetBase> allWidgets) {
        AudioComponent linearRamp = new LinearRamp(0,10000);
        AudioComponent vfSineWave = new VFSineWave();
        vfSineWave.connectInput(linearRamp);
        AudioComponentWidgetBase vfWidget = new VFSineWaveWidget(vfSineWave, mainCanvas_);
        allWidgets.add(vfWidget);
    }
    private void createMixerComponent(ActionEvent e, ArrayList<AudioComponentWidgetBase> allWidgets) {
        if( mixerWidget == null){
            AudioComponent mixer = new Mixer();
            mixerWidget = new MixerWidget( mixer, mainCanvas_ );
            allWidgets.add(volumeWidget);
        }
    }
    private void createVolumeComponent(ActionEvent e, ArrayList<AudioComponentWidgetBase> allWidgets) {
        if( volumeWidget == null){
            AudioComponent volume = new Filter(1);

            volumeWidget = new VolumeWidget( volume, mainCanvas_ );
            allWidgets.add(volumeWidget);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}

