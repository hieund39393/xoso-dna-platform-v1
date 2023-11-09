package com.xoso.lottery.model;

import com.xoso.infrastructure.core.model.AbstractPersistableCustom;
import com.xoso.language.model.TranslatedString;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Slf4j
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lottery_session")
public class LotterySession extends AbstractPersistableCustom {
    @Column(name = "lottery_id", nullable = false)
    private Long lotteryId;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Column(name = "result_0")
    private String result_0;

    @Column(name = "result_1")
    private String result_1;

    @Column(name = "result_2")
    private String result_2;

    @Column(name = "result_3")
    private String result_3;

    @Column(name = "result_4")
    private String result_4;

    @Column(name = "result_5")
    private String result_5;

    @Column(name = "result_6")
    private String result_6;

    @Column(name = "result_7")
    private String result_7;

    @Column(name = "result_arr")
    private String result_arr;

    @Column(name = "start_time")
    private long startTime;

    @Column(name = "duration")
    private long duration;

    @Column(name = "videos")
    private String videos;

}
