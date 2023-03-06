package com.ozonetel.occ.util;

import com.ozonetel.occ.model.HttpResponseDetails;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

/**
 *
 * @author pavanj
 * @mail pavan@ozonetel.com
 */
public class HttpUtils {

    public String doPostRequest(String url, List<NameValuePair> formparams) throws IOException {

        HttpClient client = new DefaultHttpClient();
        HttpPost method = new HttpPost(url);
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
            method.setEntity(entity);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return client.execute(method, responseHandler);
        } finally {
            method.releaseConnection();
        }

    }

    public String doPostRequestAsThread(final String url, final List<NameValuePair> formparams) throws IOException {

        new Thread(new Runnable() {

            public void run() {
                try {
                    org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
                    PostMethod method = new PostMethod(url);
                    InputStream in = null;
                    logger.debug("Named ValuePairs=" + formparams);
                    //Add any parameter if u want to send it with Post req.
                    for (NameValuePair nameValuePair : formparams) {
                        if (nameValuePair != null && nameValuePair.getName() != null && nameValuePair.getValue() != null && !nameValuePair.getName().isEmpty() && !nameValuePair.getValue().isEmpty());
                        method.addParameter("" + nameValuePair.getName(), "" + nameValuePair.getValue());
                    }
                    int statusCode = client.executeMethod(method);

                    if (statusCode != -1) {
                        in = method.getResponseBodyAsStream();
                    }

                    logger.debug("Status Code=" + statusCode);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
        return "Screen Pop Hitted at server side";

    }

    public String doGetRequestAsThread(final String url, final String formparams) throws IOException {

        new Thread(new Runnable() {

            public void run() {
                try {
                    org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
                    HttpClientParams clientParams = client.getParams();
                    clientParams.setConnectionManagerTimeout(15000);//15 sec
                    clientParams.setSoTimeout(15000);//15 sec
                    client.setParams(clientParams);
                    GetMethod method = new GetMethod(url);
                    InputStream in = null;
                    logger.debug("URL =" + url);
                    logger.debug("query String {" + formparams + "}");
                    //Add any parameter if u want to send it with Post req.
                    method.setQueryString(formparams);
                    int statusCode = client.executeMethod(method);

                    if (statusCode != -1) {
                        in = method.getResponseBodyAsStream();
                    }

                    logger.debug(url + " | query String {" + formparams + "} Status Code=" + statusCode);

                } catch (Exception e) {
                    logger.error("[" + e.getMessage() + "]:URL =" + url + "{" + formparams + "}");
                    e.printStackTrace();
                }

            }
        }).start();
        return "Screen Pop Hitted at server side";

    }

    public String sendHttpRequestAsThread(final String url, final String formparams, final String urlmethod) throws IOException {

        new Thread(new Runnable() {

            public void run() {
                try {
                    org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
                    HttpClientParams clientParams = client.getParams();
                    clientParams.setConnectionManagerTimeout(15000);//15 sec
                    clientParams.setSoTimeout(15000);//15 sec
                    client.setParams(clientParams);
                    HttpMethod method = new GetMethod(url);
                    if (urlmethod.equalsIgnoreCase("1")) {// 1 is for POST , 0 is for GET
                        method = new PostMethod(url);
                    }
                    //Add any parameter if u want to send it with POST/GET req.
                    method.setQueryString(formparams);
                    int statusCode = client.executeMethod(method);
                    if (statusCode == 200) {
                        logger.debug("Success[" + statusCode + "]:URL =" + url + " " + method.getQueryString());
                    } else {
                        logger.error("Error[" + statusCode + "] :URL =" + url + " " + method.getQueryString());
                    }

                } catch (Exception e) {
                    logger.error("[" + e.getMessage() + "]:URL =" + url + "{" + formparams + "}");
                    e.printStackTrace();
                }

            }
        }).start();
        return "Screen Pop Hitted at server side";

    }

    /**
     * Does GET http request.
     *
     * @param requestUrl url
     * @return {@link com.ozonetel.occ.model.HttpResponseDetails} object
     * @throws IOException
     */
    public static HttpResponseDetails doGet(String requestUrl) throws IOException {
        HttpGet get = new HttpGet(requestUrl);
        HttpClient client = new DefaultHttpClient();
        HttpResponse httpResponse = client.execute(get);
        try {
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String body = responseHandler.handleResponse(httpResponse);
            return new HttpResponseDetails(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase(), body);
        } finally {
            get.releaseConnection();
        }

    }

    /**
     * Does GET http request.
     *
     * @param requestUrl url
     * @param timeoutInMs
     * @return {@link com.ozonetel.occ.model.HttpResponseDetails} object
     * @throws IOException
     */
    public static HttpResponseDetails doGet(String requestUrl, int timeoutInMs) throws IOException {
        HttpGet get = new HttpGet(requestUrl);

        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, timeoutInMs);
        HttpConnectionParams.setSoTimeout(httpParams, timeoutInMs);
        HttpClient client = new DefaultHttpClient(httpParams);
        long before = System.currentTimeMillis();
        HttpResponse httpResponse = client.execute(get);
        logger.debug("Request: [" + requestUrl + "] |took:" + (System.currentTimeMillis() - before));
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String body = responseHandler.handleResponse(httpResponse);
        return new HttpResponseDetails(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase(), body);
    }

    public static HttpResponseDetails sendHttpPOSTRequest(String url, String data) throws Exception {

        HttpResponseDetails httpResponse = new HttpResponseDetails();

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(new KeyManager[0], new TrustManager[]{new HttpUtils.DefaultTrustManager()}, new SecureRandom());
        SSLContext.setDefault(ctx);
        String agent = "Mozilla/6.0";
        URL urlOb = new URL(url);
        URLConnection connection = urlOb.openConnection();
        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("User-Agent", agent);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + "UTF-8");
        if (connection instanceof HttpsURLConnection) {
            //
            // -----> have to follow redirections other wise customer won't get the data
//            ((HttpsURLConnection) connection).setInstanceFollowRedirects(false);
            ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {

                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });

        } else {
//            ((HttpURLConnection) connection).setInstanceFollowRedirects(false);
        }

        if (data != null) {
            OutputStream output = null;
            try {
                output = connection.getOutputStream();
                output.write(data.getBytes("UTF-8"));
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException logOrIgnore) {
                    }
                }
            }
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));
        String inputLine = "";
        String response = "";

        while ((inputLine = in.readLine()) != null) {
            response += inputLine;
        }
        try {
            in.close();
        } catch (Exception ignore) {
        }
        if (connection instanceof HttpsURLConnection) {

            httpResponse.setStatusCode(((HttpsURLConnection) connection).getResponseCode());
            httpResponse.setReasonPhrase(((HttpsURLConnection) connection).getResponseMessage());

            try {
                ((HttpsURLConnection) connection).disconnect();
            } catch (Exception ignore) {
            }
        } else if (connection instanceof HttpURLConnection) {
            httpResponse.setStatusCode(((HttpURLConnection) connection).getResponseCode());
            httpResponse.setReasonPhrase(((HttpURLConnection) connection).getResponseMessage());
            try {
                ((HttpURLConnection) connection).disconnect();
            } catch (Exception ignore) {
            }
        }

        httpResponse.setResponseBody(response);

        return httpResponse;
    }

    private static class DefaultTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
    private static Logger logger = Logger.getLogger(HttpUtils.class);
}
