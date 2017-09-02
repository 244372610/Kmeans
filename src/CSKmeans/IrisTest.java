package CSKmeans;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by sunweipeng on 2017/7/13.
 */
public class IrisTest {

    static List<PointWithLabel> dataSet;

    public static void prepareData() throws IOException {
        dataSet = Files.readAllLines(Paths.get("/Users/sunweipeng/development/source/KMeans/src/iris.data")).stream().map(new Function<String, PointWithLabel>() {
            @Override
            public PointWithLabel apply(String s) {
                String[] axis = s.split(",");
                double[] doubleaxis = new double[axis.length-1];
                String label = axis[axis.length-1];
                for(int i=0;i<axis.length-1;i++) {
                    doubleaxis[i] = Double.valueOf(axis[i]);
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
       // KMeansModel model = KMeans.train(dataSet, 3, 4, 2, 1000);

        KMeansModel model = null;
        double correctradio = 0;
        int d = 0;
        for(int desenity=1;desenity<50;desenity++) {
            model = new KMeans().setDataSet(dataSet).setK(3).setDim(4).setDesenity(desenity).setMaxIterations(1000).run();
            List<List<PointWithLabel>> clusters = model.getClusters();
            int errorcount = 0;
            for (int i = 0; i < clusters.size(); i++) {
                Map<String, Integer> map = new HashMap<>();
                for (int j = 0; j < clusters.get(i).size(); j++) {
                    String label = clusters.get(i).get(j).getLabel();
                    if (map.get(label) == null) {
                        map.put(label, 1);
                    } else {
                        map.put(label, map.get(label) + 1);
                    }
                }
                int max = 0;
                for (String key : map.keySet()) {
                    int number = map.get(key);
                    if (number > max) {
                        max = number;
                    }
                }
                errorcount += clusters.get(i).size() - max;
            }
            double radio = 1 - 1.0 * errorcount / dataSet.size();
            if (radio > correctradio) {
                correctradio = radio;
                d=desenity;
            }
        }

       /* //查看结果
        for(int i=0;i<clusters.size();i++)
        {
            KMeans.printDataArray(clusters.get(i), "cluster["+i+"]");
        }
*/
       System.out.println(d);
        System.out.println(correctradio);
    }
}
