package com.xoso.client.model;

import com.xoso.address.model.Address;
import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "client_address")
public class ClientAddress extends AbstractAuditableCustom {

    @ManyToOne
    private Client client;

    @ManyToOne
    private Address address;

    @Column(name = "address_type")
    private String addressType;

    @Column(name = "is_active")
    private boolean isActive;
}
