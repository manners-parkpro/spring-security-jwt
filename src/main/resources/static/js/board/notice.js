var ready = () => {
    if (dto != null) fileLoad();
}

var fileLoad = () => {
    $.each(dto.attachments, function(idx, f) {
        if (f.fullUri === "null" || f.fullUri === null) return false;
        var $fileWrapper = $('#file-wrapper'),
            $filePanel = $fileWrapper.find('.file-body');

        $fileWrapper.removeClass('hide');
        $filePanel.append(drawFileTags(f));
        $filePanel.find(".file-info").last().data("file_data", f);
    });
}

var fileUpload = function() {
    documentUpload({
        multiple: false,
        position: 0,
        callback: function (data) {
            if (data.rs_st === 0) {
                var fileData = data.rs_data,
                    $fileWrapper = $('#file-wrapper'),
                    $filePanel = $fileWrapper.find('.file-body');

                $fileWrapper.removeClass('hide');
                $filePanel.append(drawFileTags(fileData));
                $filePanel.find(".file-info").last().data("file_data", fileData);
            }
        }
    });
};

var fileRemove = function() {
    var $this = $(this),
        fileInfoLength = $('.file-info').length;

    commonModal({
        contents: "파일을 삭제하시겠습니까?",
        submit: function() {
            $this.closest(".file-info").remove();

            if (fileInfoLength === 1)
                $('#file-wrapper').addClass('hide');
        }
    });
};

var isModify = function($form) {
    if ($form.find('input[name=id]').val() === null || $form.find('input[name=id]').val() === '') return false;
    return true;
};
var validation = function($form) {
    if ($form.find("input[name='title']").val() === null || $form.find("input[name='title']").val() === '') {
        oneBtnModal("제목을 입력해주세요.", function() {
            $form.find("input[name='title']").focus();
        });

        return false;
    }

    if ($form.find("textarea[name='content']").val() === null || $form.find("textarea[name='content']").val() === '') {
        oneBtnModal("내용을 입력해주세요.", function() {
            $form.find("textarea[name='content']").focus();
        });

        return false;
    }

    return true;
}

var save = function() {
    var $form = $('form[name=formRegister]');

    if (!validation($form)) return false;

    var files = [];
    $form.find(".file-info").each(function() {
        files.push($(this).data('file_data'));
    });

    var params = {
        id: $form.find("input[name='id']").val(),
        title: $form.find('input[name=title]').val(),
        content: $form.find("textarea[name='content']").val(),
        fixedTopYn: $form.find('input[name=fixedTopYn]:checked').val(),
        activeYn: $form.find('input[name=activeYn]:checked').val(),
        attachments: files
    };

    commonModal({
        contents: "저장하시겠습니까?",
        submit: function() {
            $.ajax({
                url: "/board/notice/ajax/save",
                method: "post",
                type: "json",
                contentType: "application/json",
                data: JSON.stringify(params),
                success: function (response) {
                    var message = isModify($form) ? '정상적으로 수정되었습니다.' : '정상적으로 저장되었습니다.';

                    oneBtnModal(message, function() {
                        location.href = '/board/notice/edit/' + response.data;
                    });
                }
            });
        }
    });
};

$(document).ready(ready)
    .on('click', '.fileUpload', fileUpload)
    .on('click', '.btn-danger', fileRemove)
    .on('click', '#btnSave', save);
