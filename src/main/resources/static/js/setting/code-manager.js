var ready = function() {

};

var groupCode = function(e) {
    e.preventDefault();
    var $modal = $('#code-modal'),
        groupCode = $(this).data('group-code');

    getChildrenCode($(this), $modal, groupCode);
};

var getChildrenCode = function($this, $modal, groupCode) {

    $.get('/setting/code-manager/ajax/code/' + groupCode, function (response) {
        if (response.code === 200) {

            $.each(response.data, function(idx, i) {
                idx ++;
                $('.code-children-body').append(drawCodeTags(idx, i));
            });

            $this.addClass('active');
            $('#btn-child-code').removeClass('hide');
            $('.children-wrapper').removeClass('hide')
            $modal.find('.group-code-wrapper').addClass('hide');
            $this.closest('ul').find('li').removeAttr('style');
            $this.css({'background-color': '#E0E0E0'});
            $modal.find('input[name=group]').val(groupCode);
        }
    });
};

var code = function(e) {
    e.preventDefault();
    var $modal = $('#code-modal');

    if (!$('input[name=group]').val()) {
        oneBtnModal("부모코드를 선택해주세요.", function() {});
        return false;
    }

    $('input[name=codeType]').val('code');
    $modal.find('.group-code-wrapper').removeClass('hide');
    $modal.find('input[name=code]').val('');
    $modal.find('input[name=value]').val('');
    $modal.find('textarea[name=description]').val('');
    $modal.find('#activeYn_Y').iCheck('check');
    $modal.modal('show');
};

var modifyChlid = function(e) {
    e.preventDefault();
    var $this = $(this),
        $modal = $('#code-modal');

    if (!$('input[name=group]').val()) {
        oneBtnModal("부모코드를 선택해주세요.", function() {});
        return false;
    }

    $('input[name=codeType]').val('code');
    $('input[name=modifyType]').val('modify');

    var childCode = $this.find('.child-code').data('child-code'),
        childValue = $this.find('.child-value').data('child-value'),
        childDescription = $this.find('.child-description').data('child-description'),
        childActive = $this.find('.active').data('child-active');

    $modal.find('input[name=code]').val(childCode);
    $modal.find('input[name=value]').val(childValue);
    $modal.find('textarea[name=description]').val(childDescription);
    $('.group-code-wrapper').removeClass('hide');
    $modal.find('.btn-danger').attr('data-code-id', $this.find('input[name=id]').val());

    if (childActive === 'Y')
        $modal.find('#activeYn_Y').iCheck('check');
    else
        $modal.find('#activeYn_N').iCheck('check');

    $modal.modal('show');
};

var remove = function(e) {
    e.preventDefault();

    $.post('/setting/code-manager/ajax/code/remove/' + $(this).data('code-id'), function (response) {
        if (response.code === 200) {
            oneBtnModal('정상적으로 삭제되었습니다.', function() {
                location.reload();
            });
        }
    });
};

var validation = function($modal) {

    if (!$modal.find('input[name=code]').val()) {
        oneBtnModal("제목을 입력해주세요.", function() {
            $modal.find('input[name=code]').focus();
        });

        return false;
    }

    if ($('input[name=codeType]').val() === 'code') {
        if (!$modal.find('input[name=group]').val()) {
            oneBtnModal("제목을 입력해주세요.", function() {
                $modal.find('input[name=group]').focus();
            });

            return false;
        }
    }

    if (!$modal.find('input[name=value]').val()) {
        oneBtnModal("제목을 입력해주세요.", function() {
            $modal.find('input[name=value]').focus();
        });

        return false;
    }

    if (!$modal.find('textarea[name=description]').val()) {
        oneBtnModal("내용을 입력해주세요.", function() {
            $modal.find('textarea[name=description]').focus();
        });

        return false;
    }

    if (!$modal.find('input[name=activeYn]:checked').val()) {
        oneBtnModal("활성화 여부를 선택해주세요.", function() {
            $modal.find('input[name=activeYn]').focus();
        });

        return false;
    }

    return true;
};

var save = function(e) {
    e.preventDefault();
    var $modal = $('#code-modal');

    if (!validation($modal)) return false;

    var group;
    if ($('input[name=codeType]').val() === 'code')
        group = {code : $modal.find('input[name=group]').val()};

    var params = {
        code : $modal.find('input[name=code]').val(),
        group : group,
        value : $modal.find('input[name=value]').val(),
        description : $modal.find('textarea[name=description]').val(),
        codeType: $('input[name=codeType]').val(),
        modifyType: $('input[name=modifyType]').val(),
        activeYn: $('input[name=activeYn]:checked').val()
    };

    commonModal({
        contents: "저장하시겠습니까?",
        submit: function() {
            $.ajax({
                url: "/setting/code-manager/ajax/code/save",
                method: "post",
                type: "json",
                contentType: "application/json",
                data: JSON.stringify(params),
                success: function () {
                    oneBtnModal('정상적으로 저장되었습니다.', function() {
                        location.reload();
                    });
                }
            });
        }
    });
};


$(document).ready(ready)
    .on('click', '.group-code', groupCode)
    .on('click', '.btn-code', code)
    .on('click', '.code-info', modifyChlid)
    .on('click', '.btn-danger', remove)
    .on('click', '#btn-save', save);
