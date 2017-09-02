package CSKmeans;

import java.util.List;

/**
 * Created by sunweipeng on 2017/7/13.
 */
public class KMeansModel {

    private List<List<PointWithLabel>> clusters;

    private List<double[]> centers;



    public List<double[]> getCenters() {
        return centers;
    }

    public List<List<PointWithLabel>> getClusters() {
        return clusters;
    }

    public void setClusters(List<List<PointWithLabel>> clusters) {
        this.clusters = clusters;
    }

    public void setCenters(List<double[]> centers) {
        this.centers = centers;
    }

}
