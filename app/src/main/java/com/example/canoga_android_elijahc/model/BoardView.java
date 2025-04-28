/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    BoardView.java
 ************************************************************/

package com.example.canoga_android_elijahc.model;


/**
 * Responsible for rendering the current Board state into
 * a text representation and sending it to the Log.
 */
public class BoardView {

    // ──────────────────────────────────────────────────────────────
    // Public Constructors
    // ──────────────────────────────────────────────────────────────

    /**
     * Constructs a BoardView with the given board and log.
     * @param b the Board whose state will be displayed
     * @param l the Log used to output the board text
     */
    public BoardView(Board b, Log l) {
        board = b;
        this.log = l;
    }


    // ──────────────────────────────────────────────────────────────
    // Public Methods
    // ──────────────────────────────────────────────────────────────


    /**
     * Builds and logs a simple ASCII representation of the board.
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Create StringBuilder</li>
     *   <li>Append header</li>
     *   <li>Append \"Computer:\" row and its squares</li>
     *   <li>Append \"Human:\" row and its squares</li>
     *   <li>Append footer</li>
     *   <li>Send the full string to log</li>
     * </ol>
     */
    public void printBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n====== Current Board ======\n");
        sb.append("Computer: ");
        for (int i = 0; i < board.getBoardSize(); i++)
            sb.append(board.getComputerSquareAtIndex(i)).append(" ");
        sb.append("\nHuman: ");
        for (int i = 0; i < board.getBoardSize(); i++)
            sb.append(board.getHumanSquareAtIndex(i)).append(" ");
        sb.append("\n============================\n");
        log.logMessage(sb.toString());
    }


    // ──────────────────────────────────────────────────────────────
    // Private Fields
    // ──────────────────────────────────────────────────────────────

    private Board board;
    private Log log;

}

