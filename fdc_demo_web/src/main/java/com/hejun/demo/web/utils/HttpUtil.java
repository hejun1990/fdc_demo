package com.hejun.demo.web.utils;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/4/10.
 */
public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String httpClientGet(String urlParam) {
        return httpClientGet(urlParam, null, "UTF-8");
    }

    public static String httpClientGet(String urlParam, Map<String, Object> params) {
        return httpClientGet(urlParam, params, "UTF-8");
    }

    public static String httpClientGet(String urlParam, String charset) {
        return httpClientGet(urlParam, null, charset);
    }

    public static String httpClientGet(String urlParam, Map<String, Object> params, String charset) {
        // 构建请求参数
        StringBuffer sbParams = new StringBuffer();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                sbParams.append(entry.getKey());
                sbParams.append("=");
                try {
                    sbParams.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                sbParams.append("&");
            }
        }
        if (sbParams != null && sbParams.length() > 0) {
            urlParam = urlParam + "?" + sbParams.substring(0, sbParams.length() - 1);
        }

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(urlParam);
        String respContent = "";
        try {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            // 读取服务器响应数据
            respContent = EntityUtils.toString(response.getEntity(), charset);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return respContent;
    }

    public static String httpClientPost(String urlParam, Map<String, Object> params) {
        return httpClientPost(urlParam, params, "UTF-8");
    }

    public static String httpClientPost(String urlParam, Map<String, Object> params, String charset) {
        String respContent = "";
        try {
            HttpPost httpPost = new HttpPost(urlParam);
            // 构建请求参数
            List<NameValuePair> postParams = new ArrayList<>();
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        String val = (String) value;
                        postParams.add(new BasicNameValuePair(entry.getKey(), val));
                    } else if (value instanceof Integer) {
                        String val = String.valueOf((int) value);
                        postParams.add(new BasicNameValuePair(entry.getKey(), val));
                    } else if (value instanceof String[]) {
                        String[] arr = (String[]) value;
                        for (String val : arr) {
                            postParams.add(new BasicNameValuePair(entry.getKey(), val));
                        }
                    }

                }
            }

            if (postParams.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams, charset);
                httpPost.setEntity(entity);
            }
            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response = httpclient.execute(httpPost);
            // 读取服务器响应数据
            respContent = EntityUtils.toString(response.getEntity(), charset);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return respContent;
    }
}
