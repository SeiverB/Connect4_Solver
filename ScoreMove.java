public class ScoreMove {
    
    public float bestScore;
    public Integer bestMove;

    public ScoreMove(float bestScore, Integer bestMove){
        this.bestScore = bestScore;
        this.bestMove = bestMove;
    }

    public String toString(){
        return "[ScoreMove] Score: " + this.bestScore + " Move: " + this.bestMove;
    }

}
