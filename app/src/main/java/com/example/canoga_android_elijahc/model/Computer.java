/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    Computer.java
 ************************************************************/

package com.example.canoga_android_elijahc.model;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;


/**
 * The AI opponent in Canoga, extending Player to implement
 * automated dice‐rolling and move‐making logic.
 */
public class Computer extends Player {

    // ──────────────────────────────────────────────────────────────
    // Public Constructors
    // ──────────────────────────────────────────────────────────────


    /**
     * Constructs a Computer linked to the given Board.
     * @param b the Board instance this computer will play on
     */
    public Computer(Board b) {
        super(b);
    }


    // ──────────────────────────────────────────────────────────────
    // Public Methods
    // ──────────────────────────────────────────────────────────────


    /**
     * Rolls dice for the computer, with context‐sensitive logging.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>If upper squares aren’t all covered, log requirement to roll 2 dice.</li>
     *   <li>Else log choice between 1 or 2 dice based on numDice parameter.</li>
     *   <li>Generate random rolls numDice times and sum them.</li>
     *   <li>Log the total rolled and return it.</li>
     * </ol>
     *
     * @param numDice the number of dice to roll (1 or 2)
     * @param log     the Log for outputting messages
     * @return the total of the dice rolled
     */
    @Override
    public int rollDice(int numDice, Log log) {
        if (!board.checkUpperSquares(false)) {
            log.logMessage("\nThe computer must roll 2 dice (since at least one square from 7 to " +
                    board.getBoardSize() + " is uncovered).");
        } else {
            if (numDice == 1) {
                log.logMessage("\nThe computer chooses to roll 1 dice.\nSquares 7 through " +
                        board.getBoardSize() + " are covered, and the sum of their remaining squares to cover is 6 or less");
            } else {
                log.logMessage("\nThe computer chooses to roll 2 dice.\nSquares 7 through " +
                        board.getBoardSize() + " are covered, and the sum of their remaining squares to cover is greater than 6");
            }
        }
        int total = 0;
        Random rand = new Random();
        for (int i = 0; i < numDice; i++) {
            int dice = rand.nextInt(6) + 1;
            total += dice;
        }
        log.logMessage("\nComputer rolled a total of " + total + " using " + numDice + " dice.");
        return total;
    }



    /**
     * Executes the computer's move strategy for a given dice sum.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Log start and print current board.</li>
     *   <li>If no moves available, log and return false.</li>
     *   <li>Check for an instant‐win move; if found, apply and return false.</li>
     *   <li>Decide whether to cover or uncover.</li>
     *   <li>Gather valid moves; if none, log skip and return false.</li>
     *   <li>Log move choice rationale and available moves.</li>
     *   <li>Select best move, apply it, and log the action.</li>
     *   <li>Print updated board and return whether the game continues.</li>
     * </ol>
     *
     * @param move    unused (null for AI)
     * @param diceSum the total rolled this turn
     * @param isCover initial preference to cover or uncover
     * @param log     the Log for outputting messages
     * @return true if the computer’s turn should continue; false otherwise
     */
    @Override
    public boolean makeMove(Set<Integer> move, int diceSum, boolean isCover, Log log) {
        log.logMessage("\nComputer's move. Dice sum: " + diceSum);
        BoardView view = new BoardView(board, log);
        view.printBoard();

        if (checkNoMovesAvailable(false, diceSum)) {
            log.logMessage("\n\nNo available squares to cover or uncover that add up to " + diceSum + ". Turn ended.\n");
            return false;
        }

        ArrayList<Integer> instantWin = getInstantWinMove(false, diceSum);
        if (!instantWin.isEmpty() && board.getTurn() > 1) {
            log.logMessage("Computer found a winning move by " + (getWonByCover() ? "covering" : "uncovering") +
                    " the following square(s): " + instantWin);
            for (Integer s : instantWin) {
                if (getWonByCover())
                    board.coverSquare(false, s);
                else
                    board.uncoverSquare(true, s);
            }
            return false;
        }

        boolean coverOwn = shouldCoverOwnSquares(false, diceSum);
        if (!coverOwn && board.getTurn() <= 1)
            coverOwn = true;

        ArrayList<Integer> available;
        if (coverOwn)
            available = board.getAvailableSquares(false, true);
        else
            available = board.getAvailableSquares(true, false);

        ArrayList<ArrayList<Integer>> validMoves = allValidMoves(available, diceSum);
        if (validMoves.isEmpty()) {
            log.logMessage("No available squares to " + (coverOwn ? "cover" : "uncover") + ". Turn skipped.");
            return false;
        }

        log.logMessage("Computer chooses to " + (coverOwn ? "cover its own squares" : "uncover opponent's squares") + ".");
        log.logMessage("There are more " + (coverOwn ? "covering" : "uncovering") +
                " moves that will lead the computer to victory, so it's the best option.");

        displayValidMoves(validMoves, log);

        ArrayList<Integer> chosen = chooseBestMove(validMoves, coverOwn);

        StringBuilder sb = new StringBuilder();
        sb.append("Computer ").append(coverOwn ? "covered" : "uncovered").append(" squares: ");
        for (Integer s : chosen) {
            if (coverOwn)
                board.coverSquare(false, s);
            else
                board.uncoverSquare(true, s);
            sb.append(s).append(" ");
        }
        log.logMessage(sb.toString());
        if (coverOwn)
            log.logMessage("This move covers the square with the highest individual value possible");
        else
            log.logMessage("This move uncovers as many opponent squares as possible");

        BoardView updatedView = new BoardView(board, log);
        updatedView.printBoard();

        return !checkWin(false);
    }


    // ──────────────────────────────────────────────────────────────
    // Private & Protected Fields
    // ──────────────────────────────────────────────────────────────

    // (inherited from Player)

}
