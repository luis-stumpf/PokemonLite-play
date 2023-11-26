$(document).ready(function () {

    const loadingScreen =  $("#loading-screen");

    const titleScreenMusic = $("#titleScreenMusic")[0]
    titleScreenMusic.volume = 0.2;
    setTitlePosition(titleScreenMusic);

    $('#confirmButtonPokemons').click(function() {
        $("#buttonSound")[0].play();

        validatePlayerPokemons($("#player1Pokemon1").val(), $("#player1Pokemon2").val(), $("#player1Pokemon3").val(),
                                $("#player2Pokemon1").val(), $("#player2Pokemon2").val(), $("#player2Pokemon3").val())
    })

    function validatePlayerPokemons(player1Pokemon1, player1Pokemon2, player1Pokemon3,
                                    player2Pokemon1, player2Pokemon2, player2Pokemon3) {
        if ((player1Pokemon1 === null || player1Pokemon2 === null || player1Pokemon3 === null)
            && (player2Pokemon1 === null || player2Pokemon2 === null || player2Pokemon3 === null)) {
            displayValidationAlert("Please select your pokemons player 1.", "selection1");
            displayValidationAlert("Please select your pokemons player 2.", "selection2");
        } else if (player1Pokemon1 === null || player1Pokemon2 === null || player1Pokemon3 === null) {
            displayValidationAlert("Please select your pokemons player 1.", "selection1");
        } else if (player2Pokemon1 === null || player2Pokemon2 === null || player2Pokemon3 === null) {
            displayValidationAlert("Please select your pokemons player 2.", "selection2");
        } else {
            loadingScreen.show();

            setTimeout(function () {
                sendPlayerPokemons(player1Pokemon1, player1Pokemon2, player1Pokemon3,
                    player2Pokemon1, player2Pokemon2, player2Pokemon3);
            }, 2500);
        }
    }

    function sendPlayerPokemons(player1Pokemon1, player1Pokemon2, player1Pokemon3,
                                player2Pokemon1, player2Pokemon2, player2Pokemon3) {
        fetch(`http://localhost:9000/playerPokemons`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                player1Pokemon1: player1Pokemon1,
                player1Pokemon2: player1Pokemon2,
                player1Pokemon3: player1Pokemon3,
                player2Pokemon1: player2Pokemon1,
                player2Pokemon2: player2Pokemon2,
                player2Pokemon3: player2Pokemon3,
            })
        }).then(response => response.json())
            .then(data => {
                window.location.href = data.redirect;
            }).catch(error => {
            console.error("Error:", error);
        });
    }
});