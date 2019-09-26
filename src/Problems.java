import java.io.FileReader;
import java.util.Scanner;


public class Problems {
	//to create a problem 
	private Problems(String filename)  {
		FileReader data;
		Scanner scan;
		int problemType=0;
		try {
			data = new FileReader(filename);
			scan = new Scanner(data);
			//to get the type of the file: 1----symmetric TSP, edge EUC_2D
			problemType = scan.nextInt();
			if (problemType == Problems.SYMMETRIC) {
				readSymmetricData(scan);
				isSymmetric = true;
				calcuDistance();
			} else if (problemType == Problems.ASYMMETRIC){
				readAsymmetricData(scan);
				isSymmetric = false;
			}
			//nearCityNumber = cityNumber;
			scan.close();
			data.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		this.setupNearCityList();
	}

	private void readSymmetricData(Scanner scan) throws Exception {
		//to get the number of city
		cityNumber = scan.nextInt();
		//to get the length of best tour
		bestTourLength = scan.nextInt();
		cityPosition = new double[cityNumber][2];
		try {
			cityDistance = new float[cityNumber][cityNumber];
			//dCityDistance = new double[cityNumber][cityNumber];
		} catch ( Throwable ex) {
			cityDistance = null;
			System.out.println(ex.getMessage()+", City Number:" + cityNumber);
		}
		for (int i=0; i<cityNumber;i++) {
			//to skip the city number
			scan.nextInt();
			//the x position
			cityPosition[i][0] = (scan.nextDouble());//scan.nextInt();
			//the y position
			cityPosition[i][1] = (scan.nextDouble());//scan.nextInt();
		}
		
		//try to read in best tour
		if (scan.hasNextInt()) {
			bestTour = new int[cityNumber];
			for (int i = 0; i<cityNumber; i++) {
				if (scan.hasNextInt()) 
					bestTour[i] = scan.nextInt() - 1;//city ID begings from 1
				else {
					bestTour = null;
					break;
				}
			}
		} else {
			bestTour = null;
		}
	}

	private void readAsymmetricData(Scanner scan) throws Exception {
		cityNumber = scan.nextInt();
		bestTourLength = scan.nextInt();
		cityPosition = new double[cityNumber][2];
		cityDistance = new float[cityNumber][cityNumber];
		for (int i=0; i<cityNumber;i++) {
			//System.out.println("city"+i);
			for (int j=0; j<cityNumber; j++) {
				cityDistance[i][j] = scan.nextInt();
			}
			if (fileName.contains("ftv90") || fileName.contains("ftv1")) {
				int skipLength = 171 - cityNumber;
				while (skipLength-->0) {
					scan.nextInt();
				}
			}
			
		}
	}

	//to calculate the distance between cities
	public void calcuDistance() {
		for (int i=0; i<cityNumber;i++) {
			for (int j=0; j<cityNumber;j++) {
				if (i==j) {
					if (cityDistance != null) {
						cityDistance[i][j]=Integer.MAX_VALUE;
					}
				} else {
					double distance;
					distance = (cityPosition[i][0]-cityPosition[j][0]);
					distance *= distance;
					distance += (cityPosition[i][1]-cityPosition[j][1])*(cityPosition[i][1]-cityPosition[j][1]);

					if (cityDistance != null) {
						if (Problems.USE_INTEGER_EDGE) {
							cityDistance[i][j] = (int)(Math.round(Math.sqrt(distance))+0.5);
						} else {
							cityDistance[i][j] = (float)Math.sqrt(distance);
						}
					}
				}
			}
		}
	}

	//to ouput the position
	public void outputPosition() {
		for (int i=0; i<cityNumber; i++) {
			System.out.print(i+1);
			System.out.print(':');
			System.out.print(cityPosition[i][0]);
			System.out.print('-');
			System.out.print(cityPosition[i][1]);
			System.out.println();
		}
	}
	
	//to output the distance between cities
	public void outputDistance() {
		for (int i=0; i<cityNumber; i++) {
			System.out.print(i+1);
			System.out.print(':');
			for (int j=0; j<cityNumber; j++) {
				System.out.print(getEdge(i,j));
				System.out.print('-');
			}
			System.out.println();
		}
	}
	

	private void setupNearCityList( ) {
		nearCityList = new int[cityNumber][];
		for (int i = 0; i < cityNumber; i++) {
			nearCityList[i] = setupNearCityList(i, nearCityNumber);
		}
	}
	
	
	private int[] setupNearCityList( int city, int nearCityNumber) {
		int[] cityList = new int[nearCityNumber];
		int[] d = new int[cityNumber];
		//calculate the distances between city i and other cities
		for (int j=0; j< cityNumber; j++) {
			if (j != city) {
				d[j] = (int) getEdge(city,j);
			} else {
				d[j] = Integer.MAX_VALUE;
			}
		}
		//find the nearest nearCityNumber cities from i
		for (int j = 0; j < nearCityNumber; j++) {
			int index = 0;
			for (int k = 0; k < cityNumber ; k++) {
				if (d[index] > d[k] ) {
					index = k;
				}
			}
			cityList[j] = index;
			d[index] = Integer.MAX_VALUE;
		}
		return cityList;
	}
	
	public int findInNearestCityList(int ci, int cj) {
		for (int i=0; i< nearCityList[ci].length; i++) {
			if ( cj == nearCityList[ci][i] )
				return i;
		}
		return -1;
	}
	
	
	public double evaluate(Solution solution) {
		return evaluate(solution.getTour());
		
	}
	
	public double evaluate(int[] tour) {
		double tourLength = 0;
		boolean[] visited = new boolean[tour.length];
		for (int i=0; i<=tour.length-1; i++) {
			if (visited[tour[i]]) {
				System.out.println("Wrong Solution");
			}
			tourLength += getEdge(i, tour[i]);//cityDistance[i][tour[i]];
			visited[tour[i]] = true;
		}
		for (int i=0; i<cityNumber; i++) {
			if (!visited[i]) {
				System.out.println("Wrong Solution");
			}
		}
		return tourLength;
	}
	
	
	public static Problems getProblem() {
		if (problem == null) {
			problem = new Problems(fileName);
		}
		return problem;
	}
	
	public static void setFileName(String fileName) {
		Problems.fileName = fileName;
		problem = new Problems(fileName);
	}

	public int getCityNumber() {
		return cityNumber;
	}
	public double[][] getCityPosition() {
		return cityPosition;
	}


	public double getEdge(int from, int to) {
		if ( cityDistance != null) {
			return cityDistance[from][to];
		} else {
			double distance;
			distance = (cityPosition[from][0]-cityPosition[to][0]);
			distance *= distance;
			distance += (cityPosition[from][1]-cityPosition[to][1])*(cityPosition[from][1]-cityPosition[to][1]);
			if ( Problems.USE_INTEGER_EDGE ) {
				return (int)(Math.round(Math.sqrt(distance))+0.5);
			} else {
				return (Math.sqrt(distance));
			}
		}
	}
	
	public double getBestTourLength() {	return bestTourLength;	}
	public static int getNearCityNumber() { return nearCityNumber;}
	public static String getFileName() { return Problems.fileName;}
	public boolean isSymmetric() {	return isSymmetric;	}

	public int[][] getNearCityList() { 
		if ( nearCityList == null) {
			this.setupNearCityList();
		}
		return nearCityList;	
	}

	public static void setNearCityParameters() {
		setNearCityParameters(nearCityNumber);
	}
	
	public static void setNearCityParameters(int nearCityNumber) {
		Problems.nearCityNumber = nearCityNumber;
		if ( problem != null) {
			problem.nearCityList = null;
		}
	}


	private static Problems problem = null;
	//the private data member of class TravelingSalesmanProblem
	private boolean isSymmetric=true;
	private int cityNumber;
	private static int nearCityNumber = 20;
	private double[][] cityPosition;
	private float[][] cityDistance;

	private int[][] nearCityList = null;
	
	private int[] bestTour;
	private double bestTourLength;

	public static final boolean USE_INTEGER_EDGE = true;
	public static final int SYMMETRIC = 1;
	public static final int ASYMMETRIC = 2;
	public static final int SYMMETRIC_GEO = 3;

	private static String fileName = null;
}