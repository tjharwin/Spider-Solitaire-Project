package solitaire.spider;

import java.net.URL;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * <h1>solitaire.spider.GUI</h1>
 * This is a class for creating the GUI event-driven program for Spider Solitaire. The display features a row of 10
 * Tableau piles across the top of the window frame, 8 foundation piles along the bottom left and a stock pile in the
 * bottom right.
 *
 * @author Thomas Harwin
 * @version 1.0
 * @since 2018-06-11
 */

public class GUI extends JComponent implements MouseListener {

    private JFrame frame;
    private final ImageIcon icon;

    private static final int CARD_WIDTH = 73;
    private static final int CARD_HEIGHT = 97;
    private static final int SPACING = 5;
    private static final int FACE_UP_OFFSET = 18;
    private static final int FACE_DOWN_OFFSET = 5;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private int selectedCardAmount = 0;
    private SpiderSolitaire game;
    private Boolean mouseEnabled = true;

    public GUI(SpiderSolitaire game) {

        this.game = game;

        frame = new JFrame("Tom Harwin's Spider Solitaire");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.setResizable(false);

        this.setPreferredSize(new Dimension(CARD_WIDTH * 10 + SPACING * 11, CARD_HEIGHT * 5 + SPACING * 3
                + FACE_DOWN_OFFSET * 7 + 13 * FACE_UP_OFFSET));
        this.addMouseListener(this);

        createMenuBar();

        //Creates the program image icon
        URL iconUrl = getClass().getResource("/iconsmall.png");
        Image img = new ImageIcon(iconUrl).getImage();
        icon = new ImageIcon(img);
        frame.setIconImage(img);

        frame.pack();
        frame.setVisible(true);
        selectFunction();

    }

    /**
     * A method for creating the menu bar
     *
     */

    public void createMenuBar() {

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem item1 = new JMenuItem("New Game");
        JMenuItem item2 = new JMenuItem("Run Simulation");
        JMenuItem item3 = new JMenuItem("Stop Simulation");
        JMenuItem item4 = new JMenuItem("Exit");
        menuBar.add(menu);
        menu.add(item1);
        menu.add(item2);
        menu.add(item3);
        menu.add(item4);
        frame.setJMenuBar(menuBar);
        menuBar.setVisible(true);
        menuBar.setSize(400, 200);

        //Opens dialogue for user to choose which game they want to play
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                selectMode();
            }
        });

        //Opens a dialogue for user to choose their simulation options
        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                selectSimulation();
            }
        });

        //Stops the algorithm
        item3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                game.pauseSortingAlgorithm();
            }
        });

        //Exits the program
        item4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.exit(0);
            }
        });

    }

    /**
     * A method that opens a dialogue box for the user to select either the game or the simulation
     *
     */

    public void selectFunction() {

        final JOptionPane selectFunction = new JOptionPane();
        String[] options = {"Play Game", "Run Simulation"};
        int option = selectFunction.showOptionDialog(frame, "Welcome to Tom Harwin's Spider Solitaire! \n" +
                        "Please select an option:\n",
                "Select Function",
                selectFunction.DEFAULT_OPTION, selectFunction.INFORMATION_MESSAGE, icon, options, options[0]);

        switch(option) {

            case 0:
                selectMode();
                System.out.println("Play Game Selected");
                break;

            case 1:
                selectSimulation();
                System.out.println("Run Simulation Selected");
                break;
        }
    }

    /**
     * A method that opens a dialogue box for the user to select the game
     *
     */

    public void selectMode() {

        final JOptionPane selectMode = new JOptionPane();
        String[] options = {"1 suit", "2 suits", "4 suits"};
        int option = selectMode.showOptionDialog(frame, "Select a game mode",
                "New Game",
                selectMode.DEFAULT_OPTION, selectMode.INFORMATION_MESSAGE, icon, options, options[0]);

        switch(option) {

            case 0:
                game.setSuitMode(1);
                System.out.println("1 suit mode selected.");
                break;

            case 1:
                game.setSuitMode(2);
                System.out.println("2 suit mode selected.");
                break;

            case 2:
                game.setSuitMode(4);
                System.out.println("4 suit mode selected.");
                break;
        }

        game.dealGame();
        unselect();
        repaint();
        enableMouse();
    }

    /**
     * A method that opens a dialogue box for the user to input options for the simulation
     *
     */

    public void selectSimulation() {

        JPanel selectSimulation = new JPanel();
        JLabel iterationsLabel = new JLabel("Number of iterations");
        SpinnerNumberModel spinner = new SpinnerNumberModel(1, 0, 100, 1);
        JSpinner iterations = new JSpinner(spinner);
        JRadioButton setSuitMode1 = new JRadioButton("1 Suit");
        setSuitMode1.setActionCommand("1 Suit");
        setSuitMode1.setSelected(true);
        JRadioButton setSuitMode2 = new JRadioButton("2 Suits");
        setSuitMode2.setActionCommand("2 Suits");
        JRadioButton setSuitMode4 = new JRadioButton("4 Suits");
        setSuitMode2.setActionCommand("4 Suits");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(setSuitMode1);
        buttonGroup.add(setSuitMode2);
        buttonGroup.add(setSuitMode4);
        JCheckBox showSimulation = new JCheckBox("Show Simulation");
        selectSimulation.add(iterationsLabel);
        selectSimulation.add(iterations);
        selectSimulation.add(setSuitMode1);
        selectSimulation.add(setSuitMode2);
        selectSimulation.add(setSuitMode4);
        selectSimulation.add(showSimulation);
        selectSimulation.setLayout(new BoxLayout(selectSimulation, BoxLayout.PAGE_AXIS));

        int result = JOptionPane.showConfirmDialog(frame, selectSimulation,
                "Select Simulation Options", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {

            game.getSortingAlgorithm().setIterations((Integer) iterations.getValue());
            if (setSuitMode1.isSelected()) game.setSuitMode(1);
            if (setSuitMode2.isSelected()) game.setSuitMode(2);
            if (setSuitMode4.isSelected()) game.setSuitMode(4);
            if (showSimulation.isSelected()) game.getSortingAlgorithm().setSleepTime(1500);
            else game.getSortingAlgorithm().setSleepTime(50);

            unselect();
            repaint();
            game.startSortingAlgorithm();
            disableMouse();
        }
    }

    /**
     * A method that opens a dialogue box once the game is complete, congratulating the player on winning the game and
     * asking if they wish to play again.
     *
     */

    public void gameComplete() {

        final JOptionPane playAgain = new JOptionPane();
        String[] options = {"Yes", "No"};
        int option = playAgain.showOptionDialog(frame, "Congratulations! You won!\n"
                        + "Would you like to play again?", "Tom Harwin's Spider Solitaire",
                playAgain.DEFAULT_OPTION, playAgain.INFORMATION_MESSAGE, icon, options, options[0]);

        switch(option) {

            case 0:
                selectMode();
                break;

            case 1:
                break;
        }
    }

    /**
     * A method that opens a dialogue box once the sorting algorithm has finished running and lists the statistics
     * gathered from the algorithm before asking the user if they wish to run the algorithm again.
     *
     */

    public void simulationStatistics() {

        final JOptionPane statistics = new JOptionPane();
        float winPercentage = (game.getSortingAlgorithm().getWinCount() * 100f / game.getSortingAlgorithm().getIterations());
        String[] options = {"Yes", "No"};
        int option = statistics.showOptionDialog(frame, "Algorithm ran for "
                        + game.getSortingAlgorithm().getIterations() + " iteration(s).\n" +
                "Wins: " + game.getSortingAlgorithm().getWinCount() + "\n" +
                "Losses: " + game.getSortingAlgorithm().getLossCount() + "\n" +
                "Win percentage: " + winPercentage + "%\n" +
                        "Would you like to run the algorithm again?", "Tom Harwin's Spider Solitaire",
                statistics.DEFAULT_OPTION, statistics.INFORMATION_MESSAGE, icon, options, options[0]);

        switch(option) {

            case 0:
                selectSimulation();
                break;

            case 1:
                break;
        }
    }

    /**
     * A method that indicates which tableau stack is currently selected
     *
     * @return An integer value representing which tableau index is currently selected
     *
     */

    public int getSelectedStack() {

        if (selectedRow == 1)
            return selectedCol;
        else
            return -1;
    }

    /**
     * A method that indicates if the tableau is currently selected
     *
     * @return Returns true if the tableau is selected and false if not
     *
     */

    public boolean isTableauSelected() {

        return selectedRow == 1;
    }

    /**
     * A method for returning the amount of selected cards in a stack.
     *
     * @return An integer value of the amount of selected cards
     *
     */

    public int getSelectedCardAmount() {

        return selectedCardAmount;
    }

    /**
     * A method for setting the amount of selected cards in a stack.
     *
     * @return An integer value of the amount of selected cards
     *
     */

    public void setSelectedCardAmount(int selectedCardAmount) {

        this.selectedCardAmount = selectedCardAmount;
    }

    /**
     * A method that is called once a stack has been selected to change the global variables which stack is currently
     * selected
     *
     * @param column The column that corresponds to the tableau index
     *
     */

    public void selectStack(int column) {

        selectedRow = 1;
        selectedCol = column;
    }

    /**
     * A method that deselects the playing area
     *
     */

    public void unselect() {

        selectedRow = -1;
        selectedCol = -1;
        selectedCardAmount = 0;
    }

    /**
     * A method that enables the mouse
     *
     */

    public void enableMouse() {

        mouseEnabled = true;
    }

    /**
     * A method that disables the mouse
     *
     */

    public void disableMouse() {

        mouseEnabled = false;
    }

    /**
     * A method that overrides the paintComponent method for painting the content frame and positioning the cards
     * inside.
     *
     */
    @Override
    public void paintComponent(Graphics graphics) {

        //Creates the background
        URL backgroundURL = getClass().getResource("/table.jpg");
        ImagePanel background = new ImagePanel(new ImageIcon(backgroundURL).getImage());
        frame.getContentPane().add(background);
        frame.pack();
        frame.setVisible(true);

        //Create tableau display
        for (int i = 0; i < 10; i++){

            Stack<Card> stack = game.getTableauStack(i);
            int offset = 0;
            for (int j = 0; j < stack.size(); j++) {

                try {
                drawCard(graphics, stack.get(j), SPACING + (CARD_WIDTH + SPACING) * i, 2 * SPACING + offset);
                if (selectedRow == 1 && selectedCol == i && j == stack.size() - 1 && selectedCardAmount >= 1)
                    drawBorder(graphics, SPACING + (CARD_WIDTH + SPACING) * i,
                            2 * SPACING + offset - (FACE_UP_OFFSET * (selectedCardAmount - 1)));

                    if (stack.get(j).isFaceUp())
                        offset += FACE_UP_OFFSET;
                    else
                        offset += FACE_DOWN_OFFSET;
                }
                catch (ArrayIndexOutOfBoundsException e) {

                }
            }
        }

        //Create stock display
        for (int i = 0; i < 5; i++) {

            Stack<Card> pile = game.getStockStack(i);

            for (int j = 0; j < pile.size(); j++) {

                drawCard(graphics, pile.get(j), SPACING + (CARD_WIDTH + SPACING) * 8 - (i * SPACING),
                        (SPACING * 4) + (CARD_HEIGHT * 5));
            }
        }

        //Create foundation display
        for (int i = 0; i < 8; i++) {

            drawCard(graphics, game.getFoundationCard(i), SPACING * (1 + i) + CARD_WIDTH * (1 + i),
                    CARD_HEIGHT * 6 + SPACING * 10);

        }
    }

    /**
     * A method that draws card objects using the card image files in the directory path. If a card doesn't exist, a
     * black border is drawn instead.
     *
     */

    private void drawCard(Graphics graphics, Card card, int x, int y) throws NullPointerException
    {
        if (card == null) {

            graphics.setColor(Color.BLACK);
            graphics.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
        }
        else {
            URL fileName = card.getFileName();
            Image image = new ImageIcon(fileName).getImage();
            graphics.drawImage(image, x, y, CARD_WIDTH, CARD_HEIGHT, null);
        }
    }

    /**
     * A method that draws a yellow border around the selected card(s)
     *
     */

    private void drawBorder(Graphics graphics, int x, int y)
    {
        graphics.setColor(Color.YELLOW);
        graphics.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT + (FACE_UP_OFFSET * (selectedCardAmount - 1)));
        graphics.drawRect(x + 1, y + 1, CARD_WIDTH - 2,
                CARD_HEIGHT + (FACE_UP_OFFSET * (selectedCardAmount - 1)) - 2);
        graphics.drawRect(x + 2, y + 2, CARD_WIDTH - 4,
                CARD_HEIGHT + (FACE_UP_OFFSET * (selectedCardAmount - 1)) - 4);
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {

        if (mouseEnabled) {
            //Initially no card is selected
            int col = e.getX() / (SPACING + CARD_WIDTH);
            int row = e.getY() / (SPACING + CARD_HEIGHT);
            int height = e.getY();

            //If the stock pile is clicked, deal from the stock pile
            if ((col == 8) && (row == 5)) {

                game.dealStock();
                unselect();
            }

            //If a blank area is clicked or one of the foundation stacks, unselect
            else if ((getStackHeight(col) < height) && (row < 4)) {

                System.out.print("Unselected");
                unselect();
            }
            //If the tableau area is clicked and the tableau is not currently selected, calculate the amount of selected
            //cards and click on the stack
            else if (getStackHeight(col) > height && (isTableauSelected() == false)) {

                caculateSelectedCards(col, height);
                game.stackClicked(col, selectedCardAmount);
            }
            //If the tableau area is clicked and the tableau is currently selected, click on the stack without
            //calculating the amount of selected cards
            else if (getStackHeight(col) > height && isTableauSelected()) {

                game.stackClicked(col, selectedCardAmount);
            }
        }
        else {
            System.out.println("Mouse is disabled whilst algorithm is running");
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }




    /**
     * A method that calculates the amount of cards that have been selected in a stack. Multiple cards can be moved at
     * a time from one stack to another and so this method determines where the user has clicked in the stack and how
     * many cards are beneath and included at the selected point of the stack.
     *
     * @param column The column that corresponds to the tableau index
     * @param height The y coordinate of where the mouse is clicked from the action listener
     * @return An integer value of the amount of cards that have been selected dependent on where the mouse is clicked
     *
     */

    public int caculateSelectedCards(int column, int height) {

        int cardsSelected = 0;
        int faceDownCounter = getFaceDownCards(column);
        int faceUpCounter = getFaceUpCards(column);
        int cardAmount = game.getTableauStack(column).size();
        int stackHeight = getStackHeight(column);

        //If the card on the top of the tableau stack is selected
        if (height <= stackHeight && height >= ((2 * SPACING) + (faceDownCounter * FACE_DOWN_OFFSET) +
                ((faceUpCounter - 1) * FACE_UP_OFFSET))) {
            cardsSelected = 1;
        }
        //If a card in between the top card and the face down cards is selected
        else if ((height <= (stackHeight - CARD_HEIGHT)) &&
                (height >= ((2 * SPACING) + (faceDownCounter * FACE_DOWN_OFFSET)))) {

            //Selects the cards between the face down cards and the card on the top of the stack
            cardAmount = cardAmount - faceDownCounter - 1;
            //Removes the card on the top of the stack from the stack height
            stackHeight = stackHeight - CARD_HEIGHT;
            for (int i = 0; i < cardAmount; i++) {
                if (((stackHeight - (FACE_UP_OFFSET * i)) >= height ) &&
                        ( height >= (stackHeight - (FACE_UP_OFFSET * (i + 1))))) {
                    cardsSelected = i + 2;
                }
            }
        }

        selectedCardAmount = cardsSelected;
        return cardsSelected;
    }

    /**
     * A method that calculates the height of a tableau stack in pixels by adding up all the cards and spacing used.
     *
     * @param column The column that corresponds to the tableau index
     * @return An integer value of the height of a stack in pixels
     *
     */

    public int getStackHeight(int column) {

        int faceDownCounter = getFaceDownCards(column);
        int faceUpCounter = getFaceUpCards(column);

        return (2 * SPACING) + (faceDownCounter * FACE_DOWN_OFFSET) + ((faceUpCounter - 1) * FACE_UP_OFFSET)
                + (CARD_HEIGHT);
    }

    /**
     * A method that iterates through a stack and returns the number of face down cards in a stack.
     *
     * @param column The column that corresponds to the tableau index
     * @return An integer value of the amount of face down cards
     *
     */
    @SuppressWarnings("Duplicates")
    public int getFaceDownCards(int column) {

        int faceDownCounter = 0;
        Stack<Card> stack = game.getTableauStack(column);
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
     * A method that iterates through a stack and returns the number of face up cards in a stack.
     *
     * @param column The column that corresponds to the tableau index
     * @return An integer value of the amount of face up cards
     *
     */

    public int getFaceUpCards(int column) {

        int faceUpCounter = 0;
        Stack<Card> stack = game.getTableauStack(column);
        Iterator<Card> iterator = stack.iterator();
        while (iterator.hasNext()){
            Card card = iterator.next();
            if (card.isFaceUp()) {
                faceUpCounter++;
            }
        }
        return faceUpCounter;
    }
}
