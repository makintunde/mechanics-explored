import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import static java.lang.Double.parseDouble;

import javax.swing.JOptionPane;

/*
 * Mechanics Explored - Restitution
 * Copyright (R) Michael Akintunde 2013
 */

@SuppressWarnings("serial")
public class Restitution extends Canvas implements Runnable {

	//create a new planet with its own gravity and time.
	//parameter sets gravity to 9.8, even though it will make no difference to
	//simulation's output (velocity is constant during particle's motion)
	Planet earth = new Planet(9.8);

	//create two new particles
	rParticle p[] = {
			new rParticle(), //first...
			new rParticle()  //..and second
	};

	double cor = 0;

	private static final int UPDATE_RATE = 30; //milliseconds

  //check simulation is running, or is paused
  public boolean running, paused;

  //these must be public as they are used by internal methods
  public Label corLabel;
  public TextField velField1, velField2, massField1, massField2;

	Timer timer = new Timer(); // <--- find out how to get to work?

	public String name = "Restitution";

	//constructor
	Restitution() {
		//housekeeping methods
		setSize(earth.x,earth.y); //..to the size of "earth"
		final Frame pictureFrame = new Frame("Restitution Simulation"); //make a new frame, with title "Restitution Simulation"
		Panel canvasPanel = new Panel(); //create new panel for canvas
		canvasPanel.add(this); //add panel
		canvasPanel.setBackground(Color.BLACK); //set background colour of canvas to black
		pictureFrame.add(canvasPanel); //add canvasPanel panel to pictureFrame frame

		//add controlPanel panel with start and stop buttons, and a menu bar
		Panel controlPanel = new Panel();
		final Choice menu = new Choice();

		//simulation controls
		final Button startButton = new Button("Start");
		Button stopButton = new Button("Stop");
		Button pauseButton = new Button("Pause");

		//create text field for height, angle and velocity entry
		final TextField corField = new TextField(5);
		final TextField velField1 = new TextField(5);
		final TextField velField2 = new TextField(5);
		final TextField massField1 = new TextField(5);
		final TextField massField2 = new TextField(5);

		//create labels for height, angle and velocity entry
		Label corLabel = new Label("Enter coefficient of restitution (e):");
		Label velLabel1 = new Label("Enter v1");
		Label velLabel2 = new Label("Enter v2");
		Label massLabel1 = new Label("Enter m1");
		Label massLabel2 = new Label("Enter m2");

		//add labels and fields for coefficient of , mass, angle and velocity
		controlPanel.add(corLabel);
		controlPanel.add(corField);
		controlPanel.add(velLabel1);
		controlPanel.add(velField1);
		controlPanel.add(velLabel2);
		controlPanel.add(velField2);
		controlPanel.add(massLabel1);
		controlPanel.add(massField1);
		controlPanel.add(massLabel2);
		controlPanel.add(massField2);

		//add control buttons
		controlPanel.add(startButton);
		controlPanel.add(stopButton);
		controlPanel.add(pauseButton);

		//add items to menu
		menu.add("Menu");
		menu.add("About");
		menu.add("Help");
		menu.add("Quit");

		pictureFrame.add(controlPanel, BorderLayout.SOUTH);     //add control panel to the south
		pictureFrame.add(menu, BorderLayout.NORTH);     //add menu to the north
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
				System.out.println("Start button clicked.");

				try {

					//get coefficient of restitution from user's input
					cor = parseDouble(corField.getText());
					setR(cor); //validate the coefficient of restitution


					//get values for velocities and masses
					p[0].u = parseDouble(velField1.getText());
					p[1].u = -parseDouble(velField2.getText());
					p[0].m = (int) parseDouble(massField1.getText());
					p[1].m = (int) parseDouble(massField2.getText());

					//setting up positions and validating input values
					for (int i = 0; i < 2; i++) {
						boolean dir = ((i == 0)? false : true); //true = (<----), false = (---->)
						//if we are on the first particle, set its direction to (<----). If on second, dir = (---->)
						p[i].setU(p[i].u, dir);
						p[i].setM(p[i].m);
						p[i].setInitPos(i + 1);
					}

				} catch (NumberFormatException ne2) {
					showErrorMessage();
					return;
				}

				running = true; //simulation is now running

				if (!paused) {
					startButton.setEnabled(false); //de-activate the start button when it already has been clicked on

				}
				//"un-pause" simulation
				paused = false;
			}
		});

		//find out whether user clicks "Stop"
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				startButton.setEnabled(true);
				System.out.println("Stop button clicked.");
				running = false; //simulation is not running anymore
				//cor = (int)(parseDouble(corField.getText()));
				//reset variables
				for (int i = 0; i < 2; i++) {
					p[i].resetVars();
				}
				cor = 0;
			}
		});

		//find out whether user clicks "Pause"
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent even) {

				//simulation is not running, but is now paused
				startButton.setEnabled(true); //re-activate the start button to re-play
				System.out.println("Pause button clicked.");

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
					pictureFrame.setVisible(false);
					c = "";
				}
				if (c == "About") {
					displayAboutText(name);
					c = "";
				}
			}
		});

		Thread myThread = new Thread(this); //create new thread
		myThread.start(); //start thread
		System.out.println("Restitution simuation started"); //is printed on console window, as a confirmation
	}

	public void showErrorMessage() {
		JOptionPane.showMessageDialog(null, "Only numerical values are accepted.");
	}

	public void displayAboutText(String n) {
		new AboutText(n);
	}

	public void setDialog(String s) {
		JOptionPane.showMessageDialog(null, s);
	}

	//Calculates new velocity of particle after collision with other particle
	public double changeVel1(rParticle a, rParticle b, double e) {
		double v = ( (a.m * a.u) + (b.m * b.u) + (b.m * e)*(b.u - a.u) ) / (a.m + b.m);
	return v;
	}

	//Calculates new velocity of particle after collision with other particle
	public double changeVel2(rParticle a, rParticle b, double e) {
		double v = ( (a.m * a.u) + (b.m * b.u) + (a.m * e)*(a.u - b.u) ) / (a.m + b.m);
	return v;
	}

	//collision between particle a and b?
	public boolean collision(rParticle a, rParticle b) {
		if ( (a.x + a.m) > b.x ) {
			return true;
		} else {
			return false;
		}
	}

	//collision with left wall?
	public boolean collLeftWall(rParticle a) {
		if ( a.x < 0) {
			return true;
		} else {
			return false;
		}
	}

	//collision with right wall?
	public boolean collRightWall(rParticle b) {
		if ( (b.x + b.m) > earth.x ) {
			return true;
		} else {
			return false;
		}
	}

	public void run() {

		//This "true" boolean is used to loop the section indefinitely
		while (true) {
			if (running) {

				p[0].x += p[0].u;
				p[1].x += p[1].u;

				if (collLeftWall(p[0])) {
					p[0].u = wallBouncedVel(p[0].u, cor); //bounce ball and change velocity,
													  //for particle a
					p[0].x = 1; //reposition ball
				} else if (collRightWall(p[1])) {
					p[1].u = wallBouncedVel(p[1].u, cor); //bounce ball and change velocity,
													   //for particle b
					p[1].x = earth.x - p[1].m; //to reduce errors
				}
				if (collision(p[0], p[1])) { //if there's a collision between particle and b...
					double uTemp1 = changeVel1(p[0],p[1], cor); //change their velocities...
					double uTemp2 = changeVel2(p[0],p[1], cor); //according to value of "cor"
					p[0].u = uTemp1;
					p[1].u = uTemp2;
					p[0].x = p[1].x - p[0].m;  //move particle far away from each other to avoid setting of any errors
				}

				//paint items again
				repaint();

			}
			try {Thread.sleep(UPDATE_RATE);} catch (InterruptedException e){} //wait for 30 milliseconds

		}
	}

	//find velocity of particle after collision with wall
	public double wallBouncedVel (double v, double e) {
		double vTemp;
		vTemp = -e * v;
		return vTemp;
	}

	//set coefficient of restitution
	public void setR (double R) {
		double rTemp =  ((R < 1) && (R > 0)) ? R : 0.5;
		cor = rTemp;
	}

	//used to set the appropriate format for the energy string in parameter output area
	public String setEnergyString () {
		String s = "";
		return s;
	}

	public void paint(Graphics g) {

		//initial positions of text
		int textY = 20;
		int textX = 10;

		//display properties of the simulation
		//uses for loop in order to loop through particles and display
		//respective particle's properties

		for (int i = 0; i <= 1; i++) {
			g.setColor( ((i == 0) ?  Color.ORANGE : Color.RED) ); //either red or orange
			//write out the particles' masses
			g.drawString("m" + (i+1) + " : " + p[i].m + " kg, u" + (i+1) + " : " + p[i].uStr() + " m/s", textX, textY);
			textY += 15; //space next line out by 15 px
			//write out kinetic energies
			g.drawString("Kinetic Energy (" + (i + 1) + "): " + p[i].getKineticEnergyStr(), textX, textY);
			textY += 15; //space next line out by 15px again
			g.fillOval((int)p[i].x, p[i].yMid(), p[i].m, p[i].m); //draw particle
		}
		//coefficient of restitution text
		g.setColor(Color.CYAN);
		g.drawString("Coefficient of Restitution	: " + cor + "", textX, textY);

	}

	public static void main(String args[]) {
		//create new rest() Object
		new Restitution();
		System.out.println("Restitution simulation started");
	}
}
