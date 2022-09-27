//
//  library.hpp
//  testSFML
//
//  Created by Jinny Jeng on 9/22/22.
//

#ifndef library_hpp
#define library_hpp

#include <stdio.h>
#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>
#include <iostream>

class Library{
public:
    
    Library();
    
    // fire material
    sf::Texture textureFire;
    sf::Sprite spriteFire;
    
    // rock material
    sf::Texture textureRock;
    sf::Sprite spriteRock;
    
    // projectile meterial
    sf::Texture textureProj;
    sf::Sprite spriteProj;
    
    // text material
    sf::Text textScore;
    sf::Text textTitle;//unchanged
    sf::Text textEnd;
    sf::Text textKeybrd;
    
    // score material
    sf::Clock gametime;
    int intScore;
    
    // sound material;
//    sf::SoundBuffer buffer;
//    sf::Sound death;
};

#endif /* library_hpp */
