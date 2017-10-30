public class StateAndReward {

	private static final int angleStates = 9;
	private static final double maxAngle = Math.PI/3;
	private static final int xStates = 9; 
	private static final int maxX = 1;
	private static final int yStates = 15;
	private static final int maxY = 1;
	
	/* State discretization function for the angle controller */
	public static String getStateAngle(double angle, double vx, double vy) {
		
		/* TODO: IMPLEMENT THIS FUNCTION */
		
		int discrete = discretize( angle, angleStates, -maxAngle, maxAngle );
		
		String state = "state" + Integer.toString(discrete);
		
		return state;
	}

	/* Reward function for the angle controller */
	public static double getRewardAngle(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */
		
		double reward = 0;
	
		int discrete = discretize( angle, angleStates, -maxAngle, maxAngle );
	
		/**
		 * EXPONENTIAL DECAY OF REWARD
		 */
		reward = Math.exp(-2 * Math.abs(angleStates/2 - discrete));
		
		return reward;
	}

	/* State discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */

		int discreteX = discretize(vx, xStates, -maxX, maxX);
		int discreteY = discretize(vy, yStates, -maxY, maxY);
		
		String state = "x" + Integer.toString(discreteX) + "y" + Integer.toString(discreteY);
		
		return state;
	}

	/* Reward function for the full hover controller */
	public static double getRewardHover(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */
		
		double reward = 0;
		
		int discreteX = discretize(vx, xStates, -maxX, maxX);
		int discreteY = discretize(vy, yStates, -maxY, maxY);
		
		/**
		 * EXPONENTIAL DECAY OF REWARD
		 */
		reward += Math.exp(-2 * Math.abs(xStates/2 - discreteX));
		reward += Math.exp(-2 * Math.abs(yStates/2 - discreteY));
		return reward;
	}
	
	// ///////////////////////////////////////////////////////////
	// discretize() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 1 and nrValues-2 is returned.
	//
	// Use discretize2() if you want a discretization method that does
	// not handle values lower than min and higher than max.
	// ///////////////////////////////////////////////////////////
	public static int discretize(double value, int nrValues, double min,
			double max) {
		if (nrValues < 2) {
			return 0;
		}

		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * (nrValues - 2)) + 1;
	}

	// ///////////////////////////////////////////////////////////
	// discretize2() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 0 and nrValues-1 is returned.
	// ///////////////////////////////////////////////////////////
	public static int discretize2(double value, int nrValues, double min,
			double max) {
		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * nrValues);
	}

}
