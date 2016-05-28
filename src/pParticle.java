
public class pParticle extends Particle { 

	int x = 0;
	int y = 0;
	int m = 0;	
	int initY = 0;  // Initial y coordinate
	double a = 0;    // Angle of projection
	double xAcc = 0; // x-component of acceleration
	double yAcc = 0; // y-component of acceleration
	double mVel = 0; // Magnitude of velocity
	double mAcc = 0; // Magnitude of acceleration
	double tof = 0;
	boolean landed = false;
	
	//x-component of velocity
	public double xVel(double v) {
		return v * Math.cos(a);
	}
	
	public double yVel(double v) {
		return v * Math.sin(a) - earth.G * earth.t;
	}
	
	public String xVelStr(double x) {
		return twoDecPlaces.format(x);
	}
	
	public String yVelStr(double y) {
		return twoDecPlaces.format(y);
	}	
	
	public String getVelStrings(double v) {
		return twoDecPlaces.format(v);
	}
	
	public boolean landed() {
		if (y < (earth.y - 30)) {
			return false;			
		} else {
			return true;
		}
	}
	
	public String getTimeOfFlight(double v, double g) {
		double t = 2 * v * Math.sin(a) / g;
		return twoDecPlaces.format(t) + " seconds";					
	} 
	
	public String getRange(double a, double g, double v) {
		double r = mVel * mVel * Math.sin(2 * a) / earth.G;
		return twoDecPlaces.format(r);
	}
	
	public String getMaxHeight(double a, double g, double v) {
		double h = (earth.y - initY) + (mVel * mVel * (Math.sin(a)) * (Math.sin(a))) / 2 / earth.G;
		return twoDecPlaces.format(h);
	}	
	
	public void resetVars() {
		earth.t = 0;	
		xAcc = 0;
		yAcc = 0;
		x = 10;		
	}
	
	public void projectParticle() { 
		double dt = 0.02; // a "second"
		for (int i = 0; i < 0.1/dt; i++) { //slows simulation down and reduces flickering
			
			//calculate x position
			//checkIfLanded();
			
			updateX(earth.t);
			
			//calculate y position
			updateY(earth.t); 
			
			//increment time by "delta" t
			earth.t += dt;					
		}				
	}	
	
	public void updateX(double t) { 
		x = (int)(mVel * t * Math.cos(a));		
	}
	
	public void updateY(double t) { 
		double a = -earth.G;
		y = initY +	(int) ((mVel * t * -Math.sin(a)) - (.5 * a * t * t));
	}

}
