package com.github.tomakehurst.okhttpjersey1;

import com.squareup.okhttp.OkHttpClient;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.TerminatingClientHandler;
import com.sun.jersey.api.client.config.ClientConfig;
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
            Object chunkedEncodingSize = cr.getProperties().get(ClientConfig.PROPERTY_CHUNKED_ENCODING_SIZE);
            if (chunkedEncodingSize != null) {
                connection.setChunkedStreamingMode((int) chunkedEncodingSize);
            }

            connection.setRequestMethod(cr.getMethod());
            addRequestHeaders(cr, connection);
            addRequestBodyIfPresent(cr, connection);

            in = connection.getInputStream();
            return new ClientResponse(connection.getResponseCode(), responseHeadersFrom(connection), in, getMessageBodyWorkers());
        } catch (IOException ioe) {
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

    private void addRequestBodyIfPresent(ClientRequest cr, HttpURLConnection connection) throws IOException {
        if (cr.getEntity() != null) {
            RequestEntityWriter requestEntityWriter = getRequestEntityWriter(cr);
            requestEntityWriter.writeRequestEntity(connection.getOutputStream());
        }
    }

    private void addRequestHeaders(ClientRequest cr, HttpURLConnection connection) {
        for (Map.Entry<String, List<Object>> header: cr.getHeaders().entrySet()) {
            for (Object value: header.getValue()) {
                connection.addRequestProperty(header.getKey(), (String) value);
            }
        }
    }

    private InBoundHeaders responseHeadersFrom(final HttpURLConnection connection) {
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
