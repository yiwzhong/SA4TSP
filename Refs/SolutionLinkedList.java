/**
 * 
 */
package problem;

import java.util.PriorityQueue;

import strategy.ETourType;
import strategy.Strategy;

/**
 * @author Administrator
 *
 */
public class SolutionLinkedList extends Solution {

	/**
	 * To create an empty solution
	 */
	public SolutionLinkedList() {
		nTour = new int[Problems.getProblem().getCityNumber()];
		pTour = new int[nTour.length];
	}
	/**
	 * 
	 */
	public SolutionLinkedList(boolean isGreedyRandom) {
		Problems p = Problems.getProblem();
		if ( isGreedyRandom ) {
			nTour = p.greedyRandom(10, ETourType.LINKED_LIST);
		} else {
			nTour = p.randomTour(ETourType.LINKED_LIST);
		}
		tourLength = p.evaluate(nTour, ETourType.LINKED_LIST);
		pTour = new int[nTour.length];
		for (int i = 0; i< nTour.length; i++ ) {
			pTour[nTour[i]] = i;
		}
		
		//use local search to improve the solution
		//OPT2();
	}

	public void mutation(int times) {
		if (Problems.getProblem().isSymmetric()) {
			int[] tour = new int[nTour.length];
			int city = 0;
			tour[0] = city;
			for (int i=1; i<nTour.length; i++) {
				city = nTour[city];
				tour[i] = city;
			}
			while (times-- > 0) {
				int from = Strategy.rand.nextInt(tour.length);
				int to = Strategy.rand.nextInt(tour.length);
				int temp = tour[from];
				tour[from] = tour[to];
				tour[to] = temp;
			}
			tourLength = Problems.getProblem().evaluate(tour, ETourType.ARRAY);
			for (int i=0; i<tour.length-1; i++) {
				nTour[tour[i]] = tour[i+1];
			}
			nTour[tour[tour.length-1]] = tour[0];
			for (int i = 0; i< nTour.length; i++ ) {
				pTour[nTour[i]] = i;
			}
		} else {
			while (times-->0)
				doubleBridgeMove();
		}
	}
	
	public void doubleBridgeMove() {
		int[] tour = new int[nTour.length];
		int city = 0;
		tour[0] = city;
		for (int i=1; i<nTour.length; i++) {
			city = nTour[city];
			tour[i] = city;
		}

		//to find 4 edges
		int[] pos = findSequence();
		for(int i = 0; i < pos.length; i++) {
			int j = pos[(i+2)%4];
			j = (j+1) % tour.length;
			nTour[tour[pos[i]]] = tour[j];
			pTour[tour[j]] = tour[pos[i]];
		}
		
		evaluate();
	}
	
	private int[] findSequence() {
		//to find 4 different number
		int p1, p2, p3, p4;
		PriorityQueue<Integer> valueList = new PriorityQueue<Integer>();
		p1 = Strategy.rand.nextInt(nTour.length);
		valueList.add(p1);
		p2 = Strategy.rand.nextInt(nTour.length);
		while ( p2 == p1) {
			p2 = Strategy.rand.nextInt(nTour.length);
		}
		valueList.add(p2);
		p3 = Strategy.rand.nextInt(nTour.length);
		while ( p3 == p1 || p3 == p2) {
			p3 = Strategy.rand.nextInt(nTour.length);
		}
		valueList.add(p3);
		p4 = Strategy.rand.nextInt(nTour.length);
		while ( p4 == p1 || p4 == p2 || p4 == p3) {
			p4 = Strategy.rand.nextInt(nTour.length);
		}
		valueList.add(p4);
		int[] r = new int[valueList.size()];
		for (int i = 0 ; i < r.length; i++) {
			r[i] = valueList.poll();
		}
		return r;
	}
	
	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#next(int)
	 */
	@Override
	public int next(int city) {
		return nTour[city];
	}

	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#previous(int)
	 */
	@Override
	public int previous(int city) {
		return pTour[city];
	}

	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#reverse(int, int)
	 */
	@Override
	public void reverse(int from, int to, double delta) {
        int city_from = from;
        int city_from_next = nTour[from];
        int city_to = to;
        int city_to_next = nTour[to];
        while (  city_to != from) { //city_to != from
          	  nTour[city_from] = city_to;
          	  int temp = pTour[city_to];
           	  pTour[city_to] = city_from;
          	  city_from  = city_to;	    	          	  
          	  city_to = temp;
        }
        nTour[city_from_next] = city_to_next;
        pTour[city_to_next] = city_from_next;
        
        tourLength += delta;
        ivsTimes++;
	}
	
	
	@Override
	public void insert(int cityTo, int cityFrom, int cityEnd, double delta) {
		int ci = cityTo;
		int cj = cityFrom;
		int ck = cityEnd;
		int nci = nTour[ci];
		int nck = nTour[ck];
		int pcj = pTour[cj];
    	nTour[ci] = cj; 
    	nTour[ck] = nci; 
    	nTour[pcj] = nck;
    	pTour[cj] = ci; 
		pTour[nci] = ck; 
		pTour[nck] = pcj;
		
		tourLength += delta;
		insTimes++;
	}
	
	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#swap(int, int)
	 */
	@Override
	public void swap(int from, int to, double delta) {
		int ci = from;
		int cj = to;
		int nci = nTour[ci];
		int ncj = nTour[cj];
		int pcj = pTour[cj];
		int nnci = nTour[nci];
    	nTour[ci] = cj; nTour[cj] = nnci; nTour[pcj] = nci; nTour[nci] = ncj;
    	pTour[cj] = ci; pTour[nnci] = cj; pTour[nci] = pcj; pTour[ncj] = nci;
    	
    	tourLength += delta;
    	swpTimes++;
	}
	
	@Override
	public void swap(int x1, int x2, int y1, int y2, double delta) {//include x1
		int px1 = pTour[x1];
		int nx2 = nTour[x2];
		int py1 = pTour[y1];
		int ny2 = nTour[y2];
    	nTour[px1] = y1; nTour[y2] = nx2; nTour[py1] = x1; nTour[x2] = ny2;
    	pTour[y1] = px1; pTour[nx2] = y2; pTour[x1] = py1; pTour[ny2] = x2;
    	
    	tourLength += delta;
    	swpTimes++;
	}
	
	

	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#update(simulatedAnnealing.Solution)
	 */
	@Override
	public void update(Solution s) {
		if ( s instanceof SolutionLinkedList) {
			System.arraycopy(((SolutionLinkedList)s).nTour, 0, nTour, 0, nTour.length);
			System.arraycopy(((SolutionLinkedList)s).pTour, 0, pTour, 0, pTour.length);
		}else if ( s instanceof SolutionSatelliteList) {
			SolutionSatelliteList ssl = (SolutionSatelliteList) s;
			int satellite_index= 0;
			int city = ssl.city(satellite_index);
			for (int i=0; i<nTour.length; i++) {
				nTour[city] = ssl.city(ssl.next(satellite_index));
				pTour[city] = ssl.city(ssl.previous(satellite_index));
				satellite_index = ssl.next(satellite_index);
				city = ssl.city(satellite_index);
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
	public int[] getTour() { return nTour; }
	
	protected int[] nTour;
	protected int[] pTour;
}
