import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Board {
    
    public int numColumns = 7;
    public int numRows = 6;

    public float whiteHeuristic = 0;
    public float blackHeuristic = 0;

    public byte toPlay = 1;
    public byte opponent = 2;

    public Byte[] numInEachColumn = new Byte[numColumns];
    public Byte[] board = new Byte[((numColumns + 1) * (numRows + 2)) + 1];

    public Boolean gameOver = false;

    public static int[] moveOrder;

    Board(){

    }

    // order moves from center, outwards.
    public ArrayList<Integer> getPossibleMoves(){
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(int i = 0; i < numColumns; i++){
            int a = Board.moveOrder[i];
            if(numInEachColumn[a] < numRows){
                result.add(a);
            }
        }
        return result;
    }


    public void initializeValues(){

        for(int i = 0; i < numColumns; i++){
            numInEachColumn[i] = (byte)0;
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
    public Board makeMove(int x){

        Board newBoard = new Board();
        newBoard.numInEachColumn = this.numInEachColumn.clone();
        newBoard.board = this.board.clone();
        int row = newBoard.numRows - newBoard.numInEachColumn[x];
        int index = 1 + (row) * (newBoard.numColumns + 1) + x;
        newBoard.board[index] = toPlay;
        newBoard.numInEachColumn[x]++;
        float newHeuristic = newBoard.getHeuristicForMove(row, x, toPlay);
        
        if(newBoard.gameOver){
            if(toPlay == 1){
            newBoard.whiteHeuristic = 100.0f;
            newBoard.blackHeuristic = 0.0f;
            }
            else{
                newBoard.whiteHeuristic = 0.0f;
                newBoard.blackHeuristic = 100.0f;
            }
            newBoard.toPlay = opponent;
            newBoard.opponent = toPlay;
            return newBoard;
        }
        
        
        float oldHeuristic = this.getHeuristicForMove(row, x, toPlay);
        float friendlyHeuristic = (newHeuristic - oldHeuristic);

        //System.out.println("Friendly old: " + oldHeuristic);
        //System.out.println("Friendly new: " + newHeuristic);
        //System.out.println("Change: " + friendlyHeuristic);

        oldHeuristic = this.getHeuristicForMove(row, x, opponent);
        newHeuristic = newBoard.getHeuristicForMove(row, x, opponent);
        float enemyHeuristic = (newHeuristic - oldHeuristic);

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

        newBoard.toPlay = opponent;
        newBoard.opponent = toPlay;

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

    private float getHeuristicForSequence_v2(int start, int end, int iterator, byte move){
        float adjacencyValues[] = {0f, 0.1f, 0.2f, 0.3f, 0.5f, 0.4f, 0.9f};
        float rowresult = 0;
        
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
                    return 100f;
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

        float result = rowresult;
        return result;
    }

    public float evaluate(){
        if(toPlay == 1){
            return this.whiteHeuristic - this.blackHeuristic;
        }
        else{
            return this.blackHeuristic - this.whiteHeuristic;
        }
    }

    private float getHeuristicForMove(int row, int col, byte move){
        float total = 0;
        float result = 0;

        // in here, col indexed starting 0, row indexed starting 1 btw

        // recalculate row
        int startIndex = 1 + (this.numColumns + 1) * (row);
        int endIndex = startIndex + this.numColumns;
        int iterator = 1;

        result = getHeuristicForSequence_v2(startIndex, endIndex, iterator, move);
        if(result == 100){
            return 100;
        }
        total += result;
        
        // recalculate col
        startIndex = col + this.numColumns + 2;
        iterator = this.numColumns + 1;
        endIndex = startIndex + (this.numRows)*iterator;
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
            startIndex = 2 + this.numColumns + diagCol + (diagRow * (this.numColumns + 1));
            diagCol = col - b - 1;
            diagRow = row + b + 1;
            endIndex = 2 + this.numColumns + diagCol + (diagRow * (this.numColumns + 1));
            iterator = this.numColumns;
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
            startIndex = 2 + this.numColumns + diagCol + (diagRow * (this.numColumns + 1));
            diagCol = col + b + 1;
            diagRow = row + b + 1;
            endIndex = 2 + this.numColumns + diagCol + (diagRow * (this.numColumns + 1));
            iterator = this.numColumns + 2;
            result = getHeuristicForSequence_v2(startIndex, endIndex, iterator, move);
            if(result == 100){
                return 100;
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
            a = i + (this.numColumns + 2) + a;
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
