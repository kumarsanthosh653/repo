/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author rajesh
 */
public class ScreenPopUtil {

    public static void requestClient(String uri, String user, String pass) {
        try {
            URI url = new URI(uri);
            HttpGet httpget = new HttpGet(url);// Execute HTTP Get Request
            DefaultHttpClient httpClient = new DefaultHttpClient();
            Credentials credentials = new UsernamePasswordCredentials(user, pass);
            Header headers[] = httpget.getAllHeaders();
            httpClient.getCredentialsProvider().setCredentials(new AuthScope(url.getHost(), AuthScope.ANY_PORT),
                    credentials);

            headers = httpget.getAllHeaders();
            System.out.println("REQUEST Hearders>>>>>>>>>>>>");
            for (Header h : headers) {
                System.out.println(h.getName() + ": " + h.getValue());
            }
            HttpResponse response = httpClient.execute(httpget);
            System.out.println("RESPONSE Hearders<<<<<<<<<<<<");
            headers = response.getAllHeaders();
            for (Header h : headers) {
                System.out.println(h.getName() + ": " + h.getValue());
            }
            System.out.println("RESPONSE:" + response.getStatusLine() + "| uri=" + uri);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void doBasicAuthRequest(final String url, final String formparms) {
        try {
            URL urlOb = new URL(url+"?"+formparms);
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    new AuthScope(urlOb.getHost(), AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(urlOb.getUserInfo()));
            HttpGet httpget = new HttpGet(urlOb.toURI());
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpClient.setCredentialsProvider(credsProvider);
            HttpResponse response = httpClient.execute(httpget);
            System.out.println("RESPONSE:" + response.getStatusLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String doGetRequestAsThread(final String url, final String formparams) throws IOException {

        try {
            URI uri = new URI("http://localhost:8080/ScreenPop/SendMessage?" + formparams);
            HttpGet httpget = new HttpGet(uri);// Execute HTTP Get Request
            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpResponse response = httpClient.execute(httpget);
            System.out.println("RESPONSE:" + response.getStatusLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "[11111]Sent to Local Host SEND MESSAGE >>>>>>>>>>>..";

    }
}
