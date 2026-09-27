package com.team.resourcemanager.util;

/** 화면 표시용 한글 상태명. DB에는 기존 상태 코드를 그대로 저장한다. */
public final class StatusLabels {
    private StatusLabels() {
    }

    public static String item(String status) {
        return switch (status == null ? "" : status) {
            case "AVAILABLE" -> "대여 가능";
            case "BORROWED" -> "대여 중";
            case "MAINTENANCE" -> "대여 불가";
            default -> status;
        };
    }

    public static String itemCode(String label) {
        return switch (label == null ? "" : label) {
            case "대여 가능" -> "AVAILABLE";
            case "대여 중" -> "BORROWED";
            case "대여 불가" -> "MAINTENANCE";
            default -> label;
        };
    }

    public static String loan(String status) {
        return switch (status == null ? "" : status) {
            case "REQUESTED" -> "신청 대기";
            case "BORROWED" -> "대여 중";
            case "RETURN_REQUESTED" -> "반납 신청";
            case "REJECTED" -> "대여 거절";
            case "RETURNED" -> "반납 완료";
            case "OVERDUE" -> "연체";
            default -> status;
        };
    }
}
