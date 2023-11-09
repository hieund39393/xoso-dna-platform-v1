package com.xoso.campaign.service;

import com.xoso.campaign.model.TemplateContent;
import com.xoso.campaign.repository.TemplateContentRepository;
import com.xoso.campaign.wsdto.TemplateContentRequestWsDTO;
import com.xoso.campaign.wsdto.TemplateContentWsDTO;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.infrastructure.core.service.PaginationHelper;
import com.xoso.lottery.exception.LotteryVideoNotFoundException;
import com.xoso.lottery.model.LanguageEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

@Service
public class TemplateContentServiceImpl implements TemplateContentService {

    @Autowired
    private PaginationHelper paginationHelper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TemplateContentRepository templateContentRepository;

    @Override
    public Page<TemplateContentWsDTO> retrieveAll(SearchParameters searchParameters) {
        boolean searchBase = false;
        if (Boolean.TRUE.equals(searchParameters.getForClient())) {
            searchBase = true;
        }
        var templateContentMapper = new TemplateContentMapper(searchBase);
        var paramList = new ArrayList<Object>();
        final var sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(templateContentMapper.schema);

        String extraCriteria = " where 1=1 ";
        var searchValue = searchParameters.getSearchValue();
        if (StringUtils.isNotBlank(searchValue)) {
            extraCriteria += " and ( code like ? or name like ? ) ";
            var str = "%" + searchValue + "%";
            paramList.add(str);
            paramList.add(str);
        }

        if (searchBase) {
            extraCriteria += " and active = ? ";
            paramList.add(true);
//            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//            var date = new Date();
//            String dateStr = df.format(date);
//            extraCriteria += " and start_date <=  '" + dateStr + "' and end_date >= '" + dateStr + "'";
            var localeStr = searchParameters.getLocale();
            if (StringUtils.isNotBlank(localeStr)) {
                try {
                    var locale = LanguageEnum.valueOf(localeStr.toUpperCase());
                    extraCriteria += " and language = ? ";
                    paramList.add(localeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                }
            }
            var category = searchParameters.getCategory();
            if (StringUtils.isNotBlank(category)) {
                extraCriteria += " and category = ? ";
                paramList.add(category);
            }
        }
        sqlBuilder.append(extraCriteria);
        var sqlBuilderNoPaging = sqlBuilder.toString();
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getLimit()*searchParameters.getOffset());
            }
        } else {
            sqlBuilder.append(" limit 100 offset 0 ");
        }
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlBuilder.toString(), sqlBuilderNoPaging,
                paramList.toArray(), templateContentMapper);
    }

    @Override
    public void deleteContent(Long id) {
        var content = templateContentRepository.findById(id).orElseThrow(LotteryVideoNotFoundException::new);
        this.templateContentRepository.delete(content);
    }

    @Override
    public TemplateContentWsDTO retrieveOne(Long id) {
        var templateContentMapper = new TemplateContentMapper(false);
        var sql = "select " + templateContentMapper.schema()
                + " where id = ?";
        return this.jdbcTemplate.queryForObject(sql, templateContentMapper, new Object[] { id });
    }

    @Override
    public ResultBuilder createTemplateContent(TemplateContentRequestWsDTO request) {
        var language = LanguageEnum.valueOf(request.getLanguage());
        var templateContent = TemplateContent.builder()
                .name(request.getName())
                //.code(request.getCode())
                .code(request.getCategory())
                .language(language)
                .category(request.getCategory())
                .html(request.getHtml())
                .banner(request.getBanner())
                .active(true)
//                .startDate(request.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
//                .endDate(request.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .build();
        this.templateContentRepository.saveAndFlush(templateContent);
        return ResultBuilder.builder().entityId(templateContent.getId()).build();
    }

    @Override
    public ResultBuilder updateTemplateContent(Long id, TemplateContentRequestWsDTO request) {
        var templateContent = this.templateContentRepository.queryFindById(id);

        if (request.getName() != null && !request.getName().isEmpty()) {
            templateContent.setName(request.getName());
        }
        if (request.getLanguage() != null && !request.getLanguage().isEmpty()) {
            var language = LanguageEnum.valueOf(request.getLanguage());
            templateContent.setLanguage(language);
        }
        if (StringUtils.isNotBlank(request.getHtml())) {
            templateContent.setHtml(request.getHtml());
        }
        if (StringUtils.isNotBlank(request.getBanner())) {
            templateContent.setBanner(request.getBanner());
        }
//        if (request.getStartDate() != null) {
//            templateContent.setStartDate(request.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//        }
//        if (request.getEndDate() != null) {
//            templateContent.setEndDate(request.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//        }
        templateContent.setActive(request.isActive());
        this.templateContentRepository.saveAndFlush(templateContent);
        return ResultBuilder.builder().entityId(templateContent.getId()).build();
    }

    private static final class TemplateContentMapper implements RowMapper<TemplateContentWsDTO> {

        private final String schema;
        private final boolean searchBase;

        public TemplateContentMapper(boolean searchBase) {
            final StringBuilder sqlQuery = new StringBuilder()
                    .append("id, code, name, banner, category, html, language, start_date as startDate, end_date as endDate, active ")
                    .append("from template_content ");
            this.schema = sqlQuery.toString();
            this.searchBase = searchBase;
        }
        public String schema() {
            return this.schema;
        }

        @Override
        public TemplateContentWsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            var id = JdbcSupport.getLong(rs, "id");
            var name = rs.getString("name");
            var code = rs.getString("code");
            var banner = rs.getString("banner");
            var active = rs.getBoolean("active");
            String category = null;
            String language = null;
            String html = null;
            String startDateStr = "";
            String endDateStr = "";
            if (!searchBase) {
                html = rs.getString("html");
                language = rs.getString("language");
                category = rs.getString("category");
                final var startDate = JdbcSupport.getLocalDate(rs, "startDate");

                if (startDate != null) {
                    var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    startDateStr = formatter.format(startDate);
                }

                final var endDate = JdbcSupport.getLocalDate(rs, "endDate");
                if (startDate != null) {
                    var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    endDateStr = formatter.format(endDate);
                }
            }

            return new TemplateContentWsDTO(id, name, code, banner, category, html, language, startDateStr, endDateStr, active, null);
        }
    }
}
