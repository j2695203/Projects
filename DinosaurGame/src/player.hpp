//
//  player.hpp
//  testSFML
//
//  Created by Chunhao Hsu on 2022/9/21.
//

#ifndef player_hpp
#define player_hpp

#include <stdio.h>
#include <SFML/Graphics.hpp>
#include <iostream>
#include <vector>
#include "library.hpp"

class Player{
public:
    
    Player();
    
    bool isDead;
    
    // Player's Object
    sf::Sprite spritePlyr;
    
    // Player's Color
    sf::Texture texturePlyr;
    sf::Texture textureDeath;
    
    // spit saliva from the position of mouth
    sf::Vector2f posMouth;
    
    // Player's Boundry
    sf::RectangleShape box;
    
    // Player's Projectiles (saliva)
    std::vector<sf::Sprite> projectiles;
    
    bool canJump;
    void movement();
    void shoot(Library&);
    
private:

    // Jump parameters
    float dy;
    int y;
    int shoottimer;
    
};


#endif /* player_hpp */
