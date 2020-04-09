function showAllSession() {
    $.get( "/sessions", function(data) {
        let sessionsBody = "";

        for (let session of data) {
            let name = "";

            for (let user in session.userVoteMap) {
                name += user + " ,";
            }

            sessionsBody += "<tr><td class='col-md-6'>" + session.sessionId + "</td><td class='col-md-6'>" + name + "</td></tr>"
        }

        $("#adminBody").html(sessionsBody);
    });
}

function closeSession() {
    let sessionIdToDelete = $("#sessionToDelete").val().trim();
    if (!!sessionIdToDelete) {
         let url = "/sessions/" + sessionIdToDelete;
         $.ajax({
             url: url,
             type: 'DELETE',
             success: function(result) {
             }
         });
    } else {
        alert("Invalid session id.");
    }
}

function deleteUser() {
    let sessionIdToDelete = $("#sessionToDelete").val().trim();
    let userNameToDelete = $("#userToRemove").val().trim();

    if (!!sessionIdToDelete && !!userNameToDelete) {
         let url = "/sessions/" + sessionIdToDelete + "/users/" + userNameToDelete;
         $.ajax({
             url: url,
             type: 'DELETE',
             success: function(result) {
             }
         });
     } else {
        alert("Invalid session id or user name.");
     }
}

$(function () {
    $( "#showAllSession" ).click(function() { showAllSession(); });
    $( "#deleteSession" ).click(function() { closeSession(); });
    $( "#removeUser" ).click(function() { deleteUser(); });
});