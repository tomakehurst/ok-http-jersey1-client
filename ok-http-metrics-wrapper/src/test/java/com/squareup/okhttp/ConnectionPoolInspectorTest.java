package com.squareup.okhttp;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.Exceptions.throwUnchecked;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConnectionPoolInspectorTest {

    OkHttpClient client;
    ConnectionPool connectionPool;
    ConnectionPoolInspector connectionPoolInspector;

    @Rule
    public WireMockRule wm = new WireMockRule(wireMockConfig().port(9999));

    @Before
    public void init() {
        connectionPool = new ConnectionPool(2, 50000000);
        connectionPoolInspector = new ConnectionPoolInspector(connectionPool);

        client = new OkHttpClient().setConnectionPool(connectionPool);

        wm.stubFor(any(urlEqualTo("/something")).willReturn(aResponse().withStatus(200).withBody("BODY")));
    }

    @Test
    public void correctlyReportsLiveConnections() throws Exception {
        assertThat(connectionPoolInspector.liveConnections(), is(0));

        runIn10ConcurrentThreadsAndJoin(getTestUrlTask());

        assertThat(connectionPoolInspector.liveConnections(), greaterThan(0));
    }

    private Runnable getTestUrlTask() {
        return new Runnable() {
            public void run() {
                URL testUrl = url("/something");
                HttpURLConnection connection = client.open(testUrl);
                InputStream in = null;
                try {
                    in = connection.getInputStream();
                    ByteStreams.toByteArray(in);
                } catch (Exception e) {
                    throwUnchecked(e);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Exception e) {
                            throwUnchecked(e);
                        }
                    }
                }
            }
        };
    }

    private void runIn10ConcurrentThreadsAndJoin(Runnable task) throws InterruptedException {
        List<Thread> threads = newArrayList();
        for (int i = 1; i <= 10; i++) {
            Thread thread = new Thread(task);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread: threads) {
            thread.join();
        }
    }

    private URL url(String pathAndQuery) {
        try {
            return new URL("http://localhost:" + wm.port() + pathAndQuery);
        } catch (MalformedURLException e) {
            return throwUnchecked(e, URL.class);
        }
    }
}
