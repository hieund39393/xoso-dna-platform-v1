package com.xoso.language.model;

import com.xoso.infrastructure.core.model.AbstractPersistableCustom;
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
@Table(name = "translated_string")
public class TranslatedString extends AbstractPersistableCustom {
    @Column(name = "viet", nullable = false)
    private String viet;

    @Column(name = "code")
    private String code;

    @Column(name = "thai")
    private String thai;

    @Column(name = "cam")
    private String cam;

    @Column(name = "lao")
    private String lao;

}
