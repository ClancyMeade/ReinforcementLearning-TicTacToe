/* Represents a ticTakToe game */
public class Game {
    private int[][] board; 
    private Player[] players;
    private String boardState; // string representation of the current board 
    private int gamesPlayed; 
    private int p1Wins; 
    private int p2Wins; 
    private int numTies; 

    static int NUM_COLS = 3;
    static int NUM_ROWS = 3;

    public Game(Player player1, Player player2) throws InterruptedException {
        this.board = new int[NUM_ROWS][NUM_COLS];
        this.players = new Player[] {player1, player2};
        this.boardState = ""; 
        this.gamesPlayed = 0; 
        this.p1Wins = 0; 
        this.p2Wins = 0; 
        this.numTies = 0;  
    }


    /* Displays the TicTacToe board */
    public void displayBoard() {       
        System.out.println(" -----------");
        System.out.println("| " + this.board[0][0] + " | " + this.board[0][1] + " | " + this.board[0][2] + " |"); 
        System.out.println(" -----------");
        System.out.println("| " + this.board[1][0] + " | " + this.board[1][1] + " | " + this.board[1][2] + " |"); 
        System.out.println(" -----------");
        System.out.println("| " + this.board[2][0] + " | " + this.board[2][1] + " | " + this.board[2][2] + " |"); 
        System.out.println(" -----------");
    } 


    /* Returns a string representation of the board 
     * Flattens the 2d array into a string */
    public String hashBoard() {
        String hash = ""; 
        for(int i = 0; i < NUM_ROWS; i++) {
            for(int j = 0; j < NUM_COLS; j++) {
                hash = hash + board[i][j];
            }
        }
        return hash; 
    }


    /* Resets the board */
    public void reset() {
        for(int i = 0; i < NUM_ROWS; i++) {
            for(int j = 0; j < NUM_COLS; j++) {
                this.board[i][j] = 0; 
            }
        }
        this.boardState = this.hashBoard(); 
    }


    /* Updates the board given a move and the players symbol */
    public void updateBoard(String action, int symbol) {
        String[] indices = action.split(","); 
        int row = Integer.parseInt(indices[0]); 
        int col = Integer.parseInt(indices[1]); 
        this.board[row][col] = symbol; 
        this.boardState = this.hashBoard(); 
    }


    /* Checks the board for a winner 
     * Returns: 
        * 1 if player1 has won 
        * 2 if player2 has won
        * 0 if game tied 
        * -1 if game is still in progress */
    public int checkForWinner() {
        int numZeroes = 0; // number of empty spaces on board

        // check for horizontal win
        for(int i = 0; i < NUM_ROWS; i++) {
            int rowSum = 0; 
            for(int j = 0; j < NUM_COLS; j++) {
                rowSum += this.board[i][j]; 
                // keep track of how many empty spaces 
                if(this.board[i][j] == 0) {
                    numZeroes += 1; 
                }
            }
            if(rowSum == 3) {
                // player 1 win
                return 1; 
            }
            else if(rowSum == -3) {
                // player 2 win 
                return 2; 
            }
        }

        // check for vertical win 
        for(int j = 0; j < NUM_COLS; j++) {
            int colSum = 0; 
            for(int i = 0; i < NUM_ROWS; i++) {
                colSum += this.board[i][j]; 
            }
            if(colSum == 3) {        
                // player 1 win 
                return 1;         
            }
            else if(colSum == -3) {                
                // player 2 win 
                return 2; 
            }
        }

        // check for diagonal win 
        int leftDiagSum = 0; 
        int rightDiagSum = 0; 
        int j = 0; 
        for(int i = 0; i < NUM_ROWS; i++) {
            leftDiagSum += this.board[i][j]; 
            rightDiagSum += this.board[i][NUM_COLS - 1 - j]; 
            j += 1; 
        }
        if(leftDiagSum == 3 || rightDiagSum == 3) {
            // player 1 win
            return 1; 
        }
        else if(leftDiagSum == -3 || rightDiagSum == -3) {
            // player 2 win
            return 2; 
        }

        // check for tie 
        if(numZeroes == 0) {
            // tie 
            return 0; 
        }
        // game is still in progress 
        return -1; 
    }


    /* Plays a game of TicTakToe between p1 and p2 */
    public void play() {
        this.reset(); 
        int playerNum = 0; // index of current player 
        Player currentPlayer = this.players[playerNum];   
        boolean keepPlaying = true;

        while(keepPlaying) {
            this.displayBoard();

            if(currentPlayer instanceof ComputerPlayer) {
                // Player is Computer, get move 
                ComputerPlayer cp = (ComputerPlayer)currentPlayer; 
                cp.setCurrState(this.boardState);
                String action = cp.getAction(); 
                this.updateBoard(action, cp.symbol); 
                cp.setLastAction(action); 
            }

            else if(currentPlayer instanceof HumanPlayer) {
                // Player is Human, get move 
                HumanPlayer hp = (HumanPlayer)currentPlayer; 
                String action = hp.getAction(); 
                this.updateBoard(action, hp.symbol); 
            }

            // check for win 
            int winner = this.checkForWinner(); 
            if(winner == 1) {
                // player 1 win 
                keepPlaying = false; 
                this.displayBoard();
                this.reset(); 
                System.out.println("Player 1 wins");
            }
            else if(winner == 2) {
                // player 2 wins 
                keepPlaying = false; 
                this.displayBoard();
                this.reset(); 
                System.out.println("Player 2 wins");
            }
            else if(winner == 0) {
                keepPlaying = false; 
                this.displayBoard();
                this.reset(); 
                System.out.println("Tie");
            }

            //switch players 
            playerNum = playerNum ^ 1; 
            currentPlayer = this.players[playerNum]; 
        }
    }


    /* Trains 2 Computer Players learning with Q-learning 
     * p1 and p2 learn (Update Q functions) by playing against eachother 
     * Only rewards players at the end of the game (intermediate states get r = 0): 
        * Player wins: Reward = 10 
        * Player loses: Reward = -10
        * Player ties: Reward = 5 */
    public void train() {
        this.reset(); 
        // training loop (each iteration is a single game)
        while(true) {
            int playerNum = 0; // index of current player 
            int numTurns = 0;   
            ComputerPlayer currentPlayer = (ComputerPlayer)this.players[playerNum];   

            boolean keepPlaying = true;
            while(keepPlaying) {
                if(numTurns > 1) { 
                    // update Q only after each player has made their first move 
                    currentPlayer.setNextState(this.boardState);
                    currentPlayer.updateQ(0.0); 
                }

                // Current player makes a move 
                currentPlayer.setCurrState(this.boardState); 
                String action = currentPlayer.getAction(); 
                this.updateBoard(action, currentPlayer.symbol); 
                currentPlayer.setLastAction(action); 
                
                // Check for win 
                // if game is over, give rewards and update each players Q table 
                int winner = this.checkForWinner();
                if(winner == 1) {
                    // player 1 win
                    ((ComputerPlayer) this.players[0]).updateQEnd(10.0); 
                    ((ComputerPlayer) this.players[1]).updateQEnd(-10.0); 
                    keepPlaying = false; 
                    this.p1Wins++; 
                    this.reset(); 
                } 

                else if(winner == 2) {
                    //player 2 win
                    ((ComputerPlayer) this.players[1]).updateQEnd(10.0);
                    ((ComputerPlayer) this.players[0]).updateQEnd(-10.0);
                    keepPlaying = false; 
                    this.p2Wins++; 
                    this.reset();                     
                }

                else if(winner == 0) {
                    // tie
                    ((ComputerPlayer) this.players[0]).updateQEnd(5.0); 
                    ((ComputerPlayer) this.players[1]).updateQEnd(5.0);
                    keepPlaying = false; 
                    this.numTies++; 
                    this.reset(); 
                }

                // swich players 
                numTurns++; 
                playerNum = playerNum ^ 1; 
                currentPlayer = (ComputerPlayer)this.players[playerNum]; 
            }

            // decay learning rate and rate of exploration for each player
            if(((ComputerPlayer) this.players[0]).isUsingDecay()) {
                ((ComputerPlayer) this.players[0]).decayAlpha();
                ((ComputerPlayer) this.players[0]).decayEpsilon();
            }
            if(((ComputerPlayer) this.players[1]).isUsingDecay()) {
                ((ComputerPlayer) this.players[1]).decayAlpha();
                ((ComputerPlayer) this.players[1]).decayEpsilon();
            }                
            
            if(gamesPlayed % 1000000 == 0) {
                System.out.println("Games Played: " + gamesPlayed);
            }
            this.gamesPlayed++; 
        }
    }


    /* Saves each players Q-table */
    public void saveQFunctions() {
        ComputerPlayer player1 = (ComputerPlayer)this.players[0]; 
        ComputerPlayer player2 = (ComputerPlayer)this.players[1]; 
        player1.saveQFunction();
        player2.saveQFunction();
    }


    /* Restores each players Q-table */
    public void restoreQFunctions() {
        ComputerPlayer player1 = (ComputerPlayer)this.players[0]; 
        ComputerPlayer player2 = (ComputerPlayer)this.players[1]; 
        player1.loadQFunction(); 
        player2.loadQFunction(); 
    }


    /* Displays stats corresponding to AI training */
    public void displayStats() {
        System.out.println();
        System.out.println("Games Played: " + gamesPlayed + "\n" + 
                            "P1 Wins: " + p1Wins + "\n" + 
                            "P2 Wins: " + p2Wins + "\n" + 
                            "Ties: " + numTies); 
        System.out.println();

        double p1WinRate = 100 * ((double)p1Wins/gamesPlayed);
        double p2WinRate = 100 * ((double)p2Wins/gamesPlayed); 
        double tieRate = 100 * ((double)numTies/gamesPlayed);

        System.out.println("P1 Win Percent: " + Math.round(p1WinRate * 100) / 100 + "%");
        System.out.println("P2 Win Percent " + Math.round(p2WinRate * 100) / 100 + "%");
        System.out.println("Tie Percent: " + Math.round(tieRate * 100) / 100 + "%");
        System.out.println();
        System.out.println("P1 States: " + ((ComputerPlayer)this.players[0]).getQTable().size()); 
        System.out.println("P2 States: "  +((ComputerPlayer)this.players[1]).getQTable().size()); 
        System.out.println();
    }
}