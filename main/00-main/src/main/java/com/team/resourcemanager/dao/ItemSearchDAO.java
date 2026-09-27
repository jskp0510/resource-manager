package com.team.resourcemanager.dao;

import com.team.resourcemanager.model.ItemSearchResult;
import com.team.resourcemanager.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** 물품 검색 PR의 조회 기능을 공통 DB 연결 및 스키마로 제공한다. */
public class ItemSearchDAO {
    public List<String> findCategoryNames() throws SQLException {
        String sql = "SELECT name FROM CATEGORY ORDER BY name";
        List<String> names = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
            }
        }
        return names;
    }

    public List<ItemSearchResult> search(String itemName, String categoryName, String status)
            throws SQLException {
        String sql = """
                SELECT i.item_id, i.item_name, i.serial_no, c.name AS category_name, i.status
                FROM ITEM i
                JOIN CATEGORY c ON c.category_id = i.category_id
                WHERE i.item_name LIKE ?
                  AND (? = '' OR c.name = ?)
                  AND (? = '' OR i.status = ?)
                ORDER BY i.item_name, i.item_id
                """;
        List<ItemSearchResult> items = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + itemName.trim() + "%");
            statement.setString(2, categoryName);
            statement.setString(3, categoryName);
            statement.setString(4, status);
            statement.setString(5, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new ItemSearchResult(
                            resultSet.getInt("item_id"),
                            resultSet.getString("item_name"),
                            resultSet.getString("serial_no"),
                            resultSet.getString("category_name"),
                            resultSet.getString("status")));
                }
            }
        }
        return items;
    }
}
