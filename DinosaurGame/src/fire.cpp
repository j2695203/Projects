//
//  fire.cpp
//  testSFML
//
//  Created by Jinny Jeng on 9/22/22.
//

#include "fire.hpp"
#include <iostream>

// consturctor
Fire::Fire(){
    firetimer = 0;
}

// spawn fires
void Fire::spawnFire(Library& lib){
    // avoid attacking intensively and regularly
    if (firetimer < 600-lib.gametime.getElapsedTime().asSeconds()){
        firetimer +=rand()%8;
    }
    // save to vector for attacking
    else{
        fires.push_back(lib.spriteFire);
        firetimer = 0;
    }
}

// fire's movement
void Fire::moveFire(Library& lib){
    // getting faster
    for(size_t i =0; i < fires.size(); i++){
        fires[i].move((-5.f-lib.gametime.getElapsedTime().asSeconds()), 0);
        // erase fire out of window
        if(fires[i].getPosition().x < 0 ){
            fires.erase(fires.begin()+i);
        }
    }
}
