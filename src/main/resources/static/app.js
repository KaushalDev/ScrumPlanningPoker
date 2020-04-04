var stompClient = null;
var connectedUser = null;
var connectedSessionId = null;

function setConnected(connected) {
    $("#createSession").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#votesHeader").show();
        $("#votesContent").show();
        $("#sessionRow").hide();
    }
    else {
        hideVoteSection();
        $("#sessionRow").show();
    }
    $("#votes").html("");
}

function hideVoteSection() {
    $("#votesHeader").hide();
    $("#votesContent").hide();
}

function joinSession() {
    if (!$("#joinSessionUserName").val().trim()) {
        alert("Invalid user name");
    } else if (!$("#joinSessionId").val().trim()) {
        alert("Invalid session ID");
    } else {
        connectedUser = $("#joinSessionUserName").val();
        createSocket($("#joinSessionId").val())
    }
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
        sessionIdHref = "<a target='blank' href='" + $(location).attr('href')
         + "?sessionId=" + connectedSessionId + "'>" + connectedSessionId + "</a"
        $("#sessionDetails").html("Cast Vote. You are in session : " + sessionIdHref);
        addUser(sessionId, connectedUser);
    });
}

function addUser(sessionId, username) {
    let url = '/app/sessions/'+ sessionId + '/users';
    stompClient.send(url, {}, JSON.stringify({ 'username' :  connectedUser}));
}

function sendVote() {
    if (Number.isInteger($("#vote").val()) &&  parseInt($("#vote").val()) > 0) {
        alert("Invalid vote value");
    } else {
        let url = '/app/sessions/' + connectedSessionId + '/vote';
        stompClient.send(url, {}, JSON.stringify({'vote': parseInt($("#vote").val()), 'user' : { 'username' :  connectedUser}}));
    }
}

function purgeSession() {
    let url = '/app/sessions/' + connectedSessionId + '/purge';
    stompClient.send(url, {}, {});
}

function resetVotes() {
    let url = '/app/sessions/' + connectedSessionId + '/reset';
    stompClient.send(url, {}, {});
}

function createSession() {
    if (!$("#createSessionUserName").val().trim()) {
        alert("Invalid user  name");
    } else {
        $.post( "/sessions?user=" + $("#createSessionUserName").val(), function(data) {
            connectedUser = $("#createSessionUserName").val();
            createSocket(data.sessionId);
       });
    }
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

function getSessionIdParam() {
    return getQueryString("sessionId")
}

function getQueryString(key) {
    key = key.replace(/[*+?^$.\[\]{}()|\\\/]/g, "\\$&"); // escape RegEx meta chars
    var match = location.search.match(new RegExp("[?&]"+key+"=([^&]+)(&|$)"));
    return match && decodeURIComponent(match[1].replace(/\+/g, " "));
}

function handleDisplaySessionIdInQueryParam() {
    let sessionIdParam = getSessionIdParam();
    if (!!sessionIdParam) {
        $("#joinSessionId").val(sessionIdParam);
        $("#createSessionCol").hide();
    }
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#createSession" ).click(function() { createSession(); });
    $( "#sendVote" ).click(function() { sendVote(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#joinSession" ).click(function() { joinSession(); });
    $("#resetVotes").click(function() { resetVotes(); });
    handleDisplaySessionIdInQueryParam();
    hideVoteSection();
});