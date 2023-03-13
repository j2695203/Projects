# Makefile - CS6015 HW1
#
# To build a target, enter:
# make <target>
#
# Target:
# all - build everything
# clean - delete derived files from make


CXXSOURCE = main.cpp shelpers.cpp

HEADERS = shelpers.hpp

CXXFLAGS = -std=c++11 -O0

all: myShell

myShell: $(CXXSOURCE) $(HEADERS)
	$(CXX) $(CXXFLAGS) -c $(CXXSOURCE)
	$(CXX) $(CXXFLAGS) -o myShell $(CXXSOURCE) -lreadline


.PHONY: clean

clean:
	rm -f *.o myShell *.txt




