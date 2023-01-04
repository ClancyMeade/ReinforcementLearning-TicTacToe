public abstract class Player { 
    protected int symbol; 
    
    public Player(int symbol) {
        this.symbol = symbol;
    }

    protected int getSymbol() {
        return this.symbol; 
    }

    /* Returns a string representation of the players move: "row,col" */
    protected abstract String getAction(); 
}
