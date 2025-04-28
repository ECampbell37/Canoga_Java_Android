/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    TournamentResultsActivity.java
 ************************************************************/

package com.example.canoga_android_elijahc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;


/**
 * Displays the final tournament results, including each player's score,
 * declares the winner or a tie, and provides options to view the game log,
 * return to the main menu, or exit the app.
 */
public class TournamentResultsActivity extends AppCompatActivity {

    /**
     * Initializes the results screen UI and button callbacks.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Call super.onCreate and set the content view.</li>
     *   <li>Bind UI components: tvResults, tvLogContent, buttons.</li>
     *   <li>Retrieve 'gameLog' extra, populate tvLogContent or default text.</li>
     *   <li>Retrieve 'humanScore' and 'computerScore' extras.</li>
     *   <li>Build resultText with scores and winner/tie message.</li>
     *   <li>Set tvResults to resultText.</li>
     *   <li>Attach listeners:
     *     <ul>
     *       <li>Main Menu: start MainMenuActivity, finish this activity.</li>
     *       <li>Exit: call finishAffinity().</li>
     *       <li>Log: show AlertDialog displaying gameLog or "No game log" message.</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param savedInstanceState standard Android state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_results);


        // Bind UI components
        tvResults = findViewById(R.id.tvResults);
        btnMainMenu = findViewById(R.id.btnMainMenu);
        btnExit = findViewById(R.id.btnExit);
        btnLog = findViewById(R.id.btnLog);
        tvLogContent = findViewById(R.id.tvLogContent);

        // Retrieve and display game log
        String gameLog = getIntent().getStringExtra("gameLog");
        if (gameLog != null) {
            tvLogContent.setText(gameLog);
        } else {
            tvLogContent.setText("No game log available.");
        }

        // Retrieve scores and determine winner
        int humanScore = getIntent().getIntExtra("humanScore", 0);
        int computerScore = getIntent().getIntExtra("computerScore", 0);
        String resultText = "Tournament Results:\nHuman: " + humanScore + "\nComputer: " + computerScore + "\n";
        if (humanScore > computerScore) {
            resultText += "Human wins the tournament!";
        } else if (humanScore < computerScore) {
            resultText += "Computer wins the tournament!";
        } else {
            resultText += "Tournament is a tie!";
        }
        tvResults.setText(resultText);


        // Main Menu button
        btnMainMenu.setOnClickListener(v -> {
            Intent intent = new Intent(TournamentResultsActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        });

        // Exit button
        btnExit.setOnClickListener(v -> finishAffinity());

        // View Log button
        btnLog.setOnClickListener(v -> {
            String log = tvLogContent.getText().toString();
            if (!log.equals("No game log available.")) {
                new AlertDialog.Builder(TournamentResultsActivity.this)
                        .setTitle("Game Log")
                        .setMessage(log)
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                new AlertDialog.Builder(TournamentResultsActivity.this)
                        .setTitle("Game Log")
                        .setMessage("No game log to display.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }


    // ──────────────────────────────────────────────────────────────
    // Private Members
    // ──────────────────────────────────────────────────────────────

    private TextView tvResults;
    private Button btnMainMenu, btnExit, btnLog;
    private TextView tvLogContent;

}