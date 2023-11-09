package com.xoso.client.service.impl;

import com.xoso.client.data.ClientData;
import com.xoso.client.mapper.ClientMapper;
import com.xoso.client.repository.ClientRepository;
import com.xoso.client.service.ClientReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientReadServiceImpl implements ClientReadService {

    private ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientReadServiceImpl(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public List<ClientData> getAll() {
        return this.clientMapper.mapToClients(this.clientRepository.findAll());
    }
}
