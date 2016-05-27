public class TestRest {

        //constructor
        TestRest(int m1, int m2, double u1, double u2, double e){
        	
        	double v1 = ((m1 * u1) + (m2 * u2) + (m2 * e)*(u2 - u1)) / (m1 + m2);
            System.out.println("m1: " + m1 + ", u1: " + u1);
            
            double v2 = ((m1 * u1) + (m2 * u2) + (m1 * e)*(u1 - u2)) / (m1 + m2);
            System.out.println("m2: " + m2 + ", u2: " + u2);
            
            System.out.println("Final velocity1: " + v1 + "m/s");
            System.out.println("Final velocity2: " + v2 + "m/s");
        }

        public static void main(String args[]) {
                //create TestRest object
                new TestRest(50, 5, 10, -15, 1);
        }
}
