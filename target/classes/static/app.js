var stompClient = null;
var connectedUser = null;
var connectedSessionId = null;

function setConnected(connected) {
    $("#createSession").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#votes").html("");
}

function joinSession() {
    connectedUser = $("#joinSessionUserName").val();
    createSocket($("#joinSessionId").val())
}

function createSocket(sessionId) {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/sessions/'+ sessionId, function (data) {
            showVotes(JSON.parse(data.body));
        });
        connectedSessionId = sessionId;
        addUser(sessionId, connectedUser);
    });
}

function addUser(sessionId, username) {
    let url = '/app/sessions/'+ sessionId + '/users';
    stompClient.send(url, {}, JSON.stringify({ 'username' :  connectedUser}));
}

function sendVote() {
    let url = '/app/sessions/' + connectedSessionId + '/vote';
    stompClient.send(url, {}, JSON.stringify({'vote': parseInt($("#vote").val()), 'user' : { 'username' :  connectedUser}}));
}

function purgeSession() {
    let url = '/app/sessions/' + connectedSessionId + '/purge';
    stompClient.send(url, {}, {});
}

function resetSession() {
    let url = '/app/sessions/' + connectedSessionId + '/reset';
    stompClient.send(url, {}, {});
}

function createSession() {
   $.post( "/sessions?user=" + $("#createSessionUserName").val(), function(data) {
        connectedUser = $("#createSessionUserName").val();
        createSocket(data.sessionId);
   });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showVotes(session) {
    console.log(JSON.stringify(session))
    let votes = Object.values(session.userVoteMap);

    let voteHtml = "";
    for (let uservote of votes) {
        voteHtml += "<tr><td>" + uservote.user.username + "</td><td>" + uservote.vote + "</td></tr>"
    }

    $("#votes").html(voteHtml);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#createSession" ).click(function() { createSession(); });
    $( "#sendVote" ).click(function() { sendVote(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#joinSession" ).click(function() { joinSession(); });
});