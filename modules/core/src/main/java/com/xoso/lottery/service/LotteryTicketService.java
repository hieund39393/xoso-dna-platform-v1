package com.xoso.lottery.service;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.lottery.data.LotteryTicketData;
import com.xoso.lottery.wsdto.LotteryTicketSearchWsDTO;
import com.xoso.lottery.wsdto.LotteryTicketWsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LotteryTicketService {
    public List<LotteryTicketData> buyTicket(LotteryTicketWsDTO tickets);
    public List<LotteryTicketData> getPurchasedTicketBySessionId(long sessionId);
    public Page<LotteryTicketData> queryTicket(long lotteryId, int status, Pageable pageable);
    com.xoso.infrastructure.core.service.Page<LotteryTicketSearchWsDTO> retrieveAllLotteryTicket(SearchParameters searchParameters);
}
