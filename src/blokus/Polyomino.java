package blokus;

import blokus.Logger;
import blokus.Types.Color;

//   ** NAMING CONVENTIONS **
// 
// Following the naming scheme presented here,
//
// http://math.stackexchange.com/questions/281682/concise-naming-scheme-for-polyominoes
//
// A generic polyomino is named (A,B)L,Ui,Dj... where L and Ui and Dj are rotations up/down
// at squares i,j respectively.  We can "add" polyominos by attaching branches at specific sides
// the notation Pj is used to denote "plus" on side "j", for example,
//
//   
//   []
//   [][][]
//   []
//
//   could be named, A3P3A2 ---> we start with length 3 poloymino A3
//
//   [][][]
//
//   then on the 3rd edge, (starting on the left-most edge ,moving to the right), attach a poloymino of length 2
//
//   [][][]
//     []        ===>   A3P3A2.  No orientation info is given in the naming.
//     []
//
//  
//  [][][]       ===> could be named in 2 ways, A5D3 or A3P4A2, I suggest the convention be to pick the shortest name if an option exists.
//      []            So I would name this one A5D3.  Notice also that A5U3 describes the same piece.
//      []
//
//  The "B" letter is reserved for a "Block" of squares.  We can denote it's size by using R(i) and C(i) to denote row/col lengths
//  if the block is square, we could just use the area instead.
//  For example,
//
//   [][]
//   [][]   could be named B4 or B4R2C2
//
//   [][][][][]
//   [][][][][]  could be named B10R5C2 (where R and C are interchangeable here)
//
//   Following the naming convention above (shortest is best), a non-square block should be denoted by R(i) C(i), so the last
//   example could be named R5C2 instead.
//
//   One last example is adding a block,
//   
//   [][]
//   [][][]    ==>  B4P4A1 as one possible name.  Here it doesn't matter on which edge the 1-block is added, so we can remove P4 and write
//                  B4PA1 
//


// base class for each polyomino
public class Polyomino {

	public String name = null;       // string name of the piece
	public int[] origin = null;     // each piece has a conventional "origin" given by row/col
	public int size = 0;
	public Color color = Color.NO_COLOR;
	public Logger log = null;
	public int[] occupiedSquares = null;
	public int[] neighborSquares = null;
	//-------------------------------------------------------------
	// Orientations
	// A polyomino can have at most 8 unique orientations,
	// "R" denotes a 90-degree Rotation
	// "F" denotes a "flip" about the x-axis.
	// Sequence R3F denotes rotate 270, and flip about the x-axis
	// R0 is the conventional starting position of the polyomino.
	//-------------------------------------------------------------
	public enum Orientation
	{
		R0,R1,R2,R3,
		R0F,R1F,R2F,R3F	
	}
	
	
	// virtual drawing method
	public int [] draw(Orientation O, int startSquare) throws BlokusException
	{
		
		throw new BlokusException("[Polyomino] Base class draw must be overridden.");
	}
	
	
	
}
