import java.awt.*; 				//Abstract Windowing Toolkit
import java.awt.event.*;		//For event handling
import java.text.DecimalFormat; //For decimal formatting
import javax.swing.BoxLayout;	//Layout manager
import javax.swing.JOptionPane; //Used for dialogue boxes
import static java.lang.Double.parseDouble;
import java.util.Set;
import java.util.HashSet;

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

	public static enum errorType { MASS_ERR, ANG_ERR, COF_ERR, NONE };

	public double ay = 0; 	   	// Y component of acceleration
	public double vy = 0; 	   	// Y component of velocity
	public double mv = 0; 		  // Magnitude of velocity
	public double aRough = 0; 	// "Raw" angle value
	public double acc = 0; 	  	// Acceleration
	public double absAcc = 0;   // Magnitude of acceleration

	//---------------IMPORTANT VARIBLES--------------------
	public double cof = 0.5; 	// Coefficient of friction
	public double a = 0;	  	// Angle of inclination
	public double m = 0; 	  	// Mass
	//-----------------------------------------------------

	public int L = 500; // Length of "plank"
	public int d = 10; 	// Diameter of ball

	// Initial position of ball
	public int x = 0; // X-Coordinate
	public int y = 0; // Y-Coordinate

	// For checking if simulation is running/paused
	public boolean running, paused, anyErrors;

	// Labels and text fields used for
	public Label cofLabel;
	public TextField angleField, velField;

	// Used for displaying about text
	public String name = "Friction";

	// Constructor
	Friction() {
		//housekeeping methods
		setSize(earth.x,earth.y);
		final Frame pictureFrame = new Frame("Friction Simulation");

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

		//create labels for height, angle and velocity entry. and for the error panel
		Label cofLabel = new Label("Enter coefficient of friction (mu):");
		Label angleLabel = new Label("Enter angle (a):");
		Label massLabel = new Label("Enter mass (kg):");
    String dashes = "---------------------------";
    Label errorLabel = new Label(dashes + "Ready" + dashes);

		errorPanel.add(errorLabel);
		errorPanel.setForeground(Color.RED);

		//add labels and fields for coefficient of friction, mass, angle and velocity
		controlPanel.add(cofLabel);
		controlPanel.add(cofField);
		controlPanel.add(angleLabel);
		controlPanel.add(angleField);
		controlPanel.add(massLabel);
		controlPanel.add(massField);

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

		// Add control panel to the south
		pictureFrame.add(bottomPanel, BorderLayout.SOUTH);

		// Add menu to the north
		pictureFrame.add(menu, BorderLayout.NORTH);

		pictureFrame.pack();
		pictureFrame.setVisible(true);

		// Find out whether user closes window
		pictureFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				pictureFrame.setVisible(false);
			}
		});

		// Set text for the error label when start button is rolled over
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

		// Set text for the error label when pause button is rolled over
		pauseButton.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent eve) {
				errorLabel.setText("Click to pause simulation.");
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		// Set text for the error label when canvas panel is rolled over
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

				final String massErrStr	= "\nMass value invalid.\n" +
																	"Try a mass of 10kg instead.\n";

				final String angleErrStr = "\nAngle value invalid.\n" +
																  "Try an angle of 45 degrees instead.\n";

				final String cofErrStr = "\nFriction coefficient invalid.\n" +
									               "Try a friction coefficient of 0.5 instead.\n";
        String finalErrStr = "";

				try {
					// Get coefficient of friction from user's input.
					cof = parseDouble(cofField.getText());

					// Get "rough" angle.
					aRough = parseDouble(angleField.getText());

					// Get mass from user's input value, use it as diameter of ball for
					// visual representation.
					m = parseDouble(massField.getText());
				} catch(NumberFormatException ne){
					JOptionPane.showMessageDialog(null, "Only numerical values are accepted.");
					return;
				}

				// Convert valid angle to radians, for Java's use.
				setA(aRough);

				// We need a limit of the diameter of ball, so use this method.
				setD(m);

				// Error checking
				Set<errorType> checkedValues = valueChecker(m, aRough, cof);
				anyErrors = !checkedValues.isEmpty();
        System.out.println(checkedValues.size());
				if (anyErrors) {
          for (errorType t : checkedValues) {
            switch (t) {
              case MASS_ERR:
                finalErrStr += massErrStr;
                break;
              case ANG_ERR:
                finalErrStr += angleErrStr;
                break;
              case COF_ERR:
                finalErrStr += cofErrStr;
                break;
            }
          }
          setDialog(finalErrStr);
				}

				if (!paused && !anyErrors) {

					// Simulation is now running.
					running = true;

					// De-activate the start button when it already has been clicked on.
					startButton.setEnabled(false);

					// Change text of errorPanel.
					errorLabel.setText("Simulation started.");

					earth.t = 0; // Reset time

				} else {
					paused = false;
					startButton.setEnabled(true);
				}
			}
		});

		//find out whether user clicks "Stop"
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				startButton.setEnabled(true);
				String dashes = "---------------------------";
				errorLabel.setText(dashes + "Ready" + dashes);
				running = false; //simulation is not running anymore
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
		System.out.println("Friction simuation started");
	}

	//Display about text
	public void displayAboutText(String n) {
		new AboutText(n);
	}

	//used for calculation position of particle in the (x,y) plane
	//named posUtah, as algorithm was inspired by University of Utah's description
	public double posUtah (double T) {
		double factor = 0.5 * earth.G * (Math.sin(a) - (cof * Math.cos(a))) * T*T;
		return factor < L ? factor : L;
	}

	public double physicalTime() {
		//double acc = earth.G * ( Math.sin(a) - (cof * Math.cos(a)));
		double pTime = Math.sqrt((2 * L)/(a));
		return pTime;
	}

	public void run() {

		double dt = .1; //a "second"

		while (true) {
			if (running && (y < L * Math.sin(a))) {
				for (int i = 0; i < 0.1/dt; i++) {
					//slows simulation down and reduces flickering
					x = (int)(posUtah(earth.t) * Math.cos(a));
					y = (int)(posUtah(earth.t) * Math.sin(a));
					mv = Math.sqrt(2 * earth.G * y);

					// Increment time by "delta" t
					earth.t += dt;
				}
				// Paint items again
				repaint();
			}

			try {Thread.sleep(30);} catch (InterruptedException e){} //wait for 30 milliseconds
		}
	}

	public void setD (double M) { //use mass to set diameter
		d = (int) ((M < 500) ? M : 50);
	}

	public void setA (double A) {
		a = Math.PI * A / 180;	//conversion
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

	public void setDialog(String str) {
		JOptionPane.showMessageDialog(null, str);
	}

	public Set<errorType> valueChecker(double m, double a, double c) {

    Set<errorType> errors = new HashSet<>();

		if (!(m > 0 && m < 500)) { // if mass is not positive and not less than 500kg.
      System.out.println("added mass err");
			errors.add(errorType.MASS_ERR); //return a mass error
    }
    
    System.out.println("a = " + a);
	  if (!(a > 0 && a < 90)) { //if angle is not positive and not less than 90 degrees
      System.out.println("added mass err");
			errors.add(errorType.ANG_ERR); 
    }
	  if (!(c >= 0 && c <= 1)) { //if cof is not less than 1 and/or not positive
      System.out.println("added mass err");
			errors.add(errorType.COF_ERR); //return a friction coefficient error
	  }

    return errors;
  }

	public void paint(Graphics g) {

		// Middle coordinates of balls, used for position component lines
		int midXCoordOfBall = x + (int) (d/2);
		int midYCoordOfBall = y + (int) (d/2);

		int YPos = (int) (L * Math.sin(a)); // Height of plank
		int XPos = (int) (L * Math.cos(a)); // Width of plank

		//-------------------FORCES-----------------------------//

		//friction up plane - capital "F" denotes 'Force'
		double upF = cof * m * earth.G * Math.cos(a);
		String upStr = twoDecPlaces.format(upF); //round to two decimal places

		//weight down plane
		double downF = m * earth.G * Math.sin(a);
		String downStr = twoDecPlaces.format(downF); //round to two decimal places

		//friction vector components
		int upX = (int) (upF * Math.cos(a)); //x component
		int upY = (int) (upF * Math.sin(a)); //y component

		//-------------------FORCES-----------------------------//

		// Acceleration
		boolean moreForceUpPlane; // checks whether there is more force going up the plane
		// Change moreForceUpPlane boolean variable subject to the following conditions:
		if (upF > downF) {
			moreForceUpPlane = true;
		} else {
			moreForceUpPlane = false;
		}

		// Acceleration
		double accTemp = (downF - upF) / m;
		double acc = moreForceUpPlane? 0.00 : accTemp;

		String accStr = (absAcc == 0) ? "0.00" : twoDecPlaces.format(absAcc) ;
		absAcc = Math.abs(acc);

		//velocity
		String vStr = (mv == 0) ? "0.00" : twoDecPlaces.format(mv);

		//weight vector components
		int wX = (int) (downF * Math.cos(a)); //x component
		int wY = (int) (downF * Math.sin(a)); //y component

		int compLen = 10; //length of position components

		int X[] = {0, 0, XPos}; //x coordinates of plank polygon
		int Y[] = {0, YPos, YPos}; //y coordinates of plank polygon

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

		// Coordinate axes
		g.setColor(Color.RED);
		g.drawLine(0, (int) YPos, earth.x, (int) YPos); // "x" axis
		g.drawLine(10, 0, 10, 10 + (int) YPos);	// "y" axis

		// Force vectors
		// µR i.e. Friction
		g.setColor(Color.MAGENTA);
		g.drawLine(midXCoordOfBall, midYCoordOfBall, midXCoordOfBall - upX, midYCoordOfBall - upY);

		// Weight.
		g.setColor(Color.YELLOW);
		g.drawLine(midXCoordOfBall, midYCoordOfBall, midXCoordOfBall + wX, midYCoordOfBall + wY);

		// Draw particle.
		g.setColor( Color.ORANGE );
		g.fillOval(x, y, d, d);	//where mass = d, and x and y coords are set in run() method

		// Finally, display properties of movement.
		g.setColor(Color.CYAN);
		g.drawString("Length of plane: " + L + "m", textX, textY);
		g.drawString("(Frictional) force up plane: " + upStr + " N", textX, textY + 15);
		g.drawString("(Weight) force down plane: " + downStr + " N", textX, textY + 30);
		g.drawString("Acceleration: " + accStr + " m/s/s", textX, textY + 45);
		g.drawString("Velocity: " + vStr + " m/s", textX, textY + 60);
	}

	public static void main(String args[]) {
		new Friction();
	}

}
