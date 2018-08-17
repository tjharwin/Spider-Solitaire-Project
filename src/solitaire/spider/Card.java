package solitaire.spider;

import java.net.URL;

/**
 * <h1>solitaire.spider.Card</h1>
 * This is a class for creating card objects. Cards have a rank (1-13), a suit (heart, diamond, spade or club) and are
 * either face up or face down.
 *
 * @author Thomas Harwin
 * @version 1.0
 * @since 2018-06-11
 */

public class Card {

    private final int rank;
    private final String suit;
    private boolean isFaceUp = false;

    public Card(int rank, String suit) {

        if (rank < 1 || rank > 13) {
            throw new IllegalArgumentException("You must specify a valid rank.");
        }

        if (!(suit.equals("h") || suit.equals("d") || suit.equals("s") || suit.equals("c"))) {
            throw new IllegalArgumentException("You must specify a valid suit.");
        }

        this.rank = rank;
        this.suit = suit;
    }

    public int getRank() {

        return rank;
    }

    public String getSuit() {

        return suit;
    }

    public boolean isFaceUp() {

        return isFaceUp;
    }

    /**
     * A method to turn a card over so it is facing upwards
     *
     */

    public void turnUp() {

        isFaceUp = true;
    }

    /**
     * A method to return the directory path of a card so the appropriate image can be displayed
     *
     * @return A String with the file's name and directory path
     */

    public URL getFileName() {

        String filename;

        if (!isFaceUp) {

            filename = "/cards/back.gif";
        }
        else {

            switch (rank) {

                case 1:
                    filename = "/cards/a" + suit + ".gif";
                    break;

                case 10:
                    filename = "/cards/t" + suit + ".gif";
                    break;

                case 11:
                    filename = "/cards/j" + suit + ".gif";
                    break;

                case 12:
                    filename = "/cards/q" + suit + ".gif";
                    break;

                case 13:
                    filename = "/cards/k" + suit + ".gif";
                    break;

                default:
                    filename = "/cards/" + rank + suit + ".gif";
                    break;
            }
        }

        URL card = getClass().getResource(filename);
        return card;
    }
}