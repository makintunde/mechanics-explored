/*
 * Mechanics Explored - rParticle
 * Copyright (R) Michael Akintunde 2013 *
 */

public class rParticle extends Particle {

	public int m = 0;
	public double x = 0, y = 0, u = 0, v = 0;
	public String colour;

	public int yMid() {
		return (int) ( (earth.y - m) / 2 );
	}

	public String convertTo2DP(double x) {
		return twoDecPlaces.format(x);
	}

	public void resetVars() {
		u = 0;
		m = 0;
	}

	public String uStr() {
		return twoDecPlaces.format(u);
	}

	// Return a string displaying the kinetic energy of a particle.
	public String getKineticEnergyStr() {
		double e = 0.5 * m * u * u;
		String unit = "J";

		if (e > 1000) {
			e /= 1000;
			unit = "KJ";
		}

		String eStr = twoDecPlaces.format(e) + unit;

		return eStr;
	}

	public String getVelocityStr() {
		return twoDecPlaces.format(u);
	}

	// Position first particle at a distance of it's diameter
	// Position second particle at two times its diameter away from right-hand
	// side of canvas.
	public void setInitPos(int p) {
		x = (p == 1 ? m : (earth.x - 2*m));
	}

	// Validate the mass of the particle to avoid it having too large of a
	// diameter on-screen
	public void setM (double m) {
		m = (int) (m > 0 && m < 50 ? m : 10);
	}

	// Validate velocity of the particle to avoid any strange behaviour.
	// right parameter checks whether a particle starts from right-hand
	// side of screen
	public void setU (double x, boolean right) {
		if (right) {
			u = ((x > -100 && x < -5) ? x : -10);
		} else {
			u = ((x < 100 && x > 5) ? x : 10);
		}
	}

}
