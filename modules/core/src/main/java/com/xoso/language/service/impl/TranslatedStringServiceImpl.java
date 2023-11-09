package com.xoso.language.service.impl;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.infrastructure.core.service.PaginationHelper;
import com.xoso.language.data.TranslatedStringData;
import com.xoso.language.exception.TranslateNotFoundException;
import com.xoso.language.model.TranslatedString;
import com.xoso.language.repository.TranslatedStringRepository;
import com.xoso.language.service.TranslatedStringService;
import com.xoso.language.wsdto.TranslatedRequestWsDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranslatedStringServiceImpl implements TranslatedStringService {

    private final PaginationHelper paginationHelper;
    private final JdbcTemplate jdbcTemplate;
    private final TranslatedStringRepository translatedStringRepository;

    @Autowired
    public TranslatedStringServiceImpl(PaginationHelper paginationHelper, JdbcTemplate jdbcTemplate, TranslatedStringRepository translatedStringRepository) {
        this.paginationHelper = paginationHelper;
        this.jdbcTemplate = jdbcTemplate;
        this.translatedStringRepository = translatedStringRepository;
    }

    @Override
    public List<TranslatedStringData> retreiveAll() {
        List<TranslatedString> results = translatedStringRepository.findAll();
        return results.stream().map(TranslatedStringData::fromEntity).collect(Collectors.toList());
    }

    @Override
    public TranslatedStringData create(TranslatedRequestWsDTO request) {
        var translate = TranslatedString.builder()
                .code(request.getCode())
                .viet(request.getViet())
                .lao(request.getLao())
                .thai(request.getThai())
                .cam(request.getCam())
                .build();
        this.translatedStringRepository.saveAndFlush(translate);
        return TranslatedStringData.fromEntity(translate);
    }

    @Override
    public TranslatedStringData update(Long id, TranslatedRequestWsDTO request) {
        var translate = this.translatedStringRepository.findById(id).orElseThrow(TranslateNotFoundException::new);
        if (StringUtils.isNotBlank(request.getCode())) {
            translate.setCode(request.getCode());
        }
        if (StringUtils.isNotBlank(request.getViet())) {
            translate.setViet(request.getViet());
        }
        if (StringUtils.isNotBlank(request.getThai())) {
            translate.setThai(request.getThai());
        }
        if (StringUtils.isNotBlank(request.getLao())) {
            translate.setLao(request.getLao());
        }
        if (StringUtils.isNotBlank(request.getCam())) {
            translate.setCam(request.getCam());
        }
        this.translatedStringRepository.save(translate);
        return TranslatedStringData.fromEntity(translate);
    }

    @Override
    public Page<TranslatedStringData> retrieveAll(SearchParameters searchParameters) {
        var mapper = new TranslateMapper();
        var paramList = new ArrayList<Object>();
        final var sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(mapper.schema());

        String extraCriteria = " where 1=1 ";
        if (searchParameters != null) {
            var searchValue = searchParameters.getSearchValue();
            if (StringUtils.isNotBlank(searchValue)) {
                extraCriteria += " and code like ? ";
                var str = "%" + searchValue + "%";
                paramList.add(str);
            }
        }
        sqlBuilder.append(extraCriteria);
        sqlBuilder.append(" order by code DESC ");
        var sqlBuilderNoPaging = sqlBuilder.toString();
        if (searchParameters != null && searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getLimit()*searchParameters.getOffset());
            }
        } else {
            sqlBuilder.append(" limit 100 offset 0 ");
        }
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlBuilder.toString(), sqlBuilderNoPaging, paramList.toArray(), mapper);
    }

    @Override
    public TranslatedStringData retrieveOne(Long id) {
        var mapper = new TranslateMapper();
        var sql = "select " + mapper.schema()
                + " where id = ?";
        return this.jdbcTemplate.queryForObject(sql, mapper, new Object[]{id});
    }

    private static final class TranslateMapper implements RowMapper<TranslatedStringData> {
        private final String schema;

        public TranslateMapper() {
            this.schema = "id, code, viet, thai, cam, lao from translated_string ";
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public TranslatedStringData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final var id = JdbcSupport.getLong(rs, "id");
            final var code = rs.getString("code");
            final var viet = rs.getString("viet");
            final var thai = rs.getString("thai");
            final var cam = rs.getString("cam");
            final var lao = rs.getString("lao");
            return new TranslatedStringData(id, code, viet, thai, lao, cam);
        }
    }
}
