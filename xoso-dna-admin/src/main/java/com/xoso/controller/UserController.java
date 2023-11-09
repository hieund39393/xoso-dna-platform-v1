package com.xoso.controller;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.infrastructure.security.service.AuthenticationService;
import com.xoso.service.AdminUserService;
import com.xoso.user.data.UserData;
import com.xoso.user.service.UserReadService;
import com.xoso.user.service.UserWriteService;
import com.xoso.user.wsdto.ChangePasswordWsDTO;
import com.xoso.user.wsdto.UserUpdateRequestWsDTO;
import com.xoso.wallet.data.WalletData;
import com.xoso.wallet.model.WalletStatus;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("users")
public class UserController {

    private final UserReadService userReadService;
    private final UserWriteService userWriteService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    public UserController(UserReadService userReadService, UserWriteService userWriteService) {
        this.userReadService = userReadService;
        this.userWriteService = userWriteService;
    }
    @GetMapping("/export")
    public void exportWallet(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("User");

        // Tạo dòng tiêu đề
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Mã KH");
        headerRow.createCell(1).setCellValue("Tên đăng nhập ");
        headerRow.createCell(2).setCellValue("Tên");
        headerRow.createCell(3).setCellValue("Số DT");
        headerRow.createCell(4).setCellValue("Số lần đăng nhập thất bại ");
        headerRow.createCell(5).setCellValue("Khóa tài khoản");
        // ... thêm các cột khác cần xuất ...

        // Thêm dữ liệu vào Sheet
        int rowNum = 1;
        int page = 0;
        while(true){
            var searchParams = new SearchParameters(page, 50, null, new ArrayList<>());
            var userDataPage = userReadService.search(searchParams);
            if(userDataPage.getPageItems().isEmpty())
                break;
            for(UserData data: userDataPage.getPageItems()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getId());
                row.createCell(1).setCellValue(data.getUsername());
                row.createCell(2).setCellValue(data.getFullName());
                row.createCell(3).setCellValue(data.getMobileNo());
                row.createCell(4).setCellValue(data.getFailedAttempt());
                row.createCell(5).setCellValue(data.isAccountNonLocked()?"false":"true");
            }
            page++;
        }

        // Thiết lập Header cho Response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Danh sach nguoi dung.xlsx");

        // Ghi dữ liệu vào Response OutputStream
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    @GetMapping("/search")
    public String searchUsers(Model model,
                                      @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                      @RequestParam(value = "searchValue", required = false) String searchValue,
                                      @RequestParam(value = "oneServices", required = false) List<String> oneServices,
                                      @RequestParam(value = "status", required = false) List<String> status,
                                      RedirectAttributes attr) {

        attr.addFlashAttribute("isloading", true);
        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue, status);
        var userDataPage = userReadService.search(searchParams);
        model.addAttribute("userDataPage", userDataPage);

        var walletsStatusOptions = WalletStatus.values();
        model.addAttribute("walletsStatusOptions", walletsStatusOptions);

        var totalItems = userDataPage.getTotalFilteredRecords();
        int totalPages = totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("status", status);
        model.addAttribute("oneServices", oneServices);
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "user/list";
    }

    @GetMapping("/{id}")
    public String getUser(Model model, @PathVariable("id") Long id) {
        var userData = this.userReadService.retrieveDetail(id);

        model.addAttribute("userData", userData);
        return "user/detail";
    }

    @PostMapping("/{userId}/unlock")
    public String unlock(Model model, @PathVariable("userId") Long userId) {
        this.userWriteService.unlock(userId);
        return "redirect:/users/" + userId;
    }

    @GetMapping("/change-password")
    public String changePasswordTemp(Model model) {
        model.addAttribute("changePasswordData", new ChangePasswordWsDTO());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid ChangePasswordWsDTO request) {
        var user = authenticationService.authenticatedUser();
        var presentedPassword = request.getOldPassword();
        if (!(new BCryptPasswordEncoder()).matches(presentedPassword, user.getPassword())) {
            throw new AbstractPlatformException("old.password.not.match", "Old password not match");
        }
        this.userWriteService.changePassword(user.getUsername(), request);
        return "redirect:/logout";
    }

    @PostMapping("/{userId}/reset-password")
    public String changePassword(Model model, @PathVariable("userId") Long userId) {
        var passwordData = this.adminUserService.generatePassword(userId);
        model.addAttribute("passwordData", passwordData);
        return "user/reset-password";
    }

    @GetMapping("/{userId}/edit")
    public String initUpdateUser(Model model, @PathVariable("userId") Long userId) {
        var userData = this.userReadService.retrieveDetail(userId);
        var request = UserUpdateRequestWsDTO.builder()
                .id(userId)
                .username(userData.getUsername())
                .fullName(userData.getFullName())
                .email(userData.getEmail())
                .mobileNo(userData.getMobileNo()).build();
        model.addAttribute("userData", request);
        return "user/edit";
    }

    @PostMapping("/{userId}/edit")
    public String updateUser(Model model, @PathVariable("userId") Long userId, @Valid UserUpdateRequestWsDTO request) {
        this.userWriteService.updateUser(userId, request);
        return "redirect:/users/" + userId;
    }
}
