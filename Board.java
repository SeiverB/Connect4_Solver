import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Board {
    
    public static int numColumns = 7;
    public static int numRows = 6;
    
    // TODO: should be calculated programatically
    public static int numDiags = 6;

    public int whiteHeuristic = 0;
    public int blackHeuristic = 0;

    public byte toPlay = 1;
    public byte opponent = 2;

    public Byte[] numInEachColumn = new Byte[numColumns];
    public Byte[] board = new Byte[((numColumns + 1) * (numRows + 2)) + 1];

    
    public Boolean gameOver = false;

    // Used for ordering moves from center, outwards.
    // public static int[] moveOrder = {3, 4, 2, 5, 1, 6, 0};

    Board(){

    }

    // order moves from center, outwards.
    public ArrayList<Integer> getPossibleMoves(){

        ArrayList<Integer> result = new ArrayList<Integer>();

        /* old move ordering, from center outwards. 
        Replaced with better move ordering based on heuristic

        for(int i = 0; i < numColumns; i++){
            int a = Board.moveOrder[i];
            if(numInEachColumn[a] < numRows){
                result.add(a);
            }
        }*/

        for(int i = 0; i < numColumns; i++){
            if(numInEachColumn[i] < numRows){
                result.add(i);
            }
        }

        return result;
    }


    public void initializeValues(){

        // Initialize array keeping track of number of pieces in each vertical column
        for(int i = 0; i < numColumns; i++){
            numInEachColumn[i] = (byte)0;
        }

        // Initialize array which caches the last-known heuristic of each position
        for(int i = 0; i < (numColumns * numRows); i++){
            friendlyHeus[i] = (byte)0;
            enemyHeus[i] = (byte)0;
        }

        int a = 1 + ((numColumns + 1) * (numRows + 1));
        for(int i = 0; i < board.length; i++){
            if((i < (numColumns + 2)) || (i % (numColumns + 1) == 0) || (i >= a)){
                board[i]= -1;
            }
            else{
                board[i] = 0;
            }
        }
    }
    
    public void changePlayer(){
        byte temp = toPlay;
        toPlay = opponent;
        opponent = temp;
    } 

    // returns a clone of the board with the move made
    public Board makeMove(int col){

        Board newBoard = new Board();
        newBoard.numInEachColumn = this.numInEachColumn.clone();
        newBoard.friendlyHeus = this.enemyHeus.clone();
        newBoard.enemyHeus = this.friendlyHeus.clone();
        newBoard.board = this.board.clone();
        
        // Find relevant row that the piece will fall on, accounting for pieces already in the column
        int row = Board.numRows - newBoard.numInEachColumn[col];

        // Get the index of where the piece landed
        int index = 1 + (row) * (Board.numColumns + 1) + col;

        // Place chip into newBoard
        newBoard.board[index] = toPlay;
        newBoard.numInEachColumn[col]++;

        // Reverse toPlay and opponent for newBoard
        newBoard.toPlay = opponent;
        newBoard.opponent = toPlay;

        // The index that will be used to read old heuristic values for given [row/col/diags] set
        int heuIndex = col + (row-1)*numColumns;

        // (1) Calculate difference in Heuristic for the friendly pieces
        int newHeuristic = newBoard.getHeuristicForMove(row, col, toPlay);

        // Check if new board has gameOver, if so we don't need to calculate anything further.
        if(newBoard.gameOver){
            if(toPlay == 1){
            newBoard.whiteHeuristic = 1000;
            newBoard.blackHeuristic = 0;
            }
            else{
                newBoard.whiteHeuristic = 0;
                newBoard.blackHeuristic = 1000;
            }
            return newBoard;
        }

        newBoard.enemyHeus[heuIndex] = (byte)newHeuristic;

        // The old heuristic from the current board that will be applied to the new board
        // We use the ENEMY heuristic from the old board, as we are updating the NEW board's friendly value.
        // As the old board's enemy value = The new board's friendly value.
        int oldHeuristic = this.enemyHeus[heuIndex];
        int friendlyHeuristic = (newHeuristic - oldHeuristic);

        //System.out.println("Friendly old: " + oldHeuristic);
        //System.out.println("Friendly new: " + newHeuristic);
        //System.out.println("Change: " + friendlyHeuristic);

        // (2) Calculate difference in Heuristic for enemy pieces

        // We use the FRIENDLY heuristic from the OLD board, as we are updating the NEW board's ENEMY value.
        // As the old board's friendly value = The new board's enemy value.
        oldHeuristic = this.friendlyHeus[heuIndex];
        newHeuristic = newBoard.getHeuristicForMove(row, col, opponent);
        int enemyHeuristic = (newHeuristic - oldHeuristic);
        
        newBoard.friendlyHeus[heuIndex] = (byte)newHeuristic;

        //System.out.println("Enemy old: " + oldHeuristic);
        //System.out.println("Enemy new: " + newHeuristic);
        //System.out.println("Change: " + enemyHeuristic);

        if(toPlay == 1){
            newBoard.whiteHeuristic += friendlyHeuristic + this.whiteHeuristic;
            newBoard.blackHeuristic += enemyHeuristic + this.blackHeuristic;
        }
        else{
            newBoard.whiteHeuristic += enemyHeuristic + this.whiteHeuristic;
            newBoard.blackHeuristic += friendlyHeuristic + this.blackHeuristic;
        }

        return newBoard;
    }
    

    public String toString(){
        String result = "";
        String a = "";
        String b = "";
        for(int i = 0; i < board.length; i++){
            a += String.format("|%2d", board[i]);
            b += String.format("|%2d", i);
            if((i % (numColumns + 1) == 0) && (i != 0)){
                result += (a + "|      " + b);
                result += "|\n   ";
                a = "";
                b = "   ";
            }
        }
        result += "\n";
        return result;
    }

    /*
    private float getHeuristicForSequence(int start, int end, int iterator, byte move){
        float adjacencyValues[] = {0f, 0.1f, 0.3f, 0.9f, 0.8f, 3.0f, 6.0f, 12.0f};
        float rowresult = 0;
        int numOurs = 0;
        int length = 0;
        int lengthSinceOurs = 0;
        int adjacent = 0;
        boolean foundOurs = false;
        for(int i = start; i < end; i = (i + iterator)){
            byte tileValue = this.board[i];

            // If we go two empty spaces, then consider rest seperate from current
            if((lengthSinceOurs >= 2) && (length >= 4)){
                rowresult += adjacencyValues[numOurs];
                numOurs = 0;
                length = 1;
                lengthSinceOurs = 0;
                foundOurs = false;
            }

            // If tile is same colour as the move made (currentPlayer)
            if(tileValue == move){
                lengthSinceOurs = 0;
                foundOurs = true;
                adjacent += 1;
                numOurs += 1;
                length += 1;
            }
            // If tile is empty, 4-in-a-row may still be possible, increase length without increasing numOurs
            // adjacent resets.
            else if(tileValue == 0){
                length += 1;
                adjacent = 0;
                if(foundOurs){
                    lengthSinceOurs += 1;
                }
            }
            // If tile is enemy and length is less than 4, then 4-in-a-row not possible.
            else if(length < 4){
                numOurs = 0;
                length = 0;
                lengthSinceOurs = 0;
                foundOurs = false;
                adjacent = 0;
                continue;
            }
            // We have reached enemy, but length is at least 4 or greater
            else{
                rowresult += adjacencyValues[numOurs];
                adjacent = 0;
                numOurs = 0;
                length = 0;
                lengthSinceOurs = 0;
                foundOurs = false;
            }

            // We have won, return max heuristic value.
            if(adjacent == 4){
                this.gameOver = true;
                return 100f;
            }

        }
        // Reached row end
        // dont forget to add last adjacent if length >= 4.
        if(length >= 4){
            rowresult += adjacencyValues[numOurs];
        }

        float result = rowresult;
        return result;

    }
    */

    private int getHeuristicForSequence_v2(int start, int end, int iterator, byte move){
        int adjacencyValues[] = {0, 1, 2, 3, 5, 4, 9};
        int rowresult = 0;
        
        int left_freedom = 0;

        int num_adjacent = 0;
        int free_space = 0;
        int cached_group = 0;

        for(int i = start; i <= end; i = (i + iterator)){
            byte tileValue = this.board[i];

            // If tile is empty
            if(tileValue == 0){
                
                free_space += 1;

                if(num_adjacent == 0){
                    left_freedom = 1;
                    continue;
                }

                if(free_space < 4){
                    cached_group = ((num_adjacent - 1) * 2) + left_freedom + 1;
                }
                else{
                    rowresult += adjacencyValues[(num_adjacent - 1) * 2 + left_freedom + 1];
                }
                
                num_adjacent = 0;
                left_freedom = 1;
            }
            // If tile is same colour as move made
            else if(tileValue == move){
                num_adjacent += 1;
                free_space += 1;

                // We have won, return max heuristic value.
                if(num_adjacent >= 4){
                    this.gameOver = true;
                    return 1000;
                }
            }
            // If tile is enemy or wall
            else{

                if(free_space >= 4){
                    rowresult += adjacencyValues[cached_group];
                    if(num_adjacent > 0){
                        rowresult += adjacencyValues[(num_adjacent - 1) * 2 + 1];
                    }
                }
                cached_group = 0;
                num_adjacent = 0;
                left_freedom = 0;
                free_space = 0;
            }
        }

        int result = rowresult;
        return result;
    }

    public int evaluate(){
        if(toPlay == 1){
            return this.whiteHeuristic - this.blackHeuristic;
        }
        else{
            return this.blackHeuristic - this.whiteHeuristic;
        }
    }

    /*
    private int updateHeuristicForMove(Board oldBoard, int row, int col){
        
        int enemyNewTotal = 0;
        int enemyOldTotal = 0;
        int friendlyNewTotal = 0;
        int friendlyOldTotal = 0;

        int result = 0;

        // in here, col indexed starting 0, row indexed starting 1 btw

        // recalculate row
        int startIndex = 1 + (Board.numColumns + 1) * (row);
        int endIndex = startIndex + Board.numColumns;

        result = oldBoard.getHeuristicForSequence_v2(startIndex, endIndex, 1, move);
        if(result == 100){
            return 100;
        }
        total += result;

        // recalculate col
        startIndex = col + Board.numColumns + 2;
        int iterator = Board.numColumns + 1;
        endIndex = startIndex + (Board.numRows)*iterator;
        result = getHeuristicForSequence_v2(startIndex, endIndex, iterator, move);
        if(result == 100){
            return 100;
        }
        total += result;

        // now col indexed starting 0, row indexed starting 0 btw
        row = row - 1;

        // recalculate diag(+) if present

        // How many cells are diagonally going up right
        int a = Math.min(numColumns - (col + 1), row);
        // How many cells are diagonally going down left
        int b = Math.min(numRows - (row + 1), col);

        // If we have 4 diagonal tiles, including current tile
        if((a + b + 1) >= 4){
            int diagCol = col + a;
            int diagRow = row - a;
            startIndex = 2 + Board.numColumns + diagCol + (diagRow * (Board.numColumns + 1));
            diagCol = col - b - 1;
            diagRow = row + b + 1;
            endIndex = 2 + Board.numColumns + diagCol + (diagRow * (Board.numColumns + 1));
            iterator = Board.numColumns;
            result = getHeuristicForSequence_v2(startIndex, endIndex, iterator, move);
            if(result == 100){
                return 100;
            }
            total += result;
        }

        // recalculate diag(-) if present
        // How many cells are diagonally going up left
        a = Math.min(col, row);  
        // How many cells are diagonally going down right
        b = Math.min(numRows - (row + 1), numColumns - (col+1));
        if((a + b + 1) >= 4){
            int diagCol = col - a;
            int diagRow = row - a;
            startIndex = 2 + Board.numColumns + diagCol + (diagRow * (Board.numColumns + 1));
            diagCol = col + b + 1;
            diagRow = row + b + 1;
            endIndex = 2 + Board.numColumns + diagCol + (diagRow * (Board.numColumns + 1));
            iterator = Board.numColumns + 2;
            result = getHeuristicForSequence_v2(startIndex, endIndex, iterator, move);
            if(result == 100){
                return 100;
            }
            total += result;
        }

        return total;

    }
    */

    private int getHeuristicForMove(int row, int col, byte move){
        int total = 0;
        int result = 0;

        // in here, col indexed starting 0, row indexed starting 1 btw

        // recalculate row
        int startIndex = 1 + (Board.numColumns + 1) * (row);
        int endIndex = startIndex + Board.numColumns;

        result = getHeuristicForSequence_v2(startIndex, endIndex, 1, move);
        if(result == 1000){
            return 1000;
        }
        total += result;
        
        // recalculate col
        startIndex = col + Board.numColumns + 2;
        int iterator = Board.numColumns + 1;
        endIndex = startIndex + (Board.numRows)*iterator;
        result = getHeuristicForSequence_v2(startIndex, endIndex, iterator, move);
        if(result == 1000){
            return 1000;
        }
        total += result;

        // now col indexed starting 0, row indexed starting 0 btw
        row = row - 1;

        // recalculate diag(+) if present

        // How many cells are diagonally going up right
        int a = Math.min(numColumns - (col + 1), row);
        // How many cells are diagonally going down left
        int b = Math.min(numRows - (row + 1), col);

        // If we have 4 diagonal tiles, including current tile
        if((a + b + 1) >= 4){
            int diagCol = col + a;
            int diagRow = row - a;
            startIndex = 2 + Board.numColumns + diagCol + (diagRow * (Board.numColumns + 1));
            diagCol = col - b - 1;
            diagRow = row + b + 1;
            endIndex = 2 + Board.numColumns + diagCol + (diagRow * (Board.numColumns + 1));
            iterator = Board.numColumns;
            result = getHeuristicForSequence_v2(startIndex, endIndex, iterator, move);
            if(result == 1000){
                return 1000;
            }
            total += result;
        }

        // recalculate diag(-) if present
        // How many cells are diagonally going up left
        a = Math.min(col, row);  
        // How many cells are diagonally going down right
        b = Math.min(numRows - (row + 1), numColumns - (col+1));
        if((a + b + 1) >= 4){
            int diagCol = col - a;
            int diagRow = row - a;
            startIndex = 2 + Board.numColumns + diagCol + (diagRow * (Board.numColumns + 1));
            diagCol = col + b + 1;
            diagRow = row + b + 1;
            endIndex = 2 + Board.numColumns + diagCol + (diagRow * (Board.numColumns + 1));
            iterator = Board.numColumns + 2;
            result = getHeuristicForSequence_v2(startIndex, endIndex, iterator, move);
            if(result == 1000){
                return 1000;
            }
            total += result;
        }

        return total;
    }


    public void drawBoard(Graphics g, int x, int y, int width, int height){
        int cellWidth = width / numColumns;
        int cellHeight = height / numRows;
        int size = numColumns * numRows;
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        float xoffset = x;
        float yoffset = y;
        for(int i = 0; i < size; i++){
            int a = Math.floorDiv(i, numColumns);
            a = i + (Board.numColumns + 2) + a;
            switch(this.board[a]){
                case 0:
                    g.setColor(Color.DARK_GRAY);
                    break;
                case 1:
                    g.setColor(Color.WHITE);
                    break;
                case 2:
                    g.setColor(Color.BLACK);
                    break;
            }
            g.fillOval((int)xoffset, (int)yoffset, cellWidth, cellHeight);
            xoffset += cellWidth;
            if((i+1) % numColumns == 0){
                yoffset += cellHeight;
                xoffset = x;
            }
        }

    }

}
