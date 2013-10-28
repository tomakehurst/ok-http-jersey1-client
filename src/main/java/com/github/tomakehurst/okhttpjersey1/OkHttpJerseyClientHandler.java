package com.github.tomakehurst.okhttpjersey1;

import com.squareup.okhttp.OkHttpClient;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.TerminatingClientHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OkHttpJerseyClientHandler extends TerminatingClientHandler {

    private OkHttpClient client;

    public OkHttpJerseyClientHandler() {
        this(new OkHttpClient());
    }

    public OkHttpJerseyClientHandler(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        InputStream in = null;
        try {
            URL url = cr.getURI().toURL();
            HttpURLConnection connection = client.open(url);
            in = connection.getInputStream();
            return new ClientResponse(connection.getResponseCode(), null, in, null);
        } catch (IOException ioe) {
            throw new ClientHandlerException(ioe);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new ClientHandlerException(e);
                }
            }
        }
    }
}
