import java.util.Scanner;

public class Driver { 
    public static String USAGE_MESSAGE = "USAGE: java Driver <-t, -t1, -t2, -p> <qFile1> <qFile2>";
    public static void main(String[] args) throws InterruptedException {
        if(args.length == 0) {
            System.out.println(USAGE_MESSAGE); 
            System.exit(-1);         
        }

        else if(args[0].equals("-t1")) {
            // training only player1 (player2 plays random)
            String qFile = args[1]; 
            ComputerPlayer p1 = new ComputerPlayer(1, qFile, 0.95, 0.2, 0.3, false); 
            ComputerPlayer p2 = new ComputerPlayer(-1, "test.txt", 0.95, 1.0, 1.0, false);
            Game ticTacToe = new Game(p1, p2);
            p1.loadQFunction();
            // Setup hook for handling SIGINT
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.out.println();
                    System.out.println("Training Paused.");                 
                    ticTacToe.displayStats(); 
                    p1.saveQFunction(); 
                }
            }); 
            ticTacToe.train();

        }

        else if(args[0].equals("-t2")) {
            // training only player2 (player1 plays random)
            String qFile = args[1]; 
            ComputerPlayer p2 = new ComputerPlayer(-1, qFile, 0.95, 0.2, 0.3, false); 
            ComputerPlayer p1 = new ComputerPlayer(1, "test.txt", 0.95, 1.0, 1.0, false);         
            Game ticTacToe = new Game(p1, p2); 
            p2.loadQFunction(); 
            // Setup hook for handling SIGINT
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.out.println();
                    System.out.println("Training Paused.");                 
                    ticTacToe.displayStats(); 
                    p2.saveQFunction(); 
                }
            }); 
            ticTacToe.train(); 
        }

        else if(args[0].equals("-t")) {
            // training both players (both players have already gone through some training)
            String p1QFile = args[1]; 
            String p2QFile = args[2]; 
            ComputerPlayer p1 = new ComputerPlayer(1, p1QFile, 0.95, 0.2, 0.3, true); 
            ComputerPlayer p2 = new ComputerPlayer(-1, p2QFile, 0.95, 0.2, 0.3, true); 
            Game ticTacToe = new Game(p1, p2); 
            ticTacToe.restoreQFunctions();
            // Setup hook for handling SIGINT
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.out.println();
                    System.out.println("Training Paused.");                 
                    ticTacToe.displayStats(); 
                    ticTacToe.saveQFunctions(); 
                }
            }); 
            ticTacToe.train(); 
        }

        else if(args[0].equals("-p")) {
            // playing game 
            String p1QFile = args[1]; 
            String p2QFile = args[2]; 
            Scanner inScanner = new Scanner(System.in); 
            // ask player if they want to be p1 or p2
            System.out.println("Choose your player: \n(1) Player 1\n(2) Player 2");
            String choice = inScanner.nextLine();       
            boolean isValid = false; 
            while(!isValid) {
                if(!(choice.equals("1") || choice.equals("2"))) {
                    System.out.println("Please enter 1 or 2.");
                    choice = inScanner.nextLine(); 
                }
                else {
                    isValid = true; 
                }
            }                    
            // set up players and game 
            Player p1; 
            Player p2; 
            if(choice.equals("1")) {
                // human is player 1
                p1 = new HumanPlayer(1); 
                p2 = new ComputerPlayer(-1, p2QFile, 0.95, 1.0, 0, false);     
                ((ComputerPlayer) p2).loadQFunction();          
            }
            else {
                // computer is player 1
                p1 = new ComputerPlayer(1, p1QFile, 0.95, 1.0, 0, false); 
                p2 = new HumanPlayer(-1); 
                ((ComputerPlayer) p1).loadQFunction(); 
            }
            Game ticTacToe = new Game(p1, p2);
            ticTacToe.play();     
            inScanner.close(); 
        }                
    }
}
