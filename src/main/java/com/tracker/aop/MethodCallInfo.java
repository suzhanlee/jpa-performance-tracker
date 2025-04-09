package com.tracker.aop;

public record MethodCallInfo(String repositoryClassName, String methodName, Object[] args) {

    @Override
    public String toString() {
        return repositoryClassName + "." + methodName;
    }
}