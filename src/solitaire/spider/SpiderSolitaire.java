package solitaire.spider;

import java.util.*;

/**
 * <h1>solitaire.spider.SpiderSolitaire</h1>
 * This is a class for creating a game of spider solitaire. There are three variations that can be created: A one suit
 * game, a two suit game and a four suit game.
 *
 * The game is played with two packs of playing cards (104 cards) in all three modes. 4 rows of 10 face down cards are
 * dealt to form the tableau columns. 4 more cards are dealt face down to the 4 leftmost columns and then a face up card
 * is dealt to the end of each column. The remaining cards are placed face down to form the stock pile.
 *
 * The object of the game is to build cards of descending suit sequence from King to Ace within the tableau columns.
 * When such a sequence has been formed, it is automatically removed to one of the 8 foundation. When all 104 cards
 * have been played to the foundation as eight separate King to Ace sequences then the game is won.
 *
 *
 * @author Thomas Harwin
 * @version 1.0
 * @since 2018-06-11
 */

public class SpiderSolitaire {

    private static SpiderSolitaire game;
    private Stack<Card>[] tableau;
    private Stack<Card>[] foundation;
    private Stack<Card>[] stock;
    private int suitMode = 0;
    private int stockCount = 0;
    private GUI gui;
    private SortingAlgorithm sortingAlgorithm;

    public static void main(String[] args) {

        getInstance();
    }

    private SpiderSolitaire() {

        //Creates 10 tableau stacks
        tableau = (Stack<Card>[]) new Stack[10];
        for (int i = 0; i < tableau.length; i++) {

            tableau[i] = new Stack<>();
        }

        //Creates 8 foundation stacks
        foundation = (Stack<Card>[]) new Stack[8];
        for (int i = 0; i < foundation.length; i++) {

            foundation[i] = new Stack<>();
        }

        //Creates 5 stock stacks
        stock = (Stack<Card>[]) new Stack[5];
        for (int i = 0; i < stock.length; i++) {

            stock[i] = new Stack<>();
        }

        //Creates the Sorting Algorithm
        this.sortingAlgorithm = new SortingAlgorithm(this);

        //Creates the GUI
        this.gui = new GUI(this);
    }

    /**
     * A method for returning a single instance of a SpiderSolitaire  if one has not already been instantiated.
     *
     * @return A SpiderSolitaire object
     *
     */

    public static SpiderSolitaire getInstance() {

        if (game == null)
            game = new SpiderSolitaire();

        return game;
    }

    /**
     * A method for returning a tableau stack of a given index.
     *
     * @param index An integer value representing the index of the tableau stack.
     * @return A stack of cards from the tableau playing area.
     *
     */

    public Stack<Card> getTableauStack(int index) {

        if (index < 0 || index > 9) {
            throw new IllegalArgumentException("Error! " + index + " is not a valid tableau index.");
        }

        return tableau[index];
    }

    /**
     * A method for returning a stock stack of a given index.
     *
     * @param index An integer value representing the index of the stock stack.
     * @return A stack of cards from the stock pile.
     *
     */

    public Stack<Card> getStockStack(int index) {

        if (index < 0 || index > 4) {
            throw new IllegalArgumentException("Error! " + index + " is not a valid stock index.");
        }

        return stock[index];
    }

    /**
     * A method for getting a card from the top of a the stock pile. This card will always be a card facing down.
     *
     * @param index An integer value representing the index of the stock stack.
     * @return A card object from the top of the foundation stack. Returns null if the foundation pile is empty.
     *
     */

    public Card getStockCard(int index) {

        if (stock[index].isEmpty()) {

            return null;
        }
        else {

            return stock[index].peek();
        }
    }

    /**
     * A method for getting a card from the top of a foundation stack.
     *
     * @param index An integer value representing the index of the foundation stack.
     * @return A card object from the top of the foundation stack. Returns null if the foundation stack is empty.
     *
     */

    public Card getFoundationCard(int index) {

        if (index < 0 || index > 7) {
            throw new IllegalArgumentException("Error! " + index + " is not a valid foundation index.");
        }

        if (foundation[index].isEmpty()) {

            return null;
        }
        else {

            return foundation[index].peek();
        }
    }

    /**
     * A method for getting the suit mode.
     *
     * @return An integer value of the suit mode to be played
     *
     */

    public int getSuitMode() {

        return suitMode;
    }

    /**
     * A method for getting the sorting algorithm object.
     *
     * @return The Sorting Algorithm object
     *
     */

    public SortingAlgorithm getSortingAlgorithm() {

        return sortingAlgorithm;
    }

    /**
     * A method for setting how many suits the game of solitaire will use.
     *
     * @param suitAmount An integer value representing the mode to be played (1, 2 or 4 suits).
     *
     */

    public void setSuitMode(int suitAmount) {

        if (!(suitAmount == 1 || suitAmount == 2 || suitAmount == 4)) {
            throw new IllegalArgumentException("Error! " + suitAmount + " is not a valid amount of suits to be played.");
        }

        this.suitMode = suitAmount;
    }

    /**
     * A method for starting the sorting algorithm.
     *
     */

    public void startSortingAlgorithm() {

        sortingAlgorithm.start();
    }

    /**
     * A method for pausing the sorting algorithm.
     *
     */

    public void pauseSortingAlgorithm() {

        sortingAlgorithm.getSortingAlgorithm().interrupt();
    }

    /**
     * A method for ending the sorting algorithm.
     *
     */

    public void endSortingAlgorithm() {

        gui.simulationStatistics();
    }

    /**
     * A method for creating the deck of cards used in the game. This creates 2 decks (104 cards in total) and is
     * dependent on which game mode the user selects. If they select mode 1, it will create 104 cards of spade suit.
     * If they select 2, it will create 52 cards of spade suit and 52 of heart suit. If they select 4, it will create 26
     * cards of each of the four suits.
     *
     * @param suitMode The mode to be played (1, 2 or 4 suits)
     * @return An ArrayList of 104 unshuffled cards
     *
     */

    public ArrayList<Card> createDeck(int suitMode) {

        ArrayList<Card> deck = new ArrayList<>();

        for (int i = 1; i <= 8; i++) {

            switch(suitMode) {

                case 1: //1 suit game

                    for (int j = 1; j <= 13; j++) {

                        String suit = "s";
                        Card card = new Card(j, suit);
                        deck.add(card);
                    }
                    break;

                case 2: //2 suit game

                    for (int j = 1; j <= 13; j++) {

                        String suit = "";
                        if (i <= 4) {
                            suit = "s";
                        }
                        if (i >= 5) {
                            suit = "h";
                        }
                        Card card = new Card(j, suit);
                        deck.add(card);
                    }
                    break;

                case 4: //4 suit game

                    for (int j = 1; j <= 13; j++) {

                        String suit = "";
                        if ((i == 1) | (i == 2)) {
                            suit = "c";
                        }
                        if ((i == 3) | (i == 4)) {
                            suit = "s";
                        }
                        if ((i == 5) | (i == 6)) {
                            suit = "h";
                        }
                        if ((i == 7) | (i == 8)) {
                            suit = "d";
                        }
                        Card card = new Card(j, suit);
                        deck.add(card);
                    }
                    break;
            }
        }

        return deck;
    }

    /**
     * A method for shuffling a deck of cards so their order is arbitrary. This method creates a new stack and will
     * remove cards 1 by 1 from the ArrayList passed in a random order and push them onto the stack.
     *
     * @param deck An ArrayList of cards
     * @return A Stack of shuffled cards
     *
     */

    public Stack<Card> shuffleDeck(ArrayList<Card> deck) {

        Stack<Card> shuffledDeck = new Stack<>();

        //Selects a card at random from the deck of cards and pushes it onto the shuffled deck stack
        while (deck.size() > 0) {
            int index = (int) (Math.random() * deck.size());
            shuffledDeck.push(deck.remove(index));
        }

        return shuffledDeck;
    }

    /**
     * A method for dealing a deck of cards onto the playing board (tableau). The first four stacks have five cards
     * whilst the other six only have four cards. The remaining cards form the stock pile which is split into 5 piles.
     * Any cards from a previous game are disposed of.
     *
     */

    public void dealGame() {

        //The created deck of cards is shuffled and forms a temporary stock pile
        Stack<Card> tempStock = shuffleDeck(createDeck(suitMode));

        for (int i = 0; i < tableau.length; i++) {

            tableau[i] = new Stack<>();
            //The first 4 stacks have 6 cards
            if (i < 4) {
                for (int j = 0; j < 6; j++) {
                    tableau[i].push(tempStock.pop());
                }
            }
            //The last 6 stacks only have 5 cards
            else {
                for (int j = 0; j < 5; j++) {
                    tableau[i].push(tempStock.pop());
                }
            }
            //Turns the last card over so it is face up
            tableau[i].peek().turnUp();
        }

        stockCount = 0;
        //The stock pile is split into 5 new piles
        for (int i = 0; i < 5; i++) {

            stock[i] = new Stack<>();

            for (int j = 0; j < 10; j++){
                stock[i].push(tempStock.pop());
            }
            stockCount++;
        }

        //Refreshes the foundation stacks in case a previous game has been played
        for (int i = 0; i < foundation.length; i++) {

            foundation[i] = new Stack<>();
        }

        System.out.println("Game dealt.");
    }

    /**
     * A method for dealing a single card face up on top of each tableau stack. This method is used when the user clicks
     * on the stock pile and there are cards left in the stock pile. The stock pile can only be dealt from when all
     * of the tableau stacks have at least 1 card on them. There are enough cards for 5 dealings from the
     * stock pile.
     *
     */

    public void dealStock() {

        boolean canDealStock = true;

        //If there are any empty stacks on the tableau, you cannot deal from the stock
        for (Stack<Card> stack:tableau){
            if (stack.isEmpty()) {

                canDealStock = false;
            }
        }

        if (!canDealStock){
            System.out.println("You cannot deal from the stock whilst there are empty tableau stacks.");
        }

        else if (stockCount == 0) {
            System.out.println("The stock pile is empty.");
        }

        else {
            for (int i = 0; i < tableau.length; i++) {
                tableau[i].push(stock[stockCount - 1].pop());
                tableau[i].peek().turnUp();
            }
            stockCount--;
            System.out.println("Stock dealt.");
            gui.repaint();
        }
    }

    /**
     * A method for checking that a selected stack of cards can be moved to another stack (ie. the cards run
     * sequentially and are of the same suit). If the amount of cards to be moved is 1 then no checks need to take
     * place. The cards are one by one moved to a temporary stack with each card to be moved being checked against the
     * last card. A running counter ensures that the method returns true if all the cards to be moved are in order and
     * of the same suit. The cards are then put back onto the indexed tableau stack and the removeCards() method can be
     * called. Whilst an Iterator would be best used here, Java's Iterator class does not abide by the Last In First Out
     * (LIFO) properties of the Stack data structure and so it could be iterating over face down cards.
     *
     * @param index The tableau index of where the cards will be removed from
     * @param cardCount The amount of cards to be removed from the top of the stack
     * @return A temporary stack of cards in reverse order
     */

    private boolean canRemoveCards(int index, int cardCount) {

        //Creates a new temporary stack of cards so that the stack of cards to be moved can be iterated through and
        //checked one by one
        Stack<Card> cardsToMove = new Stack<>();
        int counter = 0;

        //If the tableau index has no cards there is nothing to be removed
        if (tableau[index].isEmpty()) {

            return false;
        }

        //If there is just 1 card to be moved, no checks are made and so no cards need to be temporarily removed
        if (cardCount == 1) {

            counter++;
        }

        //If there are multiple cards to be moved...
        else {

            //No comparisons need to be made on the first card
            cardsToMove.push(tableau[index].pop());
            counter++;

            for (int i = 0; i < cardCount - 1; i++) {

                //Checks that the next card from the same tableau stack is 1 higher in rank, the same suit and face up
                if ((cardsToMove.peek().getRank() + 1 == tableau[index].peek().getRank()) &&
                        (cardsToMove.peek().getSuit().equals(tableau[index].peek().getSuit())) &&
                        (tableau[index].peek().isFaceUp())) {

                    //Removes the compared card so the next card can be peeked at and compared
                    cardsToMove.push(tableau[index].pop());
                    counter++;
                }
            }

            //Once all comparisons have been made, the cards are put back one by one onto their original stack
            for (int i = 0; i < cardCount; i++) {

                tableau[index].push(cardsToMove.pop());
            }

        }

        //If the amount of cards to be moved matches the amount that were successfully compared, the method returns true
        return (cardCount == counter);
    }

    /**
     * A method for removing a selected stack of cards ready to move to another stack. The cards are removed from the
     * tableau stack and added to a new temporary stack ready to be moved. The cards are stacked in reverse order with
     * the highest rank card at the top so they are ready to be popped off in the correct order onto the new stack.
     *
     * @param index The tableau index of where the cards will be removed from
     * @param cardCount The amount of cards to be removed from the top of the stack
     * @return A temporary stack of cards in reverse order, ready to be placed onto a new stack
     */

    private Stack<Card> removeCards(int index, int cardCount) {

        //Creates a new temporary stack of cards to be moved to a different tableau index
        Stack<Card> cardsToMove = new Stack<>();

            for (int i = 0; i < cardCount; i++){

                    cardsToMove.push(tableau[index].pop());
                }

        return cardsToMove;
    }

    /**
     * A method for checking if a single selected card or a stack of cards can be added to another stack.
     *
     * @param cardsToAdd A stack which may contain 1 or more cards to be added to a tableau index
     * @param index The index of the tableau where the cards are to be added
     * @return Returns true if the stack can be added and false if it can't
     */

    private boolean canAddToStack(Stack<Card> cardsToAdd, int index) {

        boolean canAddToStack = false;
        Card tempCard = cardsToAdd.peek();

        //If the destination stack is empty, the cards can always be moved
        if (tableau[index].isEmpty()) {

            canAddToStack = true;
        }
        //If the card on top of the destination stack has a rank 1 higher than the card on the bottom of the stack to
        // move, then the cards can be added regardless of suit
        if (tempCard.getRank() + 1 == tableau[index].peek().getRank()) {

            canAddToStack = true;
        }

        return canAddToStack;
    }

    /**
     * A method for adding a stack of cards to another stack on the tableau.
     *
     * @param cardsToAdd A stack which may contain 1 or more cards to be added to a tableau index
     * @param index The index of the tableau where the cards are to be added
     */

    private void addToStack(Stack<Card> cardsToAdd, int index) {

        while(!cardsToAdd.isEmpty()) {

                tableau[index].push(cardsToAdd.pop());
        }
        //Each time one card (or more) is moved from one tableau stack to another, a check is made to see if a full run
        //of cards has been completed on the destination tableau stack
        canAddToFoundation(index);
    }

    /**
     * A method for checking if a stack of cards has the ranks 1-13 and can be added to the foundation. If a stack can
     * be added to the foundation then the addToFoundation() method is called and it is automatically added.
     *
     * @param index The index of the tableau where the cards are to be taken from and added to the foundation
     * @return Returns true if a stack of cards can be added and false if the stack can't
     */

    private boolean canAddToFoundation(int index) {

        //Creates a new stack for a full run of cards, ace to king
        Stack<Card> temporaryStack = new Stack<>();

        for (int i = 1; i < 14; i++) {

            if (tableau[index].size() > 0) {
                //The first card only needs to be an ace, the suit doesn't matter
                if (tableau[index].peek().getRank() == i && i == 1 && temporaryStack.isEmpty()) {

                    temporaryStack.push(tableau[index].pop());

                }
                //Each card after the ace has to match the suit of the ace so that all cards to be added to the foundation
                //are of the same suit
                else if ((tableau[index].peek().getRank() == i) && !temporaryStack.isEmpty()) {

                    if (tableau[index].peek().getSuit().equals(temporaryStack.peek().getSuit()) &&
                            temporaryStack.peek().getRank() + 1 == tableau[index].peek().getRank()) {

                        temporaryStack.push(tableau[index].pop());
                    }
                }
            }
        }
        //Checks if the newly created stack has 13 cards in it. If it does, the cards are added to a foundation stack
        if (temporaryStack.size() == 13) {

            addToFoundation(temporaryStack);
            System.out.println("Foundation stack formed.");
            return true;
        }
        //If the newly created stack has less than 13 cards, they are returned to the tableau
        else {

            while (temporaryStack.size() > 0) {

                tableau[index].push(temporaryStack.pop());
            }

            return false;
        }
    }

    /**
     * A method for checking if a stack of cards has the ranks 1-13 and can be added to the foundation. If a stack can
     * be added to the foundation then it is added.
     *
     * @param completedStack A stack of cards of the same suit in a run of rank 1-13 that will be added to the foundation
     */

    public void addToFoundation(Stack<Card> completedStack) {

        if (completedStack.size() == 13) {

            //Iterates through the foundation to find an empty foundation to unload the completed stack to
            for (int i = 0; i < foundation.length; i++) {

                if (foundation[i].isEmpty()) {

                    while (!completedStack.isEmpty()) {

                        foundation[i].push(completedStack.pop());
                    }
                }
            }
            //If the final foundation is filled, the game is complete
            if (foundation[7].size() == 13 && !sortingAlgorithm.isRunning()) {

                gui.gameComplete();
            }
        }
        else throw new IllegalArgumentException("The completed stack must have 13 cards");
    }

    /**
     * A method for deciding what happens when a card in a stack on the tableau is clicked. Any face up card in a stack
     * can be selected.
     *
     * @param index The index of the tableau that has been clicked
     * @param selectedCardAmount The amount of cards that have been selected
     *
     */

    public void stackClicked(int index, int selectedCardAmount) {

        System.out.print("Stack at index " + index + " clicked. ");

        //If the selected stack is empty and the tableau is not selected, nothing happens
        if (tableau[index].isEmpty() && !gui.isTableauSelected()) {

            try {
                System.out.println("Empty stack selected.");
                gui.unselect();
            }
            catch (EmptyStackException e) {

            }
        }
        //If the selected stack is empty and the tableau is selected, , the cards are moved
        else if (tableau[index].isEmpty() && gui.isTableauSelected()) {

            try {
                //If the cards from the original pile can be moved, they are removed and added to a temporary stack
                if (canRemoveCards(gui.getSelectedStack(), gui.getSelectedCardAmount())) {

                    Stack<Card> cardToMove = removeCards(gui.getSelectedStack(), gui.getSelectedCardAmount());
                    addToStack(cardToMove, index);
                }
                System.out.println(selectedCardAmount + " card(s) moved to stack at index " + index + ".");
                gui.unselect();
            }
            catch (EmptyStackException e) {

            }
        }
        //If the card on top of the clicked pile is face down and there is not a card selected, the card is turned over
        else if ((!tableau[index].peek().isFaceUp()) && (!gui.isTableauSelected())) {

            tableau[index].peek().turnUp();
            System.out.println("Card turned up.");
        }
        //If the card on top of the clicked pile is face down and there is a card selected, the selected card is
        // deselected
        else if ((!tableau[index].peek().isFaceUp()) && (gui.isTableauSelected())) {

            gui.unselect();
            System.out.println("Stack has been deselected.");
        }
        //If the pile selected is the one currently selected, the pile is unselected
        else if ((gui.isTableauSelected()) && (gui.getSelectedStack() == index)) {

            gui.unselect();
            System.out.println("Stack " + index + " has been deselected.");
        }
        //If a different pile is selected than the one currently selected
        else if ((gui.isTableauSelected()) && (gui.getSelectedStack() != index)) {

            //If the cards from the original pile can be moved, they are removed and added to a temporary stack
            if (canRemoveCards(gui.getSelectedStack(), gui.getSelectedCardAmount())) {

                Stack<Card> cardToMove = removeCards(gui.getSelectedStack(), gui.getSelectedCardAmount());

                //If the cards to be moved can be added to the destination stack, they are added
                if (canAddToStack(cardToMove, index)) {

                    addToStack(cardToMove, index);
                    gui.unselect();
                    System.out.println(selectedCardAmount + " card(s) moved to stack at index " + index + ".");
                }
                //If the cards cannot be moved, they are returned to their original stack
                else {

                    addToStack(cardToMove, gui.getSelectedStack());
                    gui.unselect();
                    System.out.println("Illegal move.");
                }
            }
        }
        //If the tableau is not selected, the pile clicked becomes the selected pile. Checks are made to ensure that the
        //amount of cards selected is allowed (ie. they run sequentially in rank and are of the same suit)
        else {

            //If there is only 1 card selected, no checks need to be made
            if (selectedCardAmount == 1) {

                gui.setSelectedCardAmount(selectedCardAmount);
                System.out.println(gui.getSelectedCardAmount() + " card(s) selected.");
                gui.selectStack(index);
            }

            else {

                gui.setSelectedCardAmount(selectedCardAmount);
                Stack<Card> temporaryStack = new Stack<>();
                temporaryStack.push(tableau[index].pop());

                for (int i = 1; i < selectedCardAmount; i++) {

                    if ((temporaryStack.peek().getRank() + 1 == tableau[index].peek().getRank()) &&
                            (temporaryStack.peek().getSuit().equals(tableau[index].peek().getSuit()))) {

                        temporaryStack.push(tableau[index].pop());
                    }

                }

                //If the amount of cards popped to the temporary stack is equal to the amount of selected cards then the
                //stack is selected
                if (temporaryStack.size() == selectedCardAmount) {

                    System.out.println(gui.getSelectedCardAmount() + " card(s) selected.");
                    gui.selectStack(index);
                }
                else {

                    System.out.println("You cannot select a card unless all cards on top of it are decreasing by 1 in " +
                            "rank and of the same suit");
                }

                //The cards are then put back onto the tableau index from the temporary stack
                while (temporaryStack.size() > 0) {

                    tableau[index].push(temporaryStack.pop());
                }
            }
        }
        gui.paintImmediately(gui.getBounds());
    }
}

