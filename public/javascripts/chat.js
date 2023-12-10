$(document).ready(function () {
        randomID()
        connect()
    }
)

let timestamp = "0"

let userID = " ";

$('#sendMsg').click(function() {
    //$("#buttonSound")[0].play(); // could get annoying after some time, maybe remove, maybe find more pleasant sound effect
    sendMessage();
})

function randomID() {
    userID = Math.floor(Math.random() * 100000).toString();
    userID = "User-" + userID
    $('#userId').get(0).innerText =  "Your message  " + userID + " ? "
}

function connect() {
    $.ajax({
        type : 'POST',
        url : '/getChat',
        dataType : 'json',
        data : JSON.stringify({'timestamp' : timestamp}),
        contentType: "application/json",
        success : function(response) {
            timestamp = response.timestamp;
            $('#chat-area').append('<div>' + response.msg + '</div>');
            noerror = true;
        },
        complete : function() {
            if (!self.noerror) {
                setTimeout(function(){ connect(); }, 1000);
            }else {
                connect();
            }
            noerror = false;
        }
    });
}


function sendMessage() {
    let message = userID + ": " + $('#send-message-box').get(0).value

    $.ajax({
        type : 'POST',
        url : '/sendChat',
        dataType : 'json',
        data : JSON.stringify({'msg' : message}) ,
        contentType: "application/json",
        success : function(response) {
            timestamp = response.timestamp;
        }
    });

    $('#send-message-box').get(0).value = ""
}