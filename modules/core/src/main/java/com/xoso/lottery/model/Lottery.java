package com.xoso.lottery.model;

import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;

@Slf4j
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lottery")
public class Lottery extends AbstractAuditableCustom {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private LotteryCategory category;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeCode type;

    @Column(name = "modes")
    private String modes;

    @Column(name = "hour")
    private int hour;
    @Column(name = "min")
    private int min;
    @Column(name = "sec")
    private int sec;

    @Column(name = "active")
    private boolean active;

    @Column(name = "vip")
    private boolean vip;

    @Column(name = "done_result_0")
    private String doneResult0;

    @Column(name = "next_time")
    private Long nextTime;
    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "total_master_win", nullable = false)
    private BigDecimal totalMasterWin;

    public boolean isSXB(){
        return type == TypeCode.XSB;
    }
    public boolean isXSN(){
        return type == TypeCode.XSN;
    }
    public boolean isXSCHANLE(){
        return type == TypeCode.XSCHANLE;
    }
    public boolean isXSCT(){
        return type == TypeCode.XSCT;
    }
}
