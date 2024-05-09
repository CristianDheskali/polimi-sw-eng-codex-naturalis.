package it.polimi.ingsw.model.GameComponents;

import it.polimi.ingsw.model.Enumerations.AnglePos;
import it.polimi.ingsw.model.Enumerations.Resource;
import it.polimi.ingsw.model.GameComponents.Exceptions.IllegalCardPlacementException;
import it.polimi.ingsw.model.GameComponents.Exceptions.IllegalCoordinatesException;
import it.polimi.ingsw.model.Goals.Goal;
import it.polimi.ingsw.model.Interfaces.CodexInterface;

import java.util.ArrayList;
import java.util.Map;

public class Codex implements CodexInterface {

    private int score;
    private ArrayList<Goal> goalsToPick;
    private Goal personalGoal;
    private Map<Resource,Integer> numOfResources;
    private Map<Coordinate, Card> cards;

    /**
     * Constructor
     * @param goalsToPick the {@link GoalsDeck} of goals to pick the personal goal from
     * @param personalGoal the {@link Goal} that is personal for each codex
     */
    public Codex(ArrayList<Goal> goalsToPick, Goal personalGoal, Map<Resource, Integer> numOfResources, Map<Coordinate, Card> cards) {
        this.goalsToPick = goalsToPick;
        this.personalGoal = personalGoal;
        this.numOfResources = numOfResources;
        this.cards = cards;
    }

    public Codex(Map<Coordinate, Card> cards) {
        this.cards = cards;
    }


    public Codex() {}

    /**
     * @return the score of the codex
     */
    public int getScore() { return this.score; }

    /**
     * @return the deck of the goals to pick
     */
    public ArrayList<Goal> getGoalsToPick() { return this.goalsToPick; }

    /**
     * @return the personal goal of the codex
     */
    public Goal getPersonalGoal() { return this.personalGoal; }

    /**
     * @return the arraylist for the number of resources
     */
    public int getNumOfResources(Resource resource) { return this.numOfResources.get(resource); }

    /**
     * @return the map of the cards
     */
    public Map<Coordinate, Card> getCards() { return this.cards; }


    /**
     * Get the card in a specified coordinate
     * @param coordinate the coordinate of the card
     * @return
     */
    public Card getCard(Coordinate coordinate) { return this.cards.get(coordinate); }

    /**
     * Set goals that can be picked from the player
     * @param goalsToPick goals that can be picked from the player
     */
    public void setGoalsToPick(ArrayList<Goal> goalsToPick) { this.goalsToPick = goalsToPick; }

    /**
     * Set the score of the codex
     * @param score the new score for the codex
     */
    public void incrementScore(int score) { this.score = this.score + score; }

    /**
     * Increase the number of resources
     * @param resource the resource to increase
     * @param amount the amount to increase
     */
    public void incrementNumOfResources(Resource resource, int amount) {
        int numOfResource = this.numOfResources.get(resource);

        this.numOfResources.remove(resource);
        this.numOfResources.put(resource, numOfResource + amount);
    }

    /**
     * Decrease the number of resources
     * @param resource the resource to decrease
     * @param amount the amount to decrease
     */
    public void decreaseNumOfResources(Resource resource, int amount) {
        int numOfResource = this.numOfResources.get(resource);

        this.numOfResources.remove(resource);
        this.numOfResources.put(resource, numOfResource - amount);
    }

    /**
     * Set the personal goal for the deck
     * @param personalGoal the personal goal for the codex
     */
    public void pickPersonalGoal(Goal personalGoal) {
        this.personalGoal = personalGoal;
    }

    /**
     * Place a {@link Card} in the specified {@link Coordinate}
     * @param coordinate the coordinates of the card
     * @param card the card to be placed
     */
    public void placeCard(Coordinate coordinate, Card card) throws IllegalCoordinatesException, IllegalCardPlacementException {
        if(cards.containsKey(coordinate)) {
            throw new IllegalCoordinatesException("The coordinates are not free to use");
        }
        if(!verifyPlacement(coordinate, card)) {
            throw new IllegalCardPlacementException("Can't place the card here");
        }
        getResourcesFromCard(card);
        cards.put(coordinate, card);
    }

    /**
     * Place a {@link GoldCard} in the specified {@link Coordinate}
     * @param coordinate the coordinates of the card
     * @param goldCard the card to be placed
     * @throws IllegalCoordinatesException
     */
    public void placeGoldCard(Coordinate coordinate, GoldCard goldCard) throws IllegalCoordinatesException,IllegalCardPlacementException {
        if(cards.containsKey(coordinate)) {
            throw new IllegalCoordinatesException("The coordinates are not free to use");
        }
        if(!verifyPlayCondition(goldCard)) {
            throw new IllegalCardPlacementException("You don't have enough resources to play the card");
        }
        if(!verifyPlacement(coordinate, goldCard)) {
            throw new IllegalCardPlacementException("Can't place the card here");
        }
        getResourcesFromCard(goldCard);
        addPoints(goldCard);
        cards.put(coordinate, goldCard);
    }

    /**
     * Verify the placement of the card
     * @param coordinate the coordinates of the card
     * @return true if the card can be placed
     */
    private boolean verifyPlacement(Coordinate coordinate, Card card) {
        Coordinate ul = new Coordinate(coordinate.getX() - 1, coordinate.getY() + 1);
        Coordinate ur = new Coordinate(coordinate.getX() + 1, coordinate.getY() + 1);
        Coordinate dl = new Coordinate(coordinate.getX() - 1, coordinate.getY() - 1);
        Coordinate dr = new Coordinate(coordinate.getX() + 1, coordinate.getY() - 1);

        if(!getCards().containsKey(ul) && !getCards().containsKey(ur) && !getCards().containsKey(dl) && !getCards().containsKey(dr)) {
            return false;
        }

        if( (getCard(ul) != null && getCard(ul).getAngle(AnglePos.DR).isAttached()) ||
            (getCard(ul) != null && getCard(ur).getAngle(AnglePos.DL).isAttached()) ||
            (getCard(ul) != null && getCard(dl).getAngle(AnglePos.UR).isAttached()) ||
            (getCard(ul) != null && getCard(dr).getAngle(AnglePos.UL).isAttached()) ) {
            return false;
        }

        if(getCards().containsKey(ul)) {
            Angle DR = cards.get(ul).getAngle(AnglePos.DR);
            if(DR.getResource() != null) {
                decreaseNumOfResources(DR.getResource(), 1);
            }
            DR.setHidden();
            DR.setAttached(card.getAngle(AnglePos.UL));
            card.getAngle(AnglePos.UL).setAttached(DR);
        }

        if(getCards().containsKey(ur)) {
            Angle DL = cards.get(ur).getAngle(AnglePos.DL);
            if(DL.getResource() != null) {
                decreaseNumOfResources(DL.getResource(), 1);
            }
            DL.setHidden();
            DL.setAttached(card.getAngle(AnglePos.UR));
            card.getAngle(AnglePos.UR).setAttached(DL);
        }

        if(getCards().containsKey(dl)) {
            Angle UR = cards.get(dl).getAngle(AnglePos.UR);
            if(UR.getResource() != null) {
                decreaseNumOfResources(UR.getResource(), 1);
            }
            UR.setHidden();
            UR.setAttached(card.getAngle(AnglePos.DL));
            card.getAngle(AnglePos.DL).setAttached(UR);
        }

        if(getCards().containsKey(dr)) {
            Angle UL = cards.get(dr).getAngle(AnglePos.UL);
            if(UL.getResource() != null) {
                decreaseNumOfResources(UL.getResource(), 1);
            }
            UL.setHidden();
            UL.setAttached(card.getAngle(AnglePos.DR));
            card.getAngle(AnglePos.DR).setAttached(UL);
        }
        return true;
    }

    /**
     * Verify that the {@link Card} can be played
     * @param goldCard the card that needs to be verified
     * @return true if the card can be played
     */
    private boolean verifyPlayCondition(GoldCard goldCard) {
        int[] playCondition = new int[4];
        ArrayList<Resource> resources = goldCard.getPlayCondition();

        for(int i  = 0; i < resources.size(); i++) {
            if(resources.get(i) == Resource.PLANT) {
                playCondition[0]++;
            } else if(resources.get(i) == Resource.ANIMAL) {
                playCondition[1]++;
            } else if(resources.get(i) == Resource.FUNGI) {
                playCondition[2]++;
            } else if(resources.get(i) == Resource.INSECT) {
                playCondition[3]++;
            }
        }
        if(getNumOfResources(Resource.PLANT) >= playCondition[0] && getNumOfResources(Resource.ANIMAL) >= playCondition[1] && getNumOfResources(Resource.FUNGI) >= playCondition[2] && getNumOfResources(Resource.INSECT) >= playCondition[3]) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get resources from the {@link Card}
     * @param card the card to get the resources from
     */
    private void getResourcesFromCard(Card card) {
        if(card.getAngles().get(AnglePos.UL).getResource() != null && card.getAngles().get(AnglePos.UL).getResource() != Resource.BLANK) {
            incrementNumOfResources(card.getAngles().get(AnglePos.UL).getResource(), 1);
        }
        if(card.getAngles().get(AnglePos.UR).getResource() != null && card.getAngles().get(AnglePos.UR).getResource() != Resource.BLANK) {
            incrementNumOfResources(card.getAngles().get(AnglePos.UR).getResource(), 1);
        }
        if(card.getAngles().get(AnglePos.DL).getResource() != null && card.getAngles().get(AnglePos.DL).getResource() != Resource.BLANK) {
            incrementNumOfResources(card.getAngles().get(AnglePos.DL).getResource(), 1);
        }
        if(card.getAngles().get(AnglePos.DR).getResource() != null && card.getAngles().get(AnglePos.DR).getResource() != Resource.BLANK) {
            incrementNumOfResources(card.getAngles().get(AnglePos.DR).getResource(), 1);
        }
    }

    /**
     * Add points after placing a {@link GoldCard}
     * @param goldCard the gold card that needs to be calculated
     */
    private void addPoints(GoldCard goldCard) {
        if(goldCard.getClass() == AngleGoldCard.class) {
            incrementScore(goldCard.getCardScore() * ((AngleGoldCard) goldCard).getNumOfAnglesCovered());
        } else if(goldCard.getClass() == ResourceGoldCard.class) {
            incrementScore(goldCard.getCardScore() * getNumOfResources(((ResourceGoldCard) goldCard).getResourceType()));
        } else if(goldCard.getClass() == GoldCard.class) {
            incrementScore(goldCard.getCardScore());
        }
    }

    /**
     * Set the initial card for the codex
     * @param initialCard the initial card of the codex
     */
    public void setInitialCard(InitialCard initialCard) {
        cards.put(new Coordinate(80, 80), (Card) initialCard);
    }


}
