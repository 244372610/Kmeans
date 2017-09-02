package KMeans.改进版本;

import java.util.List;

/**
 * Created by sunweipeng on 2017/7/13.
 */
public class KMeansModel {
    private List<List<double[]>> clusters;

    private List<double[]> centers;

    public List<List<double[]>> getClusters() {
        return clusters;
    }

    public void setClusters(List<List<double[]>> clusters) {
        this.clusters = clusters;
    }

    public List<double[]> getCenters() {
        return centers;
    }

    public void setCenters(List<double[]> centers) {
        this.centers = centers;
    }

}
