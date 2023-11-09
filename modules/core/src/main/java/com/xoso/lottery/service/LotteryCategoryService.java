package com.xoso.lottery.service;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.lottery.wsdto.LotteryCategoryRequestWsDTO;
import com.xoso.lottery.wsdto.LotteryCategoryWsDTO;

public interface LotteryCategoryService {
    Page<LotteryCategoryWsDTO> retrieveAllLotteryCategory(SearchParameters searchParameters);
    LotteryCategoryWsDTO retrieveOneLotteryCategory(Long id);
    ResultBuilder createLotteryCategory(LotteryCategoryRequestWsDTO request);
    ResultBuilder updateLotteryCategory(Long id, LotteryCategoryRequestWsDTO request);
}
