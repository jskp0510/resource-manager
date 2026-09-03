package com.team.resourcemanager.util;

public class Constants {

    // 권한
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    // ITEM 상태
    public static final String ITEM_AVAILABLE = "AVAILABLE";
    public static final String ITEM_MAINTENANCE = "MAINTENANCE";
    public static final String ITEM_BORROWED = "BORROWED";

    // LOAN 상태
    public static final String LOAN_REQUESTED = "REQUESTED";
    public static final String LOAN_BORROWED = "BORROWED";
    public static final String LOAN_REJECTED = "REJECTED";
    public static final String LOAN_RETURNED = "RETURNED";
    public static final String LOAN_OVERDUE = "OVERDUE";

    private Constants() {
    }
}