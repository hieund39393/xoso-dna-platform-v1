package com.xoso.autobank.dto;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */


import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
@Data
public class BCELResponse{
    public boolean success;
    public String message;
    public BCELData data;
    public int from_source;
}

