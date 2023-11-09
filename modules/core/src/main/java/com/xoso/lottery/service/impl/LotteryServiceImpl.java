package com.xoso.lottery.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.xoso.commandsource.model.RequestCommandSource;
import com.xoso.commandsource.repository.RequestCommandSourceRepository;
import com.xoso.infrastructure.constant.ConstantCommon;
import com.xoso.infrastructure.core.exception.BadRequestException;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.exception.ExceptionCode;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.infrastructure.core.service.PaginationHelper;
import com.xoso.infrastructure.language.MessageUtils;
import com.xoso.job.BankJob;
import com.xoso.job.LotteryJob;
import com.xoso.lottery.data.*;
import com.xoso.lottery.exception.LotteryCategoryNotFoundException;
import com.xoso.lottery.exception.LotteryNotFoundException;
import com.xoso.lottery.model.TypeCode;
import com.xoso.lottery.repository.LotteryCategoryRepository;
import com.xoso.lottery.service.LotteryService;
import com.xoso.lottery.wsdto.LotteryRequestWsDTO;
import com.xoso.lottery.wsdto.LotteryWsDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.xoso.lottery.model.Lottery;
import com.xoso.lottery.model.LotterySession;
import com.xoso.lottery.model.SessionStatus;
import com.xoso.lottery.repository.LotteryRepository;
import com.xoso.lottery.repository.LotterySessionRepository;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Service
public class LotteryServiceImpl implements LotteryService {
    Logger logger = LoggerFactory.getLogger(LotteryService.class);
    private final PaginationHelper paginationHelper;
    private final JdbcTemplate jdbcTemplate;
    private final LotteryRepository lotteryRepository;
    private final LotteryCategoryRepository lotteryCategoryRepository;
    private final RequestCommandSourceRepository requestCommandSourceRepository;
    @Autowired
    private ObjectMapper objectMapper;

    LotteryJob lotteryJob;

    @Autowired
    LotterySessionRepository lotterySessionRepository;
    public LotteryServiceImpl(PaginationHelper paginationHelper, JdbcTemplate jdbcTemplate, LotteryRepository lotteryRepository,
                              LotteryCategoryRepository lotteryCategoryRepository, RequestCommandSourceRepository requestCommandSourceRepository,
                              LotteryJob lotteryJob) {
        this.paginationHelper = paginationHelper;
        this.jdbcTemplate = jdbcTemplate;
        this.lotteryRepository = lotteryRepository;
        this.lotteryCategoryRepository = lotteryCategoryRepository;
        this.requestCommandSourceRepository = requestCommandSourceRepository;
        this.lotteryJob = lotteryJob;
    }

    @Override
    public Page<LotteryWsDTO> retrieveAllLottery(SearchParameters searchParameters) {
        var language = MessageUtils.getLanguage();
        var lotteryMapper = new LotteryMapper(language);
        var paramList = new ArrayList<Object>();
        final var sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(lotteryMapper.schema);

        String extraCriteria = " where 1=1 ";
        if (searchParameters != null) {
            var types = searchParameters.getTypes();
            var searchValue = searchParameters.getSearchValue();
            if (StringUtils.isNotBlank(searchValue)) {
                extraCriteria += " and ( l.code like ? or l.name like ? )";
                var str = "%" + searchValue + "%";
                paramList.add(str);
                paramList.add(str);
            }

            if (!CollectionUtils.isEmpty(types)) {
                StringBuilder sqlType = new StringBuilder("(");
                var index = 0;
                for (String type : types) {
                    if (index == types.size() - 1) {
                        sqlType.append(String.format("%s%s%s", "'", type, "'"));
                    } else {
                        sqlType.append(String.format("%s%s%s%s", "'", type, "'", ","));
                    }
                    index++;
                }
                sqlType.append(")");
                extraCriteria += " and l.type in " + sqlType;
            }
        }
        sqlBuilder.append(extraCriteria);
        sqlBuilder.append(" order by l.id desc ");
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
                paramList.toArray(), lotteryMapper);
    }

    @Override
    public LotteryWsDTO retrieveOneLottery(Long id) {
        var language = MessageUtils.getLanguage();
        var lotteryMapper = new LotteryMapper(language);
        var sql = "select " + lotteryMapper.schema()
                + " where l.id = ?";
        return this.jdbcTemplate.queryForObject(sql, lotteryMapper, new Object[]{id});
    }

    private static final class LotteryMapper implements RowMapper<LotteryWsDTO> {

        private final String language;
        private final String schema;

        public LotteryMapper(String language) {
            final StringBuilder sqlQuery = new StringBuilder()
                    .append("l.id as id, l.code as code, l.name as name, l.type as type, l.modes as modes, ")
                    .append("l.hour as hour, l.min as min, l.sec as sec, l.active as active,l.vip as vip, ")
                    .append("lc.code as categoryCode, lc.name as categoryName, lc.id as categoryId ")
                    .append("from lottery l ")
                    .append("join lottery_category lc on lc.id = l.category_id ");
            this.schema = sqlQuery.toString();
            this.language = language;
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public LotteryWsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            final var id = JdbcSupport.getLong(rs, "id");
            final var code = rs.getString("code");
            final var name = rs.getString("name");
            final var type = rs.getString("type");
            final var modes = rs.getString("modes");
            var hour = rs.getInt("hour");
            var min = rs.getInt("min");
            var sec = rs.getInt("sec");
            var active = rs.getBoolean("active");
            var vip = rs.getBoolean("vip");
            final var categoryCode = rs.getString("categoryCode");
            final var categoryName = rs.getString("categoryName");
            final var categoryId = JdbcSupport.getLong(rs, "categoryId");
            return new LotteryWsDTO(id, code, name, type, modes, hour, min, sec, active,vip,
                    categoryCode, categoryName, categoryId);
        }
    }

    @Override
    public List<LotteryCategoryData> getAllCategories() {
        List<Lottery> lotteries = lotteryRepository.findAll();
        HashMap<Long,LotteryCategoryData> categories = new HashMap<>();
        lotteries.sort((o1, o2) -> o1.getHour()*60*60+o1.getMin()*60*60+o1.getSec() - o2.getHour()*60*60+o2.getMin()*60*60+o2.getSec());

        lotteries.forEach(lottery -> {
            if(!categories.containsKey(lottery.getCategory().getId())){
                List<LotteryData> lotteryDatas = new ArrayList<>();
                lotteryDatas.add(LotteryData.fromEntity(lottery));
                categories.put(lottery.getCategory().getId(), LotteryCategoryData.builder()
                        .categoryName(lottery.getCategory().getName())
                        .lotteries(lotteryDatas)
                        .build());
            } else {
                //category co ton tai
                LotteryCategoryData categoryData = categories.get(lottery.getCategory().getId());
                categoryData.getLotteries().add(LotteryData.fromEntity(lottery));
                categories.put(lottery.getCategory().getId(),categoryData);
            }
        });


        return new ArrayList<>(categories.values());
    }
    @Override
    public List<LotteryHotData> getLotteryHot(){
        List<Lottery> lotteries = lotteryRepository.findAllHotLotteries();
        return lotteries.stream().map(lottery -> {
//            Long lotteryId = lottery.getId();
//            LotterySession latestSession = lotterySessionRepository.findFirstByLotteryIdOrderByIdDesc(lotteryId);
//            LotterySession doneSession = lotterySessionRepository.findFirstByLotteryIdAndStatusOrderByIdDesc(lotteryId,SessionStatus.DONE);
            return LotteryHotData.builder()
                    .id(lottery.getId())
                    .name(lottery.getCategory().getName())
                    .result(lottery.getDoneResult0())
                    .startTime(lottery.getStartTime())
                    .nextTime(lottery.getNextTime())
                    .currentTime(System.currentTimeMillis())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public LotteryDetailData getLotteryDetail(Long lotteryId, int testCase) {
        Lottery lottery = lotteryRepository.findById(lotteryId).orElse(null);
        if (lottery == null)
            throw new BadRequestException(ExceptionCode.LOTTERY_INVALID);

        LotterySession latestSession = lotterySessionRepository.findFirstByLotteryIdOrderByIdDesc(lotteryId);

        List<VideoData> videoData = new ArrayList<>();

        if (latestSession == null)
            return null;
        if(latestSession.getVideos() != null && !latestSession.getVideos().isEmpty()){
            Gson gson = new Gson();
            Type type = new com.google.gson.reflect.TypeToken<List<VideoData>>() {}.getType();
            videoData = gson.fromJson(latestSession.getVideos(), type);
        }
        return LotteryDetailData.builder()
                .id(lotteryId)
                .sessionId(latestSession.getId())
                .status(latestSession.getStatus())
                .currentTime(System.currentTimeMillis())
                .supportedModes(lottery.getModes())
                .history(null)
                .nextTime(latestSession.getStartTime())
                .startTime(latestSession.getStartTime())
                .duration(latestSession.getDuration())
                .videos(videoData)
                .doneResult0(lottery.getDoneResult0())
                .result0(latestSession.getResult_0())
                .result1(latestSession.getResult_1())
                .result2(latestSession.getResult_2())
                .result3(latestSession.getResult_3())
                .result4(latestSession.getResult_4())
                .result5(latestSession.getResult_5())
                .result6(latestSession.getResult_6())
                .result7(latestSession.getResult_7())
                .build();
    }
    public ResultBuilder createLottery(LotteryRequestWsDTO request) {
        var category = this.lotteryCategoryRepository.findById(request.getLotteryCategoryId())
                .orElseThrow(LotteryCategoryNotFoundException::new);
        var type = TypeCode.valueOf(request.getType());
        var lottery = Lottery.builder().code(request.getCode())
                .name(request.getName())
                .category(category)
                .modes(StringUtils.isNotBlank(request.getModes()) ? String.join("-",request.getModes().split(",")) : null)
                .type(type)
                .hour(request.getHour())
                .min(request.getMin())
                .sec(request.getSec())
                .active(request.isActive())
                .vip(request.isVip())
                .totalMasterWin(BigDecimal.ZERO)
                .build();
        this.lotteryRepository.saveAndFlush(lottery);
        return new ResultBuilder().withEntityId(lottery.getId()).build();
    }

    @Override
    public ResultBuilder checkerCreateLottery(RequestCommandSource commandSource) {
        try {
            var request = this.objectMapper.readValue(commandSource.getCommandAsJson(),
                    LotteryRequestWsDTO.class);
            var result = this.createLottery(request);
            commandSource.setChecker(true);
            commandSource.setCheckerOnDate(LocalDateTime.now());
            commandSource.setResourceId(result.getEntityId());
            this.requestCommandSourceRepository.save(commandSource);
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResultBuilder updateLottery(Long id, LotteryRequestWsDTO request) {
        var lottery = this.lotteryRepository.findById(id).orElseThrow(LotteryNotFoundException::new);
        if (request.getLotteryCategoryId() != null) {
            var category = this.lotteryCategoryRepository.findById(request.getLotteryCategoryId())
                    .orElseThrow(LotteryCategoryNotFoundException::new);
            lottery.setCategory(category);
        }
        if (StringUtils.isNotBlank(request.getCode())) {
            lottery.setCode(request.getCode());
        }
        if (StringUtils.isNotBlank(request.getName())) {
            lottery.setName(request.getName());
        }
        if (StringUtils.isNotBlank(request.getModes())) {
            lottery.setModes(String.join("-",request.getModes().split(",")));
        }
        if (StringUtils.isNotBlank(request.getType())) {
            var type = TypeCode.valueOf(request.getType());
            lottery.setType(type);
        }
        lottery.setHour(request.getHour());
        lottery.setMin(request.getMin());
        lottery.setSec(request.getSec());
        lottery.setActive(request.isActive());
        lottery.setVip(request.isVip());
        this.lotteryRepository.save(lottery);
        return new ResultBuilder().withEntityId(lottery.getId()).build();
    }

//    @Override
//    public Page<LotteryHistoryData> getHistory(Long id, Pageable pageable) {
//        Page<LotterySession> histories = lotterySessionRepository.getLotterySessionsByLotteryId(id, pageable);
//        return histories.stream().map(LotteryHistoryData::fromEntity).collect(Collectors.toList());
//    }

    @Override
    public org.springframework.data.domain.Page<LotteryHistoryData> getHistory(Long id, Pageable pageable) {
        org.springframework.data.domain.Page<LotterySession> histories = lotterySessionRepository.getLotterySessionsByLotteryId(id, pageable);
        return histories.map(LotteryHistoryData::fromEntity);
    }

    @Override
    public void setupResult(Long id, String result) {
        logger.error("setup lottery manual: "+result);
        Lottery lottery = lotteryRepository.findById(id).orElseThrow(()->new BadRequestException(ExceptionCode.LOTTERY_INVALID));
        if(!(lottery.isXSCT() && lottery.getCategory().getCode().equals( ConstantCommon.XSLAO)))
            throw new BadRequestException(ExceptionCode.LOTTERY_INVALID);
        boolean ret = lotteryJob.jobSXCTLAO(result);
        if(!ret){
            logger.error("setup lottery manual failed");
            throw new BadRequestException(ExceptionCode.LOTTERY_INVALID);
        }
    }
}
