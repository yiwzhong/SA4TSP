
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AntMethods {

	public static Solution antSystem(final int MAX_G, final int POP_SIZE) {
		Solution best = new Solution(Simulations.USE_GREEDY_RANDOM_STRATEGY);
		final double alpha = 1.0;
		final double beta = 5.0;
		final double rho = 0.5;

		double[][] pher;
		double[][] heur;
		heur = AntMethods.setupInitialHeur(beta);
		pher = AntMethods.setupInitialPher(pherMax);
		for (int g = 0; g < MAX_G; g++) {
			for (int ant = 0; ant < POP_SIZE; ant++) {
				Solution current = AntMethods.buildSolution(pher, alpha, heur);
				//current = Methods.hillClimbing(current, ENeighborType.INVERSE);
				if ( current.tourLength < best.tourLength ) {
					best = new Solution(current);
					best.setLastImproving(g);
					if (best.getTourLength() <= Problems.getProblem().getBestTourLength()) {
						return best;
					}
				}
				depositPher(pher, current, best.getTourLength()/current.getTourLength());
			}
			AntMethods.evaporatePher(pher, rho);
			depositPher(pher, best, 1.0 * POP_SIZE);
		}
		return best;
	}
	
	private static double[][] setupInitialHeur(final double beta) {
		int nearCityNumber = Problems.getNearCityNumber();
		Problems problem = Problems.getProblem();
		int cityNumber = problem.getCityNumber();
		double[][] heur = new double[cityNumber][nearCityNumber];
		int[][] k = problem.getNearCityList();
		for (int i=0; i<cityNumber; i++) {
    		for (int j=0; j<nearCityNumber; j++) {
    			int city = k[i][j];
    			heur[i][j] = Math.pow(1.0 / problem.getEdge(i,city), beta);
    		}
    	}
    	pherMin = 1.0 / cityNumber ;
    	pherMax = 2.0 * Simulations.POP_SIZE * 100;
		return heur;
	}
	
	private static double[][] setupInitialPher(final double p) {
		int nearCityNumber = Problems.getNearCityNumber();
		Problems problem = Problems.getProblem();
		int cityNumber = problem.getCityNumber();
		double[][] pher = new double[cityNumber][nearCityNumber];
		for (int i = 0; i < pher.length; i++) {
			for (int j = 0; j < pher[i].length; j++) {
				pher[i][j] = p;
			}
		}
		return pher;
	}
	
	private static void evaporatePher(double[][] pher, double r) {
		for (int x = 0; x < pher.length; x++) {
			for (int y = 0; y < pher[x].length; y++) {
				pher[x][y] = (1 - r) * pher[x][y];
				if (pher[x][y] < pherMin) {
					pher[x][y] = pherMin;
				}
			}
		}
	}
	
	private static void depositPher(double[][] p, final Solution s, double v) {
		Problems problem = Problems.getProblem();
		for (int x = 0; x < problem.getCityNumber(); x++) {
			int y = s.next(x);
			int pos = problem.findInNearestCityList(x, y);
			if ( pos >= 0 && pos < p[x].length) {
				p[x][pos] += v;
				if ( p[x][pos] > pherMax) {
					p[x][pos] = pherMax;
				}
			}
			if (problem.isSymmetric()) {//symmetric
				pos = problem.findInNearestCityList(y, x);
				if ( pos >= 0 && pos < p[y].length) {
					p[y][pos] += v;
					if ( p[y][pos] > pherMax) {
						p[y][pos] = pherMax;
					}
				}
			}
		}
	}
	
	
	private static Solution buildSolution(double[][] pher, double a, double[][] heur) {
		Problems problem = Problems.getProblem();
		int cityNumber = problem.getCityNumber();
		int[] nTour = new int[cityNumber];
		List<Integer> remainCities = new LinkedList<>();
		for (int i = 0; i < cityNumber; i++) {
			remainCities.add(i);
		}
		int c0 = remainCities.remove(rand.nextInt(cityNumber));
		int ci = c0;
		while (!remainCities.isEmpty()) {
			double[] probs = new double[remainCities.size()];
			for (int i = 0; i < remainCities.size(); i++) {
				int cj = remainCities.get(i);
				int pos = problem.findInNearestCityList(ci, cj);
				double p = Double.MIN_VALUE;
				if (pos >= 0) {//In near city list
					p = Math.pow(pher[ci][pos], a) * heur[ci][pos];
				}
				if ( i > 0) {
					probs[i] = probs[i-1] + p;
				} else {
					probs[i] = p;
				}
			}
			double p = AntMethods.rand.nextDouble() * probs[probs.length - 1];
			int cj = -1;
            for (int i = 0; i < probs.length; i++) {
				if ( p < probs[i]) {
					cj = remainCities.remove(i);
					break;
				}
			}
            if (cj == -1) {
            	cj = remainCities.remove(0);
            }
			nTour[ci] = cj;
			ci = cj;
		}
		nTour[ci] = c0;
		return new Solution(nTour);
	}
	
	private static Random rand = new Random();
    public static double pherMax=2000;
    public static double pherMin=20;

}
