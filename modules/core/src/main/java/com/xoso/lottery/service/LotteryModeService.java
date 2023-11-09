package com.xoso.lottery.service;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.lottery.wsdto.LotteryModeRequestWsDTO;
import com.xoso.lottery.wsdto.LotteryModeWsDTO;

public interface LotteryModeService {
    Page<LotteryModeWsDTO> retrieveAllLotteryMode(SearchParameters searchParameters);
    LotteryModeWsDTO retrieveOneLotteryMode(Long id);
    ResultBuilder createLotteryMode(LotteryModeRequestWsDTO request);
    ResultBuilder updateLotteryMode(Long id, LotteryModeRequestWsDTO request);
}
