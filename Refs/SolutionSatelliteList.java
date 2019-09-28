/**
 * 
 */
package problem;

import strategy.ETourType;
import strategy.Strategy;

/**
 * @author Administrator
 *
 */
public class SolutionSatelliteList extends Solution {
	/**
	 * To create an empty solution
	 */
	public SolutionSatelliteList() {
		tour = new int[Problems.getProblem().getCityNumber()*2];
	}
	/**
	 * 
	 */
	public SolutionSatelliteList(boolean isGreedyRandom) {
		Problems p = Problems.getProblem();
		if ( isGreedyRandom ) {
			tour = p.greedyRandom(10, ETourType.SATELLITE_LIST);
		} else {
			tour = p.randomTour(ETourType.SATELLITE_LIST);
		}
		tourLength = p.evaluate(tour, ETourType.SATELLITE_LIST);
		
		//use local search to improve the solution
		OPT2();
	}
	
	public SolutionSatelliteList(SolutionArray solution) {
		this();
		int cityNumber = Problems.getProblem().getCityNumber();
		for (int i=0; i<cityNumber-1; i++) {
			tour[solution.tour[i]*2] = solution.tour[i+1]*2;
			tour[solution.tour[i+1]*2+1] = solution.tour[i]*2+1;
		}
		tour[solution.tour[cityNumber-1]*2] = solution.tour[0]*2;
		tour[solution.tour[0]*2+1] = solution.tour[cityNumber-1]*2+1;
		tourLength = solution.tourLength;
	}

	public SolutionSatelliteList(SolutionLinkedList solution) {
		this();
		int cityNumber = Problems.getProblem().getCityNumber();
		int[] t = new int[cityNumber];
		int city = t[0]; //0
		for (int i=1; i<cityNumber; i++) {
			t[i] = solution.nTour[city];
			city = t[i];
		}
		for (int i=0; i<cityNumber-1; i++) {
			tour[t[i]*2] = t[i+1]*2;
			tour[t[i+1]*2+1] = t[i]*2+1;
		}
		tour[t[cityNumber-1]*2] = t[0]*2;
		tour[t[0]*2+1] = t[cityNumber-1]*2+1;
		tourLength = solution.tourLength;
		Problems.getProblem().evaluate(tour, ETourType.SATELLITE_LIST);
	}
	
	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#next(int)
	 */
	@Override
	public int next(int satelliteIndex) {
		return tour[satelliteIndex];
	}

	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#previous(int)
	 */
	@Override
	public int previous(int satelliteIndex) {
		return tour[satelliteIndex^1]^1;
	}

	public int city(int satelliteIndex) {
		return satelliteIndex >> 1;
	}
	
	public int complement(int satelliteIndex) {
		return satelliteIndex^1;
	}
	
	/* Replaces arcs ab and cd with arcs ac and bd. Implies the bc-path is reversed. */
	void reverseSubpath( int satellite_index_a, int satellite_index_b,
			int satellite_index_c,	int satellite_index_d )
	{
		tour[satellite_index_a] = satellite_index_c^1;
		tour[satellite_index_c] = satellite_index_a^1;
		tour[satellite_index_d^1] = satellite_index_b;
		tour[satellite_index_b^1] = satellite_index_d;
	}
	
	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#reverse(int, int, int)
	 */
	@Override
	public void reverse(int from, int to, double delta) {
		// TODO Auto-generated method stub
		
	}

//	/* (non-Javadoc)
//	 * @see simulatedAnnealing.Solution#insert(int, int, int)
//	 */
//	@Override
//	public void insert(int from, int to, int delta, int direction) {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public void insert(int from, int to, int length, double delta) {
		
	}
	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#swap(int, int, int)
	 */
	@Override
	public void swap(int from, int to, double delta) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void swap(int x1, int x2, int y1, int y2, double delta) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#update(simulatedAnnealing.Solution)
	 */
	@Override
	public void update(Solution s) {
		if ( s instanceof SolutionSatelliteList) {
			System.arraycopy(((SolutionSatelliteList)s).tour, 0, tour, 0, tour.length);
		}
		tourLength = s.tourLength;
	}
	
	@Override
	public void OPT2() {
		Problems problem = Problems.getProblem();
		int cityNumber = problem.getCityNumber();
		//System.out.print(tourLength);
		boolean improved = true;
		while ( improved ) {
			improved = false;
			int satellite_index_a = Strategy.rand.nextInt(cityNumber*2);
			int outer_count = 0;
			do {
				int city_a = city(satellite_index_a);
				int satellite_index_b = next(satellite_index_a);
				int city_b = city(satellite_index_b);
				int satellite_index_c = next(satellite_index_b);
				int inner_count = 0;
				do {
					int city_c = city(satellite_index_c);
					int satellite_index_d = next(satellite_index_c);
					int city_d = city(satellite_index_d);
					
					double dci_cj = problem.getEdge(city_a, city_c);
		            double dnci_ncj = problem.getEdge(city_b, city_d);
		            double dci_nci = problem.getEdge(city_a, city_b);
		            double dcj_ncj = problem.getEdge(city_c, city_d);
		            double ivsDelta = dci_cj + dnci_ncj - (dci_nci + dcj_ncj);
		            if ( ivsDelta < 0 ) {
		            	//System.out.println(Problems.outputTour(tour, ETourType.SATELLITE_LIST));
		            	reverseSubpath(satellite_index_a, satellite_index_b, satellite_index_c, satellite_index_d);
		            	tourLength += ivsDelta;
		            	improved = true;
		            	//System.out.println(tourLength);
		            	//System.out.println("Cost:"+Problems.getProblem().evaluate(tour, ETourType.SATELLITE_LIST));
		            	//System.out.println(Problems.outputTour(tour, ETourType.SATELLITE_LIST));
		            	//assert(tourLength==Problems.getProblem().evaluate(tour, ETourType.SATELLITE_LIST));
		            } else {
		            	satellite_index_c = satellite_index_d;
		            }
					inner_count++;
				} while ( (!improved) && inner_count < cityNumber -3 && tourLength > 0);
				satellite_index_a = satellite_index_b;
				outer_count++;
			} while ( (!improved) && outer_count < cityNumber && tourLength > 0);
		}
		//System.out.println(","+tourLength);
	}
	
	@Override
	public int[] getTour() { return tour; }

	protected int[] tour;
}

