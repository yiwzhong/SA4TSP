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
	public static Solution simulatedAnnealingWithAdativeInitialTemperature( int MAX_G) {
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

	/**
	 * Tabu search for TSP. A simplified version of the algorithm in following paper.
	 * 
	 * Knox, J. (1994). Tabu search performance on the symmetric traveling salesman problem. 
	 *   Computers & Operations Research, 21(8), 867-876.
	 * 
	 * @return
	 */
	public static Solution tabuSearch() {
		Solution current = new Solution(Simulations.USE_GREEDY_RANDOM_STRATEGY);
		Solution best = new Solution(current);
		final int cityNumber = Problems.getProblem().getCityNumber(); 
		int[][] tabu = new int[cityNumber][cityNumber];
		int met = 0;
		final int MAX_ITERATIONS = 20 * cityNumber;
		final int TABU_TENURE = cityNumber * 3;
		while (met++ < MAX_ITERATIONS) {
			//Find best inverse operator
			Neighbor bestNb = new Neighbor(0,0,Integer.MAX_VALUE);
			int firstCity = 0;
			for (int ci = 0; ci < cityNumber - 2; ci++) {
				int nci = current.next(ci);
				int cj = current.next(nci);
				while ( cj != firstCity) {
					int ncj = current.next(cj);
					Neighbor nb = current.findInverse(ci, cj);
					if (tabu[ci][cj] < met /*first added edge*/ ||
							tabu[nci][ncj] < met /*second added edge*/) {
						if (nb.getDelta() < bestNb.getDelta()) {
							bestNb = nb;
						}
					} else if (current.tourLength + nb.delta < best.tourLength){//Aspiration
						if (nb.getDelta() < bestNb.getDelta()) {
							bestNb = nb;
						}
					}
					cj = current.next(cj);
				}
			}
			
			//Update tabu list
			int ci = bestNb.x1;
			int cj = bestNb.y1;
			//The first deleted edge
			int nci = current.next(ci);
			tabu[ci][nci] = met + TABU_TENURE;
			tabu[nci][ci] = met + TABU_TENURE;
			//The second deleted edge
			int ncj = current.next(cj);
			tabu[cj][ncj] = met + TABU_TENURE;
			tabu[ncj][cj] = met + TABU_TENURE;
			
			//Update current solution
			current.update(bestNb);
			if (current.getTourLength() < best.getTourLength()) {
				best.update(current);
				best.setLastImproving(met);
			}
			//System.out.println(current.getTourLength());
			//System.out.println(current.getTourLength() + "-" + best.getTourLength());
		}
		return best;
	}
	
	/**
	 * Variable neighbor descend method
	 * 
	 * @return
	 */
	public static Solution varaibleNeighborDescent() {
		Solution current = new Solution(Simulations.USE_GREEDY_RANDOM_STRATEGY);
		Simulations.clearMaxInsertBlockSize(); //allow single city only
        ENeighborType[] nbTypes;
        nbTypes = new ENeighborType[]{ENeighborType.INVERSE, 
        		ENeighborType.INSERT, ENeighborType.SWAP};
        
		int nbIdx = 0;
		while (nbIdx < nbTypes.length) {
			System.out.println(current.tourLength);
			Solution s;
			s = hillClimbing(current, nbTypes[nbIdx]);
			if (s.getTourLength() < current.getTourLength()) {
				current = s;
				nbIdx = 0;
			} else {
				nbIdx++;
			}
		}
		return current;
	}
	
	/**
	 * Variable neighbor descend method
	 * 
	 * @return
	 */
	public static Solution varaibleNeighborSearch() {
		Solution current = new Solution(Simulations.USE_GREEDY_RANDOM_STRATEGY);
		Simulations.clearMaxInsertBlockSize(); //allow single city only
        ENeighborType[] nbTypes;
        nbTypes = new ENeighborType[]{ENeighborType.INVERSE, 
        		ENeighborType.INSERT, ENeighborType.SWAP};
        boolean improved = true;
        while (improved) {
        	improved = false;
        	int nbIdx = 0;
        	while (nbIdx < nbTypes.length) {
        		//System.out.println(current.tourLength);
        		Solution s;
        		s = hillClimbing(perturbSolution(current, 2, nbTypes[2]), nbTypes[nbIdx]);
        		if (s.getTourLength() < current.getTourLength()) {
        			improved = true;
        			current = s;
        			nbIdx = 0;
        		} else {
        			nbIdx++;
        		}
        	}
        }
		return current;
	}
	
	/**
	 * General variable neighbor descend method
	 * 
	 * @return
	 */
	public static Solution generalVaraibleNeighborSearch() {
		Solution current = new Solution(Simulations.USE_GREEDY_RANDOM_STRATEGY);
		Simulations.clearMaxInsertBlockSize(); //allow single city only
        ENeighborType[] nbTypes;
        nbTypes = new ENeighborType[]{ENeighborType.INVERSE, 
        		ENeighborType.INSERT, ENeighborType.SWAP};
        boolean improved = true;
        while (improved) {
        	improved = false;
        	int sIdx = 0; //neighbor index for shaking
        	while (sIdx < nbTypes.length) {
        		Solution s = perturbSolution(current, 2, nbTypes[sIdx++]);
        		int nbIdx = 0;
        		while (nbIdx < nbTypes.length) {
        			//System.out.println(current.tourLength);
        			Solution ss;
        			ss = hillClimbing(s, nbTypes[nbIdx++]);
        			if (ss.tourLength < s.tourLength) {
        				s = ss;
        				nbIdx = 0;
        			}
        			if (ss.getTourLength() < current.getTourLength()) {
        				improved = true;
        				current = ss;
        				sIdx = 0;
         				nbIdx = nbTypes.length; //exit from inner loop
        			} 
        		}
        	}
        }
		return current;
	}
	
	public static Solution hillClimbing(Solution s, ENeighborType neighborType) {
		Solution current = new Solution(s);
		int cityNumber = current.getCityNumber();
		boolean improved = true;
		while (improved) {
			improved = false;
			int firstCity = 0;
			for (int ci = 0; ci < cityNumber - 2 && !improved; ci++) {
				int nci = current.next(ci);
				int cj = current.next(nci);
				while ( cj != firstCity && !improved) {
					Neighbor nb;
					if (neighborType == ENeighborType.INVERSE) {
					    nb = current.findInverse(ci, cj);
					} else if (neighborType == ENeighborType.INSERT) {
					    nb = current.findInsert(ci, cj);
					} else {//swap
						nb = current.findSwap(ci, cj);
					}
					if (nb.getDelta() < 0 ) {
						current.update(nb);
						improved = true;
					}
					cj = current.next(cj);
				}
			}
		}
		return current;
	}
	
	private static Solution perturbSolution(Solution s, int times, ENeighborType nt) {
		Solution current = new Solution(s);
		while (times-- > 0) {
			int ci = rand.nextInt(current.getCityNumber());
			int cj = ci;
			while (cj == ci || cj == current.next(ci) || cj== current.previous(ci)) {
				cj = rand.nextInt(current.getCityNumber());
			}
			if (nt == ENeighborType.INSERT) {
				current.update(current.findInsert(ci, cj));
			} else if (nt == ENeighborType.INVERSE) {
				current.update(current.findInverse(ci, cj));
			} else if (nt == ENeighborType.SWAP) {
				current.update(current.findSwap(ci, cj));
			}
		}
		return current;
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
			s = Methods.simulatedAnnealingWithAdativeInitialTemperature(1000);
			System.out.println(i + "-:" + s.getTourLength());
			tourLength += s.getTourLength();
		}
		tourLength /= TIMES;
		System.out.println("Average: " + tourLength);
	}
}
