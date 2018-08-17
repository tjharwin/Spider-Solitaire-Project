package solitaire.spider;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class CardTest {

    private Card testCard1;
    private Card testCard2;
    private Card testCard3;
    private Card testCard4;


    @Before
    public void setUp() {

        testCard1 = new Card(1, "h");
        testCard2 = new Card(3, "s");
        testCard3 = new Card(11, "c");
        testCard4 = new Card(13, "d");
    }

    @Test
    public void testGetRank() {

        assertEquals(1, testCard1.getRank());
        assertEquals(3, testCard2.getRank());
        assertEquals(11, testCard3.getRank());
        assertEquals(13, testCard4.getRank());
    }

    @Test
    public void testGetSuit() {

        assertEquals("h", testCard1.getSuit());
        assertEquals("s", testCard2.getSuit());
        assertEquals("c", testCard3.getSuit());
        assertEquals("d", testCard4.getSuit());
    }

    @Test
    public void isFaceUp() {

        testCard2.turnUp();

        assertEquals(false, testCard1.isFaceUp());
        assertEquals(true, testCard2.isFaceUp());
        assertEquals(false, testCard3.isFaceUp());
        assertEquals(false, testCard4.isFaceUp());
    }

    @Test
    public void getFileName() {

        testCard2.turnUp();
        testCard3.turnUp();
        testCard4.turnUp();

        assertEquals("../Spider Solitaire Project/src/cards/back.gif", testCard1.getFileName());
        assertEquals("../Spider Solitaire Project/src/cards/3s.gif", testCard2.getFileName());
        assertEquals("../Spider Solitaire Project/src/cards/jc.gif", testCard3.getFileName());
        assertEquals("../Spider Solitaire Project/src/cards/kd.gif", testCard4.getFileName());
    }

}