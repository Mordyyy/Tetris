import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;


public class BoardTest extends TestCase {
	Board b;
	Piece pyr1, pyr2, pyr3, pyr4, s, sRotated;

	protected void setUp() throws Exception {
		b = new Board(3, 6);

		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();

		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();

		b.place(pyr1, 0, 0);
	}

	public void testSample1() {
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
		assertEquals(3, b.getWidth());
		assertEquals(6, b.getHeight());
	}

	public void testSample2() {                    // zeda marcxena aris 0 0
		b.commit();
		int result = b.place(sRotated, 1, 1);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
	}

	public void testPlace(){
		Board board = new Board(4,20);
		board.committed = false;
		Assertions.assertThrows(RuntimeException.class, ()-> {board.place(pyr1, 1, 1); });
		board.commit();
		int checkOK = board.place(pyr1,0,0);
		assertEquals(Board.PLACE_OK, checkOK);
		board.commit();
		int checkInvalidBounds =  board.place(pyr1, 0, -1);
		assertEquals(Board.PLACE_OUT_BOUNDS, checkInvalidBounds);
		board.commit();
		checkInvalidBounds =  board.place(pyr1, 0, 100);
		assertEquals(Board.PLACE_OUT_BOUNDS, checkInvalidBounds);
		board.commit();
		checkInvalidBounds =  board.place(pyr1, -1, 0);
		assertEquals(Board.PLACE_OUT_BOUNDS, checkInvalidBounds);
		board.commit();
		checkInvalidBounds =  board.place(pyr1, 100, -1);
		assertEquals(Board.PLACE_OUT_BOUNDS, checkInvalidBounds);
		board.commit();
		int checkBadPosition = board.place(pyr1, 0,1);
		assertEquals(Board.PLACE_BAD, checkBadPosition);
		board.commit();
		int checkFilledRow = board.place(new Piece(Piece.STICK_STR), 3, 0);
		assertEquals(Board.PLACE_ROW_FILLED, checkFilledRow);
}

	public void testClearRows(){
		Board bo = new Board(3,5);
		bo.place(new Piece(Piece.SQUARE_STR), 0, 0);
		bo.commit();
		bo.place(new Piece(Piece.SQUARE_STR),0, 2);
		bo.commit();
		bo.place(new Piece(Piece.STICK_STR), 2, 0);
		int clearedRows = bo.clearRows();
		assertEquals(4, clearedRows);
	}

	public void testGridGet(){
		for(int i = 0; i < 3; i++)
			assertTrue(b.getGrid(i, 0));
		for(int i = 0; i < 3; i++)
			assertFalse(b.getGrid(i, 5));
		Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, ()-> {b.getGrid(-1,2);});
	}

	public void testUndo(){
		Board bo = new Board(3,5);
		bo.place(new Piece(Piece.SQUARE_STR), 0, 0);
		boolean res = true;
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 5; j++){
				if(bo.getGrid(i,j)){
					res = false;
					break;
				}
			}
		}
		bo.undo();
		assertFalse(res);
		bo.undo();
	}

	public void testUndo2(){
		Board b1 = new Board(10,10);
		Board b2 = new Board(10,10);
		b1.place(pyr1,5,5);
		b2.place(pyr1, 5,5);
		b1.commit();
		b1.place(pyr1, 0, 0);
		b1.undo();
		for(int i = 0; i < 10; i++){
			for(int j = 0; j < 10; j++){
				assertEquals(b1.getGrid(i,j), b2.getGrid(i,j));
			}
		}
	}
	public void testUndoCommitted(){
		Board b = new Board(5, 6);
		b.place(new Piece(Piece.SQUARE_STR), 0, 0);
		b.commit();
		b.undo();
		b.clearRows();
		b.commit();
		b.undo();
		assertEquals(2, b.getColumnHeight(0));
		for(int i = 0; i < b.getColumnHeight(0); i++)
			assertEquals(2, b.getRowWidth(i));
		assertEquals(2, b.getMaxHeight());
	}



	public void testToString(){
		Board bo = new Board(3,3);
		bo.place(pyr1,0,0);
		String res = new String("|   |\n" +
				"| + |\n" +
				"|+++|\n" +
				"-----");
		assertEquals(res, bo.toString());
	}

	public void testDrop(){
		Board bo = new Board(10,5);
		bo.place(new Piece(Piece.STICK_STR),0,0);
		assertEquals(4, bo.dropHeight(new Piece(Piece.SQUARE_STR),0));
	}
	public void testSanity(){
		Board bo = new Board(10,5);
		bo.place(new Piece(Piece.STICK_STR),0,0);
		Assertions.assertThrows(RuntimeException.class, ()-> {bo.invalidWidth(100);});
		Assertions.assertThrows(RuntimeException.class, ()-> {bo.invalidHeight(100);});
		Assertions.assertThrows(RuntimeException.class, ()-> {bo.invalidMaxHeight(100);});
		bo.changeDebugValue();;
		Assertions.assertDoesNotThrow(bo::sanityCheck);
		bo.changeDebugValue();
		Assertions.assertDoesNotThrow(()-> bo.invalidHeight(bo.getHeight()));
		Assertions.assertDoesNotThrow(()-> bo.invalidWidth(bo.getWidth()));
		Assertions.assertDoesNotThrow(()-> bo.invalidMaxHeight(bo.getMaxHeight()));
		bo.sanityCheck();
	}

}
