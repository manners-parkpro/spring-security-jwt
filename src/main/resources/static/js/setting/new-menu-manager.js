var menu = function(e) {
    e.preventDefault();
    var $this = $(this),
        id = $this.attr('data-menu-id');

    $('#pattern-all').prop('disabled', false);

    if ($this.hasClass('parents'))
        parentsHandle(id);
    else
        childrenHandle(id);
};

var radio = function(e) {
    e.preventDefault();

    $('#p-title').addClass('hide');
    $('#uri').prop('readonly', false);

    if ($(this).val() === 'All') {
        $('#uri').val('');
        $('#p-title').removeClass('hide');
    } else {
        $('#uri').prop('readonly', true);
        $('#uri').val('/');
        $('#parents-title').val('');
    }
};

var parentsHandle = function(id) {
    $('#pattern-all').prop('disabled', true);
    getMenuByAjax(id);
};

var childrenHandle = function(id) {
    getMenuByAjax(id);
};

var register = function(e) {
    e.preventDefault();
    $('#pattern-all').iCheck('uncheck');
    $('#pattern-single').iCheck('check');
    $('input:radio[name=optPattern]').prop('disabled', false);
    $('#uri').prop('disabled', false);
    $('#uri').prop('readonly', false);
    $('#parents-title').prop('readonly', false);
    $('form[name=formMenu]').find('input[name=id]').val('');
    $('input:text').val('');
};

var validation = function() {

    if (!$('#title').val()) {
        oneBtnModal("메뉴명을 입력해주세요.", function() {
            $('#title').focus();
        });

        return false;
    }

    if ($('input:radio[name=optPattern]:checked').val() === 'All' && !$('#parents-title').val()) {
        oneBtnModal("부모 Menu명을 입력해주세요.", function() {
            $('#parents-title').focus();
        });

        return false;
    }

    if (!$('#uri').val()) {
        oneBtnModal("URL을 입력해주세요.", function() {
            $('#uri').focus();
        });

        return false;
    }

    if ($('input:radio[name=optPattern]:checked').val() === 'Single' && $('#uri').val() !== '/') {
        oneBtnModal("Single은 URL 패턴이 / 만 가능합니다.", function() {
            $('#uri').focus();
        });

        return false;
    }

    if (!$('#sortOrder').val()) {
        oneBtnModal("정렬순서를 입력해주세요.", function() {
            $('#sortOrder').focus();
        });

        return false;
    }

    return true;
}

var save = function(e) {
    e.preventDefault();

    if (!validation()) return false;

    var params = {
        id: $('form[name=formMenu]').find('input[name=id]').val(),
        menuName: $('#title').val(),
        parentsMenuName: $('input:radio[name=optPattern]:checked').val() === 'All' ? $('#parents-title').val() : null,
        url: $('#uri').val(),
        antMatcherType: $('input:radio[name=optPattern]:checked').val(),
        sortOrder: $('#sortOrder').val()
    };

    commonModal({
        contents: "저장하시겠습니까?",
        submit: function() {
            $.ajax({
                url: "/setting/menu-manager/ajax/menu/save",
                method: "post",
                type: "json",
                contentType: "application/json",
                data: JSON.stringify(params),
                success: function (response) {
                    console.log(response);
                    var message = response.code === 200 ? '정상적으로 저장되었습니다.' : response.message;

                    oneBtnModal(message, function() {
                        location.reload();
                    });
                }
            });
        }
    });
};

var getMenuByAjax = function(id) {
    if (!id) return false;

    $('form[name=formMenu]').find('input[name=id]').val(id);

    $.get('/setting/menu-manager/ajax/menu/' + id, function (response) {
        if (response.code === 200) {
            settingMenu(response.data);
        }
    });
};

var settingMenu = function(data) {
    var menuName = data.menuName,
        antMatcherType = data.antMatcherType,
        url = data.url,
        sortOrder = data.sortOrder;

    if (url !== '/') {
        var parentsTitle = url.split('/')[1],
            $parents = $('#parents-title');
        $parents.val(parentsTitle.substring(0, 1).toUpperCase() + parentsTitle.substring(1).toLowerCase());
        $parents.prop('readonly', true);
    }

    $('#uri').prop('disabled', false);
    $('input:checkbox').prop('checked', false);

    if (antMatcherType === 'Single') {
        $('#pattern-all').iCheck('uncheck');
        $('#pattern-all').prop('disabled', true);
        $('#pattern-single').iCheck('check');
        $('#uri').prop('disabled', true);
    } else {
        $('#pattern-single').iCheck('uncheck');
        $('#pattern-single').prop('disabled', true);
        $('#pattern-all').iCheck('check');
    }

    $('#title').val(menuName);
    $('#uri').val(url);
    $('#sortOrder').val(sortOrder);
};

$(document).ready()
    .on('click', '.menu', menu)
    .on('ifChecked', 'input:radio[name=optPattern]', radio)
    .on('click', '#btn-register', register)
    .on('click', '#btn-save', save);
