var ready = () => {
    if (dto != null) fileLoad();
}

var fileLoad = () => {
    if (dto === null || dto === "") return false;

    $.each(dto.attachments, function(idx, f) {
        if (f.fullUri === "null" || f.fullUri === null) return false;
        var $fileWrapper = $(".attachment");
        var $filePanel = $fileWrapper.find('.filePanel');

        var tag = "<li style=\"position: relative;\" class=\"fileInfo\">";
        tag += "<button type=\"button\" class=\"btn btn-default btn-sm pull-right btnRemoveFile\" style=\"position: absolute; top: 5px; right: 5px;\"><i class=\"fa fa-trash-o\"></i></button>";
        tag += "<span class=\"mailbox-attachment-icon\"><i class=\"fa fa-file-text-o\"></i></span>";
        tag += "<div class=\"mailbox-attachment-info\">";
        tag += "<a href='/file/download?id=" + f.id  + "' class='mailbox-attachment-name'>";
        tag += "<i class=\"fa fa-paperclip\"></i> "+ f.orgFilename;
        tag += "</a>";
        tag += "</div>";
        tag += "</li>";

        $filePanel.append(tag);
        $filePanel.find(".fileInfo").last().data("file_data", f);
    });
}

var fileUpload = function() {
    var $this = $(this);

    documentUpload({
        multiple: false,
        position: 0,
        callback: function (data) {
            if (data.rs_st === 0) {
                var fileData = data.rs_data,
                    $fileWrapper = $this.closest('.fileWrapper'),
                    $filePanel = $fileWrapper.find('.filePanel');

                var tag = "<li style=\"position: relative;\" class=\"fileInfo\">";
                tag += "<button type=\"button\" class=\"btn btn-default btn-sm pull-right btnRemoveFile\" style=\"position: absolute; top: 5px; right: 5px;\"><i class=\"fa fa-trash-o\"></i></button>";
                tag += "<span class=\"mailbox-attachment-icon\"><i class=\"fa fa-file-text-o\"></i></span>";
                tag += "<div class=\"mailbox-attachment-info\">";
                tag += "<a href='/attachment/download?orgFilename=" + fileData.orgFilename + "&fullPath=" + fileData.fullPath + "' class='mailbox-attachment-name'>";
                tag += "<i class=\"fa fa-paperclip\"></i> "+ fileData.orgFilename;
                tag += "</a>";
                tag += "</div>";
                tag += "</li>";

                $filePanel.append(tag);
                $filePanel.find(".fileInfo").last().data("file_data", fileData);
            }
        }
    });
};

var fileRemove = function() {
    var $this = $(this);

    commonModal({
        contents: "파일을 삭제하시겠습니까?",
        submit: function() {
            $this.closest(".fileInfo").remove();
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
    $form.find(".fileInfo").each(function() {
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
    .on('click', '.btnRemoveFile', fileRemove)
    .on('click', '#btnSave', save);
