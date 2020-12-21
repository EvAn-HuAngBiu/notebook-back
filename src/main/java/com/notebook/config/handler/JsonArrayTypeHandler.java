package com.notebook.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notebook.config.storage.FileStorageService;
import lombok.SneakyThrows;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Project: notebook
 * File: JsonArrayTypeHandler
 *
 * @author evan
 * @date 2020/11/9
 */
@SuppressWarnings("all")
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonArrayTypeHandler extends BaseTypeHandler<List<String>> {
    @SneakyThrows
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<String> strings, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, new ObjectMapper().writeValueAsString(strings));
    }

    @SneakyThrows
    @Override
    public List<String> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return new ObjectMapper().readValue(resultSet.getString(s), List.class);
    }

    @SneakyThrows
    @Override
    public List<String> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String jsonStr = resultSet.getString(i);
        return new ObjectMapper().readValue(jsonStr, List.class);
    }

    @SneakyThrows
    @Override
    public List<String> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String jsonStr = callableStatement.getString(i);
        return new ObjectMapper().readValue(jsonStr, List.class);
    }
}
