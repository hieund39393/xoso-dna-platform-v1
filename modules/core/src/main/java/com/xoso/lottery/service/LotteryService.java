package com.xoso.lottery.service;

import com.xoso.bank.data.BankData;
import com.xoso.commandsource.model.RequestCommandSource;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.lottery.data.LotteryCategoryData;
import com.xoso.lottery.data.LotteryDetailData;
import com.xoso.lottery.data.LotteryHistoryData;
import com.xoso.lottery.data.LotteryHotData;
import com.xoso.lottery.wsdto.LotteryRequestWsDTO;
import com.xoso.lottery.wsdto.LotteryWsDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LotteryService {
    Page<LotteryWsDTO> retrieveAllLottery(SearchParameters searchParameters);
    LotteryWsDTO retrieveOneLottery(Long id);
    List<LotteryCategoryData> getAllCategories();
    LotteryDetailData getLotteryDetail(Long lotteryId, int testCase);
    ResultBuilder createLottery(LotteryRequestWsDTO request);
    ResultBuilder checkerCreateLottery(RequestCommandSource commandSource);
    ResultBuilder updateLottery(Long id, LotteryRequestWsDTO request);
    org.springframework.data.domain.Page<LotteryHistoryData> getHistory(Long id, Pageable pageable);
    List<LotteryHotData> getLotteryHot();
    void setupResult(Long id, String result);
}
