#include "shelpers.hpp"
#include <fcntl.h>

/*
  text handling functions
 */

bool splitOnSymbol(std::vector<std::string>& words, int i, char c){
  if(words[i].size() < 2){ return false; }
  int pos;
  if((pos = words[i].find(c)) != std::string::npos){
	if(pos == 0){
	  //starts with symbol
	  words.insert(words.begin() + i + 1, words[i].substr(1, words[i].size() -1));
	  words[i] = words[i].substr(0,1);
	} else {
	  //symbol in middle or end
	  words.insert(words.begin() + i + 1, std::string(1,c));
	  std::string after = words[i].substr(pos + 1, words[i].size() - pos - 1);
	  if(!after.empty()){
		words.insert(words.begin() + i + 2, after);
	  }
	  words[i] = words[i].substr(0, pos);
	}
	return true;
  } else {
	return false;
  }
}

std::vector<std::string> tokenize(const std::string& s){

  std::vector<std::string> ret;
  int pos = 0;
  int space;
  //split on spaces
  while((space = s.find(' ', pos)) != std::string::npos){
	std::string word = s.substr(pos, space - pos);
	if(!word.empty()){
	  ret.push_back(word);
	}
	pos = space + 1;
  }

  std::string lastWord = s.substr(pos, s.size() - pos);
  if(!lastWord.empty()){
	ret.push_back(lastWord);
  }

  for(int i = 0; i < ret.size(); ++i){
      char ch[] =  {'&', '<', '>', '|', '='};
	for(auto c :ch){
	  if(splitOnSymbol(ret, i, c)){
		--i;
		break;
	  }
	}
  }
  return ret;
}


std::ostream& operator<<(std::ostream& outs, const Command& c){
  outs << c.exec << " argv: ";
  for(const auto& arg : c.argv){ if(arg) {outs << arg << ' ';}}
  outs << "fds: " << c.fdStdin << ' ' << c.fdStdout << ' ' << (c.background ? "background" : "");
  return outs;
}


std::vector<Command> getCommands(const std::vector<std::string>& tokens){
  std::vector<Command> ret(std::count(tokens.begin(), tokens.end(), "|") + 1);  //1 + num |'s commands

  int first = 0;
  int last = std::find(tokens.begin(), tokens.end(), "|") - tokens.begin();
  bool error = false;

  for(int i = 0; i < ret.size(); ++i){

    // first character can't be symbols
	if((tokens[first] == "&") || (tokens[first] == "<") ||
		(tokens[first] == ">") || (tokens[first] == "|")){
	  error = true;
	  break;
	}

    // initialize each command ( ret[i] )
	ret[i].exec = tokens[first];
	ret[i].argv.push_back(tokens[first].c_str()); //argv0 = program name
	std::cout << "exec start: " << ret[i].exec << std::endl;
	ret[i].fdStdin = 0;
	ret[i].fdStdout = 1;
	ret[i].background = false;
    ret[i].setEnv = false;

    // set command contents
	for(int j = first + 1; j < last; ++j){

	  if( tokens[j] == ">" || tokens[j] == "<" ){ // I/O redirection

        if( tokens[j] == ">" ){ // write
            ret[i].fdStdout = open(tokens[j+1].c_str(), O_WRONLY|O_CREAT|O_TRUNC, 0777);
            break;
        }else{ // read
            ret[i].fdStdin = open(tokens[j+1].c_str(), O_RDONLY);
        }
        // syscall error check: open()
        if( ret[i].fdStdout == -1 || ret[i].fdStdin == -1 ){
            perror("open() failed \n");
            error = true;
            break;
        }

      } else if( tokens[j] == "&" ){ // builtin: background
		ret[i].background = true;

      } else if( tokens[j] == "=" ){ // builtin: environment variables
          ret[i].setEnv = true;
          // set environment variable
          if( setenv(tokens[j-1].c_str(), tokens[j+1].c_str(), 0) == -1 ){ // syscall error check
              perror("setenv() failed \n");
              error = true;
              break;
          }
          continue;

      } else if( tokens[j].find("$") != std::string::npos ){ // builtin: environment variables (if contains $)
          // get environment variable
          char* ev = getenv(tokens[j].substr(1,std::string::npos).c_str()); // no error for this syscall (only NULL if there is no match)
          ret[i].argv.push_back(ev);

      } else { // otherwise this is a normal command line argument!
          ret[i].argv.push_back(tokens[j].c_str());
	  }
	}

    // set pipe if there are multiple commands
	if(i > 0){
        // open a pipe
        int fd[2];
        // syscall error check: pipe()
        if( pipe(fd) != 0 ){
            perror("pipe() failed \n");
            error = true;
            break;
        }

        // Connect the ends to the fds for the commands (pipe)
        ret[i].fdStdin = fd[0];
        ret[i-1].fdStdout = fd[1];
	}

	//exec wants argv to have a nullptr at the end!
	ret[i].argv.push_back(nullptr);

	//find the next pipe character
	first = last + 1;
	if(first < tokens.size()){
	  last = std::find(tokens.begin() + first, tokens.end(), "|") - tokens.begin();
	}
  }

  // close any file descriptors you opened in this function!
  if(error){
    for(Command cmd : ret){
        if( cmd.fdStdin != 0 ){
            if( close(cmd.fdStdin) == -1 ){ // syscall error check
                perror("close() failed \n");
            }
        }
        if( cmd.fdStdout != 1 ){
            if( close(cmd.fdStdout) == -1 ){ // syscall error check
                perror("close() failed \n");
            }
        }
    }
    ret.clear(); // returns an empty vector on error
  }
  return ret;
}
