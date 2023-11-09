function submitTempContentForm() {
    let justHtml = $('#contentEditor').summernote('code')
    $('#inputItemHtml').val( justHtml );
    $("#form1").submit();
}

function editTempContentForm() {
    let justHtml = $('#contentEditor').summernote('code')
    $('#inputItemHtml').val( justHtml );
    $("#formUpTemplateContent").submit();
}

function showHTML() {
    let justHtml = $('#contentEditor').summernote('code');
    $('#showHTML').text(justHtml);
}