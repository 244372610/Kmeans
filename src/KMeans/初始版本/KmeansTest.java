package KMeans.初始版本;


import KMeans.改进版本.KMeansModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KmeansTest {

	static List<double[]> dataSet;

	public static void prepareData() throws IOException {
		dataSet = Files.readAllLines(Paths.get("/Users/sunweipeng/development/source/KMeans/src/iris")).stream().map(new Function<String, double[]>() {
			@Override
			public double[] apply(String s) {
				String[] axis = s.split(",");
				double[] doubleaxis = new double[axis.length];
				int i = 0;
				for(String axi:axis) {
					doubleaxis[i] = Float.valueOf(axis[i]);
					i++;
				}
				Point point = new Point(doubleaxis);
				return doubleaxis;
			}
		}).collect(Collectors.toList());
	}


	public  static void main(String[] args) throws IOException {

		prepareData();
		//初始化一个Kmean对象，将k置为10
		Kmeans kmeans=new Kmeans().setK(3).setDataSet(dataSet).setMaxIterations(10000);

		KMeansModel model= kmeans.run();

		List<List<double[]>> clusters = model.getClusters();
		//查看结果
		for(int i=0;i<clusters.size();i++)
		{
			kmeans.printDataArray(clusters.get(i), "cluster["+i+"]");
		}
		
	}
}
