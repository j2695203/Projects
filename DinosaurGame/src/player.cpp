//
//  player.cpp
//  testSFML
//
//  Created by Chunhao Hsu on 2022/9/21.
//

#include "player.hpp"


Player::Player(){
    // Set Up Player
    if (!texturePlyr.loadFromFile("dinosaur.png") ) // alive dino pic
        std::cout<< "EXIT_FAILURE\n";
    if (!textureDeath.loadFromFile("death2.png") )  // dead dino pic
        std::cout<< "EXIT_FAILURE\n";
    
    spritePlyr.setTexture(texturePlyr);
    spritePlyr.setPosition(100, 410);
    spritePlyr.setScale(1.4, 1.4);
  
    // Player's life
    isDead = false;

    // Player's Boundry
    box.setSize(sf::Vector2f(250, 460)); // size
    box.setFillColor(sf::Color(0,0,0,100));
    
    // Jump parameters
    canJump = false;
    dy = 0;
    y = 410;
    
    // Shoottimer
    shoottimer = 0;
}

/*
 Player's Position when Jumping
*/
 void Player::movement(){
    // Player's Move
     if (sf::Keyboard::isKeyPressed(sf::Keyboard::Left) && spritePlyr.getPosition().x > 0)
         spritePlyr.move(-5.f,0);
     if (sf::Keyboard::isKeyPressed(sf::Keyboard::Right) && spritePlyr.getPosition().x < 1800){
         spritePlyr.move(5.f, 0);
     }
     
     // Jump Event
     if (sf::Keyboard::isKeyPressed(sf::Keyboard::Up) && canJump == false){
         canJump = true;
     }
    if(canJump){
        dy+=0.8;
        y+=dy;
        if (y>410){  // jump before hitting ground
            dy=-22.5;
            canJump = false;
        }
        spritePlyr.setPosition(spritePlyr.getPosition().x, y);
    }
}

/*
 
 */
void Player::shoot(Library& lib){
    // Avoid Shooting Intensive Siliva
    if ( shoottimer < 100){
        shoottimer +=1;
    }
    
    // Update Mouth's Position (for spitting)
    posMouth = sf::Vector2f(spritePlyr.getPosition().x+550, spritePlyr.getPosition().y + 120);
    
    // Ready to Shoot Saliva When User Press Space
    if (sf::Keyboard::isKeyPressed(sf::Keyboard::Space) && shoottimer >=18){
        lib.spriteProj.setPosition(posMouth);
        projectiles.push_back(lib.spriteProj);
        shoottimer = 0;
    }
    
    // Saliva Movement
    for(size_t i =0; i < projectiles.size(); i++){
        projectiles[i].move(15.f, 0);
        if(projectiles[i].getPosition().x > 2400 ){
            projectiles.erase(projectiles.begin()+i); // If saliva meets boundary of window, delete from vector
        }
    }
}
