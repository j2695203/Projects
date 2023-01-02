package com.example.androidchatclient;

import android.util.Log;
import android.view.View;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class MyWebSocket extends WebSocketAdapter {
    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        MainActivity.wsOpen = true;
        Log.i("WebSock", "ws is open" );
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        super.onTextMessage(websocket, text);
        JSONObject receiveJson = new JSONObject(text);

        if( receiveJson.getString("type").equals("join") ){
            ChatActivity.allSendMsgs_.add( receiveJson.getString("user") + "has joined the room." );
//            ChatActivity.allUsers_.add( receiveJson.getString("user") );
        }else if( receiveJson.getString("type").equals("leave") ){
            ChatActivity.allSendMsgs_.add( receiveJson.getString("user") + "has left the room" );
//            ChatActivity.allUsers_.remove( receiveJson.getString( "user") );
        }else if( receiveJson.getString("type").equals("message") ){
            ChatActivity.allSendMsgs_.add( receiveJson.getString("user") + ": " + receiveJson.getString("message") );
        }

//        ChatActivity.updateLv();

        ChatActivity.lv_.post(new Runnable() {
            @Override
            public void run() {
                ChatActivity.adapter.notifyDataSetChanged();
                ChatActivity.lv_.smoothScrollToPosition( ChatActivity.adapter.getCount() );
            }
        });
    }
}
