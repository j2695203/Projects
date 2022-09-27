#pragma once
#include <stdio.h>
#include <SFML/Graphics.hpp>
#include "player.hpp"
#include "fire.hpp"
#include "library.hpp"
#include "rock.hpp"

class World{
public:
    World();    // constructor
    void run(); // run program
private:
    sf::RenderWindow window; // window
    sf::Texture textureBkgd; // background
    sf::Sprite spriteBkgd;   // background
    
    // Create Objects
    Library library;   // elements in game
    Player player;
    Fire fire;
    Rock rock;
    
    // Colliision Event
    int collisionCount;
    void collisionEvent();
    
    // Game Process
    void processInput();
    void update();
    void draw();
    void gameOver();
    void restart();
};
