var stompClient = null;
var connectedUser = null;
var connectedSessionId = null;

function setConnected(connected) {
    $("#createSession").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#votesHeader").show();
        $("#votesContent").show();
    }
    else {
        hideVoteSection();
    }
    $("#votes").html("");
}

function hideVoteSection() {
    $("#votesHeader").hide();
    $("#votesContent").hide();
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
        $("#sessionDetails").html("Cast Vote. You are in session " + connectedSessionId);
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

function showVotes(voteSummary) {
    console.log(JSON.stringify(voteSummary))

    let voteHtml = "";
    for (let uservote of voteSummary) {
        let cssClass = "voteValue";

        if (uservote.voteState === 'Voted') {
            cssClass = "voted";
        } else if (uservote.voteState === 'Awaiting vote') {
            cssClass = "awaiting_vote";
        }

        voteHtml += "<tr><td class='col-md-6'>" + uservote.user + "</td><td class='col-md-6 " + cssClass + "' >" + uservote.voteState + "</td></tr>"
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
    hideVoteSection();
});