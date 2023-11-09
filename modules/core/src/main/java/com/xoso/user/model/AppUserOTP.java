package com.xoso.user.model;

import com.xoso.infrastructure.core.model.AbstractPersistableCustom;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_user_otp")
public class AppUserOTP extends AbstractPersistableCustom implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(name = "otp")
    private String otp;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;



}
