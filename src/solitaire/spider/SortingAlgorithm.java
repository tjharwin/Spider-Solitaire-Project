package solitaire.spider;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;
import java.util.ArrayList;

/**
 * <h1>solitaire.spider.SortingAlgorithm</h1>
 * This is a class for creating a sorting algorithm object that can play the Spider Solitaire game automatically any
 * given number of times. Once the algorithm has finished running, it calculates the win/loss ratio from all the game
 * and presents this as a percentage of games that have been won.
 *
 * @author Thomas Harwin
 * @version 1.0
 * @since 2018-07-06
 */

public class SortingAlgorithm extends Thread {

    private SpiderSolitaire game;
    private Thread sortingAlgorithm;
    private Boolean running;
    private int iterations;
    private int suitMode;
    private int winCount;
    private int lossCount;
    private int sleepTime = 0;

    //These 3 variables are defined to remember the last move the algorithm made and stop the move being unmade hence
    //causing an endless loop
    private int lastOriginIndex = -1;
    private int lastDestinationIndex = -1;
    private int lastCardAmount = -1;
    private Card lastCard;

    public SortingAlgorithm(SpiderSolitaire game) {

        this.game = game;
    }

    public int getIterations() {

        return iterations;
    }

    public int getWinCount() {

        return winCount;
    }

    public int getLossCount() {

        return lossCount;
    }

    public Thread getSortingAlgorithm() {

        return sortingAlgorithm;
    }

    public boolean isRunning() {

        if (running) {
            return true;
        }
        else
            return false;
    }

    public void setIterations(int iterations) {

        this.iterations = iterations;
    }

    public void resetWinCount() {

        this.winCount = 0;
    }

    public void resetLossCount() {

        this.lossCount = 0;
    }

    public void setSleepTime(int sleepTime) {

        this.sleepTime = sleepTime;
    }

    public void startRunning() {

        running = true;
    }

    public void stopRunning() {

        running = false;
    }

    public void start() {

        sortingAlgorithm = new Thread(this);
        sortingAlgorithm.start();
    }

    /**
     * The overridden run method that runs once the algorithm has been executed in the Spider Solitaire main method.
     */

    @Override
    public void run() {

        startRunning();
        resetWinCount();
        resetLossCount();
        while (running) {
            for (int i = 0; i < iterations; i++) {
                suitMode = game.getSuitMode();
                game.dealGame();
                System.out.println("***ALGORITHM BEGINS***");
                System.out.println("No of iterations: " + iterations);
                System.out.println("Suit mode: " + suitMode + " suit(s)");
                System.out.println("Sleep time: " + sleepTime + "ms");

                try {
                    //The algorithm is ran in 6 cycles; each constitutes a deal. The initial game counts as a deal and
                    //there are 5 stock deals
                    for (int j = 5; j >= 0; j--) {
                        //The order in which the moves are tried is as follows: Marriage, Sequence Marriage, Split
                        //Sequence Marriage, Re-arrange Marriage, Reveal Cards and Deal Preparation
                            while (marriage() || sequenceMarriage() || splitSequenceMarriage() ||
                                    rearrangeMarriage() || differentSuitMarriage() || differentSuitSequenceMarriage() ||
                                    revealCards() || prepDeal()) {

                                //If there are 22 cards or less on the tableau and still cards to deal from the stock
                                //pile, a deal is made from the stock pile to avoid a stalemate scenario
                                if (retainCards()) {
                                    game.dealStock();
                                }
                            }

                        //If a deal has already been made after prepDeal() or retainCards(), then another deal does not
                        //need to be made
                        if (j > 0 && game.getStockCard(j - 1) == null) {

                                break;
                        }
                        else {

                            Thread.sleep(sleepTime);
                            game.dealStock();
                        }
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted. Failed to complete algorithm");
                }

                //If all the foundations are filled, the game is won
                if (game.getFoundationCard(7) != null) {
                    winCount++;
                }
                else {
                    lossCount++;
                }
                System.out.println("***ALGORITHM ENDS***");
            }
            game.endSortingAlgorithm();
            stopRunning();
        }
    }

    /**
     * A method that moves one card onto another which is 1 higher in rank and of the same suit. This method starts from
     * the furthest right tableau index and works its way across the stacks from right to left as the stacks on the
     * right have 1 less card on them and will create empty stacks more quickly (which is favourable). This method will
     * only move cards of the same suit onto one another. If there are two or more cards of the same rank that can be
     * moved, the card that is sitting on the smallest amount of sequences/cards out of sequence and the smallest amount
     * of face down cards will be chosen to increase the chances of creating an empty stack.
     *
     * @return Returns true while a marriage can been made between two cards of the same suit
     */

    @SuppressWarnings("Duplicates")
    public boolean marriage() throws InterruptedException {

        System.out.println("**MARRIAGE STARTED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        boolean canMarriage = false;

        //The method will first look for Queens to add to Kings, and will then decrease in rank 1 by 1
        int rankToFind = 12;
        while (rankToFind > 0) {

            try {
                int priorityStack = -1;
                boolean rankFound = false;
                //Starts from the furthest right tableau index
                for (int i = 9; i >= 0; i--) {
                    //A card is remembered if it is the rank to find and not in sequence.
                    if (!game.getTableauStack(i).isEmpty() && game.getTableauStack(i).peek().getRank() == rankToFind &&
                            !isInSequence(i)) {

                        //If it is the first card found with that rank, it becomes the priority stack to make a marriage
                        //from.
                        if (priorityStack == -1) {
                            priorityStack = i;
                            rankFound = true;
                        }
                        //If it is not the first card, the amount of cards in sequence and the amount of face down cards
                        //must be less to prioritise a move that will make an empty stack more likely to appear
                        else {
                            if (countFaceDown(i) <= countFaceDown(priorityStack)) {
                                if (countSequences(i) < countSequences(priorityStack)) {
                                    priorityStack = i;
                                }
                            }

                        }
                    }
                }
                if (rankFound) {
                    boolean marriageMade = false;
                    for (int j = 0; j < 10; j++) {

                        //This loop will not do anything more once a marriage has been made
                        if (!marriageMade) {
                            //If there is another card on the tableau that is 1 higher in rank and the same suit as
                            // the temporary card, the card is moved and the counters are reset so that any newly
                            // turned over card that was previously face down is included in the next iteration
                            if (!game.getTableauStack(j).isEmpty() &&
                                    game.getTableauStack(j).peek().getRank() == rankToFind + 1 &&
                                    game.getTableauStack(j).peek().getSuit().equals(game.getTableauStack(priorityStack).peek().getSuit())) {

                                    moveCards(priorityStack, j, 1);
                                    rankToFind = 13;
                                    priorityStack = -1;
                                    marriageMade = true;
                                    canMarriage = true;
                            }
                        }
                    }
                }
            }
            catch (EmptyStackException e) {
            }
            rankToFind--;
        }
        System.out.println("**MARRIAGE ENDED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        return canMarriage;
    }

    /**
     * A method that moves one sequence of cards onto another which is 1 higher in rank and of the same suit. This
     * method starts from the furthest right tableau index and works its way across the stacks from right to left as the
     * stacks on the right have 1 less card on them and will create empty stacks more quickly (which is favourable).
     * This method will only move cards of the same suit onto one another.
     *
     * @return Returns true while a marriage can been made between two sequences of cards
     */

    public boolean sequenceMarriage() throws InterruptedException {

        System.out.println("**SEQUENCE MARRIAGE STARTED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        boolean canMarriage = false;

        //The method will first look for Queens to add to Kings, and will then decrease in rank 1 by 1
        int rankToFind = 12;
        while (rankToFind > 0) {

            //Starts from the furthest right tableau index
            for (int i = 9; i >= 0; i--) {

                try {

                    //First we check if the origin tableau index is a sequence of one or more cards
                    if (isInSequence(i)) {

                        //A new temporary stack is made and the sequence added
                        Stack<Card> cardsToMove = removeCardSequence(i);
                        int counter = cardsToMove.size();
                        boolean rankFound = false;

                        //If the final card in the sequence is equal to the rank to find,
                        if (cardsToMove.peek().getRank() == rankToFind) {

                            rankFound = true;
                        }
                        //The temporary stack is put back in its original place
                        replaceCardSequence(cardsToMove, i);

                        if (rankFound) {

                            boolean marriageMade = false;
                            for (int j = 0; j < 10; j++) {

                                //This loop will not do anything more once a marriage has been made
                                if (!marriageMade) {
                                    //If there is another card on the tableau that is 1 higher in rank and the same suit
                                    // as the card on top of the temporary stack, the stack is moved and the loops are
                                    // reset so that any newly turned over card that was previously face down is
                                    // included in the next iteration

                                    if (!game.getTableauStack(j).isEmpty() &&
                                            game.getTableauStack(j).peek().getRank() == rankToFind + 1 &&
                                            game.getTableauStack(j).peek().getSuit().equals(game.getTableauStack(i).peek().getSuit()) ) {

                                        moveCards(i, j, counter);
                                        rankToFind = 13;
                                        marriageMade = true;
                                        canMarriage = true;
                                    }
                                }
                            }
                        }
                    }
                }
                catch (EmptyStackException e) {
                }
            }
            rankToFind--;
        }
        System.out.println("**SEQUENCE MARRIAGE FINISHED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        return canMarriage;
    }

    /**
     * A method that finds a sequence or a single card that can be added to another sequence to form a longer sequence
     * of cards. If only some of the cards in a sequence are needed to be moved, this method will split the sequence
     * at the necessary point and move the cards needed to add to the new sequence. Split sequence marriage will only
     * occur if the new sequence to be formed will be longer than the previous sequence before it was split.
     *
     * @return Returns true while a marriage can been made between two sequences of cards that will form a longer
     * sequence
     */

    public boolean splitSequenceMarriage() throws InterruptedException {

        System.out.println("**SPLIT SEQUENCE MARRIAGE STARTED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        boolean canMarriage = false;

        //The method will first look for a sequence on the tableau
        for (int i = 0; i < 10; i++) {

            try {

                if (isInSequence(i)) {

                    //The rank and suit of the top card of the sequence is stored
                    int rankToFind = game.getTableauStack(i).peek().getRank();
                    String suitToFind = game.getTableauStack(i).peek().getSuit();

                    //A second sequence is searched for which has cards of the same suit and a lower rank than the top
                    //card of the first sequence
                    for (int j = 9; j >= 0; j--) {

                        if (isInSequence(j) && game.getTableauStack(j).peek().getRank() < rankToFind &&
                                game.getTableauStack(j).peek().getSuit().equals(suitToFind)) {

                            //The cards which are less than the rank to find in the second sequence are counted and
                            //moved to the first sequence
                            Stack<Card> temporaryStack = new Stack<>();
                            int counter = 0;
                            boolean canSplit = false;

                            //Starting with the first card, the second sequence is popped to a temporary stack and the
                            //cards are counted until we get to the card in the sequence equal in rank to the top card
                            //of the first sequence stack
                            temporaryStack.push(game.getTableauStack(j).pop());
                            counter++;
                            while (!game.getTableauStack(j).isEmpty() &&
                                    game.getTableauStack(j).peek().getRank() < rankToFind &&
                                    game.getTableauStack(j).peek().getRank() == temporaryStack.peek().getRank() + 1 &&
                                    game.getTableauStack(j).peek().getSuit().equals(temporaryStack.peek().getSuit()) &&
                                    game.getTableauStack(j).peek().isFaceUp()) {

                                temporaryStack.push(game.getTableauStack(j).pop());
                                counter++;
                            }
                            //Checks to make sure the bottom card is one less in rank and the same suit
                            if (temporaryStack.peek().getRank() == rankToFind - 1 &&
                                    temporaryStack.peek().getSuit().equals(suitToFind)) {

                                canSplit = true;
                            }
                            replaceCardSequence(temporaryStack, j);
                            //If splitting the second sequence and moving to the first sequence would form a longer
                            //sequence then the move is made
                            if (isStackTaller(j, i, counter) && canSplit) {

                                moveCards(j, i, counter);
                                //Will try the same index again until no more sequences can be moved there
                                canMarriage = true;
                            }
                        }
                    }
                }
            }
            catch (EmptyStackException e) {

            }
        }
        System.out.println("**SPLIT SEQUENCE MARRIAGE FINISHED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        return canMarriage;
    }

    /**
     * A method that uses an empty stack to rearrange the cards from another stack by performing two marriages. The top
     * card/sequence from tableau index i is moved to an empty stack, whilst the newly available card/stack on top of
     * tableau index i is married onto another stack. The original top card from tableau index i is then married to the
     * newly presented cards on tableau index i.
     *
     * @return Returns true while a marriage can been made by moving one or more cards to an empty stack
     */

    public boolean rearrangeMarriage() throws InterruptedException {

        System.out.println("**REARRANGE MARRIAGE STARTED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        boolean canMarriage = false;

        //The method will first look for an empty stack. If one is not found, the method cannot run and returns false
        for (int i = 0; i < 10; i++) {

            if (game.getTableauStack(i).isEmpty()) {

                try {

                    for (int j = 9; j >= 0; j--) {

                        //If there are 2 or more sequences/cards out of sequence
                        if (countSequences(j) >= 2) {

                            Stack<Card> firstStack = removeCardSequence(j);
                            int firstStackSize = firstStack.size();
                            int firstStackRank = firstStack.peek().getRank();

                            Stack<Card> secondStack = removeCardSequence(j);
                            int secondStackSize = secondStack.size();
                            int secondStackRank = secondStack.peek().getRank();
                            String secondStackSuit = secondStack.peek().getSuit();

                            int unavailableCardRank = 0;
                            if (!game.getTableauStack(j).isEmpty()) {
                                unavailableCardRank = game.getTableauStack(j).peek().getRank();
                            }

                            replaceCardSequence(secondStack, j);
                            replaceCardSequence(firstStack, j);


                            if (firstStackRank == unavailableCardRank - 1) {

                                for (int k = 0; k < 10; k++) {

                                    //If a marriage can be made with the second stack
                                    if (game.getTableauStack(k).peek().getRank() == secondStackRank + 1 &&
                                            game.getTableauStack(k).peek().getSuit().equals(secondStackSuit)) {

                                        //Moves first card/sequence to the empty stack
                                        moveCards(j, i, firstStackSize);
                                        //Marries the second card/sequence
                                        moveCards(j, k, secondStackSize);
                                        //Moves the first card/sequence back to it's original stack, keeping the empty
                                        //stack
                                        moveCards(i, j, firstStackSize);
                                        canMarriage = true;
                                    }
                                }
                            }
                        }
                    }
                }
                catch (EmptyStackException e) {
                }
            }
        }
        System.out.println("**REARRANGE MARRIAGE FINISHED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        return canMarriage;
    }

    /**
     * A method that moves one card onto another which is 1 higher in rank and of different suit. This method starts from
     * the furthest right tableau index and works its way across the stacks from right to left as the stacks on the
     * right have 1 less card on them and will create empty stacks more quickly (which is favourable). This method will
     * only move cards of different suits onto each other when a same suit marriage can't be completed.
     *
     * @return Returns true while a marriage can been made between two cards of different suits
     */

    @SuppressWarnings("Duplicates")
    public boolean differentSuitMarriage() throws InterruptedException {

        System.out.println("**DIFFERENT SUIT MARRIAGE STARTED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        boolean canMarriage = false;
            //The method will first look for Queens to add to Kings, and will then decrease in rank 1 by 1
            int rankToFind = 12;
            while (rankToFind > 0 && !marriage() && !sequenceMarriage() && !splitSequenceMarriage() &&
                !rearrangeMarriage()) {

                try {
                        int priorityStack = -1;
                        boolean rankFound = false;
                        //Starts from the furthest right tableau index
                        for (int i = 9; i >= 0; i--) {
                            //A card is remembered if it is the rank to find and not in sequence.
                            if (!game.getTableauStack(i).isEmpty() && game.getTableauStack(i).peek().getRank() == rankToFind &&
                                    !isInSequence(i) && !isInOrder(i)) {

                                //If it is the first card found with that rank, it becomes the priority stack to make a marriage
                                //from.
                                if (priorityStack == -1) {
                                    priorityStack = i;
                                    rankFound = true;
                                }
                                //If it is not the first card, the amount of cards in sequence and the amount of face down cards
                                //must be less to prioritise a move that will make an empty stack more likely to appear
                                else {
                                    if (countFaceDown(i) <= countFaceDown(priorityStack)) {
                                        if (countSequences(i) < countSequences(priorityStack)) {
                                            priorityStack = i;
                                        }
                                    }

                                }
                            }
                        }
                        if (rankFound) {
                            boolean marriageMade = false;
                            for (int j = 0; j < 10; j++) {

                                //This loop will not do anything more once a marriage has been made
                                if (!marriageMade) {
                                    //If there is another card on the tableau that is 1 higher in rank and the same suit as
                                    // the temporary card, the card is moved and the counters are reset so that any newly
                                    // turned over card that was previously face down is included in the next iteration
                                    if (!game.getTableauStack(j).isEmpty() &&
                                            game.getTableauStack(j).peek().getRank() == rankToFind + 1) {

                                        moveCards(priorityStack, j, 1);
                                        rankToFind = 13;
                                        priorityStack = -1;
                                        marriageMade = true;
                                        canMarriage = true;
                                    }
                                }
                            }
                        }
                }
                catch(EmptyStackException e){
                    }
                    rankToFind--;
            }
            System.out.println("** DIFFERENT SUIT MARRIAGE FINISHED**");
            System.out.println("**CARDS IN PLAY**");
            cardsInPlay();
        return canMarriage;
    }

    /**
     * A method that moves one sequence of cards onto another which is 1 higher in rank and of the same suit. This
     * method starts from the furthest right tableau index and works its way across the stacks from right to left as the
     * stacks on the right have 1 less card on them and will create empty stacks more quickly (which is favourable).
     * This method will only move cards of the same suit onto one another.
     *
     * @return Returns true while a marriage can been made between two sequences of cards
     */

    public boolean differentSuitSequenceMarriage() throws InterruptedException {

        System.out.println("**DIFFERENT SUIT SEQUENCE MARRIAGE STARTED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        boolean canMarriage = false;

        //The method will first look for Queens to add to Kings, and will then decrease in rank 1 by 1
        int rankToFind = 12;
        while (rankToFind > 0 && !marriage() && !sequenceMarriage() && !splitSequenceMarriage() &&
                !rearrangeMarriage()) {

            //Starts from the furthest right tableau index
            for (int i = 9; i >= 0; i--) {

                try {

                    //First we check if the origin tableau index is a sequence of one or more cards
                    if (isInSequence(i)) {

                        //A new temporary stack is made and the sequence added
                        Stack<Card> cardsToMove = removeCardSequence(i);
                        int counter = cardsToMove.size();
                        int hiddenCardRank = 0;
                        if (!game.getTableauStack(i).isEmpty()) {
                            hiddenCardRank = game.getTableauStack(i).peek().getRank();
                        }
                        boolean rankFound = false;

                        //If the final card in the sequence is equal to the rank to find,
                        if (cardsToMove.peek().getRank() == rankToFind && hiddenCardRank != rankToFind + 1) {

                            rankFound = true;
                        }
                        //The temporary stack is put back in its original place
                        replaceCardSequence(cardsToMove, i);

                        if (rankFound) {

                            boolean marriageMade = false;
                            for (int j = 0; j < 10; j++) {

                                //This loop will not do anything more once a marriage has been made
                                if (!marriageMade) {
                                    //If there is another card on the tableau that is 1 higher in rank and the same suit
                                    // as the card on top of the temporary stack, the stack is moved and the loops are
                                    // reset so that any newly turned over card that was previously face down is
                                    // included in the next iteration

                                    if (!game.getTableauStack(j).isEmpty() &&
                                            game.getTableauStack(j).peek().getRank() == rankToFind + 1) {

                                        moveCards(i, j, counter);
                                        rankToFind = 13;
                                        marriageMade = true;
                                        canMarriage = true;
                                    }
                                }
                            }
                        }
                    }
                }
                catch (EmptyStackException e) {
                }
            }
            rankToFind--;
        }
        System.out.println("**DIFFERENT SUIT SEQUENCE MARRIAGE FINISHED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        return canMarriage;
    }

    /**
     * A method that will reveal unavailable cards by moving available cards onto empty stacks. This method will
     * prioritise finding cards of higher ranks from stacks that have less sequences/cards out of sequence and less face
     * down cards
     *
     * @return Returns true while an unavailable card can be revealed
     */

    public boolean revealCards() throws InterruptedException {

        System.out.println("**REVEAL CARDS STARTED**");
        boolean canReveal = false;
        //Looks through each tableau index to see if any are empty stacks
        for (int i = 0; i < 10; i++) {

            if (game.getTableauStack(i).isEmpty()) {

                try {
                    //The method will first try and find the smallest accessible stack that has the least amount of face
                    //down cards and at least two sequences
                    int priorityStack = 0;
                    int sequenceCount = 0;
                    int faceDownCount = 0;
                    int sequenceSize = 0;

                    for (int j = 0; j < 10; j++) {

                        if (!game.getTableauStack(j).isEmpty() && !isInOrder(j)) {

                            //The amount of face down cards will then be counted and the amount of sequences / cards
                            //out of sequence. This method will favour a stack with less sequences and then less
                            //face down cards
                            int faceDownCounter = countFaceDown(j);
                            int sequenceCounter = countSequences(j);
                            Stack<Card> temporaryStack = removeCardSequence(j);

                            //If it is not a different suit sequence
                            if (!game.getTableauStack(j).isEmpty() &&
                                    temporaryStack.peek().getRank() != game.getTableauStack(j).peek().getRank() - 1) {
                                //If this is the first iteration
                                if (sequenceSize == 0 && sequenceCounter > 1) {
                                    priorityStack = j;
                                    faceDownCount = faceDownCounter;
                                    sequenceCount = sequenceCounter;
                                    sequenceSize = temporaryStack.size();
                                }
                                //If the amount of sequences/cards out of sequence is lower than the previous best and
                                //if the amount of face down cards in the stack is also lower, the current stack becomes
                                //the new priority stack
                                else if (sequenceCounter <= sequenceCount && sequenceCounter > 1) {
                                    if (faceDownCounter < faceDownCount) {
                                        priorityStack = j;
                                        faceDownCount = faceDownCounter;
                                        sequenceCount = sequenceCounter;
                                        sequenceSize = temporaryStack.size();
                                    }
                                }
                            }
                            replaceCardSequence(temporaryStack, j);
                        }
                        if (j == 9 && sequenceSize > 0) {

                            moveCards(priorityStack, i, sequenceSize);
                            canReveal = true;
                        }
                    }

                }
                catch (EmptyStackException e) {

                }
            }
        }
        System.out.println("**REVEAL CARDS FINISHED**");
        return canReveal;
    }

    /**
     * A method that makes preparations for a stock deal. The stock pile can only be dealt from if there are no empty
     * stacks in the tableau playing area. This method will prioritise finding cards of higher ranks from the right hand
     * side of the playing area that are on top of a face down card to fill the empty stacks prior to a stock deal being
     * made.
     *
     * @return Returns a boolean true if a prepDeal can be made
     *
     */

    public boolean prepDeal() throws InterruptedException {

        System.out.println("**PREP DEAL STARTED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        boolean prepDone = false;

        //Looks through each tableau index to see if any are empty stacks
        for (int i = 0; i < 10; i++) {

            //If there are empty stacks and face down cards still in play, this method will aim to uncover those face
            //down cards by prioritising moving the cards on top of face down cards first
            if (game.getTableauStack(i).isEmpty() && !faceDownUncovered()) {

                try {
                    //This method is done in two iterations. The first prioritises a card that will reveal a face
                    //down card, the second does not
                    for (int j = 0; j < 2; j++) {
                    //The method will first look for Kings (in or out of sequence), and will then descend in rank by
                    //one. Once a move has been made and the empty stack has been filled, the method will search for
                    //any other empty stacks and repeat.
                    int rankToFind = 13;
                    while (rankToFind > 0) {

                            //Starts from the furthest right tableau index
                            for (int k = 9; k >= 0; k--) {

                                //Skips over the empty stack(s)
                                if (!game.getTableauStack(k).isEmpty()) {
                                    //The first iteration looks for cards/sequences on a face down card
                                    //The second iteration looks for cards/sequences not on a face down card
                                    if (onFaceDown(k) && j == 0 || !onFaceDown(k) && j == 1) {
                                        //If the card is in a sequence, the bottom card on the sequence is checked
                                        Stack<Card> temporarySequence = removeCardSequence(k);
                                        int tempRank = temporarySequence.peek().getRank();
                                        int tempSize = temporarySequence.size();
                                        replaceCardSequence(temporarySequence, k);

                                        if (tempRank == rankToFind && game.getTableauStack(k).size() > tempSize &&
                                                tempRank != game.getTableauStack(k).peek().getRank() - 1 ) {

                                            moveCards(k, i, tempSize);
                                            rankToFind = 0;
                                            j = 2;
                                            k = -1;
                                            prepDone = true;
                                        }
                                    }
                                    //If the card is not in a sequence, the rank of the card is checked
                                    else {

                                        if (game.getTableauStack(k).peek().getRank() == rankToFind &&
                                                game.getTableauStack(k).size() > 1 && onFaceDown(k) && j == 0 ||
                                                game.getTableauStack(k).peek().getRank() == rankToFind &&
                                                        game.getTableauStack(k).size() > 1 && !onFaceDown(k) && j == 1) {

                                            //Moves the cards and exits the loop
                                            moveCards(k, i, 1);
                                            rankToFind = 0;
                                            j = 2;
                                            k = -1;
                                            prepDone = true;
                                        }
                                    }
                                }
                            }
                        rankToFind--;
                        }
                    }
                }
                catch (EmptyStackException e) {
                }
            }
            //If there are empty stacks and no face down cards in play, this method will remove one sequence that is
            //sitting on top of another sequence
            else if (game.getTableauStack(i).isEmpty() && faceDownUncovered()) {

                try {
                    //The method will first look for Kings at the bottom of a sequence, and will then descend in rank by
                    //one.
                    int rankToFind = 13;
                    while (rankToFind > 0) {

                        for (int j = 9; j >= 0; j--) {

                            //Skips over the empty stack(s) to find stacks in sequence
                            if (!game.getTableauStack(j).isEmpty() && isInSequence(j)) {

                                Stack<Card> temporarySequence = removeCardSequence(j);
                                int tempRank = temporarySequence.peek().getRank();
                                int tempSize = temporarySequence.size();

                                //If there is a card or a sequence of cards underneath and the bottom card of
                                //the removed sequence is the rank to find and there is no different suit sequence
                                if (!game.getTableauStack(j).isEmpty() && tempRank == rankToFind &&
                                        tempRank != game.getTableauStack(j).peek().getRank() - 1) {
                                    replaceCardSequence(temporarySequence, j);
                                    moveCards(j, i, (tempSize));
                                    rankToFind = 0;
                                    j = -1;
                                    prepDone = true;
                                }
                                else {
                                    replaceCardSequence(temporarySequence, j);
                                }
                            }
                        }
                        rankToFind--;
                    }
                }
                catch (EmptyStackException e) {
                }
            }
            //If there are still empty spaces on the tableau, the method will look to split a high rank sequence near the
            //top (ie. Take a sequence sitting on top of a Queen off a King card)
            if (game.getTableauStack(i).isEmpty()) {

                try {
                    //The method will first look for Kings at the bottom of a sequence, and will then descend in rank by
                    //one.
                    int rankToFind = 13;
                    while (rankToFind > 0) {

                        for (int j = 9; j >= 0; j--) {

                            //Skips over the empty stack(s) to find stacks in sequence
                            if (!game.getTableauStack(j).isEmpty() && isInSequence(j)) {

                                Stack<Card> temporarySequence = removeCardSequence(j);
                                int tempRank = temporarySequence.peek().getRank();
                                int tempSize = temporarySequence.size();
                                replaceCardSequence(temporarySequence, j);

                                if (tempRank == rankToFind) {

                                    //The sequence will be split at one rank below the King (the Queen), meaning the
                                    //amount of cards to be moved decreases by 1
                                    moveCards(j, i, (tempSize - 1));
                                    rankToFind = 0;
                                    j = -1;
                                    prepDone = true;
                                }
                            }
                        }
                        rankToFind--;
                    }
                }
                catch (EmptyStackException e) {
                }
            }
        }
        //A fail-safe is implemented in case the marriage method attempts to undo the prep deal by marrying the cards
        //that have just been split ready for a stock deal
        try {
            boolean canDealStock = true;
            for (int i = 0; i < 10; i++) {
                if (!(faceDownUncovered() && countSequence(i) == game.getTableauStack(i).size()) ||
                        !(faceDownUncovered() && countOrder(i) == game.getTableauStack(i).size())) {
                    canDealStock = false;
                }
            }
            if (canDealStock) {
                game.dealStock();
            }
        }
        catch (EmptyStackException e) {
        }
        System.out.println("**PREP DEAL FINISHED**");
        System.out.println("**CARDS IN PLAY**");
        cardsInPlay();
        return prepDone;
    }

    /**
     * A method that determines whether or not a card is part of a same-suit sequence. The method returns true if the
     * card underneath the top card of a tableau stack is of the same suit as the top card and one higher in rank.
     *
     * @param index The index of the tableau stack
     * @return Returns true if a card is on top
     */

    public boolean isInSequence(int index) {

        boolean isInSequence = false;
        if (game.getTableauStack(index).size() >= 2) {

            Card tempCard = game.getTableauStack(index).pop();

            if (game.getTableauStack(index).peek().getRank() == tempCard.getRank() + 1 &&
                    game.getTableauStack(index).peek().getSuit().equals(tempCard.getSuit())
                    && game.getTableauStack(index).peek().isFaceUp()) {
                isInSequence = true;
            }
            game.getTableauStack(index).push(tempCard);
        }
        return isInSequence;
    }

    /**
     * A method that determines whether or not a card is part of a different-suit sequence. The method returns true if
     * the card underneath the top card of a tableau stack is one higher in rank and of a different suit.
     *
     * @param index The index of the tableau stack
     * @return Returns true if a card is on top
     */

    public boolean isInOrder(int index) {

        boolean isInOrder = false;
        if (game.getTableauStack(index).size() >= 2) {

            Card tempCard = game.getTableauStack(index).pop();

            if (game.getTableauStack(index).peek().getRank() == tempCard.getRank() + 1 &&
                    !game.getTableauStack(index).peek().getSuit().equals(tempCard.getSuit())
                    && game.getTableauStack(index).peek().isFaceUp()) {
                isInOrder = true;
            }
            game.getTableauStack(index).push(tempCard);
        }
        return isInOrder;
    }

    /**
     * A method that removes one or more cards from a tableau stack that are in a same-suit sequence and places them in
     * a temporary stack.
     *
     * @param index The index of the tableau stack where the cards are to be removed from
     * @return A Stack containing one or more cards
     */

    public Stack<Card> removeCardSequence(int index) {

        Stack<Card> temporaryStack = new Stack<>();
        //Moves the first card to the temporary stack
        if (!game.getTableauStack(index).isEmpty()) {
            temporaryStack.push(game.getTableauStack(index).pop());
        }
        //Subsequent cards card then moved if they are in sequence, of the same suit and face up
        while (!game.getTableauStack(index).isEmpty() &&
                game.getTableauStack(index).peek().getRank() == temporaryStack.peek().getRank() + 1 &&
                game.getTableauStack(index).peek().getSuit().equals(temporaryStack.peek().getSuit())
                && game.getTableauStack(index).peek().isFaceUp()) {

            temporaryStack.push(game.getTableauStack(index).pop());
        }

        return temporaryStack;
    }

    /**
     * A method that replaces one or more cards to a tableau stack that they have temporarily been removed from.
     *
     * @param temporaryStack A temporarily removed stack that needs to be replaced
     * @param index The index of the tableau stack where the cards are to be replaced
     */

    public void replaceCardSequence(Stack<Card> temporaryStack, int index) {

        while (!temporaryStack.isEmpty()) {

            game.getTableauStack(index).push(temporaryStack.pop());
        }
    }

    /**
     * A method that counts the amount of cards in a sequence where the cards in the sequence are of the same suit.
     *
     * @param index The index of the tableau stack where the cards are to be counted
     * @return Returns an integer value of the amount of cards counted in a sequence
     */

    public int countSequence(int index) {

        Stack<Card> temporaryStack = removeCardSequence(index);
        int count = temporaryStack.size();
        replaceCardSequence(temporaryStack, index);
        return count;
    }

    /**
     * A method that counts the amount of cards in a sequence where the cards in the sequence are of a different suit.
     *
     * @param index The index of the tableau stack where the cards are to be counted
     * @return Returns an integer value of the amount of cards counted in a sequence
     */

    public int countOrder(int index) {

        int count = 0;
        Stack<Card> temporaryStack = new Stack<>();
        //Moves the first card to the temporary stack
        if (!game.getTableauStack(index).isEmpty()) {
            temporaryStack.push(game.getTableauStack(index).pop());
            count++;
        }
        //Subsequent cards card then moved if they are in sequence, of the same suit and face up
        while (!game.getTableauStack(index).isEmpty() &&
                game.getTableauStack(index).peek().getRank() == temporaryStack.peek().getRank() + 1 &&
                game.getTableauStack(index).peek().isFaceUp()) {

            temporaryStack.push(game.getTableauStack(index).pop());
            count++;
        }
        //Returns the cards after counting
        while (!temporaryStack.isEmpty()) {
            game.getTableauStack(index).push((temporaryStack.pop()));
        }
        return count;
    }

    /**
     * A method that counts the amount of sequences/cards out of sequence on a tableau index. This count does not
     * include face down cards on a tableau index.
     *
     * @param index The index of the tableau stack where the sequences are to be counted
     * @return Returns an integer value of the amount of sequences/cards not in sequence
     */

    public int countSequences(int index) {

        ArrayList<Stack<Card>> sequences = new ArrayList<>();

        while (!game.getTableauStack(index).isEmpty() && game.getTableauStack(index).peek().isFaceUp()) {
            sequences.add(removeCardSequence(index));
        }
        int sequenceCount = sequences.size();

        for (int i = sequences.size(); i >= 1; i--) {
            replaceCardSequence(sequences.remove(i - 1), index);
        }
        return sequenceCount;
    }

    /**
     * A method that determines whether or not there is a longer sequence of cards beneath a stack of cards compared to
     * another sequence of cards prior to moving. If the origin index has a longer sequence of cards of the same suit
     * beneath the stack to be moved, then the move should not be made and the method returns false. If the destination
     * index has a longer sequence of the same suit, the move should be made and the method returns true.
     *
     * @param originIndex      The index of the tableau stack where the cards are to be moved from
     * @param destinationIndex The index of the tableau stack where the cards are to be moved to
     * @param cardsToMove      The amount of cards to be moved
     * @return Returns true if the destination stack has a longer sequence of cards of the same suit
     */

    public boolean isStackTaller(int originIndex, int destinationIndex, int cardsToMove) {

        boolean isStackTaller = true;

        Stack<Card> temporaryStack = new Stack<>();
        int originCounter = 0;
        int destinationCounter = 0;

        //Temporarily removes the cards to be moved so the origin and destination stack can be compared
        for (int i = 0; i < cardsToMove; i++) {

            temporaryStack.push(game.getTableauStack(originIndex).pop());
        }
        //Checks the first card underneath the stack to move from the origin index and if it is in sequence, counts the
        //amount of cards in that sequence
        if (!game.getTableauStack(originIndex).isEmpty() &&
                game.getTableauStack(originIndex).peek().getRank() == temporaryStack.peek().getRank() + 1 &&
                game.getTableauStack(originIndex).peek().getSuit().equals(temporaryStack.peek().getSuit())
                && game.getTableauStack(originIndex).peek().isFaceUp()) {

            originCounter = countSequence(originIndex);
        }
        //Checks the first card underneath from the destination index and if it is in sequence, counts the amount of
        //cards in that sequence
        if (!game.getTableauStack(destinationIndex).isEmpty() &&
                game.getTableauStack(destinationIndex).peek().getRank() == temporaryStack.peek().getRank() + 1 &&
                game.getTableauStack(destinationIndex).peek().getSuit().equals(temporaryStack.peek().getSuit())
                && game.getTableauStack(destinationIndex).peek().isFaceUp()) {

            destinationCounter = countSequence(destinationIndex);
        }
        //Puts the cards to move back onto the origin index
        while (!temporaryStack.isEmpty()) {

            game.getTableauStack(originIndex).push(temporaryStack.pop());
        }

        if (originCounter >= destinationCounter) {
            isStackTaller = false;
        }
        return isStackTaller;
    }

    /**
     * A method that moves one or more cards from one tableau stack to another. The stack which the cards are moving
     * from is the origin stack and the stack the cards are moved to is the destination stack. Prior to a move being
     * made, any face down cards on the tableau playing area are turned face up and once a move has been made, the same
     * is done again.
     *
     * @param originIndex The index on the tableau where the cards are to be moved from
     * @param destinationIndex The index on the tableau where the cards are to be moved to
     * @param cardAmount The amount of cards to be moved if they are in a sequence
     */

    public void moveCards(int originIndex, int destinationIndex, int cardAmount) throws InterruptedException {

        //If the move to be made will reverse the last move that was made and the stock pile has cards on it, a stock
        //deal will be made.
        if (originIndex == lastDestinationIndex && destinationIndex == lastOriginIndex &&
                cardAmount == lastCardAmount && game.getTableauStack(originIndex).peek() == lastCard) {
            prepDeal();
            game.dealStock();
        }
        else {
            try {
                //If either of the cards are not face up, turn them face up
                faceUpCards();
                Thread.sleep(sleepTime);
                //Selects the card to move
                game.stackClicked(originIndex, cardAmount);
                Thread.sleep(sleepTime);
                //Selects the destination stack
                game.stackClicked(destinationIndex, cardAmount);
                //Turns up the face down card after a move has been made
                faceUpCards();
                cardsInPlay();
            } catch (EmptyStackException e) {
            }
            lastOriginIndex = originIndex;
            lastDestinationIndex = destinationIndex;
            lastCardAmount = cardAmount;
            lastCard = game.getTableauStack(destinationIndex).peek();
        }
    }

    /**
     * A method that prevents the tableau from reaching a stalemate where less than 10 cards are present and the stock
     * pile still has cards left to deal.
     * If the tableau has less than 10 cards, they cannot fill all the empty stacks which prevents a stock deal
     * taking place, making the game un-winnable. In order to prevent the amount of cards on the tableau from falling
     * beneath 10, at least 22 cards must stay on the tableau whilst the stock pile is not empty. This is because a
     * marriage between a sequence of 12 and 1 card will cause 13 cards to be removed to a foundation stack. If this
     * marriage is prevented from happening by dealing from the stock when 22 cards are present, a stalemate can be
     * avoided.
     *
     * @return Returns a boolean indicating whether or not a stock deal needs to take place
     *
     */

    public boolean retainCards() throws InterruptedException {

        boolean retainCards = false;
        //If there are 22 cards or less on the tableau and still cards in the stock pile
        if (countTableau() <= 22 && game.getStockCard(0) != null) {
            prepDeal();
            retainCards = true;
        }
        return retainCards;
    }

    /**
     * A method that turns up any face down cards prior to a move being made. Cards are often left face down once a
     * full sequence that was built on top of a face down card has been moved to one of the foundation stacks.
     *
     */

    public void faceUpCards() {

        for (int i = 0; i < 10; i++) {
            if (!game.getTableauStack(i).isEmpty() &&
                    !game.getTableauStack(i).peek().isFaceUp()) {
                game.getTableauStack(i).peek().turnUp();
            }
        }
    }

    /**
     * A method that counts all the cards in the tableau area
     *
     * @return An integer value of all the cards on the tableau playing area
     */

    public int countTableau() {

        int cardCount = 0;
        for (int i = 0; i < 10; i++) {
            cardCount += game.getTableauStack(i).size();
        }
        return cardCount;
    }

    /**
     * A method that counts the amount of face down cards in a tableau stack
     *
     * @return Returns true if one card or a sequence of cards is on top of a face down card
     */

    @SuppressWarnings("Duplicates")
    public int countFaceDown(int index) {

        int faceDownCounter = 0;
        Stack<Card> stack = game.getTableauStack(index);
        Iterator<Card> iterator = stack.iterator();
        while (iterator.hasNext()){
            Card card = iterator.next();
            if (!card.isFaceUp()) {
                faceDownCounter++;
            }
        }
        return faceDownCounter;
    }

    /**
     * A method that removes one card or a sequence of cards to see if the card beneath is facing down or not.
     *
     * @return Returns true if one card or a sequence of cards is on top of a face down card
     */

    public boolean onFaceDown(int index) {

        boolean onFaceDown = false;
        Stack<Card> temporaryStack = removeCardSequence(index);
        if (!game.getTableauStack(index).isEmpty()) {

            onFaceDown = !game.getTableauStack(index).peek().isFaceUp();
        }
        replaceCardSequence(temporaryStack, index);
        return onFaceDown;
    }

    /**
     * A method that checks if all the face down cards on the tableau have been uncovered.
     *
     * @return Returns true if there are no face down cards on the tableau
     */

    public boolean faceDownUncovered() {

        boolean faceDownUncovered = true;
        for (int i = 0; i < 10; i++) {

            if (countFaceDown(i) >= 1) {

                faceDownUncovered = false;
            }
        }
        return faceDownUncovered;
    }

    /**
     * **DEBUGGING METHOD** A method that counts all the cards in play to see if any have gone missing
     *
     */

    public int cardsInPlay() {

        int cardCount = 0;
        for (int i = 0; i < 10; i++) {
            cardCount += game.getTableauStack(i).size();
        }
        for (int i = 0; i < 8; i++) {
            if (game.getFoundationCard(i) != null) {
                cardCount += 13;
            }
        }
        for (int i = 0; i < 5; i++) {
            cardCount += game.getStockStack(i).size();
        }
        System.out.println(cardCount + " cards in play.");
        return cardCount;
    }




}
