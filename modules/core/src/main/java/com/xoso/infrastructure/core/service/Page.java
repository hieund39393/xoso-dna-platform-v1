package com.xoso.infrastructure.core.service;

import java.io.Serializable;
import java.util.List;

public class Page<E> implements Serializable {

    private final int totalFilteredRecords;
    private final List<E> pageItems;

    public Page(final List<E> pageItems, final int totalFilteredRecords) {
        this.pageItems = pageItems;
        this.totalFilteredRecords = totalFilteredRecords;
    }

    public int getTotalFilteredRecords() {
        return this.totalFilteredRecords;
    }

    public List<E> getPageItems() {
        return this.pageItems;
    }
}
