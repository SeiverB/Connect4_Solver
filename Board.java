import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class Board {
    
    public static int numColumns = 7;
    public static int numRows = 6;
    
    // TODO: should be calculated programatically
    public static int numDiags = 6;

    public int heuristic = 0;

    public byte toPlay = 1;
    public byte opponent = 2;

    public Byte[] numInEachColumn = new Byte[numColumns];
    public Byte[] board = new Byte[((numColumns + 1) * (numRows + 2)) + 1];

    // heuristics for each row, column, and diag.
    public byte[] rowH = new byte[numRows];
    public byte[] colH = new byte[numColumns];
    public byte[] diagPH = new byte[numDiags];
    public byte[] diagNH = new byte[numDiags];
    
    public Boolean gameOver = false;

    // Used for ordering moves from center, outwards.
    public static int[] moveOrder = {3, 4, 2, 5, 1, 6, 0};

    public static int[] neighbourOffsets = {1, -1, Board.numColumns + 1, -Board.numColumns - 1, Board.numColumns, -Board.numColumns, -Board.numColumns - 2, Board.numColumns + 2};

    Board(){

    }


    // TODO: Implement functionality for this to also detect 3-in-a-rows
    // Should return a list of moves that create 3-in-a-rows for re-ordering. 
    public LinkedList<Integer> orderMoves(LinkedList<Integer> moves){

        LinkedList<Integer> results = new LinkedList<Integer>();

        for(int j = 0; j < moves.size(); j++){

            int col = moves.get(j);
            
            // Find relevant row that the piece will fall on, accounting for pieces already in the column
            int row = Board.numRows - this.numInEachColumn[col];
            int length = 1;
            boolean firstDir = true;

            // Get the index of where the piece landed
            int index = 1 + (row) * (Board.numColumns + 1) + col;

            for(int i = 0; i < neighbourOffsets.length; i++){
                int curOffset = neighbourOffsets[i];
                int curIndex = index + curOffset;
                while(board[curIndex] == toPlay){
                    length++;
                    if(length == 3){
                        // There may be a chance to make a three-in-a-row, make this move the front of the list.
                        moves.remove(j);
                        moves.addFirst(col);
                    }
                    else if(length == 4){
                        // If we have a winning opportunity, return linked list containing only the winning move
                        results.add(col);
                        return results;
                    }
                    
                    curIndex += curOffset;
                }
                if(firstDir){
                    firstDir = false;
                }
                else{
                    firstDir = true;
                    length = 1;
                }
            }
            
        }
        // No 4-in-a-rows found, return empty results
        return results;
    }

    // order moves from center, outwards.
    public LinkedList<Integer> getPossibleMoves(){

        LinkedList<Integer> result = new LinkedList<Integer>();

        // old move ordering, from center outwards. 

        for(int i = 0; i < numColumns; i++){
            int a = Board.moveOrder[i];
            if(numInEachColumn[a] < numRows){
                result.add(a);
            }
        }
        /*
        for(int i = 0; i < numColumns; i++){
            if(numInEachColumn[i] < numRows){
                result.add(i);
            }
        }
        */

        return result;
    }


    public void initializeValues(){

        // Initialize array keeping track of number of pieces in each vertical column
        for(int i = 0; i < numColumns; i++){
            numInEachColumn[i] = (byte)0;
        }

        // Intialize heuristic arrays to 0
        Arrays.fill(rowH, (byte)0);
        Arrays.fill(colH, (byte)0);
        Arrays.fill(diagPH, (byte)0);
        Arrays.fill(diagNH, (byte)0);

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

    // TODO: this is hardcoded for our current gridSize. Needs fixing to extrapolate to all like the rest of the code
    public int getDiagP(int row, int col){
        int result = row + col - 3;
        if((result >= 0) && (result <= 5)){
            return result;
        }
        return -1;
    }

    public int getDiagN(int row, int col){
        int result = row - col + 3;
        if((result >= 0) && (result <= 5)){
            return result;
        }
        return -1;
    }

    // returns a clone of the board with the move made
    public Board makeMove(int col){

        Board newBoard = new Board();
        newBoard.numInEachColumn = this.numInEachColumn.clone();
        
        newBoard.board = this.board.clone();
        
        // Clone and invert all heuristics
        newBoard.colH = cloneAndInvert(colH);
        newBoard.rowH = cloneAndInvert(rowH);
        newBoard.diagPH = cloneAndInvert(diagPH);
        newBoard.diagNH = cloneAndInvert(diagNH);

        newBoard.heuristic = -this.heuristic;

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

        updateHeuristicForMove(newBoard, row, col);

        // Check if newboard has gameOver, if so we don't need to calculate anything further.
        if(newBoard.gameOver){
            newBoard.heuristic = -1000;
            return newBoard;
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
        return this.heuristic;
    }

    // Method to clone an array and invert the signs of every entry
    public byte[] cloneAndInvert(byte[] originalArray) {
        byte[] clonedArray = new byte[originalArray.length];
        for (int i = 0; i < originalArray.length; i++) {
            clonedArray[i] = (byte)-originalArray[i];
        }
        return clonedArray;
    }

    private void updateHeuristicForMove(Board newBoard, int row, int col){
        
        // new total for heuristic for row/col/diags
        int total = 0;

        // now col indexed starting 0, row indexed starting 0
        row = row - 1;

        // Get old heuristic for this set of rows/cols/diags
        int oldHeuristic = -this.colH[col] - this.rowH[row];

        int diagPIndex = getDiagP(row, col);
        int diagNIndex = getDiagN(row, col);

        if(diagPIndex != -1){
            oldHeuristic -= this.diagPH[diagPIndex];
        }

        if(diagNIndex != -1){
            oldHeuristic -= this.diagNH[diagNIndex];
        }        

        // recalculate row
        int startIndex = 1 + (Board.numColumns + 1) * (row + 1);
        int endIndex = startIndex + Board.numColumns;

        int rowResult = -newBoard.getHeuristicForSequence_v2(startIndex, endIndex, 1, newBoard.opponent);
        if(rowResult == -1000){
            newBoard.gameOver = true;
            return;
        }
        rowResult += newBoard.getHeuristicForSequence_v2(startIndex, endIndex, 1, newBoard.toPlay);
        newBoard.rowH[row] = (byte)rowResult;

        total += rowResult;

        // recalculate col
        startIndex = col + Board.numColumns + 2;
        int iterator = Board.numColumns + 1;
        endIndex = startIndex + (Board.numRows)*iterator;
        int colResult = -newBoard.getHeuristicForSequence_v2(startIndex, endIndex, iterator, newBoard.opponent);
        if(colResult == -1000){
            newBoard.gameOver = true;
            return;
        }
        colResult += newBoard.getHeuristicForSequence_v2(startIndex, endIndex, iterator, newBoard.toPlay);
        newBoard.colH[col] = (byte)colResult;
        total += colResult;

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
            int diagPResult = -newBoard.getHeuristicForSequence_v2(startIndex, endIndex, iterator, newBoard.opponent);
            if(diagPResult == -1000){
                newBoard.gameOver = true;
                return;
            }
            diagPResult += newBoard.getHeuristicForSequence_v2(startIndex, endIndex, iterator, newBoard.toPlay);
            newBoard.diagPH[diagPIndex] = (byte)diagPResult;
            total += diagPResult;
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
            int diagNResult = -newBoard.getHeuristicForSequence_v2(startIndex, endIndex, iterator, newBoard.opponent);
            if(diagNResult == -1000){
                newBoard.gameOver = true;
                return;
            }
            diagNResult += newBoard.getHeuristicForSequence_v2(startIndex, endIndex, iterator, newBoard.toPlay);
            newBoard.diagNH[diagNIndex] = (byte)diagNResult;
            total += diagNResult;
        }

        // total is the new value of the row/col/diags due to the new piece, relative to the new board.
        newBoard.heuristic -= (oldHeuristic - total);

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
