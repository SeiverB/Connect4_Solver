## LOGIC / DESIGN

This game allows you to play connect4 against an AI opponent.
By clicking any of the columns on the screen, you can place a white piece onto the board.
The opponent will then follow-up with a calculated response based on a depth-limited alphabeta-minimax 
search. 
The heuristic assigns scores according to how many adjacent pieces of the same colour are present, and if
they are still able to create 4-in-a-row. 
The board itself is represented by a padded matrix, where the borders are given a special sentinel value, 
which indicates if a value is out of bound when attempting to find neighbours. This allows us to search
across the board without expensive bounds-checking, which could slow down execution due to more branching.
Each time a move is made, the heuristic only updates the necessary rows/columns/diagonals, for a maximum
of four being updated. This saves on execution time, as we don't need to update every single row, column
and diagonal present on the board for every move. 

## HOW TO COMPILE / RUN

To run game, simply execute .jar file from the releases page.

To compile game from source into .jar file, ensure you have java jdk installed, 
then open up your command-line interface.
navigate to the "source" folder included in this zip file, and run the following 2 commands:


javac *.java

jar cvfm game.jar MANIFEST.MF *.class


the .jar file may then be executed from the command line by: java -jar game.jar



## BUGS

Though the opponent is found to be strong, being depth-limited means that it does not play perfectly.
