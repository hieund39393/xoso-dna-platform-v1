$(function () {

    $('.lotteryNewBtn, .table .lotteryEditBtn').on('click', function(event) {
        event.preventDefault();
        var href = $(this).attr('href');
        var text = $(this).text();
        if (text === 'Edit') {
            $.get(href, function (lotteryData) {
                $('.formUpdate #id').val(lotteryData.id);
                $('#name').val(lotteryData.name);
                $('#code').val(lotteryData.code);
                $('#lotteryCategoryIdUp').val(lotteryData.lotteryCategoryId);
                $('#typeUp').val(lotteryData.type);
                if (lotteryData.modes !== undefined && lotteryData.modes !== null) {
                    $('.select2').val(lotteryData.modes.split("-"));
                    $('.select2').trigger("change");
                }
                $('#hour').val(lotteryData.hour);
                $('#min').val(lotteryData.min);
                $('#sec').val(lotteryData.sec);
                $('#active').prop("checked", lotteryData.active);
            });
            $('.formUpdate #updateModal').modal();
        } else {
            $('.formCreate #name').val('');
            $('.formCreate #code').val('');
            $('.formCreate #lotteryCategoryId').val('');
            $('.formCreate #type').val('');
            $('.select2').val([]);
            $('.select2').trigger("change");
            $('.formCreate #hour').val(0);
            $('.formCreate #min').val(0);
            $('.formCreate #sec').val(0);
            $('.formCreate #active').prop("checked");
            $('.formCreate #modalCreate').modal();
        }
    });
    $('.table .setupResultBtn').on('click', function(event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (lotteryData) {
            $('.formSetupResult #id').val(lotteryData.id);
            $('.formSetupResult #result').val('');
        });
        $('.formSetupResult #setupResultModal').modal();
    });
});