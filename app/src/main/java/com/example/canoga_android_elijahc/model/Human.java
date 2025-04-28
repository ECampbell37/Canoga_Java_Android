/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    Human.java
 ************************************************************/

package com.example.canoga_android_elijahc.model;
import java.util.Random;
import java.util.Set;


/**
 * Represents the human player in Canoga, extending Player
 * to implement manual dice‐rolling and move validation.
 */
public class Human extends Player {

    // ──────────────────────────────────────────────────────────────
    // Public Constructors
    // ──────────────────────────────────────────────────────────────


    /**
     * Constructs a Human linked to the given Board.
     * @param b the Board instance this human will play on
     */
    public Human(Board b) {
        super(b);
    }


    // ──────────────────────────────────────────────────────────────
    // Public Methods
    // ──────────────────────────────────────────────────────────────


    /**
     * Simulates dice rolls for the human player.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Initialize total = 0.</li>
     *   <li>Repeat numDice times:</li>
     *     <ol type="a">
     *       <li>Generate random 1–6.</li>
     *       <li>Add to total.</li>
     *     </ol>
     *   <li>Log the result and return total.</li>
     * </ol>
     *
     * @param numDice number of dice to roll (1 or 2)
     * @param log     the Log for output messages
     * @return total rolled value
     */
    @Override
    public int rollDice(int numDice, Log log) {
        int total = 0;
        Random rand = new Random();
        for (int i = 0; i < numDice; i++) {
            int dice = rand.nextInt(6) + 1;
            total += dice;
        }
        log.logMessage("\nYou rolled a total of " + total + " using " + numDice + " dice.");
        return total;
    }



    /**
     * Executes the human player's move, validating selection count
     * and sum before applying cover/uncover.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Ensure 1–4 squares selected; if not, log error and return false.</li>
     *   <li>Sum selected values; if intermediate sum exceeds diceSum, log and return false.</li>
     *   <li>If final sum != diceSum, log and return false.</li>
     *   <li>Apply cover or uncover for each square.</li>
     *   <li>Log the action and display updated board.</li>
     *   <li>Return true for successful move.</li>
     * </ol>
     *
     * @param move    set of square values selected
     * @param diceSum target sum to match
     * @param isCover true to cover; false to uncover
     * @param log     the Log for output messages
     * @return true if move applied successfully; false otherwise
     */
    @Override
    public boolean makeMove(Set<Integer> move, int diceSum, boolean isCover, Log log) {
        //Must be between 1-4 squares
        if(move.isEmpty() || move.size() > 4)
        {
            log.logMessage("\nYou must select between 1 and 4 squares that add exactly to " + diceSum + ".");
            return false;
        }

        //Find total of square values
        int total = 0;
        for (Integer sq : move)
        {
            if (total + sq > diceSum) {
                log.logMessage("\nSum: " + total + " + " + sq + " = " + (total + sq)
                        + ", Adding " + sq + " would exceed " + diceSum + ". Try a different move.");
                return false;
            }
            total += sq;
        }

        //Must add up exactly to diceSum
        if(total !=diceSum)
        {
            log.logMessage("\nSum: " + total + " != " + diceSum + ". Try a different move.");
            return false;
        }

        //If valid, make move
        StringBuilder resultSb = new StringBuilder("\nYou ");
        resultSb.append(isCover ? "covered" : "uncovered").append(" squares: ");
        for (Integer sq : move) {
            if (isCover)
                board.coverSquare(true, sq);
            else
                board.uncoverSquare(false, sq);
            resultSb.append(sq).append(" ");
        }
        log.logMessage(resultSb.toString());
        BoardView updatedView = new BoardView(board, log);
        updatedView.printBoard();
        return true;
    }


    // ──────────────────────────────────────────────────────────────
    // Private & Protected Fields
    // ──────────────────────────────────────────────────────────────

    // (inherited from Player)
}
