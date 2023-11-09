package com.xoso.agency.service;

import com.xoso.agency.data.AgencyState;
import com.xoso.agency.data.AgencyUser;
import com.xoso.agency.wsdto.AgencyCreateWsDTO;
import com.xoso.agency.wsdto.AgencyRequestWsDTO;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.Page;

public interface AgencyService {
    AgencyState register(AgencyCreateWsDTO request);
    AgencyState getState();
    AgencyState approve(Long id);
    AgencyState reject(Long id, String reason);
    Page<AgencyRequestWsDTO> retrieveAllAgencyRequest(SearchParameters searchParameters);
    AgencyRequestWsDTO retrieveOneAgencyRequest(Long id);

    org.springframework.data.domain.Page<AgencyUser> getUserList(int pageNumber, int pageSize, String searchValue);
}
