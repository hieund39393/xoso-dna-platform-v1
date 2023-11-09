package com.xoso.infrastructure.core.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchParameters {

    private String sqlSearch;
    private String searchValue;
    private List<String> status;
    private List<String> types;
    private List<Long> oneServices;
    private Integer offset;
    private Integer limit;
    private String orderBy;
    private String sortOrder;

    private Date fromDate;
    private Date toDate;

    private Long walletId;
    private Long orderId;
    private String orderCode;
    private Long userId;
    private Boolean deleted;
    private Boolean active;
    private Boolean forClient;
    private String locale;
    private String category;
    private Integer statusInt;
    private List<String> modeCodes;
    private String lotteryCode;

    public SearchParameters(Integer offset, Integer limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public SearchParameters(Integer offset, Integer limit, String searchValue) {
        this.offset = offset;
        this.limit = limit;
        this.searchValue = searchValue;
    }

    public SearchParameters(Integer offset, Integer limit, String searchValue, List<String> status) {
        this.offset = offset;
        this.limit = limit;
        this.searchValue = searchValue;
        this.status = status;
    }

    public SearchParameters(Integer offset, Integer limit, String searchValue, List<String> status, List<String> types, List<Long> oneServices) {
        this.offset = offset;
        this.limit = limit;
        this.searchValue = searchValue;
        this.status = status;
        this.oneServices = oneServices;
    }

    public SearchParameters(Integer offset, Integer limit, String searchValue, List<String> status, List<String> types) {
        this.offset = offset;
        this.limit = limit;
        this.searchValue = searchValue;
        this.status = status;
        this.types = types;
    }

    public SearchParameters(Integer offset, Integer limit, String searchValue, List<String> status, Date fromDate, Date toDate) {
        this.offset = offset;
        this.limit = limit;
        this.searchValue = searchValue;
        this.status = status;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public SearchParameters(Integer offset, Integer limit, Long walletId) {
        this.offset = offset;
        this.limit = limit;
        this.walletId = walletId;
    }

    public SearchParameters(Long orderId) {
        this.orderId = orderId;
    }

    public boolean isLimited() {
        return this.limit != null && this.limit.intValue() > 0;
    }

    public boolean isOffset() {
        return this.offset != null && this.offset.intValue() > 0;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getForClient() {
        return forClient;
    }

    public void setForClient(Boolean forClient) {
        this.forClient = forClient;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }



}
