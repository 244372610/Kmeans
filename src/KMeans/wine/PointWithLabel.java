package KMeans.wine;

/**
 * Created by sunweipeng on 2017/7/13.
 */
public class PointWithLabel {

    private double[] data;

    private String label;

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
