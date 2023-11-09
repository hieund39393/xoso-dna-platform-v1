package com.xoso.schedule;

import com.xoso.job.BankJob;
import com.xoso.job.LotteryJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerManager {
    @Autowired
    LotteryJob lotteryJob;
    @Autowired
    BankJob bankJob;
    @Value("${apibank.auto.deposit.enable}")
    private boolean autoDeposit;
    //schedule xo so nhanh, chay trong khoang thoi gian 3s
    @Scheduled(fixedDelay = 3000)
    public void scheduleXSN() {
       lotteryJob.jobXSN();
    }
    @Scheduled(fixedDelay = 3500)
    public void scheduleXSCHANLE() {
        lotteryJob.jobXSCHANLE();
    }

    @Scheduled(fixedDelay = 1700)
    public void schedulePayXSN() {
        lotteryJob.payJob();
    }
    //schedule xo so bip, chay trong 10s
    @Scheduled(fixedDelay =10000)
    public void scheduleXSB() {
        lotteryJob.jobXSB();
    }


    @Scheduled(fixedDelay =60000)
    public void scheduleXSCT() {
        lotteryJob.jobSXCTVN();
        lotteryJob.jobSXCTLAO(null);
    }

    @Scheduled(fixedDelay =2*60*1000)
    public void scheduleAutoDeposit() {
        if(autoDeposit) {
            bankJob.jobAutoDeposit(null);
        }
    }
}
