package com.xoso.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xoso.commandsource.model.RequestCommandSource;
import com.xoso.commandsource.repository.RequestCommandSourceRepository;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.lottery.exception.CommandSourceNotFoundException;
import com.xoso.lottery.service.LotteryService;
import com.xoso.lottery.service.LotteryVideoService;
import com.xoso.lottery.wsdto.LotteryRequestWsDTO;
import com.xoso.lottery.wsdto.LotteryVideoRequestWsDTO;
import com.xoso.wsdto.OtpVerifyRequestWsDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class OTPController {

    @Autowired
    private RequestCommandSourceRepository requestCommandSourceRepository;
    @Autowired
    private LotteryVideoService lotteryVideoService;
    @Autowired
    private LotteryService lotteryService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/otp-page")
    public String otpPage(Model model) {
        model.addAttribute("otpVerifyRequest", new OtpVerifyRequestWsDTO());
        return "otpverify";
    }

    @PostMapping("/opt-verify")
    public String verifyOTP(@Valid OtpVerifyRequestWsDTO body, HttpServletRequest request) {
        request.getSession().setAttribute("otpVerified", true);
        // TODO: verify otp
        if (!"123456".equals(body.getOtp())) {
            throw new AbstractPlatformException("error.msg.otp.invalid", "OTP invalid");
        }
        return  "redirect:/";
    }

    @PostMapping("/otp/lottery/send-otp")
    public String sentOtp(Model model, @Valid LotteryRequestWsDTO request) {
        try {
            var commandSource = newCommandSource("lottery", objectMapper.writeValueAsString(request));
            return "redirect:/otp/" + commandSource.getId() + "/verify-page";
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/otp/lottery_videos/send-otp")
    public String sentOtp(Model model, @Valid LotteryVideoRequestWsDTO request) {
        try {
            var commandSource = newCommandSource("lottery_videos", objectMapper.writeValueAsString(request));
            return "redirect:/otp/" + commandSource.getId() + "/verify-page";
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/otp/{commandId}/verify-page")
    public String otpVerifyPage(Model model, @PathVariable Long commandId) {
        model.addAttribute("commandId", commandId);
        model.addAttribute("otpVerifyRequest", new OtpVerifyRequestWsDTO());
        return "otppage";
    }

    @PostMapping("/otp/{commandId}/verify")
    public String verifyOtp(Model model, @PathVariable Long commandId, @Valid OtpVerifyRequestWsDTO request) {
        var commandSource = this.requestCommandSourceRepository.findById(commandId).orElseThrow(CommandSourceNotFoundException::new);
        if (commandSource.isChecker()) {
            throw new CommandSourceNotFoundException();
        }
        //TODO: verify OTP
        if (!"123456".equals(request.getOtp())) {
            throw new AbstractPlatformException("error.msg.otp.invalid", "OTP invalid");
        }

        var entity = commandSource.getEntityName();
        ResultBuilder result = null;
        if (is(entity, "lottery_videos")) {
            result = this.lotteryVideoService.checkerLotteryVideo(commandSource);
            return  "redirect:/lotteryvideo/detail/" + result.getEntityId();
        } else if (is(entity, "lottery")) {
            result = this.lotteryService.checkerCreateLottery(commandSource);
            return  "redirect:/lotteries/detail/" + result.getEntityId();
        }
        return "";
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

    private RequestCommandSource newCommandSource(String entityName, String json) {
        var commandSource = RequestCommandSource.builder()
                .entityName(entityName)
                .actionName("CREATE")
                .checker(Boolean.FALSE)
                .commandAsJson(json)
                .build();
        this.requestCommandSourceRepository.saveAndFlush(commandSource);
        return commandSource;
    }
}
