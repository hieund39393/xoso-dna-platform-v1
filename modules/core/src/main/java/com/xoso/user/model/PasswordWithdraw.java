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
@Table(name = "password_withdraw")
public class PasswordWithdraw extends AbstractPersistableCustom implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(name = "password")
    private String password;

    @Column(name = "last_time_password_updated")
    private LocalDateTime lastTimePasswordUpdated;
}
