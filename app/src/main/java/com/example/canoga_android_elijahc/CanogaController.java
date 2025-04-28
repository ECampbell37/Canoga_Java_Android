/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    CanogaController.java
 ************************************************************/

package com.example.canoga_android_elijahc;

import com.example.canoga_android_elijahc.model.CanogaModel;
import com.example.canoga_android_elijahc.model.Log;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;


/**
 * Controller in the MVC architecture.  Implements Log to receive
 * game messages and drives the UI (CanogaView) based on GameState.
 */
public class CanogaController implements Log {

    // ──────────────────────────────────────────────────────────────
    // Public Enum Data type
    // ──────────────────────────────────────────────────────────────

    /**
     * All possible UI/game states for the controller’s state machine.
     */
    public enum GameState {
        START_GAME,
        BEGIN_TURN,
        NUM_DICE,
        ROLL_TYPE,
        WAIT_FOR_INPUT,
        MOVE_TYPE,
        MOVE_SELECTION,
        ROUND_END,
        NEW_BOARDSIZE
    }


    // ──────────────────────────────────────────────────────────────
    // Public Constructors
    // ──────────────────────────────────────────────────────────────


    /**
     * Creates the controller, wiring the model to the view.
     * @param boardSize initial board size (9,10,11)
     * @param view      UI implementation to drive
     */
    public CanogaController(int boardSize, CanogaView view) {
        this.view = view;
        model = new CanogaModel(boardSize, this);
    }



    // ──────────────────────────────────────────────────────────────
    // Public Selectors
    // ──────────────────────────────────────────────────────────────

    /** @return the most recently recorded user dice sum */
    public int getUserDiceSum() {
        return this.userDiceSum;
    }

    /** @return the number of dice the user chose (1 or 2) */
    public int getUserNumDice() {
        return userNumDice;
    }

    /** @return the current GameState in the UI flow */
    public GameState getCurrentState() {
        return currentState;
    }

    /** @return true if the human’s last choice was COVER, false for UNCOVER */
    public boolean getUserCoverChoice() {
        return userCoverChoice;
    }

    /** @return true if in MOVE_SELECTION, enabling board taps */
    public boolean canUserSelect()
    {
        return currentState == GameState.MOVE_SELECTION;
    }


    /** @return a fresh Set of selected human squares */
    public Set<Integer> getSelectedHumanSquares() {
        return new HashSet<>(selectedHumanSquares);
    }


    /** @return a fresh Set of selected computer squares */
    public Set<Integer> getSelectedComputerSquares() {
        return new HashSet<>(selectedComputerSquares);
    }

    /** @return current human cumulative score */
    public int getHumanScore() {
        return model.getHuman().getTournamentScore();

    }

    /** @return current computer cumulative score */
    public int getComputerScore() {
        return model.getComputer().getTournamentScore();
    }


    /**
     * Retrieves the displayed value of a human’s board square.
     * @param i 0‑based index
     * @return the square value or ‑1 if invalid index
     */
    public int getHumanSquareValue(int i) {
        if (i < 0 || i >= model.getBoard().getBoardSize())
        {
            return -1;
        }
        return model.getBoard().getHumanSquares().get(i);
    }


    /**
     * Retrieves the displayed value of a computer’s board square.
     * @param i 0‑based index
     * @return the square value or ‑1 if invalid index
     */
    public int getComputerSquareValue(int i) {
        if (i < 0 || i >= model.getBoard().getBoardSize())
        {
            return -1;
        }
        return model.getBoard().getComputerSquares().get(i);
    }



    // ──────────────────────────────────────────────────────────────
    // Public Mutators
    // ──────────────────────────────────────────────────────────────


    /**
     * Supplies manual dice sums and resumes WAIT_FOR_INPUT.
     * @param rolls list of pre‑chosen dice totals
     */
    public void setManualRolls(ArrayList<Integer> rolls) {
        this.userManualRolls = rolls;
        this.waitingForInput = false;
    }


    /**
     * Records the last dice sum rolled by the human.
     * @param sum the total of the dice roll
     */
    public void setUserDiceSum(int sum) {
        this.userDiceSum = sum;
    }




    // ──────────────────────────────────────────────────────────────
    // Public Methods
    // ──────────────────────────────────────────────────────────────



    /**
     * Resumes a saved game.  Loads model state, updates UI state.
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Log resume attempt.</li>
     *   <li>Call model.loadGame(path).</li>
     *   <li>If success, log previous/next player, set state to BEGIN_TURN, return board size.</li>
     *   <li>Else log failure and return 0.</li>
     * </ol>
     * @param loadFile     filename key for display
     * @param absolutePath full path on device
     * @return board size on success, 0 on failure
     */
    public int resumeGame(String loadFile, String absolutePath) {
        logMessage("Resuming game from file: " + loadFile);
        if (model.loadGame(absolutePath))
        {
            logMessage("\nResuming Game! \nPrevious first player: " +
                    (model.getHuman().getIsFirst() ? "Human" : "Computer") +
                    "\nNext Player: " + (model.getHuman().getIsNext() ? "Human" : "Computer"));
            currentState = GameState.BEGIN_TURN;
            return model.getBoard().getBoardSize();
        }
        else {
            logMessage("\nResumed Game was unable to load correctly, please try again.");
            return 0;
        }
    }


    /**
     * Persists current model state to storage.
     * @param absolutePath full path to write file
     */
    public void saveGame(String absolutePath) {
        model.saveGame(absolutePath);
    }



    /**
     * Handle presses of the three dynamic buttons and drive the
     * GameState state machine accordingly.
     *
     * <p><strong>State Summaries:</strong>
     * <ul>
     *   <li><b>START_GAME</b> – Initialize a new round: reset board, determine first player.</li>
     *   <li><b>BEGIN_TURN</b> – Prepare for next turn: clear selections, decide who rolls next.</li>
     *   <li><b>NUM_DICE</b> – (Human only) Choose to roll 1 or 2 dice when allowed.</li>
     *   <li><b>ROLL_TYPE</b> – Random vs. manual roll path for human or computer.</li>
     *   <li><b>WAIT_FOR_INPUT</b> – Wait for manual roll inputs, then branch.</li>
     *   <li><b>MOVE_TYPE</b> – (Human only) Choose cover vs. uncover after rolling.</li>
     *   <li><b>MOVE_SELECTION</b> – (Human only) Select and submit the exact squares to cover/uncover.</li>
     *   <li><b>ROUND_END</b> – Announce winner, update scores, ask for new round or end.</li>
     *   <li><b>NEW_BOARDSIZE</b> – Let user pick 9/10/11 for the next round, then restart.</li>
     * </ul>
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Switch on <code>currentState</code>.</li>
     *   <li>In each case, update model, view, logs, and next state.</li>
     *   <li>After switch, call <code>view.updateUI()</code>.</li>
     * </ol>
     *
     * @param buttonNumber which dynamic button was pressed (1–3)
     */
    public void onDynamicButtonClick(int buttonNumber) {
        switch (currentState) {
            case START_GAME:
                model.startGame(roundNum);
                view.updateBoardDisplay();
                logMessage("\n=======Round " + roundNum + "=======\n");
                currentState = GameState.BEGIN_TURN;
                break;

            case BEGIN_TURN:
                userManualRolls.clear();
                selectedHumanSquares.clear();
                selectedComputerSquares.clear();
                view.updateBoardDisplay();
                if (model.getHuman().getIsNext()) {
                    if (model.getBoard().checkUpperSquares(true)) {
                        logMessage("\nTime to roll! Since squares 7 through " + model.getBoard().getBoardSize() +
                                " are covered, you may choose to roll 1 or 2 dice.\n");
                        currentState = GameState.NUM_DICE;
                    } else {
                        logMessage("\nTime to roll! You must roll 2 dice (since at least one square from 7 to " +
                                model.getBoard().getBoardSize() + " is uncovered). \n\nWould you like to roll randomly or manually?");
                        userNumDice = 2;
                        currentState = GameState.ROLL_TYPE;
                    }
                } else {
                    model.getBoard().incrementTurn();
                    logMessage("\n===Computer's turn=== \nPlease select how you would like the Computer to roll");
                    currentState = GameState.ROLL_TYPE;
                }

                break;

            case NUM_DICE:
                if (buttonNumber == 1) {
                    // Roll 1 die
                    userNumDice = 1;
                    logMessage("\n\nWould you like to roll randomly or manually?");
                    currentState = GameState.ROLL_TYPE;
                } else if (buttonNumber == 2) {
                    // Roll 2 dice
                    userNumDice = 2;
                    logMessage("\nWould you like to roll randomly or manually?");
                    currentState = GameState.ROLL_TYPE;
                } else {
                    model.numDiceHelp();
                }
                break;

            case ROLL_TYPE:
                if (model.getHuman().getIsNext()) {
                    if (buttonNumber == 1) {
                        // Random Roll
                        userDiceSum = model.getHuman().rollDice(userNumDice, this);
                        if (model.checkMoveAvailable(userDiceSum, true)) {
                            logMessage("\nWould you like to cover or uncover?");
                            currentState = GameState.MOVE_TYPE;
                        } else {
                            currentState = GameState.BEGIN_TURN;
                        }
                    } else {
                        // Manual Roll
                        view.promptForManualRollCount(); //Get user's manual rolls
                        waitingForInput = true;
                        currentState = GameState.WAIT_FOR_INPUT;
                    }

                } else { // Computer's turn
                    if (buttonNumber == 1) {
                        //Computer Random
                        if (model.computerMove(null)) {
                            view.updateBoardDisplay();
                            logMessage("\n===Human's turn===");
                            currentState = GameState.BEGIN_TURN;
                        } else {
                            winner = "Computer";
                            view.updateBoardDisplay();
                            model.roundEnd(winner, roundNum);
                            currentState = GameState.ROUND_END;
                        }
                    } else {
                        //Computer Manual
                        view.promptForManualRollCount();
                        waitingForInput = true;
                        currentState = GameState.WAIT_FOR_INPUT;
                    }
                }
                break;

            case WAIT_FOR_INPUT:
                if (!waitingForInput)
                {
                    if (model.getHuman().getIsNext())
                    {
                        //Human manual
                        popManualRollOrRandom(userManualRolls);
                    }
                    else {
                        //Computer Manual
                        if (model.computerMove(userManualRolls)) {
                            view.updateBoardDisplay();
                            logMessage("\n===Human's turn===");
                            currentState = GameState.BEGIN_TURN;
                        } else {
                            winner = "Computer";
                            view.updateBoardDisplay();
                            model.roundEnd(winner, roundNum);
                            currentState = GameState.ROUND_END;
                        }
                    }

                }
                break;

            case MOVE_TYPE:
                if (buttonNumber == 1) {
                    // Cover Button
                    userCoverChoice = model.checkMoveType(true, userDiceSum);
                    logMessage("\nSelect the squares that sum up to " + userDiceSum + ".");
                    currentState = GameState.MOVE_SELECTION;
                } else if (buttonNumber == 2) {
                    // Uncover Button
                    userCoverChoice = model.checkMoveType(false, userDiceSum);
                    logMessage("\nSelect the squares that sum up to " + userDiceSum + ".");
                    currentState = GameState.MOVE_SELECTION;
                } else {
                    model.moveTypeHelp(userDiceSum);
                }
                view.updateBoardDisplay();
                break;

            case MOVE_SELECTION:
                if (buttonNumber == 1) {
                    logMessage("Selection reset.");
                    selectedHumanSquares.clear();
                    selectedComputerSquares.clear();
                    view.updateBoardDisplay();
                } else if (buttonNumber == 2) {
                    Set<Integer> combinedSelections = new HashSet<>();
                    combinedSelections.addAll(selectedHumanSquares);
                    combinedSelections.addAll(selectedComputerSquares);
                    boolean validMove = model.humanMove(userDiceSum, userCoverChoice, combinedSelections);
                    if (validMove) {
                        if (!model.checkWinner(true)) {
                            selectedHumanSquares.clear();
                            selectedComputerSquares.clear();
                            view.updateBoardDisplay();
                            logMessage("\nHuman's turn continues.");
                            if(userManualRolls != null && !userManualRolls.isEmpty()) {
                                popManualRollOrRandom(userManualRolls);
                            }
                            else {
                                currentState = GameState.BEGIN_TURN;
                            }

                        } else {
                            winner = "Human";
                            view.updateBoardDisplay();
                            model.roundEnd(winner, roundNum);
                            currentState = GameState.ROUND_END;
                        }
                    } else {
                        selectedHumanSquares.clear();
                        selectedComputerSquares.clear();
                        view.updateBoardDisplay();
                    }
                } else {
                    model.moveSelectionHelp(userDiceSum, userCoverChoice);
                }
                break;

            case ROUND_END:
                if (buttonNumber == 1) {
                    logMessage("\nStarting new round!");
                    selectedHumanSquares.clear();
                    selectedComputerSquares.clear();
                    view.updateBoardDisplay();
                    roundNum++;
                    logMessage("\nEnter new Board size (9,10,11)\n");
                    currentState = GameState.NEW_BOARDSIZE;
                } else {
                    view.goToTournamentResults();
                }
                break;

            case NEW_BOARDSIZE:
                if (buttonNumber == 1)
                {
                    model.getBoard().setBoardSize(9);
                    view.updateBoardSize(9);
                }
                else if (buttonNumber == 2)
                {
                    model.getBoard().setBoardSize(10);
                    view.updateBoardSize(10);
                }
                else {
                    model.getBoard().setBoardSize(11);
                    view.updateBoardSize(11);
                }

                currentState = GameState.START_GAME;

                break;
        }
        view.updateUI();
    }


    /**
     * Handles taps on human board squares during selection.
     * @param originalSquareValue 1‑based square identifier
     * @return true if the tap selected a square
     */
    public boolean selectHumanSquare(int originalSquareValue) {
        boolean selectedSquare = false;
        if (currentState == GameState.MOVE_SELECTION && userCoverChoice) {
            if (selectedHumanSquares.contains(originalSquareValue)) {
                selectedHumanSquares.remove(originalSquareValue);
            } else {
                selectedHumanSquares.add(originalSquareValue);
                selectedSquare = true;
            }
            view.updateBoardDisplay();
        }
        return selectedSquare;
    }


    /**
     * Handles taps on computer board squares during selection.
     * @param originalSquareValue 1‑based square identifier
     * @return true if the tap selected a square
     */
    public boolean selectComputerSquare(int originalSquareValue) {
        boolean selectedSquare = false;
        if (currentState == GameState.MOVE_SELECTION && !userCoverChoice) {
            if (selectedComputerSquares.contains(originalSquareValue)) {
                selectedComputerSquares.remove(originalSquareValue);
            } else {
                selectedComputerSquares.add(originalSquareValue);
                selectedSquare = true;
            }
            view.updateBoardDisplay();
        }
        return selectedSquare;
    }



    /**
     * Consumes the next manual roll or falls back to a random roll,
     * then advances the state accordingly. Used for Human turn only.
     * @param manualRolls list of pending manual dice sums
     */
    public void popManualRollOrRandom(ArrayList<Integer> manualRolls) {
        if (manualRolls != null && !manualRolls.isEmpty()) {
            for (int roll : manualRolls)
            {
                logMessage(String.valueOf(roll));
            }
            setUserDiceSum(manualRolls.remove(0));
            logMessage("\nRolled manually! Sum: " + getUserDiceSum());
        } else {
            setUserDiceSum(model.getHuman().rollDice(2, this));
            logMessage("\nRolled randomly! Sum: " + getUserDiceSum());
        }

        if (model.checkMoveAvailable(userDiceSum, model.getHuman().getIsNext())) {
            currentState = GameState.MOVE_TYPE;
        } else {
            currentState = GameState.BEGIN_TURN;
        }
        view.updateUI();
    }


    /**
     * Implementation of the Log interface, forwarding messages
     * to the view’s display.
     * @param message the text to append to the UI log
     */
    public void logMessage(String message) {
        view.displayMessage(message);
    }


    // ──────────────────────────────────────────────────────────────
    // Private Members
    // ──────────────────────────────────────────────────────────────

    private GameState currentState = GameState.START_GAME;
    private int roundNum = 1;
    private int userNumDice = 0;
    private int userDiceSum = 0;
    private boolean userCoverChoice = true;
    private ArrayList<Integer> userManualRolls = new ArrayList<>();
    private boolean waitingForInput = false;

    // Sets to track currently selected squares.
    private Set<Integer> selectedHumanSquares = new HashSet<>();
    private Set<Integer> selectedComputerSquares = new HashSet<>();

    private String winner = "";

    private CanogaModel model;
    private CanogaView view; // View interface implemented by GameActivity

}
