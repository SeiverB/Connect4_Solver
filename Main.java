import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

	//declare and initialize the frame
    static JFrame f = new JFrame("Connect 4");
    
    public static int DELAY = 17; // Frame time in milliseconds

    private static Timer frameTimer;

    // Game Object
    private static Connect4 game;

    public static void main(String[] args) {

		//make it so program exits on close button click
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Create game area
        Main.game = new Connect4();
        Main.game.startGame();

        // Add game object to frame
        f.add(game);

        //add a frame timer object
        frameTimer = new Timer(DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //repaint the screen
                game.repaint();
                
            }
        });

        // Start frame timer once it is initialized
        Main.frameTimer.start();
        
        //the size of the game will be 810x810, the size of the JFrame needs to be slightly larger
        f.setSize(816,690);

        f.setLocationRelativeTo(null);

		//show the window
        f.setVisible(true);


        /* TESTGAME
        Connect4 game2 = new Connect4();
        game2.startGame();
        game2.playMove(3);
        System.out.printf("white heuristic: %f\n",game2.board.whiteHeuristic);
        game2.playMove(0);
        game2.playMove(3);
        System.out.printf("white heuristic: %f\n",game2.board.whiteHeuristic);
        game2.playMove(3);
        System.out.printf("white heuristic: %f\n",game2.board.whiteHeuristic);
        */
	}

}