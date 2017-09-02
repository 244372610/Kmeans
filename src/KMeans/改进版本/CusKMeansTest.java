package KMeans.改进版本;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by sunweipeng on 2017/7/13.
 */
public class CusKMeansTest {

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
                return doubleaxis;
            }
        }).collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException {
        prepareData();
        //初始化一个Kmean对象，将k置为10
        KMeans kmeans=new KMeans().setK(3).setDataSet(dataSet).setDim(4).setDesenity(4).setMaxIterations(10000);

        KMeansModel model= kmeans.run();

        List<List<double[]>> clusters = model.getClusters();
        //查看结果
        for(int i=0;i<clusters.size();i++)
        {
            kmeans.printDataArray(clusters.get(i), "cluster["+i+"]");
        }
    }
}
