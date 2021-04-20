package com.erikmolssons.template;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JDBCTemplate {

    private final HikariDataSource dataSource;

    public JDBCTemplate(HikariConfig hikariConfig) {
        this.dataSource = new HikariDataSource(hikariConfig);
    }

    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper) {
        var list = new ArrayList<T>();
        try (var connection = this.dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            var result = statement.executeQuery();
            while (result.next()) {
                list.add(rowMapper.map(result));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public <T> Optional<T> queryForObject(String sql, RowMapper<T> rowMapper) {
        T t = null;
        try (var connection = this.dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            var result = statement.executeQuery();
            if (result.first()) {
                t = rowMapper.map(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(t);
    }

    public int query(String sql, PreparedStatementBuilder preparedStatementBuilder) {
        int rowsAffected = 0;
        try (var connection = this.dataSource.getConnection()) {
            var statement = connection.prepareStatement(sql);
            preparedStatementBuilder.build(statement);
            rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) throw new SQLException();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int[] updateBatch(String sql, PreparedStatementBuilder preparedStatementBuilder) {
        int[] rowsAffected = new int[0];
        try (var connection = this.dataSource.getConnection()) {
            var statement = connection.prepareStatement(sql);
            preparedStatementBuilder.build(statement);
            rowsAffected = statement.executeBatch();
            if (rowsAffected.length == 0) throw new SQLException();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public boolean execute(String sql) {
        try (var connection = this.dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            return statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
