package pieces;
import java.util.Arrays;

import blokus.BlokusException;
import blokus.Polyomino;
import blokus.Logger.LogType;
import blokus.Polyomino.Orientation;
import blokus.Board;
import blokus.Types.Color;
import blokus.Logger;

public class P1 extends Polyomino {

	// array pointing to the neighbors of this square
	public int[] directions = {-1,1,Board.boardWidth,-Board.boardWidth};
	
	P1(Color c, Logger lg)
	{
		name = "P1";      
		origin[0] = 0;
		origin[1] = 0;
		color = c;
		if (log==null) log = lg;
	}
	
	@Override 
	public int [] draw(Orientation O, int startSquare)
	{

		int [] tmpBoard = null;
		try
		{
			// the main board
			tmpBoard = new int[ Board.boardHeight * Board.boardWidth];
			Arrays.fill(tmpBoard,0);
		}
		catch(Exception any)
		{
			if (log != null) log.write("..[Draw P1] Exception, no memory!",LogType.CRITICAL);
			System.exit(BlokusException.OUT_OF_MEMORY);
		}
		
		// fill tmpBoard based on orientation
		switch(O)
		{
			default: tmpBoard=draw_p1(startSquare); break;
		}
		
		return tmpBoard;
	}
	
	
	
	
	private int [] draw_p1(int square)
	{
		// build temporary array
		int [] tmpBoard = null;
		try
		{
			// the main board
			tmpBoard = new int[ Board.boardHeight * Board.boardWidth];
			Arrays.fill(tmpBoard,0);
			
			if (occupiedSquares == null)
			{
				occupiedSquares = new int[size];
				Arrays.fill(occupiedSquares,0);
			}
			
			
			if (neighborSquares == null)
			{
				neighborSquares = new int[4*size];
				Arrays.fill(neighborSquares,0);
			}
		}
		catch(Exception any)
		{
			if (log != null) log.write("..[Draw P1] Exception, no memory!",LogType.CRITICAL);
			System.exit(BlokusException.OUT_OF_MEMORY);
		}
		
		// toggle the position bit
		tmpBoard[square] = 1;

		for (int i=0; i<4; ++i)
		  neighborSquares[i] = square +directions[i]; 

		
		// set the occupied squares for this piece
		occupiedSquares[0] = square;
		
		return tmpBoard;
	}
}
