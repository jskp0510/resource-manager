package com.team.resourcemanager.model;
import java.time.LocalDate;

public class Loan {
	private int loanId;
	private int userId;
	private int itemId;
	private LocalDate startDate;
	private LocalDate dueDate;
	private LocalDate returnDate;
	private String purpose;
	private String status;
	
	public Loan() {
		
	}
	//대여 신청용
	public Loan(int userId, int itemId, LocalDate startDate, 
			LocalDate dueDate, String purpose, String status) {
		this.userId = userId;
		this.itemId=itemId;
		this.startDate = dueDate;
		this.purpose = purpose;
		this.status = status;
	}	
	//DB 조회용
	public Loan(int loanId, int userId, int itemId,
		   LocalDate startDate, LocalDate dueDate,
		   LocalDate returnDate, String purpose, String status) {

		 this.loanId = loanId;
		 this.userId = userId;
		 this.itemId = itemId;
		 this.startDate = startDate;
		 this.dueDate = dueDate;
		 this.returnDate = returnDate;
		 this.purpose = purpose;
		 this.status = status;

	}
	public int getLoanId() {
		return loanId;
	}
	public void setLoanId(int loanId) {
		this.loanId = loanId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getDueDate() {
		return dueDate;
	}
	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}
	public LocalDate getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
