import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList; // For arbitrary size lists 

public class Connect4 extends JPanel implements MouseListener, MouseMotionListener{
    
    static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 650;


    // Game board
    private Board board;

    // Board dimensions
    public int boardx, boardy, boardwidth, boardheight;

    // Negamax
    private ABNegamax negamax;
    
    public Boolean playerTurn = true;

    public int mousex = 0;
    public int mousey = 0;

    public int gameState = 0;

    public Connect4(){

        //listen for mouse events (clicks and movements) on this object
        addMouseMotionListener(this);
        addMouseListener(this);
        this.boardx = 0;
        this.boardy = 0;
        this.boardwidth = WINDOW_WIDTH;
        this.boardheight = WINDOW_HEIGHT;

    }

    public void drawCenteredString(Graphics g, int posx, int posy, String text, Font font){
        FontMetrics metrics = g.getFontMetrics(font);
        g.setFont(font);
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();
        g.drawString(text, Math.round(posx - (width / 2)), Math.round(posy + (height / 2)));
    }

    public void startGame(){
        int[] moveorder = {3, 4, 2, 5, 1, 6, 0};
        Board.moveOrder = moveorder;
        this.board = new Board();
        this.board.initializeValues();
        this.negamax = new ABNegamax(12);

    }

    public void playMove(int column){

        if((gameState == 0) && checkValidMove(column)){
            this.board = this.board.makeMove(column);
            //doAIMove();
            checkIfGameOver();
            playerTurn = false;
        }
        System.out.printf("White Score: %f Black Score: %f\n", this.board.whiteHeuristic, this.board.blackHeuristic);

        if((gameState == 0) && playerTurn == false){
            doAIMove();
            checkIfGameOver();
        }
        
        System.out.printf("White Score: %f Black Score: %f\n", this.board.whiteHeuristic, this.board.blackHeuristic);

    }

    public Boolean checkValidMove(int move){
        Boolean validMove = false;
        ArrayList<Integer> moves = this.board.getPossibleMoves();
        for(int i = 0; i < moves.size(); i++){
            if(move == moves.get(i)){
                validMove = true;
            }
        }
        return validMove;
    }

    public void checkIfGameOver(){
        if(this.board.gameOver){
            this.gameState = this.board.opponent;
        }
    }

    public void doAIMove(){
        ScoreMove result = this.negamax.getBestMove(this.board, 0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        Integer move = null;
        // The AI prolly gave up, just try and play something that won't immediately lose. Stall.
        if(result.bestMove == null){
            int temp = this.negamax.maxDepth;
            this.negamax.maxDepth = 4;
            result = this.negamax.getBestMove(this.board, 0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
            move = result.bestMove;
            this.negamax.maxDepth = temp;
            if(move == null){
                ArrayList<Integer> moves = this.board.getPossibleMoves();
                move = moves.get(0);
            }
        }
        else{
            move = result.bestMove;
        }
        this.board = this.board.makeMove(move);
        playerTurn = true;
    }

    public void resetGame(){
        this.board = new Board();
        this.board.initializeValues();
        this.playerTurn = true;
    }

    // Redraws the graphics on the game window
    public void paintComponent(Graphics g){
        // Set background to black
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WINDOW_WIDTH, 800);

        this.board.drawBoard(g, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        if(gameState == 1){
            g.setColor(Color.RED);
            drawCenteredString(g, WINDOW_WIDTH/2, WINDOW_HEIGHT/2, "You Win", new Font("Arial Black", Font.BOLD, 40));
        }
        else if(gameState == 2){
            g.setColor(Color.RED);
            drawCenteredString(g, WINDOW_WIDTH/2, WINDOW_HEIGHT/2, "Game Over", new Font("Arial Black", Font.BOLD, 40));
        }
    }

    // Capture mouse drag events
    @Override
    public void mouseDragged(MouseEvent e) {
        this.mousex = e.getX();
        this.mousey = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e){
        mousex = e.getX();
        mousey = e.getY();
        int localx = Math.clamp(mousex - boardx, 0, WINDOW_WIDTH);
        int column = (int)Math.floor(((float)localx / (float)boardwidth) * (float)board.numColumns);

        if(gameState != 0){
            resetGame();
            this.gameState = 0;
        }
        else if(playerTurn){
            playMove(column);
        }
        

    }

    // Capture mouse move events
    @Override
    public void mouseMoved(MouseEvent e) {
        this.mousex = e.getX();
        this.mousey = e.getY();
    }

    @Override
    public void mouseExited(MouseEvent e){
    }

    @Override
    public void mouseEntered(MouseEvent e){
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

}