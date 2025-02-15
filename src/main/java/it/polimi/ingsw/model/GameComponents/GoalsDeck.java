package it.polimi.ingsw.model.GameComponents;

import it.polimi.ingsw.model.Data.CardsLoader;
import it.polimi.ingsw.model.Goals.Goal;
import it.polimi.ingsw.model.Interfaces.GoalDeckInterface;

import java.util.Collections;
import java.util.Stack;

/**
 * Class for goals deck
 */
public class GoalsDeck implements GoalDeckInterface {
    private int numOfGoals;
    private Stack<Goal> goals;

    /**
     * Constructor
     * @param numOfGoals the number of goals in the deck
     * @param goals the stack of goals in the deck
     */
    public GoalsDeck(int numOfGoals, Stack<Goal> goals) {
        this.numOfGoals = numOfGoals;
        this.goals = goals;
    }

    /**
     * Constructor
     */
    public GoalsDeck() {}

    /**
     * @return the number of goals
     */
    public int getNumOfGoals() { return numOfGoals; }

    /**
     * @return stack of goals in the deck
     */
    public Stack<Goal> getGoals() { return goals; }

    /**
     * Take a goal from the top of the stack
     */
    public Goal getGoal(){
        numOfGoals--;
        return goals.pop();
    }

    /**
     * Add a {@link Goal} in the deck
     * @param goal the goal to add in the deck
     */
    public void addGoal(Goal goal) {
        numOfGoals++;
        goals.add(goal);
    }

    /**
     * Shuffle the stack of goals
     */
    public void goalsShuffle(){
        Collections.shuffle(goals);
    }

    /**
     * Function to build the deck from JSON data
     */
    public void buildDeck() {
        this.goals = new Stack<Goal>();
        this.goals.addAll(CardsLoader.getInstance().loadObjectivesCards());
        this.numOfGoals = this.goals.size();
    }
}
