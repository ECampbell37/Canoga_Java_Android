/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    Board.java
 ************************************************************/

package com.example.canoga_android_elijahc.model;
import java.util.ArrayList;

/**
 * Represents the Canoga game board, tracking covered/uncovered squares
 * for both the human and computer players, and the current turn count.
 */
public class Board {

    // ──────────────────────────────────────────────────────────────
    // Public Constructors
    // ──────────────────────────────────────────────────────────────

    /**
     * Initializes a board of size n, with all squares uncovered.
     * @param n the number of squares per row (e.g., 9, 10, or 11)
     */
    public Board(int n) {
        this.size = n;
        humanSquares = new ArrayList<>(n);
        computerSquares = new ArrayList<>(n);
        resetBoard();
    }


    /**
     * Default constructor, creates a board of size 9.
     */
    public Board() {
        this(9); // default board size of 9
    }


    // ──────────────────────────────────────────────────────────────
    // Public Selectors
    // ──────────────────────────────────────────────────────────────


    /**
     * @return the current board size
     */
    public int getBoardSize() {
        return size;
    }


    /**
     * @return the number of turns that have elapsed on this board
     */
    public int getTurn() {
        return turn;
    }


    /**
     * Recursively computes 1 + 2 + … + last.
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>If last == 0, return 0</li>
     *   <li>Else return last + getMaxScore(last-1)</li>
     * </ol>
     * @param last the highest square value to sum
     * @return the sum of all integers from 1 to last
     */
    public int getMaxScore(int last) {
        if (last == 0)
            return 0;
        else
            return last + getMaxScore(last - 1);
    }


    /**
     * @return a defensive copy of the human player's square list
     */
    public ArrayList<Integer> getHumanSquares() {
        return new ArrayList<>(humanSquares);
    }


    /**
     * @return a defensive copy of the computer player's square list
     */
    public ArrayList<Integer> getComputerSquares() {
        return new ArrayList<>(computerSquares);
    }


    /**
     * @param idx 0-based index
     * @return the value at that index in the human's squares
     */
    public int getHumanSquareAtIndex(int idx) {
        return humanSquares.get(idx);
    }


    /**
     * @param idx 0-based index
     * @return the value at that index in the computer's squares
     */
    public int getComputerSquareAtIndex(int idx) {
        return computerSquares.get(idx);
    }


    // ──────────────────────────────────────────────────────────────
    // Public Mutators
    // ──────────────────────────────────────────────────────────────


    /**
     * Increments the board turn counter by one.
     */
    public void incrementTurn() {
        turn++;
    }

    /**
     * Sets a new board size and resets all squares to uncovered.
     * @param n the new board size
     */
    public void setBoardSize(int n) {
        this.size = n;
        humanSquares = new ArrayList<>(n);
        computerSquares = new ArrayList<>(n);
        resetBoard();
    }


    /**
     * Validates and sets the human player's squares.
     * @param squares new list of squares (must be length == size)
     * @return true if valid and applied; false (and reset) otherwise
     */
    public boolean setHumanSquares(ArrayList<Integer> squares) {
        if (squares.size() != size)
            return false;
        for (Integer s : squares) {
            if (s < 0 || s > size) {
                resetBoard();
                return false;
            }
        }
        humanSquares = new ArrayList<>(squares);
        return true;
    }


    /**
     * Validates and sets the computer player's squares.
     * @param squares new list of squares
     * @return true if valid and applied; false (and reset) otherwise
     */
    public boolean setComputerSquares(ArrayList<Integer> squares) {
        if (squares.size() != size)
            return false;
        for (Integer s : squares) {
            if (s < 0 || s > size) {
                resetBoard();
                return false;
            }
        }
        computerSquares = new ArrayList<>(squares);
        return true;
    }






    // ──────────────────────────────────────────────────────────────
    // Public Methods
    // ──────────────────────────────────────────────────────────────


    /**
     * Resets both players' squares to [1..size] and zeros the turn counter.
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>turn = 0</li>
     *   <li>clear both lists</li>
     *   <li>for i in 1..size: add i to both lists</li>
     * </ol>
     */
    public void resetBoard() {
        turn = 0;
        humanSquares.clear();
        computerSquares.clear();
        for (int i = 0; i < size; i++) {
            humanSquares.add(i + 1);
            computerSquares.add(i + 1);
        }
    }


    /**
     * Checks whether squares 7..size are all covered (zeroed).
     * @param isHuman true to check the human's board; false for computer
     * @return true if size<7 or if all those squares == 0
     */
    public boolean checkUpperSquares(boolean isHuman) {
        if (size < 7)
            return false;
        if (isHuman) {
            for (int i = 6; i < size; i++) {
                if (humanSquares.get(i) != 0)
                    return false;
            }
        } else {
            for (int i = 6; i < size; i++) {
                if (computerSquares.get(i) != 0)
                    return false;
            }
        }
        return true;
    }


    /**
     * Lists the available squares for covering or uncovering.
     * @param isHuman whose board to inspect (true for human, false for computer)
     * @param forCover true to list non-zero (coverable) squares; false for zero (uncoverable)
     * @return 1-based list of square positions matching the criteria
     */
    public ArrayList<Integer> getAvailableSquares(boolean isHuman, boolean forCover) {
        ArrayList<Integer> avail = new ArrayList<>();
        if (isHuman) {
            for (int i = 0; i < size; i++) {
                if (forCover) {
                    if (humanSquares.get(i) != 0)
                        avail.add(i + 1);
                } else {
                    if (humanSquares.get(i) == 0)
                        avail.add(i + 1);
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (forCover) {
                    if (computerSquares.get(i) != 0)
                        avail.add(i + 1);
                } else {
                    if (computerSquares.get(i) == 0)
                        avail.add(i + 1);
                }
            }
        }
        return avail;
    }


    /**
     * Covers (zeros out) the given square if valid.
     * @param isHuman whose board to modify (true for human, false for computer)
     * @param sq the 1-based square to cover
     */
    public void coverSquare(boolean isHuman, int sq) {
        int idx = sq - 1;
        if (idx < 0 || idx >= size)
            return;
        if (isHuman) {
            if (humanSquares.get(idx) == sq) {
                humanSquares.set(idx, 0);
            }
        } else {
            if (computerSquares.get(idx) == sq) {
                computerSquares.set(idx, 0);
            }
        }
    }


    /**
     * Uncovers (restores) the given square if valid.
     * @param isHuman whose board to modify (true for human, false for computer)
     * @param sq the 1-based square to uncover
     */
    public void uncoverSquare(boolean isHuman, int sq) {
        int idx = sq - 1;
        if (idx < 0 || idx >= size)
            return;
        if (isHuman) {
            if (humanSquares.get(idx) == 0) {
                humanSquares.set(idx, sq);
            }
        } else {
            if (computerSquares.get(idx) == 0) {
                computerSquares.set(idx, sq);
            }
        }
    }


    /**
     * @param isHuman whose board to inspect (true for human, false for computer)
     * @return true if every square is covered (==0)
     */
    public boolean allSquaresCovered(boolean isHuman) {
        if (isHuman) {
            for (int i = 0; i < size; i++) {
                if (humanSquares.get(i) != 0)
                    return false;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (computerSquares.get(i) != 0)
                    return false;
            }
        }
        return true;
    }


    /**
     * @param isHuman whose board to inspect
     * @return true if every square is uncovered (non-zero)
     */
    public boolean allSquaresUncovered(boolean isHuman) {
        if (isHuman) {
            for (int i = 0; i < size; i++) {
                if (humanSquares.get(i) == 0)
                    return false;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (computerSquares.get(i) == 0)
                    return false;
            }
        }
        return true;
    }



    // ──────────────────────────────────────────────────────────────
    // Private Members
    // ──────────────────────────────────────────────────────────────

    private int size;
    private ArrayList<Integer> humanSquares;
    private ArrayList<Integer> computerSquares;
    private int turn;


}
