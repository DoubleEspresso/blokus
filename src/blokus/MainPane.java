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

//ironically, has 3 things if you count the label...
public class MainPane {

	//private Frame mainFrame;
	private Label headerLabel;
	//private Panel gridPanel;
	//private Panel sliderPanel;

	public MainPane() {
		prepFrame();
	}

	// public static void main(String[] args)
	// {
	// MainPane twoFrame = new MainPane();
	// twoFrame.showScrollbars();
	// }

	private void prepFrame() {
		JFrame mainFrame = new JFrame();
		mainFrame.setSize(400, 600);
		mainFrame.setTitle("Two things in a frame.");
		mainFrame.setLayout(new GridLayout(3, 1, 1, 1));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// mainFrame.pack(); //makes window resize to fit components

		headerLabel = new Label();
		headerLabel.setAlignment(Label.CENTER);
		headerLabel.setSize(350, 150);
		headerLabel.setText("A first start");

		// sliderPanel = new Panel();
		// sliderPanel.setLayout(new FlowLayout(FlowLayout.LEADING,5,5));
		// try this out instead of a Panel...
		JTextArea txtArea = new JTextArea(5, 40);
		JScrollPane sliderPanel = new JScrollPane(txtArea);

		mainFrame.add(headerLabel);
		mainFrame.add(new GridPanel());
		mainFrame.add(sliderPanel);

		mainFrame.setVisible(true);
	}

//	private void showScrollbars() {
//		/**
//		 * final Scrollbar horizontalScroller = new
//		 * Scrollbar(Scrollbar.HORIZONTAL); final Scrollbar verticalScroller =
//		 * new Scrollbar(); verticalScroller.setOrientation(Scrollbar.VERTICAL);
//		 * horizontalScroller.setMaximum (120); horizontalScroller.setMinimum
//		 * (1); verticalScroller.setMaximum (120); verticalScroller.setMinimum
//		 * (1);
//		 * 
//		 * sliderPanel.add(verticalScroller);
//		 * sliderPanel.add(horizontalScroller); //mainFrame.setVisible(true);
//		 **/
//	}

	public class GridPanel extends JPanel {

		// silences serialized ID error
		private static final long serialVersionUID = 1L;

		public GridPanel() {
			setLayout(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();
			for (int row = 0; row < 15; row++) {
				for (int col = 0; col < 15; col++) {
					gbc.gridx = col;
					gbc.gridy = row;

					CellPane cellPane = new CellPane();
					Border border = null;
					if (row < 14) {
						if (col < 14) {
							border = new MatteBorder(1, 1, 0, 0, Color.GRAY);
						} else {
							border = new MatteBorder(1, 1, 0, 1, Color.GRAY);
						}

					} else {
						if (col < 14) {
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
			return new Dimension(50, 50);
		}
	}

}

//}
