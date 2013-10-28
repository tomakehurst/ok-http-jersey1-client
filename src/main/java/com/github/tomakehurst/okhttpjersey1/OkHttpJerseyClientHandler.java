package com.github.tomakehurst.okhttpjersey1;

import com.squareup.okhttp.OkHttpClient;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.TerminatingClientHandler;
import com.sun.jersey.core.header.InBoundHeaders;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            return new ClientResponse(connection.getResponseCode(), getInBoundHeaders(connection), in, getMessageBodyWorkers());
        } catch (Exception ioe) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new ClientHandlerException(e);
                }
            }
            throw new ClientHandlerException(ioe);
        }
    }

    private InBoundHeaders getInBoundHeaders(final HttpURLConnection connection) {
        final InBoundHeaders headers = new InBoundHeaders();

        for (Map.Entry<String, List<String>> header: connection.getHeaderFields().entrySet()) {
            List<String> list = headers.get(header.getKey());
            if (list == null) {
                list = new ArrayList<String>();
            }

            list.addAll(header.getValue());
            headers.put(header.getKey(), list);
        }
        return headers;
    }
}
