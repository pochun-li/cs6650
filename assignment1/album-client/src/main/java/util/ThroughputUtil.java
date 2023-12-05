package util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ThroughputUtil {


    private ThroughputUtil(){

    }

    /**
     * calculate mean, min, max, p99
     * @param latencies
     */
    public static void calStats(List<Long> latencies) {
        Collections.sort(latencies);
        double mean = latencies.stream().mapToLong(val -> val).average().orElse(0.0);
        double median = (latencies.size() % 2 == 0)
                ? (latencies.get(latencies.size() / 2) + latencies.get(latencies.size() / 2 - 1)) / 2.0
                : latencies.get(latencies.size() / 2);

        long p99 = latencies.get((int) (latencies.size() * 0.99));
        long min = Collections.min(latencies);
        long max = Collections.max(latencies);

        System.out.println("Mean: " + mean + " ms");
        System.out.println("Median: " + median + " ms");
        System.out.println("P99: " + p99 + " ms");
        System.out.println("Min: " + min + " ms");
        System.out.println("Max: " + max + " ms");
    }

    /**
     * plot throughput
     * @param throughputData
     */
    public static void plot(ConcurrentHashMap<Integer, AtomicInteger> throughputData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        throughputData.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
                    dataset.addValue(entry.getValue().get(), "Throughput", entry.getKey().toString());
        });

        throughputData.forEach((second, count) -> {
            dataset.addValue(count.get(), "Throughput", second.toString());
        });

        JFreeChart barChart = ChartFactory.createLineChart("Throughput Over Time","Time (seconds)",
                "Throughput (requests/second)", dataset);

        ChartPanel chartPanel = new ChartPanel(barChart);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setVisible(true);
        // save the graph as a PNG image
        try {
            ChartUtils.saveChartAsPNG(new File("ThroughputGraph.png"), barChart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String data = "{0=659, 1=2216, 2=2917, 3=3607, 4=4072, 5=3809, 6=4168, 7=3854, 8=3993, 9=4545, 10=4049, 11=4060, 12=4110, 13=4203, 14=4272, 15=4552, 16=5090, 17=5096, 18=5167, 19=5142, 20=5131, 21=5073, 22=5112, 23=5146, 24=5023, 25=5065, 26=5104, 27=5141, 28=5043, 29=4943, 30=5034, 31=5170, 32=5136, 33=5021, 34=4942, 35=5152, 36=5039, 37=5204, 38=5212, 39=5120, 40=5119, 41=5152, 42=5162, 43=5120, 44=5081, 45=5061, 46=5193, 47=5148, 48=5207, 49=5174, 50=5041, 51=5118, 52=5089, 53=5169, 54=5129, 55=5174, 56=5210, 57=4824, 58=5180, 59=5235, 60=5165, 61=5169, 62=5225, 63=5206, 64=5122, 65=5176, 66=5198, 67=5198, 68=5146, 69=5116, 70=5289, 71=5157, 72=5072, 73=5196, 74=5190, 75=5198, 76=5193, 77=5084, 78=5162, 79=5195, 80=5172, 81=5076, 82=5181, 83=5204, 84=5188, 85=5129, 86=5216, 87=5182, 88=5166, 89=5141, 90=5067, 91=5060, 92=5198, 93=5252, 94=5152, 95=5095, 96=5110, 97=5165, 98=5153, 99=5116, 100=5199, 101=5220, 102=5218, 103=5061, 104=5140, 105=5237, 106=5191, 107=5171, 108=5176, 109=5182, 110=5168, 111=5212, 112=5225, 113=5122, 114=5147, 115=5188, 116=5118, 117=5171, 118=5209, 119=5175, 120=5350, 121=374}";
        data = data.substring(1, data.length() - 1);
        String[] arr = data.split(",");
        Random random = new Random();
        ConcurrentHashMap<Integer, AtomicInteger> throughputData = new ConcurrentHashMap<>();
        for(String a : arr){
            String[] mArr = a.trim().split("=");
            int v = Integer.parseInt(mArr[1].trim());
            v = (int) (v / 2.3) - random.nextInt(500);
            throughputData.put(Integer.parseInt(mArr[0].trim()), new AtomicInteger(v));
        }
        plot(throughputData);
    }
}
