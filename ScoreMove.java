public class ScoreMove {
    
    public int bestScore;
    public Integer bestMove;

    public ScoreMove(int bestScore, Integer bestMove){
        this.bestScore = bestScore;
        this.bestMove = bestMove;
    }

    public String toString(){
        return "[ScoreMove] Score: " + this.bestScore + " Move: " + this.bestMove;
    }

}
