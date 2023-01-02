package com.example.androidchatclient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

//    private boolean switchActivities_ = false;
    static WebSocket ws_;
    public static boolean wsOpen;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ws_ = new WebSocketFactory().createSocket("ws://10.0.2.2:8080/endpoint", 1000 );
        }
        catch( IOException e ) {
            Log.e( "Ee:","WS error" );
        }
        ws_.addListener( new MyWebSocket() );
        ws_.connectAsynchronously();

    }
    public void handleClick(View view){
        Log.i("Cc: MainActivity", "button was pressed" );
        EditText etUser = findViewById(R.id.userNameInput);
        EditText etRoom = findViewById(R.id.roomNameInput);

        // if not valid user/ room name
        if( etUser.getText().toString().equals("") || etRoom.getText().toString().equals("") ){
            Toast.makeText(this, "Please input User/Room name", Toast.LENGTH_LONG).show();
        }else{
            // switch to 2nd activity
            Intent intent = new Intent( this, ChatActivity.class );
            intent.putExtra("roomNameInput", etRoom.getText().toString() );
            intent.putExtra("userNameInput", etUser.getText().toString() );
            // send ws msg
            ws_.sendText( "join" + " " + etUser.getText() + " " + etRoom.getText() );
            startActivity( intent );
        }
    }
}