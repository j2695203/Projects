"use strict";

let userName = document.getElementById('userName');
let roomName = document.getElementById('roomName');
let showPpl = document.getElementById('showPpl');
let showMsg = document.getElementById('showMsg');
let msgInput = document.getElementById('msgInput');
let leaveBtn = document.getElementById('leave');


// default value
userName.value = "Jinny";
roomName.value = "room1";

// events
userName.addEventListener("keypress", handleRoomKeyPressCB);
roomName.addEventListener("keypress", handleRoomKeyPressCB);
msgInput.addEventListener("keypress", handleMsgKeyPressCB);
leaveBtn.addEventListener("click", handleCloseCB);
// window.addEventListener("close", handleCloseCB);

// create the websocket
let ws = new WebSocket("ws://localhost:8080");
ws.onopen = handleWsConnectCB;
ws.onmessage = handleSendWsMessageCB;
ws.onerror = handleWsErrorCB;


////////////// Call back functions /////////////////////////
let wsOpen = false;
function handleWsConnectCB() {
    wsOpen = true;
    //ws.send("ws.send() test msg from javascript"); // test
    console.log("Ws open");
}

function handleWsErrorCB() {
    console.log("error");
}

/// to be modified!
function handleCloseCB(){
    console.log("client leave"); // test
    isInRoom = false;
    ws.send("leave " + userName.value + " " + roomName.value );

    let pUsers = document.getElementsByClassName('user');
    for( let el of pUsers){
        showPpl.removeChild(el);
    }
    let pMsgs = document.getElementsByClassName('msg');
    for( let el of pMsgs ){
        showMsg.removeChild(el);
    }

    let pNew = document.createElement('p');
    pNew.textContent = "Bye!";
    showMsg.appendChild(pNew);
}


function handleSendWsMessageCB( event ) {
    // save JSON object into a variable
    let serverMsg = JSON.parse(event.data);

    // display time with every message
    let d = new Date();
    let h = d.getHours();
    let m = d.getMinutes();
    let s = d.getSeconds();

    // handle join event
    if( serverMsg.type === "join" ){
        let pUser = document.createElement("p");
        let pMsg = document.createElement("p");
        pUser.setAttribute("class", "user");
        pMsg.setAttribute("class", "msg");
        pUser.textContent = ( serverMsg.user );
        pMsg.textContent = ( "[ "+h+":"+m+":"+s+" ]  " + serverMsg.user + " has joined the room." );
        pUser.setAttribute("id", serverMsg.user );
        showPpl.appendChild( pUser ); // how to show all members previously joined??
        showMsg.appendChild( pMsg );
    }

    // handle leave event
    if( serverMsg.type === "leave" ){
        // remove user from people list
        let pDel = document.getElementById(serverMsg.user);
        showPpl.removeChild(pDel);
        // show message
        let pMsg = document.createElement("p");
        pMsg.textContent = ( "[ "+h+":"+m+":"+s+" ]  " + serverMsg.user + " has left the room." );
        showMsg.appendChild( pMsg );
    }

    // handle message event
    if( serverMsg.type === "message" ){
        let pMsg = document.createElement("p");
        pMsg.textContent = ( "[ "+h+":"+m+":"+s+" ]  " + serverMsg.user + ": " + serverMsg.message );
        showMsg.appendChild( pMsg );
    }

    showMsg.scrollTop = showMsg.scrollHeight;
}

let isInRoom = false;
function handleRoomKeyPressCB( event ){
    if( event.keyCode == 13 ){

        event.preventDefault(); // avoid changing line when pressing enter

        // make sure the client validates the user/room name before sending it to the server.
        if( userName.value == "" ){
            alert("Valid user name contains any value");
            return
        }
        if( userName.value == "" || roomName.value != roomName.value.toLowerCase() ){
            alert("Valid room name contain only lowercase letters (and no spaces)");
            return
        }

        if( wsOpen ){
            // ws send msg to server
            ws.send("join " + userName.value + " " + roomName.value );
            isInRoom = true;

        }else{
            showMsg.value = "WS is not open..."
        }
    }

}

function handleMsgKeyPressCB( event ){
    if( event.keyCode==13 && !isInRoom ){
        alert("Please enter a room.");
        return;
    }
    if( event.keyCode == 13 ){

        event.preventDefault(); // avoid changing line when pressing enter

        if( wsOpen ){
            // ws send msg to server
            ws.send( userName.value + " " + msgInput.value );
            msgInput.value = "";

        }else{
            showMsg.value = "WS is not open..."
        }
    }
}





