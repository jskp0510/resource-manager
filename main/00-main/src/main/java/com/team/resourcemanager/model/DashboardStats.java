package com.team.resourcemanager.model;

public record DashboardStats(
        int totalItems,
        int availableItems,
        int borrowedItems,
        int totalLoans) {
}
