import java.text.DecimalFormat;


public class rParticle extends Particle {
	
	public int m = 0;
	public float x = 0;
	public float y = 0;
	public float u = 0;	
	public float v = 0;
	public String colour;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~		
	
	public int yMid() {
		return (int) ( (earth.y - m) / 2 );		
	}	
	
	public String convertTo2DP(float x) {
		return twoDecPlaces.format(x);
	}
	
	public void resetVars() {
		u = 0;
		m = 0;		
	}
	
	public String uStr() {
		return twoDecPlaces.format(u);
	}
	
	public String getKineticEnergyStr() {
		float e = (float) (0.5 * m * u * u);
		String eStr = (e < 1000) ? twoDecPlaces.format(e) + " J" : twoDecPlaces.format(e/1000) + " KJ";    	
		return eStr;
	}
	
	public String getVelocityStr() {
		return twoDecPlaces.format(u);
	}
	
	public void setInitPos(int p) {
		//p = particle no.
		switch (p) {
		case 1 : 
		//position first particle at a distance of it's diameter 
		//away from left-hand side of canvas
			x = m; 
			break;
		case 2 :
		//position second particle at a distance of two multiples of it's
		//diameter away from right-hand side of canvas, ensures minimum errors
			x = earth.x - (2 * m);
			break;
		}
	}
	
	//validate the velocity
	public void setV(float x) {
		
	}
	
	//validate the mass of the particle to avoid it having too large of a diameter on-screen
	public void setM (float M) { //use mass to set diameter
		m = (int) (((M < 50) && (M > 0)) ? M : 10);
	}
	
	//validate velocity of the particle to avoid any strange behaviour.
	public void setU (float x, boolean right) { //boolean parameter checks whether a particle starts from right-hand side of screen
		if (!right) {
			u = (float) (((x < 100) && (x > 5)) ? x : 10); 			
		} else {
			u = (float) (((x > -100) && (x < -5)) ? x : -10); 
		}
	}
	
	
	
}


