package com.xoso.client.service;

import com.xoso.client.data.ClientCreateRequestData;
import com.xoso.infrastructure.core.data.ResultBuilder;

public interface ClientWriteService {

    ResultBuilder createClient(ClientCreateRequestData request);
}
