import java.util.ArrayList;

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
        int bestScore = -999999999;

        // Go through each move
        ArrayList<Integer> moves = board.getPossibleMoves();

        for(int i = 0; i < moves.size(); i++){
            int curMove = moves.get(i);
            if(board.fastCheckIfWin(curMove)){
                return new ScoreMove(1000, curMove);
            }
        }

        for(int i = 0; i < moves.size(); i++){
            int move = moves.get(i);
            Board newBoard = board.makeMove(move);

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