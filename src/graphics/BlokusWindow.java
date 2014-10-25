package graphics;
//import java.awt.Canvas;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.GridLayout;
//import javafx.application.Application;
//import javafx.scene.Group;
//import javafx.scene.Scene;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.paint.Color;
//import javafx.stage.Stage;
import javax.swing.*;
import java.awt.*;




import blokus.Logger;
import blokus.Logger.LogType;
import blokus.BlokusException;

@SuppressWarnings("serial")
public class BlokusWindow extends JFrame 
{
	//Canvas BoardPane = null;
	//GraphicsContext gBoardContext = null;
	
	
	//GraphicsContext gPieceContext = null;
	
	JFrame MainFrame  = null;
	JPanel wPiecePanel = null;
	JPanel bPiecePanel = null;
	JPanel GameDataPanel = null;
	JPanel BoardPanel = null;
	//Group BlokusGroup = null;
	//Dimension MainFrameSize = null;
	
	private int BoardPanelWidth = -1;
	private int BoardPanelHeight = -1;
	private int nBoxesX = -1;
	private int nBoxesY = -1;
	
	public BlokusWindow(int _width, int _height, Logger log )
	{
		try
		{
			nBoxesX = _width;
			nBoxesY = _height;
			
			if (MainFrame == null)
			{
				MainFrame = new JFrame();

				BoardPanel = new JPanel();
				BoardPanel.add(new DrawMainBoard(nBoxesX, nBoxesY));

				wPiecePanel = new JPanel();
				bPiecePanel = new JPanel();
				GameDataPanel = new JPanel();
				
				
				MainFrame.add(BoardPanel);
				MainFrame.pack();
				MainFrame.setVisible(true);
				MainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
			}
			
		}
		catch(Exception any)
		{
			//log.write("..[MainPane] Exception creating the main pane!", LogType.ERROR);
			System.exit(BlokusException.GRAPHICS_ERROR);
		}
		
	
	
	}


	
	public class DrawMainBoard extends JComponent
	{
		private float boxSizeX = -1;
		private float boxSizeY = -1;
		private int nBloksX = -1;
		private int nBloksY = -1;
		
		DrawMainBoard(int _nBoxesX, int _nBoxesY)
		{
			
			nBloksX = _nBoxesX;
			nBloksY = _nBoxesY;
			
			Rectangle ScreenBoundingBox;
			ScreenBoundingBox = GraphicsEnvironment.getLocalGraphicsEnvironment()
												   .getDefaultScreenDevice()
												   .getDefaultConfiguration()
												   .getBounds();
			
			BoardPanelWidth = (int) ( (float) ScreenBoundingBox.width / 1.5);
			BoardPanelHeight = (int) ( (float) ScreenBoundingBox.height / 1.5);
			setPreferredSize(new Dimension(BoardPanelWidth, BoardPanelHeight));
			
			boxSizeX = (float) (  BoardPanelWidth / 1.5 ) / _nBoxesX;
			boxSizeY = (float) (  BoardPanelHeight / 1.5 ) / _nBoxesY;
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			int offsetX = (int) ((BoardPanelWidth - nBloksX * boxSizeX)/2.0);
			int offsetY = (int) ((BoardPanelHeight - nBloksY * boxSizeY)/2.0);
			
			super.paintComponent(g);
			int locX = 0;
			int locY = 0;
			for( int y = 0; y<nBloksY; ++y)
			{
				
				locY = (offsetY + (int) boxSizeY * y);
				
				for( int x = 0; x<nBloksX; ++x)
				{
					locX = ( offsetX + (int)boxSizeX * x);
					g.drawRect(locX, locY, (int)boxSizeX, (int)boxSizeY);
				}
			}
		}
	}
	

}
