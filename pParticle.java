
public class pParticle extends Particle { //it's a type of particle

	int x = 0;
	int y = 0;
	int m = 0;	
	int initY = 0; // initial y coordinate
	float a = 0; // angle of projection
	float xAcc = 0; // x-component of acceleration
	float yAcc = 0; // y-component of acceleration
	float mVel = 0; // magnitude of velocity
	float mAcc = 0; // magnitude of acceleration
	float tof = 0;
	boolean landed = false;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	
	
	//x-component of velocity
	public float xVel(float v) {
		return (float) (v * Math.cos(a));
	}
	
	public float yVel(float v) {
		return (float) ((v * Math.sin(a)) - (earth.G * earth.t));
	}
	
	public String xVelStr(float x) {
		return twoDecPlaces.format(x);
	}
	
	public String yVelStr(float y) {
		return twoDecPlaces.format(y);
	}	
	
	public String getVelStrings(float v) {
		return twoDecPlaces.format(v);
	}
	
	public boolean landed() {
		if (y < (earth.y - 30)) {
			return false;			
		} else {
			return true;
		}
	}
	
	public String getTimeOfFlight(float v, float g) {
		float t = (float) ((2 * v * Math.sin(a)) / g);
		return twoDecPlaces.format(t) + " seconds";					
	} 
	
	public String getRange(float a, float g, float v) {
		float r = (float) ( (Math.pow(mVel, 2) * Math.sin(2 * a)) / earth.G );
		return twoDecPlaces.format(r);
	}
	
	public String getMaxHeight(float a, float g, float v) {
		float h = (float) ((earth.y - initY) + (Math.pow(mVel, 2) * (Math.sin(a)) * (Math.sin(a)))/(2 * earth.G));
		return twoDecPlaces.format(h);
	}	
	
	public void resetVars() {
		earth.t = 0;	
		xAcc = 0;
		yAcc = 0;
		x = 10;		
	}
	
	public void projectParticle() { //args : {int t, float g}
		float dt = (float) 0.02; // a "second"
		for (int i = 0; i < 0.1/dt; i++) { //slows simulation down and reduces flickering
			
			//calculate x position
			//checkIfLanded();
			
			updateX(earth.t); //args : {mVel, t, a}
			
			//calculate y position
			updateY(earth.t); // takes arguments (time, gravitational force) args : mVel, t, a, g
			
			//increment time by "delta" t
			earth.t += dt;					
		}				
	}	
	
	public void updateX(float t) { //parameters : float v, int t, float a
		x = (int)(mVel * t * Math.cos(a));		
	}
	
	public void updateY(float t) { //parameters : float v, int t, float a, float acc
		float a = -earth.G;
		y = initY +	(int) ((mVel * t * -Math.sin(a)) - (.5 * a * t * t));
	}

}
