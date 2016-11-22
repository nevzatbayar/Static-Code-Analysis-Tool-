package Arayuz;
import javax.swing.SwingUtilities;

public class Arayuz {
	
	public static void main(String args[]) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ArayuzGui();				
			}
		});

	}

}
