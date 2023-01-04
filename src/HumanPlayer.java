import java.util.Scanner;

public class HumanPlayer extends Player {
    private Scanner inScanner;

    static int NUM_COLS = 3;
    static int NUM_ROWS = 3;

    public HumanPlayer(int symbol) {
        super(symbol); 
        this.inScanner = new Scanner(System.in); 
    }

    /* Returns a string representation of the players move
     * Output: "row,col" */
    @Override
    public String getAction() {
        int row = this.getRowMove(); 
        int col = this.getColMove(); 
        String action = row + "," + col; 
        return action; 
    }

    /* Returns the users row choice from stdin */ 
    public int getRowMove() {
        System.out.print("Enter row: "); 
        int rowMove = this.inScanner.nextInt();
        if(rowMove < 0 || rowMove >= NUM_ROWS) { 
            // out of bounds row
            System.out.println("Error: Invalid row");
            System.exit(-1); 
        }
        return rowMove; 
    }

    /* Returns the users column choice from stdin */
    public int getColMove() {
        System.out.print("Enter col: "); 
        int colMove = this.inScanner.nextInt(); 
        if(colMove < 0 || colMove >= NUM_COLS) {
            // out of bounds col
            System.out.println("Error: Invalid col");
            System.exit(-1); 
        }
        return colMove; 
    }
}