#To use cmake, you create a folder for all the compiled/build files
#and don't usually compile things in the same directory as your source files

#I usually do something like the following:
#for xcode:
#mkdir xcode
#cd xcode
#cmake -G Xcode .. #tells cmake to make an XCode project, and that the CMakelists.txt file is in the directory above
#you can then open the asteroids.xcodeproj file with xcode
#in product -> scheme, select asteroids to run that executable when you press
#the play button

# to use make, I do something like
#mkdir build
#cd build
#cmake .. #by default it creates a makefile for you
#make #calls the make command which looks in Makefile to see what to do
#./asteroids  #run the resulting file


#use a recent version of CMake
cmake_minimum_required(VERSION 3.15)

#the project is called asteroids
project(testSFML)

#the project contains an executable named testSFML that uses the following cpp files
add_executable(testSFML src/helloSFML.cpp src/world.cpp src/library.cpp src/player.cpp src/fire.cpp src/rock.cpp)

#create a folder named "cmake" at the top of your project and put the
#findSFML.cmake file in it.  This line tells Cmake to look there for
#find____.cmake files
#set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/cmake")

#this says find the graphics, window, and system libraries
#it uses the FindSFML.cmake file to do so and sets
#some variables ($SFML_LIBRARIES and SFML_INCLUDE_DIRECTORIES) that are
#used below
find_package(SFML 2.5 COMPONENTS graphics REQUIRED)

#tells Cmake that the asteroids executable needs to be linked with SFML
target_link_libraries(testSFML sfml-graphics)

#passes an option to clang when compiling to use recent c++ stuff
target_compile_options(testSFML PUBLIC -std=c++17)
