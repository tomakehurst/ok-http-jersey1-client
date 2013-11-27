package com.squareup.okhttp;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Executors;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

public class ConnectionPoolInspectorTest {

    OkHttpClient client;
    ConnectionPool connectionPool;
    ConnectionPoolInspector connectionPoolInspector;

    @Rule
    public WireMockRule wm = new WireMockRule(wireMockConfig().port(0));

    @Before
    public void init() {
        connectionPool = new ConnectionPool(2, 5000);
        connectionPoolInspector = new ConnectionPoolInspector(connectionPool);

        client = new OkHttpClient().setConnectionPool(connectionPool);
    }

    @Test
    public void correctlyReportsLiveConnections() {
        Executors.newFixedThreadPool(10).invokeAll()
    }

    private Connection activeConnection() {
        Connection connection = mock(Connection.class);
        when(connection.isAlive()).thenReturn(true);
        return connection;
    }

    private Connection inactiveConnection() {
        Connection connection = mock(Connection.class);
        when(connection.isAlive()).thenReturn(false);
        return connection;
    }
}
