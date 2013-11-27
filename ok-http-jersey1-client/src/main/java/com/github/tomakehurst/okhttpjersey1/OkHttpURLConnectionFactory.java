package com.github.tomakehurst.okhttpjersey1;

import com.squareup.okhttp.OkHttpClient;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class OkHttpURLConnectionFactory implements HttpURLConnectionFactory {

    private final OkHttpClient okHttpClient;

    public OkHttpURLConnectionFactory() {
        this(new OkHttpClient());
    }

    public OkHttpURLConnectionFactory(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        return okHttpClient.open(url);
    }
}
