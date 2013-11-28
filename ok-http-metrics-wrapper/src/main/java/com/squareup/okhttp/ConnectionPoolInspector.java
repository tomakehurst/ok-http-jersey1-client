package com.squareup.okhttp;

import com.google.common.base.Predicate;

import static com.google.common.collect.FluentIterable.from;

public class ConnectionPoolInspector {

    private final ConnectionPool connectionPool;

    public ConnectionPoolInspector(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public int liveConnections() {
        return countMatching(new Predicate<Connection>() {
            public boolean apply(Connection connection) {
                return connection.isAlive();
            }
        });
    }

    public int totalConnections() {
        return connectionPool.getConnectionCount();
    }

    private int countMatching(Predicate<Connection> predicate) {
        return from(connectionPool.getConnections()).filter(predicate).size();
    }
}
