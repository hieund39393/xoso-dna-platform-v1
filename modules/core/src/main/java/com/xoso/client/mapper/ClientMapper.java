package com.xoso.client.mapper;

import com.xoso.client.data.ClientCreateRequestData;
import com.xoso.client.data.ClientData;
import com.xoso.client.model.Client;
import com.xoso.user.wsdto.UserCreateRequestWsDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ClientMapper {
    List<ClientData> mapToClients(List<Client> clients);
    ClientCreateRequestData fromUserRequest(UserCreateRequestWsDTO request);
}
