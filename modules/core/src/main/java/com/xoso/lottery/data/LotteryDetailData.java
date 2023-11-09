package com.xoso.lottery.data;

import com.xoso.lottery.model.SessionStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.List;

@Data
@Builder
@Setter
@Getter
public class LotteryDetailData {
    public Long id;
    public Long sessionId;
    public SessionStatus status;
    public long nextTime;
    String supportedModes;
    List<LotteryHistoryData> history;
    public long startTime;
    public long duration;
    public long currentTime;
    public List<VideoData> videos;
    private String doneResult0;
    private String result0;
    private String result1;
    private String result2;
    private String result3;
    private String result4;
    private String result5;
    private String result6;
    private String result7;
}
