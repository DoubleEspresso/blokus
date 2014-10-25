package blokus;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.JTextArea;

public class MainPane {


	private Label headerLabel;

	
	
	public MainPane(int _width, int _height) 
	{
		prepFrame(_width, _height);
	}

	
	

	private void prepFrame(int _width, int _height) 
	{

		// compute size based on width/height
		
		
		JFrame mainFrame = new JFrame();
		mainFrame.setSize(400, 700);
		mainFrame.setTitle("Blokus");
		mainFrame.setLayout(new GridLayout(2, 1, 1, 1));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		//headerLabel = new Label();
		//headerLabel.setAlignment(Label.CENTER);
		//headerLabel.setSize(350, 150);
		//headerLabel.setText("A first start");
		//mainFrame.add(headerLabel);

		JTextArea txtArea = new JTextArea(5, 40);
		JScrollPane sliderPanel = new JScrollPane(txtArea);


		mainFrame.add(new GridPanel(_width, _height));
		mainFrame.add(sliderPanel);

		mainFrame.setVisible(true);
	}


	public class GridPanel extends JPanel {

		// silences serialized ID error
		private static final long serialVersionUID = 1L;

		public GridPanel(int _rsize, int _csize) 
		{
			setLayout(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();
			
			for (int row = 0; row < _rsize; row++) {
				for (int col = 0; col < _csize; col++) {
					gbc.gridx = col;
					gbc.gridy = row;

					CellPane cellPane = new CellPane();
					Border border = null;
					if (row < _rsize-1) {
						if (col < _csize-1) {
							border = new MatteBorder(1, 1, 0, 0, Color.GRAY);
						} else {
							border = new MatteBorder(1, 1, 0, 1, Color.GRAY);
						}

					} else {
						if (col < _csize-1) {
							border = new MatteBorder(1, 1, 1, 0, Color.GRAY);
						} else {
							border = new MatteBorder(1, 1, 1, 1, Color.GRAY);
						}
					}
					cellPane.setBorder(border);
					add(cellPane, gbc);
				}
			}
		}

	}

	// Extend Panel and add mouse sensitivity
	public class CellPane extends JPanel {

		private static final long serialVersionUID = 1L;
		private Color defaultBackground;

		public CellPane() {
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					defaultBackground = getBackground();
					setBackground(Color.BLUE);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					setBackground(defaultBackground);
				}
			});
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(80, 80);
		}
	}

}

//}
