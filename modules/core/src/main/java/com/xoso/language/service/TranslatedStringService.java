package com.xoso.language.service;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.language.data.TranslatedStringData;
import com.xoso.language.wsdto.TranslatedRequestWsDTO;

import java.util.List;

public interface TranslatedStringService {
    List<TranslatedStringData> retreiveAll();
    TranslatedStringData create(TranslatedRequestWsDTO request);
    TranslatedStringData update(Long id, TranslatedRequestWsDTO request);
    Page<TranslatedStringData> retrieveAll(SearchParameters searchParameters);
    TranslatedStringData retrieveOne(Long id);
}
