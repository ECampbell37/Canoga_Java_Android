/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    GameActivity.java
 ************************************************************/

package com.example.canoga_android_elijahc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Configuration;


import java.util.List;
import java.util.ArrayList;


/**
 * Main game screen activity. Implements CanogaView to receive
 * updates from the controller and drive UI accordingly.
 */
public class GameActivity extends AppCompatActivity implements CanogaView {

    // ──────────────────────────────────────────────────────────────
    // onCreate() Method
    // ──────────────────────────────────────────────────────────────

    /**
     * Called when the activity is first created.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Call super.onCreate and set content view.</li>
     *   <li>Bind all UI components (TextViews, Buttons, GridLayouts).</li>
     *   <li>Create controller with boardSize from intent.</li>
     *   <li>If loadFile extra present, resume game; else start new game.</li>
     *   <li>Wire dynamic buttons and save/quit/log buttons.</li>
     *   <li>Call setupBoard, init originalSquares, update displays.</li>
     * </ol>
     *
     * @param savedInstanceState Android state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Bind UI components
        tvHeader = findViewById(R.id.tvHeader);
        tvScore = findViewById(R.id.tvScore);
        tvGameLog = findViewById(R.id.tvGameLog);
        svGameLog = findViewById(R.id.svGameLog);
        glComputerBoard = findViewById(R.id.glComputerBoard);
        glHumanBoard = findViewById(R.id.glHumanBoard);
        btnDynamic1 = findViewById(R.id.dynamicButton1);
        btnDynamic2 = findViewById(R.id.dynamicButton2);
        btnDynamic3 = findViewById(R.id.dynamicButton3);
        btnSave = findViewById(R.id.btnSave);
        btnQuit = findViewById(R.id.btnQuit);
        btnShowLog = findViewById(R.id.btnShowLog);

        // Determine board size and initialize controller
        boardSize = getIntent().getIntExtra("boardSize", DEFAULT_BOARD_SIZE);
        controller = new CanogaController(boardSize, this);


        // Resume or start new game
        String loadFile = getIntent().getStringExtra("loadFile");
        if (loadFile != null && !loadFile.isEmpty()) {
            String absolutePath = getFilesDir().getAbsolutePath() + "/" + loadFile;
            boardSize = controller.resumeGame(loadFile, absolutePath);
        } else {
            displayMessage("Starting new game!");
        }

        // Dynamic button callbacks
        btnDynamic1.setOnClickListener(v -> controller.onDynamicButtonClick(1));
        btnDynamic2.setOnClickListener(v -> controller.onDynamicButtonClick(2));
        btnDynamic3.setOnClickListener(v -> controller.onDynamicButtonClick(3));

        // Save, quit, and show log buttons
        btnSave.setOnClickListener(v -> controller.saveGame(getFilesDir().getAbsolutePath() + "/game.txt"));
        btnQuit.setOnClickListener(v -> goToTournamentResults());
        btnShowLog.setOnClickListener(v -> showGameLogDialog());


        // Prepare board layout and initial data
        setupBoard();
        originalSquares = new ArrayList<>(boardSize);
        for (int i = 0; i < boardSize; i++) {
            originalSquares.add(i + 1);
        }

        updateBoardDisplay();
        updateUI();
    }



    // ──────────────────────────────────────────────────────────────
    // Public CanogaView Implementations
    // ──────────────────────────────────────────────────────────────


    /**
     * Renders the current board state into the two GridLayouts.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Clear all existing buttons from both computer and human board layouts.</li>
     *   <li>For each index i from 0 to boardSize–1:</li>
     *     <ol type="a">
     *       <li>Create a new Button for the computer board.</li>
     *       <li>Get the displayed value and original index value.</li>
     *       <li>Set the button text to the displayed (possibly zero) value.</li>
     *       <li>Determine if the button should be enabled (selectable state).</li>
     *       <li>If enabled, show the original index instead of 0.</li>
     *       <li>Apply a highlight color if selected; otherwise use default.</li>
     *       <li>Attach an onClick listener that delegates selection to the controller.</li>
     *       <li>Add the button to the computer GridLayout.</li>
     *     </ol>
     *   <li>Repeat steps a–h for the human board, using human-specific enable logic and coloring.</li>
     * </ol>
     */
    @Override
    public void updateBoardDisplay() {
        glComputerBoard.removeAllViews();
        glHumanBoard.removeAllViews();

        // --- Computer Board ---
        for (int i = 0; i < boardSize; i++) {
            Button btnComputer = new Button(this);
            int computerSquareValue = controller.getComputerSquareValue(i);
            int originalSquareValue = originalSquares.get(i);

            btnComputer.setText(String.valueOf(computerSquareValue));

            // Allow selection if the square is not already covered (0)
            boolean canSelect = (controller.canUserSelect() &&
                    !controller.getUserCoverChoice() && computerSquareValue == 0);
            btnComputer.setEnabled(canSelect);
            if (canSelect) {
                btnComputer.setText(String.valueOf(originalSquareValue));
            }

            // Change button color if selected.
            if (controller.getSelectedComputerSquares().contains(originalSquareValue)) {
                btnComputer.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            } else {
                btnComputer.setBackgroundResource(android.R.drawable.btn_default);
            }

            btnComputer.setOnClickListener(v -> {
                // Delegate selection to the controller.
                controller.selectComputerSquare(originalSquareValue);
            });
            glComputerBoard.addView(btnComputer);
        }

        // --- Human Board ---
        for (int i = 0; i < boardSize; i++) {
            Button btnHuman = new Button(this);
            int humanSquareValue = controller.getHumanSquareValue(i);
            int originalSquareValue = originalSquares.get(i);

            btnHuman.setText(String.valueOf(humanSquareValue));

            // Allow selection if the square is covered (non-zero)
            boolean canSelect = (controller.canUserSelect() &&
                    controller.getUserCoverChoice() && humanSquareValue != 0);
            btnHuman.setEnabled(canSelect);

            if (controller.getSelectedHumanSquares().contains(originalSquareValue)) {
                btnHuman.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            } else {
                btnHuman.setBackgroundResource(android.R.drawable.btn_default);
            }

            btnHuman.setOnClickListener(v -> {
                // Delegate selection to the controller.
                controller.selectHumanSquare(originalSquareValue);
            });
            glHumanBoard.addView(btnHuman);
        }
    }


    /**
     * Updates the header and dynamic button texts based on the current game state.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Call updateHeader() to refresh score display.</li>
     *   <li>Call updateDynamicButtons() to set labels & enabled flags on the three dynamic buttons.</li>
     * </ol>
     */
    @Override
    public void updateUI() {
        updateHeader();
        updateDynamicButtons();
    }


    /**
     * Appends a message to the in‑game log TextView and scrolls the ScrollView to the bottom.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *     <li>Append a newline plus the message to tvGameLog.</li>
     *     <li>Smooth-scroll svGameLog so the newest text is visible.</li>
     * </ol>
     *
     * @param message text to display in the game log
     */
    @Override
    public void displayMessage(String message) {
        tvGameLog.append("\n" + message);
        svGameLog.post(() -> svGameLog.smoothScrollTo(0, tvGameLog.getBottom()));
    }


    /**
     * Navigates to the TournamentResultsActivity, passing along
     * the final human/computer scores and the full game log.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Create an Intent for TournamentResultsActivity.</li>
     *   <li>Put extras: "humanScore", "computerScore", "gameLog".</li>
     *   <li>Start the new Activity and call finish() on this one.</li>
     * </ol>
     */
    @Override
    public void goToTournamentResults() {
        Intent intent = new Intent(GameActivity.this, TournamentResultsActivity.class);
        intent.putExtra("humanScore", controller.getHumanScore());
        intent.putExtra("computerScore", controller.getComputerScore());
        intent.putExtra("gameLog", tvGameLog.getText().toString());
        startActivity(intent);
        finish();
    }


    /**
     * Prompts the user to select how many manual dice rolls they want.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Build an AlertDialog titled "How many rolls?".</li>
     *   <li>Fill items 1–10 as options.</li>
     *   <li>On selection:</li>
     *     <ol type="a">
     *       <li>Store pendingManualRolls = selected count.</li>
     *       <li>Reset currentRollInput and manualRolls list.</li>
     *       <li>Call promptForManualDiceValues() to begin entering dice values.</li>
     *     </ol>
     *   <li>Show the dialog (non‑cancelable).</li>
     * </ol>
     */
    public void promptForManualRollCount() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("How many rolls?");

        String[] rollOptions = new String[10];
        for (int i = 0; i < 10; i++) {
            rollOptions[i] = String.valueOf(i + 1);
        }

        builder.setItems(rollOptions, (dialog, which) -> {
            pendingManualRolls = which + 1; // 1-based
            currentRollInput = 0;
            manualRolls.clear();
            promptForManualDiceValues();
        });

        builder.setCancelable(false);
        builder.show();
    }


    /**
     * Sets a new board dimension at runtime (9, 10, or 11) and resets the display.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>If size ∈ {9,10,11}:</li>
     *     <ol type="a">
     *       <li>Update boardSize field.</li>
     *       <li>Rebuild originalSquares list [1…size].</li>
     *       <li>Call setupBoard() to reconfigure GridLayouts.</li>
     *       <li>Call updateBoardDisplay() to render empty board.</li>
     *       <li>displayMessage("Board size changed… Start Game!").</li>
     *     </ol>
     * </ol>
     *
     * @param size new board dimension
     */
    public void updateBoardSize(int size) {
        if (size == 9 || size == 10 || size == 11)
        {
            boardSize = size;
            originalSquares.clear();
            for (int i = 0; i < boardSize; i++) {
                originalSquares.add(i + 1);
            }
            setupBoard();
            updateBoardDisplay();
            displayMessage("Board size changed to " + boardSize + ". \nStart Game!");
        }
    }



    // ──────────────────────────────────────────────────────────────
    // Private Methods
    // ──────────────────────────────────────────────────────────────


    /**
     * Configures the two GridLayouts’ column counts based on screen orientation.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Clear both GridLayouts.</li>
     *   <li>Get current orientation.</li>
     *   <li>If landscape → set columnCount = boardSize.</li>
     *   <li>Else (portrait) → set columnCount = 3.</li>
     * </ol>
     */
    private void setupBoard() {
        glComputerBoard.removeAllViews();
        glHumanBoard.removeAllViews();


        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // One long row — column count matches board size
            glComputerBoard.setColumnCount(boardSize);
            glHumanBoard.setColumnCount(boardSize);
        } else {
            // Portrait — 3 columns
            glComputerBoard.setColumnCount(3);
            glHumanBoard.setColumnCount(3);
        }
    }


    /**
     * Updates the top header TextView with the fixed title and current scores.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Set tvHeader text to "Canoga".</li>
     *   <li>Build scoreText = "Human: X | Computer: Y".</li>
     *   <li>Set tvScore to scoreText.</li>
     * </ol>
     */
    private void updateHeader() {
        tvHeader.setText("Canoga");
        String scoreText = "Human: " + controller.getHumanScore() +
                " | Computer: " + controller.getComputerScore();
        tvScore.setText(scoreText);
    }


    /**
     * Configures each of the three dynamic buttons
     * (text, enabled state, visibility) based on controller state.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Make all three buttons visible.</li>
     *   <li>Switch on controller.getCurrentState():</li>
     *     <ol type="a">
     *       <li>For each enum value set btn text & enabled flags.</li>
     *     </ol>
     * </ol>
     */
    private void updateDynamicButtons() {
        btnDynamic1.setVisibility(View.VISIBLE);
        btnDynamic2.setVisibility(View.VISIBLE);
        btnDynamic3.setVisibility(View.VISIBLE);

        // Use the controller's current state to update texts.
        switch (controller.getCurrentState()) {
            case START_GAME:
                btnDynamic1.setText("Start");
                btnDynamic1.setEnabled(true);
                btnDynamic2.setText("");
                btnDynamic2.setEnabled(false);
                btnDynamic3.setText("");
                btnDynamic3.setEnabled(false);
                break;
            case BEGIN_TURN:
                btnDynamic1.setText("Next");
                btnDynamic1.setEnabled(true);
                btnDynamic2.setText("");
                btnDynamic2.setEnabled(false);
                btnDynamic3.setText("");
                btnDynamic3.setEnabled(false);
                break;
            case NUM_DICE:
                btnDynamic1.setText("1 Dice");
                btnDynamic1.setEnabled(true);
                btnDynamic2.setText("2 Dice");
                btnDynamic2.setEnabled(true);
                btnDynamic3.setText("Help");
                btnDynamic3.setEnabled(true);
                break;
            case ROLL_TYPE:
                btnDynamic1.setText("Random");
                btnDynamic1.setEnabled(true);
                btnDynamic2.setText("Manual");
                btnDynamic2.setEnabled(true);
                btnDynamic3.setText("");
                btnDynamic3.setEnabled(false);
                break;
            case WAIT_FOR_INPUT:
                btnDynamic1.setText("Go");
                btnDynamic1.setEnabled(true);
                btnDynamic2.setText("");
                btnDynamic2.setEnabled(false);
                btnDynamic3.setText("");
                btnDynamic3.setEnabled(false);
                break;
            case MOVE_TYPE:
                btnDynamic1.setText("Cover");
                btnDynamic1.setEnabled(true);
                btnDynamic2.setText("Uncover");
                btnDynamic2.setEnabled(true);
                btnDynamic3.setText("Help");
                btnDynamic3.setEnabled(true);
                break;
            case MOVE_SELECTION:
                btnDynamic1.setText("Reset");
                btnDynamic1.setEnabled(true);
                btnDynamic2.setText("Submit");
                btnDynamic2.setEnabled(true);
                btnDynamic3.setText("Help");
                btnDynamic3.setEnabled(true);
                break;
            case ROUND_END:
                btnDynamic1.setText("New Round");
                btnDynamic1.setEnabled(true);
                btnDynamic2.setText("End Game");
                btnDynamic2.setEnabled(true);
                btnDynamic3.setText("");
                btnDynamic3.setEnabled(false);
                break;
            case NEW_BOARDSIZE:
                btnDynamic1.setText("9");
                btnDynamic1.setEnabled(true);
                btnDynamic2.setText("10");
                btnDynamic2.setEnabled(true);
                btnDynamic3.setText("11");
                btnDynamic3.setEnabled(true);
                break;
            default:
                break;
        }
    }


    /**
     * Shows the entire game log in a modal dialog.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Read tvGameLog text.</li>
     *   <li>If not empty → show AlertDialog with that text.</li>
     *   <li>Else → show dialog saying "No game log to display."</li>
     * </ol>
     */
    private void showGameLogDialog() {
        String log = tvGameLog.getText().toString();
        if (!log.trim().isEmpty()) {
            new androidx.appcompat.app.AlertDialog.Builder(GameActivity.this)
                    .setTitle("Game Log")
                    .setMessage(log)
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            new androidx.appcompat.app.AlertDialog.Builder(GameActivity.this)
                    .setTitle("Game Log")
                    .setMessage("No game log to display.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }



    /**
     * Recursively prompts the user for each die value when in manual‐roll mode.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Build AlertDialog asking for first or second die (based on firstDie flag).</li>
     *   <li>On selection:</li>
     *     <ol type="a">
     *       <li>If firstDie == -1 → store as firstDie, recall this method.</li>
     *       <li>Else → compute sum, add to manualRolls, reset firstDie.</li>
     *       <li>If currentRollInput < pendingManualRolls → recurse; else → controller.setManualRolls(manualRolls).</li>
     *     </ol>
     *   <li>Show dialog (non‑cancelable).</li>
     * </ol>
     */
    private void promptForManualDiceValues() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(firstDie == -1 ? "Select first die (Roll " + (currentRollInput + 1) + ")" : "Select second die (Roll " + (currentRollInput + 1) + ")");

        String[] diceOptions = {"1", "2", "3", "4", "5", "6"};

        builder.setItems(diceOptions, (dialog, which) -> {
            int selectedValue = which + 1; // 1-based
            if (firstDie == -1) {
                firstDie = selectedValue;
                promptForManualDiceValues(); // Now ask for second die
            } else {
                int secondDie = selectedValue;
                manualRolls.add(firstDie + secondDie); // Sum the two dice
                firstDie = -1;
                currentRollInput++;
                if (currentRollInput < pendingManualRolls) {
                    promptForManualDiceValues(); // Next roll
                } else {
                    controller.setManualRolls(manualRolls);

                }
            }
        });

        builder.setCancelable(false);
        builder.show();
    }





    // ──────────────────────────────────────────────────────────────
    // Private Members
    // ──────────────────────────────────────────────────────────────

    private static final int DEFAULT_BOARD_SIZE = 9;

    private TextView tvHeader, tvScore, tvGameLog;
    private ScrollView svGameLog;
    private GridLayout glComputerBoard, glHumanBoard;
    private Button btnDynamic1, btnDynamic2, btnDynamic3;
    private Button btnSave, btnQuit, btnShowLog;

    private CanogaController controller;

    private int boardSize = DEFAULT_BOARD_SIZE;
    private List<Integer> originalSquares;

    private ArrayList<Integer> manualRolls = new ArrayList<>();
    private int pendingManualRolls = 0;
    private int currentRollInput = 0;
    private int firstDie = -1;

}
