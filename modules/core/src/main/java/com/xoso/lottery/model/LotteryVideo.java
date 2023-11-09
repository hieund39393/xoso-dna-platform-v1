package com.xoso.lottery.model;

import com.xoso.infrastructure.core.model.AbstractPersistableCustom;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lottery_videos")
public class LotteryVideo extends AbstractPersistableCustom {

    @Column(name = "group_video", nullable = false)
    private int group;
    @Column(name = "index", nullable = false)
    private int index;
    @Column(name = "number", nullable = false)
    private int number;
    @Column(name = "duration", nullable = false)
    private long duration;
    @Column(name = "url", nullable = false)
    private String url;
}
