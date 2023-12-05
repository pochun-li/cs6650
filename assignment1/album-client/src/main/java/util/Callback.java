package util;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;

@FunctionalInterface
public interface Callback {
    void action(CloseableHttpResponse res, long start, long time);
}