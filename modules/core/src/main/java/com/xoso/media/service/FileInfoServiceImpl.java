package com.xoso.media.service;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.infrastructure.core.service.PaginationHelper;
import com.xoso.media.wsdto.FileInfoWsDTO;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class FileInfoServiceImpl implements FileInfoService {

    @Autowired
    private PaginationHelper paginationHelper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FilesStorageService filesStorageService;

    @Override
    public Page<FileInfoWsDTO> retrieveAll(SearchParameters searchParameters) {
        var paramList = new ArrayList<Object>();
        var mapper = new FileInfoMapper(filesStorageService);
        final var sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(mapper.schema);
        String extraCriteria = " where 1=1 ";
        if (searchParameters != null) {
            var searchValue = searchParameters.getSearchValue();
            if (StringUtils.isNotBlank(searchValue)) {
                extraCriteria += " and file_name like ? ";
                var str = "%" + searchValue + "%";
                paramList.add(str);
            }
        }
        sqlBuilder.append(extraCriteria);
        var sqlBuilderNoPaging = sqlBuilder.toString();
        if (searchParameters != null && searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getLimit()*searchParameters.getOffset());
            }
        } else {
            sqlBuilder.append(" limit 100 offset 0 ");
        }
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlBuilder.toString(), sqlBuilderNoPaging,
                paramList.toArray(), mapper);
    }

    @Override
    public FileInfoWsDTO retrieveOne(Long id) {
        var mapper = new FileInfoMapper(filesStorageService);
        var sql = "select " + mapper.schema()
                + " where id = ?";
        return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { id });
    }

    private static final class FileInfoMapper implements RowMapper<FileInfoWsDTO> {
        private final String schema;
        private final FilesStorageService filesStorageService;

        public FileInfoMapper(FilesStorageService filesStorageService) {
            final StringBuilder sqlQuery = new StringBuilder()
                    .append("id, file_name as fileName, url ")
                    .append("from file_info ");
            this.schema = sqlQuery.toString();
            this.filesStorageService = filesStorageService;
        }
        public String schema() {
            return this.schema;
        }
        @SneakyThrows
        @Override
        public FileInfoWsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            var id = JdbcSupport.getLong(rs, "id");
            var fileName = rs.getString("fileName");
            var url = rs.getString("url");
            return new FileInfoWsDTO(id, fileName, url);
        }
    }
}
