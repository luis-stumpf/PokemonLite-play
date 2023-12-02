const ids = [ "#player-1-status", "#player-2-status", "#menu-field" ]
const idsWithGif = [ "#player-1-status", "#player-2-status", "#menu-field", "#player-1-pokemon", "#player-2-pokemon" ]

const onAttack = (attackId, pokemonArt, turn) => {
    $.ajax({
        url: '/api/fighting',
        type: 'POST',
        data: JSON.stringify({
            move: attackId
        }),
        contentType: 'application/json; charset=utf-8',
        success: data => {
            showAttackAnimation(pokemonArt, turn);
        },
        error: function (xhr, status, error) {
            console.log(xhr.responseText);
        }
    });
}

const onDecide = (value) => {
    $.ajax({
        url: '/api/decision',
        type: 'POST',
        data: JSON.stringify({
            move: value
        }),
        contentType: 'application/json; charset=utf-8',
        success: data => {
        },
        error: function (xhr, status, error) {
            console.log(xhr.responseText);
        }
    });
}

const onSwitch = (value) => {
$.ajax({
        url: '/api/switch',
        type: 'POST',
        data: JSON.stringify({
            move: value + 1
        }),
        contentType: 'application/json; charset=utf-8',
        success: data => {
        },
        error: function (xhr, status, error) {
            console.log(xhr.responseText);
        }
    });

}

const updateGamefield = (givenIds) => {
    $.ajax({
        url: '/game',
        type: 'GET',
        success: data => {
            const parser = new DOMParser();
            const $remoteDocument = parser.parseFromString(data, "text/html");
            givenIds.forEach(id => {
                updateId(id, $remoteDocument);
            });
        },
    });
}

const updateId = (id, $remoteDoc) => {
    const $target = $remoteDoc.querySelector(id);
    const $source = $(id)[0];
    $source.parentNode.replaceChild($target, $source);
}


const showAttackAnimation = (pokemonType, turn) => {
    let toShow = "punch"
    if (pokemonType === "Feuer") {
        toShow = "fire"
    } else if (pokemonType === "Wasser") {
        toShow = "bubbles"
    }
    if (turn === 1) {
        toShow = toShow + "2"
    } else {
        toShow = toShow + "1"
    }
    $(`#${toShow}`).show()
    setTimeout(function () {
        $(`#${toShow}`).hide()
    }, 2500);
}
