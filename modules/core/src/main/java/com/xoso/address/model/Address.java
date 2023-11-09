package com.xoso.address.model;


import com.xoso.client.model.ClientAddress;
import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "address")
public class Address extends AbstractAuditableCustom {

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private Set<ClientAddress> clientAddress;

    @Column(name = "street")
    private String street;


}
