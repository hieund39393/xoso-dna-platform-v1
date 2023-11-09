package com.xoso.autobank.service.impl;

import com.xoso.autobank.dto.BCELRequest;
import com.xoso.autobank.dto.BCELResponse;
import com.xoso.autobank.dto.BankTransferInfoDto;
import com.xoso.autobank.service.BCELBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@Service
public class BCELBankServiceImpl implements BCELBankService {
    @Value("${apibank.bcel.url}")
    private String HOST;
    @Autowired
    RestTemplate restTemplate;

    @Override
    public List<BankTransferInfoDto> queryTodayTransaction(String userName, String password, String accountNumber, String date) throws Exception {
        List<BankTransferInfoDto> rsp = new ArrayList<>();

        BCELRequest request = BCELRequest.builder()
                .username(userName)
                .password(password)
                .accountNumber(accountNumber)
                .fromDate(getYesterday())
                .toDate(getCurrDate())
                .build();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<BCELRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<BCELResponse> response =
                restTemplate.exchange(HOST, HttpMethod.POST, entity, BCELResponse.class);
        // Process the response
        if (response.getStatusCode().is2xxSuccessful()) {
            BCELResponse transactionResponse = response.getBody();
            if (transactionResponse.isSuccess()) {
                transactionResponse.data.responseData.forEach(bcelResponseData -> {
                    if (bcelResponseData.amount > 0) {
                        String[] transferDescs = bcelResponseData.transferDesc.split("\\|");
                        String content = "";
                        if (transferDescs.length > 6)
                            content = transferDescs[5];
                        BankTransferInfoDto dto = BankTransferInfoDto
                                .builder()
                                .amount(BigDecimal.valueOf(bcelResponseData.amount))
                                .content(content)
                                .xref(bcelResponseData.getXref())
                                .build();
                        rsp.add(dto);
                    }
                });
            } else {
                log.error("call queryBcelBank error: " + transactionResponse.message);
                throw new Exception("Call api bank thanh cong, response tra ve loi["+transactionResponse.message+"]");
            }
        } else {
            System.out.println("Request failed with status code: " + response.getStatusCode());
            throw new Exception("Call api bank loi[HTTPCode "+response.getStatusCode()+"]");
        }
        return rsp;
    }

    private String getCurrDate(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
    private String getYesterday(){
        Date date = new Date(System.currentTimeMillis() - 24*60*60*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }


}
