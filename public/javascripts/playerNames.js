$(document).ready(function () {

    const titleScreenMusic = $("#titleScreenMusic")[0]
    titleScreenMusic.volume = 0.2;
    setTitlePosition(titleScreenMusic);

    $("#player1Name").on('input', () => {
        const inputLength = $("#player1Name").val().length;

        if (inputLength === 0) {
            $("#cursor1").css('left', '0px');
        } else {
            $("#cursor1").css('left', `${inputLength * 1.5}vh`);
        }
    });

    $("#player2Name").on('input', () => {
        const inputLength = $("#player2Name").val().length;

        if (inputLength === 0) {
            $("#cursor2").css('left', '0px');
        } else {
            $("#cursor2").css('left', `${inputLength * 1.5}vh`);
        }
    });

    $('#confirmButton').click(function() {
        $("#buttonSound")[0].play();

        validatePlayerNames($("#player1Name").val(), $("#player2Name").val())
    })

    function validatePlayerNames(player1Name, player2Name) {

        if (player1Name.trim() === "" && player2Name.trim() === "") {
            displayValidationAlert("Please enter a name for player 1.", "player1Name");
            displayValidationAlert("Please enter a name for player 2", "player2Name");
        } else if (player1Name.trim() === "") {
            displayValidationAlert("Please enter a name for player 1.", "player1Name");
        } else if (player2Name.trim() === "") {
            displayValidationAlert("Please enter a name for player 2.", "player2Name");
        } else {
            setTimeout(() => {
                sendPlayerNames(player1Name, player2Name)
            }, 300);
        }
    }

    function sendPlayerNames(player1Name, player2Name) {

        const position = titleScreenMusic.currentTime;

        fetch(`http://localhost:9000/playerNames`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                player1Name: player1Name,
                player2Name: player2Name,
            })
        }).then(response => response.json())
            .then(data => {
                window.location.href = `${data.redirect}?position=${position}`;
            }).catch(error => {
            console.error("Error:", error);
        });
    }
});