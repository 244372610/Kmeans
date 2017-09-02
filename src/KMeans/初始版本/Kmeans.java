package KMeans.初始版本;

import KMeans.改进版本.KMeansModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * K均值聚类算法
 */
public class Kmeans {
	private int k;// 分成多少簇
	private int maxIterations;//最大迭代次数
	private List<double[]> dataSet;// 数据集链表
	private Random random = new Random();

	/**
	 * 设置需分组的原始数据集
	 *
	 * @param dataSet
	 */

	public Kmeans setDataSet(List<double[]> dataSet) {
		this.dataSet = dataSet;
		return this;
	}

	/**
	 * 构造函数，传入需要分成的簇数量
	 *
	 * @param k
	 *            簇数量,若k<=0时，设置为1，若k大于数据源的长度时，置为数据源的长度
	 */
	public Kmeans setK(int k) {
		if (k <= 0) {
			k = 1;
		}
		this.k = k;
		return this;
	}

	/**
     * 设置最大迭代次数
	 * @param maxIterations
	 */
	public Kmeans setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
		return this;
	}

	public static KMeansModel train(ArrayList<double[]> dataArray, int k, int maxIterations) {
		return new Kmeans().setDataSet(dataArray).setK(k).setMaxIterations(maxIterations).run();
	}

	/**
	 * 执行算法
	 */
	public KMeansModel run() {
		long startTime = System.currentTimeMillis();
		System.out.println("kmeans begins");
		KMeansModel model = kmeans();
		long endTime = System.currentTimeMillis();
		System.out.println("kmeans running time=" + (endTime - startTime)
				+ "ms");
		System.out.println("kmeans ends");
		System.out.println();
		return model;
	}

	/**
	 * 初始化中心数据链表，分成多少簇就有多少个中心点,去不同的中心点
	 *
	 * @return 中心点集
	 */
	private List<double[]> initCenters() {
		ArrayList<double[]> center = new ArrayList<double[]>();
		int[] randoms = new int[k];
		boolean flag;
		int temp = random.nextInt(dataSet.size());
		randoms[0] = temp;
		for (int i = 1; i < k; i++) {
			flag = true;
			while (flag) {
				temp = random.nextInt(dataSet.size());
				int j = 0;

				while (j < i) {
					if (temp == randoms[j]) {
						break;
					}
					j++;
				}
				if (j == i) {
					flag = false;
				}
			}
			randoms[i] = temp;
		}
		for (int i = 0; i < k; i++) {
			center.add(dataSet.get(randoms[i]));// 生成初始化中心链表
		}
		return center;
	}

	/**
	 * 计算两个点之间的欧式距离
	 *
	 * @param element
	 *            点1
	 * @param center
	 *            点2
	 * @return 距离
	 */
	private double distance(double[] element, double[] center) {
		if(center.length!=4) {
			System.out.println(center.length);
			System.out.println(center[0]+":"+center[1]);
		}
		int length = element.length;
		double[] x = new double[length];
		double distance = 0.0f;
		for(int i=0;i<length;i++) {
			x[i]=element[i]-center[i];
			distance+=Math.pow(x[i],2);
		}
		return Math.sqrt(distance);
	}

	/**
	 * 获取距离集合中最小距离的位置
	 *
	 * @param distance
	 *            距离数组
	 * @return 最小距离在距离数组中的位置
	 */
	private int minDistance(double[] distance) {
		double minDistance = distance[0];
		int minLocation = 0;
		for (int i = 1; i < distance.length; i++) {
			if (distance[i] < minDistance) {
				minDistance = distance[i];
				minLocation = i;
			} else if (distance[i] == minDistance) // 如果相等，随机返回一个位置
			{
				if (random.nextInt(10) < 5) {
					minLocation = i;
				}
			}
		}

		return minLocation;
	}

	/**
	 *
	 * 核心，将当前元素放到最小距离中心相关的簇中
	 * 返回聚类
	 */
	private List<List<double[]>> clusterSet(List<double[]> centers,List<List<double[]>> clusters) {
		double[] distance = new double[k];
		for (int i = 0; i < dataSet.size(); i++) {
			for (int j = 0; j < k; j++) {
				distance[j] = distance(dataSet.get(i), centers.get(j));
			}
			int minLocation = minDistance(distance);

			clusters.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中
		}
		return clusters;
	}

	/**
	 * 求两点误差平方的方法
	 *
	 * @param element
	 *            点1
	 * @param center
	 *            点2
	 * @return 误差平方
	 */
	private double errorSquare(double[] element, double[] center) {
		int length = element.length;
		double errSquare = 0;
		for(int i=0;i<length;i++) {
			errSquare = Math.pow(element[i]-center[i],2);
		}
		return errSquare;
	}

	/**
	 * 计算误差平方和准则函数方法
	 */
	private double countRule(List<List<double[]>> clusters,List<double[]> centers) {
		double cost = 0;
		for (int i = 0; i < clusters.size(); i++) {
			for (int j = 0; j < clusters.get(i).size(); j++) {
				cost += errorSquare(clusters.get(i).get(j), centers.get(i));

			}
		}
		return cost;
	}

	/**
	 * 设置新的簇中心方法
	 */
	private List<double[]> setNewCenter(List<List<double[]>> clusters, List<double[]> centers) {
		int dim = dataSet.get(0).length;
		for (int i = 0; i < k; i++) {
			int n = clusters.get(i).size();
			if (n != 0) {
				double[] newCenter = new double[dim];
				for (int j = 0; j < n; j++) {
					for(int k=0;k<dim;k++) {
						newCenter[k] += clusters.get(i).get(j)[k];
					}
				}
				for(int k=0;k<dim;k++) {
					// 设置一个平均值
					newCenter[k] = newCenter[k] / n;
				}
				centers.set(i, newCenter);
			}
		}
		return centers;
	}



	/**
	 * Kmeans算法核心过程方法
	 */
	private KMeansModel kmeans() {
		long initStartTime = System.nanoTime();
		//初始化聚类中心
		List<double[]> centers = initCenters();
		List<List<double[]>> clusters = initClusters();
		double initTimeInSeconds = (System.nanoTime() - initStartTime) / 1e9;
		System.out.format("Initialization with $initializationMode took %.3f seconds.",initTimeInSeconds);

		boolean converaged = false;
		double oldcost = 0.0;
		int iteration = 0;

		KMeansModel model = new KMeansModel();

		long iterationStartTime = System.nanoTime();
		// 循环分组，直到误差不变为止
		while (iteration<maxIterations && !converaged) {

			converaged = true;

			clusters = clusterSet(centers,clusters);

			double cost = countRule(clusters,centers);


			if(Math.abs(cost-oldcost)>1e-7){
				converaged = false;
			}

			oldcost = cost;

			model.setClusters(clusters);
			model.setCenters(centers);

			centers = setNewCenter(clusters,centers);
			// printDataArray(center,"newCenter");
			iteration++;
			clusters = initClusters();
		}

		return model;

		// System.out.println("note:the times of repeat:m="+m);//输出迭代次数
	}

	private List<List<double[]>> initClusters() {
		List<List<double[]>> list = new ArrayList();
		for(int i=0;i<k;i++) {
			list.add(new ArrayList<double[]>());
		}
		return list;
	}


	/**
	 * 打印数据，测试用
	 *
	 * @param dataArray
	 *            数据集
	 * @param dataArrayName
	 *            数据集名称
	 */
	public void printDataArray(List<double[]> dataArray,
							   String dataArrayName) {
		int dim = dataSet.get(0).length;
		for (int i = 0; i < dataArray.size(); i++) {
			StringBuilder sb = new StringBuilder("print:"+ dataArrayName + "[" + i + "]={");
			for (int j = 0; j < dim-1; j++) {
				sb.append("%.1f,");
			}
			sb.append("%.1f}\n");
			//dataArray.get(i)[0] + "," + dataArray.get(i)[1] + "}";
			System.out.format(sb.toString(),dataArray.get(i)[0],dataArray.get(i)[1],dataArray.get(i)[2],dataArray.get(i)[3]);
		}
		System.out.println("===================================");
	}


}
