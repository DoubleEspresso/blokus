package blokus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

public class GridFrame
{


	//Make panel to draw on
	public GridFrame()
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} 
				catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
				{
					
				}
			
				JFrame frame = new JFrame("Grid board");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new BorderLayout());
				frame.add(new BoardPane());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	// box container for the polyominos
	public Box createHorizontalBox()
	{
		Box lineBox = new Box(BoxLayout.LINE_AXIS);
		return lineBox;
	}
	
	
	//Extend panel and draw grid	
	public class BoardPane extends JPanel
	{
		public BoardPane()
		{
			setLayout(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();
			
			// read in MAX_ROW/ MAX_COL here.
			for (int row = 0; row<15; row++)
			{
				for(int col = 0; col<15; col++)
				{
					gbc.gridx = col;
					gbc.gridy = row;

					CellPane cellPane = new CellPane();
					Border border = null;
					if (row<15)
					{
						border = new MatteBorder(1,1,0,0, Color.GRAY);
					} else
					{
						if (col<15)
						{
							border = new MatteBorder(1,1,1,0, Color.GRAY);
						} else {
							border = new MatteBorder(1,1,1,1, Color.GRAY);
						}
					}
					cellPane.setBorder(border);
					add(cellPane, gbc);
				}	
			}
			
			// next add a container at the bottom to hold all the blokus
			// pieces.
			Box horizontalBox = createHorizontalBox();
			add(horizontalBox,gbc);
		}

	}

	//Extend Panel and add mouse sensitivity
	public class CellPane extends JPanel
	{
		private Color defaultBackground;

		public CellPane()
		{
			addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseEntered(MouseEvent e)
				{
				defaultBackground = getBackground();
				setBackground(Color.BLUE);
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
				setBackground(defaultBackground);
				}
			});
		}

		@Override 
		public Dimension getPreferredSize()
		{
		return new Dimension(50,50);
		}
	}
}