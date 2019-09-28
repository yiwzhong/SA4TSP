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
public class SolutionArray extends Solution {

	/**
	 * To create an empty solution
	 */
	public SolutionArray() {
		tour = new int[Problems.getProblem().getCityNumber()];
		cityIndex = new int[tour.length];
	}
	
	public SolutionArray(boolean isGreedyRandom) {
		Problems p = Problems.getProblem();
		if ( isGreedyRandom ) {
			tour = p.greedyRandom(10, ETourType.ARRAY);
		} else {
			tour = p.randomTour(ETourType.ARRAY);
		}
		tourLength = p.evaluate(tour, ETourType.ARRAY);
		cityIndex = new int[tour.length];
		for (int i = 0; i< tour.length; i++ ) {
			cityIndex[tour[i]] = i;
		}
		
		//use local search to improve the solution
		//OPT2();
	}

	public void mutation(int times) {
		while (times-- > 0) {
			int from = Strategy.rand.nextInt(tour.length);
			int to = Strategy.rand.nextInt(tour.length);
			int temp = tour[from];
			tour[from] = tour[to];
			tour[to] = temp;
		}
		tourLength = Problems.getProblem().evaluate(tour, ETourType.ARRAY);
		for (int i = 0; i< tour.length; i++ ) {
			cityIndex[tour[i]] = i;
		}
	}
	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#next(int)
	 */
	@Override
	public int next(int city) {
		int pos = cityIndex[city];
		pos = (pos+1) % tour.length;
		return tour[pos];
	}

	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#previous(int)
	 */
	@Override
	public int previous(int city) {
		int pos = cityIndex[city];
		pos = (pos-1+tour.length) % tour.length;
		return tour[pos];
	}

	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#reverse(int, int)
	 */
	@Override
	public void reverse(int from, int to, double delta) {
		int pos = cityIndex[from];
		int nextPos = (pos+1) % tour.length;
		int j = cityIndex[to];
		while (  j != nextPos ) {
			int city = tour[j];
			tour[j] = tour[nextPos];
			tour[nextPos] = city;
			cityIndex[tour[nextPos]] = nextPos;
			cityIndex[tour[j]] = j;
			j = ( j -1 + tour.length) % tour.length;
			if ( j== nextPos )
				break;
			nextPos = ( nextPos+1) % tour.length;
		}
		
		tourLength += delta;
		ivsTimes++;
	}
	

	@Override
	public void insert(int cityTo, int cityFrom, int cityEnd, double delta) {
		// TODO Auto-generated method stub
		int pos = cityIndex[cityTo];
		int nextPos = (pos+1) % tour.length;
		int cityFromPos = cityIndex[cityFrom];
		int cityEndPos = cityIndex[cityEnd];
		int[] temp = tour.clone();
		
		int j = cityFromPos;	
		int k = cityEndPos;
		while (  j != nextPos ) {
          	tour[k] = tour[(j-1+tour.length)%tour.length];
        	cityIndex[tour[k]] = k;
          	j = (j-1+tour.length)%tour.length;
          	k = (k-1+tour.length)%tour.length;
        }
		
		k = nextPos;
		j = cityFromPos;
		while ( j != cityEndPos) {
			tour[k] = temp[j];
	        cityIndex[temp[j]] = k;
	        j = (j-1+tour.length)%tour.length;
          	k = (k-1+tour.length)%tour.length;
		}
		tour[k] = temp[j];
        cityIndex[temp[j]] = k;
        
        tourLength += delta;
        insTimes++;	
    }
	
	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#swap(int, int)
	 */
	@Override
	public void swap(int from, int to, double delta) {
		int pos = cityIndex[from];
		int nextPos = (pos+1) % tour.length;	
		int from_next = tour[nextPos];
		int posTo = cityIndex[to];

		tour[nextPos] = to;
        tour[posTo] = from_next;
        cityIndex[to] = nextPos;
        cityIndex[from_next] = posTo;

        tourLength += delta;
        swpTimes++;
	}
	
	public void swap(int x1, int x2, int y1, int y2, double delta) {
		//how to implement this operator for array?
	}

	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#update(simulatedAnnealing.Solution)
	 */
	@Override
	public void update(Solution s) {
		if ( s instanceof SolutionArray) {
			System.arraycopy(((SolutionArray)s).tour, 0, tour, 0, tour.length);
			System.arraycopy(((SolutionArray)s).cityIndex, 0, cityIndex, 0, cityIndex.length);
		} else if ( s instanceof SolutionSatelliteList) {
			SolutionSatelliteList ssl = (SolutionSatelliteList) s;
			int satellite_index= 0;
			int city = ssl.city(satellite_index);
			tour[0] = city;
			cityIndex[city] = 0;
			for (int i=1; i<tour.length; i++) {
				satellite_index= ssl.next(satellite_index);
				city = ssl.city(satellite_index);
				tour[i] = city;
				cityIndex[city] = i;
			}
			
		}
		tourLength = s.tourLength;
	}
	
	@Override
	public void OPT2() {
		SolutionSatelliteList s = new SolutionSatelliteList(this);
		s.OPT2();
		update(s);
	}

	@Override
	public int[] getTour() { return tour; }

	protected int[] tour;
	protected int[] cityIndex;
}
