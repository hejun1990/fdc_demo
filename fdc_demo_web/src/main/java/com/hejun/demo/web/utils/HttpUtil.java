package com.hejun.demo.web.utils;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/4/10.
 */
public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static CloseableHttpClient httpclient = null;

    private final static Object syncLock = new Object();

    public static CloseableHttpClient createHttpClient() {
        try {
            LayeredConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            cm.setMaxTotal(200);
            cm.setDefaultMaxPerRoute(20);

            HttpRequestRetryHandler rh = new HttpRequestRetryHandler() {
                @Override
                public boolean retryRequest(IOException e, int executionCount, HttpContext httpContext) {
                    if (executionCount >= 1) {// 如果已经重试了1次，就放弃
                        return false;
                    }
                    if (e instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                        return true;
                    }
                    if (e instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                        return false;
                    }
                    if (e instanceof InterruptedIOException) {// 超时
                        return false;
                    }
                    if (e instanceof UnknownHostException) {// 目标服务器不可达
                        return false;
                    }
                    if (e instanceof ConnectTimeoutException) {// 连接被拒绝
                        return false;
                    }
                    if (e instanceof SSLException) {// SSL握手异常
                        return false;
                    }

                    HttpClientContext clientContext = HttpClientContext
                            .adapt(httpContext);
                    HttpRequest request = clientContext.getRequest();
                    // 如果请求是幂等的，就再次尝试
                    if (!(request instanceof HttpEntityEnclosingRequest)) {
                        return true;
                    }
                    return false;
                }
            };
            CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).setRetryHandler(rh).build();
            return httpclient;
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static CloseableHttpClient getHttpClient() {
        if (httpclient == null) {
            synchronized (syncLock) {
                if (httpclient == null) {
                    httpclient = createHttpClient();
                }
            }
        }
        return httpclient;
    }

    public static String httpClientGet(String url) {
        return httpClientGet(url, null, "UTF-8");
    }

    public static String httpClientGet(String url, String charset) {
        return httpClientGet(url, null, charset);
    }

    public static String httpClientGet(String url, Map<String, Object> params, String charset) {
        // 请求构建参数
        StringBuffer getParams = new StringBuffer();
        if (params != null && !params.isEmpty()) {
            try {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof String[]) {
                        String[] arr = (String[]) value;
                        for (String val : arr) {
                            getParams.append(key);
                            getParams.append("=");
                            getParams.append(URLEncoder.encode(val, "UTF-8"));
                            getParams.append("&");
                        }
                    } else {
                        getParams.append(key);
                        getParams.append("=");
                        getParams.append(URLEncoder.encode(String.valueOf(value), "UTF-8"));
                        getParams.append("&");
                    }
                }
            } catch (UnsupportedEncodingException e) {
                logger.error("转义HTTP请求参数报错:{}", e.getMessage());
            }
        }
        if (getParams != null && getParams.length() > 0) {
            url += url.contains("?") ? "&" : "?" + getParams.substring(0, getParams.length() - 1);
        }

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.addHeader("Accept-Encoding", "gzip,deflate,sdch");
        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        String respContent = "";
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            response = getHttpClient().execute(httpGet);
            // 读取服务器响应数据
            entity = response.getEntity();
            // 获取内容编码类型
            Header contentType = entity.getContentType();
            if (contentType != null) {
                HeaderElement[] elements = contentType.getElements();
                for (HeaderElement element : elements) {
                    NameValuePair[] nameValuePairs = element.getParameters();
                    for (NameValuePair nameValuePair : nameValuePairs) {
                        if ("charset".equalsIgnoreCase(nameValuePair.getName())) {
                            charset = nameValuePair.getValue();
                            break;
                        }
                    }
                }
            }
            // 判断内容是否进行过压缩，如果是则进行解压
            Header contentEncoding = entity.getContentEncoding();
            if (contentEncoding != null) {
                HeaderElement[] elements = contentEncoding.getElements();
                for (int i = 0; i < elements.length; i++) {
                    if (elements[i].getName().equalsIgnoreCase("gzip")) {
                        response.setEntity(new GzipDecompressingEntity(entity));
                        break;
                    }
                }
            }
            respContent = EntityUtils.toString(response.getEntity(), charset);
            return respContent;
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (entity != null) {
                    EntityUtils.consume(entity);
                }
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return null;
    }
}
