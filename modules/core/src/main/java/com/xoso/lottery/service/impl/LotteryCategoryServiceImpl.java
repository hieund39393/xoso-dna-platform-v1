package com.xoso.lottery.service.impl;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.infrastructure.core.service.PaginationHelper;
import com.xoso.lottery.exception.LotteryCategoryNotFoundException;
import com.xoso.lottery.model.LotteryCategory;
import com.xoso.lottery.repository.LotteryCategoryRepository;
import com.xoso.lottery.service.LotteryCategoryService;
import com.xoso.lottery.wsdto.LotteryCategoryRequestWsDTO;
import com.xoso.lottery.wsdto.LotteryCategoryWsDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class LotteryCategoryServiceImpl implements LotteryCategoryService {

    private final PaginationHelper paginationHelper;
    private final JdbcTemplate jdbcTemplate;
    private final LotteryCategoryRepository lotteryCategoryRepository;

    @Autowired
    public LotteryCategoryServiceImpl(PaginationHelper paginationHelper, JdbcTemplate jdbcTemplate, LotteryCategoryRepository lotteryCategoryRepository) {
        this.paginationHelper = paginationHelper;
        this.jdbcTemplate = jdbcTemplate;
        this.lotteryCategoryRepository = lotteryCategoryRepository;
    }

    @Override
    public Page<LotteryCategoryWsDTO> retrieveAllLotteryCategory(SearchParameters searchParameters) {
        var lotteryCategoryMapper = new LotteryCategoryMapper();
        var paramList = new ArrayList<Object>();
        final var sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(lotteryCategoryMapper.schema);

        String extraCriteria = " where 1=1 ";
        if (searchParameters != null) {
            var searchValue = searchParameters.getSearchValue();
            if (StringUtils.isNotBlank(searchValue)) {
                extraCriteria += " and (code like ? or name like ? ) ";
                var str = "%" + searchValue + "%";
                paramList.add(str);
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
            sqlBuilder.append(" limit 1000 offset 0 ");
        }
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlBuilder.toString(), sqlBuilderNoPaging,
                paramList.toArray(), lotteryCategoryMapper);
    }

    @Override
    public LotteryCategoryWsDTO retrieveOneLotteryCategory(Long id) {
        var lotteryCategoryMapper = new LotteryCategoryMapper();
        var sql = "select " + lotteryCategoryMapper.schema()
                + " where id = ?";
        return this.jdbcTemplate.queryForObject(sql, lotteryCategoryMapper, new Object[] { id });
    }

    @Override
    public ResultBuilder createLotteryCategory(LotteryCategoryRequestWsDTO request) {
        var lotteryCategory = LotteryCategory.builder()
                .code(request.getCode())
                .name(request.getName())
                .enable_campaign(request.isCampaign())
                .active(request.isActive()).build();
        this.lotteryCategoryRepository.saveAndFlush(lotteryCategory);
        return new ResultBuilder().withEntityId(lotteryCategory.getId()).build();
    }

    @Override
    public ResultBuilder updateLotteryCategory(Long id, LotteryCategoryRequestWsDTO request) {
        var lotteryCategory = this.lotteryCategoryRepository.findById(id).orElseThrow(LotteryCategoryNotFoundException::new);
        if (StringUtils.isNotBlank(request.getCode())) {
            lotteryCategory.setCode(request.getCode());
        }
        if (StringUtils.isNotBlank(request.getName())) {
            lotteryCategory.setName(request.getName());
        }
        lotteryCategory.setActive(request.isActive());
        lotteryCategory.setEnable_campaign(request.isCampaign());
        this.lotteryCategoryRepository.save(lotteryCategory);
        return new ResultBuilder().withEntityId(lotteryCategory.getId()).build();
    }

    private static final class LotteryCategoryMapper implements RowMapper<LotteryCategoryWsDTO> {

        private final String schema;

        public LotteryCategoryMapper() {
            final StringBuilder sqlQuery = new StringBuilder()
                    .append("id, code, name, active, enable_campaign from lottery_category ");
            this.schema = sqlQuery.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public LotteryCategoryWsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            final var id = JdbcSupport.getLong(rs, "id");
            final var code = rs.getString("code");
            final var name =  rs.getString("name");
            final var active =  rs.getBoolean("active");
            final var campaign =  rs.getBoolean("enable_campaign");
            return new LotteryCategoryWsDTO(id, code, name, active, campaign);
        }
    }
}
