package util;

import clientPart1.AppMain;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtil {

    private HttpUtil(){

    }

    /**
     * get request
     * @param url
     * @return
     * @throws Exception
     */
    public static CloseableHttpResponse get(String url) throws Exception{
        HttpGet httpGet = new HttpGet(url);
        return request(httpGet, null);
    }

    /**
     * get request
     * @param url
     * @param callback
     * @return
     * @throws Exception
     */
    public static CloseableHttpResponse get(String url, Callback callback) throws Exception{
        HttpGet httpGet = new HttpGet(url);
        return request(httpGet, callback);
    }


    /**
     * get request
     * @param url
     * @return
     * @throws Exception
     */
    public static CloseableHttpResponse post(String url, Map<String, Object> body) throws Exception{
        HttpPost httpPost = new HttpPost(url);
        HttpEntity httpEntity = new StringEntity(JsonUtil.toJson(body));
        httpPost.setEntity(httpEntity);
        return request(httpPost, null);
    }

    /**
     * get request
     * @param url
     * @param callback
     * @return
     * @throws Exception
     */
    public static CloseableHttpResponse post(String url, Map<String, Object> body, Callback callback) throws Exception{
        HttpPost httpPost = new HttpPost(url);
        HttpEntity httpEntity = new StringEntity(JsonUtil.toJson(body));
        httpPost.setEntity(httpEntity);
        return request(httpPost, callback);
    }

    /**
     * send request
     * @param requestBase
     * @param callback
     * @return
     * @throws Exception
     */
    public static CloseableHttpResponse request(HttpUriRequestBase requestBase, Callback callback) throws Exception{
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.of(3000, TimeUnit.MILLISECONDS))
                .setResponseTimeout(3000, TimeUnit.MILLISECONDS)
                .setConnectionRequestTimeout(3000, TimeUnit.MILLISECONDS)
                .build();
        try {
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultHeaders(Collections.emptyList())
                    .setDefaultRequestConfig(requestConfig)
                    .build();
            long startTime = System.currentTimeMillis();
            CloseableHttpResponse res = httpClient.execute(requestBase);
            long endTime = System.currentTimeMillis();
            if(callback != null){
                callback.action(res, startTime, endTime);
            }
            return res;
        } finally {

        }
    }

    public static void main(String[] args) {
        try (CloseableHttpResponse response = HttpUtil.get("http://localhost:8080/album/1")){
            if (response.getCode() == 200){
                System.out.println(response.toString());
                System.out.println("GET:" + EntityUtils.toString(response.getEntity()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
