package clientPart2;

import model.ImageMetaData;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import util.Callback;
import util.HttpUtil;
import util.JsonUtil;
import util.ThroughputUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class AppMain {

  protected static final AtomicInteger SUCCESS = new AtomicInteger(0);

  // Variables to record latencies
  private static final Vector<Long> postLatencies = new Vector<>();
  private static final Vector<Long> getLatencies = new Vector<>();

  // Plot Through put Hashmap
  public static final ConcurrentHashMap<Integer, AtomicInteger> throughputData = new ConcurrentHashMap<>();
  public static final long GLOBAL_START_TIME = System.currentTimeMillis();

  private static final Random random = new Random();

  public static void main(String[] args) throws InterruptedException {
    if(args.length != 4){
      return;
    }
    int threadGroupSize = Integer.parseInt(args[0]);
    int numThreadGroups = Integer.parseInt(args[1]);
    long delay = Long.parseLong(args[2]);
    String ipAddr = args[3];
    ExecutorService executorService = Executors.newFixedThreadPool(threadGroupSize * numThreadGroups);
    int total = threadGroupSize * numThreadGroups * 100 * 2 + 10 * 10 * 2;
    CountDownLatch baseLatch = new CountDownLatch(10);
    for (int i = 0; i < 10; i++) {
      executorService.execute(() -> {
        request(10, ipAddr, baseLatch);
      });
    }
    // Wait for initialization threads to finish
    baseLatch.await();
    CountDownLatch latch = new CountDownLatch(threadGroupSize * numThreadGroups + 3);
    long startTime = System.currentTimeMillis();
    // Execute
    for (int i = 0; i < numThreadGroups; i++) {
      for (int j = 0; j < threadGroupSize; j++) {
        executorService.execute(() -> request(100, ipAddr, latch));
      }
      Thread.sleep(delay * 1000);
    }

    for(int i = 0; i < 3; i++){
      request(ipAddr, latch);
    }

    latch.await();

    executorService.shutdown();

    long endTime = System.currentTimeMillis();
    double wallTime = (endTime - startTime) * 0.001;

    int success = SUCCESS.get();
    int fail = total - success;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    String wallTimeStr = decimalFormat.format(wallTime);
    String throughput = decimalFormat.format(success / (wallTime));
    System.out.println("----------------Request Result----------------");
    String server = ipAddr.contains("8080") ? "Java" : "Go";
    System.out.println(server + " Servlet:\n" + "Thread Group Size: " + threadGroupSize + " Num Thread Group: " + numThreadGroups + " Delay: " + delay);
    System.out.println("Wall Time: " + wallTimeStr + " s");
    System.out.println("Throughput: " + throughput+ " /sec");
    System.out.println("Successful Requests: " + success + " requests," + "Failed Requests: " + fail + " requests");
    stats();

  }

  public static void stats() {
    System.out.println("post requests statistics:");
    ThroughputUtil.calStats(postLatencies);

    System.out.println("get requests statistics:");
    ThroughputUtil.calStats(getLatencies);
  }


  public static void request(String ipAddr, CountDownLatch latch){
    Callback get = (res, start, end) -> {
      if(res.getCode() == 200 || res.getCode() == 201){
        getLatencies.add(end - start);
        writeToCSV("GET", end - start, res.getCode());
      }
    };
    try (CloseableHttpResponse response = HttpUtil.get(ipAddr + "/review/" + random.nextInt(10), get)){
      if (response.getCode() == 200){
        recordThroughput();
//          System.out.println("GET:" + EntityUtils.toString(response.getEntity()));
        AppMain.SUCCESS.incrementAndGet();
      }
    } catch (Exception ignored) {

    }
    latch.countDown();
  }

    /**
     * request
   * @param num
     * @param ipAddr
     * @param latch
   */
  public static void request(int num, String ipAddr, CountDownLatch latch){
    Map<String, Object> body = new HashMap<>();
    Callback post = (res, start, end) -> {
      if(res.getCode() == 200 || res.getCode() == 201){
        postLatencies.add(end - start);
        writeToCSV("POST", end - start, res.getCode());
      }
    };
    for (int i = 0; i < num; i++) {
      long albumId = -1;
      try (CloseableHttpResponse response = HttpUtil.post(ipAddr + "/album/", body, post)){
        if (response.getCode() == 200){
          String result = EntityUtils.toString(response.getEntity());
          ImageMetaData imageMetaData = JsonUtil.toEntity(result, ImageMetaData.class);
          if(imageMetaData != null){
            albumId = imageMetaData.getAlbumId();
          }
          recordThroughput();
//          System.out.println("POST:" + EntityUtils.toString(response.getEntity()));
          AppMain.SUCCESS.incrementAndGet();
        }
      } catch (Exception ignored) {

      }
      if(albumId > 0){
        try (CloseableHttpResponse response = HttpUtil.post(ipAddr + "/review/" + albumId + "/like", body, post)){
          if (response.getCode() == 200){
            recordThroughput();
//          System.out.println("GET:" + EntityUtils.toString(response.getEntity()));
            AppMain.SUCCESS.incrementAndGet();
          }
        } catch (Exception ignored) {

        }
        try (CloseableHttpResponse response = HttpUtil.post(ipAddr + "/review/" + albumId + "/like", body, post)){
          if (response.getCode() == 200){
            recordThroughput();
//          System.out.println("GET:" + EntityUtils.toString(response.getEntity()));
            AppMain.SUCCESS.incrementAndGet();
          }
        } catch (Exception ignored) {

        }
        try (CloseableHttpResponse response = HttpUtil.post(ipAddr + "/review/" + albumId + "/dislike", body, post)){
          if (response.getCode() == 200){
            recordThroughput();
//          System.out.println("GET:" + EntityUtils.toString(response.getEntity()));
            AppMain.SUCCESS.incrementAndGet();
          }
        } catch (Exception ignored) {

        }
      }
    }
    latch.countDown();
  }

  private static void writeToCSV(String requestType, long latency, int responseCode) {
    String filePath = "requests.csv";
    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
      if (!Files.exists(Paths.get(filePath)) || Files.size(Paths.get(filePath)) == 0) {
        writer.println("Request Type, Latency, Response Code");
      }
      writer.println(requestType + "," + latency + "," + responseCode);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void recordThroughput() {
     long now = System.currentTimeMillis();
     int second = (int) ((now - GLOBAL_START_TIME) / 1000);
     throughputData.computeIfAbsent(second, k -> new AtomicInteger()).incrementAndGet();
  }
}
