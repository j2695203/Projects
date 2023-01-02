package com.example.androidchatclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    static ArrayList<String> allSendMsgs_ = new ArrayList<>();
//    static ArrayList<String> allUsers_ = new ArrayList<>();
    static ArrayAdapter<String> adapter;
    static ListView lv_;
    private String username_;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // get the user/room name from intent
        String roomName = getIntent().getStringExtra("roomNameInput");
        username_ = getIntent().getStringExtra("userNameInput");
        // change the text view
        TextView textView = findViewById(R.id.roomName);
        textView.setText(roomName);

//        Bundle extras = getIntent().getExtras();
//        if( extras != null ){
//            String roomName = extras.getString("roomNameInput");
//            textView.setText(roomName);
//        }

        // load previous msgs
        adapter = new ArrayAdapter( this, android.R.layout.simple_list_item_1, allSendMsgs_ );
        lv_ = findViewById( R.id.lv_message );
        lv_.setAdapter( adapter );
    }

    public void handleBackClick(View view){
        Log.i("Jj: ChatActivity", "back button was pressed" );
        // switch to 1st activity
        Intent intent = new Intent( this, MainActivity.class );
        startActivity( intent );
    }

    public void handleSendClick(View view){
        Log.i("Jj: ChatActivity", "send button was pressed" );
        // send message to server
        TextInputEditText etMsg = findViewById(R.id.msgInput);
        Log.i("websocket",username_ + " " + etMsg.getText() );
        MainActivity.ws_.sendText( username_ + " " + etMsg.getText());
    }
}