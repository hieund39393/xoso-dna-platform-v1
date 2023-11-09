$(function () {

    $('.lotteryVideoNewBtn, .table .lotteryVideoEditBtn, .table .lotteryVideoDelBtn').on('click', function(event) {
        event.preventDefault();
        var href = $(this).attr('href');
        var text = $(this).text();
        if (text === 'Edit') {
            $.get(href, function (lotteryVideoData) {
                $('.formUpdate #id').val(lotteryVideoData.id);
                $('.formUpdate #index').val(lotteryVideoData.index);
                $('.formUpdate #group').val(lotteryVideoData.group);
                $('.formUpdate #number').val(lotteryVideoData.number);
                $('.formUpdate #duration').val(lotteryVideoData.duration);
                $('.formUpdate #url').val(lotteryVideoData.url);
            });
            $('.formUpdate #updateModal').modal();
        } else {
            $('.formUpdate #index').val('');
            $('.formUpdate #group').val('');
            $('.formUpdate #number').val('');
            $('.formUpdate #duration').val('');
            $('.formUpdate #url').val('');
            $('.formCreate #modalCreate').modal();
        }
    });
    $('.table .delBtn').on('click', function(event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $('#removeModalCenter #delRef').attr('href', href);
        $('#removeModalCenter').modal();
    });
});