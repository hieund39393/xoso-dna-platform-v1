$(function () {

    $('.lotteryModeNewBtn, .table .lotteryModeEditBtn').on('click', function(event) {
        event.preventDefault();
        var href = $(this).attr('href');
        var text = $(this).text();
        if (text === 'Edit') {
            $.get(href, function (lotteryModeData) {
                $('.formUpdate #id').val(lotteryModeData.id);
                $('.formUpdate #name').val(lotteryModeData.name);
                $('.formUpdate #code').val(lotteryModeData.code);
                $('.formUpdate #price').val(lotteryModeData.price);
                $('.formUpdate #prizeMoney').val(lotteryModeData.prizeMoney);
            });
            $('.formUpdate #updateModal').modal();
        } else {
            $('.formCreate #name').val('');
            $('.formCreate #code').val('');
            $('.formCreate #price').val('');
            $('.formCreate #prizeMoney').val('');
            $('.formCreate #modalCreate').modal();
        }
    });
});