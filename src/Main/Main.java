package Main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.*;

public class Main extends Application {

    private static XYChart.Series<String, Number> randomizedDivideConquerGraph;
    private static XYChart.Series<String, Number> medianOfMediansGraph;
    private static XYChart.Series<String, Number> naiveMethodGraph;
    private static int PARTITION_SIZE = 5;
    private static int AVERAGE_RUNS = 5;
    private static int MAX_ARRAY = 10000000;

    @Override
    public void start(Stage primaryStage) throws Exception{
        HBox hBox = new HBox();
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Size of Array");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time in Milliseconds");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setTitle("Time Performance of Algorithms");
        lineChart.setMinWidth(1700);
        lineChart.setStyle("-fx-font-size: " + 15 + "px;");

        lineChart.getData().add(randomizedDivideConquerGraph);
        lineChart.getData().add(medianOfMediansGraph);
        lineChart.getData().add(naiveMethodGraph);

        hBox.getChildren().add(lineChart);
        primaryStage.setTitle("Sorting");
        primaryStage.setScene(new Scene(hBox, 1700, 1000));
        primaryStage.show();
    }

    public static void main(String[] args) {

        randomizedDivideConquerGraph = new XYChart.Series<>();
        randomizedDivideConquerGraph.setName("Randomized Divide And Conquer");
        medianOfMediansGraph = new XYChart.Series<>();
        medianOfMediansGraph.setName("Median Of Medians");
        naiveMethodGraph = new XYChart.Series<>();
        naiveMethodGraph.setName("Naive Method");

//        Random rd = new Random(); // creating Random object

        for (int size = 10; size <=MAX_ARRAY; ) {
            ArrayList<Integer> arrList = new ArrayList<>(size);

            //Generate Array Sequentially
            for (int i = 0; i < size; i++) {
                arrList.add(i);
            }
            //Shuffle Array
            Collections.shuffle(arrList);

            //Elapsed Time
            long endTime;
            long startTime;
            long[] timeElapsed = new long[AVERAGE_RUNS];
            long timeElapsedSum = 0;
            long timeElapsedAverage;
            String methodName = "";

//            System.out.println(arrList);
            //Randomized Divide and Conquer
            for(int j=0; j<AVERAGE_RUNS; j++) {
                ArrayList<Integer> arrayUnsorted = (ArrayList<Integer>) arrList.clone();
                startTime = System.currentTimeMillis();
                randomSelect(arrayUnsorted, 0, arrayUnsorted.size() - 1, arrayUnsorted.size() / 2);
                endTime = System.currentTimeMillis();
                timeElapsed[j] = endTime - startTime;
                timeElapsedSum+=timeElapsed[j];
            }
            output( "Randomized Divide and Conquer", size, timeElapsedSum,1);
//            methodName = "Randomized Divide and Conquer";
//            timeElapsedAverage = timeElapsedSum/AVERAGE_RUNS;
//            System.out.println("Avergae execution time of " + size + " elements using " + methodName + " in milliseconds: " + timeElapsedAverage);
//            randomizedDivideConquerGraph.getData().add(new XYChart.Data<String, Number>(String.valueOf(size), timeElapsedAverage));
            timeElapsedSum = 0;

            //Median of Medians
            for(int i=0;i<AVERAGE_RUNS; i++) {
                ArrayList<Integer> arrayUnsorted=(ArrayList<Integer>)arrList.clone();
                startTime = System.currentTimeMillis();
                medianOfMedians(arrayUnsorted, size / 2);
                endTime = System.currentTimeMillis();
                timeElapsed[i] = endTime - startTime;
                timeElapsedSum+=timeElapsed[i];
            }
            output("Median Of Medians", size, timeElapsedSum,2);
//            methodName = "Median Of Medians";
//            timeElapsedAverage = timeElapsedSum/AVERAGE_RUNS;
//            System.out.println("Avergae execution time of " + size + " elements using " + methodName + " in milliseconds: " + timeElapsedAverage);
//            medianOfMediansGraph.getData().add(new XYChart.Data<String, Number>(String.valueOf(size), timeElapsedAverage));
            timeElapsedSum = 0;

            //Naive Method
            for(int j=0; j<AVERAGE_RUNS; j++) {
                ArrayList<Integer> arrayUnsorted = (ArrayList<Integer>) arrList.clone();
                startTime = System.currentTimeMillis();
                quickSort(arrayUnsorted, 0, arrayUnsorted.size()-1);
                endTime = System.currentTimeMillis();
                timeElapsed[j] = endTime - startTime;
                timeElapsedSum+=timeElapsed[j];
            }
            output("Naive Method", size, timeElapsedSum,3);
//            methodName = "Naive Method";
//            timeElapsedAverage = timeElapsedSum/AVERAGE_RUNS;
//            System.out.println("Avergae execution time of " + size + " elements using " + methodName + " in milliseconds: " + timeElapsedAverage);
//            naiveMethodGraph.getData().add(new XYChart.Data<String, Number>(String.valueOf(size), timeElapsedAverage));



                if (size >= 100000 && size < 1000000)
                size += 100000;
            else if (size >= 1000000)
                size += 2000000;
            else
                size *= 10;
        }

        launch(args);
    }

    public static void output(String methodName, int size, long timeElapsedSum,int method){
        long timeElapsedAverage = timeElapsedSum/AVERAGE_RUNS;
        System.out.println("Average execution time of " + size + " elements using " + methodName + " in milliseconds: " + timeElapsedAverage);
        if(method==1)
            randomizedDivideConquerGraph.getData().add(new XYChart.Data<String, Number>(String.valueOf(size), timeElapsedAverage));
        else if(method==2)
            medianOfMediansGraph.getData().add(new XYChart.Data<String, Number>(String.valueOf(size), timeElapsedAverage));
        else
            naiveMethodGraph.getData().add(new XYChart.Data<String, Number>(String.valueOf(size), timeElapsedAverage));


    }

    public static int medianOfMedians(ArrayList<Integer> arr, int ith){
        int size = arr.size();
        //Divide into sublists of Len 5
        int numPartitions = size/ PARTITION_SIZE;
        //Check if size divisible by partition size
        if(size % PARTITION_SIZE != 0){
            numPartitions++;
        }
//        System.out.println(arr);

        List<Integer>[] partition = new ArrayList[numPartitions];
        ArrayList<Integer> medians = new ArrayList<>(numPartitions);
        //Divide into Sublists
        for(int i=0; i<numPartitions; i++){
            int from = i* PARTITION_SIZE;
            int to;
            if((i* PARTITION_SIZE + PARTITION_SIZE) < size) {
                to = i * PARTITION_SIZE + PARTITION_SIZE;
            }
            else {
                to = size;
            }
            partition[i] = new ArrayList(arr.subList(from,to));
            Collections.sort(partition[i]);
            medians.add(partition[i].get(partition[i].size()/2));

//            System.out.println(partition[i]);
        }
//            if(partition[numPartitions-1] % partitionSize !=0)
//        System.out.println(medians);
        int pivot;
        //Get median of medians list
        if(medians.size() <= 5){
            Collections.sort(medians);
            pivot = medians.get(medians.size()/2);
        }
        else {
            pivot = medianOfMedians(medians, medians.size()/2);
        }
//        System.out.println(arr);
//        System.out.println(pivot);
//        System.out.println(pivot);
        //Partition
//        int kth = medianOfMediansPartition(arr, 0, arr.size()-1, pivot);
        ArrayList<Integer> low = new ArrayList<>();
        ArrayList<Integer> high = new ArrayList<>();
        //Divide List into two lists (list < pivot) && (list > pivot)
        for(int j=0; j<size; j++){
            if(arr.get(j) < pivot){
                low.add(arr.get(j));
            }
            else if(arr.get(j) > pivot){
                high.add(arr.get(j));
            }
        }
        //Kth equals index of last element in low sublist
        int kth = low.size();

        //Determine which part to ignore
        if (ith < kth){
            return medianOfMedians(low, ith);
        }
        else if (ith > kth){
            return medianOfMedians(high, ith-kth-1);
        }
        else{
//            System.out.println(pivot);
            return pivot;
        }
    }

    //ith starts from 1
    public static int randomSelect(ArrayList<Integer> arr, int low, int high, int ith)
    {
        if(low==high)
            return arr.get(low);
        int rank = randomPartition(arr, low, high);
        int kth = rank - low + 1;
//        System.out.println(kth+ low -1);

        if(ith == kth) {
            return arr.get(rank);
        }
        if(ith < kth)
            return randomSelect(arr, low, rank-1, ith);
        else
            return randomSelect(arr, rank+1, high, ith-kth);
    }

    public static int randomPartition(ArrayList<Integer> arr, int low, int high)
    {
        Random rd = new Random(); // creating Random object
        int randomNumber = rd.nextInt(arr.size());
        swap(arr, randomNumber, high);
        int pivot = arr.get(high);

        int i = (low - 1);

        for(int j = low; j <= high - 1; j++)
        {
            if (arr.get(j) < pivot)
            {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return (i + 1);
    }

    public static void swap(ArrayList<Integer> arr, int i, int j)
    {
        int temp = arr.get(i);
//        arr[i] = arr[j];
        arr.set(i,arr.get(j));
//        arr[j] = temp;
        arr.set(j,temp);
    }

    public static int partition(ArrayList<Integer> arr, int low, int high)
    {
        int pivot = arr.get(high);
        int i = (low - 1);

        for(int j = low; j <= high - 1; j++)
        {

            if (arr.get(j) < pivot)
            {

                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return (i + 1);
    }

    public static void quickSort(ArrayList<Integer> arr, int low, int high)
    {
        if (low < high)
        {
            int pi = partition(arr, low, high);

            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }
}

