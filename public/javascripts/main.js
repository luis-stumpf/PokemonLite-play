function setTitlePosition(titleScreenMusic) {
    const urlParams = new URLSearchParams(window.location.search);
    const storedPosition = urlParams.get('position');
    if (storedPosition !== null) {
        titleScreenMusic.currentTime = parseFloat(storedPosition);
        titleScreenMusic.play();
    }
}

function displayValidationAlert(message, inputId) {
    let inputField = $("#" + inputId);
    let alertDiv = $("<div>")
        .addClass("alert alert-warning alert-dismissible fade show mt-1")
        .attr("role", "alert")
        .css("font-size", "2vh")
        .html(message);
    let closeButton = $("<button>").attr({
        type: "button",
        class: "btn-close",
        "data-bs-dismiss": "alert"
    });
    alertDiv.append(closeButton);
    inputField.parent().append(alertDiv);

    setTimeout(function () {
        alertDiv.addClass("fade-out");
    }, 2500);

    setTimeout(function () {
        alertDiv.remove();
    }, 7600);
}

connectWebSocket()
function connectWebSocket() {
    let websocket = new WebSocket("ws://localhost:9000/websocket");

    websocket.onopen = function(event) {
        console.log("Connected to Websocket");
    }

    websocket.onclose = function () {
        console.log('Connection with Websocket Closed!');
    };

    websocket.onerror = function (error) {
        console.log('Error in Websocket Occured: ' + error);
    };

    websocket.onmessage = function (e) {
        switch (e.data) {
            case "updateGamefield":
                updateGamefield(ids)
                break;
            case "updateGamefieldWithGifs":
                updateGamefield(idsWithGif)
                break;
        }
    }
}