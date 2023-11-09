package com.xoso.api.controller;

import com.xoso.agency.service.AgencyService;
import com.xoso.agency.wsdto.AgencyCreateWsDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/agency")
public class AgencyController extends BaseController{
    @Autowired
    AgencyService agencyService;
    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody @Valid AgencyCreateWsDTO request){
        return response(agencyService.register(request));
    }

    @GetMapping("state")
    public ResponseEntity<?> getState(){
        return response(agencyService.getState());
    }
    //Test
    @PostMapping("{id}/approve")
    public ResponseEntity<?> approve(@PathVariable long id){
        return response(agencyService.approve(id));
    }
    @PostMapping("{id}/reject")
    public ResponseEntity<?> reject(@PathVariable long id){
        return response(agencyService.reject(id,"tu choi"));
    }

    @GetMapping("users")
    public ResponseEntity<?> getUserList(@RequestParam(value = "pageNumber",required = false, defaultValue = "1") @Min(value = 1,message = "pageNumber must be greater than 0") Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Max(value = 20L,message = "pageSize must be less than 20")  Integer pageSize,
                                         @RequestParam(value = "searchValue", required = false, defaultValue = "") String searchValue){
        return response(agencyService.getUserList(pageNumber,pageSize,searchValue));
    }
}
