package com.xoso.lottery.service.impl;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.infrastructure.core.service.PaginationHelper;
import com.xoso.lottery.exception.LotteryModeNotFoundException;
import com.xoso.lottery.model.LotteryMode;
import com.xoso.lottery.model.ModeCode;
import com.xoso.lottery.repository.LotteryModeRepository;
import com.xoso.lottery.service.LotteryModeService;
import com.xoso.lottery.wsdto.LotteryModeRequestWsDTO;
import com.xoso.lottery.wsdto.LotteryModeWsDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class LotteryModeServiceImpl implements LotteryModeService {

    private final PaginationHelper paginationHelper;
    private final JdbcTemplate jdbcTemplate;
    private final LotteryModeRepository lotteryModeRepository;

    @Autowired
    public LotteryModeServiceImpl(PaginationHelper paginationHelper, JdbcTemplate jdbcTemplate, LotteryModeRepository lotteryModeRepository) {
        this.paginationHelper = paginationHelper;
        this.jdbcTemplate = jdbcTemplate;
        this.lotteryModeRepository = lotteryModeRepository;
    }

    @Override
    public Page<LotteryModeWsDTO> retrieveAllLotteryMode(SearchParameters searchParameters) {
        var lotteryModeMapper = new LotteryModeMapper();
        var paramList = new ArrayList<Object>();
        final var sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(lotteryModeMapper.schema);

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
                paramList.toArray(), lotteryModeMapper);
    }

    @Override
    public LotteryModeWsDTO retrieveOneLotteryMode(Long id) {
        var lotteryModeMapper = new LotteryModeMapper();
        var sql = "select " + lotteryModeMapper.schema()
                + " where id = ?";
        return this.jdbcTemplate.queryForObject(sql, lotteryModeMapper, new Object[] { id });
    }

    @Override
    public ResultBuilder createLotteryMode(LotteryModeRequestWsDTO request) {
        var code = ModeCode.valueOf(request.getCode());
        var lotteryMode = LotteryMode.builder().code(code)
                .name(request.getName())
                .price(request.getPrice())
                .prizeMoney(request.getPrizeMoney()).build();
        this.lotteryModeRepository.saveAndFlush(lotteryMode);
        return new ResultBuilder().withEntityId(lotteryMode.getId()).build();
    }

    @Override
    public ResultBuilder updateLotteryMode(Long id, LotteryModeRequestWsDTO request) {
        var lotteryMode = this.lotteryModeRepository.findById(id).orElseThrow(LotteryModeNotFoundException::new);
        if (StringUtils.isNotBlank(request.getCode())) {
            var code = ModeCode.valueOf(request.getCode());
            lotteryMode.setCode(code);
        }
        if (StringUtils.isNotBlank(request.getName())) {
            lotteryMode.setName(request.getName());
        }
        lotteryMode.setPrice(request.getPrice());
        lotteryMode.setPrizeMoney(request.getPrizeMoney());
        this.lotteryModeRepository.save(lotteryMode);
        return new ResultBuilder().withEntityId(lotteryMode.getId()).build();
    }

    private static final class LotteryModeMapper implements RowMapper<LotteryModeWsDTO> {
        private final String schema;

        public LotteryModeMapper() {
            final StringBuilder sqlQuery = new StringBuilder()
                    .append("id, code, name, price, prize_money as prizeMoney ")
                    .append("from lottery_mode ");
            this.schema = sqlQuery.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public LotteryModeWsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            final var id = JdbcSupport.getLong(rs, "id");
            final var code = rs.getString("code");
            final var price = rs.getLong("price");
            final var prizeMoney = rs.getLong("prizeMoney");
            final var name =  rs.getString("name");
            return new LotteryModeWsDTO(id, code, name, price, prizeMoney);
        }
    }
}
