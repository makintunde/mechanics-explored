import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class AboutText {
	
	public static void main(String[] args) {
		String nameOfFrame = null;
		new AboutText(nameOfFrame);
	}
  
	public AboutText(final String n) {  
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
      
				JTextArea textArea = new JTextArea();
		        String aboutStr = null;
		        
		        // add text to it; we want to make it scroll
		        if (n.equals("Mechanics Explored")) {
		        	aboutStr = "Welcome to Mechanics Explored.\n\n " +
		        			"This tool can be used to explore various areas of mechanics, " +
			            	"including kinematics and dynamics. If it is not known what " +
			            	"these terms mean, kinematics is the study of properties of " +
			            	"motion, without any consideration of why those quantities have "+
			            	"the values they do. Dynamics on the other hand means a study of " +
			            	"the rules governing the interactions of these particles, allowing " +
			            	"us to determine why the quantities have the values they do.";        	
		        } else if (n.equals("Projectile")) {
		        	aboutStr = "Welcome to the projectile simulator. This simulation in particular is for the motion of a projectile in a vacuum (in the absence of air molecules) which also implies that any force of air resistance acting on the particle is negligible. In this case, there Is some initial force which acts on the particle initially (at t=0) which gets the particle in motion in the first place, without any more forces acting on it during its motion, apart from the acceleration of gravity. Notice the horizontally symmetrical parabola produced by its motion during flight, the absence of acceleration parallel to the x-axis, and the fact that different masses make no difference to the trajectory of the particle. Try altering the mass of the particle. Altering it has no effect on the motion of the particle in flight.";
		        } else if (n.equals("Friction")) {
		        	aboutStr = "Welcome to the friction simulator. This simulation is for the motion of some object placed on an inclined plane, which is of some degree of roughness. The roughness is determined by what is called the coefficient of friction. Having a coefficient of restitution of a higher value means that the plane will be of a greater roughness and will have more of a resistance on the object while moving. The coefficient of friction is a constant, denoted by the Greek symbol �, which can take a decimal value between 0 and 1.";	
		        } else if (n.equals("Restitution")) {
		        	aboutStr = "Welcome to the simulator of the coefficient of restitution, i.e. elastic collisions of particles. This simulation is for the motion of particles in a 2D area, moving in a straight line, with a certain elasticity, or coefficient of restitution, denoted by the letter e. The value stays constant for only one direction of motion, and can take a value between 0 and 1. Having a high value close to 1 means that more energy is conserved and the collision between the particles is more elastic. A smaller value of 0 however means that more energy is lost, resulting in a less elastic collision and the possibility of the particles coalescing in extreme cases.";
		        }
		        
		        textArea.setText(aboutStr);          
		        textArea.setEditable(false);
		        textArea.setOpaque(false);
		        textArea.setCursor(null); 
		        textArea.setLineWrap(true);
		        textArea.setWrapStyleWord(true);
		        
		        // create a scrollpane, giving it the textarea as a constructor argument
		        JScrollPane scrollPane = new JScrollPane(textArea);        
		
		        // now add the scrollpane to the jframe's content pane, specifically
		        // placing it in the center of the jframe's borderlayout
		        
		        JFrame frame = new JFrame(n + " - About");
		        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		        // make it easy to close the application
		        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		        // set the frame size (you'll usually want to call frame.pack())
		        frame.setSize(new Dimension(450, 200));
		        
		        // center the frame
		        frame.setLocationRelativeTo(null);
		        
		        // make it visible to the user
		        frame.setVisible(true);
			}
		});
	}
}