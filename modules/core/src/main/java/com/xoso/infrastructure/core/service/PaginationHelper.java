package com.xoso.infrastructure.core.service;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.format;

@Component
public class PaginationHelper {

    public <E> Page<E> fetchPage(final JdbcTemplate jt, final String sqlFetchRows, final String sqlFetchRowsNotPaging, final Object[] args, final RowMapper<E> rowMapper) {

        final List<E> items = jt.query(sqlFetchRows, rowMapper, args); // NOSONAR

        // determine how many rows are available
        final String sqlCountRows = countQueryResult(sqlFetchRowsNotPaging);
        final int totalFilteredRecords;
        totalFilteredRecords = jt.queryForObject(sqlCountRows, Integer.class, args);

        return new Page<>(items, totalFilteredRecords);
    }

    public <E> Page<Long> fetchPage(JdbcTemplate jdbcTemplate, String sql, Class<Long> type) {
        final List<Long> items = jdbcTemplate.queryForList(sql, type);

        // determine how many rows are available
        String sqlCountRows = countQueryResult(sql);
        Integer totalFilteredRecords = jdbcTemplate.queryForObject(sqlCountRows, Integer.class);

        return new Page<>(items, ObjectUtils.defaultIfNull(totalFilteredRecords, 0));
    }

    public String countQueryResult(String sql) {
        return format("SELECT COUNT(*) FROM (%s) AS temp", sql);
    }
}
