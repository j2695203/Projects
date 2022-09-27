//
//  library.cpp
//  testSFML
//
//  Created by Jinny Jeng on 9/22/22.
//

#include "library.hpp"
#include <iostream>


Library::Library(){
    // Set Up Fire
    if (!textureFire.loadFromFile("fire.png"))
        std::cout<< "EXIT_FAILURE\n";
    spriteFire.setTexture(textureFire);
    spriteFire.setPosition(2300, 530);
    spriteFire.setScale(0.15, 0.15);
    spriteFire.setRotation(10.f);
    
    // Set Up Rock
    if (!textureRock.loadFromFile("rock.png"))
        std::cout<< "EXIT_FAILURE\n";
    spriteRock.setTexture(textureRock);
    spriteRock.setPosition(2300, 790);
    spriteRock.setScale(1, 1);
    
    // Set up Porjectile
    if (!textureProj.loadFromFile("saliva.png"))
        std::cout<< "EXIT_FAILURE\n";
    spriteProj.setTexture(textureProj);
    spriteProj.setPosition(650, 550);
    spriteProj.setScale(0.8, 0.8);
    
    // Text for Score
    intScore = 0;
    std::string input = std::to_string(intScore);

    textScore.setString(input);
    textScore.setCharacterSize(80);
    textScore.setPosition(500, 30);
    textScore.setFillColor(sf::Color::White);
    
    textTitle.setString("Score:");
    textTitle.setCharacterSize(80);
    textTitle.setFillColor(sf::Color::White);
    textTitle.setPosition(100, 30);
    
    // Text for Instruction
    textKeybrd.setString("Move : Left, Right \nJump : Up \nSpit : Space \nRestart : Enter");
    textKeybrd.setCharacterSize(50);
    textKeybrd.setFillColor(sf::Color::White);
    textKeybrd.setPosition(1930, 30);
    
    // Text for Gameover
    textEnd.setCharacterSize(200);
    textEnd.setFillColor(sf::Color(255,80,20));
    textEnd.setStyle(sf::Text::Bold);
    textEnd.setPosition(700.f, 300.f);
    textEnd.setString("Game Over!");
    
    // Sound
//    if (!buffer.loadFromFile("death.mp3"))
//        std::cout<< "EXIT_FAILURE\n";
//    death.setBuffer(buffer);
    
}
