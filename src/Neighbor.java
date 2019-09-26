public class Neighbor {
	public int type;
	public int x1;
	public int x2;
	public int y1;
	public int y2;
	public double delta;
		
	public Neighbor(int x1, int y1, double delta) {
		this.x1 = x1;
		this.y1 = y1;
		this.delta = delta;
		this.type = Neighbor.INVERSE;
	}

	public Neighbor(int x1, int y1, int y2, double delta) {
		this.x1 = x1;
		this.y1 = y1;
		this.y2= y2;
		this.delta = delta;
		this.type = Neighbor.INSERT;
	}
	
	public Neighbor(int x1, int x2, int y1, int y2, double delta) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2= y2;
		this.delta = delta;
		this.type = Neighbor.SWAP;
	}
	
	@Override
	public String toString() {
		return x1 + "-" + y1 + "-" + type;
	}
	
	public int getType() { return type;}
	public double getDelta() { return delta;}

	public static final int INVERSE = 0;
	public static final int INSERT = 1;
	public static final int SWAP = 2;
}
