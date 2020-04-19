// Board.java

import java.util.HashSet;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
 */
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean[][] backup;
	private boolean DEBUG = true;
	boolean committed;


	// Here a few trivial methods are provided:

	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	 */
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		backup = new boolean[width][height];
		grid = new boolean[width][height];
		committed = true;

	}



	/**
	 Returns the width of the board in blocks.
	 */
	public int getWidth() {
		return width;
	}


	/**
	 Returns the height of the board in blocks.
	 */
	public int getHeight() {
		return height;
	}


	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	 */
	public int getMaxHeight() {
		int tmp = -1;
		for(int i = 0; i < width; i++)
			tmp = Math.max(tmp, getColumnHeight(i));
		return tmp;
	}


	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	 */
	public void sanityCheck() {
		if (DEBUG) {
			invalidHeight(getHeight());
			invalidWidth(getWidth());
			invalidMaxHeight(getMaxHeight());
		}
	}

	protected void invalidHeight(int countedHeight){
		if(countedHeight != height){
			throw new RuntimeException("Height error");
		}
	}

	protected  void invalidWidth(int countedWidth){
		if(countedWidth != width)
			throw new RuntimeException("Width error");
	}

	protected void invalidMaxHeight(int val){
		int tmp = -1;
		for(int i = 0; i < width; i++)
			tmp = Math.max(tmp, getColumnHeight(i));
		if(tmp != val)
			throw new RuntimeException("Invalid max height exception");
	}

	protected  void changeDebugValue(){
		DEBUG = !DEBUG;
	}

	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.

	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	 */
	public int dropHeight(Piece piece, int x) {
		int res = 0;
		for(int i = 0; i < piece.getWidth(); i++){
			int h = getColumnHeight(x + i);
			int s = piece.getSkirt()[i];
			int y = h - s;
			res = Math.max(y,res);
		}
		return res;
	}


	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	 */
	public int getColumnHeight(int x) {
		int cnt = 0;
		for(int i = 0; i < height; i++){
			if(grid[x][i])
				cnt = i + 1;
		}
		return cnt;
	}


	/**
	 Returns the number of filled blocks in
	 the given row.
	 */
	public int getRowWidth(int y) {
		int cnt = 0;
		for(int i = 0; i < width; i++){
			if(grid[i][y])
				cnt++;
		}
		return cnt;
	}


	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	 */
	public boolean getGrid(int x, int y) {
		boolean answer = false;
		try{
			answer = grid[x][y];
		}catch (ArrayIndexOutOfBoundsException e){
			throw e;
		}
		return answer;
	}


	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;

	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.

	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	 */
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) {
			throw new RuntimeException("place commit problem");
		}
		committed = false;
		copyGrid();
		for(TPoint point : piece.getBody()){
			int X = point.x + x;
			int Y = point.y + y;
			if(X > width - 1 || x < 0 || Y > height - 1 || y < 0)
				return PLACE_OUT_BOUNDS;
			else if(grid[X][Y])
				return PLACE_BAD;
			else
				grid[X][Y] = true;
		}
		for(int i = y; i < y + piece.getHeight(); i++){
			if(getRowWidth(i) == width)
				return PLACE_ROW_FILLED;
		}
		return PLACE_OK;
	}

	private boolean isRowFilled(int row){
		int cnt = getRowWidth(row);
		return cnt == width;
	}

	private void copyGrid(){
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
				backup[i][j] = grid[i][j];
	}


	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	 */
	public int clearRows() {
		if(committed)
			copyGrid();
		committed = false;
		HashSet<Integer> blackList = new HashSet<>();
		int rowsCleared = 0;
		int currMx = getMaxHeight();
		for(int i = 0; i < height; i++){
			if(isRowFilled(i)){
				blackList.add(i);
				rowsCleared++;
			}
		}
		boolean [][] tmp = new boolean[width][height];
		int curr = 0;
		for(int i = 0; i < height; i++){
			if(blackList.contains(i)) continue;
			for(int j = 0; j < width; j++){
				tmp[j][curr] = grid[j][i];
			}
			curr++;
		}
		for(int i = curr; i < height; i++)
			for(int j = 0; j < width; j++)
				tmp[j][i] = false;
		grid = tmp;
		return rowsCleared;
	}



	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	 */
	public void undo() {
		if(!committed){
			boolean[][] temp = grid;
			grid = backup;
			backup = temp;
			committed = true;
		}
	}


	/**
	 Puts the board in the committed state.
	 */
	public void commit() {
		committed = true;
	}



	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility)
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return (buff.toString());
	}
}


