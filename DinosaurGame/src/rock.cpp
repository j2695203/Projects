//
//  rock.cpp
//  testSFML
//
//  Created by Jinny Jeng on 9/22/22.
//

#include "rock.hpp"

// constructor
Rock::Rock(){
    rocktimer = 0;
}

// spawn rocks
void Rock::spawnRock(Library& lib){
    // avoid attacking intensively and regularly
    if (rocktimer < 300-lib.gametime.getElapsedTime().asSeconds()){
        rocktimer +=rand()%4;        // Avoid shooting intensively and regularly
    }
    // save to vector for attacking
    else{
        rocks.push_back(lib.spriteRock);
        rocktimer = 0;
    }
}

// rock's mevement
void Rock::moveRock(Library& lib){
    // getting faster
    for(size_t i =0; i < rocks.size(); i++){
        rocks[i].move((-5.f-lib.gametime.getElapsedTime().asSeconds()), 0);
        if(rocks[i].getPosition().x < 0 ){
            rocks.erase(rocks.begin()+i);    // If rock meets boundary of window, delete from vector
        }
    }
}




