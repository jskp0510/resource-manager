package com.team.resourcemanager.model;

public class ItemSearchResult {

    private int itemId;
    private String itemName;
    private String serialNo;
    private String categoryName;
    private String status;

    public ItemSearchResult(int itemId, String itemName, String serialNo,
                            String categoryName, String status) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.serialNo = serialNo;
        this.categoryName = categoryName;
        this.status = status;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getStatus() {
        return status;
    }
}