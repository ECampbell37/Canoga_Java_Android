/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    Player.java
 ************************************************************/

package com.example.canoga_android_elijahc.model;

import java.util.ArrayList;
import java.util.Set;


/**
 * Abstract base for a Canoga player (human or computer),
 * encapsulating scores, turn flags, and move‐making logic.
 */
public abstract class Player {

    // ──────────────────────────────────────────────────────────────
    // Public Constructor
    // ──────────────────────────────────────────────────────────────

    /**
     * Creates a Player linked to the given Board.
     * @param b the Board instance this player will act upon
     */
    public Player(Board b) {
        board = b;
    }



    // ──────────────────────────────────────────────────────────────
    // Public Selectors
    // ──────────────────────────────────────────────────────────────


    /** @return score earned in the last round */
    public int getRoundScore() {
        return roundScore;
    }

    /** @return cumulative tournament score */
    public int getTournamentScore() {
        return tournamentScore;
    }

    /** @return the handicap square value (0 if none) */
    public int getHandicapSquare() {
        return handicapSquare;
    }

    /** @return true if this player is first in the upcoming round */
    public boolean getIsFirst() {
        return isFirst;
    }

    /** @return true if this player moves next */
    public boolean getIsNext() {
        return isNext;
    }

    /** @return true if the last victory was by covering squares */
    public boolean getWonByCover() {
        return wonByCover;
    }

    /** @return true if this player won the previous round */
    public boolean getWonPrevious() {
        return wonPrevious;
    }


    // ──────────────────────────────────────────────────────────────
    // Public Mutators
    // ──────────────────────────────────────────────────────────────


    /**
     * Sets the last round's score for this player.
     * @param score non‐negative integer less than Integer.MAX_VALUE
     * @return true if applied; false otherwise
     */
    public boolean setRoundScore(int score) {
        if (score >= 0 && score < Integer.MAX_VALUE) {
            roundScore = score;
            return true;
        }
        return false;
    }


    /**
     * Directly sets the tournament score.
     * @param score non‐negative integer less than Integer.MAX_VALUE
     * @return true if applied; false otherwise
     */
    public boolean setTournamentScore(int score) {
        if (score >= 0 && score < Integer.MAX_VALUE) {
            tournamentScore = score;
            return true;
        }
        return false;
    }


    /**
     * Adds to the tournament score, up to the maximum possible.
     * @param score non‐negative integer
     * @return true if applied; false otherwise
     */
    public boolean addTournamentScore(int score) {
        int maxScore = board.getMaxScore(board.getBoardSize());
        if (score >= 0 && score <= maxScore) {
            tournamentScore += score;
            return true;
        }
        return false;
    }


    /**
     * Assigns the handicap square for next round.
     * @param sq value between 0 and board size
     * @return true if valid and applied; false otherwise
     */
    public boolean setHandicapSquare(int sq) {
        if (sq >= 0 && sq <= board.getBoardSize()) {
            handicapSquare = sq;
            return true;
        }
        return false;
    }

    /** @param first true if this player goes first next round */
    public void setIsFirst(boolean first) {
        isFirst = first;
    }


    /** @param next true if this player moves next */
    public void setIsNext(boolean next) {
        isNext = next;
    }


    /** @param cover true if the last win was by covering; false if by uncovering */
    public void setWonByCover(boolean cover) {
        wonByCover = cover;
    }

    /** @param won true if this player won the previous round */
    public void setWonPrevious(boolean won) {
        wonPrevious = won;
    }




    // ──────────────────────────────────────────────────────────────
    // Abstract Methods
    // ──────────────────────────────────────────────────────────────

    // Abstract methods for rolling dice and making moves.
    public abstract int rollDice(int numDice, Log log);
    public abstract boolean makeMove(Set<Integer> move, int diceSum, boolean isCover, Log log);




    // ──────────────────────────────────────────────────────────────
    // Public Methods
    // ──────────────────────────────────────────────────────────────

    /**
     * Chooses whether to roll one or two dice based on upper squares.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>If upper squares < 7 uncovered, return 2.</li>
     *   <li>Sum values of coverable squares.</li>
     *   <li>If sum ≤ 6 return 1, else return 2.</li>
     * </ol>
     *
     * @param isHuman true if evaluating the human player's board
     * @return 1 or 2 dice choice
     */
    public int chooseNumDice(boolean isHuman) {
        if (!board.checkUpperSquares(isHuman))
            return 2;
        ArrayList<Integer> available = board.getAvailableSquares(isHuman, true);
        int sumAvailable = 0;
        for (Integer sq : available) {
            sumAvailable += sq;
        }
        if (sumAvailable <= 6)
            return 1;
        return 2;
    }


    /**
     * Checks if no valid cover/uncover moves exist for the dice sum.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Get coverMoves = allValidMoves(...).</li>
     *   <li>Get uncoverMoves = allValidMoves(...).</li>
     *   <li>If turn ≤ 1, clear uncoverMoves.</li>
     *   <li>Return true if both lists empty.</li>
     * </ol>
     *
     * @param isHuman whose turn to evaluate
     * @param diceSum the rolled total
     * @return true if turn must end immediately
     */
    public boolean checkNoMovesAvailable(boolean isHuman, int diceSum) {
        ArrayList<Integer> availableCover = board.getAvailableSquares(isHuman, true);
        ArrayList<Integer> availableUncover = board.getAvailableSquares(!isHuman, false);

        ArrayList<ArrayList<Integer>> coverMoves = allValidMoves(availableCover, diceSum);
        ArrayList<ArrayList<Integer>> uncoverMoves = allValidMoves(availableUncover, diceSum);

        if (board.getTurn() <= 1)
            uncoverMoves.clear();

        return coverMoves.isEmpty() && uncoverMoves.isEmpty();
    }


    /**
     * Finds an instant‐win move if available.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Return [] if preconditions (turn≤1 or already won).</li>
     *   <li>For each cover move:</li>
     *     <ol type="a">
     *       <li>Clone board, apply move, check win.</li>
     *     </ol>
     *   <li>If none, repeat for uncover moves.</li>
     *   <li>Return first winning move or [].</li>
     * </ol>
     *
     * @param isHuman whose perspective
     * @param diceSum the rolled total
     * @return squares for immediate win; empty if none
     */
    public ArrayList<Integer> getInstantWinMove(boolean isHuman, int diceSum) {
        //If win condition is not yet legal, return empty list.
        if (board.getTurn() <= 1 || board.allSquaresCovered(isHuman) || board.allSquaresUncovered(!isHuman))
            return new ArrayList<>();


        // test cover moves
        ArrayList<Integer> availableCover;
        if (isHuman)
            availableCover = board.getAvailableSquares(true, true);
        else
            availableCover = board.getAvailableSquares(false, true);


        ArrayList<ArrayList<Integer>> coverMoves = allValidMoves(availableCover, diceSum);
        for (ArrayList<Integer> move : coverMoves) {
            Board boardCopy = copyBoard(board);
            for (Integer sq : move) {
                if (isHuman)
                    boardCopy.coverSquare(true, sq);
                else
                    boardCopy.coverSquare(false, sq);
            }
            if (isHuman) {
                if (boardCopy.allSquaresCovered(true)) {
                    setWonByCover(true);
                    return move;
                }
                if (boardCopy.allSquaresUncovered(false)) {
                    setWonByCover(false);
                    return move;
                }
            } else {
                if (boardCopy.allSquaresCovered(false)) {
                    setWonByCover(true);
                    return move;
                }
                if (boardCopy.allSquaresUncovered(true)) {
                    setWonByCover(false);
                    return move;
                }
            }
        }


        // test uncover moves
        ArrayList<Integer> availableUncover;
        if (isHuman)
            availableUncover = board.getAvailableSquares(false, false);
        else
            availableUncover = board.getAvailableSquares(true, false);

        ArrayList<ArrayList<Integer>> uncoverMoves = allValidMoves(availableUncover, diceSum);
        for (ArrayList<Integer> move : uncoverMoves) {
            Board boardCopy = copyBoard(board);
            for (Integer sq : move) {
                if (isHuman)
                    boardCopy.uncoverSquare(false, sq);
                else
                    boardCopy.uncoverSquare(true, sq);
            }
            if (isHuman) {
                if (boardCopy.allSquaresCovered(true)) {
                    setWonByCover(true);
                    return move;
                }
                if (boardCopy.allSquaresUncovered(false)) {
                    setWonByCover(false);
                    return move;
                }
            } else {
                if (boardCopy.allSquaresCovered(false)) {
                    setWonByCover(true);
                    return move;
                }
                if (boardCopy.allSquaresUncovered(true)) {
                    setWonByCover(false);
                    return move;
                }
            }
        }

        return new ArrayList<>();
    }



    /**
     * Determines whether to favor covering or uncovering.
     * @param isHuman whose perspective
     * @param diceSum the rolled total
     * @return true to cover; false to uncover
     */
    public boolean shouldCoverOwnSquares(boolean isHuman, int diceSum) {
        ArrayList<Integer> availableCover = board.getAvailableSquares(isHuman, true);
        ArrayList<Integer> availableUncover = board.getAvailableSquares(!isHuman, false);

        ArrayList<ArrayList<Integer>> coverMoves = allValidMoves(availableCover, diceSum);
        ArrayList<ArrayList<Integer>> uncoverMoves = allValidMoves(availableUncover, diceSum);

        if (!coverMoves.isEmpty() && uncoverMoves.isEmpty())
            return true;
        if (coverMoves.isEmpty() && !uncoverMoves.isEmpty())
            return false;

        int numAvailCover = availableCover.size();
        int numAvailUncover = availableUncover.size();
        int ratio = board.getBoardSize() / 2;

        if (numAvailCover <= ratio)
            return true;
        if (numAvailUncover < ratio)
            return false;
        return true;
    }


    /**
     * Lists all valid subsets (size 1–4) summing to the target.
     * @param available values to combine
     * @param target desired sum
     * @return list of valid moves
     */
    public ArrayList<ArrayList<Integer>> allValidMoves(ArrayList<Integer> available, int target) {
        ArrayList<ArrayList<Integer>> moves = new ArrayList<>();
        int n = available.size();

        // Check all subsets of 1 to 4 numbers.
        for (int i = 0; i < n; i++) {
            if (available.get(i) == target) {
                ArrayList<Integer> singleMove = new ArrayList<>();
                singleMove.add(available.get(i));
                moves.add(singleMove);
            }
            for (int j = i + 1; j < n; j++) {
                if (available.get(i) + available.get(j) == target) {
                    ArrayList<Integer> twoMove = new ArrayList<>();
                    twoMove.add(available.get(i));
                    twoMove.add(available.get(j));
                    moves.add(twoMove);
                }
                for (int k = j + 1; k < n; k++) {
                    if (available.get(i) + available.get(j) + available.get(k) == target) {
                        ArrayList<Integer> threeMove = new ArrayList<>();
                        threeMove.add(available.get(i));
                        threeMove.add(available.get(j));
                        threeMove.add(available.get(k));
                        moves.add(threeMove);
                    }
                    for (int l = k + 1; l < n; l++) {
                        if (available.get(i) + available.get(j) + available.get(k) + available.get(l) == target) {
                            ArrayList<Integer> fourMove = new ArrayList<>();
                            fourMove.add(available.get(i));
                            fourMove.add(available.get(j));
                            fourMove.add(available.get(k));
                            fourMove.add(available.get(l));
                            moves.add(fourMove);
                        }
                    }
                }
            }
        }
        return moves;
    }


    /**
     * Logs all valid moves in a user‐readable format.
     * @param moves list of move subsets
     * @param log the Log instance to print to
     */
    public void displayValidMoves(ArrayList<ArrayList<Integer>> moves, Log log) {
        if (moves.isEmpty()) {
            log.logMessage("No valid moves available.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Valid moves:\n");
        for (ArrayList<Integer> move : moves) {
            sb.append("{ ");
            for (int i = 0; i < move.size(); i++) {
                sb.append(move.get(i));
                if (i < move.size() - 1)
                    sb.append(", ");
            }
            sb.append(" }\n");
        }
        log.logMessage(sb.toString());
    }


    /**
     * Picks the “best” move: minimal squares when covering,
     * maximal squares when uncovering.
     * @param moves candidate moves
     * @param forCover true if covering mode
     * @return the chosen subset
     */
    public ArrayList<Integer> chooseBestMove(ArrayList<ArrayList<Integer>> moves, boolean forCover) {
        if (moves.isEmpty())
            return new ArrayList<>();
        ArrayList<Integer> bestMove = moves.get(0);
        if (forCover) {
            for (ArrayList<Integer> move : moves) {
                if (move.size() < bestMove.size())
                    bestMove = move;
            }
        } else {
            for (ArrayList<Integer> move : moves) {
                if (move.size() > bestMove.size())
                    bestMove = move;
            }
        }
        return bestMove;
    }


    /**
     * Provides human‐player advice for the chosen best move.
     * @param moves all valid moves
     * @param forCover true if covering
     * @param log the Log instance
     */
    public void helpHumanPickBest(ArrayList<ArrayList<Integer>> moves, boolean forCover, Log log) {
        ArrayList<Integer> bestMove = chooseBestMove(moves, forCover);
        StringBuilder moveString = new StringBuilder("{ ");
        for (int i = 0; i < bestMove.size(); i++) {
            moveString.append(bestMove.get(i));
            if (i != bestMove.size() - 1)
                moveString.append(", ");
        }
        moveString.append(" }");
        if (forCover) {
            log.logMessage("\nHelp: The best move is " + moveString.toString() +
                    ".\nIdeally, you want to cover the highest value possible, since those values are harder to roll." +
                    "\nThis move does exactly that!");
        } else {
            log.logMessage("\nHelp: The best move is " + moveString.toString() +
                    ".\nIdeally, you want to uncover as many opponent squares as possible to maximize your score." +
                    "\nThis move does exactly that!");
        }
    }


    /**
     * Checks for a win condition after a move.
     * @param isHuman whose board to check
     * @return true if that player has won
     */
    public boolean checkWin(boolean isHuman) {
        return ((board.allSquaresCovered(isHuman) || board.allSquaresUncovered(!isHuman)) && board.getTurn() > 1);
    }


    /**
     * Computes the round score based on covered/uncovered state.
     * @param isHuman who won
     * @return points earned this round
     */
    public int calculateRoundScore(boolean isHuman) {
        int score = 0;
        int tempScore = 0;
        if (isHuman) {
            if (wonByCover) {
                for (Integer s : board.getComputerSquares())
                    score += s;
            } else {
                for (Integer s : board.getHumanSquares())
                    tempScore += s;
                score = board.getMaxScore(board.getBoardSize()) - tempScore;
            }
        } else {
            if (wonByCover) {
                for (Integer s : board.getHumanSquares())
                    score += s;
            } else {
                for (Integer s : board.getComputerSquares())
                    tempScore += s;
                score = board.getMaxScore(board.getBoardSize()) - tempScore;
            }
        }
        return score;
    }


    // ──────────────────────────────────────────────────────────────
    // Private Methods
    // ──────────────────────────────────────────────────────────────


    /**
     * Creates a deep copy of the given board for simulation.
     * @param original the Board to clone
     * @return a separate Board instance with identical state
     */
    private Board copyBoard(Board original) {
        Board copy = new Board(original.getBoardSize());
        copy.resetBoard();
        copy.setHumanSquares(original.getHumanSquares());
        copy.setComputerSquares(original.getComputerSquares());
        while (copy.getTurn() < original.getTurn()) {
            copy.incrementTurn();
        }
        return copy;
    }



    // ──────────────────────────────────────────────────────────────
    // Protected Members
    // ──────────────────────────────────────────────────────────────

    protected Board board;
    protected Log log;


    // ──────────────────────────────────────────────────────────────
    // Private Members
    // ──────────────────────────────────────────────────────────────

    private int roundScore = 0;
    private boolean isFirst = true;
    private boolean isNext = false;
    private boolean wonByCover = true;
    private boolean wonPrevious = false;
    private int handicapSquare = 0;
    private int tournamentScore = 0;

}
