package com.xoso.campaign.service;

import com.xoso.campaign.wsdto.TemplateContentRequestWsDTO;
import com.xoso.campaign.wsdto.TemplateContentWsDTO;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.Page;

public interface TemplateContentService {
    Page<TemplateContentWsDTO> retrieveAll(SearchParameters searchParameters);
    TemplateContentWsDTO retrieveOne(Long id);
    ResultBuilder createTemplateContent(TemplateContentRequestWsDTO request);
    ResultBuilder updateTemplateContent(Long id, TemplateContentRequestWsDTO request);
    void deleteContent(Long id);
}
