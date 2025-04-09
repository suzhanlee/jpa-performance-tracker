package com.tracker.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class PerformanceDataSourceProxy implements DataSource {

    private final DataSource targetDataSource;

    public PerformanceDataSourceProxy(DataSource targetDataSource) {
        this.targetDataSource = targetDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection originalConnection = targetDataSource.getConnection();
        return createConnectionProxy(originalConnection);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection originalConnection = targetDataSource.getConnection(username, password);
        return createConnectionProxy(originalConnection);
    }

    private Connection createConnectionProxy(Connection originalConnection) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                new ConnectionInvocationHandler(originalConnection)
        );
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return targetDataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        targetDataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        targetDataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return targetDataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return targetDataSource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return targetDataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || targetDataSource.isWrapperFor(iface));
    }
}