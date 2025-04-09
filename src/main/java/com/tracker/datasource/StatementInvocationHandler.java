package com.tracker.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StatementInvocationHandler implements InvocationHandler {

    private static final Set<String> EXECUTION_METHODS = new HashSet<>(
            Arrays.asList("execute", "executeQuery", "executeUpdate", "executeBatch", "executeLargeBatch", "executeLargeUpdate")
    );

    private final Statement delegate;
    private final String sql;
    private final String repositoryClassName;
    private final String methodName;

    public StatementInvocationHandler(Statement delegate, String sql, String repositoryClassName, String methodName) {
        this.delegate = delegate;
        this.sql = sql;
        this.repositoryClassName = repositoryClassName;
        this.methodName = methodName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("close".equals(methodName)) {
            delegate.close();
            return null;
        }

        if (EXECUTION_METHODS.contains(method.getName())) {
            String executedSql = sql;
            if (executedSql == null && args != null && args.length > 0 && args[0] instanceof String) {
                executedSql = (String) args[0];
            }

            long startTime = System.currentTimeMillis();

            try {
                Object result = method.invoke(delegate, args);
                long executionTime = System.currentTimeMillis() - startTime;
            } catch (Exception e) {
                long executionTime = System.currentTimeMillis() - startTime;
                throw e;
            }
        }

        return method.invoke(delegate, args);
    }
}
