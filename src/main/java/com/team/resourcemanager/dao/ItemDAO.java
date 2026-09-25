package com.team.resourcemanager.dao;

import com.team.resourcemanager.util.DBConnection;

import com.team.resourcemanager.model.ItemSearchResult;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ItemDAO {

    public void searchByName(String keyword) {

        String sql = """
                SELECT *
                FROM item
                WHERE item_name LIKE ?
                """;

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                System.out.println("물품번호: " + rs.getInt("item_id"));
                System.out.println("물품명: " + rs.getString("item_name"));
                System.out.println("일련번호: " + rs.getString("serial_no"));
                System.out.println("상태: " + rs.getString("status"));

                System.out.println("----------------------");
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchBySerialNo(String serialNo) {

        String sql = """
                SELECT *
                FROM item
                WHERE serial_no LIKE ?
                """;

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, "%" + serialNo + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                System.out.println("물품번호: " + rs.getInt("item_id"));
                System.out.println("물품명: " + rs.getString("item_name"));
                System.out.println("일련번호: " + rs.getString("serial_no"));
                System.out.println("상태: " + rs.getString("status"));

                System.out.println("----------------------");
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchByCategory(String categoryName) {

        String sql = """
                SELECT
                    i.item_id,
                    i.item_name,
                    i.serial_no,
                    i.status,
                    c.name AS category_name
                FROM item i
                JOIN category c
                    ON i.category_id = c.category_id
                WHERE c.name LIKE ?
                """;

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, "%" + categoryName + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                System.out.println("물품번호: " + rs.getInt("item_id"));
                System.out.println("물품명: " + rs.getString("item_name"));
                System.out.println("일련번호: " + rs.getString("serial_no"));
                System.out.println("카테고리: " + rs.getString("category_name"));
                System.out.println("상태: " + rs.getString("status"));

                System.out.println("----------------------");
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchByStatus(String status) {

        String sql = """
                SELECT
                    i.item_id,
                    i.item_name,
                    i.serial_no,
                    i.status,
                    c.name AS category_name
                FROM item i
                JOIN category c
                    ON i.category_id = c.category_id
                WHERE i.status = ?
                """;

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, status);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                System.out.println("물품번호: " + rs.getInt("item_id"));
                System.out.println("물품명: " + rs.getString("item_name"));
                System.out.println("일련번호: " + rs.getString("serial_no"));
                System.out.println("카테고리: " + rs.getString("category_name"));
                System.out.println("상태: " + rs.getString("status"));

                System.out.println("----------------------");
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ItemSearchResult> searchItems(
            String itemName,
            String serialNo,
            String categoryName,
            String status
    ) {

        List<ItemSearchResult> list = new ArrayList<>();

        String sql = """
                SELECT
                    i.item_id,
                    i.item_name,
                    i.serial_no,
                    i.status,
                    c.name AS category_name
                FROM item i
                JOIN category c
                    ON i.category_id = c.category_id
                WHERE i.item_name LIKE ?
                  AND (i.serial_no LIKE ? OR i.serial_no IS NULL)
                  AND c.name LIKE ?
                  AND i.status LIKE ?
                """;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, "%" + itemName + "%");
            pstmt.setString(2, "%" + serialNo + "%");
            pstmt.setString(3, "%" + categoryName + "%");
            pstmt.setString(4, "%" + status + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                ItemSearchResult item =
                        new ItemSearchResult(
                                rs.getInt("item_id"),
                                rs.getString("item_name"),
                                rs.getString("serial_no"),
                                rs.getString("category_name"),
                                rs.getString("status")
                        );

                list.add(item);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
