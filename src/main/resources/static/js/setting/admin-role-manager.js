var roles = function(e) {
    e.preventDefault();
    console.log('roles');
};

$(document).ready()
    .on('click', '.roles', roles);
