package clientPart1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import util.HttpUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.text.DecimalFormat;

/**
 *
 */
public class AppMain {

  protected static final AtomicInteger SUCCESS = new AtomicInteger(0);

  public static void main(String[] args) throws InterruptedException {
    if(args.length != 4){
      return;
    }
    int threadGroupSize = Integer.parseInt(args[0]);
    int numThreadGroups = Integer.parseInt(args[1]);
    long delay = Long.parseLong(args[2]);
    String ipAddr = args[3];
    ExecutorService executorService = Executors.newFixedThreadPool(threadGroupSize * numThreadGroups);
    int total = threadGroupSize * numThreadGroups * 1000 * 2 + 10 * 10 * 2;
    CountDownLatch baseLatch = new CountDownLatch(10);
    for (int i = 0; i < 10; i++) {
      executorService.execute(() -> {
        request(10, ipAddr, baseLatch);
      });
    }
    // Wait for initialization threads to finish
    baseLatch.await();
    CountDownLatch latch = new CountDownLatch(threadGroupSize * numThreadGroups);
    long startTime = System.currentTimeMillis();
    // Execute
    for (int i = 0; i < numThreadGroups; i++) {
      for (int j = 0; j < threadGroupSize; j++) {
        executorService.execute(() -> request(1000, ipAddr, latch));
      }
      Thread.sleep(delay * 1000);
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
  }

  /**
   * request
   * @param num
   * @param ipAddr
   * @param latch
   */
  public static void request(int num, String ipAddr, CountDownLatch latch){
    Map<String, Object> body = new HashMap<>();
    for (int i = 0; i < num; i++) {
      try (CloseableHttpResponse response = HttpUtil.get(ipAddr + "/1")){
        if (response.getCode() == 200){
          System.out.println("GET:" + EntityUtils.toString(response.getEntity()));
          AppMain.SUCCESS.incrementAndGet();
        }
      } catch (Exception ignored) {

      }
      try (CloseableHttpResponse response = HttpUtil.post(ipAddr + "/", body)){
        if (response.getCode() == 200){
          System.out.println("POST:" + EntityUtils.toString(response.getEntity()));
          AppMain.SUCCESS.incrementAndGet();
        }
      } catch (Exception ignored) {

      }
    }
    latch.countDown();
  }
}
