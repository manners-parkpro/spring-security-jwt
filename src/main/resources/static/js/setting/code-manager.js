$(document).ready(function () {
    $.get('/setting/code-manager/ajax/code?type=group', function (resp) {
        if (resp.code === 200) {
            var children = []
            $(resp.data).each(function (idx, d) {
                children.push({
                    id: d.code,
                    data: d,
                    text: d.description
                })
            });

            $('.code-panel').jstree({
                core: {
                    multiple: false,
                    check_callback: true,
                    themes: {
                        stripes: true,
                        responsive: false,
                        variant: 'small',
                    }, data: {
                        text: 'Code Manager',
                        state: {
                            opened: true,
                        },
                        icon: 'jstree-folder',
                        children: children
                    }
                },
                types: {
                    default: {icon: 'fa fa-folder-o'}
                },
                plugins: [
                    "changed",
                    "types",
                    "wholerow",
                ]
            })
            ;
        }
    });
}).on("click", '.code-list .code', function () {
    $.get('/setting/code-manager/ajax/code/' + $(this).text(), function (resp) {
            if (resp.code === 200) {
                var data = resp.data;
                $('#code').val(data.code);
                $('#code-description').val(data.description);
                $('#code-value').val(data.value);
                $('#code-save-type').val("modify");
                $('#org-code').val(data.code);
                if (data.activeYn === "Y") {
                    $("#code-active").iCheck('check');
                } else {
                    $("#code-inactive").iCheck('check');
                }
                $('#code-editor').modal('show')
            } else if (resp.code === 404) {
                alert('코드를 찾을 수 없습니다.')
            }

        }
    );
});


/**
 * tree node click
 */
$('.code-panel').on("select_node.jstree", function (e, data) {

    //초기화
    $('#save-type').val('new');
    $("#active").iCheck('check');
    $('#group-code').val('');
    $('#org-group-code').val('');
    $('#description').val('');
    $('#codes-panel').hide();

    if (data.node.parent === "#") {
        $('.btn-new-group').attr('disabled', false);
    } else {
        $('.btn-new-group').attr('disabled', true);
        var rawData = data.node.data;
        if (rawData.code !== "temp") {
            $('#save-type').val('modify');
            $('#org-group-code').val(rawData.code);
            $('#group-code').val(rawData.code);
            $('#description').val(rawData.description);
            if (rawData.activeYn === "Y") {
                $("#active").iCheck('check');
            } else {
                $("#inactive").iCheck('check');
            }

            loadCodes(rawData.code);
            $('#codes-panel').show();


        }
    }
});
/**
 * code load
 */
var loadCodes = function () {
    var $codeList = $('.code-list');
    $codeList.empty();
    var groupCode = $('#org-group-code').val();
    $.get('/setting/code-manager/ajax/code?groupCode=' + groupCode, function (resp) {
        if (resp.code === 200) {

            $(resp.data).each(function (idx, d) {
                var item = '<li class="row">' +
                    '<div class="col-sm-1">' +
                    '   <span class="handle">' +
                    '       <i class="fa fa-ellipsis-v"></i>' +
                    '       <i class="fa fa-ellipsis-v"></i>' +
                    '   </span>' +
                    '</div>' +
                    '<div class="col-sm-2 code">' + d.code + '</div>' + // code
                    '<div class="col-sm-4">' + d.value + '</div>' + // value
                    '<div class="col-sm-3">' + d.description + '</div>' + // description
                    '<div class="col-sm-2">' + d.activeYn + '</div>' + // activeYn
                    '</li>';
                $codeList.append(item);
            });

            $codeList.sortable({
                placeholder: 'sort-highlight',
                handle: '.handle',
                forcePlaceholderSize: true,
                zIndex: 999999
            });
        }
    });
};

/**
 * create new group
 */
$('.btn-new-group').on("click", function () {
    var $codePanel = $(".code-panel");

    if ($codePanel.jstree("get_selected").length > 0) {
        var parent_id = $codePanel.jstree("get_selected")[0];
        var parent_node = $codePanel.jstree("get_node", parent_id);

        if (parent_node.parent === '#') {
            $codePanel.jstree("create_node", parent_node, {
                data: {code: 'temp'},
                icon: 'fa fa-folder',
                text: 'new group'
            }, "last", function (new_node) {
                $codePanel.jstree("open_node", $codePanel.jstree("get_selected"));
                $codePanel.jstree('deselect_node', parent_node);
                $codePanel.jstree('select_node', new_node);
            });
        } else {
            alert("하위 그룹을 생성 할 수 없습니다.")
        }
    } else {
        alert("그룹을 선택해 주세요");
    }
});
/**
 * Group info save
 */
$('.btn-info-save').on('click', function () {
    var description = $('#description').val();
    if (description === "") {
        alert('description is required.');
        return;
    }

    var groupCode = $('#group-code').val();
    if (groupCode === "") {
        alert('Group Code is required.');
        return;
    }

    var param = {
        description: description,
        code: groupCode,
        activeYn: $('input[name="optActive"]:checked').val(),
    };


    $.post('/setting/code-manager/ajax/code/save?type=' + $('#save-type').val() + "&orgCd=" + $('#org-group-code').val(), param, function (resp) {
        if (resp.code === 200) {
            location.reload();
        } else if (resp.code === 501) {
            alert('코드값은 필수입니다.');
        } else if (resp.code === 502) {
            alert('이미 등록된 코드 입니다.');
        } else {
            alert('코드값을 찾을 수 없습니다.');
        }
    });
});

/**
 * code 추가
 */
$('.btn-add-code').on("click", function () {
    $('#code-save-type').val("new");
    $('#org-code').val("");
    $('#code').val('');
    $('#code-description').val('');
    $('#code-value').val('');
    $("#code-active").iCheck('check');
    $('#code-editor').modal('show')
});


/**
 * code info save
 */
$('.btn-code-save').on('click', function () {

    var code = $('#code').val();
    if (code === "") {
        alert('Code is required.');
        return;
    }
    var value = $('#code-value').val();
    if (code === "") {
        alert('Code is required.');
        return;
    }
    var groupCode = $('#group-code').val();
    if (groupCode === "") {
        alert('Group Code is required.');
        return;
    }

    var param = {
        description:  $('#code-description').val(),
        code: code,
        value: value,
        "group.code": groupCode,
        activeYn: $('input[name="optCodActive"]:checked').val(),
    };


    $.post('/setting/code-manager/ajax/code/save?type=' + $('#code-save-type').val() + "&orgCd=" + $('#org-code').val(), param, function (resp) {
        if (resp.code === 200) {
            loadCodes();
            $('#code-editor').modal('hide');
        } else if (resp.code === 501) {
            alert('코드값은 필수입니다.');
        } else if (resp.code === 502) {
            alert('이미 등록된 코드 입니다.');
        } else {
            alert('코드값을 찾을 수 없습니다.');
        }
    });
});