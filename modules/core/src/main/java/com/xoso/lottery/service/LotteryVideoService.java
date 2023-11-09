package com.xoso.lottery.service;

import com.xoso.commandsource.model.RequestCommandSource;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.lottery.wsdto.LotteryVideoRequestWsDTO;
import com.xoso.lottery.wsdto.LotteryVideoWsDTO;

public interface LotteryVideoService {
    Page<LotteryVideoWsDTO> retrieveAllLotteryVideo(SearchParameters searchParameters);
    LotteryVideoWsDTO retrieveOne(Long id);
    ResultBuilder createLotteryVideo(LotteryVideoRequestWsDTO request);
    ResultBuilder checkerLotteryVideo(RequestCommandSource requestCommandSource);
    ResultBuilder updateLotteryVideo(Long id, LotteryVideoRequestWsDTO request);
    void deleteLotteryVideo(Long id);
}
