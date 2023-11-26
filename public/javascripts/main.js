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