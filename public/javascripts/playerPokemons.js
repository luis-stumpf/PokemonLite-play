$(document).ready(function () {

    let player1Pokemons = [];
    let player2Pokemons = [];

    const loadingScreen =  $("#loading-screen");

    const titleScreenMusic = $("#titleScreenMusic")[0]

    titleScreenMusic.volume = 0.2;
    setTitlePosition(titleScreenMusic);

    $(document).on('change', '#player1Pokemon1', function() {

        const player1Pokemon1 = $(this).val()

        if (player1Pokemon1 !== null) {
            player1Pokemons.splice(0, 0, player1Pokemon1)
        }
    });

    $(document).on('change', '#player1Pokemon2', function() {

        const player1Pokemon2 = $(this).val()

        if (player1Pokemon2 !== null) {
            player1Pokemons.splice(1, 0, player1Pokemon2)
        }
    });

    $(document).on('change', '#player1Pokemon3', function() {

        const player1Pokemon3 = $(this).val()

        if (player1Pokemon3 !== null) {
            player1Pokemons.splice(2, 0, player1Pokemon3)
        }
    });

    $(document).on('change', '#player2Pokemon1', function() {

        const player2Pokemon1 = $(this).val()

        if (player2Pokemon1 !== null) {
            player2Pokemons.splice(0, 0, player2Pokemon1)
        }
    });

    $(document).on('change', '#player2Pokemon2', function() {

        const player2Pokemon2 = $(this).val()

        if (player2Pokemon2 !== null) {
            player2Pokemons.splice(1, 0, player2Pokemon2)
        }
    });

    $(document).on('change', '#player2Pokemon3', function() {

        const player2Pokemon3 = $(this).val()

        if (player2Pokemon3 !== null) {
            player2Pokemons.splice(2, 0, player2Pokemon3)
        }
    });

    $('#confirmButtonPokemons').click(function() {

        $("#buttonSound")[0].play();

        validatePlayerPokemons(player1Pokemons, player2Pokemons)
    })

    function validatePlayerPokemons(player1Pokemons, player2Pokemons) {

        if (player1Pokemons.length !== 3 && player2Pokemons.length !== 3) {
            displayValidationAlert("Please select your pokemons player 1.", "selection1");
            displayValidationAlert("Please select your pokemons player 2.", "selection2");
        } else if (player1Pokemons.length !== 3) {
            displayValidationAlert("Please select your pokemons player 1.", "selection1");
        } else if (player2Pokemons.length !== 3) {
            displayValidationAlert("Please select your pokemons player 2.", "selection2");
        } else {
            loadingScreen.show();

            setTimeout(function () {
                sendPlayerPokemons(player1Pokemons[0], player1Pokemons[1], player1Pokemons[2],
                    player2Pokemons[0], player2Pokemons[1], player2Pokemons[2]);
            }, 2500);
        }
    }

    function sendPlayerPokemons(player1Pokemon1, player1Pokemon2, player1Pokemon3,
                                player2Pokemon1, player2Pokemon2, player2Pokemon3) {
        $.ajax({
            url: 'http://localhost:9000/playerPokemons',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                player1Pokemon1: player1Pokemon1,
                player1Pokemon2: player1Pokemon2,
                player1Pokemon3: player1Pokemon3,
                player2Pokemon1: player2Pokemon1,
                player2Pokemon2: player2Pokemon2,
                player2Pokemon3: player2Pokemon3,
            }),
            success: function (data) {
                window.location.href = data.redirect;
            },
            error: function (error) {
                console.error('Error:', error);
            }
        });

        /*
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
        */
    }
});