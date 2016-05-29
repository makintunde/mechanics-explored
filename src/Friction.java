import java.awt.*; 				//Abstract Windowing Toolkit
import java.awt.event.*;		//For event handling
import java.text.DecimalFormat; //For decimal formatting
import javax.swing.BoxLayout;	//Layout manager
import javax.swing.JOptionPane; //Used for dialogue boxes
import static java.lang.Double.parseDouble;

/*
 * Mechanics Explored - Friction
 * Copyright (R) Michael Akintunde 2013
 *
 * Latest updates: 07/03/13
 * 	-	added velocity and acceleration functions
 * 	-	added "about" text function
 * 	-	changing "double" datatypes to "double"
 */

@SuppressWarnings("serial")
public class Friction extends Canvas implements Runnable {

	DecimalFormat twoDecPlaces = new DecimalFormat("0.00");
	Planet earth = new Planet(9.8);

	public double ay = 0; 		// Y component of acceleration - x component is negligible since constant
	public double vy = 0; 		// Y component of velocity
	public double mv = 0; 		// Magnitude of velocity
	public double aRough = 0; 	// "Raw" angle value
	public double acc = 0; 		// Acceleration
	public double absAcc = 0;

	//---------------IMPORTANT VARIBLES--------------------
	public double cof = 0.5; 	// Coefficient of friction
	public double a = 0;		// Angle of inclination
	public double m = 0; 		// Mass
	//-----------------------------------------------------

	public int L = 500; 		// Length of "plank"
	public int d = 10; 		// Diameter of ball

	// Initial position of ball
	public int x = 0; // X-Coordinate
	public int y = 0; // Y-Coordinate

	// Used for checking if simulation is running/paused
	public boolean running = false;
	public boolean paused = false;
	public boolean anyErrors = false;

	// Labels and text fields used for
	public Label cofLabel;
	public TextField angleField;
	public TextField velField;

	// Used for displaying about text
	public String name = "Friction";

	// Constructor
	Friction() {
		//housekeeping methods
		setSize(earth.x,earth.y);
		final Frame pictureFrame = new Frame("Friction Simulation"); //make a new frame, with title "Friction Simulation"
		Panel canvasPanel = new Panel(); //create new panel
		canvasPanel.add(this); //add panel
		canvasPanel.setBackground(Color.BLACK); //set background colour to black
		pictureFrame.add(canvasPanel); //add canvasPanel panel to pictureFrame frame

		//add controlPanel panel with start and stop buttons, and a menu bar
		Panel bottomPanel = new Panel();
		Panel controlPanel = new Panel();
		Panel errorPanel = new Panel(); //for errors
		final Choice menu = new Choice();

		//simulation controls
		final Button startButton = new Button("Start");
		Button stopButton = new Button("Stop");
		Button pauseButton = new Button("Pause");

		//create text field for height, angle and velocity entry
		final TextField cofField = new TextField(5);
		final TextField angleField = new TextField(5);
		final TextField massField = new TextField(5);
		//final TextField lengthField = new TextField(5);

		//create labels for height, angle and velocity entry. and for the error panel
		Label cofLabel = new Label("Enter coefficient of friction (mu):");
		Label angleLabel = new Label("Enter angle (a):");
		Label massLabel = new Label("Enter mass (kg):");
		//Label lengthLabel = new Label("Enter plane length (m):");
		final Label errorLabel = new Label("---------------------------Ready----------------------------"); //####################################

		errorPanel.add(errorLabel);
		errorPanel.setForeground(Color.RED);

		//add labels and fields for coefficient of friction, mass, angle and velocity
		controlPanel.add(cofLabel);
		controlPanel.add(cofField);
		controlPanel.add(angleLabel);
		controlPanel.add(angleField);
		controlPanel.add(massLabel);
		controlPanel.add(massField);
		//controlPanel.add(lengthLabel);
		//controlPanel.add(lengthField);

		//add control buttons
		controlPanel.add(startButton);
		controlPanel.add(stopButton);
		controlPanel.add(pauseButton);

		//add error and control panel to bottom
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
		bottomPanel.add(errorPanel);
		bottomPanel.add(controlPanel);

		//add items to menu
		menu.add("Menu");
		menu.add("Help");
		menu.add("About");
		menu.add("Quit");

		pictureFrame.add(bottomPanel, BorderLayout.SOUTH);	//add control panel to the south
		pictureFrame.add(menu, BorderLayout.NORTH);	//add menu to the north
		pictureFrame.pack();
		pictureFrame.setVisible(true); //we would like to see the simulation in action

		//find out whether user closes window
		pictureFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				pictureFrame.setVisible(false);
			}
		});


		//set text for the error label when start button is rolled over
		startButton.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				errorLabel.setText("Click to start simulation.");
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		//set text for the error label when stop button is rolled over
		stopButton.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent ev) {
				errorLabel.setText("Click to stop simulation.");
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		//set text for the error label when pause button is rolled over
		pauseButton.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent eve) {
				errorLabel.setText("Click to pause simulation.");
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		//set text for the error label when canvas panel is rolled over
		canvasPanel.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent even) {
				errorLabel.setText("Ready.");
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});


		//set text for the error label when pause button is rolled over
		pauseButton.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent eve) {
				errorLabel.setText("Click to pause simulation.");
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});


		//find out whether user clicks "Start"
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {

				String massErrStr 	= "Mass error.";
				String massValStr 	= "Mass of " + m + " is not valid.";
				String massRecStr 	= "Try a mass of 10kg.";

				String angleErrStr 	= "Angle error.";
				String angleValStr 	= "Angle of " + a + " is not valid.";
				String angleRecStr 	= "Try an angle of 45 degrees.";

				String cofErrStr 	= "Friction coefficient error.";
				String cofValStr 	= "Friction coefficient of " + cof + " is not valid.";
				String cofRecStr	= "Try a friction coefficient of 0.5.";

				try{
					//get coefficient of friction from user's input
					cof = parseDouble(cofField.getText());

					//get "rough" angle
					aRough = parseDouble(angleField.getText());

					//get mass from user's input value, use it as diameter of ball for visual representation
					m = parseDouble(massField.getText());
				}
				catch(NumberFormatException ne){
					JOptionPane.showMessageDialog(null, "Only numerical values are accepted.");
					return;
				}

				//convert valid angle to radians, for Java's use
				setA(aRough); //validates

				//we need a limit of the diameter of ball, so use this method
				setD(m);

				//error checking
				if ((	valueChecker(m, a, cof) == 1)
					|| (valueChecker(m, a, cof) == 2)
					|| (valueChecker(m, a, cof) == 3)) {
					anyErrors = true;

					//single errors
					if (valueChecker(m, a, cof) == 1) {
						setDialog(massErrStr, massValStr, massRecStr);
					} if (valueChecker(m, a, cof) == 2) {
						setDialog(angleErrStr, angleValStr, angleRecStr);
					} if (valueChecker(m, a, cof) == 3) {
						setDialog(cofErrStr, cofValStr, cofRecStr);

					//double errors
					} if ((valueChecker(m, a, cof) == 1) && (valueChecker(m, a, cof) == 2) && !(valueChecker(m, a, cof) == 3)) {
						setDialog(massErrStr + "\n" + angleErrStr,
								massValStr + "\n" + angleValStr,
								massRecStr + "\n" + angleRecStr);
					} if ((valueChecker(m, a, cof) == 1) && (valueChecker(m, a, cof) == 3) && !(valueChecker(m, a, cof) == 2)) {
						setDialog(massErrStr + "\n" + cofErrStr,
								massErrStr + "\n" + cofValStr,
								massErrStr + "\n" + cofRecStr);
					} if ((valueChecker(m, a, cof) == 2) && (valueChecker(m, a, cof) == 3) && !(valueChecker(m, a, cof) == 1)) {
						setDialog(angleErrStr + "\n" + cofErrStr,
								angleValStr + "\n" + cofValStr,
								angleRecStr + "\n" + cofRecStr);

					//triple error
					} if ((valueChecker(m, a, cof) == 1)
							&& (valueChecker(m, a, cof) == 2)
							&& (valueChecker(m, a, cof) == 3)) {
						setDialog(massErrStr + "\n" + angleErrStr + "\n" + cofErrStr,
								massValStr + "\n" + angleValStr + "\n" + cofValStr,
								massRecStr + "\n" + angleRecStr + "\n" + cofRecStr);
					}
				} else {
					anyErrors = false; //there are not any errors
				}

				if (!paused && !anyErrors) {

					running = true; //simulation is now running

					startButton.setEnabled(false); //de-activate the start button when it already has been clicked on

					errorLabel.setText("Simulation started."); //change text of errorPanel

					earth.t = 0; //reset time

				} else {
					paused = false;
					startButton.setEnabled(true);
				}
				//simulation is not paused
			}
		});

		//find out whether user clicks "Stop"
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				startButton.setEnabled(true);
				errorLabel.setText("---------------------------Ready----------------------------");
				running = false; //simulation is not running anymore
				//reset variables
				resetVars();
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
				String c ;
				c =  (String) menu.getSelectedItem();
				if (c == "Quit") {
					System.exit(0);
				} else if (c == "Help") {
					//Initiate help methods
					System.out.println("Help button clicked...");
				} else if (c == "About") {
					displayAboutText(name);
				}
			}
		});

		Thread myThread = new Thread(this); //create new thread
		myThread.start(); //start thread
		System.out.println("Friction simuation started"); //is printed on console window, as a confirmation
	}

	//Display about text
	public void displayAboutText(String n) {
		new AboutText(n);
	}

	//used for calculation position of particle in the (x,y) plane
	//named posUtah, as algorithm was inspired by University of Utah's description
	public double posUtah (double T) {
		double p = 0;
		if (earth.t < 0) {
			//do nothing
		} else if ((cof * Math.cos(a)) > (Math.sin(a))) {
			//do nothing
		} else if (( .5 * earth.G * ( Math.sin(a) - (cof * Math.cos(a))  ) * Math.pow(T,2)   ) < L) {
			//accelerate particle down plane, i.e. increase distance from starting point
			p = .5 * earth.G * ( Math.sin(a) - (cof * Math.cos(a))  ) * Math.pow(T,2);
		} else {
			p = L; //put particle (constantly) at the end of the plane
		}
		return p; //distance down plane
	}

	public double physicalTime() {
		//double acc = earth.G * ( Math.sin(a) - (cof * Math.cos(a)));
		double pTime = Math.sqrt((2 * L)/(a));
		return pTime;
	}

	public void run() {

		double dt = .1; //a "second"

		//This "true" boolean is used to loop the section indefinitely
		while (true) {
			if (running && (y < L * Math.sin(a))) {
				for (int i = 0; i < 0.1/dt; i++) { //slows simulation down and reduces flickering

					x = (int)( posUtah(earth.t) * Math.cos(a));
					y = (int)( posUtah(earth.t) * Math.sin(a));
					mv = Math.sqrt(2 * earth.G * y);

					//x = (int)(pos(t) * Math.cos(a));
					//y = (int)(pos(t) * Math.sin(a));

					//increment time by "delta" t
					earth.t += dt;
				}
				//paint items again
				repaint();
			}

			try {Thread.sleep(30);} catch (InterruptedException e){} //wait for 30 milliseconds
		}
	}

	public void setD (double M) { //use mass to set diameter
		d = (int) ((M < 500) ? M : 50);
	}

	public void setA (double A) {
		double aTemp = ((A < 90) && (A > 0)) ? A : 45;
		a = (Math.PI/180) * aTemp;	//conversion
	}

	public void setC (double C) {
		double cTemp = 0.5;
		if ((C < 1) && (C > 0)) {
			cTemp = C;
		} else {
			cTemp = 0.5;
		}
		cof = cTemp;
	}

	public void resetVars() { //resets all variables
		earth.t = 0;
		ay = 0;
		vy = 0;
		x = 0;
		y = 0;
		acc = 0;
	}

	public void setDialog(String a, String b, String c) {
		JOptionPane.showMessageDialog(null, a + "\n" + b + "\n" + c);
	}

	public int valueChecker(double m, double a, double c) {
		int check = 0;

		/*-----------------------
		 * 1 = mass input error
		 * 2 = angle input error
		 * 3 = cof input error
		 -----------------------*/
		if (!((m < 500) && (m > 0))){ // if mass is not positive and not less than 500kg.
			check = 1; //return a mass error
		} else if (!((a < 90) && (a >= 0))) { //if angle is not positive and not less than 90 degrees
			check = 2; //return an angle error
		} else if (!((c <= 1) && (c >= 0))) { //if cof is not less than 1 and/or not positive
			check = 3; //return a friction coefficient error
		} else {
			check = 0; //no errors found.
		}

		return check;
	}

	public void paint(Graphics g) {

		//middle coordinates of balls, used for position component lines
		int midXCoordOfBall = x + (int)(d/2);
		int midYCoordOfBall = y + (int)(d/2);

		int YPos = (int)( L * Math.sin(a) ); //Height of plank
		int XPos = (int)( L * Math.cos(a) ); //Width of plank

		//-------------------FORCES-----------------------------//

		//friction up plane - capital "F" denotes 'Force'
		double upF = cof * m * earth.G * Math.cos(a);
		String upStr = twoDecPlaces.format(upF); //round to two decimal places

		//weight down plane
		double downF = m * earth.G * Math.sin(a);
		String downStr = twoDecPlaces.format(downF); //round to two decimal places

		//friction vector components
		int upX = (int)( upF * Math.cos(a) ); //x component
		int upY = (int)( upF * Math.sin(a) ); //y component

		//-------------------FORCES-----------------------------//

		//Acceleration
		boolean moreForceUpPlane; //checks whether there is more force going up the plane
		//Change moreForceUpPlane boolean variable subject to the following conditions:
		if (upF > downF) {
			moreForceUpPlane = true;
		} else {
			moreForceUpPlane = false;
		}

		//acceleration
		double accTemp = (downF - upF)/m; //acceleration used for conditional statement below...
		double acc = moreForceUpPlane? 0.00 : accTemp; //is there more force up the plane? return 0 else return tempAcc

		String accStr = (absAcc == 0) ? "0.00" : twoDecPlaces.format(absAcc) ;
		absAcc = Math.abs(acc); //absolute value - negative accelerations are not of any use here

		//velocity
		String vStr = (mv == 0) ? "0.00" : twoDecPlaces.format(mv);

		//weight vector components
		int wX = (int)( downF * Math.cos(a)); //x component
		int wY = (int)( downF * Math.sin(a)); //y component

		int compLen = 10; //length of position components

		int [] X = {0, 0, XPos}; //x coordinates of plank polygon
		int [] Y = {0, YPos, YPos}; //y coordinates of plank polygon

		int textX = earth.x - 200; //x coordinate of top line of text
		int textY = 30;	//y coordinate of top line of text



		//draw plank as polygon
		g.setColor(Color.darkGray);
		g.fillPolygon(X,Y,3);

		//draw position components on coordinate axes
		g.setColor(Color.WHITE);
		//g.drawLine(x1, y1, x2, y2);
		g.drawLine(midXCoordOfBall, YPos, midXCoordOfBall, compLen + YPos); //horizontal component
		g.drawLine(midXCoordOfBall + 2, YPos, midXCoordOfBall + 2, compLen + YPos);
		g.drawLine(10, midYCoordOfBall, 10 + compLen, midYCoordOfBall);	//vertical component
		g.drawLine(10, midYCoordOfBall + 2, 10 + compLen, midYCoordOfBall + 2);

		//Coordinate axes
		g.setColor(Color.RED);
		g.drawLine(0, (int) YPos, earth.x, (int) YPos); // "x" axis
		g.drawLine(10, 0, 10, 10 + (int) YPos);	// "y" axis

		//Force vectors
		//µR i.e. Friction
		g.setColor(Color.MAGENTA);
		g.drawLine(midXCoordOfBall, midYCoordOfBall, midXCoordOfBall - upX, midYCoordOfBall - upY);

		//Weight
		g.setColor(Color.YELLOW);
		g.drawLine(midXCoordOfBall, midYCoordOfBall, midXCoordOfBall + wX, midYCoordOfBall + wY);



		/*draw plank as a line
		g.setColor(Color.BLUE);
		g.drawLine(0, 0, (int)(L * Math.cos(a)), (int)(L * Math.sin(a)));
		g.drawLine(0, 0, (int)(L * Math.cos(a)), (int)(L * Math.sin(a)));
		*/

		/*for a thicker line...
		 *  g.drawLine(midXCoordOfBall, midYCoordOfBall - (int)(d/2), midXCoordOfBall - fricX, midYCoordOfBall - fricY);
		 *  g.drawLine(midXCoordOfBall, midYCoordOfBall - (int)(d/2), midXCoordOfBall + wX, midYCoordOfBall + wY);
		 */

		//draw particle
		g.setColor( Color.ORANGE );
		g.fillOval(x, y, d, d);	//where mass = d, and x and y coords are set in run() method

		//finally, display properties of movement
		g.setColor(Color.CYAN);
		g.drawString("Length of plane: " + L + "m", textX, textY);
		g.drawString("(Frictional) force up plane: " + upStr + " N", textX, textY + 15);
		g.drawString("(Weight) force down plane: " + downStr + " N", textX, textY + 30);
		g.drawString("Acceleration: " + accStr + " m/s/s", textX, textY + 45);
		g.drawString("Velocity: " + vStr + " m/s", textX, textY + 60); //increments of 15 px for even spacing
	}

	public static void main(String args[]) {
		//create new Friction() Object
		new Friction();
	}

}
