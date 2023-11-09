$(function () {
    $('#btnUploadVideoUp').on('click', function(event) {
        event.preventDefault();
        // Get form
        let data = new FormData($("#uploadVideoFormUp")[0]);
        $("#btnUploadVideoUp").prop("disabled", true);

        let _csrf_token = $('meta[name="_csrf"]').attr('content');
        let _csrf_header = $('meta[name="_csrf_header"]').attr('content');


        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: "/files/uploadVideo",
            data: data,
            processData: false, //prevent jQuery from automatically transforming the data into a query string
            contentType: false,
            cache: false,
            timeout: 600000,
            headers : {
                [_csrf_header] :  _csrf_token
            },
            success: function (data) {
                $('.formUpdate #duration').val(data.duration);
                $('.formUpdate #url').val(data.url);
                $("#btnUploadVideoUp").prop("disabled", false);
            },
            error: function (e) {
                $("#result").text(e.responseText);
                console.log("ERROR : ", e);
                $("#btnUploadVideoUp").prop("disabled", false);
            }
        });
    });
});