/**
 * 
 */
package problem;

import java.util.ArrayList;

import simulatedAnnealing.Population;
import strategy.EInsertType;
import strategy.ESwapType;
import strategy.Strategy;
import strategy.EAddEdgeType;

/**
 * @author Administrator
 *
 */
public abstract class Solution implements Comparable<Solution> {
	public static void clearTimes() {
		ivsTimes = 0;
		insTimes = 0;
		swpTimes = 0;
	}

	public void mutation(int times) {}
	
	/**
	 * get th city visited after para city
	 * @param city
	 * @return the city which is visited after para city
	 */
	public abstract int next(int city);
	
	/**
	 * get the city visited before para city
	 * @param city
	 * @return
	 */
	public abstract int previous(int city);
	
	/**
	 * reverse those cities between from (exclusive) and to (inclusive)
	 * @param from
	 * @param to
	 * @param delta  used to update the tourLength
	 * @param direction 
	 */
	public abstract void reverse(int from, int to, double delta);
	
//	/**
//	 * insert city cityFrom to the position of city cityTo
//	 * @param cityTo
//	 * @param cityFrom
//	 * @param delta
//	 * @param direction
//	 */
//	public abstract void insert(int cityTo, int cityFrom, int delta, int direction);

	/**
	 * move a block of cities (from cityFrom to cityEnd) to the position of city cityTo
	 * @param cityTo
	 * @param cityFrom
	 * @param cityEnd
	 * @param delta
	 */
	public abstract void insert(int cityTo, int cityFrom, int cityEnd, double delta);
	
	/**
	 * swap city from and to
	 * @param from
	 * @param to
	 * @param delta
	 * @param direction
	 */
	public abstract void swap(int from, int to, double delta);
	
	public abstract void swap(int x1, int x2, int y1, int y2, double delta);
	
	public abstract void update(Solution s);
	public abstract void OPT2();
	
	public void evaluate() {
		Problems p = Problems.getProblem();
		tourLength = p.evaluate(this);
	}
	
	public void move(Move m) {
		if ( m != null ) {
			if ( m.type == Move.INVERSE) {
				reverse(m.x1, m.y1, m.delta);
			} else if ( m.type == Move.INSERT) {
				insert(m.x1, m.y1, m.y2, m.delta);
			} else if ( m.type == Move.SWAP){
				swap(m.x1, m.x2, m.y1, m.y2, m.delta);
			}
		}
		
		if (!Problems.getProblem().isSymmetric()) {
			evaluate();
		}
		//evaluate();
	}

	public Move findMove(int ci, int cj) {
		// INVERSE_INSERT, INVERSE_SWAP, INSERT_SWAP, INVERSE_INSERT_SWAP, INV_OR_INS_OR_SWAP;
		Move bestMove = null;
		if (Strategy.addEdgeType == EAddEdgeType.INVERSE) {
			bestMove = findInverse(ci, cj);
		} else if (Strategy.addEdgeType == EAddEdgeType.INSERT) {
			bestMove = findInsert(ci, cj);
		} else if (Strategy.addEdgeType == EAddEdgeType.SWAP){
			bestMove = findSwap(ci, cj);
		} else if (Strategy.addEdgeType == EAddEdgeType.INVERSE_INSERT){
			bestMove = findInverse(ci, cj);
			Move move = findInsert(ci, cj);
			if (move.delta < bestMove.delta) {
				bestMove = move;
			}
		} else if (Strategy.addEdgeType == EAddEdgeType.INVERSE_SWAP){
			bestMove = findInverse(ci, cj);
			Move move = findSwap(ci, cj);
			if (move.delta < bestMove.delta) {
				bestMove = move;
			}
		} else if (Strategy.addEdgeType == EAddEdgeType.INSERT_SWAP){
			bestMove = findInsert(ci, cj);
			Move move = findSwap(ci, cj);
			if (move.delta < bestMove.delta) {
				bestMove = move;
			}
		} else if (Strategy.addEdgeType == EAddEdgeType.INV_OR_INS_OR_SWAP){
			double p = Strategy.rand.nextDouble();
			if ( p < 1.0/3) {
				bestMove = findInverse(ci, cj);
			} else if ( p < 2.0 / 3) {
				bestMove = findInsert(ci, cj);
			} else {
				bestMove = findSwap(ci, cj);
			}
		} else {//INVERSE_INSERT_SWAP
			bestMove = findInverse(ci, cj);
			Move move = findInsert(ci, cj);
			if (move.delta < bestMove.delta) {
				bestMove = move;
			}
			move = findSwap(ci, cj);
			if (move.delta < bestMove.delta) {
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	//find the best suitable strategy to move cj to the back of ci
	public Move findMove_OLD(int ci, int cj) {
		Problems problem = Problems.getProblem();
		Move move = new Move(ci, ci, cj, cj, Integer.MAX_VALUE);
		int nci = next(ci);
		int nnci = next(nci);
		int pci = previous(ci);

		int ncj = next(cj);
		int pcj = previous(cj);

		double dci_cj = problem.getEdge(ci, cj);
		double dnci_ncj = problem.getEdge(nci, ncj);
		double dci_nci = problem.getEdge(ci, nci);
		double dcj_ncj = problem.getEdge(cj, ncj);
		double dcj_nci = problem.getEdge(cj, nci);
		double dpcj_ncj = problem.getEdge(pcj, ncj);
		double dpcj_cj = problem.getEdge(pcj, cj);
		double dcj_nnci = problem.getEdge(cj, nnci);
		double dpcj_nci = problem.getEdge(pcj, nci);
		double dnci_nnci = problem.getEdge(nci, nnci);
		double ivsDelta = dci_cj + dnci_ncj - (dci_nci + dcj_ncj);
		if ( ! problem.isSymmetric() ) {
			ivsDelta = this.findInverseDelta(ci, cj);
		}
		double insDelta = dci_cj + dcj_nci + dpcj_ncj -(dci_nci+dpcj_cj+dcj_ncj);
		double swaDelta = dci_cj+dcj_nnci+dpcj_nci+dnci_ncj -(dci_nci+dnci_nnci+dpcj_cj+dcj_ncj);

		if ( nci==cj || nnci==cj ) {
			swaDelta =  Integer.MAX_VALUE;
		}

		int ck = cj;
		//find a single block move 
		if (Strategy.insertType == EInsertType.BLOCK) {
			int len = Population.rand.nextInt( Strategy.maxMoveBlockSize );
			int nck = next(ck);
			while (len > 0 && nck != pci ) {
				ck = nck;
				nck = next(ck);
				len--;
			}
			double dck_nck = problem.getEdge(ck, nck);
			double dck_nci = problem.getEdge(ck, nci);
			double dpcj_nck = problem.getEdge(pcj, nck);
			double movDelta = dci_cj + dck_nci + dpcj_nck -(dci_nci+dpcj_cj+dck_nck);
			if (movDelta < insDelta) {
				insDelta = movDelta;
			} else {
				ck = cj;
			}
		} /*else if (Strategy.insertType == EInsertType.MULTI_BLOCK) {
			//find a best block move
			int len = Population.rand.nextInt( Strategy.maxMoveBlockSize );
			int ce = ck;
			int nck = next(ck);
			while (len > 0 && nck != pci ) {
				ck = nck;
				nck = next(ck);
				len--;
				double dck_nck = problem.getEdge(ck, nck);
				double dck_nci = problem.getEdge(ck, nci);
				double dpcj_nck = problem.getEdge(pcj, nck);
				double movDelta = dci_cj + dck_nci + dpcj_nck -(dci_nci+dpcj_cj+dck_nck);
				if (movDelta < insDelta) {
					insDelta = movDelta;
					ce = ck;
				} 
			}
			ck = ce;
		}*/

		if (EAddEdgeType.hasInverse(Strategy.addEdgeType) &&
			ivsDelta < move.delta ) {
			move.delta = ivsDelta;
			move.type = Move.INVERSE;
		} 
		if (EAddEdgeType.hasInsert(Strategy.addEdgeType) &&
			insDelta < move.delta) {
			move.y2 = ck;
			move.delta = insDelta;
			move.type = Move.INSERT;
		}
		if (EAddEdgeType.hasSwap(Strategy.addEdgeType) &&
			swaDelta < move.delta) {
			move.delta = swaDelta;
			move.type = Move.SWAP;
		}
		return move;
	}
	
	public Move findInverse(int ci, int cj) {
		Problems problem = Problems.getProblem();
		int nci = next(ci);
		int ncj = next(cj);

		double dci_cj = problem.getEdge(ci, cj);
		double dnci_ncj = problem.getEdge(nci, ncj);
		double dci_nci = problem.getEdge(ci, nci);
		double dcj_ncj = problem.getEdge(cj, ncj);

		double ivsDelta = dci_cj + dnci_ncj - (dci_nci + dcj_ncj);
		if ( ! problem.isSymmetric() ) {
			ivsDelta = this.findInverseDelta(ci, cj);
		}
		return new Move(ci, cj, ivsDelta);
	}
	
	public Move findInsert(int ci, int cj) {
		Problems problem = Problems.getProblem();
		int x1 = ci;
		int px1 = previous(x1);
		int nx1 = next(x1);

		int y1 = cj;
		int y2 = y1;

		//find a single block move 
		if (Strategy.insertType == EInsertType.BLOCK) {
			int len = Population.rand.nextInt( Strategy.maxMoveBlockSize );
			int ny = next(y2);
			while (len > 0 && ny != px1 ) {
				y2 = ny;
				ny = next(y2);
				len--;
			}
		} 
		
		double x1_y1 = problem.getEdge(x1, y1);
		double y2_nx1 = problem.getEdge(y2, nx1);
		double py1_ny2 = problem.getEdge(previous(y1), next(y2));
		double movDelta = x1_y1 + y2_nx1 + py1_ny2;
		
		double x1_nx1 = problem.getEdge(x1, nx1);
		double py1_y1 = problem.getEdge(previous(y1), y1);
		double y2_ny2 = problem.getEdge(y2, next(y2));
		movDelta -= x1_nx1 + py1_y1 + y2_ny2;

		return new Move(x1, y1, y2, movDelta);
	}
	
	public Move findSwap(int ci, int cj) {
		Problems problem = Problems.getProblem();
		int x1 = next(ci);
		int px1 = ci;
		int x2 = x1;
		
		if (next(x1)==cj) return findInsert(ci, cj);
		
		int y1 = cj;
		int y2 = y1;
		
		if (Strategy.swapType == ESwapType.BLOCK_BLOCK || Strategy.swapType == ESwapType.BLOCK_SINGLE) {
			int len = Population.rand.nextInt( Strategy.maxMoveBlockSize );
			int nx = next(x2);
			int py1 = previous(y1);
			while (len > 0 && nx != py1 ) {
				x2 = nx;
				nx = next(x2);
				len--;
			}
		} 

		if (Strategy.swapType == ESwapType.SINGLE_BLOCK || Strategy.swapType == ESwapType.BLOCK_BLOCK) {
			int len = Population.rand.nextInt( Strategy.maxMoveBlockSize );
			int ny = next(y2);
			int pci = previous(ci);
			while (len > 0 && ny != pci ) {
				y2 = ny;
				ny = next(y2);
				len--;
			}
		}
		
		double px1_y1 = problem.getEdge(ci, y1);
		double y2_nx2 = problem.getEdge(y2, next(x2));
		double py1_x1 = problem.getEdge(previous(y1), x1);
		double x2_ny2 = problem.getEdge(x2, next(y2));
		double swaDelta = px1_y1 + y2_nx2 + py1_x1 + x2_ny2;
		
		double px1_x1 = problem.getEdge(px1, x1);
		double x2_nx2 = problem.getEdge(x2, next(x2));
		double py1_y1 = problem.getEdge(previous(y1), y1);
		double y2_ny2 = problem.getEdge(y2, next(y2));
		swaDelta -= px1_x1 + x2_nx2 + py1_y1 + y2_ny2;
		
		return new Move(x1, x2, y1, y2, swaDelta);
	}
	
	public ArrayList<Move> findMoveList(int ci, int cj) {
		Problems problem = Problems.getProblem();
		ArrayList<Move> mList = new ArrayList<Move>();
		int nci = next(ci);
		int nnci = next(nci);

		int ncj = next(cj);
		int pcj = previous(cj);

		double dci_cj = problem.getEdge(ci, cj);
		double dnci_ncj = problem.getEdge(nci, ncj);
		double dci_nci = problem.getEdge(ci, nci);
		double dcj_ncj = problem.getEdge(cj, ncj);
		double dcj_nci = problem.getEdge(cj, nci);
		double dpcj_ncj = problem.getEdge(pcj, ncj);
		double dpcj_cj = problem.getEdge(pcj, cj);
		double dcj_nnci = problem.getEdge(cj, nnci);
		double dpcj_nci = problem.getEdge(pcj, nci);
		double dnci_nnci = problem.getEdge(nci, nnci);
		double ivsDelta = dci_cj + dnci_ncj - (dci_nci + dcj_ncj);
		if ( ! problem.isSymmetric() ) {
			ivsDelta = this.findInverseDelta(ci, cj);
		}
		double insDelta = dci_cj + dcj_nci + dpcj_ncj -(dci_nci+dpcj_cj+dcj_ncj);
		double swaDelta = dci_cj+dcj_nnci+dpcj_nci+dnci_ncj -(dci_nci+dnci_nnci+dpcj_cj+dcj_ncj);
		
		mList.add(new Move(ci, cj, ivsDelta));
		mList.add(new Move(ci, cj, cj, insDelta));
		if ( nci==cj || nnci==cj ) {
		} else {
			mList.add(new Move(ci, ci, cj, cj, swaDelta));
		}
		return mList;
	}
	
	private double findInverseDelta(int ci, int cj) {//for ASTSP
		Problems problem = Problems.getProblem();
		double delta = 0;
		int from = ci;
		int to = next(from);
		int ncj = next(cj);
		while (to != ncj) {
			delta += -problem.getEdge(from, to);
			from = to;
			to = next(from);
		}
		delta += -problem.getEdge(from, to);
		
		int nci = next(ci);
		delta += problem.getEdge(ci, cj);
		delta += problem.getEdge(nci, ncj);
		from = nci;
		to = next(nci);
		while ( to != ncj) {
			delta += problem.getEdge(to,from);
			from = to;
			to = next(from);
		}
		
		return delta;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Solution s) {
		if ( tourLength < s.tourLength) {
			return 1;
		} else if ( tourLength == s.tourLength) {
			return 0;
		} else {
			return -1;
		}
	}
	
	public String toString() {
		String str = "";
		int city = 0;
		do {
			str += city + "-";
			city = next(city);
		} while (city != 0);
		return str;
	}

	//public void setTourLength(long tourLength) { this.tourLength = tourLength; }
	public double getTourLength() { return tourLength; }
	public abstract int[] getTour();
	
	public double tourLength;
	
	public static long ivsTimes;
	public static long insTimes;
	public static long swpTimes;
}
