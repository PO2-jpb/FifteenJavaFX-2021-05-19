package pt.ipbeja.estig.fifteen.model;

import pt.ipbeja.estig.fifteen.gui.View;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The fifteen puzzle model
 *
 * @author João Paulo Barros
 * @version 2014/05/19 - 2016/04/03 - 2017/04/19 - 2019/05/06 - 2021/05/18
 */
public class FifteenModel {
    public static final int N_LINES = 4;
    public static final int N_COLS = 4;
    public static final int EMPTY = 0;

    private final static Random RAND = new Random();
    private final static int[][] NEIGHBORS = {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};

    private int pieces[][];
    private Position emptyPosition;

    private Deque<Move> moves;

    private Timer timer;
    private int timerValue;

    private View view;

    /**
     * Creates board in winning position
     */
    public FifteenModel(View view) {
        this.moves = new ArrayDeque<>();
        this.resetBoard();
        this.timer = new Timer();
        this.view = view;
    }

    /**
     * Creates a random mixed board starting from a winning position
     *
     * @param minIter        minimum number of iterations to mix board
     * @param additionalIter maximum number of additional iterations to mix board
     */
    public FifteenModel(View view, int minIter, int additionalIter) {
        this(view); // call default constructor Fifteen()
        this.mix(minIter, additionalIter);
        this.resetTimer();
        this.startTimer();
    }

    /**
     * Puts the board in the winning position (numbers in sequence)
     */
    private void resetBoard() {
        this.pieces = new int[FifteenModel.N_LINES][FifteenModel.N_COLS];
        int pieceNumber = 1;
        for (int line = 0; line < FifteenModel.N_LINES; line++) {
            for (int col = 0; col < FifteenModel.N_COLS; col++) {
                this.pieces[line][col] = pieceNumber++;
            }
        }
        this.pieces[FifteenModel.N_LINES - 1][FifteenModel.N_COLS - 1] = FifteenModel.EMPTY; // empty
        this.emptyPosition = new Position(FifteenModel.N_LINES - 1, FifteenModel.N_COLS - 1);
    }

    /**
     * @return fifteen board content in text form
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int line = 0; line < N_LINES; line++) {
            for (int col = 0; col < N_COLS; col++) {
                s.append(String.format("%2d, ", this.pieces[line][col]));
            }
            s.setLength(s.length() - 2);
            s.append("\n");
        }
        return s.toString();
    }

    /**
     * get piece at given position
     *
     * @param position to get piece
     * @return the piece at position
     */
    public int pieceAt(Position position) {
        return this.pieces[position.getLine()][position.getCol()];
    }

    /**
     * get piece at given position
     *
     * @param position to get piece
     * @return the text for the piece at position
     */
    public String pieceTextAt(Position position) {
        int i = this.pieceAt(position);
        return i == EMPTY ? "empty" : i + "";
    }

    /**
     * mixes the puzzle with random moves
     *
     * @param minMoves minimum of moves
     * @param maxMoves maximum of moves
     * @return the moves executed, but in reverse order
     */
    public void mix(int minMoves, int maxMoves) {
        assert (minMoves <= maxMoves);
        // see http://docs.oracle.com/javase/8/docs/api/java/util/Deque.html
        Position empty = new Position(N_LINES - 1, N_COLS - 1);
        int nMoves = minMoves + RAND.nextInt(maxMoves - minMoves + 1);

        for (int i = 0; i < nMoves; i++) {
            Position pieceToMove = this.randomlySelectNeighborOf(empty);
            Move m = new Move(pieceToMove, empty); // occupy empty space
            this.applyMove(m);
            empty = pieceToMove; // moved piece position is now the empty
            // position
            this.moves.addFirst(m); // add at head (begin) of deque
        }
    }

    /**
     * Solve the puzzle using the stored positions
     */
    public void solve() {
        this.unmix(500);
    }

    /**
     * rewinds the puzzle with given moves and applies the reverse of each move
     *
     * @param sleepTime time between each move
     */
    public void unmix(int sleepTime) {
        Runnable task = () -> {
            Move m;
            while ((m = moves.poll()) != null) {
                Move mr = m.getReversed();
                applyMove(mr);
                FifteenModel.sleep(sleepTime);
                boolean winning = inWinningPositions();

                notifyViews(mr, winning, timerValue);

                if (winning) {
                    moves.clear();
                    break;
                }
            }
        };
        Thread threadToUnmix = new Thread(task);
        threadToUnmix.start();
    }


    /**
     * Get all the pieces in a new list
     *
     * @return list with all pieces (line order)
     */
    public List<Integer> getPieces() {
        List<Integer> list = new ArrayList<>();
        for (int line = 0; line < N_LINES; line++) {
            for (int col = 0; col < N_COLS; col++) {
                list.add(this.pieces[line][col]);
            }
        }
        return list;
    }

    public void pieceSelected(Position pos) {
        this.movePieceAt(pos);
    }

    public void keyPressed(Direction direction) {
        Position pos = getPositionNextToEmpty(direction);
        this.movePieceAt(pos);
    }

    private Position getPositionNextToEmpty(Direction direction) {
        switch (direction) {
            case UP: return new Position(emptyPosition.getLine() + 1, emptyPosition.getCol());
            case DOWN: return new Position(emptyPosition.getLine() - 1, emptyPosition.getCol());
            case LEFT: return new Position(emptyPosition.getLine(), emptyPosition.getCol() + 1) ;
            case RIGHT: return new Position(emptyPosition.getLine(), emptyPosition.getCol() - 1);
        }
        return null; // should never happen! Added to avoid compilation error
    }

    /**
     * Tries to move a piece at position If moved notifies views
     *
     * @param position position of piece to move
     * @return true if moved, false otherwise
     */
    private void movePieceAt(Position position) {
        if (position.isInside()) {
            Position emptyPos = this.getEmptyInNeighborhood(position);
            if (emptyPos != null) {
                Move newMove = new Move(position, emptyPos);
                this.applyMove(newMove);
                this.moves.addFirst(newMove); // add at head (begin) of deque
                boolean winning = inWinningPositions();
                this.notifyViews(newMove, winning, timerValue);
                if (winning) {
                    timerValue = 0;
                    timer.cancel();
                }
            }
        }
    }

    /**
     * Notify observers using methods inherited from from class Observable
     *
     * @param move    the executed move
     * @param winning true if this is a winning position
     * @param tValue  current time count
     */
    private void notifyViews(Move move, Boolean winning, int tValue) {

        this.view.notifyView(move, winning, tValue);
    }

    /**
     * Gets last executed move
     *
     * @return the last move
     */
    public Move getLastMove() {
        return this.moves.getFirst();
    }

    /**
     * Checks if board as all pieces in winning positions
     *
     * @return true if winning positions, false otherwise
     */
    public boolean inWinningPositions() {
        int n = 1;
        final int TOTAL = N_LINES * N_COLS;
        for (int line = 0; line < N_LINES; line++) {
            for (int col = 0; col < N_COLS; col++) {
                if (this.pieces[line][col] != n && n < TOTAL) {
                    return false;
                }
                n++;
            }
        }
        return true;
    }

    /**
     * Applies move m to the board assert(move != null)
     *
     * @param move the move to apply
     * @return true
     */
    private void applyMove(Move move) {
        assert (move != null);
        this.swap(move);
    }

    private void swap(Move move) {
        assert (move.getEnd().equals(emptyPosition));

        this.swap(move.getBegin(), move.getEnd());
        this.emptyPosition = move.getBegin();
    }

    private void swap(Position pInit, Position pEnd) {
        int posXi = pInit.getLine();
        int posYi = pInit.getCol();
        int posXe = pEnd.getLine();
        int posYe = pEnd.getCol();
        this.swap(posXi, posYi, posXe, posYe);
    }

    private void swap(int posXi, int posYi, int posXe, int posYe) {
        int aux = this.pieces[posXe][posYe];
        this.pieces[posXe][posYe] = this.pieces[posXi][posYi];
        this.pieces[posXi][posYi] = aux;
    }

    /**
     * Randomly selects position that can be moved to the empty position
     *
     * @param empty the empty position
     * @return the selected neighbor position
     */
    private Position randomlySelectNeighborOf(Position empty) {
        int line = 0;
        int col = 0;
        do {
            int[] pos = NEIGHBORS[RAND.nextInt(NEIGHBORS.length)];
            line = empty.getLine() + pos[0];
            col = empty.getCol() + pos[1];
        } while (Position.isInside(line, col) == false);
        return new Position(line, col);
    }

    /**
     * Tries to return the empty position in the neighborhood of line col
     *
     * @param center position to find empty in its neighborhood
     * @return the empty position or null if non-existent in the neighborhood
     */
    private Position getEmptyInNeighborhood(Position center) {
        boolean isNeighbor = Math.abs(center.getLine() - emptyPosition.getLine()) == 1 ^
                Math.abs(center.getCol() - emptyPosition.getCol()) == 1;
        return isNeighbor ? emptyPosition : null;
    }

    /**
     * Wait the specified time in milliseconds
     *
     * @param sleepTime
     */
    public static void sleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            // nothing to do
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((moves == null) ? 0 : moves.hashCode());
        result = prime * result + Arrays.deepHashCode(pieces);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FifteenModel other = (FifteenModel) obj;
        if (moves == null) {
            if (other.moves != null)
                return false;
        } else if (!moves.equals(other.moves))
            return false;
        if (!Arrays.deepEquals(pieces, other.pieces))
            return false;
        return true;
    }

    /**
     * Creates a new timer and sets the timer count to zero
     */
    public void resetTimer() {
        this.timerValue = -1;
        this.timer = new Timer();
    }

    /**
     * Starts timer
     */
    public void startTimer() {
        this.resetTimer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timerValue++;
                notifyViews(null, false, timerValue);
            }
        };
        this.timer.schedule(timerTask, 0, 1000);
    }

    /**
     * Stops the current timer
     */
    public void stopTimer() {
        timer.cancel();
    }

    /**
     * Get current timer value
     *
     * @return time in seconds
     */
    public int getTimerValue() {
        return this.timerValue;
    }

}
