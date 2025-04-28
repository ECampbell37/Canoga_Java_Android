/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    CanogaModel.java
 ************************************************************/

package com.example.canoga_android_elijahc.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.io.*;


/**
 * The high‑level game model, orchestrating rounds,
 * serialization, turn order, and delegating to Human/Computer players.
 */
public class CanogaModel {

    // ──────────────────────────────────────────────────────────────
    // Public Constructors
    // ──────────────────────────────────────────────────────────────

    /**
     * Initializes a new game model with the given board size and log.
     * @param boardSize number of squares per row (9,10,11)
     * @param l         Log implementation for output
     */
    public CanogaModel(int boardSize, Log l) {
        board = new Board(boardSize);
        human = new Human(board);
        computer = new Computer(board);
        log = l;
    }

    // ──────────────────────────────────────────────────────────────
    // Public Selectors
    // ──────────────────────────────────────────────────────────────


    /** @return the human player object */
    public Human getHuman()
    {
        return human;
    }


    /** @return the computer player object */
    public Computer getComputer()
    {
        return computer;
    }


    /** @return the Board instance */
    public Board getBoard()
    {
        return board;
    }


    // ──────────────────────────────────────────────────────────────
    // Public Methods
    // ──────────────────────────────────────────────────────────────


    /**
     * Loads game state from a text file.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Open file for reading.</li>
     *   <li>Read lines, tokenize by whitespace.</li>
     *   <li>When key "Computer:", parse squares then score.</li>
     *   <li>When key "Human:", parse squares then score.</li>
     *   <li>Parse "First Turn:" and "Next Turn:".</li>
     *   <li>Validate and apply board size, squares, and scores.</li>
     *   <li>Set first/next flags and advance turn counter twice.</li>
     *   <li>Return true on success; catch IOException and return false.</li>
     * </ol>
     *
     * @param filename path to load file
     * @return true if loaded successfully; false on any error
     */
    public boolean loadGame(String filename) {
        log.logMessage("\nLoading... ");
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line, key;
            ArrayList<Integer> computerSquares = new ArrayList<>();
            ArrayList<Integer> humanSquares = new ArrayList<>();
            int computerScore = 0, humanScore = 0;
            String firstTurn = "", nextTurn = "";
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                if (!st.hasMoreTokens())
                    continue;
                key = st.nextToken();
                if (key.equals("Computer:")) {
                    line = br.readLine();
                    if (line != null) {
                        st = new StringTokenizer(line);
                        if (st.hasMoreTokens() && st.nextToken().equals("Squares:")) {
                            while (st.hasMoreTokens()) {
                                computerSquares.add(Integer.parseInt(st.nextToken()));
                            }
                        } else {
                            log.logMessage("\nError: Expected 'Squares:' after 'Computer:'.");
                            return false;
                        }
                    }
                    line = br.readLine();
                    if (line != null) {
                        st = new StringTokenizer(line);
                        if (st.hasMoreTokens() && st.nextToken().equals("Score:")) {
                            if (st.hasMoreTokens())
                                computerScore = Integer.parseInt(st.nextToken());
                            else {
                                log.logMessage("\nError: Invalid computer score format.");
                                return false;
                            }
                        } else {
                            log.logMessage("\nError: Expected 'Score:' after 'Computer Squares'.");
                            return false;
                        }
                    }
                } else if (key.equals("Human:")) {
                    line = br.readLine();
                    if (line != null) {
                        st = new StringTokenizer(line);
                        if (st.hasMoreTokens() && st.nextToken().equals("Squares:")) {
                            while (st.hasMoreTokens()) {
                                humanSquares.add(Integer.parseInt(st.nextToken()));
                            }
                        } else {
                            log.logMessage("\nError: Expected 'Squares:' after 'Human:'.");
                            return false;
                        }
                    }
                    line = br.readLine();
                    if (line != null) {
                        st = new StringTokenizer(line);
                        if (st.hasMoreTokens() && st.nextToken().equals("Score:")) {
                            if (st.hasMoreTokens())
                                humanScore = Integer.parseInt(st.nextToken());
                            else {
                                log.logMessage("\nError: Invalid human score format.");
                                return false;
                            }
                        } else {
                            log.logMessage("\nError: Expected 'Score:' after 'Human Squares'.");
                            return false;
                        }
                    }
                } else if (key.equals("First")) {
                    if (st.hasMoreTokens()) {
                        st.nextToken(); // Skip "Turn:"
                        firstTurn = st.nextToken();
                        if (!firstTurn.equals("Human") && !firstTurn.equals("Computer")) {
                            log.logMessage("\nError: Invalid value for First Turn.");
                            return false;
                        }
                    } else {
                        log.logMessage("\nError: Incorrect format for First Turn.");
                        return false;
                    }
                } else if (key.equals("Next")) {
                    if (st.hasMoreTokens()) {
                        st.nextToken(); // Skip "Turn:"
                        nextTurn = st.nextToken();
                        if (!nextTurn.equals("Human") && !nextTurn.equals("Computer")) {
                            log.logMessage("\nError: Invalid value for Next Turn.");
                            return false;
                        }
                    } else {
                        log.logMessage("\nError: Incorrect format for Next Turn.");
                        return false;
                    }
                }
            }

            board.setBoardSize(computerSquares.size());
            if (!board.setComputerSquares(computerSquares)) {
                log.logMessage("\nError: Invalid board data entry for Computer Player.");
                return false;
            }
            if (!board.setHumanSquares(humanSquares)) {
                log.logMessage("\nError: Invalid board data entry for Human Player.");
                return false;
            }
            if (!computer.setTournamentScore(computerScore)) {
                log.logMessage("\nError: Invalid score data entry for Computer Player.");
                return false;
            }
            if (!human.setTournamentScore(humanScore)) {
                log.logMessage("\nError: Invalid score data entry for Human Player.");
                return false;
            }
            human.setIsFirst(firstTurn.equals("Human"));
            human.setIsNext(nextTurn.equals("Human"));
            board.incrementTurn();
            board.incrementTurn();

            log.logMessage("\nGame loaded successfully!");
            return true;
        } catch (IOException e) {
            log.logMessage("\nError: Unable to open file for loading.");
            return false;
        }
    }



    /**
     * Saves current game state into a text file.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Open PrintWriter on filename.</li>
     *   <li>Write "Computer:", its squares and score.</li>
     *   <li>Write "Human:", its squares and score.</li>
     *   <li>Write "First Turn:" and "Next Turn:".</li>
     *   <li>Log success; catch IOException and log error.</li>
     * </ol>
     *
     * @param filename path to save file
     */
    public void saveGame(String filename) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("Computer:");
            pw.print("  Squares: ");
            for (Integer sq : board.getComputerSquares())
                pw.print(sq + " ");
            pw.println();
            pw.println("  Score: " + computer.getTournamentScore());

            pw.println("Human:");
            pw.print("  Squares: ");
            for (Integer sq : board.getHumanSquares())
                pw.print(sq + " ");
            pw.println();
            pw.println("  Score: " + human.getTournamentScore());

            pw.println("First Turn: " + (human.getIsFirst() ? "Human" : "Computer"));
            pw.println("Next Turn: " + (human.getIsNext() ? "Human" : "Computer"));

            log.logMessage("\nGame saved successfully to " + filename + ".");
        } catch (IOException e) {
            log.logMessage("\nError: Unable to open file for saving.");
        }
    }



    /**
     * Rolls dice for both players to determine who goes first.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Repeat rolling two dice each until values differ.</li>
     *   <li>Log each roll; on tie, repeat.</li>
     *   <li>Assign first and next flags based on higher roll.</li>
     * </ol>
     */
    public void determineFirstPlayer() {
        log.logMessage("\nDetermining first player...");
        int humanRoll, computerRoll;
        Random rand = new Random();
        do {
            humanRoll = (rand.nextInt(6) + 1) + (rand.nextInt(6) + 1);
            computerRoll = (rand.nextInt(6) + 1) + (rand.nextInt(6) + 1);
            log.logMessage("Human rolled: " + humanRoll);
            log.logMessage("Computer rolled: " + computerRoll);
            if (humanRoll == computerRoll)
                log.logMessage("\nTie in the roll! Time to roll again!\n");
        } while (humanRoll == computerRoll);

        if (humanRoll >= computerRoll) {
            log.logMessage("Human goes first.");
            human.setIsFirst(true);
            computer.setIsFirst(false);
            human.setIsNext(true);
            computer.setIsNext(false);
        } else {
            log.logMessage("Computer goes first.");
            human.setIsFirst(false);
            computer.setIsFirst(true);
            human.setIsNext(false);
            computer.setIsNext(true);
        }
    }


    /**
     * Alternates the first‐player flag for the next round.
     */
    public void alternateFirstPlayer() {
        if (human.getIsFirst()) {
            log.logMessage("\nLast round, the human player got first move. This round, the computer player gets first move!");
            computer.setIsFirst(true);
            human.setIsFirst(false);
        } else {
            log.logMessage("\nLast round, the computer player got first move. This round, the human player gets first move!");
            computer.setIsFirst(false);
            human.setIsFirst(true);
        }
    }



    /**
     * Applies a handicap square based on previous round score.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Compute sum of digits of previousScore, wrap if >size.</li>
     *   <li>If sum==0, log no advantage and return.</li>
     *   <li>If previous winner was first, grant to opponent; else to winner.</li>
     *   <li>Cover that square and set handicap flags.</li>
     * </ol>
     *
     * @param previousScore score from last round
     */
    public void determineHandicap(int previousScore) {
        boolean humanPreviousFirst = human.getIsFirst();
        int handicapSquare = sumOfTwoDigits(previousScore);

        if (human.getWonPrevious()) {
            if (handicapSquare == 0) {
                log.logMessage("\nScore was not increased last round. No advantage given!");
                return;
            }
            if (humanPreviousFirst) {
                board.coverSquare(false, handicapSquare);
                log.logMessage("\nAdvantage given to computer player! The square " + handicapSquare + " has been covered!");
                human.setHandicapSquare(handicapSquare);
                computer.setHandicapSquare(0);
            } else {
                board.coverSquare(true, handicapSquare);
                log.logMessage("\nAdvantage given to human player! The square " + handicapSquare + " has been covered!");
                computer.setHandicapSquare(handicapSquare);
                human.setHandicapSquare(0);
            }
        } else {
            if (handicapSquare == 0) {
                log.logMessage("\nScore was not increased last round. No advantage given!");
                return;
            }
            if (humanPreviousFirst) {
                board.coverSquare(false, handicapSquare);
                log.logMessage("\nAdvantage given to computer player! The square " + handicapSquare + " has been covered!");
                human.setHandicapSquare(handicapSquare);
                computer.setHandicapSquare(0);
            } else {
                board.coverSquare(true, handicapSquare);
                log.logMessage("\nAdvantage given to human player! The square " + handicapSquare + " has been covered!");
                computer.setHandicapSquare(handicapSquare);
                human.setHandicapSquare(0);
            }
        }
    }


    /**
     * Returns the sum of the tens and ones digits, wrapped by board size.
     * @param num the score to split into digits
     * @return digit sum modulo board size
     */
    public int sumOfTwoDigits(int num) {
        int tens = num / 10;
        int ones = num % 10;
        int sum = tens + ones;
        log.logMessage("\n\nScore was " + num + " last round. The tens is " + tens + ", the ones is " + ones + ". The total of the digits is " + sum + ".");
        if (sum > board.getBoardSize()) {
            sum = sum % board.getBoardSize();
            log.logMessage("\nSince this sum is greater than the size of the board, we will wrap the advantage square! New value: " + sum);
        }
        return sum;
    }



    /**
     * Starts a round: resets board and determines first player (and handicap).
     * @param roundNum current round number (1 = new tournament)
     */
    public void startGame(int roundNum) {
        board.resetBoard();
        if(roundNum == 1)
        {
            determineFirstPlayer();
        }
        else {
            determineHandicap(winnerScore);
            determineFirstPlayer();
        }

    }


    /**
     * Executes the computer's full turn, processing manual then auto‐rolls.
     * @param manualRolls optional list of manual dice sums
     * @return true if human’s turn follows; false if computer wins
     */
    public boolean computerMove(ArrayList<Integer> manualRolls) {
        boolean turnActive = true;

        if (manualRolls != null && !manualRolls.isEmpty())
        {
            for (int roll : manualRolls)
            {
                log.logMessage(String.valueOf(roll));
            }
            while (turnActive)
            {
                if (manualRolls.isEmpty()) break;
                int diceSum = manualRolls.remove(0);
                turnActive = computer.makeMove(null, diceSum, computer.shouldCoverOwnSquares(false, diceSum), log);
            }
        }
        else {
            log.logMessage("Manual Rolls are empty!");
        }

        while (turnActive)
        {
            int diceSum = computer.rollDice(computer.chooseNumDice(false), log);
            turnActive = computer.makeMove(null, diceSum, computer.shouldCoverOwnSquares(false, diceSum), log);
        }

        if(computer.checkWin(false))
        {
            human.setIsNext(true);
            computer.setIsNext(false);
            return false;
        }

        human.setIsNext(true);
        computer.setIsNext(false);
        getBoard().incrementTurn();
        log.logMessage("\nHuman's turn!");
        return true;
    }


    /**
     * Delegates a human move to the Human object.
     * @param diceSum target sum to match
     * @param isCover true to cover; false to uncover
     * @param selectedSquares set of square values selected
     * @return true if move applied successfully; false otherwise
     */
    public boolean humanMove(int diceSum, boolean isCover, Set<Integer> selectedSquares) {
        return human.makeMove(selectedSquares, diceSum, isCover, log);

    }


    /**
     * Checks if a move is possible for the given player and dice sum.
     * @param diceSum rolled total
     * @param isHuman true if checking human, false for computer
     * @return false if no moves available (and switches turn); true otherwise
     */
    public boolean checkMoveAvailable(int diceSum, boolean isHuman) {
        if (isHuman)
        {
            if (human.checkNoMovesAvailable(true, diceSum)) {
                log.logMessage("\n\nNo available squares to cover or uncover that add up to " + diceSum + ". Turn ended.\n");
                human.setIsNext(false);
                computer.setIsNext(true);
                return false;
            }

        }
        else {
            if (computer.checkNoMovesAvailable(false, diceSum)) {
                log.logMessage("\n\nNo available squares to cover or uncover that add up to " + diceSum + ". Turn ended.\n");
                human.setIsNext(true);
                computer.setIsNext(false);
                return false;
            }

        }
        return true;
    }


    /** Prints advice on choosing 1 vs. 2 dice for the human. */
    public void numDiceHelp() {
        if (human.chooseNumDice(true) == 1) {
            log.logMessage("\nHelp: It is best to roll one dice since the sum of your squares is 6 or lower.");
        } else {
            log.logMessage("\nHelp: In your situation, since the sum of your remaining squares is over 6, rolling 2 dice is better.");
        }
    }


    /** Gives strategy advice on cover vs. uncover for the human. */
    public void moveTypeHelp(int diceSum) {
        ArrayList<Integer> winMove = human.getInstantWinMove(true, diceSum);
        boolean coverHelp = human.shouldCoverOwnSquares(true, diceSum);

        if (board.getTurn() <= 1)
            coverHelp = true;

        if (winMove != null && !winMove.isEmpty() && board.getTurn() > 1) {
            log.logMessage("\nHelp: You should definitely " + (human.getWonByCover() ? "cover" : "uncover") + "! You have a winning move!");
        } else if (coverHelp) {
            log.logMessage("\nHelp: It is best to cover, as you have more cover moves that lead to victory.");
        } else {
            log.logMessage("\nHelp: It is best to uncover, as you have more uncover moves that lead to victory.");
        }
    }


    /**
     * Validates and possibly flips the human’s cover/uncover choice.
     * @param isCover initial choice
     * @param diceSum rolled total
     * @return possibly adjusted choice
     */
    public boolean checkMoveType(boolean isCover, int diceSum) {
        if (board.getTurn() <= 1 && human.getHandicapSquare() != 0 && !isCover) {
            log.logMessage("\nYou cannot remove the handicap square yet! Switching to cover...");
            isCover = true;
            return isCover;
        }

        ArrayList<Integer> available;
        if (isCover) {
            available = board.getAvailableSquares(true, true);
            if (available.isEmpty()) {
                log.logMessage("\nThat's not a great choice... there are no moves to cover! Switching to uncover...");
                isCover = false;
                return isCover;
            }
        } else {
            available = board.getAvailableSquares(false, false);
            if (available.isEmpty()) {
                log.logMessage("\nThat's not a great choice... there are no moves to uncover! Switching to cover...");
                isCover = true;
                return isCover;
            }
        }

        ArrayList<ArrayList<Integer>> possibleMoves = human.allValidMoves(available, diceSum);
        if (possibleMoves.isEmpty()) {
            log.logMessage("No possible " + (isCover ? "cover" : "uncover") +
                    " moves that add up to " + diceSum + "! Switching to "
                    + (!isCover ? "cover" : "uncover") + "...");
            return !isCover;
        }

        return isCover;
    }


    /** Helps the human pick squares when selecting move. */
    public void moveSelectionHelp(int diceSum, boolean isCover) {
        ArrayList<Integer> winMove = human.getInstantWinMove(true, diceSum);

        ArrayList<Integer> available;
        if (isCover)
        {
            available = board.getAvailableSquares(true, true);
        }
        else {
            available = board.getAvailableSquares(false, false);
        }

        ArrayList<ArrayList<Integer>> possibleMoves = human.allValidMoves(available, diceSum);
        if (possibleMoves.isEmpty()) {
            log.logMessage("No available squares to " + (isCover ? "cover" : "uncover") + ".");
            return;
        }

        human.displayValidMoves(possibleMoves, log);
        if (!winMove.isEmpty()) {
            StringBuilder winSb = new StringBuilder("\nHelp: You can win with the following move: { ");
            for (int i = 0; i < winMove.size(); i++) {
                winSb.append(winMove.get(i));
                if (i < winMove.size() - 1)
                    winSb.append(", ");
            }
            winSb.append(" }");
            log.logMessage(winSb.toString());
        } else {
            human.helpHumanPickBest(possibleMoves, isCover, log);
        }
    }


    /**
     * Checks if the specified player has won the round.
     * @param isHuman true to check human; false for computer
     * @return true if that player has met win conditions
     */
    public boolean checkWinner(boolean isHuman) {
        if (isHuman)
        {
            //Human win
            if(human.checkWin(true))
            {
                human.setIsNext(false);
                computer.setIsNext(true);
                return true;
            }
        }
        else {
            //Computer win
            if(computer.checkWin(false))
            {
                human.setIsNext(true);
                computer.setIsNext(false);
                return true;
            }
        }
        return false;
    }


    /**
     * Handles end‑of‑round scoring, logging, and board reset.
     * @param winner   "Human" or "Computer"
     * @param roundNum current round number
     */
    public void roundEnd(String winner, int roundNum) {
        if (winner.equals("Human")) {
            winnerScore = human.calculateRoundScore(true);
            human.setRoundScore(winnerScore);
            human.addTournamentScore(winnerScore);
            log.logMessage("Human wins round " + roundNum + " and earns " + winnerScore + " points!");
        } else if (winner.equals("Computer")) {
            winnerScore = computer.calculateRoundScore(false);
            computer.setRoundScore(winnerScore);
            computer.addTournamentScore(winnerScore);
            log.logMessage("Computer wins round " + roundNum + " and earns " + winnerScore + " points!");
        } else {
            log.logMessage("\n\nNo winner this round... Ending Tournament.");
            winnerScore = 0;
            return;
        }

        log.logMessage("\n\n******** Updated Tournament Score ********");
        log.logMessage("Rounds Played: " + roundNum);
        log.logMessage("Computer score: " + computer.getTournamentScore());
        log.logMessage("Human score: " + human.getTournamentScore());
        log.logMessage("******************************************\n");

        board.resetBoard();
    }


    // ──────────────────────────────────────────────────────────────
    // Private Members
    // ──────────────────────────────────────────────────────────────

    private Board board;
    private Human human;
    private Computer computer;
    private Log log;
    private int winnerScore = 0;

}
