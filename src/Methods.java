import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;


public class Methods {

	/**
     * Simple Simulated Annealing Algorithm for TSP
	 */
	public static Solution SA( int MAX_G) {
		Solution current = new Solution(Simulations.USE_GREEDY_RANDOM_STRATEGY);
		Solution best = new Solution(current);
		final int cityNumber = Problems.getProblem().getCityNumber(); 
		final int MCL = cityNumber * Simulations.MARKOV_CHAIN_LENGTH_FACTOR; //Markov chain length
		double[] temperatures = new double[MAX_G];
		double[] makespans = new double[MAX_G];
		double[] bestMakespans = new double[MAX_G];

		double t = 0;
		for (int i = 0; i < 1000; i++) {
			Neighbor neighbor = best.findNeighbor();
			if ( neighbor.getDelta() > t) {
				t = neighbor.getDelta();
			}
			if (neighbor.getDelta() < 0) {
				best.update(neighbor);
			}
		}
		double alpha = Simulations.alpha;
		for (int q = 0; q < MAX_G; q++) {
			temperatures[q] = t;
			makespans[q] = current.getTourLength();
			bestMakespans[q] = best.getTourLength();
			for (int c = 0; c < MCL; c++) {
				Neighbor move = current.findNeighbor();
				double p = Methods.rand.nextDouble();
				if (move.getDelta() < 0 || p < 1.0 / Math.exp(move.getDelta()/t)) {
					current.update(move);
					if (current.getTourLength() < best.getTourLength()) {
						best.update(current);
						best.setLastImproving(q);
					}
				}
			}
			t *= alpha;
		}
		if (Simulations.SAVING_PROCESS_DATA) Methods.saveConvergenceData(temperatures, makespans, bestMakespans);
		return best;
	}
	
	/**
     * Simple Simulated Annealing Algorithm for TSP
	 */
	public static Solution simulatedAnnealing( int MAX_G) {
		Solution current = new Solution(Simulations.USE_GREEDY_RANDOM_STRATEGY);
		Solution best = new Solution(current);
		final int cityNumber = Problems.getProblem().getCityNumber(); 
		final int MCL = cityNumber * Simulations.MARKOV_CHAIN_LENGTH_FACTOR; 
        double t = Simulations.t0;
		double alpha = Simulations.alpha;
		for (int q = 0; q < MAX_G; q++) {
			for (int c = 0; c < MCL; c++) {
				Neighbor move = current.findNeighbor();
				double p = Methods.rand.nextDouble();
				if (move.getDelta() < 0 || p < 1.0/Math.exp(move.getDelta()/t)) {
					current.update(move);
					if (current.getTourLength() < best.getTourLength()) {
						best.update(current);
						best.setLastImproving(q);
					}
				}
			}
			t *= alpha;
		}
		return best;
	}


	private static void saveConvergenceData( double[] ts, double[] vs, double[] bs) {
		try {
			String f = Problems.getFileName();
			File file = new File(f);
			f = (new File("")).getAbsolutePath() + "\\results\\Convergence\\" + file.getName();
			f += " " + Simulations.USE_GREEDY_RANDOM_STRATEGY + " convergence process of SA algorithm for TSP results.csv";

			System.out.println(f);
			PrintWriter printWriter = new PrintWriter(new FileWriter(f));
			for (int idx=0; idx<ts.length; idx++) {
				printWriter.println(ts[idx] + "," + vs[idx] + "," + bs[idx]);
			}
			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private static Random rand = new Random();

	public static int histCount;
	public static int probCount;
	public static int randCount;


	public static void main(String[] args) {
		final int TIMES = 25;
		String fileName = (new File("")).getAbsolutePath() + "\\..\\TSP4\\01lin318.txt";
		Problems.setFileName(fileName);
		Solution s;
		double tourLength = 0;
		for (int i = 0; i < TIMES; i++) {
			s = Methods.SA(1000);
			System.out.println(i + "-:" + s.getTourLength());
			tourLength += s.getTourLength();
		}
		tourLength /= TIMES;
		System.out.println("Average: " + tourLength);
	}
}
