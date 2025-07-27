package com.chenx.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class PostgresQuery {
    static final String sql = "select * from userinfo where id = ?";
    static final String url = "jdbc:postgresql://localhost:5432/mydb";
    static final String username = "postgres";
    static final String password = "root";

    public static void main(String[] args) {
//        driverManager();
        hikari();
    }

    private static void hikari() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        try (HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
             PreparedStatement preparedStatement = hikariDataSource.getConnection().prepareStatement(sql);) {
            preparedStatement.setInt(1, 1);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int age = resultSet.getInt(3);
                User user = new User(id, name, age);
                System.out.println(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void driverManager() {
        try (Connection conn = DriverManager.getConnection(url, username, password);) {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, 1);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int age = resultSet.getInt(3);
                User result = new User(id, name, age);
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    record User(int id, String name, int age) {
    }
}
