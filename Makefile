JC = javac -Werror -d ./classFiles

CLR = \033[1;36m
CLRB = \033[1;32m
NC = \033[0m

# Compiles source code 
build: 
	@echo "${CLR}Building...${NC}"
	@$(JC) \
	./src/Driver.java \
	./src/Player.java \
	./src/ComputerPlayer.java \
	./src/HumanPlayer.java \
	./src/Game.java
	@echo "${CLR}done.${NC}"

play: build
	@echo "${CLR}Playing...${NC}"
	@cd classFiles && java Driver -p ../qFiles/p1Q.txt ../qFiles/p2Q.txt && cd ..
	@echo "${CLR}done.${NC}"

trainP1: build
	@echo "${CLR}Training Player 1...${NC}"
	@cd classFiles && java Driver -t1 ../qFiles/p1Q.txt && cd ..

trainP2: build
	@echo "${CLR}Training Player 2...${NC}"
	@cd classFiles && java Driver -t2 ../qFiles/p2Q.txt && cd .. 

train: build
	@echo "${CLR}Training Both Players...${NC}"
	@ cd classFiles && java Driver -t ../qFiles/p1Q.txt ../qFiles/p2Q.txt && cd ..


