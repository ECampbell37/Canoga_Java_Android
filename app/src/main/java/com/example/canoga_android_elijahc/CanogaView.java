/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    CanogaView.java
 ************************************************************/

package com.example.canoga_android_elijahc;


/**
 * Interface defining the methods the View (Activity) must implement
 * to interact with the CanogaController.
 */
public interface CanogaView {

    /**
     * Refreshes the visual representation of the game board
     * (both human and computer rows).
     */
    void updateBoardDisplay();


    /**
     * Updates UI elements (buttons, header) based on the current game state.
     */
    void updateUI();


    /**
     * Appends a message to the in‑game log display.
     *
     * @param message the text to show in the log
     */
    void displayMessage(String message);


    /**
     * Navigates to the tournament results screen, passing final scores.
     */
    void goToTournamentResults();


    /**
     * Prompts the user to enter how many manual dice rolls they wish to input.
     */
    void promptForManualRollCount();


    /**
     * Updates the board size UI, reconfiguring layouts for the new size.
     *
     * @param size the new board dimension (9, 10, or 11)
     */
    void updateBoardSize(int size);
}
