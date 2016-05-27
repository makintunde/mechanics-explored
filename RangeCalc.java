import java.text.DecimalFormat;

public class RangeCalc {
	
	RangeCalc(double speed, double angleInDegrees) {
		DecimalFormat twoDecPlaces = new DecimalFormat("0.00");
		double g, angleInRads, range, airRes, drag;
		drag = 0.5;
		g = 9.8;
		airRes = drag * Math.abs(speed) * Math.abs(speed);
		angleInRads = angleInDegrees * Math.PI/180;
		range = 2 * speed * speed 
			* Math.sin(angleInRads) * Math.cos(angleInRads) / g;
		System.out.print("Speed = " + twoDecPlaces.format(speed) + " m/s, ");
		System.out.print("Angle = " + twoDecPlaces.format(angleInDegrees) + " degrees, ");
		System.out.print("Range = " + twoDecPlaces.format(range) + " metres.");
		System.out.println();
		
		if (range > 40) {
			System.out.println("Nice throw!"); 
		} else {
			System.out.println("Try again!");
		}		
	}
	
	public static double printNo(int num) {		
		int x = 5;
		num += x;
		return num;
	}
	
	public static void main(String args[]) {
		new RangeCalc(600,89); //launch at "speed" m/s, "angleInDegrees" degrees
		
		
	}

}
