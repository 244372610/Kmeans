package com.sunweipeng;


import java.util.*;

/**
 * 候选对象
 */
class Candidate {
    //在数据集中的位置
    int index;
    //密度半径
    double radius;
    Candidate(int index,double radius) {
        this.index = index;
        this.radius = radius;
    }
    @Override
    public boolean equals(Object obj) {
        Candidate candidate = (Candidate)obj;
        return (this.index == candidate.index);
    }
}


/**
 * K均值聚类算法
 */
public class KMeans {
    private int k;// 分成多少簇
    private int maxIterations;//最大迭代次数
    private List<PointWithLabel> dataSet;// 数据集链表
    private int desenity;//密度参数
    private int dim;//数据维度
    private Random random = new Random();
    private double[][] matrixDistances;
    /**
     * 设置需分组的原始数据集
     *
     * @param dataSet
     */
    public KMeans setDataSet(List<PointWithLabel> dataSet) {
        this.dataSet = dataSet;
        return this;
    }

    /**
     * 构造函数，传入需要分成的簇数量
     *
     * @param k 簇数量
     */
    public KMeans setK(int k) {
        this.k = k;
        return this;
    }

    /**
     * 设置最大迭代次数
     * @param maxIterations
     */
    public KMeans setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }

    /**
     * 半径密度参数
     * @param desenity
     * @return
     */
    public KMeans setDesenity(int desenity) {
        this.desenity = desenity;
        return this;
    }

    public static KMeansModel train(List<PointWithLabel> dataArray, int k, int dim,int desenity, int maxIterations) {
        return new KMeans().setDataSet(dataArray).setK(k).setDim(dim).setDesenity(desenity).setMaxIterations(maxIterations).run();
    }

    /**
     * 执行算法
     */
    public KMeansModel run() {
        long startTime = System.currentTimeMillis();
        System.out.println("kmeans begins");
        matrixDistances = getMatrix();
        KMeansModel model = kmeans();
        long endTime = System.currentTimeMillis();
        System.out.println("kmeans running time=" + (endTime - startTime) + "ms");
        System.out.println("kmeans ends");
        return model;
    }

    /**
     * 得到数据对象的距离矩阵
     * @return
     */
    private double[][] getMatrix() {
        matrixDistances = new double[dataSet.size()][dataSet.size()];
        for(int i=0;i<dataSet.size();i++) {
            for(int j=0;j<dataSet.size();j++) {
                if(i<j) {
                    matrixDistances[i][j] = distance(dataSet.get(i).getData(), dataSet.get(j).getData());
                }else {
                    matrixDistances[i][j] = matrixDistances[j][i];
                }
            }
        }
        return matrixDistances;
    }

    /**
     * 数据对象间的平均距离
     * @return
     */
    private double getAvarageDistance() {
        double sumDistance = 0;
        for(int i=0;i<dataSet.size();i++) {
            for(int j=i+1;j<dataSet.size();j++) {
                sumDistance += distance(dataSet.get(i).getData(),dataSet.get(j).getData());
            }
        }
        return 2*sumDistance/(dataSet.size()*dataSet.size()-1);
    }

    //计算各点到中心点的平均距离
    private double getAvarageRadius() {
        double sumDistance = 0;
        double[] center = new double[dim];
        for(int i=0;i<dim;i++) {
            for(int j=0;j<dataSet.size();j++) {
                center[i] += dataSet.get(j).getData()[i];
            }
            center[i]=center[i]/dataSet.size();
        }

        for(int i=0;i<dataSet.size();i++) {
            sumDistance += distance(dataSet.get(i).getData(),center);
        }
        return sumDistance/dataSet.size();
    }


    /**
     * 初始化中心数据链表，分成多少簇就有多少个中心点,去不同的中心点
     *
     * @return 中心点集
     */
    private List<double[]> initCenters() {
        ArrayList<double[]> center = new ArrayList<double[]>();
        int[] randoms = new int[k];
        boolean flag;
        int temp = random.nextInt(dataSet.size());
        randoms[0] = temp;
        for (int i = 1; i < k; i++) {
            flag = true;
            while (flag) {
                temp = random.nextInt(dataSet.size());
                int j = 0;
                while (j < i) {
                    /*if (temp == randoms[j]) {
                        break;
                    }*/
                    j++;
                }
                if (j == i) {
                    flag = false;
                }
            }
            randoms[i] = temp;
        }
        for (int i = 0; i < k; i++) {
            center.add(dataSet.get(randoms[i]).getData());// 生成初始化中心链表
        }
        return center;
    }


    /**
     * 初始化中心数据链表，分成多少簇就有多少个中心点,取不同的中心点
     *
     * @return 中心点集
     */
    private List<double[]> initCenters(int d) {
        //double avarageDistance = getAvarageDistance();
        //double averageRadius = getAvarageRadius();
        List<List<Double>> distances = new ArrayList();  //记录各点到其余点的距离
        for(int i=0;i<dataSet.size();i++) {
            List distance = new ArrayList<Double>();
            for(int j=0;j<dataSet.size();j++) {
                if(i!=j){
                    distance.add(matrixDistances[i][j]);
                }
            }
            distances.add(distance);
        }
        for(int i=0;i<distances.size();i++) {
            List<Double> distance = distances.get(i);
            Collections.sort(distance, new Comparator<Double>() {
                @Override
                public int compare(Double o1, Double o2) {
                    if(o2 < o1)
                        return 1;
                    else if(o2>o1)
                        return -1;
                    else
                        return 0;
                }
            });
        }

        double totalRadius = 0;
        List<Candidate> candidates = new ArrayList<Candidate>();
        for(int i=0;i<distances.size();i++) {
            List<Double> distance = distances.get(i);
            double radius = distance.get(d-1);
            totalRadius+=radius;
        }
        double averageRadius = totalRadius/distances.size();
        for(int i=0;i<distances.size();i++) {
            List<Double> distance = distances.get(i);
            double radius = distance.get(d-1);
            if(radius<=averageRadius) {
                candidates.add(new Candidate(i,radius));
            }
        }

        //开始合并
        List<Set<Candidate>> sets = new ArrayList<>();
        for(int i=0;i<candidates.size();i++) {
            for(int j=i+1;j<candidates.size();j++) {
                if(matrixDistances[i][j]<=(candidates.get(i).radius+candidates.get(j).radius)){
                    Set<Candidate> setA = new HashSet<Candidate>();
                    Set<Candidate> setB = new HashSet<Candidate>();
                    for(int s=0;s < sets.size();s++) {
                        Set<Candidate> set = sets.get(s);
                        if(set.contains(candidates.get(i))) {
                            setA = set;
                        }
                        if(set.contains(candidates.get(j))){
                            setB = set;
                        }
                    }
                    if(setA.size()==0&&setB.size()==0) {
                        Set<Candidate> temp = new HashSet<>();
                        temp.add(candidates.get(i));
                        temp.add(candidates.get(j));
                        sets.add(temp);
                    }else if(setA.size()==0){
                        setB.add(candidates.get(i));
                    }else if(setB.size()==0){
                        setA.add(candidates.get(j));
                    }else {
                        if(setA!=setB) {
                            setA.addAll(setB);
                            sets.remove(setB);
                        }
                    }
                }
            }
        }

        if(sets.size()>k) {
            return initCenters(d+1);
        }

        //找到每个集合中密度半径最小的点
        int[] index = new int[sets.size()];
        for(int i=0;i<sets.size();i++) {
            double minValue = Double.MAX_VALUE;
            int indexValue = 0;
            for (Candidate candidate : sets.get(i)) {
                if (candidate.radius < minValue) {
                    minValue = candidate.radius;
                    indexValue = candidate.index;
                }
            }
            index[i] = indexValue;
        }

       // List<double[]> centers = calculateCenters(sets);

        //所得sets的大小小于k，则进行分裂操作，找到距离中心最远的候选点
        double max = 0;
        Candidate isolate = null;  //要分离出来的中心点
        int setIndex=0;  //要执行分割操作的集合下标
        while(sets.size()<k){
            for(int i=0;i<sets.size();i++) {
                for(Candidate candidate:sets.get(i)) {
                    double temp = matrixDistances[index[i]][candidate.index];
                    if(temp>max) {
                        max = temp;
                        isolate = candidate;
                        setIndex = i;
                    }
                }
            }
            Set<Candidate> splitSet = sets.get(setIndex);

            splitSet.remove(isolate);
            Set<Candidate> newSet = new HashSet<>(); //要新生成的中心点集合
            newSet.add(isolate);
            sets.add(newSet);
            //centers.add(dataSet.get(isolate.index).getData());
            Iterator<Candidate> iterator = splitSet.iterator();
            while(iterator.hasNext()) {
                Candidate candidate = iterator.next();
                if(matrixDistances[index[setIndex]][candidate.index]<matrixDistances[isolate.index][candidate.index]){
                    newSet.add(candidate);
                    iterator.remove();
                }
                /*if(distance(centers.get(centers.size()-1),dataSet.get(candidate.index).getData())<distance(dataSet.get(candidate.index).getData(),centers.get(setIndex))){
                    newSet.add(candidate);
                    iterator.remove();
                    centers = calculateCenters(sets);
                }*/
            }
            //splitSet.removeAll(newSet);

            //找到每个集合中密度半径最小的点
            index = new int[sets.size()];
            for(int i=0;i<sets.size();i++) {
                double minValue = Double.MAX_VALUE;
                int indexValue = 0;
                for (Candidate candidate : sets.get(i)) {
                    if (candidate.radius < minValue) {
                        minValue = candidate.radius;
                        indexValue = candidate.index;
                    }
                }
                index[i] = indexValue;
            }
        }
        //List<double[]> centers = calculateCenters(sets);
        List<double[]> centers = new ArrayList<>();
        for(int i=0;i<index.length;i++) {
            centers.add(dataSet.get(index[i]).getData());
        }
        return centers;
    }

    private List<double[]> calculateCenters(List<Set<Candidate>> sets) {
        List<double[]> centers = new ArrayList<>();
        for(int i=0;i<sets.size();i++) {
            double caudisSum = 0;
            for(Candidate candidate: sets.get(i)) {
                caudisSum+=1/candidate.radius;  //这个地方注意，求的是半径的倒数和，为的是半径越小权重越大
            }
            double[] c = new double[dim];
            for(int j=0;j<dim;j++) {
                for(Candidate candidate: sets.get(i)) {
                    c[j]+=dataSet.get(candidate.index).getData()[j]*(1/candidate.radius)/caudisSum;
                }
            }
            centers.add(c);
        }
        return centers;
    }


    /**
     * 计算两个点之间的欧式距离
     *
     * @param element
     *            点1
     * @param center
     *            点2
     * @return 距离
     */
    private double distance(double[] element, double[] center) {
        double[] x = new double[dim];
        double distance = 0.0f;
        for(int i=0;i<dim;i++) {
            x[i]=element[i]-center[i];
            distance+=Math.pow(x[i],2);
        }
        return Math.sqrt(distance);
    }

    /**
     * 获取距离集合中最小距离的位置
     *
     * @param distance
     *            距离数组
     * @return 最小距离在距离数组中的位置
     */
    private int minDistance(double[] distance) {
        double minDistance = distance[0];
        int minLocation = 0;
        for (int i = 1; i < distance.length; i++) {
            if (distance[i] < minDistance) {
                minDistance = distance[i];
                minLocation = i;
            } else if (distance[i] == minDistance) // 如果相等，随机返回一个位置
            {
                if (random.nextInt(10) < 5) {
                    minLocation = i;
                }
            }
        }

        return minLocation;
    }

    /**
     *
     * 核心，将当前元素放到最小距离中心相关的簇中
     * 返回聚类
     */
    private List<List<PointWithLabel>> clusterSet(List<double[]> centers, List<List<PointWithLabel>> clusters) {
        double[] distance = new double[k];
        for (int i = 0; i < dataSet.size(); i++) {
            for (int j = 0; j < k; j++) {
                distance[j] = distance(dataSet.get(i).getData(), centers.get(j));
            }
            int minLocation = minDistance(distance);

            clusters.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中
        }
        return clusters;
    }

    /**
     * 求两点误差平方的方法
     *
     * @param element
     *            点1
     * @param center
     *            点2
     * @return 误差平方
     */
    private double errorSquare(double[] element, double[] center) {
        double errSquare = 0;
        for(int i=0;i<dim;i++) {
            errSquare = Math.pow(element[i]-center[i],2);
        }
        return errSquare;
    }

    /**
     * 计算误差平方和准则函数方法
     */
    public double countRule(List<List<PointWithLabel>> clusters, List<double[]> centers) {
        double cost = 0;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j < clusters.get(i).size(); j++) {
                cost += errorSquare(clusters.get(i).get(j).getData(), centers.get(i));

            }
        }
        return cost;
    }

    /**
     * 设置新的簇中心方法
     */
    private List<double[]> setNewCenter(List<List<PointWithLabel>> clusters, List<double[]> centers) {
        int dim = dataSet.get(0).getData().length;
        for (int i = 0; i < k; i++) {
            int n = clusters.get(i).size();
            if (n != 0) {
                double[] newCenter = new double[dim];
                for (int j = 0; j < n; j++) {
                    for(int k=0;k<dim;k++) {
                        newCenter[k] += clusters.get(i).get(j).getData()[k];
                    }
                }
                for(int k=0;k<dim;k++) {
                    // 设置一个平均值
                    newCenter[k] = newCenter[k] / n;
                }
                centers.set(i, newCenter);
            }
        }
        return centers;
    }



    /**
     * Kmeans算法核心过程方法
     */
    private KMeansModel kmeans() {
        long initStartTime = System.nanoTime();
        //初始化聚类中心
        List<double[]> centers = initCenters(desenity);
        List<List<PointWithLabel>> clusters = initClusters();
        double initTimeInSeconds = (System.nanoTime() - initStartTime) / 1e9;
        System.out.format("Initialization with $initializationMode took %.3f seconds.",initTimeInSeconds);

        boolean converaged = false;
        double oldcost = 0.0;
        int iteration = 0;

        KMeansModel model = new KMeansModel();

        long iterationStartTime = System.nanoTime();
        // 循环分组，直到误差不变为止
        while (iteration<maxIterations && !converaged) {

            converaged = true;

            clusters = clusterSet(centers,clusters);

            double cost = countRule(clusters,centers);


            if(Math.abs(cost-oldcost)>1e-7){
                converaged = false;
            }

            oldcost = cost;

            model.setClusters(clusters);
            model.setCenters(centers);

            centers = setNewCenter(clusters,centers);
            // printDataArray(center,"newCenter");
            iteration++;
            clusters = initClusters();
        }

        return model;

        // System.out.println("note:the times of repeat:m="+m);//输出迭代次数
    }

    private List<List<PointWithLabel>> initClusters() {
        List<List<PointWithLabel>> list = new ArrayList();
        for(int i=0;i<k;i++) {
            list.add(new ArrayList<PointWithLabel>());
        }
        return list;
    }


    /**
     * 打印数据，测试用
     *
     * @param pointWithLabels
     *            数据集
     * @param dataArrayName
     *            数据集名称
     */
    public static void printDataArray(List<PointWithLabel> pointWithLabels,
                               String dataArrayName) {
        for (int i = 0; i < pointWithLabels.size(); i++) {
            StringBuilder sb = new StringBuilder("print:"+ dataArrayName + "[" + i + "]={");
            for (int j = 0; j < 3; j++) {
                sb.append("%.1f,");
            }
            sb.append("%.1f,");
            sb.append("    %s}\n");
            //dataArray.get(i)[0] + "," + dataArray.get(i)[1] + "}";
            System.out.format(sb.toString(),pointWithLabels.get(i).getData()[0],pointWithLabels.get(i).getData()[1],pointWithLabels.get(i).getData()[2],pointWithLabels.get(i).getData()[3],pointWithLabels.get(i).getLabel());
        }
        System.out.println("===================================");
    }


    public KMeans setDim(int dim) {
        this.dim = dim;
        return this;
    }
}
