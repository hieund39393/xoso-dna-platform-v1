/*
package com.xoso.api.schedule;

import com.xoso.job.LotteryJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerManager {
    @Autowired
    LotteryJob lotteryJob;
    //schedule xo so nhanh, chay trong khoang thoi gian 3s
    @Scheduled(fixedDelay = 7000)
    public void scheduleXSN() {
       lotteryJob.jobXSN();;
    }

    @Scheduled(fixedDelay = 3000)
    public void schedulePayXSN() {
        lotteryJob.payXSN();
    }
    //schedule xo so bip, chay trong 3 phut
    @Scheduled(fixedDelay =60000)
    public void scheduleXSB() {
        lotteryJob.jobXSB();;
    }
}
*/
