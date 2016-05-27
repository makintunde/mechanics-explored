import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

import javax.swing.JOptionPane;

/*
 * Mechanics Explored - Projectile
 * Copyright (R) Michael Akintunde 2013
 * 
 * Latest updates: 11/03/13 
 * 	-	added "about" text function
 * 	-	changing data types from "double" to "float"
 *  -	Commenting clean-up
 * 								
 */

public class Projectile extends Canvas implements Runnable {	
	
	DecimalFormat twoDecPlaces = new DecimalFormat("0.00");
	
	public double y = 10; 			//position in y-direction
	public double t = 0; 			//time
	public double ay = 0; 			//y component of acceleration - x component is negligible since constant
	public double vy = 0; 			//y component of velocity
	public double aRough = 0; 		//round (not rounded) acceleration value
	public double a = 0; 			//acceleration
	public double v = 0; 			//velocity
	public double G = 9.8;			//gravity, not to get confused with small "g" for paintComponent method
	
	//initial position of ball
	public int x = 5;
	public int initY = 0;
	
	//can be used to change colour of an item in real time - not relevant for now
	/*public int colx = 0;
	public int coly = 0;
	public int colz = 0;*/
	
	//size of canvas
	public int CANVAS_SIZEy = 500;
	public int CANVAS_SIZEx = 1000;	
	
	//check simulation is running, or is paused
	public boolean running = false;
	public boolean paused = false;
	
	//these must be public as they are used by internal methods
	public Label heightLabel;
	public TextField angleField;
	public TextField velField;
	
	public String name = "Projectile"; //used for "About" text
	
	//constructor
	Projectile() {		
		//housekeeping methods
		setSize(CANVAS_SIZEx,CANVAS_SIZEy); // Set size of canvas to the predefined coodrinates
		final Frame pictureFrame = new Frame("Projectile Simulation"); // Make a new frame, with title "Projectile Simulation"	
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
		pictureFrame.addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) {
				pictureFrame.setVisible(false);
				
			}
		});	
		
		//find out whether user clicks "Start"
		startButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent ev) {
				
				// Validate user input
				try {
					// "Raw" angle
					aRough = getTextFrom(angleField); //get the text from the angle field
					
					// Java works in radians for Math class - so conversion needed
					a = convertToRadians(aRough);  //convert it into radians
					
					// Convert the user's input for velocity into a value of the "double" datatype
					v = getTextFrom(velField); //get text from velocity field				
				} catch (NumberFormatException ne1) {
					JOptionPane.showMessageDialog(null, "Only numerical values are accepted.");
					return;
				}
				
				running = true; //simulation is now running
				
				
				
				if (!paused) {
					
					startButton.setEnabled(false);
					t = 0;				
					//Java's canvas coordinates are upside-down, so converter is needed
					//cast input of heightField into a suitable height integer
					initY = CANVAS_SIZEy - (int)getTextFrom(heightField);
					//cast input of angleField into a suitable angle integer				
										
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
				y = CANVAS_SIZEy - (int)getTextFrom(heightField); //reset height
				//reset variables
				resetVars();
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
	
	public void resetVars() {
		t = 0;	
		ay = 0;
		vy = 0;
		x = 10;		
	}
	
	public void displayAboutText(String n) {
		new AboutText(n);
	}
	
	public int updateX() {
		return (int)(v * t * Math.cos(a));		
	}
	
	public int updateY() {
		return initY + (int) ((v * t * -Math.sin(a)) - (.5 * ay * t * t));
	}
	
	public void run() {
			
		double dt = 0.02; //a "second"
		double ay = -G;
		 //resolving "up" as positive, gravity acts opposite this so is negative
		
		//This "true" boolean is used to loop the section indefinitely
		while (true) {
			if (running && (y < CANVAS_SIZEy - 30)) { //we do not want ball to fall below canvas's bottom
				for (int i = 0; i < 0.1/dt; i++) { //slows simulation down and reduces flickering
					
					//calculate x position
					x = updateX();
					
					//calculate y position
					y = initY +	(int) ((v * t * -Math.sin(a)) - (.5 * ay * t * t));
					
					//increment time by "delta" t
					t += dt;					
				}	
				//paint items again
				repaint();
			}
			
			try {Thread.sleep(30);} catch (InterruptedException e){} //wait for 30 milliseconds			
		}
	}	
	
	public void paint(Graphics g) {
		//calculate time of flight
		double tof = (2 * v * Math.sin(a)) / G;
		String tofStr = twoDecPlaces.format(tof); //convert to two decimal places
		
		//calculate greatest height reached
		double gHeight = (CANVAS_SIZEy - initY) + (v * v * (Math.sin(a)) * (Math.sin(a)))/(2 * G);
		String gHeightStr = twoDecPlaces.format(gHeight); //convert to two decimal places
		
		//calculate range of flight
		double range = (v * v * Math.sin(2 * a)) / G;
		String rangeStr = twoDecPlaces.format(range);
		
		double vX = v * Math.cos(a);
		String vXStr = twoDecPlaces.format(vX);
		
		double vY = (v * Math.sin(a)) - (G * t);
		String vYStr = twoDecPlaces.format(vY);
		
		int Y = 10;
		int X = 800;		
		
		//display properties of the parabolic flight
		g.setColor(Color.CYAN);
		g.drawString("Time of flight : " + tofStr + " seconds", X, Y);
		g.drawString("Maximum Height : " + gHeightStr + "m", X, Y + 15);
		g.drawString("Range			 : " + rangeStr + "m", X, Y + 30);
		
		//coloured intercepts of coordinate axes
		g.setColor(Color.WHITE);
		g.drawLine(x, CANVAS_SIZEy - 20, x, CANVAS_SIZEy);
		g.drawLine(10, (int)y, 22, (int)y);
		g.drawLine(x-2, CANVAS_SIZEy - 20, x-2, CANVAS_SIZEy);
		g.drawLine(10, (int)y - 2, 22, (int)y - 2);
		
		//Coordinate axes
		g.setColor(Color.RED);
		g.drawLine(0, CANVAS_SIZEy - 10, CANVAS_SIZEx, CANVAS_SIZEy - 10);
		g.drawLine(10, 0, 10, CANVAS_SIZEy);		
		
		//velocity vector
		g.setColor(Color.MAGENTA);
		g.drawLine((int)x + 2, (int)y + 5, (int)(x + v * Math.cos(a)), (int)(y - (v * Math.sin(a)) + (G * t)));
		g.drawLine((int)x + 2, (int)y + 15, (int)(x + v * Math.cos(a)), (int)(y - (v * Math.sin(a)) + (G * t)));
		
		//parameters
		if (vY < 0) {
			g.setColor(Color.RED); //for negative y-component of velocity
			g.drawString(vYStr, x - 40, (int)y + 20); //spacing needed between y-coordinates
			g.drawString(vXStr, x - 40, (int)y + 0);
		} else {
			g.setColor(Color.GREEN); //for positive y-component of velocity
			g.drawString(vYStr, x - 40, (int)y + 20);
			g.drawString(vXStr, x - 40, (int)y + 0);	
			
		}	
		
		//draw particle
		g.setColor( Color.ORANGE );
		g.fillOval(x, (int) y, 20, 20);
	}
	
	public static void main(String args[]) {
		//create new AnimationObject
		new Projectile();		
	}
}
