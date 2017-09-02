package KMeans.wine;


import java.util.*;

class Candidate {
    int index;
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
    private Random random = new Random();
    private int dim;
    private int d;
    public KMeans setDim(int dim) {
        this.dim = dim;
        return this;
    }

    public KMeans setDesenity(int d) {
        this.d = d;
        return this;
    }

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
     * @param k
     *            簇数量,若k<=0时，设置为1，若k大于数据源的长度时，置为数据源的长度
     */
    public KMeans setK(int k) {
        if (k <= 0) {
            k = 1;
        }
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

    public static KMeansModel train(ArrayList<PointWithLabel> dataArray, int k, int maxIterations) {
        return new KMeans().setDataSet(dataArray).setK(k).setMaxIterations(maxIterations).run();
    }

    /**
     * 执行算法
     */
    public KMeansModel run() {
        long startTime = System.currentTimeMillis();
        System.out.println("kmeans begins");
        KMeansModel model = kmeans();
        long endTime = System.currentTimeMillis();
        System.out.println("kmeans running time=" + (endTime - startTime)
                + "ms");
        System.out.println("kmeans ends");
        System.out.println();
        return model;
    }

    private double getAvarageDistance() {
        double sumDistance = 0;
        for(int i=0;i<dataSet.size();i++) {
            for(int j=i+1;j<dataSet.size();j++) {
                sumDistance += distance(dataSet.get(i).getData(),dataSet.get(j).getData());
            }
        }
        return 2*sumDistance/(dataSet.size()*dataSet.size()-1);
    }

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
    private List<double[]> initCenters(int d) {
        //double avarageDistance = getAvarageDistance();
        double averageRadius = getAvarageRadius();
        ArrayList<double[]> center = new ArrayList<double[]>();
        List<List<Double>> distances = new ArrayList();
        for(int i=0;i<dataSet.size();i++) {
            List distance = new ArrayList<Double>();
            for(int j=0;j<dataSet.size();j++) {
                if(i!=j){
                    distance.add(distance(dataSet.get(i).getData(),dataSet.get(j).getData()));
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
                    else
                        return -1;
                }
            });
        }



        List<Candidate> candidates = new ArrayList<Candidate>();
        for(int i=0;i<distances.size();i++) {
            List<Double> distance = distances.get(i);
            double radius = distance.get(d-1);
            if(radius<averageRadius){
                candidates.add(new Candidate(i,radius));
            }
        }

        if(candidates.size()<k) {
            return initCenters(d/2);
        }
        List<Set<Candidate>> sets = new ArrayList<>();
        for(int i=0;i<candidates.size();i++) {
            for(int j=i+1;j<candidates.size();j++) {
                if(distance(dataSet.get(i).getData(),dataSet.get(j).getData())<(candidates.get(i).radius+candidates.get(j).radius)){
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
                        Set<Candidate> temp = new HashSet<Candidate>();
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

        List<double[]> centers = calculateCenters(sets);


        //所得sets的大小小于k，则进行分裂操作，找到距离中心最远的候选点
        double max = 0;
        Candidate isolate = null;
        int setIndex=0;
        while(sets.size()<k){
            for(int i=0;i<sets.size();i++) {
                for(Candidate candidate:sets.get(i)) {
                    double temp = distance(centers.get(i),dataSet.get(candidate.index).getData());
                    if(temp>max) {
                        max = temp;
                        isolate = candidate;
                        setIndex = i;
                    }
                }
            }
            Set<Candidate> splitSet = sets.get(setIndex);
            splitSet.remove(isolate);
            Set<Candidate> newSet = new HashSet<Candidate>();
            newSet.add(isolate);
            for(Candidate candidate:splitSet) {
                if(distance(dataSet.get(isolate.index).getData(),dataSet.get(candidate.index).getData())<distance(dataSet.get(candidate.index).getData(),centers.get(setIndex))){
                    newSet.add(candidate);
                }
            }
            splitSet.removeAll(newSet);
            sets.add(newSet);
            centers = calculateCenters(sets);
        }
        return centers;
    }

    private List<double[]> calculateCenters(List<Set<Candidate>> sets) {
        List<double[]> centers = new ArrayList<>();
        for(int i=0;i<sets.size();i++) {
            double caudisSum = 0;
            for(Candidate candidate: sets.get(i)) {
                caudisSum+=candidate.radius;
            }
            double[] c = new double[dim];
            for(int j=0;j<dim;j++) {
                for(Candidate candidate: sets.get(i)) {
                    c[j]+=dataSet.get(candidate.index).getData()[j]*candidate.radius/caudisSum;
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
        int length = element.length;
        double[] x = new double[length];
        double distance = 0.0f;
        for(int i=0;i<length;i++) {
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
    private List<List<PointWithLabel>> clusterSet(List<double[]> centers,List<List<PointWithLabel>> clusters) {
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
        int length = element.length;
        double errSquare = 0;
        for(int i=0;i<length;i++) {
            errSquare = Math.pow(element[i]-center[i],2);
        }
        return errSquare;
    }

    /**
     * 计算误差平方和准则函数方法
     */
    private double countRule(List<List<PointWithLabel>> clusters,List<double[]> centers) {
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
        List<double[]> centers = initCenters(d);
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
     * @param
     *
     * @param dataArrayName
     *            数据集名称
     */
    public void printDataArray(List<PointWithLabel> pointWithLabels,
                               String dataArrayName) {
        int dim = dataSet.get(0).getData().length;
        for (int i = 0; i < pointWithLabels.size(); i++) {
            StringBuilder sb = new StringBuilder("print:"+ dataArrayName + "[" + i + "]={");
            sb.append("%s}\n");
            //dataArray.get(i)[0] + "," + dataArray.get(i)[1] + "}";
            System.out.format(sb.toString(),pointWithLabels.get(i).getLabel());
        }
        System.out.println("===================================");
    }


}
