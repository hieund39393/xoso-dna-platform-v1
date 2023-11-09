package com.xoso.client.service.impl;

import com.xoso.client.service.ClientWriteService;
import com.xoso.client.data.ClientCreateRequestData;
import com.xoso.client.model.Client;
import com.xoso.client.repository.ClientRepository;
import com.xoso.infrastructure.service.AccountNumberGenerator;
import com.xoso.infrastructure.core.data.ResultBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientWriteServiceImpl implements ClientWriteService {

    private final ClientRepository clientRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    @Autowired
    public ClientWriteServiceImpl(ClientRepository clientRepository, AccountNumberGenerator accountNumberGenerator) {
        this.clientRepository = clientRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    @Override
    public ResultBuilder createClient(ClientCreateRequestData request) {

        var client = Client.fromRequest(request);
        this.clientRepository.saveAndFlush(client);
        client.setAccountNo(this.accountNumberGenerator.generate(client));
        this.clientRepository.save(client);
        return new ResultBuilder().withEntityId(client.getId()).build();
    }
}
