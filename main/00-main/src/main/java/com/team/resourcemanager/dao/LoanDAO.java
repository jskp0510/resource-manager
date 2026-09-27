package com.team.resourcemanager.dao;

import java.util.LinkedHashMap;
import java.util.Map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.team.resourcemanager.model.Loan;
import com.team.resourcemanager.util.DBConnection;

import java.sql.Date;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

//DB 조회 및 변경
public class LoanDAO {
	//신청 등록
	public int insertLoan(Loan loan) throws SQLException{
		String sql = """
		        INSERT INTO LOAN
		        (user_id, item_id, start_date, due_date, purpose, status)
		        VALUES (?, ?, ?, ?, ?, ?)
		        """;
		try (Connection conn = DBConnection.getConnection();
			     PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, loan.getUserId());
			pstmt.setInt(2, loan.getItemId());
			pstmt.setDate(3, Date.valueOf(loan.getStartDate()));
			pstmt.setDate(4, Date.valueOf(loan.getDueDate()));
			pstmt.setString(5, loan.getPurpose());
			pstmt.setString(6, loan.getStatus());
			
			return pstmt.executeUpdate();
		}
	}
	public int insertLoan(Connection conn, Loan loan) throws SQLException {

	    String sql = """
	            INSERT INTO LOAN
	            (user_id, item_id, start_date, due_date, purpose, status)
	            VALUES (?, ?, ?, ?, ?, ?)
	            """;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, loan.getUserId());
	        pstmt.setInt(2, loan.getItemId());
	        pstmt.setDate(3, Date.valueOf(loan.getStartDate()));
	        pstmt.setDate(4, Date.valueOf(loan.getDueDate()));
	        pstmt.setString(5, loan.getPurpose());
	        pstmt.setString(6, loan.getStatus());

	        return pstmt.executeUpdate();
	    }
	}
	
	//신청 가능 물품 조회
	public boolean isItemAvailable(Connection conn, int itemId) throws SQLException {

	    String sql = """
	            SELECT 1
	            FROM ITEM i
	            WHERE item_id = ?
	    			AND i.status = 'AVAILABLE'
	    		 	AND NOT EXISTS (
	    		          	SELECT 1
	    		          	FROM LOAN l
	    		          	WHERE l.item_id = i.item_id
	    	            		AND l.status = 'REQUESTED')
	            """;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, itemId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            return rs.next();
	        }
	    }
	}
	//신청 대기 목록 조회
	public List<Loan> findRequestedLoans(Connection conn) throws SQLException {

	    String sql = """
	            SELECT l.loan_id, l.user_id, l.item_id, u.name AS user_name, i.item_name,
	                   l.start_date, l.due_date, l.return_date, l.purpose, l.status
	            FROM LOAN l
	            JOIN USER u ON u.user_id = l.user_id
	            JOIN ITEM i ON i.item_id = l.item_id
	            WHERE l.status = 'REQUESTED'
	            ORDER BY l.loan_id ASC
	            """;

	    List<Loan> loans = new ArrayList<>();

	    try (PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {

	            Date returnDate = rs.getDate("return_date");

	            Loan loan = new Loan(
	                    rs.getInt("loan_id"),
	                    rs.getInt("user_id"),
	                    rs.getInt("item_id"),
	                    rs.getDate("start_date").toLocalDate(),
	                    rs.getDate("due_date").toLocalDate(),
	                    returnDate == null ? null : returnDate.toLocalDate(),
	                    rs.getString("purpose"),
	                    rs.getString("status")
	            );
	            loan.setUserName(rs.getString("user_name"));
	            loan.setItemName(rs.getString("item_name"));

	            loans.add(loan);
	        }
	    }

	    return loans;
	}
	//승인 대기 물품 조회
	public Integer findItemIdByRequestedLoanId(Connection conn, int loanId)
	        throws SQLException {

	    String sql = """
	            SELECT item_id
	            FROM LOAN
	            WHERE loan_id = ?
	              AND status = 'REQUESTED'
	            """;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, loanId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("item_id");
	            }
	        }
	    }

	    return null;
	}


	//대여 신청 승인 상태 변경
	public int markLoanBorrowed(Connection conn, int loanId)
	        throws SQLException {

	    String sql = """
	            UPDATE LOAN
	            SET status = 'BORROWED'
	            WHERE loan_id = ?
	              AND status = 'REQUESTED'
	            """;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, loanId);

	        return pstmt.executeUpdate();
	    }
	}


	//대여 중 상태 변경
	public int markItemBorrowed(Connection conn, int itemId)
	        throws SQLException {

	    String sql = """
	            UPDATE ITEM
	            SET status = 'BORROWED'
	            WHERE item_id = ?
	              AND status = 'AVAILABLE'
	            """;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, itemId);

	        return pstmt.executeUpdate();
	    }
	}
	
	//대여 신청 거절
	public int markLoanRejected(Connection conn, int loanId) 
			throws SQLException{
		
		String sql ="""
				UPDATE LOAN
				SET status = 'REJECTED'
				WHERE loan_id = ?
				AND status ='REQUESTED'
				""";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, loanId);

	        return pstmt.executeUpdate();
	    }
	}
	//대여 신청 가능한 물품 목록 조회
	public Map<Integer, String> findRequestableItems(Connection conn)
	        throws SQLException {

	    String sql = """
	            SELECT i.item_id, i.item_name, i.serial_no
	            FROM ITEM i
	            WHERE i.status = 'AVAILABLE'
	              AND NOT EXISTS (
	                  SELECT 1
	                  FROM LOAN l
	                  WHERE l.item_id = i.item_id
	                    AND l.status = 'REQUESTED'
	              )
	            ORDER BY i.item_name, i.item_id
	            """;

	    Map<Integer, String> items = new LinkedHashMap<>();

	    try (PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {

	            int itemId = rs.getInt("item_id");
	            String itemName = rs.getString("item_name");
	            String serialNo = rs.getString("serial_no");

	            String displayName;

	            if (serialNo == null || serialNo.isBlank()) {
	                displayName = itemName;
	            } else {
	                displayName = itemName + " / " + serialNo;
	            }

	            items.put(itemId, displayName);
	        }
	    }

	    return items;
	}

	// ===================================================================
	// 아래부터 반납 / 연체 관련 메서드 (팀원4 담당분 추가)
	// ===================================================================

	//본인이 대여중(BORROWED) 또는 연체(OVERDUE)인 대여 목록 조회 - 반납 신청 화면에서 사용
	public List<Loan> findBorrowedLoansByUser(Connection conn, int userId)
	        throws SQLException {

	    String sql = """
	            SELECT loan_id, user_id, item_id, start_date, due_date, return_date, purpose, status
	            FROM LOAN
	            WHERE user_id = ?
	              AND status IN ('BORROWED', 'OVERDUE')
	            ORDER BY due_date ASC
	            """;

	    List<Loan> loans = new ArrayList<>();

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, userId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                loans.add(mapRow(rs));
	            }
	        }
	    }

	    return loans;
	}

	//반납 신청 처리 (사용자) - BORROWED/OVERDUE -> RETURN_REQUESTED
	public int markLoanReturnRequested(Connection conn, int loanId)
	        throws SQLException {

	    String sql = """
	            UPDATE LOAN
	            SET status = 'RETURN_REQUESTED'
	            WHERE loan_id = ?
	              AND status IN ('BORROWED', 'OVERDUE')
	            """;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, loanId);

	        return pstmt.executeUpdate();
	    }
	}

	//반납 신청 목록 조회 (관리자)
	public List<Loan> findReturnRequestedLoans(Connection conn)
	        throws SQLException {

	    String sql = """
	            SELECT loan_id, user_id, item_id, start_date, due_date, return_date, purpose, status
	            FROM LOAN
	            WHERE status = 'RETURN_REQUESTED'
	            ORDER BY loan_id ASC
	            """;

	    List<Loan> loans = new ArrayList<>();

	    try (PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            loans.add(mapRow(rs));
	        }
	    }

	    return loans;
	}

	//반납 신청된 loan의 item_id 조회 (반납 확인 처리 시 물품 상태도 같이 바꿔야 하므로 필요)
	public Integer findItemIdByReturnRequestedLoanId(Connection conn, int loanId)
	        throws SQLException {

	    String sql = """
	            SELECT item_id
	            FROM LOAN
	            WHERE loan_id = ?
	              AND status = 'RETURN_REQUESTED'
	            """;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, loanId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("item_id");
	            }
	        }
	    }

	    return null;
	}

	//반납 확인 처리 (관리자) - RETURN_REQUESTED -> RETURNED, return_date 기록
	public int markLoanReturned(Connection conn, int loanId, Date returnDate)
	        throws SQLException {

	    String sql = """
	            UPDATE LOAN
	            SET status = 'RETURNED',
	                return_date = ?
	            WHERE loan_id = ?
	              AND status = 'RETURN_REQUESTED'
	            """;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setDate(1, returnDate);
	        pstmt.setInt(2, loanId);

	        return pstmt.executeUpdate();
	    }
	}

	//물품 상태를 다시 AVAILABLE로 변경 (반납 확인 시 함께 처리)
	public int markItemAvailable(Connection conn, int itemId)
	        throws SQLException {

	    String sql = """
	            UPDATE ITEM
	            SET status = 'AVAILABLE'
	            WHERE item_id = ?
	              AND status = 'BORROWED'
	            """;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, itemId);

	        return pstmt.executeUpdate();
	    }
	}

	//연체 대상 조회 - 반납 신청 전이며 반납예정일이 지난 BORROWED 상태
	public List<Loan> findOverdueLoans(Connection conn) throws SQLException {

	    String sql = """
	            SELECT loan_id, user_id, item_id, start_date, due_date, return_date, purpose, status
	            FROM LOAN
	            WHERE status = 'BORROWED'
	              AND due_date < CURDATE()
	            """;

	    List<Loan> loans = new ArrayList<>();

	    try (PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            loans.add(mapRow(rs));
	        }
	    }

	    return loans;
	}

	//대여 건을 연체(OVERDUE) 상태로 변경
	public int markLoanOverdue(Connection conn, int loanId) throws SQLException {

	    String sql = """
	            UPDATE LOAN
	            SET status = 'OVERDUE'
	            WHERE loan_id = ?
	              AND status = 'BORROWED'
	            """;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, loanId);

	        return pstmt.executeUpdate();
	    }
	}

	//ResultSet 한 행을 Loan 객체로 변환 (반납/연체 조회 메서드들에서 공통 사용)
	private Loan mapRow(ResultSet rs) throws SQLException {

	    Date returnDate = rs.getDate("return_date");

	    return new Loan(
	            rs.getInt("loan_id"),
	            rs.getInt("user_id"),
	            rs.getInt("item_id"),
	            rs.getDate("start_date").toLocalDate(),
	            rs.getDate("due_date").toLocalDate(),
	            returnDate == null ? null : returnDate.toLocalDate(),
	            rs.getString("purpose"),
	            rs.getString("status")
	    );
	}

}
