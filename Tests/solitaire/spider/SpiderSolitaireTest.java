package solitaire.spider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;
import static org.junit.Assert.*;

public class SpiderSolitaireTest {

    private Stack<Card>[] tableau;
    private Stack<Card>[] foundations;
    private Stack<Card> stock;
    private int suitMode = 1;
    private GUI gui;
    private SpiderSolitaire game;
    private ArrayList <Card> testDeck1, testDeck2, testDeck3;
    private Stack<Card> testStack1, testStack2, testStack3;


    @Before
    public void setUp() {

        //game = new SpiderSolitaire();
        testDeck1 = game.createDeck(1);
        testDeck2 = game.createDeck(2);
        testDeck3 = game.createDeck(4);
        testStack1 = new Stack<>();
        testStack2 = new Stack<>();
        testStack3 = new Stack<>();

        for (int i = 0; i < 13; i++) {
            testStack1.push(testDeck1.get(i));
        }

        game.addToFoundation(testStack1);

    }

    @After
    public void tearDown() {


    }


    @Test
    public void testGetTableauStack() {

        for (int i = 0; i < 10; i++){

        assertTrue(game.getTableauStack(i) instanceof Stack);
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetTableauStackValidity() {

        for (int i = 10; i < 20; i++){

            assertTrue(game.getTableauStack(i) instanceof Stack);
        }
    }

    @Test
        public void testGetFoundationCard() {

        //First foundation stack should have cards on it
            assertTrue(game.getFoundationCard(0) instanceof Card);

        //The other foundation stacks shouldn't have cards on them
        for (int i = 1; i < 8; i++){

            assertTrue(game.getFoundationCard(i) == null);
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetFoundationCardValidity() {

        for (int i = 8; i < 16; i++){

            assertTrue(game.getFoundationCard(i) instanceof Card);
        }
    }

    @Test
    public void testGetStockCard() {

        assertTrue(game.getStockCard(5) instanceof Card);
    }

    @Test (expected = EmptyStackException.class)
    public void testGetStockCardEmpty() {

            //Deals from the stock pile 5 times to assert that the method then returns null
            for (int i = 0; i < 5; i++){
                game.dealStock();
            }

            assertEquals(null, game.getStockCard(0));
    }

    @Test
    public void testGetSuitMode() {

        assertTrue(game.getSuitMode() > 0);
        assertTrue(game.getSuitMode() < 5);
        assertTrue(game.getSuitMode() != 3);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetSuitMode() {

        game.setSuitMode(5);
        game.setSuitMode(10);
        game.setSuitMode(-1);
    }

    @Test
    public void testCreateDeck() {

        assertEquals(104, testDeck1.size());

        int sCounter = 0;
        int hCounter = 0;
        int cCounter = 0;
        int dCounter = 0;

        if (game.getSuitMode() == 1) {

            for (Card card: testDeck1) {
                assertEquals("s", card.getSuit());
                assertTrue(card.getRank() > 0);
                assertTrue(card.getRank() < 14);
            }
        }

        if (game.getSuitMode() == 2) {

            for (int i = 0; i < testDeck2.size(); i++) {
                assertTrue(testDeck2.get(i).getRank() > 0);
                assertTrue(testDeck2.get(i).getRank() < 14);
                if (testDeck2.get(i).getSuit().equals("s")) sCounter++;
                if (testDeck2.get(i).getSuit().equals("h")) hCounter++;
            }
            assertEquals(52, sCounter);
            assertEquals(52, hCounter);
        }

        if (game.getSuitMode() == 4) {

            for (int i = 0; i < testDeck3.size(); i++) {
                assertTrue(testDeck3.get(i).getRank() > 0);
                assertTrue(testDeck3.get(i).getRank() < 14);
                if (testDeck3.get(i).getSuit().equals("s")) sCounter++;
                if (testDeck3.get(i).getSuit().equals("h")) hCounter++;
                if (testDeck3.get(i).getSuit().equals("c")) cCounter++;
                if (testDeck3.get(i).getSuit().equals("d")) dCounter++;

            }
            assertEquals(26, sCounter);
            assertEquals(26, hCounter);
            assertEquals(26, cCounter);
            assertEquals(26, dCounter);
        }
    }

    @Test
    public void shuffleDeck() {

        Stack<Card> shuffledTestDeck1 = new Stack<>();
        int counter = 0;

        //Asserts the cards are first in order prior to shuffle deck
        for (int i = 0; i < 13; i++) {
            assertEquals(i + 1, testDeck1.get(i).getRank());
        }
        shuffledTestDeck1 = game.shuffleDeck(testDeck1);

        //Asserts the cards are no longer order after calling shuffle deck
        for (int i = 0; i < 13; i++) {
            if (shuffledTestDeck1.pop().getRank() == i) {
                counter++;
            }
            ;
            assertTrue(counter != 13);
        }
    }

    @Test
    public void testDealGame() {

        game.dealGame();

        for (int i = 0; i < 4; i++) {
            assertEquals(7, game.getTableauStack(i).size());
        }
        for (int i = 4; i < 10; i++) {
            assertEquals(6, game.getTableauStack(i).size());
        }
        for (int i = 0; i < 8; i++) {
            assertEquals(null, game.getFoundationCard(0));
        }

        }


    @Test
    public void testDealStock() {

        //Checks amount of cards on each stack prior to dealing stock
        for (int i = 0; i < 4; i++) {
            assertEquals(7, game.getTableauStack(i).size());
        }
        for (int i = 4; i < 10; i++) {
            assertEquals(6, game.getTableauStack(i).size());
        }

        game.dealStock();

        //Checks amount of cards on each stack after dealing stock
        for (int i = 0; i < 4; i++) {
            assertEquals(8, game.getTableauStack(i).size());
        }
        for (int i = 4; i < 10; i++) {
            assertEquals(7, game.getTableauStack(i).size());
        }
    }

    @Test
    public void testAddToFoundation() throws Exception {
    }

    @Test
    public void pileClicked() throws Exception {
    }

}