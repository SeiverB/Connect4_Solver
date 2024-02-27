import java.util.ArrayList;
import java.util.Collections;

public class ABNegamax {

    public int maxDepth;

    public ABNegamax(int maxDepth){
        this.maxDepth = maxDepth;
    }
    
    public ScoreMove getBestMove(Board board, int currentDepth, int alpha, int beta){

        // Check if done recursing
        if(board.gameOver || (currentDepth == this.maxDepth)){
            return new ScoreMove(board.evaluate(), null);
        }

        Integer bestMove = null;
        int bestScore = -99999999;

        // Go through each move
        ArrayList<Integer> moves = board.getPossibleMoves();

        // List of boards and their moves for move Sorting
        ArrayList<BoardMove> boardMoves = new ArrayList<BoardMove>();

        for(int i = 0; i < moves.size(); i++){
            int move = moves.get(i);
            Board newBoard = board.makeMove(move);
            BoardMove newBoardMove = new BoardMove(newBoard, move);
            boardMoves.add(newBoardMove);
        }

        // Sort boardMoves according to their heuristic
        Collections.sort(boardMoves, new BoardMoveComparator());

        /*
        System.out.println("MOVES:");
        for(int i = 0; i < boardMoves.size(); i++){
            System.out.println(boardMoves.get(i));
        }
        */

        for(int i = 0; i < boardMoves.size(); i++){
            BoardMove currentBoardMove = boardMoves.get(i);

            int move = currentBoardMove.move;
            Board newBoard = currentBoardMove.board;
            
            // Immediately prune result if a winning move is playable
            if(newBoard.gameOver == true){
                return new ScoreMove(1000, move);
            }

            // Recurse
            ScoreMove result = getBestMove(newBoard, currentDepth + 1, -beta, -Math.max(alpha, bestScore));
            int currentScore = -result.bestScore; 
            
            // Update best score if we find better
            if(currentScore > bestScore){
                bestScore = currentScore;
                bestMove = move;
            }

            // If we are outside the bounds, prune by exiting immediately.
            if(bestScore >= beta){
                break;
            }
        }

        if(currentDepth == 1){
            System.out.println(new ScoreMove(bestScore, bestMove));
        }

        return new ScoreMove(bestScore, bestMove);
    }

}