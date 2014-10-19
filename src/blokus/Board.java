package blokus;
import java.util.Arrays;

import com.sun.prism.paint.Color;

import blokus.Logger.LogType;
import blokus.BlokusException;
import blokus.Polyomino;
import blokus.Polyomino.Orientation;

// the internal board class
// which keeps track of the Polyomino positions
// board size/who's move, time etc.

public class Board
{
	

	// board dimensions
    public static int boardWidth;
    public static int boardHeight;
	
	// the white/black pieces
	private int[] blackPieces = null;
	private int[] whitePieces = null;
	public int[] blokusBoard = null;
	
	// game data
	public Color whosMove = null;
	public int moveNumber = -1;
	
	
	// the private logger
	private Logger log = null;
	
	// constructors for this class
	Board(int _width, int _height, Logger lg)
	{
		boardWidth = _width;
		boardHeight = _height;

		try
		{
			// the main board
			blokusBoard = new int[boardWidth * boardHeight];
			Arrays.fill(blokusBoard,0);
			
			// the white pieces
			whitePieces = new int[boardWidth * boardHeight];
			Arrays.fill(whitePieces,0);
			
			blackPieces = new int[boardWidth * boardHeight];
			Arrays.fill(blackPieces, 0);
			
			if (log == null) log = lg;
			
			// TODO: read from game settings/startup each of these!
			whosMove = Color.WHITE;
			moveNumber = 1;
			
		}
		catch(Exception any)
		{
			if (log != null) log.write("..[Board] Exception, no memory!",LogType.CRITICAL);
			System.exit(BlokusException.OUT_OF_MEMORY);
		}
		
	}

	
	public Boolean do_move(Polyomino p, Orientation O, int row, int col)
	{
		
		
		int startSquare = square_of(row,col);
		Types.Color color = p.color;
		int [] tmpBoard = null;
		Boolean isLegal = false;
		
		
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
			return false;
		}
		
		
		
		// check legality
		try
		{
		  tmpBoard = p.draw(O, startSquare);
		  isLegal  = is_legal(p,tmpBoard,startSquare,color);
		  
		}
		catch(Exception any)
		{
			log.write("..[Board] Exception do-move for "+p.name+".",LogType.CRITICAL);
			System.exit(BlokusException.ILLEGAL_MOVE);
		}

		
		// update move
		whosMove = (whosMove == Color.WHITE ? Color.BLACK : Color.WHITE);
		
		// update move number
		moveNumber++;
		
		return isLegal;
	}
	
	
	public Boolean undo_move(Polyomino p, Orientation O, int row, int col)
	{
		return false;
	}
	
	
	private Boolean is_legal(Polyomino p, int[] tmpBoard, int start, Types.Color c)
	{
		//check that this square isn't already occupied
		for(int i=0; i<p.size; ++i)
		{
			int sq = p.occupiedSquares[i];
			if (blokusBoard[sq] == 1) return false;

			// check neighbors intelligently
			for(int j=0; j<p.neighborSquares.length; ++i)
			{
				if (p.neighborSquares[j] < 0 || p.neighborSquares[j] > boardHeight*boardWidth )
					continue;
				else if (on_left_edge(sq) && (p.neighborSquares[j]-sq) == -1) continue; // don't check left-neighbor when on left-edge
				else if (on_right_edge(sq) && (p.neighborSquares[j]-sq) == 1) continue; // don't check right-neighbor when on right-edge
					
				else if((c == Types.Color.WHITE && whitePieces[p.neighborSquares[j]]==1) ||
						(c == Types.Color.BLACK && blackPieces[p.neighborSquares[j]]==1)) return false; // check if neighbor square is friendly

			}
			

		}
		
		// all tests passed, we can update board arrays
		for(int i=0; i<p.size; ++i)
		{
			int sq = p.occupiedSquares[i];
			blokusBoard[sq] = 1;
			if (c == Types.Color.WHITE) whitePieces[sq] = 1;
			else blackPieces[sq] = 1;
		}

		return true;
	}
	
	
	
	public int square_of (int row, int col) 
	{
		assert row < Board.boardWidth;
		assert col < Board.boardHeight;
	
		return row*Board.boardWidth + col;
	}

	public Boolean on_left_edge(int square)
	{
		assert square >= 0;
		assert square <= Board.boardHeight * Board.boardWidth;
		
		// square  = board.width * row + col;
		// if col == 0, square % width = 0;
		return ((square % Board.boardWidth) == 0);
	}
	
	public Boolean on_right_edge(int square)
	{
		assert square >= 0;
		assert square <= Board.boardHeight * Board.boardWidth;

		return ( (square % Board.boardWidth) ==  (Board.boardWidth-1));
	}
	
	public Boolean on_bottom_edge(int square)
	{
		assert square >= 0;
		assert square <= Board.boardHeight * Board.boardWidth;
		return ((square % Board.boardHeight)==0);
	}
	
	public Boolean on_top_edge(int square)
	{
		assert square >= 0;
		assert square <= Board.boardHeight * Board.boardWidth;
		return ((square % Board.boardHeight)==(Board.boardHeight-1));
	}
}
