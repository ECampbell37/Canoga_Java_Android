/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    BoardSizeActivity.java
 ************************************************************/

package com.example.canoga_android_elijahc;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Activity that prompts the user to select a board size (9,10,11)
 * before starting a new Canoga game.
 */
public class BoardSizeActivity extends AppCompatActivity {

    /**
     * Sets up the UI and button callbacks.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Call super.onCreate and set content view.</li>
     *   <li>Attach click listeners for:
     *     <ul>
     *       <li>btnSize9   → launchGameActivity(9)</li>
     *       <li>btnSize10  → launchGameActivity(10)</li>
     *       <li>btnSize11  → launchGameActivity(11)</li>
     *       <li>btnBack    → finish()</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param savedInstanceState standard Android state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_size);

        findViewById(R.id.btnSize9).setOnClickListener(v -> launchGameActivity(9));
        findViewById(R.id.btnSize10).setOnClickListener(v -> launchGameActivity(10));
        findViewById(R.id.btnSize11).setOnClickListener(v -> launchGameActivity(11));
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }


    /**
     * Starts GameActivity with the chosen board size.
     *
     * @param boardSize number of squares per row (9,10,11)
     */
    private void launchGameActivity(int boardSize) {
        Intent intent = new Intent(BoardSizeActivity.this, GameActivity.class);
        intent.putExtra("boardSize", boardSize);
        startActivity(intent);
    }
}
