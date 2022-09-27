//
//  rock.hpp
//  testSFML
//
//  Created by Jinny Jeng on 9/22/22.
//

#ifndef rock_hpp
#define rock_hpp

#include <stdio.h>
#include <SFML/Graphics.hpp>
#include <vector>
#include "library.hpp"

class Rock{
public:
    
    Rock();
    
    std::vector<sf::Sprite> rocks;
    
    void spawnRock(Library&);
    
    void moveRock(Library&);
    
private:
    
    int rocktimer;
};



#endif /* rock_hpp */
