package com.sunweipeng;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author SUNWP
 * @create 2017-07-19 14:59
 */
public class Test {

    static List<PointWithLabel> dataSet;

    public static void preProcess(int dim) {
        double[] min = new double[dim];
        double[] max = new double[dim];
        for(int i=0;i<dim;i++) {
            min[i] = Double.MAX_VALUE;
            max[i] = Double.MIN_VALUE;
        }
        for(PointWithLabel pointWithLabel : dataSet) {
            double[] data = pointWithLabel.getData();
            for(int i=0;i<data.length;i++) {
                if(min[i]>data[i]) {
                    min[i]=data[i];
                }
                if(max[i]<data[i]) {
                    max[i] = data[i];
                }
            }
        }

        for(PointWithLabel pointWithLabel:dataSet) {
            double[] data = pointWithLabel.getData();
            for(int i=0;i<data.length;i++) {
                data[i] = (data[i] - min[i])/(max[i]-min[i]);
            }
        }
    }
}
