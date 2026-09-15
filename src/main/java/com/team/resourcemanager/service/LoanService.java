package com.team.resourcemanager.service;

import java.util.List;

import java.util.Map;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.team.resourcemanager.dao.LoanDAO;
import com.team.resourcemanager.model.Loan;

//대여 신청 및 관리자 승인/거절 처리
public class LoanService {

    private final LoanDAO loanDAO = new LoanDAO();

    //대여 신청
    public boolean requestLoan(
            Connection conn,
            int userId,
            int itemId,
            LocalDate startDate,
            LocalDate dueDate,
            String purpose) throws SQLException {

        if (startDate == null || dueDate == null) {
            throw new IllegalArgumentException(
                    "대여 시작일과 반납 예정일을 입력해주세요.");
        }

        if (dueDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "반납 예정일은 대여 시작일보다 빠를 수 없습니다.");
        }

        // 대여 가능한 물품인지 확인
        if (!loanDAO.isItemAvailable(conn, itemId)) {
            return false;
        }

        Loan loan = new Loan(
                userId,
                itemId,
                startDate,
                dueDate,
                purpose,
                "REQUESTED"
        );

        return loanDAO.insertLoan(conn, loan) == 1;
    }
    //신청 가능 물품 목록 조회
    public Map<Integer, String> getRequestableItems(Connection conn)
            throws SQLException {

        return loanDAO.findRequestableItems(conn);
    }
	//관리자 대여 승인
    public boolean approveLoan(Connection conn, int loanId)
            throws SQLException {

        boolean originalAutoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            Integer itemId =
                    loanDAO.findItemIdByRequestedLoanId(conn, loanId);

            if (itemId == null) {
                conn.rollback();
                return false;
            }

            int loanResult =
                    loanDAO.markLoanBorrowed(conn, loanId);

            int itemResult =
                    loanDAO.markItemBorrowed(conn, itemId);

            if (loanResult == 1 && itemResult == 1) {
                conn.commit();
                return true;
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {

            conn.rollback();
            throw e;

        } finally {

            conn.setAutoCommit(originalAutoCommit);
        }
    }  
    //관리자 승인 대기 목록 조회
    public List<Loan> getRequestedLoans(Connection conn)
            throws SQLException {

        return loanDAO.findRequestedLoans(conn);
    }
    //관리자 대여 거절
    public boolean rejectLoan(Connection conn, int loanId)
            throws SQLException {

        return loanDAO.markLoanRejected(conn, loanId) == 1;
    }

    // ===================================================================
    // 아래부터 반납 / 연체 관련 메서드 (팀원4 담당분 추가)
    // ===================================================================

    //본인이 대여중/연체중인 목록 조회 (반납 신청 화면에서 사용)
    public List<Loan> getMyBorrowedLoans(Connection conn, int userId)
            throws SQLException {

        return loanDAO.findBorrowedLoansByUser(conn, userId);
    }

    //반납 신청 (사용자)
    public boolean requestReturn(Connection conn, int loanId)
            throws SQLException {

        return loanDAO.markLoanReturnRequested(conn, loanId) == 1;
    }

    //관리자 반납 신청 목록 조회
    public List<Loan> getReturnRequestedLoans(Connection conn)
            throws SQLException {

        return loanDAO.findReturnRequestedLoans(conn);
    }

    //관리자 반납 확인 처리 - LOAN 상태 변경 + ITEM 상태를 AVAILABLE로 되돌림 (트랜잭션 처리)
    public boolean confirmReturn(Connection conn, int loanId)
            throws SQLException {

        boolean originalAutoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            Integer itemId =
                    loanDAO.findItemIdByReturnRequestedLoanId(conn, loanId);

            if (itemId == null) {
                conn.rollback();
                return false;
            }

            java.sql.Date today = java.sql.Date.valueOf(LocalDate.now());

            int loanResult =
                    loanDAO.markLoanReturned(conn, loanId, today);

            int itemResult =
                    loanDAO.markItemAvailable(conn, itemId);

            if (loanResult == 1 && itemResult == 1) {
                conn.commit();
                return true;
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {

            conn.rollback();
            throw e;

        } finally {

            conn.setAutoCommit(originalAutoCommit);
        }
    }

    //연체 여부 확인 + 연체일수 계산 (오늘 날짜 기준)
    public long calculateOverdueDays(Loan loan) {

        LocalDate dueDate = loan.getDueDate();
        LocalDate compareDate =
                (loan.getReturnDate() != null) ? loan.getReturnDate() : LocalDate.now();

        long diffDays = ChronoUnit.DAYS.between(dueDate, compareDate);

        return Math.max(diffDays, 0);
    }

    //전체 대여 건을 훑어서 연체된 것들을 OVERDUE 상태로 일괄 갱신
    public int updateOverdueStatuses(Connection conn) throws SQLException {

        int updatedCount = 0;

        List<Loan> overdueLoans = loanDAO.findOverdueLoans(conn);

        for (Loan loan : overdueLoans) {

            if (!"OVERDUE".equals(loan.getStatus())) {

                int result = loanDAO.markLoanOverdue(conn, loan.getLoanId());

                if (result == 1) {
                    updatedCount++;
                }
            }
        }

        return updatedCount;
    }

}