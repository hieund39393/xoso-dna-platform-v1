package com.xoso.lottery.model;

import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import com.xoso.infrastructure.core.model.AbstractPersistableCustom;
import com.xoso.user.model.AppUser;
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
@Table(name = "lottery_ticket")
public class LotteryTicket extends AbstractAuditableCustom {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id", nullable = false)
    private LotterySession session;

    @Column(name = "code", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModeCode code;

    @Column(name = "numbers")
    private String numbers;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "price")
    private long price;

    @Column(name = "prize_money")
    private long prizeMoney;

    @Column(name = "win")
    private boolean win;

    @Column(name = "status")
    private int status;

}
