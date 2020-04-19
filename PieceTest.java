import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

/*
  Unit test for Piece class -- starter shell.
 */
public class PieceTest extends TestCase {
	// You can create data to be used in the your
	// test cases like this. For each run of a test method,
	// a new PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece s, sRotated;

	protected void setUp() throws Exception {
		super.setUp();
		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();

		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();
	}

	// Here are some sample tests to get you started



	public void testSampleSize() {
//		// Check size of pyr piece
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());

		// Now try after rotation
		// Effectively we're testing size and rotation code here
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());
		// Now try with some other piece, made a different way
		Piece l = new Piece(Piece.STICK_STR);
		assertEquals(1, l.getWidth());
		assertEquals(4, l.getHeight());
	}


	// Test the skirt returned by a few pieces
	public void testSampleSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));

		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, sRotated.getSkirt()));
	}

	public void testEquals() {
		Piece p1 = new Piece(Piece.PYRAMID_STR);
		Piece p2 = new Piece(Piece.PYRAMID_STR);
		assertTrue(p1.equals(p2));
		ArrayList<String> error = new ArrayList<>();
		assertFalse(p1.equals(error));
		p1 = new Piece("0 0  1 1");
		p2 = new Piece("0 0");
		assertFalse(p1.equals(p2));
		p1 = new Piece(Piece.SQUARE_STR);
		assertTrue(p1.equals(p1.computeNextRotation()));
	}

	public void testBadConstructor(){
		assertThrows(RuntimeException.class, () -> { new Piece("დაბადაბადუბააადააა");});
//		try{
//			Piece pc = new Piece("[a[a[a[a");
//		}catch (RuntimeException e){
//			System.out.println("Invalid format of input");}
	}

	public void testRotation() {
		assertEquals(pyr2, new Piece("1 0	1 1	 0 1  1 2"));
		assertEquals(pyr3, new Piece("2 1	1 1	 1 0  0 1"));
		assertEquals(pyr4, new Piece("0 2	0 1	 1 1  0 0"));
		Piece stick = new Piece(Piece.STICK_STR);
		assertEquals(4, stick.getHeight());
		Piece rotatedStick = stick.computeNextRotation();
		assertEquals(1, rotatedStick.getHeight());
	}

	public void testFastRotation() {
		Piece[] pieces = Piece.getPieces();
		Piece[] dog = Piece.getPieces();
		Piece square = pieces[Piece.SQUARE];
		Piece pyramid = pieces[Piece.PYRAMID];
		Piece stick = pieces[Piece.STICK];
		Piece L = pieces[Piece.L1];
		assertEquals(square, square.fastRotation());
		assertEquals(square, square.fastRotation().fastRotation());
		assertEquals(square, square.fastRotation().fastRotation().fastRotation());
		assertEquals(square, square.fastRotation().fastRotation().fastRotation().fastRotation());
		assertEquals(pyramid, pyramid.fastRotation().fastRotation().fastRotation().fastRotation());
		assertEquals(pyramid.fastRotation(), pyramid.fastRotation().fastRotation().fastRotation().fastRotation().fastRotation());
		assertEquals(stick, stick.fastRotation().fastRotation());
		assertEquals(L, L.fastRotation().fastRotation().fastRotation().fastRotation());
	}
}
