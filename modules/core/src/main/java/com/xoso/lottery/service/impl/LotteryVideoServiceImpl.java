package com.xoso.lottery.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xoso.commandsource.model.RequestCommandSource;
import com.xoso.commandsource.repository.RequestCommandSourceRepository;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.infrastructure.core.service.PaginationHelper;
import com.xoso.lottery.exception.CommandSourceNotFoundException;
import com.xoso.lottery.exception.LotteryVideoNotFoundException;
import com.xoso.lottery.model.LotteryVideo;
import com.xoso.lottery.repository.LotteryVideoRepository;
import com.xoso.lottery.service.LotteryVideoService;
import com.xoso.lottery.wsdto.LotteryVideoRequestWsDTO;
import com.xoso.lottery.wsdto.LotteryVideoWsDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class LotteryVideoServiceImpl implements LotteryVideoService {

    @Autowired
    private PaginationHelper paginationHelper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LotteryVideoRepository lotteryVideoRepository;
    @Autowired
    private RequestCommandSourceRepository requestCommandSourceRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Page<LotteryVideoWsDTO> retrieveAllLotteryVideo(SearchParameters searchParameters) {
        var lotteryVideoMapper = new LotteryVideoMapper();
        var paramList = new ArrayList<Object>();
        final var sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(lotteryVideoMapper.schema);

        String extraCriteria = " where 1=1 ";
        if (searchParameters != null) {
            var searchValue = searchParameters.getSearchValue();
            if (StringUtils.isNotBlank(searchValue)) {
                extraCriteria += " and ( url like ? ) ";
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
            sqlBuilder.append(" limit 1000 offset 0 ");
        }
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlBuilder.toString(), sqlBuilderNoPaging,
                paramList.toArray(), lotteryVideoMapper);
    }

    @Override
    public LotteryVideoWsDTO retrieveOne(Long id) {
        var lotteryVideoMapper = new LotteryVideoMapper();
        var sql = "select " + lotteryVideoMapper.schema()
                + " where id = ?";
        return this.jdbcTemplate.queryForObject(sql, lotteryVideoMapper, new Object[] { id });
    }

    @Override
    public ResultBuilder createLotteryVideo(LotteryVideoRequestWsDTO request) {
        var video = LotteryVideo.builder()
                .group(request.getGroup())
                .index(request.getIndex())
                .number(request.getNumber())
                .duration(request.getDuration())
                .url(request.getUrl())
                .build();
        this.lotteryVideoRepository.saveAndFlush(video);
        return new ResultBuilder().withEntityId(video.getId()).build();
    }

    @Override
    @Transactional
    public ResultBuilder checkerLotteryVideo(RequestCommandSource requestCommandSource) {
        try {
            var body = this.objectMapper.readValue(requestCommandSource.getCommandAsJson(),
                    LotteryVideoRequestWsDTO.class);
            var result = this.createLotteryVideo(body);
            requestCommandSource.setChecker(true);
            requestCommandSource.setCheckerOnDate(LocalDateTime.now());
            requestCommandSource.setResourceId(result.getEntityId());
            this.requestCommandSourceRepository.save(requestCommandSource);
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResultBuilder updateLotteryVideo(Long id, LotteryVideoRequestWsDTO request) {
        var lotteryVideo = lotteryVideoRepository.findById(id).orElseThrow(LotteryVideoNotFoundException::new);
        lotteryVideo.setGroup(request.getGroup());
        lotteryVideo.setIndex(request.getIndex());
        lotteryVideo.setNumber(request.getNumber());
        lotteryVideo.setDuration(request.getDuration());
        lotteryVideo.setUrl(request.getUrl());
        this.lotteryVideoRepository.save(lotteryVideo);
        return new ResultBuilder().withEntityId(lotteryVideo.getId()).build();
    }

    @Override
    public void deleteLotteryVideo(Long id) {
        var lotteryVideo = lotteryVideoRepository.findById(id).orElseThrow(LotteryVideoNotFoundException::new);
        this.lotteryVideoRepository.delete(lotteryVideo);
    }

    private static final class LotteryVideoMapper implements RowMapper<LotteryVideoWsDTO> {

        private final String schema;

        public LotteryVideoMapper() {
            final StringBuilder sqlQuery = new StringBuilder()
                    .append("id as id, group_video as group, index, number, duration, url ")
                    .append("from lottery_videos ");
            this.schema = sqlQuery.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public LotteryVideoWsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            var id = JdbcSupport.getLong(rs, "id");
            var group = JdbcSupport.getInteger(rs, "group");
            var index = JdbcSupport.getInteger(rs, "index");
            var number = JdbcSupport.getInteger(rs, "number");
            var duration = JdbcSupport.getLong(rs, "duration");
            var url = rs.getString("url");

            return new LotteryVideoWsDTO(id, group, index, number, duration, url);
        }
    }
}
