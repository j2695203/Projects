#include <iostream>
#include <stdio.h>
#include <unistd.h>
#include "shelpers.hpp"
#include <readline/readline.h>

int main(int argc, const char *argv[]){

    std::string userInput;
    std::vector<int> backgrounds_pid;

    while( true ){  // instead of ( getline(std::cin, userInput ) because of readline

        userInput = readline("@MyShell > ");

        std::vector<std::string> tokenInput = tokenize(userInput);
        std::vector<Command> commands = getCommands(tokenInput);
//        std::vector<Command> commands = getCommands(tokenize(userInput)); // sometimes fails if I combine into one line, why??? C++ bug! (pass vector+string)

        // deal with each command
        for( int i = 0; i < commands.size(); i++ ) {

            // change directory if it's builtin "cd"
            if( commands[i].exec == "cd" ){

                if( commands[i].argv.size() == 2 || (std::string)commands[i].argv[1] == "~") { // empty path (size 2 means only cd & nullptr)
                    if( chdir(getenv("HOME")) == -1 ){ // syscall error check
                        perror("chdir() failed \n");
                        exit(1);
                    }
                }else{
                    if( chdir(commands[i].argv[1]) == -1 ){ // syscall error check
                        perror("chdir() failed \n");
                        exit(1);
                    }
                }
                continue; // don't execute "cd" in child
            }

            // set environment variables if it's builtin "XX=YY"
            if( commands[i].setEnv ){

                continue; // don't execute builtin in child
            }

            // create fork
            int rc = fork();


            if (rc < 0) { // syscall error check: fork()
                fprintf(stderr, "fork() failed \n");
                exit(1);

            } else if (rc == 0) { // child

                // I/O redirection or pipe
                if (commands[i].fdStdin != 0) {
                    if( dup2(commands[i].fdStdin, 0) == -1 ){ // syscall error check
                        perror("dup2() failed \n");
                        exit(1);
                    }
                }
                if (commands[i].fdStdout != 1) {
                    if( dup2(commands[i].fdStdout, 1) == -1){ // syscall error check
                        perror("dup2() failed \n");
                        exit(1);
                    }
                }
                // execute command and exit when finished
                if( execvp(commands[i].exec.c_str(), const_cast<char *const *>(commands[i].argv.data())) == -1 ){ // syscall error check
                    perror("execvp() failed \n");
                    exit(1);
                }

            } else { // parent

                // wait for child if it's not background
                if( !commands[i].background ){
                    int status;
                    if( waitpid(rc,&status,0) == -1 ){ // syscall error check
                        perror("waitpid() failed \n");
                        exit(1);
                    }
                }else{
                    // print background pid
                    std::cout << "[" << "new background pid: "<< rc << "]" << std::endl;
                    // save background uid
                    backgrounds_pid.push_back(rc);
                }

                // close file descriptors
                if(commands[i].fdStdin != 0){
                    if( close(commands[i].fdStdin) == -1 ){ // syscall error check
                        perror("close() failed \n");
                        exit(1);
                    }
                }
                if(commands[i].fdStdout != 1){
                    if( close(commands[i].fdStdout) == -1 ){ // syscall error check
                        perror("close() failed \n");
                        exit(1);
                    }
                }
            }

            // check if any background commands have completed
            for(int j = 0; j < backgrounds_pid.size(); j++){

                int status;
                if( waitpid(backgrounds_pid[j],&status,WNOHANG) == -1){ // syscall error check
                    perror("waitpid() failed \n");
                    exit(1);
                }

                if(  WIFEXITED(status) != 0 ){ // if any child is finished
                    // print completed pid
                    std::cout<< "[" << "pid: "<< backgrounds_pid[j] << " done. ]" << std::endl;
                    // delete completed
                    backgrounds_pid.erase(backgrounds_pid.begin()+j);
                    j--;
                    std::cout << "[# of background " << backgrounds_pid.size() << " ]" << std::endl;
                }
            }

        }
    }
    return 0;
}