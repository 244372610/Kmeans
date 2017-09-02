import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Canopy算法 借助canopy算法计算对应的Kmeans中的K值大小
 * 其中对于计算K值来说，canopy算法中的T1没有意义，只用设定T2(T1>T2) 我们这里将T2设置为平均距离
 * 
 * @author YD
 *
 */
public class Canopy {
    private List<Point> points = new ArrayList<Point>(); // 进行聚类的点
    private List<List<Point>> clusters = new ArrayList<List<Point>>(); // 存储簇
    private double T2 = -1; // 阈值

    public Canopy(List<Point> points) {
        for (Point point : points)
            // 进行深拷贝
            this.points.add(point);
    }

    /**
     * 进行聚类，按照Canopy算法进行计算，将所有点进行聚类
     */
    public void cluster() {
        T2 = getAverageDistance(points);
        while (points.size() != 0) {
            List<Point> cluster = new ArrayList<Point>();
            Point basePoint = points.get(0); // 基准点
            cluster.add(basePoint);
            points.remove(0);
            int index = 0;
            while (index < points.size()) {
                Point anotherPoint = points.get(index);
                double distance = Math.sqrt((basePoint.x - anotherPoint.x)
                        * (basePoint.x - anotherPoint.x)
                        + (basePoint.y - anotherPoint.y)
                        * (basePoint.y - anotherPoint.y)
                        + (basePoint.z - anotherPoint.z)
                        * (basePoint.z - anotherPoint.z)
                        + (basePoint.w - anotherPoint.w)
                        * (basePoint.w - anotherPoint.w));
                if (distance <= T2) {
                    cluster.add(anotherPoint);
                    points.remove(index);
                } else {
                    index++;
                }
            }
            clusters.add(cluster);
        }
    }

    /**
     * 得到Cluster的数目
     * 
     * @return 数目
     */
    public int getClusterNumber() {
        return clusters.size();
    }

    /**
     * 获取Cluster对应的中心点(各点相加求平均)
     * 
     * @return
     */
    public List<Point> getClusterCenterPoints() {
        List<Point> centerPoints = new ArrayList<Point>();
        for (List<Point> cluster : clusters) {
            centerPoints.add(getCenterPoint(cluster));
        }
        return centerPoints;
    }

    /**
     * 得到的中心点(各点相加求平均)
     * 
     * @return 返回中心点
     */
    private double getAverageDistance(List<Point> points) {
        double sum = 0;
        int pointSize = points.size();
        int count = 0;
        for (int i = 0; i < pointSize; i++) {
            for (int j = 0; j < pointSize; j++) {
                if (i == j)
                    continue;
                count++;
                Point pointA = points.get(i);
                Point pointB = points.get(j);
                sum += Math.sqrt((pointA.x - pointB.x) * (pointA.x - pointB.x)
                        + (pointA.y - pointB.y) * (pointA.y - pointB.y)
                        + (pointA.z - pointB.z) * (pointA.z - pointB.z)
                        + (pointA.w - pointB.w) * (pointA.w - pointB.w));
            }
        }
        int distanceNumber = pointSize * (pointSize - 1);
        double T2 = sum / distanceNumber ; // 平均距离的一半
        return T2;
    }

    /**
     * 得到的中心点(各点相加求平均)
     * 
     * @return 返回中心点
     */
    private Point getCenterPoint(List<Point> points) {
        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;
        double sumW = 0;
        for (Point point : points) {
            sumX += point.x;
            sumY += point.y;
            sumZ += point.z;
            sumW += point.w;
        }
        int clusterSize = points.size();
        Point centerPoint = new Point(sumX / clusterSize, sumY / clusterSize, sumZ/clusterSize, sumW/clusterSize);
        return centerPoint;
    }

    /**
     * 获取阈值T2
     * 
     * @return 阈值T2
     */
    public double getThreshold() {
        return T2;
    }
    
    /**
     * 测试9个点，进行操作
     * @param args
     */
    public static void main(String[] args) throws IOException {
        List<Point> points = Files.readAllLines(Paths.get("/Users/sunweipeng/development/source/KMeans/src/iris")).stream().map(new Function<String, Point>() {
            @Override
            public Point apply(String s) {
                String[] axis = s.split(",");
                double[] doubleaxis = new double[axis.length];
                int i = 0;
                for(String axi:axis) {
                    doubleaxis[i] = Float.valueOf(axis[i]);
                    i++;
                }
                Point point = new Point(doubleaxis[0],doubleaxis[1],doubleaxis[2],doubleaxis[3]);
                return point;
            }
        }).collect(Collectors.toList());

        Canopy canopy = new Canopy(points);
        canopy.cluster();

                //获取canopy数目
        int clusterNumber = canopy.getClusterNumber();
        System.out.println(clusterNumber);


                //获取canopy中T2的值
        System.out.println(canopy.getThreshold());
    }
}