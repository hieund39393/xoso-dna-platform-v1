$(function () {

    $('.lotteryCategoryNewBtn, .table .lotteryCategoryEditBtn').on('click', function(event) {
        event.preventDefault();
        var href = $(this).attr('href');
        var text = $(this).text();
        if (text === 'Edit') {
            $.get(href, function (lotteryCategoryData) {
                $('.formUpdate #id').val(lotteryCategoryData.id);
                $('.formUpdate #name').val(lotteryCategoryData.name);
                $('.formUpdate #code').val(lotteryCategoryData.code);
                $('.formUpdate #active').prop("checked", lotteryCategoryData.active);
                $('.formUpdate #campaign').prop("checked", lotteryCategoryData.campaign);
            });
            $('.formUpdate #updateModal').modal();
        } else {
            $('.formCreate #name').val('');
            $('.formCreate #code').val('');
            $('.formCreate #active').prop("checked");
            $('.formCreate #campaign').prop("checked");
            $('.formCreate #modalCreate').modal();
        }
    });
});