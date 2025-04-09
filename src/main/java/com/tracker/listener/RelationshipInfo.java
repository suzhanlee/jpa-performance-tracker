package com.tracker.listener;

public class RelationshipInfo {
    private final String ownerEntityName;
    private final String childEntityName;
    private final String role;
    private int usageCount;
    private boolean optimizationSuggested;
    private int n1PotentialScore;

    public RelationshipInfo(String ownerEntityName, String childEntityName, String role) {
        this.ownerEntityName = ownerEntityName;
        this.childEntityName = childEntityName;
        this.role = role;
        this.usageCount = 0;
        this.optimizationSuggested = false;
        this.n1PotentialScore = 0;
    }

    @Override
    public String toString() {
        return "RelationshipInfo{" +
                "ownerEntityName='" + ownerEntityName + '\'' +
                ", childEntityName='" + childEntityName + '\'' +
                ", role='" + role + '\'' +
                ", usageCount=" + usageCount +
                ", optimizationSuggested=" + optimizationSuggested +
                ", n1PotentialScore=" + n1PotentialScore +
                '}';
    }

    public void incrementUsageCount() {
        this.usageCount += 1;
    }
}