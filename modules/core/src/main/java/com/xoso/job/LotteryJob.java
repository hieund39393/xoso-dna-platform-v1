package com.xoso.job;

import com.google.gson.Gson;
import com.xoso.infrastructure.constant.ConstantCommon;
import com.xoso.lottery.data.LotteryResult;
import com.xoso.lottery.data.VideoData;
import com.xoso.lottery.model.*;
import com.xoso.lottery.repository.*;
import com.xoso.wallet.service.WalletService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LotteryJob {
    Logger logger = LoggerFactory.getLogger(LotteryJob.class);
    @Autowired
    LotteryRepository lotteryRepository;
    @Autowired
    LotteryCategoryRepository lotteryCategoryRepository;
    @Autowired
    LotterySessionRepository lotterySessionRepository;
    @Autowired
    LotteryTicketRepository lotteryTicketRepository;
    @Autowired
    LotteryVideoRepository lotteryVideoRepository;
    @Autowired
    WalletService walletService;
    //Ti le lãi của nhà cái
    private static int PROFIT_RATE = 5; //tinh theo %

    //Job xo so nhanh
    @Transactional
    public void jobXSN(){
        //get tat ca cac lottery
        List<Lottery> lotteries = lotteryRepository.findAllActiveLotteries().stream().filter(lottery -> lottery.isXSN()).collect(Collectors.toList());
        if(lotteries.isEmpty())
            return;

        lotteries.forEach(lottery -> {
            //kiem tra xem lottery nao chua co session thi tao session cho no
            LotterySession lotterySession = lotterySessionRepository.findFirstByLotteryIdOrderByIdDesc(lottery.getId());
            if(lotterySession == null){
                //chua co session nao-> tao session moi
                LotterySession newSession = LotterySession.builder()
                        .lotteryId(lottery.getId())
                        .startTime(System.currentTimeMillis() + lottery.getSec()*1000)
                        .status(SessionStatus.WAITING)
                        .build();
                lotterySessionRepository.save(newSession);
            } else if(lotterySession.getStatus() == SessionStatus.WAITING
                    && lotterySession.getStartTime()< System.currentTimeMillis()){
                //neu qua thoi gian start time cua xo so -> thuc hien random so va tao session moi
                quayXS(lottery, lotterySession);
                long nextTime = System.currentTimeMillis() + lottery.getSec()*1000;
                lottery.setDoneResult0(lotterySession.getResult_0());
                lottery.setStartTime(lotterySession.getStartTime());
                lottery.setNextTime(nextTime);
                lotteryRepository.save(lottery);
                //tao session moi
                LotterySession newSession = LotterySession.builder()
                        .lotteryId(lottery.getId())
                        .startTime(nextTime)//buffer 5s
                        .status(SessionStatus.WAITING)
                        .build();
                lotterySessionRepository.saveAndFlush(newSession);
            }else if(lotterySession.getStatus() == SessionStatus.DONE){
                LotterySession newSession = LotterySession.builder()
                        .lotteryId(lottery.getId())
                        .startTime(System.currentTimeMillis() + lottery.getSec()*1000)
                        .status(SessionStatus.WAITING)
                        .build();
                lotterySessionRepository.save(newSession);
            }

        });
    }

    @Transactional
    public void jobXSCHANLE(){
        //get tat ca cac lottery
        List<Lottery> lotteries = lotteryRepository.findAllActiveLotteries().stream().filter(lottery -> lottery.isXSCHANLE()).collect(Collectors.toList());
        if(lotteries.isEmpty())
            return;

        lotteries.forEach(lottery -> {
            //kiem tra xem lottery nao chua co session thi tao session cho no
            LotterySession lotterySession = lotterySessionRepository.findFirstByLotteryIdOrderByIdDesc(lottery.getId());
            if(lotterySession == null){
                //chua co session nao-> tao session moi
                LotterySession newSession = LotterySession.builder()
                        .lotteryId(lottery.getId())
                        .startTime(System.currentTimeMillis() + lottery.getSec()*1000)
                        .status(SessionStatus.WAITING)
                        .build();
                lotterySessionRepository.save(newSession);
            } else if(lotterySession.getStatus() == SessionStatus.WAITING
                    && lotterySession.getStartTime()< System.currentTimeMillis()){
                //neu qua thoi gian start time cua xo so -> thuc hien random so va tao session moi
                quayXSCHANLE(lottery, lotterySession);
                long nextTime = System.currentTimeMillis() + lottery.getSec()*1000;
                lottery.setDoneResult0(lotterySession.getResult_0());
                lottery.setStartTime(lotterySession.getStartTime());
                lottery.setNextTime(nextTime);
                lotteryRepository.save(lottery);
                //tao session moi
                LotterySession newSession = LotterySession.builder()
                        .lotteryId(lottery.getId())
                        .startTime(nextTime)//buffer 5s
                        .status(SessionStatus.WAITING)
                        .build();
                lotterySessionRepository.saveAndFlush(newSession);
            }else if(lotterySession.getStatus() == SessionStatus.DONE){
                LotterySession newSession = LotterySession.builder()
                        .lotteryId(lottery.getId())
                        .startTime(System.currentTimeMillis() + lottery.getSec()*1000)
                        .status(SessionStatus.WAITING)
                        .build();
                lotterySessionRepository.save(newSession);
            }

        });
    }

    @Transactional
    public boolean jobSXCTLAO(String manualResult){
        Lottery lottery = lotteryRepository.findLotteriesByTypeAndCategoryCode(TypeCode.XSCT, ConstantCommon.XSLAO);
        if(lottery == null)
            return false;
        LotterySession lotterySession = lotterySessionRepository.findFirstByLotteryIdOrderByIdDesc(lottery.getId());
        if(lotterySession == null){
            //chua co thi tao session DONE
            String result = manualResult!= null?manualResult:crawlSXLao();
            if(result == null || result.length() != 6)
                return false;
            LotterySession oldSession = LotterySession.builder()
                    .lotteryId(lottery.getId())
                    .startTime(0)
                    .status(SessionStatus.DONE)
                    .build();
            oldSession.setResult_0(result);
            oldSession.setResult_1(result.substring(1));
            oldSession.setResult_2(result.substring(2));
            oldSession.setResult_3(result.substring(3));
            oldSession.setResult_4(result.substring(4));
            oldSession.setResult_5(result.substring(5));
            oldSession.setDuration(0);
            lotterySessionRepository.save(oldSession);
            //tao session moi
            long nextTime = getNextTimeSXLAO();
            LotterySession newSession = LotterySession.builder()
                    .lotteryId(lottery.getId())
                    .startTime(nextTime)
                    .status(SessionStatus.WAITING)
                    .build();
            lotterySessionRepository.save(newSession);
            lottery.setStartTime(oldSession.getStartTime());
            lottery.setNextTime(nextTime);
            lottery.setDoneResult0(oldSession.getResult_0());
            lotteryRepository.save(lottery);

        } else if(lotterySession.getStartTime()< System.currentTimeMillis()/* && lotterySession.getStatus() == SessionStatus.PROCESSING*/){
            //qua thoi gian quay so, call job crawl data
            String result = manualResult!= null?manualResult:crawlSXLao();
            if(result == null || result.length() != 6)
                return false;
            LotterySession doneSession = lotterySessionRepository.findFirstByLotteryIdAndStatusOrderByIdDesc(lottery.getId(),SessionStatus.DONE);
            if(doneSession == null || !doneSession.getResult_0().equals(result)){
                lotterySession.setResult_0(result);
                lotterySession.setResult_1(result.substring(1));
                lotterySession.setResult_2(result.substring(2));
                lotterySession.setResult_3(result.substring(3));
                lotterySession.setResult_4(result.substring(4));
                lotterySession.setResult_5(result.substring(5));
                lotterySession.setDuration(0);
                lotterySession.setStatus(SessionStatus.DONE);
                lotterySessionRepository.save(lotterySession);
                long nextTime = getNextTimeSXLAO();
                LotterySession newSession = LotterySession.builder()
                        .lotteryId(lottery.getId())
                        .startTime(nextTime)
                        .status(SessionStatus.WAITING)
                        .build();
                lotterySessionRepository.save(newSession);

                lottery.setStartTime(lotterySession.getStartTime());
                lottery.setNextTime(nextTime);
                lottery.setDoneResult0(lotterySession.getResult_0());
                lotteryRepository.save(lottery);
            }
        }
        return true;
    }

    //job so xo chinh thong
    @Transactional
    public void jobSXCTVN(){
        //get xo so viet nam
        Lottery lottery = lotteryRepository.findLotteriesByTypeAndCategoryCode(TypeCode.XSCT, ConstantCommon.XSVIE);
        if(lottery == null)
            return;
        LotterySession lotterySession = lotterySessionRepository.findFirstByLotteryIdOrderByIdDesc(lottery.getId());
        if(lotterySession == null){
            //chua co session nao-> tao session moi
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            long nextTime = getNextTimeSXBMs((now.getHour()<ConstantCommon.XSVIE_HOUR)?0:1,ConstantCommon.XSVIE_HOUR,ConstantCommon.XSVIE_MIN);
            LotterySession newSession = LotterySession.builder()
                    .lotteryId(lottery.getId())
                    .startTime(nextTime)
                    .status(SessionStatus.WAITING)
                    .build();
            lotterySessionRepository.save(newSession);
        } else if(lotterySession.getStartTime()< System.currentTimeMillis()){
            //qua thoi gian quay so, call job crawl data
            crawlXSCTVN(lottery, lotterySession);
        }
    }

    //job pay xo so nhanh
    @Transactional
    public void payJob(){
        //lay 50 ticket xo so nhanh xu ly 1 lan
        Pageable pageable = PageRequest.of(0, 100);
        List<LotteryTicket> tickets = lotteryTicketRepository.getTicketsPay(pageable);
        tickets.forEach(lotteryTicket -> {
            //kiem tra ticket win hay lose
            boolean isWin = checkWin(lotteryTicket);
            // cap nhat lai trang thai ticket
            lotteryTicket.setWin(isWin);
            lotteryTicket.setStatus(isWin?1:-1);
            lotteryTicket.setModifiedDate(LocalDateTime.now());
            lotteryTicketRepository.saveAndFlush(lotteryTicket);
            //cap nhat so tien cho user
            if(isWin){
                walletService.updateBalanceOfUserByTicket(lotteryTicket.getUserId(),lotteryTicket.getPrizeMoney());
            }
        });
    }

    //job quay ket qua xo so bip
    //ket qua can duoc quay truoc 5p truoc gio ra so
//    @Transactional
//    public void jobQuayXSB(){
//        //get tat ca cac lottery
//        List<Lottery> lotteries = lotteryRepository.findAll().stream().filter(lottery -> lottery.isSXB()).collect(Collectors.toList());
//        if(lotteries.isEmpty())
//            return;
//        lotteries.forEach(lottery -> {
//            //lay session moi nhat cua lottery
//            LotterySession lotterySession = lotterySessionRepository.findFirstByLotteryIdOrderByIdDesc(lottery.getId());
//            if(lotterySession != null && lotterySession.getStatus() == SessionStatus.WAITING
//                    && (System.currentTimeMillis()> lotterySession.getStartTime() - 5*60*1000)){
//                quaySoxoBip(lotterySession);
//            }
//        });
//    }
    @Transactional
    public void jobXSB(){
        List<Lottery> lotteries = lotteryRepository.findAll().stream().filter(lottery -> lottery.isSXB()).collect(Collectors.toList());
        if(lotteries.isEmpty())
            return;
        lotteries.forEach(lottery -> {
            //lay session moi nhat cua lottery
            LotterySession lotterySession = lotterySessionRepository.findFirstByLotteryIdOrderByIdDesc(lottery.getId());
            if(lotterySession == null || lotterySession.getStatus() == SessionStatus.DONE){
                //chua co session nao waiting-> tao session moi
                long nextTime = getNextTimeSXBMs(1,lottery.getHour(),lottery.getMin());
                LotterySession newSession = LotterySession.builder()
                        .lotteryId(lottery.getId())
                        .startTime(nextTime)
                        .status(SessionStatus.WAITING)
                        .build();
                lotterySessionRepository.save(newSession);
                lottery.setDoneResult0("00000");
                lottery.setStartTime(System.currentTimeMillis());
                lottery.setNextTime(nextTime);
                lotteryRepository.save(lottery);
            } else if(lotterySession.getStatus() == SessionStatus.PROCESSING
                    && (System.currentTimeMillis() > lotterySession.getStartTime() + lotterySession.getDuration())){
                lotterySession.setStatus(SessionStatus.DONE);
                lotterySessionRepository.saveAndFlush(lotterySession);
                long nextTime = getNextTimeSXBMs(1,lottery.getHour(),lottery.getMin());
                LotterySession newSession = LotterySession.builder()
                        .lotteryId(lottery.getId())
                        .startTime(nextTime)
                        .status(SessionStatus.WAITING)
                        .build();
                lotterySessionRepository.saveAndFlush(newSession);
                lottery.setDoneResult0(lotterySession.getResult_0());
                lottery.setStartTime(lotterySession.getStartTime());
                lottery.setNextTime(nextTime);
                lotteryRepository.save(lottery);

            } else if(lotterySession.getStatus() == SessionStatus.WAITING
                    && (System.currentTimeMillis()> lotterySession.getStartTime() - 5*60*1000)){
                quayXS(lottery, lotterySession);
            }
        });
    }
    @Transactional
    public boolean checkWin(LotteryTicket ticket){
       LotterySession session = ticket.getSession();
       if(session == null){
           System.out.println("session is null: "+ticket.getId());
           return false;
       }
       if(ticket.getCode() == ModeCode.DDB){
           //de dac biet
           if(ticket.getNumbers().equals(session.getResult_0().substring(session.getResult_0().length() - 2)))
               return true;
           return false;
       } else if(ticket.getCode() == ModeCode.DGN){
           //de giai nhat
           if(ticket.getNumbers().equals(session.getResult_1().substring(session.getResult_0().length() - 2)))
               return true;
           return false;
        } else if(ticket.getCode() == ModeCode.BCDB){
           if(ticket.getNumbers().equals(session.getResult_0().substring(session.getResult_0().length() - 3)))
               return true;
           return false;
       } else if(ticket.getCode() == ModeCode.BCGN){
           if(ticket.getNumbers() == session.getResult_0().substring(session.getResult_0().length() - 3))
               return true;
           return false;
       } else if(ticket.getCode() == ModeCode.CHANLE){
           int resultInt = Integer.parseInt(session.getResult_0().substring(session.getResult_0().length() - 4));
           if(resultInt %2 == 0){
               //ket qua chan
               return ticket.getNumbers().equals("00");
           } else
               return ticket.getNumbers().equals("01");
       }else {
           //LO, loxien 2, xien 3, xien 4
           //TODO check truong hop loxien
           if(ticket.getNumbers().length() >=2 && session.getResult_arr().contains(ticket.getNumbers()))
               return true;
           return false;
       }

       // return true;
    }
    private long getNextTimeSXBMs(int day, int hour,int min){
        if(false){
            //dev- 15 phut quay 1 lan
            return System.currentTimeMillis() + 6*60*1000;
        }else {
            LocalDate now = LocalDate.now(); // Lấy ngày hiện tại
            LocalDate epoch = LocalDate.ofEpochDay(0); // Lấy ngày Epoch time (1/1/1970)
            long daysSinceEpoch = now.toEpochDay() - epoch.toEpochDay() + day;
            Instant epc = Instant.EPOCH; // Lấy thời điểm Epoch time (1/1/1970 00:00:00 UTC)
            Instant currentInstant = epc.plus(daysSinceEpoch, ChronoUnit.DAYS).plus(hour-7, ChronoUnit.HOURS).plus(min, ChronoUnit.MINUTES); // Thêm số giờ: GMT+7
            long epochTimeInMillis = currentInstant.toEpochMilli(); // Chuyển đổi thành Epoch time tính theo milliseconds
            return epochTimeInMillis;
        }
    }
    private void quayXSCHANLE(Lottery lottery, LotterySession lotterySession){
        Long tongcuocCHANLE = lotteryTicketRepository.getTotalBetByCode(ModeCode.CHANLE,lotterySession.getId()).orElse(0L);
        if(tongcuocCHANLE == 0){
            LotteryResult lotteryResult = LotteryResult.generateRandom();
            LotterySession sesion = saveSessionInfo(true, lotterySession, lotteryResult);
            lotterySessionRepository.saveAndFlush(sesion);
            return;
        }
        boolean hasCampain = lottery.getCategory().isEnable_campaign();
        long totalWinChan = lotteryTicketRepository.getTotalWinByCode(ModeCode.CHANLE,"00",lotterySession.getId()).orElse(0L);
        long totalWinLe = lotteryTicketRepository.getTotalWinByCode(ModeCode.CHANLE,"01",lotterySession.getId()).orElse(0L);
        if(!hasCampain){
            LotteryResult lotteryResult;
            if(totalWinChan > totalWinLe)
                lotteryResult = LotteryResult.generateRandomChanLe(false);
            else
                lotteryResult = LotteryResult.generateRandomChanLe(true);
            LotterySession sesion = saveSessionInfo(true, lotterySession, lotteryResult);
            lotterySessionRepository.saveAndFlush(sesion);

            //        //reset lai master win neu !=0
            if (lottery.getTotalMasterWin() != null && lottery.getTotalMasterWin().longValue() > 0) {
                lottery.setTotalMasterWin(BigDecimal.ZERO);
                lotteryRepository.save(lottery);
            }
            LotteryCategory lotteryCategory = lottery.getCategory();
            if(lotteryCategory.getTotalMasterWin() != null && lotteryCategory.getTotalMasterWin().longValue() > 0){
                lotteryCategory.setTotalMasterWin(BigDecimal.ZERO);
                lotteryCategoryRepository.save(lotteryCategory);
            }
        } else {
            long totalMasterWin = lottery.getTotalMasterWin().longValue() + tongcuocCHANLE;
            boolean hasWinner = false;
            LotteryResult lotteryResult;
            if(totalWinChan >totalWinLe){
                if(totalMasterWin > totalWinChan) {
                    lotteryResult = LotteryResult.generateRandomChanLe(true);
                    hasWinner = true;
                } else {
                    lotteryResult = LotteryResult.generateRandomChanLe(false);
                    hasWinner = totalWinLe != 0;
                }
            } else {
                if(totalMasterWin > totalWinLe) {
                    lotteryResult = LotteryResult.generateRandomChanLe(false);
                    hasWinner = true;
                } else {
                    lotteryResult = LotteryResult.generateRandomChanLe(true);
                    hasWinner = totalWinChan != 0;
                }
            }
            LotterySession sesion = saveSessionInfo(true, lotterySession, lotteryResult);
            lotterySessionRepository.saveAndFlush(sesion);
            if(hasWinner){
                lottery.setTotalMasterWin(BigDecimal.ZERO);
                lotteryRepository.save(lottery);
            } else {
                lottery.setTotalMasterWin(BigDecimal.valueOf(totalMasterWin));
                lotteryRepository.save(lottery);
            }
        }
    }

    @Transactional
    public void quayXS(Lottery lottery, LotterySession lotterySession){
        //tinh tong số tiền cuoc
        //Long tongCuoc = lotteryTicketRepository.getTotalBet(lotterySession.getId()).orElse(0L);
        Long tongCuocDDB = lotteryTicketRepository.getTotalBetByCode(ModeCode.DDB,lotterySession.getId()).orElse(0L);
        Long tongCuocBCDB = lotteryTicketRepository.getTotalBetByCode(ModeCode.BCDB,lotterySession.getId()).orElse(0L);
        Long tongCuocDGN = lotteryTicketRepository.getTotalBetByCode(ModeCode.DGN,lotterySession.getId()).orElse(0L);
        Long tongCuocBCGN = lotteryTicketRepository.getTotalBetByCode(ModeCode.BCGN,lotterySession.getId()).orElse(0L);
        boolean hasCampain = lottery.getCategory().isEnable_campaign();
        if(!hasCampain) {
            String ddb = calculateDe(lotterySession.getId(), tongCuocDDB, tongCuocBCDB, ModeCode.DDB, ModeCode.BCDB);
            String dgn = calculateDe(lotterySession.getId(), tongCuocDGN, tongCuocBCGN, ModeCode.DGN, ModeCode.BCGN);
            //////////////////////////////////////////////////
            LotteryResult lotteryResult = LotteryResult.generateRandom(ddb, dgn);
            LotterySession sesion = saveSessionInfo(lottery.isXSN(), lotterySession, lotteryResult);
            lotterySessionRepository.saveAndFlush(sesion);
            //reset lai master win neu !=0
            if (lottery.getTotalMasterWin() != null && lottery.getTotalMasterWin().longValue() > 0) {
                lottery.setTotalMasterWin(BigDecimal.ZERO);
                lotteryRepository.save(lottery);
            }
            LotteryCategory lotteryCategory = lottery.getCategory();
            if(lotteryCategory.getTotalMasterWin() != null && lotteryCategory.getTotalMasterWin().longValue() > 0){
                lotteryCategory.setTotalMasterWin(BigDecimal.ZERO);
                lotteryCategoryRepository.save(lotteryCategory);
            }
        }else{
            logger.info("quay so xo campaign");
            calcuateCampaign(tongCuocDDB,tongCuocBCDB,tongCuocDGN,tongCuocBCGN, lottery, lotterySession);
        }
    }

    private void calcuateCampaign(long tongCuocDDB, long tongCuocBCDB,long tongCuocDGN, long tongCuocBCGN, Lottery lottery, LotterySession lotterySession){
        long totalMasterWin = (lottery.isXSN()?lottery.getTotalMasterWin().longValue():lottery.getCategory().getTotalMasterWin().longValue())
                + tongCuocDDB + tongCuocBCDB + tongCuocDGN + tongCuocBCGN;
        long masterWin = totalMasterWin;

        List<String> randomList = RandomNumberStringGenerator.generateRandomList();
        List<Integer> randomNumberList = RandomNumberStringGenerator.geneerateRandomList();
        String ddb = randomList.get(0);

        long userWin =0;
        long totalWinDDB =0;
        long totalUserWinDDB = 0;
        long totalWinBCDB =0;
        long totalUserWinBCDB = 0;

        if (tongCuocDDB > 0) {
            //tim bo so de dac biet co so luong user dat lon nhat sao cho tong thang < tong thang cuar nha cai(totalMasterWin)
            for (String random : randomList) {
                long tmpTotalWinDe = lotteryTicketRepository.getTotalWinByCode(ModeCode.DDB, random, lotterySession.getId()).orElse(0L);
                long tmpTotalUserWinDe = lotteryTicketRepository.getTotalUserWinByCode(ModeCode.DDB, random, lotterySession.getId()).orElse(0L);
                if(tmpTotalWinDe <= masterWin && tmpTotalUserWinDe >=totalUserWinDDB){
                    totalWinDDB = tmpTotalWinDe;
                    totalUserWinDDB = tmpTotalUserWinDe;
                    ddb = random;
                }
            }
        }
        userWin = userWin + totalWinDDB;
        //ba cang dac biet
        String bcdb = randomNumberList.get(0)+ddb;
        if (tongCuocBCDB > 0) {
            for (int random : randomNumberList) {
                long tmpTotalWinBC = lotteryTicketRepository.getTotalWinByCode(ModeCode.BCDB, random + ddb, lotterySession.getId()).orElse(0L);
                long tmpTotalUserWinBC = lotteryTicketRepository.getTotalUserWinByCode(ModeCode.BCDB, random + ddb, lotterySession.getId()).orElse(0L);
                if ((userWin + tmpTotalWinBC) <= masterWin && tmpTotalUserWinBC >= totalUserWinBCDB) {
                    totalWinBCDB = tmpTotalWinBC;
                    totalUserWinBCDB = tmpTotalUserWinBC;
                    bcdb = random + ddb;
                }
            }
        }
        userWin = userWin + totalWinBCDB;

        //de va ba cang giai nhat
        randomList = RandomNumberStringGenerator.generateRandomList();
        randomNumberList = RandomNumberStringGenerator.geneerateRandomList();
        String dgn = randomList.get(0);


        long totalWinDGN =0;
        long totalUserWinDGN = 0;
        long totalWinBCGN =0;
        long totalUserWinBCGN = 0;

        if (tongCuocDGN > 0) {
            //tim bo so de dac biet co so luong user dat lon nhat sao cho tong thang < tong thang cuar nha cai(totalMasterWin)
            for (String random : randomList) {
                long tmpTotalWinDe = lotteryTicketRepository.getTotalWinByCode(ModeCode.DGN, random, lotterySession.getId()).orElse(0L);
                long tmpTotalUserWinDe = lotteryTicketRepository.getTotalUserWinByCode(ModeCode.DGN, random, lotterySession.getId()).orElse(0L);
                if(userWin + tmpTotalWinDe <= masterWin && tmpTotalUserWinDe >=totalUserWinDGN){
                    totalWinDGN = tmpTotalWinDe;
                    totalUserWinDGN = tmpTotalUserWinDe;
                    dgn = random;
                }
            }
        }
        userWin = userWin + totalWinDGN;
        String bcgn = randomNumberList.get(0)+ddb;
        //ba cang dac biet
        if (tongCuocBCGN > 0) {
            for (int random : randomNumberList) {
                long tmpTotalWinBC = lotteryTicketRepository.getTotalWinByCode(ModeCode.BCGN, random + ddb, lotterySession.getId()).orElse(0L);
                long tmpTotalUserWinBC = lotteryTicketRepository.getTotalUserWinByCode(ModeCode.BCGN, random + ddb, lotterySession.getId()).orElse(0L);
                if ((userWin + tmpTotalWinBC) <= masterWin && tmpTotalUserWinBC >= totalUserWinBCGN) {
                    totalWinBCGN = tmpTotalWinBC;
                    totalUserWinBCGN = tmpTotalUserWinBC;
                    bcgn = random + dgn;
                }
            }
        }
        userWin = userWin + totalWinBCGN;
        //luu lai ket qua
        if(userWin >0){
            //reset ket qua
            if(lottery.isXSN()) {
                lottery.setTotalMasterWin(BigDecimal.ZERO);
                lotteryRepository.save(lottery);
            }else {
                LotteryCategory lotteryCategory = lottery.getCategory();
                lotteryCategory.setTotalMasterWin(BigDecimal.ZERO);
                lotteryCategoryRepository.save(lotteryCategory);
            }
        } else {
            //khong co user nao win
            if(lottery.isXSN()) {
                lottery.setTotalMasterWin(BigDecimal.valueOf(totalMasterWin));
                lotteryRepository.save(lottery);
            } else {
                LotteryCategory lotteryCategory = lottery.getCategory();
                lotteryCategory.setTotalMasterWin(BigDecimal.valueOf(totalMasterWin));
                lotteryCategoryRepository.save(lotteryCategory);
            }
        }

        LotteryResult lotteryResult = LotteryResult.generateRandom(bcdb, bcgn);
        LotterySession sesion = saveSessionInfo(lottery.isXSN(), lotterySession, lotteryResult);
        lotterySessionRepository.save(sesion);
    }

    private String calculateDe(Long sessionId, long tongCuocDe, long tongCuocBC, ModeCode deCode, ModeCode bcCode){
        long maxWin,minWin;
        List<String> randomList = RandomNumberStringGenerator.generateRandomList();
        String ddb = randomList.get(0);

        while(randomList.size() !=0) {
            ddb = randomList.get(0);
            long totalWinD =0;
            long totalWinBC=0;
            long expectedResult = (100-PROFIT_RATE)*tongCuocDe/100;
            minWin = tongCuocDe;
            maxWin = 0;
            String ret1 = randomList.get(0);
            String ret2 = randomList.get(0);
            boolean hasMinWin = false;
            //neu khong co ai dat De-> khong can tinh toan

            if(tongCuocDe > 0) {
                for (String random : randomList) {
                    totalWinD = lotteryTicketRepository.getTotalWinByCode(deCode, random, sessionId).orElse(0L);
                    if (totalWinD < expectedResult && totalWinD >= maxWin) {
                        ret1 = random;
                        maxWin = totalWinD;
                    }
                    if(expectedResult<= totalWinD && totalWinD<=tongCuocDe && totalWinD<=minWin){
                        hasMinWin = true;
                        ret2 = random;
                        minWin = totalWinD;
                    }
                }
                if(hasMinWin){
                    ddb = ret2;
                    totalWinD = minWin;
                }else {
                    totalWinD = maxWin;
                    ddb = ret1;
                }
            }
            //tim ket qua ba cang
            expectedResult = (100-PROFIT_RATE)*(tongCuocDe+tongCuocBC)/100;
            maxWin = 0;
            minWin = tongCuocDe + tongCuocBC;
            hasMinWin = false;
            boolean hasMaxWin = false;
            List<Integer> randomNumberList = RandomNumberStringGenerator.geneerateRandomList();

            int bc1 =randomNumberList.get(0);
            int bc2 = randomNumberList.get(0);
            if(tongCuocBC >0) {
                for (int random : randomNumberList) {
                    totalWinBC = lotteryTicketRepository.getTotalWinByCode(bcCode, random + ddb, sessionId).orElse(0L);
                    long totalWin = totalWinBC + totalWinD;
                    if (totalWin <= expectedResult && totalWinBC >= maxWin) {
                        hasMaxWin = true;
                        bc1 = random;
                        maxWin = totalWin;
                    }
                    if(expectedResult<totalWin && totalWin<= (tongCuocBC+tongCuocDe) && totalWinBC<=minWin){
                        hasMinWin = true;
                        bc2 = random;
                        minWin = totalWin;
                    }
                }

                if(hasMinWin){
                    ddb = bc2+ddb;
                    return ddb;
                }
                if(hasMaxWin){
                    ddb = bc1+ddb;
                    return ddb;
                }
                else
                    randomList.remove(ddb);
            } else
                return bc1+ ddb;

        }
        return ddb;
    }

    public LotterySession saveSessionInfo(boolean isSXN, LotterySession lotterySession, LotteryResult lotteryResult){
        if(!isSXN) {
            List<LotteryVideo> videos = lotteryVideoRepository.findAll();
            List<VideoData> videoDataList = new ArrayList<>();
            long duration = 0;
            long startTime = lotterySession.getStartTime();
            //thu tu video: 1->7->db
            //giai 1
            for(String result:lotteryResult.getResult1().split("-")) {
                VideoData videoData = generateVideoData(videos, startTime, 1, result);
                duration += videoData.getDuration();
                startTime += videoData.getDuration();
                videoDataList.add(videoData);
            }
            //giai 2
            for(String result:lotteryResult.getResult2().split("-")) {
                VideoData videoData = generateVideoData(videos, startTime, 2, result);
                duration += videoData.getDuration();
                startTime += videoData.getDuration();
                videoDataList.add(videoData);
            }
            //giai 3
            for(String result:lotteryResult.getResult3().split("-")) {
                VideoData videoData = generateVideoData(videos, startTime, 3, result);
                duration += videoData.getDuration();
                startTime += videoData.getDuration();
                videoDataList.add(videoData);
            }
            //giai 4
            for(String result:lotteryResult.getResult4().split("-")) {
                VideoData videoData = generateVideoData(videos, startTime, 4, result);
                duration += videoData.getDuration();
                startTime += videoData.getDuration();
                videoDataList.add(videoData);
            }
            //giai 5
            for(String result:lotteryResult.getResult5().split("-")) {
                VideoData videoData = generateVideoData(videos, startTime, 5, result);
                duration += videoData.getDuration();
                startTime += videoData.getDuration();
                videoDataList.add(videoData);
            }
            //giai 6
            for(String result:lotteryResult.getResult6().split("-")) {
                VideoData videoData = generateVideoData(videos, startTime, 6, result);
                duration += videoData.getDuration();
                startTime += videoData.getDuration();
                videoDataList.add(videoData);
            }
            //giai 7
            for(String result:lotteryResult.getResult7().split("-")) {
                VideoData videoData = generateVideoData(videos, startTime, 7, result);
                duration += videoData.getDuration();
                startTime += videoData.getDuration();
                videoDataList.add(videoData);
            }
            //giai 0
            for(String result:lotteryResult.getResult0().split("-")) {
                VideoData videoData = generateVideoData(videos, startTime, 0, result);
                duration += videoData.getDuration();
                startTime += videoData.getDuration();
                videoDataList.add(videoData);
            }

            Gson gson = new Gson();
            String videoDatas = gson.toJson(videoDataList);
            lotterySession.setVideos(videoDatas);
            lotterySession.setDuration(duration);
        }

        lotterySession.setResult_arr(lotteryResult.getResultArr());
        lotterySession.setResult_7(lotteryResult.getResult7());
        lotterySession.setResult_6(lotteryResult.getResult6());
        lotterySession.setResult_5(lotteryResult.getResult5());
        lotterySession.setResult_4(lotteryResult.getResult4());
        lotterySession.setResult_3(lotteryResult.getResult3());
        lotterySession.setResult_2(lotteryResult.getResult2());
        lotterySession.setResult_1(lotteryResult.getResult1());
        lotterySession.setResult_0(lotteryResult.getResult0());
        lotterySession.setStatus(isSXN?SessionStatus.DONE:SessionStatus.PROCESSING);
        return lotterySession;

    }

    private VideoData generateVideoData(List<LotteryVideo> videos, long startTime, int position, String result){
        long duration = 0;
        List<String> videosStr = new ArrayList<>();
        List<String> resultArr = new ArrayList<>();
        resultArr.add(result);
        char[] digits = result.toCharArray();
        int index = 0;
        for(char digit: digits){
            index ++;
            int number = Character.getNumericValue(digit);
            LotteryVideo video = getLotteryVideo(5-digits.length+index,number, videos);
            videosStr.add(video.getUrl());
            if(video.getDuration()>duration)
                duration =video.getDuration();
        }
        return VideoData.builder()
                .possition(position)
                .results(resultArr)
                .startTime(startTime)
                .videos(videosStr)
                .duration(duration)
                .build();
    }
    @Transactional
    private boolean crawlXSCTVN(Lottery lottery, LotterySession lotterySession){
        try {
            Document document = Jsoup.connect("https://www.minhngoc.net.vn/getkqxs/mien-bac.js").get();
            String dateStr = getResult(document, "td.ngay");
            if (dateStr == null)
                return false;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Ngày:'dd/MM/yyyy");
            LocalDate date = LocalDate.parse(dateStr, formatter);
            int day = date.getDayOfMonth();
            if(day != LocalDateTime.now().getDayOfMonth())
                return false;

            //giai dacbiet
            String result0 = getResult(document, "td.giaidb");
            if (result0 == null)
                return false;
            String result1 = getResult(document, "td.giai1");
            if (result1 == null)
                return false;
            String result2 = getResult(document, "td.giai2");
            if (result2 == null)
                return false;
            String result3 = getResult(document, "td.giai3");
            if (result3 == null)
                return false;
            String result4 = getResult(document, "td.giai4");
            if (result4 == null)
                return false;
            String result5 = getResult(document, "td.giai5");
            if (result5 == null)
                return false;
            String result6 = getResult(document, "td.giai6");
            if (result6 == null)
                return false;
            String result7 = getResult(document, "td.giai7");
            if (result7 == null)
                return false;
            //du lieu khong thay doi
            if(lotterySession.getResult_0() == result0
                    && lotterySession.getResult_1() == result1
                    && lotterySession.getResult_2() == result2
                    && lotterySession.getResult_3() == result3
                    && lotterySession.getResult_4() == result4
                    && lotterySession.getResult_5() == result5
                    && lotterySession.getResult_6() == result6
                    && lotterySession.getResult_7() == result7
            )
                return false;
            lotterySession.setResult_0(result0);
            lotterySession.setResult_1(result1);
            lotterySession.setResult_2(result2);
            lotterySession.setResult_3(result3);
            lotterySession.setResult_4(result4);
            lotterySession.setResult_5(result5);
            lotterySession.setResult_6(result6);
            lotterySession.setResult_7(result7);
            lotterySession.setDuration(0);

            ArrayList<String> results = new ArrayList<>();
            results.addAll(Arrays.asList(lotterySession.getResult_0().split("-")));
            results.addAll(Arrays.asList(lotterySession.getResult_1().split("-")));
            results.addAll(Arrays.asList(lotterySession.getResult_2().split("-")));
            results.addAll(Arrays.asList(lotterySession.getResult_3().split("-")));
            results.addAll(Arrays.asList(lotterySession.getResult_4().split("-")));
            results.addAll(Arrays.asList(lotterySession.getResult_5().split("-")));
            results.addAll(Arrays.asList(lotterySession.getResult_6().split("-")));
            results.addAll(Arrays.asList(lotterySession.getResult_7().split("-")));

            lotterySession.setResult_arr(getLastTwoDigits(results));
            lotterySession.setStatus(SessionStatus.DONE);
            lotterySessionRepository.save(lotterySession);

            //tao session moi
            long nextTime = getNextTimeSXBMs(1,ConstantCommon.XSVIE_HOUR,ConstantCommon.XSVIE_MIN);
            LotterySession newSession = LotterySession.builder()
                    .lotteryId(lottery.getId())
                    .startTime(nextTime)
                    .status(SessionStatus.WAITING)
                    .build();
            lotterySessionRepository.save(newSession);

            lottery.setStartTime(lotterySession.getStartTime());
            lottery.setNextTime(nextTime);
            lottery.setDoneResult0(lotterySession.getResult_0());
            lotteryRepository.save(lottery);

        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public String crawlSXLao(){
        try {
            Document document = Jsoup.connect("https://lotto.mthai.com/lottery/lao").get();
            Elements liElements = document.select("p");
            for (Element liElement : liElements) {
                String content = liElement.text();
                if(content.contains("เลข 6 ตัว")){
                    // Tìm vị trí bắt đầu của số
                    int startIndex = content.indexOf("เลข 6 ตัว ") + "เลข 6 ตัว ".length();
                    // Tìm vị trí kết thúc của số
                    int endIndex = content.indexOf(" ", startIndex);
                    // Tách số từ chuỗi dữ liệu
                    String number = content.substring(startIndex, endIndex);
                    return number;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String getResult(Document document, String queryCSS){
        Element element = document.selectFirst(queryCSS);
        if(element == null)
            return null;
        return element.text().replaceAll("\\s", "");
    }

    private LotteryVideo getLotteryVideo(int index, int value, List<LotteryVideo> lotteryVideos){
        for(LotteryVideo video: lotteryVideos){
            if(video.getNumber() == value && video.getIndex() == index){
                return video;
            }
        }
        for(LotteryVideo video: lotteryVideos){
            if(video.getNumber() == value){
                return video;
            }
        }
        return lotteryVideos.get(0);
    }

    public static String getLastTwoDigits(ArrayList<String> numbers) {
        ArrayList<Integer> lastTwoDigits = new ArrayList<>();

        for (String number : numbers) {
            if (number.length() >= 2 && number.length() <= 6) {
                int lastTwo = Integer.parseInt(number.substring(number.length() - 2));
                lastTwoDigits.add(lastTwo);
            }
        }

        Collections.sort(lastTwoDigits);

        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < lastTwoDigits.size(); i++) {
            resultBuilder.append(String.format("%02d", lastTwoDigits.get(i)));
            if (i < lastTwoDigits.size() - 1) {
                resultBuilder.append("-");
            }
        }

        return resultBuilder.toString();
    }

    public static long getNextTimeSXLAO() {
        // Lấy thời điểm hiện tại với timezone GMT+7
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        // Lấy thứ của thời điểm hiện tại
        DayOfWeek currentDayOfWeek = now.getDayOfWeek();

        // Tạo một mảng chứa các thứ cần tìm (Thứ 2, Thứ 4, Thứ 6)
        DayOfWeek[] daysToFind = {DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY};

        // Tạo một mảng chứa các khoảng thời gian từ thời điểm hiện tại tới thời điểm 20h30 của các thứ cần tìm
        long[] timeDifferences = new long[3];

        // Duyệt qua mảng và tính khoảng thời gian tới thời điểm 20h30 gần nhất cho các thứ cần tìm
        for (int i = 0; i < daysToFind.length; i++) {
            LocalDateTime nextTime = now.with(TemporalAdjusters.nextOrSame(daysToFind[i]))
                    .withHour(13)
                    .withMinute(30)
                    .withSecond(0)
                    .withNano(0);

            // Tính khoảng thời gian còn lại tính từ thời điểm hiện tại tới thời điểm 20h30 của ngày đang xét
            Duration duration = Duration.between(now, nextTime);

            // Chuyển đổi thời gian còn lại thành miliseconds và lưu vào mảng timeDifferences
            timeDifferences[i] = duration.toMillis();
        }

        // Tìm giá trị nhỏ nhất trong mảng timeDifferences
        long minDifference = Long.MAX_VALUE;
        for (long difference : timeDifferences) {
            if(difference <0) continue;
            if (difference < minDifference) {
                minDifference = difference;
            }
        }

        return System.currentTimeMillis() + minDifference;
    }
}
