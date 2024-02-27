import java.util.Comparator;

public class BoardMoveComparator implements Comparator<BoardMove> {

    @Override
    public int compare(BoardMove o1, BoardMove o2) {
        return Integer.compare(o2.board.evaluate(), o1.board.evaluate());
    }

}
