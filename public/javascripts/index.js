$(document).ready(function() {

    const titleScreenMusic = $("#titleScreenMusic")[0]
    titleScreenMusic.volume = 0.2;
    titleScreenMusic.play();

    const loadingScreen =  $("#loading-screen");
    loadingScreen.show();

    setTimeout(function () {
        loadingScreen.hide();
    }, 2000);

    $("#rulesButton").click(() => {
        $("#buttonSound")[0].play();
        window.open("/rules", "_blank");
    });

    $("#startButton").click(() => {
        redirectTo("/playerNames");
    });

    function redirectTo(path) {
        $("#buttonSound")[0].play();

        setTimeout(() => {
            window.location.href = `${path}?position=${titleScreenMusic.currentTime}`;
        }, 300);
    }
});