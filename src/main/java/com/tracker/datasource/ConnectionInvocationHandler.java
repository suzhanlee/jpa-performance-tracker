package com.tracker.datasource;

import com.tracker.aop.MethodCallInfo;
import com.tracker.aop.RepositoryMethodAspect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class ConnectionInvocationHandler implements InvocationHandler {

    private final Connection delegate;

    public ConnectionInvocationHandler(Connection delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("close".equals(method.getName())) {
            delegate.close();
            return null;
        }

        if ("prepareStatement".equals(method.getName()) && args != null && args.length > 0 && args[0] instanceof String) {
            String sql = (String) args[0];
            PreparedStatement preparedStatement = (PreparedStatement) method.invoke(delegate, args);

            return Proxy.newProxyInstance(
                    PreparedStatement.class.getClassLoader(),
                    new Class[] { PreparedStatement.class },
                    new StatementInvocationHandler(preparedStatement, sql, getCurrentRepositoryClass(), getCurrentMethodName())
            );
        }

        if ("createStatement".equals(method.getName())) {
            Statement statement = (Statement) method.invoke(delegate, args);

            return Proxy.newProxyInstance(
                    Statement.class.getClassLoader(),
                    new Class[] { Statement.class },
                    new StatementInvocationHandler(statement, null, getCurrentRepositoryClass(), getCurrentMethodName())
            );
        }

        return method.invoke(delegate, args);
    }

    private String getCurrentRepositoryClass() {
        MethodCallInfo methodCallInfo = RepositoryMethodAspect.getCurrentMethod();
        return methodCallInfo != null ? methodCallInfo.repositoryClassName() : "Unknown";
    }

    private String getCurrentMethodName() {
        MethodCallInfo methodCallInfo = RepositoryMethodAspect.getCurrentMethod();
        return methodCallInfo != null ? methodCallInfo.methodName() : "Unknown";
    }
}
