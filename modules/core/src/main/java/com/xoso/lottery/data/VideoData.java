package com.xoso.lottery.data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@Builder
public class VideoData {
    private int possition;
    private List<String> results;
    private List<String> videos;
    private long startTime;
    private long duration;
}
