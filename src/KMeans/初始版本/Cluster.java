package KMeans.初始版本;

import java.util.List;

/**
 * Created by sunweipeng on 2017/7/13.
 */
public class Cluster {
    double[] center;
    double[] oldcenter;

    List<double[]> points;

    public double[] getCenter() {
        return center;
    }

    public void setCenter(double[] center) {
        this.center = center;
    }

    public List<double[]> getPoints() {
        return points;
    }

    public void setPoints(List<double[]> points) {
        this.points = points;
    }

    public double[] getOldcenter() {
        return oldcenter;
    }

    public void setOldcenter(double[] oldcenter) {
        this.oldcenter = oldcenter;
    }
}
