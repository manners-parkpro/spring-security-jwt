var roles = function(e) {
    e.preventDefault();
    var role = $(this).text();

    if (!role) return false;

    $('input[name=roleName]').val(role);
    $(this).closest('table').find('tr').removeAttr('style');
    $(this).closest('tr').css({'background-color': '#E0E0E0'});

    $.get('/setting/admin-role-manager/ajax/role/' + role, function (response) {
        if (response.code === 200) {
            $('input:checkbox').iCheck("uncheck");
            settingAdminRole(response.data);
        }
    });
};

var settingAdminRole = function(datas) {
    if ($.isEmptyObject(datas)) return false;

    $.each(datas, function(idx, p) {
        if (!$.isEmptyObject(p.children)) {
            $.each(p.children, function(i, c) {
                $('[data-menu-id=' + c.id + ']').iCheck('check');
            });
        } else
            $('[data-menu-id=' + p.id + ']').iCheck('check');
    });
};

var checkParentsElementHandle = function(e) {
    e.preventDefault();
    var $this = $(this),
        $element = $this.closest('tr').find('input:checkbox[name=children]');

    if ($this.is(':checked'))
        $element.iCheck('check');
    else
        $element.iCheck('uncheck');
};

var checkChildrenElementHandle = function(e) {
    e.preventDefault();
    var $this = $(this),
        $wrapper = $this.closest('tr'),
        $element = $wrapper.find('input:checkbox[name=children]'),
        $parentsElement = $wrapper.find('input:checkbox[name=parents]');

    if ($element.filter(':checked').length === 0)
        $parentsElement.iCheck('uncheck');
    else
        $parentsElement.iCheck('check');
};

var save = function(e) {
    e.preventDefault();

    if (!$('input[name=roleName]').val()) {
        oneBtnModal("저장하실 Role을 클릭 해주세요.", function() {});

        return false;
    }

    var $checked = $('.checkElement:checked'),
        $unChecked = $('.checkElement:not(:checked)');

    var roles = [];
    var role = { roleName: $('input[name=roleName]').val() };
    roles.push(role);

    var idxs = [];
    $unChecked.each(function() {
       idxs.push($(this).data('menu-id'));
    });

    var ids = [];
    $checked.each(function() {
        ids.push($(this).data('menu-id'));
    });

    var params = {
        ids : ids,
        idxs: idxs,
        roles : roles
    };

    commonModal({
        contents: "저장하시겠습니까?",
        submit: function() {
            $.ajax({
                url: "/setting/admin-role-manager/ajax/save/role",
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

$(document).ready()
    .on('click', '.roles', roles)
    //.on('ifChanged', 'input:checkbox[name=parents]', checkParentsElementHandle)
    .on('ifChanged', 'input:checkbox[name=children]', checkChildrenElementHandle)
    .on('click', '#btn-save', save);
