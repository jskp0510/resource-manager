package com.team.resourcemanager.model;

import java.sql.Date;

public class LoanHistoryResult {

    private int loanId;
    private String itemName;
    private String userName;
    private Date startDate;
    private Date dueDate;
    private Date returnDate;
    private String purpose;
    private String status;

    public LoanHistoryResult(
            int loanId,
            String itemName,
            String userName,
            Date startDate,
            Date dueDate,
            Date returnDate,
            String purpose,
            String status
    ) {
        this.loanId = loanId;
        this.itemName = itemName;
        this.userName = userName;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.purpose = purpose;
        this.status = status;
    }

    public int getLoanId() {
        return loanId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getUserName() {
        return userName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStatus() {
        return status;
    }
}