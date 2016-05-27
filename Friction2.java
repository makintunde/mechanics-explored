
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class Friction2 extends Canvas implements Runnable {	
	
	DecimalFormat twoDecPlaces = new DecimalFormat("0.00");
	
	//public double y = 10; 
	public double t = 0; //time
	public double ay = 0; //y component of acceleration - x component is negligible since constant
	public double vy = 0; //y component of velocity
	public double aRough = 0; //round (not rounded) acceleration value
	public double a = 0; //acceleration
	public double v = 0; //velocity
	public double G = 9.8;	 //gravity, not to get confused with small "g" for paintComponent method
	public double cof = 0.5; //coefficient of friction
	
	public int L = 500; //length of "plank"
	
	//size of canvas
	public int CANVAS_SIZEy = 600;
	public int CANVAS_SIZEx = 900;	
	
	//initial position of ball
	public int x = 0;
	public int y = 0;
	
	//can be used to change colour of an item in real time - not relevant for now
	/*public int colx = 0;
	public int coly = 0;
	public int colz = 0;*/	
	
	//check simulation is running, or is paused
	public boolean running = false;
	public boolean paused = false;
	public boolean projecting = false;
	
	//these must be public as they are used by internal methods
	public Label cofLabel;
	public TextField angleField;
	public TextField velField;
	
	//constructor
	Friction2() {		
		//housekeeping methods
		setSize(CANVAS_SIZEx,CANVAS_SIZEy);
		Frame pictureFrame = new Frame("Friction Simulation"); //make a new frame, with title "Friction Simulation"	
		Panel canvasPanel = new Panel(); //create new panel
		canvasPanel.add(this); //add panel
		canvasPanel.setBackground(Color.BLACK); //set background colour to black
		pictureFrame.add(canvasPanel); //add canvasPanel panel to pictureFrame frame	
		
		//add controlPanel panel with start and stop buttons, and a menu bar
		Panel controlPanel = new Panel(); 
		final Choice menu = new Choice();
		final Choice type = new Choice();
		
		//simulation controls
		Button startButton = new Button("Start");
		Button stopButton = new Button("Stop");
		Button pauseButton = new Button("Pause");	
		
		//create text field for height, angle and velocity entry
		final TextField cofField = new TextField(5);
		final TextField velField = new TextField(5);
		final TextField angleField = new TextField(5);
		final TextField massField = new TextField(5);
		
		//create labels for height, angle and velocity entry
		Label cofLabel = new Label("Enter coefficient of friction (�):");		
		Label velLabel = new Label("Enter velocity (v):");
		Label angleLabel = new Label("Enter angle (a):");
		Label massLabel = new Label("Enter mass (kg):");
		
		//add labels and fields for coefficient of friction, mass, angle and velocity
		controlPanel.add(cofLabel);
		controlPanel.add(cofField);
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
		menu.addItem("Menu");
		menu.addItem("Help");
		menu.addItem("Quit");
		
		//add items to "type" menu
		type.addItem("Drop");
		type.addItem("Push");
		
		pictureFrame.add(type, BorderLayout.EAST);
		pictureFrame.add(controlPanel, BorderLayout.SOUTH);	//add control panel to the south
		pictureFrame.add(menu, BorderLayout.NORTH);	//add menu to the north
		pictureFrame.pack();
		pictureFrame.setVisible(true); //we would like to see the simulation in action
		
		//find out whether user closes window
		pictureFrame.addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});	
		
		//find out whether user clicks "Start"
		startButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent ev) {
				
				running = true; //simulation is now running
				if (!paused) {
					t = 0;		
					
					//get coefficient of friction from user's input
					cof = (int)(Double.parseDouble(cofField.getText()));	
					
					//get "rough" angle
					aRough = Double.parseDouble(angleField.getText());
					setAngle(aRough); //validating function, for angles
					
					
					
					//cast input of velField into a suitable velocity integer
					v = Double.parseDouble(velField.getText());			
					
					/*
					
					//Java's canvas coordinates are upside-down, so converter is needed
					//cast input of heightField into a suitable height integer
					initY = CANVAS_SIZEy - (int)(Double.parseDouble(heightField.getText()));
					//cast input of angleField into a suitable angle integer
					aRough = Double.parseDouble(angleField.getText());
					//Java works in radians for Math class
					a = (Math.PI/180) * aRough;
					//cast input of velField into a suitable velocity integer
					v = Double.parseDouble(velField.getText());		
					
				    */
				}
				//simulation is not paused
				paused = false;						
			}			
		});
		
		//find out whether user clicks "Stop"
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				running = false; //simulation is not running anymore	
				cof = (int)(Double.parseDouble(cofField.getText()));
				//reset variables
				t = 0;	
				ay = 0;
				vy = 0;
				x = 0;
				y = 0;
			}			
		});
		
		//find out whether user clicks "Pause"
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent even) {
				//simulation is not running, but is now paused
				running = false; 
				paused = true;				
			}			
		});
		
		//find out whether user's menu option is "c"
		menu.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				String c = (String) menu.getSelectedItem();
				if (c == "Quit") {
					System.exit(0); //exit system
				}
			}
		});	
		
		//find out whether user's "type" menu option is "c"
		type.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent events) {
				String c = (String) type.getSelectedItem();
				//find out whether user wants to "push" the ball, or "drop" it
				if (c == "Push") {
					projecting = true;
					//do required type of simulation
				} else {
					projecting = false;
				}
			}
		});	
		
		Thread myThread = new Thread(this); //create new thread
		myThread.start(); //start thread
		System.out.println("Friction simuation started"); //is printed on console window, as a confirmation
	}
	
	public double pos (double T) {
		double p = 0;
		
		if ((y < (L - 10) * Math.sin(a)) && (projecting == false)) {
			p = .5 * G * (Math.sin(a) - (cof * Math.cos(a))) * Math.pow(T, 2);
 		} else {
 			p = L ;
 		}
		
	
		
		if (projecting == true) {
			p = (v * T) - (.5 * G * ((cof * Math.cos(a)) + Math.sin(a)) * Math.pow(T, 2));
		}
	
		return p;
	}
	
	public void run() {
			
		double dt = .1; //a "second"		
		double ay = -G; //resolving "up" as positive, gravity acts opposite this so is negative
		int time = 0;
		//This "true" boolean is used to loop the section indefinitely
		while (true) {
			if (running) { 
				for (int i = 0; i < 0.1/dt; i++) { //slows simulation down and reduces flickering
					
					if (!projecting) {
						x = (int)(pos(t) * Math.cos(a));
						y = (int)(pos(t) * Math.sin(a));						
					} else {
						x = (CANVAS_SIZEx - 20) -(int)(pos(t) * Math.cos(a));
						y = (CANVAS_SIZEy - 20) -(int)(pos(t) * Math.sin(a));
					}						
					
					//increment time by delta t
					t += dt;					
				}	
				//paint items again
				repaint();
				
				time += 1;
				System.out.println("Time: " + time);
			}
			
			try {Thread.sleep(30);} catch (InterruptedException e){} //wait for 30 milliseconds
			
		}
	}	
	
	// check that angle is less than 90, and greater than 0 degrees. Anything else - return 45 as "default" angle. (Pi/4 radians) 
	public void setAngle (double A) {		
		double rawA = ((A < 90) && (A > 0)) ? A : 45;  
		//convert angle to radians, for Java's use
		a = (Math.PI/180) * rawA;	
	}
	
	//check for a non-zero positive decimal, which is less than 1. For a valid coefficient of friction.
	public void setCof (double C) {
		cof = ((C > 0) && (C < 1)) ? C : 0.5;	//set coefficient of friction	
	}
	
	public void paint(Graphics g) {
		
		/*
		 * ------- PRINT STRINGS IN TWO D.P. -------------
		 * 
		double vX = v * Math.cos(a);
		String vXStr = twoDecPlaces.format(vX);
		
		double vY = (v * Math.sin(a)) - (G * t);
		String vYStr = twoDecPlaces.format(vY);
		
		g.setColor(Color.CYAN);
		g.drawString("Vel(X): " + vX, CANVAS_SIZEx - 50, 50);
		g.drawString("Vel(Y): " + vY, CANVAS_SIZEy - 50, 60);
		
		*/
		
		double A = .5 * G * (Math.sin(a) - (cof * Math.cos(a)));		
		
		//g.drawLine(0, 0, (int)(L * Math.cos(a)), (int)(L * Math.sin(a)));
		
		int Y = 10;
		int X = 800;		
		
		double YPos = L * Math.sin(a);
		double XPos = L * Math.cos(a);
		
		//draw plank
		g.setColor(Color.BLUE);		
		if (!projecting) {
			g.drawLine(0, 0, (int)(XPos), (int)(YPos)); //downward sloping
		} else {
			g.drawLine(CANVAS_SIZEx, CANVAS_SIZEy, CANVAS_SIZEx - (int)XPos, CANVAS_SIZEy - (int)(YPos)); //upward sloping
		}		
		
		//draw position components on coordinate axes
		g.setColor(Color.WHITE);
		//g.drawLine(x1, y1, x2, y2);
		g.drawLine(x, 	(int)(YPos- 10), 	x, 		(int) YPos); 	//horizontal component
		g.drawLine(x-2, (int)(YPos - 10), 	x-2, 	(int) YPos);
		g.drawLine(10, 	(int)y, 			20, 	(int) y); 		//vertical component	
		g.drawLine(10, 	(int)y - 2, 		20, 	(int) y - 2);
		
		//Coordinate axes
		g.setColor(Color.RED);
		g.drawLine(0, (int) YPos, CANVAS_SIZEx, (int) YPos); // "x" axis
		g.drawLine(10, 0, 10, 10 + (int) YPos);	// "y" axis	
		
		//velocity vector
		/*
		g.setColor(Color.MAGENTA);
		g.drawLine((int)x + 2, (int)y + 5, (int)(x + A * t * Math.cos(a)), (int)(y - (A * t * Math.sin(a))));
		g.drawLine((int)x + 2, (int)y + 15, (int)(x + A * t * Math.cos(a)), (int)(y - (x + A * t * Math.cos(a))));
		*/
		
		//draw particle
		g.setColor( Color.ORANGE );
		g.fillOval(x, y, 15, 15);
	}
	
	public static void main(String args[]) {
		//create new AnimationObject
		new Friction2();		
	}
	
}
