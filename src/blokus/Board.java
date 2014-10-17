package blokus;

import java.awt.*; 
import javax.swing.*;
import java.awt.Graphics;
import javax.swing.JPanel;


// to draw a board, we need a swing component
// with a paint method we can override, we provide this
// through the  basewindowpane class..
public class Board extends JPanel
{

	// board dimensions
	//private int boardWidth;
	//private int boardHeight;
	//private int squareLength;
	//private int numSquares;
	
	// graphics
	//private Graphics g = new Graphics();
	private Graphics2D g2d = null;
	
	
	// constructors for this class
	Board(int _width, int _height)
	{
		// exception handling?
		//g2d = (Graphics2D) g;
		//boardWidth = _width;
		//boardHeight = _height;
	}
	
	Board()
	{
		
		//g2d = (Graphics2D) g;
		// size in pixels?
		//boardWidth = 200;
		//boardHeight = 200;
		//makeFrame();
	}
	
	
	// the frame
	private void makeFrame()
	{
		// just draw a rectangle...
		//g2d.draw(new Line2D.Double(0,0,200,0));
		//g2d.draw(new Line2D.Double(200,0,200,200));
		//g2d.draw(new Line2D.Double(200,200,0,200));
		//g2d.draw(new Line2D.Double(0,200,0,0));
	}
	
	// 4 side panels?
	private void panelTop()
	{
		
	}
	
	private void panelBottom()
	{
		
	}
	private void panelLeft()
	{
		
	}
	private void panelRight()
	{
		
	}
	
	
	// the squares
	public void square() 
	{
		
	}
	
	// vertices?
	public void vertex()
	{
	}
	
	//score panel
	public void panelScore()
	{
	}	
	
}
