$(document).ready(function() {
    $('#flipbook').turn({
        gradients: true,
        acceleration: true,
        autoCenter: false,
        when: {
            turning: function (event, page) {
                if (page === 1 || page === 2 || page === 11) {
                    $("#coverTurnSound")[0].play();
                } else {
                    $("#pageTurnSound")[0].play();
                }

                if (page === 1) {
                    $('.front-cover-back').removeClass('fixed');
                    $('.bookContainer').css('transform', 'translateX(-19vh)');
                } else {
                    $('.front-cover-back').addClass('fixed');
                    $('.bookContainer').css('transform', 'translateX(0)');
                }
            }
        }
    });
});

