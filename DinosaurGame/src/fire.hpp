//
//  fire.hpp
//  testSFML
//
//  Created by Jinny Jeng on 9/22/22.
//

#ifndef fire_hpp
#define fire_hpp

#include <stdio.h>
#include <SFML/Graphics.hpp>
#include "library.hpp"

class Fire{
public:
    
    Fire();
    
    std::vector<sf::Sprite> fires;
    
    void spawnFire(Library&);
    
    void moveFire(Library&);
    
private:
    
    int firetimer;
};


#endif /* fire_hpp */
