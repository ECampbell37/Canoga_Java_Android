/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    Log.java
 ************************************************************/

package com.example.canoga_android_elijahc.model;

/**
 * Logging interface for outputting game messages.
 * Implementations handle where and how messages are displayed.
 */
public interface Log {

    /**
     * Outputs a message to the game log.
     * @param message the text to append to the log
     */
    void logMessage(String message);

}
