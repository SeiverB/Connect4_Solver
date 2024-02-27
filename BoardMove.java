public class BoardMove {
    public final Board board;
    public final int move;

    public BoardMove(Board board, int move) {
        this.board = board;
        this.move = move;
    }

    public String toString(){
        return "[BoardMove] Move: " + move + " Score: " + this.board.evaluate();
    }

}
