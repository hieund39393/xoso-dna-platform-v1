package com.xoso.infrastructure.core.exception;

public enum ExceptionCode {
    BUY_TICKET_EXPIRED_TIME("error.msg.ticket.expired.time","Đã quá thời gian đặt cược","ໝົດເວລາເດິມພັນແລ້ວ"),
    WALLET_MONEY_NOT_ENOUGH("error.msg.wallet.money.not.enough","Tiền trong tài khoản không đủ","ເງິນໃນບັນຊີຂອງທ່ານບໍ່ພຽງພໍ"),

    AGENCY_REQUEST_ALREADY_APPROVED("error.msg.agency.request.already.approved","Bạn đã được làm đại lý","ທ່ານໄດ້ຮັບຕົວແທນ"),
    AGENCY_REQUEST_IS_PROCESSING("error.msg.agency.request.is.processing","Yêu cầu đang được xử lý","ກຳລັງປະມວນຜົນຄຳຮ້ອງຂໍ"),
    AGENCY_REQUEST_NOT_EXISTED("error.msg.agency.request.not.existed","Yêu cầu không tồn tại","ບໍ່ມີຄຳຮ້ອງຂໍ"),
    BANK_NOT_FOUND("error.msg.bank.not.found","Không tìm thấy thông tin ngân hàng","ບໍ່ພົບຂໍ້ມູນທະນາຄານ"),
    BANK_ACCOUNT_ALREADY_EXISTED("error.msg.bank.account.already.existed","Tài khoản ngân hàng đã được đăng ký","ບັນຊີທະນາຄານທີ່ລົງທະບຽນ"),
    CAPTCHA_INCORRECT("error.msg.captcha.invalid","Mã xác minh không chính xác","ລະຫັດຢືນຢັນບໍ່ຖືກຕ້ອງ"),
    WITHDRAW_NOT_ACCEPT_BYTIME("error.msg.withdraw_not_accept"," Thời gian giữa 2 lần rút  tiền cách nhau 30 phút","ระยะเวลาระหว่างการถอนเงินครั้งละ 30 นาที"),
    GIFTCODE_ALREADY_USED("error.giftcode.already.used","Mã quà tặng đã được sử dụng","ໃຊ້ລະຫັດຂອງຂວັນແລ້ວ"),
    GIFTCODE_INVALID("error.giftcode.invalid","Mã quà tặng không chính xác","ລະຫັດຂອງຂວັນບໍ່ຖືກຕ້ອງ"),
    BANK_ACCOUNT_DOES_NOT_EXISTED("error.msg.bank.account.does.not.existed","Chưa có thông tin ngân hàng","ບໍ່ມີຂໍ້ມູນທະນາຄານເທື່ອ"),
    USER_ALREADY_EXISTED("error.msg.user.already.exists","Tài khoản đã tồn tại","ບັນຊີີນີ້ມີຢູ່ແລ້ວ"),
    USER_NAME_NOT_FOUND("error.msg.user.name.not.found","Thông tin tài khoản không khớp","ຂໍ້ມູນບັນຊີບໍ່ຖືກກັນ"),
    USERNAME_PASSWORD_INVALID("error.msg.username.or.password.invalid","Sai tên đăng nhập/mật khẩu","ຊື່ຜູ້ໃຊ້/ລະຫັດບໍ່ຖືກຕ້ອງ"),
    USER_IS_LOCKED("error.msg.user.is.locked","Tài khoản đã bị khóa, vui lòng liên hệ admin để mở khóa","ບັນຊີຖືກລອັກ, ກະລຸນາຕິດຕໍ່ແອັດມິນເພື່ອປົດລອັກ"),
    ERROR_OTP_EXPIRED("error.user.otp.expired","Mã OTP đã hết hiệu lực","ລະຫັດຢືນຢັັນ OTP ໝົດອາຍ"),
    PHONE_ALREADY_REGISTED("error.msg.user.phone.number.already.registered","Số điện thoại đã tồn tại, Vui lòng nhập Số điện thoại khác","ເບີໂທນີ້ມີຄົນໃຊ້ງານແລ້ວ, ກະລຸນາໃສ່ເບີໂທໃໝ່"),
    DATA_INVALID("error.msg.data.invalid","Dữ liệu không hợp lệ","ຂໍ້ມູນບໍ່ຖືກຕ້ອງ"),
    USERNAME_INVALID("error.msg.user.username.invalid","Tên đăng nhập từ 6-20 ký tự chữ và số, phải bắt đầu bằng chữ cái","ຊື່ື້ຜູ້ໃຊ້ຕ້ອງມີ ຕົວອັກສອນ ແລະ ຕົວເລກ ຢູ່ລະຫວ່າງ 6-20 ຕົວ, ຕ້ອງຂຶ້ນຕົ້ນດ້ວຍຕົວອັກສອນ"),
    PASSWORD_INVALID("error.msg.user.password.invalid","Mật khâu từ 6-20 Ký Tự, phải bao gồm chữ cái và số","ລະຫັດຜ່ານຕ້ອງມີຄວາມຍາວ 6-20 ຕົວ, ຕ້ອງມີທັງຕົວອັກສອນ ແລະ ຕົວເລກ"),
    PASSWORD_WITHDRAW_NOT_CREATED("error.msg.user.password.withdraw.has.not.created","Chưa tạo mã PIN rút tiền","คุณยังไม่ได้สร้างรหัส PIN สำหรับการถอนเงิน"),
    PASSWORD_WITHDRAW_NOT_MATCH("error.msg.password.withdraw.does.not.match","Mã PIN ko chính xác, vui lòng nhập lại mã PIN","ขออภัยเนื่องจากคุณได้ป้อนรหัส PIN ไม่ถูกต้อง โปรดลองป้อนรหัส PIN อีกครั้ง"),
    WITHDRAW_NOT_ACCEPT_BY_BET_MONEY("error.msg.widthraw.not.accept","Vui lòng cược thêm %s KIP nữa để rút","ກະລຸນາວາງເດີມພັນ %s KIP ເພີ່ມເຕີມເພື່ອຖອນ"),

    LOTTERY_INVALID("error.msg.lottery.invalid","Không tìm thấy thông tin xổ số","ບໍ່ພົບຂໍ້ມູນຫວຍ"),
    LOTTERY_MODE_INVAILID("error.msg.lottery.mode.invalid","Không mua được vé ở chế độ này","ບໍ່ສາມາດຊື້ປີ້ໄດ້ໃນໂໝດນີ້"),
    LOTTERY_TICKET_INVALID("error.msg.lottery.ticket.invalid","Thông tin vé không chính xác","ຂໍ້ມູນປີ້ບໍ່ຖືກຕ້ອງ"),

    ;


    private final String code;
    private final String viMsg;
    private final String loMsg;

    ExceptionCode(String code, String viMessage, String loMsg) {
        this.code =code;
        this.viMsg = viMessage;
        this.loMsg = loMsg;
    }

    public String getCode() {
        return code;
    }

    public String getViMsg() {
        return viMsg;
    }
    public String getLoMsg() {
        return loMsg;
    }
}
