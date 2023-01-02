"use strict"

let canvas = document.getElementsByTagName("canvas")[0];
let ctx = canvas.getContext('2d');
let cWidth = canvas.width;
let cHeight = canvas.height;

// dinosaur image
let myImg = new Image();
myImg.src = "dinosaur.png";
myImg.xPos = 10;
myImg.yPos = 20;

let isDead = false;

function main(){
    createFires(8);
    window.requestAnimationFrame( animate );
}
window.onload = main;
document.onmousemove = handleMove;

// Call back functions //////////////

let fires = [];
function createFires( numFire ){
    for( let i = 0; i < numFire ; i++ ){
        let fire = {};
        fire.img = new Image();
        fire.img.src = "fire.png";
        fire.xPos = Math.random() * 1000;
        fire.yPos = Math.random() * 700;
        fires.push(fire);
    }
}

function handleMove( e ){
    myImg.xPos = e.x - 60;
    myImg.yPos = e.y - 60;
}

function animate(){

    erase();

    // For dinosaur
    ctx.drawImage( myImg, myImg.xPos, myImg.yPos, 130, 130 );

    // For fires
    for( let i = 0; i < fires.length; i++ ){

        ctx.drawImage( fires[i].img, fires[i].xPos, fires[i].yPos, 70, 40);

        // fires moving toward dinosaur
        if( fires[i].xPos - ( myImg.xPos + 60 )  > 2 ){
            fires[i].xPos -= 3;
        }else if ( fires[i].xPos - ( myImg.xPos + 60 ) < -2 ){
            fires[i].xPos += 3;
        }
        if( fires[i].yPos - ( myImg.yPos + 50 ) > 2 ){
            fires[i].yPos -= 3;
        }else if ( fires[i].yPos - ( myImg.yPos + 50 ) < -2 ){
            fires[i].yPos += 3;
        }

        // die when dinosaur hit by fires
        if( Math.abs( fires[i].xPos - ( myImg.xPos + 60 ) ) <= 2 && Math.abs( fires[i].yPos - ( myImg.yPos + 50 ) ) <= 2 ){
            isDead = true;
        }

    }
    // keep update the frame if the dinosaur is not dead
    if ( !isDead ){
        window.requestAnimationFrame( animate );
    }

}

function erase(){
    ctx.fillStyle = '#FFFFFF';
    ctx.fillRect(0,0, cWidth, cHeight );
}