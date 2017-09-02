package KMeans.初始版本;

/**
 * Created by sunweipeng on 2017/7/13.
 */
public class Point {
    private double[] axis;

    public Point(double[] axis) {
        this.axis = axis;
    }

    public double[] getAxis() {
        return axis;
    }

    public void setAxis(double[] axis) {
        this.axis = axis;
    }
}
