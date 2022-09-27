#include <iostream>
#include "world.hpp"


/*
 Constructor
 */
World::World(){
    srand(time(NULL));  // randomly spawn fire and rock
    
    // Set Up Window
    window.create(sf::VideoMode(2400, 1000), "How Dinosaur Died");
    window.setFramerateLimit(60);
    
    // Set Up Background
    if (!textureBkgd.loadFromFile("background.jpg"))
        std::cout<< "EXIT_FAILURE\n";
    spriteBkgd.setTexture(textureBkgd);
    spriteBkgd.setPosition(0, 0);
    spriteBkgd.setScale(1.4, 1.4);
    
    // ??????
    collisionCount = 0;
}

/*
 Run the Game
 */
void World::run(){
    
    // Set Up Texts Font (can't move to other places)
    sf::Font font;
    if (!font.loadFromFile("Phosphate.ttc")){
        std::cout<< "EXIT_FAILURE\n";
    }
    library.textScore.setFont(font);
    library.textTitle.setFont(font);
    library.textEnd.setFont(font);
    library.textKeybrd.setFont(font);
    
    
    // Keep Running Game While Window is Opend
    while (window.isOpen())
    {
        // Process User's Input
        processInput();
        
        // Update Game When Alive
        if(!player.isDead){
            update();
        }
        // Render The Window
        draw();
    }
}

/*
 Process Input Events
 */
void World::processInput(){
    
    // Check Triggered Events
    sf::Event event;
    while (window.pollEvent(event))
    {
        // Close Event
        if (event.type == sf::Event::Closed)
            window.close();
        if (event.key.code == sf::Keyboard::Escape)
            window.close();
    }
    
    // Input Events When Alive
    if (!player.isDead){
        // Move Event
        player.movement();
        // Shoot Siliva
        player.shoot(library);
    }
    else{
        // Restart
        if (sf::Keyboard::isKeyPressed(sf::Keyboard::Enter)){
            restart();
        }
    }
}


/*
 Update the Game
 */
void World::update(){
    
    // Update Player's Boundary (to Check Collision)
    player.box.setPosition(player.spritePlyr.getPosition().x+210.f, player.spritePlyr.getPosition().y+10.f);
    
  
    
    // Spawn Rock
    rock.spawnRock(library);
    rock.moveRock(library);
    
    // Spawn Fireball
    fire.spawnFire(library);
    fire.moveFire(library);
    
    // Check if Collision Between Two Objects
    collisionEvent();
        
    // Score Based on Survival Time
    library.intScore = (library.gametime.getElapsedTime().asSeconds())*1.3;
    library.textScore.setString(std::to_string(library.intScore));
    
}

/*
 Render the Game
 */
void World::draw(){
    
    window.clear(sf::Color::Black);
    window.draw(spriteBkgd);         // background
    window.draw(player.spritePlyr);  // player
    window.draw(library.textScore);  // score
    window.draw(library.textTitle);  // score
    window.draw(library.textKeybrd); // instruction
    
    for(size_t i = 0; i < player.projectiles.size(); i++){
        window.draw(player.projectiles[i]);
    }
    for(size_t i = 0; i < rock.rocks.size(); i++){
        window.draw(rock.rocks[i]);
    }
    for(size_t i = 0; i < fire.fires.size(); i++){
        window.draw(fire.fires[i]);
    }
    
    // Death
    if(player.isDead) {
        window.draw(library.textEnd);  // game over
    }
    window.display();
}


void World::collisionEvent(){
    
    // Eliminate Fire When Shooted By Siliva 3 Times
    for(size_t i = 0; i < player.projectiles.size(); i++){
        for (size_t k = 0; k < fire.fires.size(); k++){
            if(player.projectiles[i].getGlobalBounds().intersects(fire.fires[k].getGlobalBounds())){
                player.projectiles.erase(player.projectiles.begin()+i);
                collisionCount++;
                fire.fires[k].setScale(0.15 - collisionCount*0.03, 0.15- collisionCount*0.03);
                if(collisionCount == 3){
                    fire.fires.erase(fire.fires.begin()+k);
                    collisionCount = 0;
                }
            }
        }
    }
    
    // Gameover When Player Touch Rock
    for(size_t i = 0; i < rock.rocks.size(); i++){
        if(player.box.getGlobalBounds().intersects(rock.rocks[i].getGlobalBounds())){
            gameOver();
        }
    }
    // Gameover When Player Touch Fire
    for(size_t i = 0; i < fire.fires.size(); i++){
        if(player.box.getGlobalBounds().intersects(fire.fires[i].getGlobalBounds())){
            gameOver();
        }
    }
}

/*
 Game Over Setting
 */
void World::gameOver(){
//    library.death.play();
    player.isDead = true;
    // just switch player's face
    player.spritePlyr.setTexture(player.textureDeath);
    player.spritePlyr.setScale(2.5, 2.5);
    player.spritePlyr.setRotation(-30);
    player.spritePlyr.setPosition(player.spritePlyr.getPosition().x-50, player.spritePlyr.getPosition().y+150);
}

/*
 Initialize Values When Restart
 */
void World::restart(){
    collisionCount = 0;
    library.gametime.restart();
    player.isDead = false;
    player.spritePlyr.setTexture(player.texturePlyr); // switch default face
    player.spritePlyr.setScale(1.4, 1.4);
    player.spritePlyr.setPosition(100, 410);
    player.spritePlyr.setRotation(0);
    std::vector<sf::Sprite>().swap(player.projectiles); // release vectors value and memory
    std::vector<sf::Sprite>().swap(fire.fires);
    std::vector<sf::Sprite>().swap(rock.rocks);
}
