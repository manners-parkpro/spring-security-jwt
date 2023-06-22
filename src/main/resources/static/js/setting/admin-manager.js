$(document).ready(function () {
    if (adminData) {
        $.each(adminData.roles, function (idx, role) {
            $('#exclude-role option[value="' + role.roleName + '"]').hide();
            $('#include-role option[value="' + role.roleName + '"]').show();
        })
    }
});


$('#btn-include-role').on('click', function () {
    var role = $('#exclude-role option:checked').val();
    $('#exclude-role option[value="' + role + '"]').hide();
    $('#include-role option[value="' + role + '"]').show();
});

$('#btn-exclude-role').on('click', function () {
    var role = $('#include-role option:checked').val();
    $('#exclude-role option[value="' + role + '"]').show();
    $('#include-role option[value="' + role + '"]').hide();
});


$('.btn-save').on('click', function () {
    var username = $('#username').val();
    if (username === "") {
        alert('Username is required.');
        return;
    }

    var password = $('#password').val();
    var passwordConfirm = $('#password-confirm').val();
    if (password !== passwordConfirm) {
        alert('Invalid password confirm.');
        return;
    }

    var param = {
        username: username,
        password: password,
        name: $('#name').val(),
        email: $('#email').val(),
        activeYn: $('input[name="optActive"]:checked').val()
    };

    var id = $('#account-id').val();
    if (id !== 'new') {
        param["id"] = id;
    }

    var isEmptyRole = true;
    $('#include-role option').filter(function () {
        return $(this).css('display') === 'block';
    }).each(function (idx, role) {
        param["roles[" + idx + "].roleName"] = $(role).val();
        param["roles[" + idx + "].description"] = $(role).text();
        isEmptyRole = false;
    });

    if (isEmptyRole) {
        alert('Role is required.');
        return;
    }

    console.log(param);

    $.post('/setting/admin-manager/ajax/save', param, function (resp) {
        if (resp.code === 200) {
            location.href = '/setting/admin-manager/' + resp.data;
            //location.reload();
        } else {
            alert("Error: " + resp.message);
        }
    });
});
