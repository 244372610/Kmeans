package KMeans.wine;


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

    static List<PointWithLabel> dataSet;

    public static void prepareData() throws IOException {
        dataSet = Files.readAllLines(Paths.get("/Users/sunweipeng/development/source/KMeans/src/wine.data")).stream().map(new Function<String, PointWithLabel>() {
            @Override
            public PointWithLabel apply(String s) {
                String[] axis = s.split(",");
                double[] doubleaxis = new double[axis.length-1];
                String label = axis[0];
                for(int i=1;i<axis.length;i++) {
                    doubleaxis[i-1] = Double.valueOf(axis[i]);
                }
                PointWithLabel pointWithLabel = new PointWithLabel();
                pointWithLabel.setData(doubleaxis);
                pointWithLabel.setLabel(label);
                return pointWithLabel;
            }
        }).collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException {
        prepareData();
        //初始化一个Kmean对象，将k置为10
        KMeans kmeans=new KMeans().setK(3).setDim(13).setDesenity(2).setDataSet(dataSet).setMaxIterations(10000);

        KMeansModel model= kmeans.run();

        List<List<PointWithLabel>> clusters = model.getClusters();
        //查看结果
        for(int i=0;i<clusters.size();i++)
        {
            kmeans.printDataArray(clusters.get(i), "cluster["+i+"]");
        }
    }
}
