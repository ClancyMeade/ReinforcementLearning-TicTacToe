import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

/* Represents an AI ticTakToe player
 * Learns using Reinforcement Learning: 
     * Tabular Q-Learning with epsilon greedy approach */
public class ComputerPlayer extends Player {
    private String qFile; 
    private double gamma; // Discount Factor
    private double alpha; // Learning Rate
    private double epsilon; // Probability of random choice 
    private String currState;
    private String nextState; 
    private String lastAction;
    private HashMap<String, Double> qTable; // key = state + action, value = long term discounted reward
    private boolean isUsingDecay; 

    static int NUM_COLS = 3;
    static int NUM_ROWS = 3;
    static double ALPHA_DECAY_RATE = 0.999999; 
    static double EPSILON_DECAY_RATE = 0.999999; 

    public ComputerPlayer(int symbol, String qFile, double gamma, double alpha, double epsilon, boolean isUsingDecay) {
        super(symbol); 
        this.qFile = qFile; 
        this.gamma = gamma;
        this.alpha = alpha; 
        this.epsilon = epsilon;
        this.currState = ""; 
        this.nextState = "";
        this.lastAction = "";
        this.qTable = new HashMap<String, Double>();
        this.isUsingDecay = isUsingDecay; 
    }


    public HashMap<String, Double> getQTable() {
        return this.qTable; 
    }


    public void setCurrState(String state) {        
        this.currState = state; 
    }


    public void setNextState(String state) {        
        this.nextState = state; 
    }


    public void setLastAction(String action) {        
        this.lastAction = action; 
    }


    public String getCurrState() {
        return this.currState; 
    }


    public String getNextState() {
        return this.nextState; 
    }


    public String getLastAction() {
        return this.lastAction; 
    }


    public double getAlpha() {
        return this.alpha; 
    }


    public double getEpsilon() {
        return this.epsilon; 
    }

    public boolean isUsingDecay() {   
        return this.isUsingDecay; 
    }

    /* Decays the learning rate */
    public void decayAlpha() {
        if(this.alpha > 0.001) {
            this.alpha = this.alpha * ALPHA_DECAY_RATE; 
        }        
    }
    

    /* Decays the rate of exploration */
    public void decayEpsilon() {
        if(this.epsilon > 0.001) {
            this.epsilon = this.epsilon * EPSILON_DECAY_RATE; 
        }
    }


    /* Returns an ArrayList of all possible actions the player can make in the given state */
    public ArrayList<String> getPossibleActions(String state) {
        ArrayList<String> possibleActions = new ArrayList<String>(); 
        int k = 0; 
        for(int i = 0; i < NUM_ROWS; i++) {
            for(int j = 0; j < NUM_COLS; j++) {
                // check for open space (if open: possible action = i,j)
                if(state.substring(k, k+1).equals("0")) {
                    String action = Integer.toString(i) + "," + Integer.toString(j); 
                    possibleActions.add(action); 
                    k += 1; 
                }
                else if(state.substring(k, k+1).equals("-")) {
                    k += 2;
                }
                else if(state.substring(k, k+1).equals("1")) {
                    k += 1;
                }
            }
        }
        return possibleActions;     
    }


    /* Returns a string representation of the players move
     * Output: "row,col" */
    @Override
    public String getAction() {
        ArrayList<String> possibleActions = this.getPossibleActions(this.currState); 

        // Generate a uniform random number in [0, 1]
        Random rand = new Random(); 
        double n = rand.nextDouble(1); 

        if(n < this.epsilon) {
            // Explore: choose a random action 
            int randomIndex = rand.nextInt(possibleActions.size());
            String randomAction = possibleActions.get(randomIndex); 
            return randomAction; 
        }

        else {
            // Greedy: choose the best action
            double maxQ = Double.MIN_VALUE; 
            int randomIndex = rand.nextInt(possibleActions.size()); 
            String bestAction = possibleActions.get(randomIndex); 
            ArrayList<String> tiedActions = new ArrayList<>();             
            for(String a : possibleActions) {
                String key = this.currState + ":" + a; 
                if(this.qTable.containsKey(key)) {
                    if(this.qTable.get(key) > maxQ) {
                        maxQ = this.qTable.get(key); 
                        bestAction = a;
                    }
                }
            }
            // second pass to check for ties in Q values
            for(String b : possibleActions) {
                String key = this.currState + ":" + b; 
                if(this.qTable.containsKey(key)) {
                    if(this.qTable.get(key) == maxQ) {
                        tiedActions.add(b); 
                    }
                }
            }
            // if there are ties, choose action randomly 
            if(tiedActions.size() > 1) {
                int indx = rand.nextInt(tiedActions.size()); 
                String bestActionRandom = tiedActions.get(indx); 
                return bestActionRandom; 
            }
            // no ties, return best action
            return bestAction; 
        }        
    }


    /* Updates Q table given the reward for observation: (currentState, lastAction) -> (nextState)
     * Uses Bellman's Optimality Equation: 
        * Qest(Si, a) = α[ri + γ(maxa' Qest(Sj, a'))] + (1-α)Qest(Si, a) */ 
    public void updateQ(double reward) {
        String key = this.currState + ":" + this.lastAction; 
        if(!this.qTable.containsKey(key)) {
            // State has not been visited yet, start its long term value as zero (non-optimistic)
            this.qTable.put(key, 0.0); 
        }
        double prevQ = this.qTable.get(key); 
        // get all possible actions from the next state, and find the maximum Q value         
        ArrayList<String> possibleActions = this.getPossibleActions(this.nextState); 
        double maxQ = (Double.MIN_VALUE); 
        for(String a: possibleActions) {
            String newKey = this.nextState + ":" + a; 
            if(this.qTable.containsKey(newKey)) {
                maxQ = Math.max(maxQ, this.qTable.get(newKey)); 
            }
        }
        // update q with Bellman's Equation (current estimated long term reward + predicted reward from next state)
        double newQ = (this.alpha * (reward + (this.gamma * maxQ))) + ((1-this.alpha)*prevQ);
        this.qTable.put(key, newQ); 
    }


    /* Updates Q table given the reward for (currentState, lastAction) 
     * This is used to update Q for the players last state of the game */
    public void updateQEnd(double reward) {
        String key = this.currState + ":" + this.lastAction;
        if(!this.qTable.containsKey(key)) {
            this.qTable.put(key, 0.0); 
        }
        double prevQ = this.qTable.get(key); 
        double newQ = (this.alpha * reward) + ((1-this.alpha)*prevQ); 
        this.qTable.put(key, newQ);
    }
    

    /* Writes the players Q table, epsilon, and alphs to a file */
    public void saveQFunction() {
        try {
            FileWriter myWriter = new FileWriter(this.qFile); 
            // write alpha and epsilon 
            String alphaStr = Double.toString(this.alpha);  
            String epsilonStr = Double.toString(this.epsilon); 
            myWriter.write(alphaStr + "\n"); 
            myWriter.write(epsilonStr + "\n"); 
            // write q table 
            for(String key : this.qTable.keySet()) {
                double qValue = this.qTable.get(key); 
                String qValueStr = Double.toString(qValue); 
                String toWrite = key + " " + qValueStr + "\n"; 
                myWriter.write(toWrite); 
            }
            myWriter.close(); 
        } catch (IOException e) {
            System.out.println("File Error");
            e.printStackTrace();
        }
    }

    
    /* Reads the players Q table, epsilon, and alpha from a file  */
    public void loadQFunction() {
        try {
            File myFile = new File(this.qFile);                 
            Scanner myScanner = new Scanner(myFile);             
            if(myFile.length() > 0) {
                // file is not empty, read alpha and epsilon 
                String alphaStr = myScanner.nextLine(); 
                String epsilonStr = myScanner.nextLine(); 
                Double savedAlpha = Double.parseDouble(alphaStr); 
                Double savedEpsilon = Double.parseDouble(epsilonStr);   
                this.alpha = savedAlpha; 
                this.epsilon = savedEpsilon; 
                // load the q function 
                while(myScanner.hasNextLine()) {
                    String line = myScanner.nextLine(); 
                    String[] lineList = line.split(" ");                 
                    String stateActionKey = lineList[0]; 
                    double qValue = Double.parseDouble(lineList[1]); 
                    this.qTable.put(stateActionKey, qValue);
                }
            }            
            myScanner.close(); 
        }
        catch (FileNotFoundException e) {
            System.out.println("File Not Found");
            e.printStackTrace();
        }
    }    
}

