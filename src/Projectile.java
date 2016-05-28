import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;

/*
 * Mechanics Explored - Projectile
 * Copyright (R) Michael Akintunde 2013 * 								
 */

@SuppressWarnings("serial")
public class Projectile extends Canvas implements Runnable {	
	
	Planet earth = new Planet(9.8);
	pParticle p = new pParticle();
	
	// Check simulation is running, or is paused
	public boolean running = false;
	public boolean paused = false;
	
	public Label heightLabel;
	public TextField angleField;
	public TextField velField;
	
	public String name = "Projectile"; //used for "About" text
	
	Projectile() {		
		//housekeeping methods
		setSize(earth.x, earth.y); 
		final Frame pictureFrame = new Frame("Projectile Simulation"); 
		Panel canvasPanel = new Panel(); // Create new panel
		canvasPanel.add(this); // Add panel
		canvasPanel.setBackground(Color.BLACK); // Set background colour to black
		pictureFrame.add(canvasPanel); // Add canvasPanel panel to pictureFrame frame	
		
		// Add controlPanel panel with start and stop buttons, and a menu bar
		Panel controlPanel = new Panel(); 
		final Choice menu = new Choice();
		
		// Simulation controls
		final Button startButton = new Button("Start");
		Button stopButton = new Button("Stop");
		Button pauseButton = new Button("Pause");	
		
		//create text field for height, angle and velocity entry
		final TextField heightField = new TextField(5);
		final TextField velField = new TextField(5);
		final TextField angleField = new TextField(5);
		final TextField massField = new TextField(5);
		
		//create labels for height, angle and velocity entry
		Label heightLabel = new Label("Enter height (m):");		
		Label velLabel = new Label("Enter velocity (v):");
		Label angleLabel = new Label("Enter angle (a):");
		Label massLabel = new Label("Enter mass (kg):");
		
		//add labels and fields for height, angle and velocity
		controlPanel.add(heightLabel);
		controlPanel.add(heightField);
		controlPanel.add(angleLabel);
		controlPanel.add(angleField);
		controlPanel.add(velLabel);
		controlPanel.add(velField);
		controlPanel.add(massLabel);
		controlPanel.add(massField);
		
		//add control buttons
		controlPanel.add(startButton);
		controlPanel.add(stopButton);
		controlPanel.add(pauseButton);		
		
		//add items to menu
		menu.add("Menu");
		menu.add("Help");
		menu.add("About"); 
		menu.add("Quit");
		
		pictureFrame.add(controlPanel, BorderLayout.SOUTH);	//add control panel to the south
		pictureFrame.add(menu, BorderLayout.NORTH);	//add menu to the north
		pictureFrame.pack();
		pictureFrame.setVisible(true); //we would like to see the simulation in action
		
		//find out whether user closes window
		pictureFrame.addWindowListener(new WindowAdapter() { //add window listener
			public void windowClosing(WindowEvent e) { //what to do if the window is closed
				pictureFrame.setVisible(false);	//hide frame, not exit			
			}
		});	
		
		//find out whether user clicks "Start"
		startButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent ev) {
				
				// Validate user input
				try {
					
					// Java works in radians for Math class - so conversion needed
					p.a = convertToRadians(getTextFrom(angleField));
					
					// Convert the user's input for velocity into a value of the "double" datatype
					p.mVel = getTextFrom(velField);	
					
					p.initY = earth.y - (int) getTextFrom(heightField);		
					
				} catch (NumberFormatException ne1) {
					JOptionPane.showMessageDialog(null, "Only numerical values are accepted.");
					return;
				}
				
				running = true; //simulation is now running				
				
				if (!paused) {		
					earth.t = 0;
					startButton.setEnabled(false);		
					
				}
				//simulation is not paused
				else {
					paused = false;	
					startButton.setEnabled(true);					
				}									
			}			
		});
		
		//find out whether user clicks "Stop"
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				running = false; //simulation is not running anymore	
				startButton.setEnabled(true);	
				p.y = earth.y - (int)getTextFrom(heightField);
				//reset variables
				p.resetVars();
			}			
		});
		
		//find out whether user clicks "Pause"
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent even) {
				//simulaiton is not running, but is now paused
				running = false; 
				paused = true;	
				startButton.setEnabled(true);
			}			
		});
		
		//find out whether user's menu option is "c"
		menu.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				String c = (String) menu.getSelectedItem();
				if (c == "Quit") {
					System.out.println("Quit button clicked...");
					//pictureFrame.setVisible(false); 
				} else if (c == "Help") {
					//Initiate help methods
					System.out.println("Help button clicked...");
				} else if (c == "About") {
					//Open "about" information window
					displayAboutText(name);

				}
			}
		});	
		
		Thread myThread = new Thread(this); //create new thread
		myThread.start(); //start thread
		System.out.println("Projectile simuation started"); //is printed on console window, as a confirmation
	}
	
	public double getTextFrom(TextField s) {
		return Double.parseDouble(s.getText());
	}
	
	public double convertToRadians(double x) {
		return (Math.PI/180) * x;
	}
	
	public void displayAboutText(String n) {
		new AboutText(n);
	}
	
	public void run() {	
		
		double dt = 0.02; //a "second"	
		double ay = -earth.G;
		//This "true" boolean is used to loop the section indefinitely
		while (true) {
			if (running && (p.y < earth.y - 30)) { //we do not want ball to fall below canvas's bottom
				for (int i = 0; i < 0.1/dt; i++) { //slows simulation down and reduces flickering
					
					//calculate x position
					//p.updateX(earth.t);
					p.x = (int)(p.mVel * earth.t * Math.cos(p.a));
					
					//calculate y position
					//p.updateY(earth.t);
					p.y = p.initY + (int) ((p.mVel * earth.t * -Math.sin(p.a)) - (.5 * ay * earth.t * earth.t));
				
					
					//increment time by "delta" t
					earth.t += dt;					
				}	
				//paint items again
				repaint();
			}
			
			try {Thread.sleep(30);} catch (InterruptedException e){} //wait for 30 milliseconds			
		}
	}	
	
	public void paint(Graphics g) {
		// time of flight		
		// convert to two decimal places
		String tofStr = p.getTimeOfFlight(p.mVel, earth.G); 
		
		//calculate greatest height reached
		//convert to two decimal places
		String gHeightStr = p.getMaxHeight(p.a, earth.G, p.mVel); 
		
		//calculate range of flight
		String rangeStr = p.getRange(p.a, earth.G, p.mVel);
		
		int Y = 10;
		int X = 800;		
		
		//display properties of the parabolic flight
		g.setColor(Color.CYAN);
		g.drawString("Time of flight : " + ((running && (p.y < earth.y - 30))? "" : tofStr), X, Y);
		g.drawString("Maximum Height : " + gHeightStr + "m", X, Y + 15);
		g.drawString("Range			 : " + rangeStr + "m", X, Y + 30);
		
		//coloured intercepts of coordinate axes
		g.setColor(Color.WHITE);
		g.drawLine(p.x, earth.y - 20, p.x, earth.y);
		g.drawLine(10, (int)p.y, 22, (int)p.y);
		g.drawLine(p.x-2, earth.y - 20, p.x-2, earth.y);
		g.drawLine(10, (int)p.y - 2, 22, (int)p.y - 2);
		
		//Coordinate axes
		g.setColor(Color.RED);
		g.drawLine(0, earth.y - 10, earth.x, earth.y - 10);
		g.drawLine(10, 0, 10, earth.y);		
		
		//velocity vector
		g.setColor(Color.MAGENTA);
		g.drawLine((int)p.x + 2, (int)p.y + 5, (int)(p.x + p.mVel * Math.cos(p.a)), 
				(int)(p.y - (p.mVel * Math.sin(p.a)) + (earth.G * earth.t)));
		g.drawLine((int)p.x + 2, (int)p.y + 15, (int)(p.x + p.mVel * Math.cos(p.a)), 
				(int)(p.y - (p.mVel * Math.sin(p.a)) + (earth.G * earth.t)));
				
		//parameters
		
		double vX = p.mVel * Math.cos(p.a);
		String vXStr = p.xVelStr(vX);
		double vY = (p.mVel * Math.sin(p.a)) - (earth.G * earth.t);
		String vYStr = p.yVelStr(vY);
		
		
		if (vY < 0) {
			g.setColor(Color.RED); //for negative y-component of velocity
			g.drawString(vYStr, p.x - 40, (int)p.y + 20); //spacing needed between y-coordinates
			g.drawString(vXStr, p.x - 40, (int)p.y + 0);
		} else {
			g.setColor(Color.GREEN); //for positive y-component of velocity
			g.drawString(vYStr, p.x - 40, (int)p.y + 20);
			g.drawString(vXStr, p.x - 40, (int)p.y + 0);				
		}	
		
		//draw particle
		g.setColor( Color.ORANGE );
		g.fillOval(p.x, p.y, 20, 20);
	}
	
	public static void main(String args[]) {
		//create new AnimationObject
		new Projectile();		
	}
}
