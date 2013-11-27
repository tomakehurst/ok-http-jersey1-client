package com.squareup.okhttp;

public class ConnectionPoolInspector {

    private final ConnectionPool connectionPool;

    public ConnectionPoolInspector(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public int activeConnections() {
        return 0;
    }
}
