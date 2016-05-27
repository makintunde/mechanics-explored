import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

/*
 * Mechanics Explored - Front End
 * Copyright (R) Michael Akintunde 2013
 * 
 * Latest updates: 06/01/13 (deprecated "Assess" button, adding about text)
 * 							(Added ToolTip text, depending on simulation selected)
 */

public class FrontEnd extends JFrame /*implements ActionListener, ItemListener*/ {
	private ImageIcon MechImage; //The main image to display
	private JLabel label1; 
	public int simChoice = 1; 
	/*1 = Projectile
	 * 2 = Friction
	 * 3 = Restitution
	 */
						
	JLabel instructions = new JLabel("Choose Simulation:");
	final JLabel welcomeText = new JLabel("Welcome to Mechanics Explored!");
	JButton aboutBtn = new JButton("About");
	JButton beginBtn = new JButton("Begin");
	JButton quitBtn = new JButton("Quit");	
	JComboBox sims = new JComboBox();
	JLabel image1;
	public String name = "Mechanics Explored"; //used for setting "about" text
	
	 FrontEnd() { //constructor
		 
		String[] comboBoxStrings = {"---------", "Projectile", "Friction", "Restitution"};	
		
		FlowLayout f = new FlowLayout();
		setLayout(f);//(new GridLayout(0,1,10,10)); 
		 
		FlowLayout flo = new FlowLayout();
		JPanel welcome = new JPanel();
		welcome.setLayout(flo);
		welcome.add(welcomeText);
		add(welcome); //add(welcome, BorderLayout.PAGE_START);
		
		//add main image
		GridLayout layout3 = new GridLayout(1,1);
		JPanel row1 = new JPanel();
		row1.setLayout(layout3);
		//ImageIcon icon = new ImageIcon("front.png", "Mechanics Explored");
		//label1 = new JLabel(MechImage);
		image1 = new JLabel();
		image1.setHorizontalAlignment(JLabel.CENTER);
		updateLabel("front.png");
        image1.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		row1.add(image1);
		
		add(row1);		//add(row1, BorderLayout.CENTER);		
		
		//add first set of buttons
		GridLayout layout1 = new GridLayout(1,2,10,10);
		JPanel row2 = new JPanel();
		row2.setLayout(layout1);	
		row2.add(instructions);
		final JComboBox sims = new JComboBox(comboBoxStrings); 	
		sims.setSelectedIndex(0); //sets default value to "-------" prompting user to select an simulation
		row2.add(sims);
		sims.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String ch;
				ch = (String) sims.getSelectedItem();
				if (ch == "Projectile") {
					simChoice = 1;		
					updateLabel(ch + ".png");
				}
				if (ch == "Friction") {
					simChoice = 2;	
					updateLabel(ch + ".png");
				}
				if (ch == "Restitution") {
					simChoice = 3;		
					updateLabel(ch + ".png");
				}		
				if (!((ch == "Projectile") || (ch == "Friction") || (ch == "Restitution"))) {
					updateLabel("front.png");
				}
			}			
		});
		add(row2);	//add(row2, BorderLayout.PAGE_END);			
		
		//add second set of buttons
		FlowLayout layout2 = new FlowLayout();//GridLayout(1,3,10,10);
		JPanel row3 = new JPanel();
		row3.setLayout(layout2);	
		row3.add(beginBtn);
		row3.add(aboutBtn);
		row3.add(quitBtn);
		
		// Actions taken when "about" button is clicked
		aboutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				displayAboutText(name);			
			}
		});
		
		// Actions taken when "quit" button is clicked
		quitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		// Actions taken when "begin" button is clicked
		beginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				switch (simChoice) {
				case 1:					
					new Projectile2();
					updateLabel("front.png"); // revert back to default image
				break;
				case 2:					
					new Friction();
					updateLabel("front.png"); // revert back to default image
				break;
				case 3:									
					new Restitution();
					updateLabel("front.png"); // revert back to default image
				break;
				}
			}
		});
		add(row3);	//add(row3, BorderLayout.AFTER_LAST_LINE);			
	 }
	 
	 public void displayAboutText(String n) {
		 new AboutText(n);
	 }
	 
	 protected void updateLabel (String s) {
		 ImageIcon icon = createImageIcon(s, "Mechanics Explored");
	     image1.setIcon(icon);
	     String simStr = s.substring(0,s.length()-4);
	     String ttText = (simStr.equals("front"))? "Mechanics Explored" : simStr + " Simulation in action.";
	     image1.setToolTipText(ttText);
	     if (icon != null) {
	    	 image1.setText(null);
	     } else {
	         image1.setText("Image not found");
	     }
	}
	 
	 //Returns an ImageIcon, or null if the path was invalid.
	 protected static ImageIcon createImageIcon(String path, String description) {
	     java.net.URL imgURL = FrontEnd.class.getResource(path);
	     if (imgURL != null) {
	         return new ImageIcon(imgURL, description);
	     } else {
	         System.err.println("Couldn't find file: " + path);
	         return null;
	     }
	 }
	
	public static void main(String args[]) {
		
		FrontEnd gui = new FrontEnd();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
		gui.setTitle("Mechanics Explored");
		gui.setSize(420, 500);
	}
}