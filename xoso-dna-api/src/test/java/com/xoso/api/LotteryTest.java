package com.xoso.api;

import com.xoso.autobank.dto.BankTransferInfoDto;
import com.xoso.autobank.service.BCELBankService;
import com.xoso.job.LotteryJob;
import com.xoso.lottery.data.LotteryCategoryData;
import com.xoso.lottery.data.LotteryData;
import com.xoso.lottery.data.LotteryResult;
import com.xoso.lottery.model.Lottery;
import com.xoso.lottery.model.LotterySession;
import com.xoso.lottery.model.LotteryTicket;
import com.xoso.lottery.model.SessionStatus;
import com.xoso.lottery.repository.LotteryRepository;
import com.xoso.lottery.repository.LotterySessionRepository;
import com.xoso.lottery.repository.LotteryTicketRepository;
import com.xoso.lottery.service.LotteryService;
import com.xoso.telegram.service.TelegramService;
import com.xoso.wallet.model.WalletTransactions;
import com.xoso.wallet.repository.WalletTransactionsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@SpringBootTest
public class LotteryTest {
    @Autowired
    LotteryJob lotteryJob;
    //@Autowired
    LotteryService lotteryService;
   // @Autowired
    LotteryRepository lotteryRepository;
    @Autowired
    LotterySessionRepository lotterySessionRepository;
   // @Autowired
    BCELBankService bcelBankService;
   // @Autowired
    WalletTransactionsRepository walletTransactionsRepository;

    //@Autowired
    LotteryTicketRepository lotteryTicketRepository;

    @Autowired
    TelegramService telegramService;

    //@Test
    void testLottery(){
//       lotteryJob.jobSXCTLAO();
//       System.out.println(result);
//       System.out.println(result.substring(1));
//       System.out.println(result.substring(2));
//       System.out.println(result.substring(3));
//       System.out.println(result.substring(4));
//       System.out.println(result.substring(5));
    }
    //@Test
//    void testAutoBank(){
//        System.out.println(getCurrDate());
//       List<BankTransferInfoDto>  dto = bcelBankService.queryTodayTransaction("Doantrunghoan","Mittit2018@","163120001626037001",getCurrDate());
//        System.out.println(dto.size());
//        WalletTransactions walletTransactions = walletTransactionsRepository.findFirstByContentOrderByIdDesc("DEP1900912195");
//        System.out.println(walletTransactions.getTransactionNo());
//    }

    private String getCurrDate(){
        long epochTimeMs = System.currentTimeMillis() - 20*60*60*1000; // Thời gian Epoch (milisecond)
        Date date = new Date(epochTimeMs);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    //test generate lotteyr data()
   // @Test
    void testLotterySession(){
        LotterySession session = LotterySession.builder().startTime(0).build();
        LotteryResult lotteryResult = LotteryResult.generateRandom();
        LotterySession newSession = lotteryJob.saveSessionInfo(false,session, lotteryResult);
        System.out.println(newSession.getDuration());
    }

    //@Test
    void testCheckWin(){
//        lotteryJob.quayXS(true,lotterySessionRepository.findById(29418L).orElse(null));
//        LotteryTicket ticket = lotteryTicketRepository.findById(302L).orElse(null);
//        if(ticket != null){
//            boolean ret = lotteryJob.checkWin(ticket);
//            System.out.println(ret);
//        }
//        Long tongCuoc = lotteryTicketRepository.getTotalBet(567800).orElse(0L);
//        System.out.println(tongCuoc);
//        Long totalWinDDB = lotteryTicketRepository.getTotalWinDDB("84",567800).orElse(0L);
//        System.out.println(totalWinDDB);
    }

  //  @Test
    void testTelegram(){
        telegramService.sendMessageToTelegramBot("RUT TIEN: \n"+"MA GD: 12134 \n"+"USER: Khach \n"+"TIEN: 123 KIP");
    }
}
