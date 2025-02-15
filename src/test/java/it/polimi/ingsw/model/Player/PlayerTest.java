package it.polimi.ingsw.model.Player;

import it.polimi.ingsw.model.Enumerations.Color;
import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest extends TestCase {

    PlayerHand hand = null;
    Player p = new Player("Username", hand);

    @BeforeEach
    public void setup() {

        p.setColor(Color.Blue);
        Color.Blue.toString();
        Color.Red.toString();
        Color.Green.toString();
        Color.Yellow.toString();
    }

    @Test
    public void testGetNickname() {
        assertEquals("Username", p.getNickname());
    }

    @Test
    public void testGetColor() {
        assertEquals(Color.Blue, p.getColor());
    }

    @Test
    public void testGetPlayerHand() {
        assertEquals(hand, p.getPlayerHand());
    }

    @Test
    public void testSetColor() {
        assertEquals(Color.Blue, p.getColor());
    }

    @Test
    public void testTestEquals() {
        Object p2 = new Player("Username1", hand);
        assertFalse(p.equals(p2));
    }
}