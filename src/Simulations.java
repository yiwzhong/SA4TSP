import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Simulations {

	public static void main(String[] args) {
		Problems.setNearCityParameters(Simulations.nearCityNumber);
		String filePath = (new File("")).getAbsolutePath() + "/../TSPLIB6small/"; 
		//filePath = (new File("")).getAbsolutePath() + "/../TSPLIB6median/"; 
		if (Simulations.TEST_TYPE == ETestType.SINGLE_INSTANCE) {
			filePath = (new File("")).getAbsolutePath() + "/../TSPLIB6small/";
			String fileName = filePath+"01eil51.txt";
			testSingleInstance(fileName);
		} else if (Simulations.TEST_TYPE == ETestType.MULTIPLE_INSTANCE) {
			testPerformance(filePath);
		} else if (Simulations.TEST_TYPE == ETestType.PARAMETER_TUNNING_FOR_ALPHA) {
			parametersTunning4alpha(filePath);
		} else if (Simulations.TEST_TYPE == ETestType.COMPARE_KNOWLEDGE_TYPE) {
			compareKnowledgeType(filePath);
		} else if (Simulations.TEST_TYPE == ETestType.COMPARE_NEIGHBOR_TYPE) {
			compareNeighborType(filePath);
		} 
	}
	
	
	
	private static void parametersTunning4alpha(String filePath) {
		java.io.File dir = new java.io.File(filePath);
		java.io.File[] files = dir.listFiles();
		String pathName = filePath.substring(filePath.lastIndexOf("/", filePath.length()-2)).substring(1);
		pathName = pathName.substring(0, pathName.length()-1);
		
		String fileName = (new File("")).getAbsolutePath() + "/results/Parameters/";
		fileName +=  pathName + "-" + Simulations.getParaSetting();
		//fileName += " alpha tunning results.csv";
		
		//System.out.println(dir.exists());
		List<double[]> resultsList = new ArrayList<>();
		List<Double> paras = new ArrayList<>();
		//double min = 0.9, max = 1.0, step = 0.01;
		//double min = 0.99, max = 1.0, step = 0.001;
		double min = 0.985, max = 0.995, step = 0.001;
		for (int i = 0; i <= 10; i++) {
			double scale = min + i * step;
			paras.add(scale);
		}
		fileName += " alpha (" + min + "-" + max + "-" + step + ") tunning results.csv";
		
		for (java.io.File file : files) {
			Problems.setFileName(file.getAbsolutePath());
			for (double para : paras) {
				Simulations.alpha = para;
				System.out.println(file.getName() + ",alpha--" + para);
				double[] rs = runSimulation(Simulations.MAX_GENERATION, Simulations.TIMES);
				for (double r : rs) {
					System.out.print(r + "\t");
				}
				System.out.println();
				resultsList.add(rs);
				Simulations.saveParametersTunningResults(fileName, paras, resultsList);
			}
		}
	}

	private static void compareKnowledgeType(String filePath) {
		java.io.File dir = new java.io.File(filePath);
		java.io.File[] files = dir.listFiles();
		String pathName = filePath.substring(filePath.lastIndexOf("/", filePath.length()-2)).substring(1);
		pathName = pathName.substring(0, pathName.length()-1);
		
		String fileName = (new File("")).getAbsolutePath() + "/results/Parameters/";
		fileName +=  pathName + "-" + Simulations.getParaSetting();
		fileName += " compare knowledge type results.csv";
		
		//System.out.println(dir.exists());
		List<double[]> resultsList = new ArrayList<>();
		EKnowledgeType[] knowledgeTypes = EKnowledgeType.values();
		List<Double> paras = new ArrayList<>();
		for (int i = 0; i < knowledgeTypes.length; i++) {
			paras.add(new Double(i));
		}
		for (java.io.File file : files) {
			Problems.setFileName(file.getAbsolutePath());
			for (EKnowledgeType knowledgeType : knowledgeTypes) {
				Simulations.knowledgeType = knowledgeType;
				System.out.println(file.getName() + ", knowledge type--" + knowledgeType);
				double[] rs = runSimulation(Simulations.MAX_GENERATION, Simulations.TIMES);
				for (double r : rs) {
					System.out.print(r + "\t");
				}
				System.out.println();
				resultsList.add(rs);
				Simulations.saveParametersTunningResults(fileName, paras, resultsList);
			}
		}
	}
	
	private static void compareNeighborType(String filePath) {
		java.io.File dir = new java.io.File(filePath);
		java.io.File[] files = dir.listFiles();
		String pathName = filePath.substring(filePath.lastIndexOf("/", filePath.length()-2)).substring(1);
		pathName = pathName.substring(0, pathName.length()-1);
		
		String fileName = (new File("")).getAbsolutePath() + "/results/Parameters/";
		fileName +=  pathName + "-" + Simulations.getParaSetting();
		fileName += " compare neighbor type results.csv";
		
		//System.out.println(dir.exists());
		List<double[]> resultsList = new ArrayList<>();
		ENeighborType[] neighborTypes = ENeighborType.values();
		List<Double> paras = new ArrayList<>();
		for (int i = 0; i < neighborTypes.length; i++) {
			paras.add(new Double(i));
		}
		for (java.io.File file : files) {
			Problems.setFileName(file.getAbsolutePath());
			for (ENeighborType neighborType : neighborTypes) {
				Simulations.neighborType = neighborType;
				System.out.println(file.getName() + ", neighbor type--" + neighborType);
				double[] rs = runSimulation(Simulations.MAX_GENERATION, Simulations.TIMES);
				for (double r : rs) {
					System.out.print(r + "\t");
				}
				System.out.println();
				resultsList.add(rs);
				Simulations.saveParametersTunningResults(fileName, paras, resultsList);
			}
		}	
	}
	
	
	private static double[] testSingleInstance(String fileName) {
		Problems.setFileName(fileName);
		double[] results = runSimulation(Simulations.MAX_GENERATION, Simulations.TIMES);
		for (double d : results) {
			System.out.print(d + "\t");
		}
		System.out.println();
		return results;
	}

	private static double[] testPerformance(String filePath) {
		java.io.File dir = new java.io.File(filePath);
		java.io.File[] files = dir.listFiles();
		String pathName = filePath.substring(filePath.lastIndexOf("/", filePath.length()-2)).substring(1);
		pathName = pathName.substring(0, pathName.length()-1);
		String fileName = (new File("")).getAbsolutePath() + "\\results\\Performance\\";
		fileName += pathName;
		fileName += "-" + Simulations.getParaSetting() + " results.csv";
		
		//System.out.println(dir.exists());
		List<double[]> resultList = new ArrayList<>();
		List<String> fileList = new ArrayList<>();
		for (java.io.File file : files) {
			Problems.setFileName(file.getAbsolutePath());
			double[] rs;
			rs = runSimulation(Simulations.MAX_GENERATION,  Simulations.TIMES);
			resultList.add(rs);
			fileList.add(file.getName());
			System.out.print(file.getName()+"\t");
			for (double d : rs) {
				System.out.print(d+"\t");
			}
			System.out.println();
			Simulations.saveFinalResults(fileName, fileList, resultList);
		}
		double[] totals = new double[resultList.get(0).length];
		for (int i = 0; i < files.length; i++) {
			System.out.println();
			System.out.print(files[i].getName()+"\t");
			double[] datas = resultList.get(i);
			for (int j = 0; j < datas.length; j++) {
				System.out.print(datas[j]+"\t");
				totals[j] += datas[j];
			}
		}
		System.out.println("\t");
		for (int j = 0; j < totals.length; j++) {
			totals[j] = Math.round(totals[j]/files.length*1000)/1000.0;
			System.out.print(totals[j]+"\t");
		}
		return totals; //average data for all files
	}
	
	private static void saveFinalResults(String fileName, List<String> fileList, List<double[]> resultList) {
		if ( !Simulations.SAVING_FINAL_RESULTS) {
			return;
		}
		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));
			for (int i = 0; i < fileList.size(); i++) {
				printWriter.println();
				printWriter.print(fileList.get(i));
				for (double data : resultList.get(i)) {
					printWriter.print("," + data);
				}
			}
			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static double[] runSimulation(final int MAX_GENERATION, final int TIMES) {
		double duration = (new java.util.Date()).getTime();
		double bMakespan = Problems.getProblem().getBestTourLength();
		Solution s = null;
		double[] makespans = new double[TIMES];
		int[] iterations = new int[TIMES]; //last improving iteration
		System.out.println(Simulations.getParaSetting());
		for (int i = 0; i < TIMES; i++) {
			if (Simulations.method == EMethodType.SA) {
			    s = Methods.simulatedAnnealingWithAdativeInitialTemperature(MAX_GENERATION);
			} else if (Simulations.method == EMethodType.TS) {
				s = Methods.tabuSearch();
			} else if (Simulations.method == EMethodType.VND) {
				s = Methods.varaibleNeighborDescent();
			} else if (Simulations.method == EMethodType.VNS) {
				s = Methods.varaibleNeighborSearch();
			} else if (Simulations.method == EMethodType.GVNS) {
				s = Methods.generalVaraibleNeighborSearch();
			} else {
				System.out.println("Cannot reach here!");
			}
			makespans[i] = s.getTourLength();
			iterations[i] = s.getLastImproving();
			if (Simulations.OUT_INDIVIDUAL_RUNNING_DATA) {
				System.out.println( i + " -- " + makespans[i] + "," + iterations[i]);
			}
		}
		duration = (new java.util.Date()).getTime()-duration;
		duration /= TIMES;
		duration = Math.round(duration/1000*1000)/1000.0;

		double min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, count = 0;
		double total = 0;
		double totalIterations = 0;
		for (int i = 0; i < makespans.length; i++) {
			double mk = makespans[i];
			total += mk;
			if ( (mk-bMakespan) * (1.0/bMakespan) *100 < 1) {
				count++;
			}
			if ( mk < min) {
				min = mk;
			}
			if (mk > max) {
				max = mk;
			}
			totalIterations += iterations[i];
		}
		double ave = total / TIMES;
		double bpd = Math.round((min-bMakespan) * (1.0/bMakespan) *100*1000)/1000.0;
		double wpd = Math.round((max-bMakespan) * (1.0/bMakespan) *100*1000)/1000.0;
		double apd = Math.round((ave-bMakespan) * (1.0/bMakespan) *100*1000)/1000.0;
		double itr = Math.round(totalIterations/iterations.length*10)/10; //average last improving iteration
		double[] stat =  new double[] {bMakespan, min, max, ave, bpd, wpd, apd, count, itr, duration};
	
		double[] results = new double[stat.length + makespans.length];
		System.arraycopy(stat, 0, results, 0, stat.length);
		System.arraycopy(makespans, 0, results, stat.length, makespans.length);
		return results;
	}
	
	
	private static void saveParametersTunningResults(String fileName, List<Double> paras, List<double[]> resultsList) {
		if (!Simulations.SAVING_PARA_TUNNING) { return;	}
		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));
			for (int idx = 0; idx < resultsList.size(); idx++) {
				double[] rs = resultsList.get(idx);
				printWriter.println();
				printWriter.print(paras.get(idx % paras.size()));
				for (int j = 0; j < rs.length; j++) {
					printWriter.print(","+rs[j]);
				}
			}
			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static boolean isSavingFinalResults() { return Simulations.SAVING_FINAL_RESULTS;}
	public static boolean isSavingProcessData() { return Simulations.SAVING_PROCESS_DATA;}
	public static String getParaSetting() {
		String str = Simulations.method + "-" + Simulations.knowledgeType + "-" + Simulations.neighborType;
		str +=  " NCN=" + Simulations.nearCityNumber;
		str += " G=" + Simulations.MAX_GENERATION + " MCLF=" + Simulations.MARKOV_CHAIN_LENGTH_FACTOR;
		return str;
	}
	
	
	public static EKnowledgeType getKnowledgeType() { return Simulations.knowledgeType; }
	public static int getMaxInsertBlockSize() { return Simulations.maxInsertBlockSize; }
	public static void clearMaxInsertBlockSize() { Simulations.maxInsertBlockSize = 1; }
	
	public static EKnowledgeType knowledgeType = EKnowledgeType.PROBLEM;
	public static ENeighborType neighborType = ENeighborType.BEST;
		
	public static final EMethodType method = EMethodType.GVNS;
	public static final int MAX_GENERATION = 1000;
	public static final int MARKOV_CHAIN_LENGTH_FACTOR = 100;
	public static final int TIMES = 30;

	public static final boolean OUT_INDIVIDUAL_RUNNING_DATA = true;
	public static final boolean SAVING_PROCESS_DATA = false;
	public static final boolean SAVING_FINAL_RESULTS = false;
	public static final boolean SAVING_PARA_TUNNING = true;
	public static final boolean USE_GREEDY_RANDOM_STRATEGY = true;
	public static final ETestType TEST_TYPE = ETestType.MULTIPLE_INSTANCE;
	
	//parameters for SA algorithm 
	public static double t0 = 1000;
	public static double alpha = 0.992;
	private static int maxInsertBlockSize = 10;
	private static int nearCityNumber = 20;
}
