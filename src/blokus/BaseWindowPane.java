package blokus;
import javax.swing.JFrame;

public class BaseWindowPane {

		// draw a simple window for the application	
		public void createBaseWindowPane()
		{
			
			JFrame frame = new JFrame("Blokus");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(600,600);
			
			// display 
			frame.setLocationRelativeTo(null);
			//frame.pack(); -- use when we add some components
			frame.setVisible(true);

		}
	
		// we can add many options here...
}
