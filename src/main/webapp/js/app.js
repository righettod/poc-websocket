var AUTH_WS_CLIENT;
var MESSAGE_WS_CLIENT;
var BASE_URL = "ws://localhost:9090";


//Affect component event handlers
$(document).ready(function() {
  //Initialize WS client connections
  configureWSClientForAuthentication();
  configureWSClientForMessaging();

  //Add click action for authentication button
  $("#authenticationAction").click(function() {
    //Check state of the connection with the authentication WS endpoint
    configureWSClientForAuthentication();
    //Get authentication information
    var login = $("#userLogin").val();
    var pwd = $("#userPassword").val();
    if (login == "" || pwd == "") {
      $("#infoBoard").removeClass();
      $("#infoBoard").text("Please fill the credentials !")
      $("#infoBoard").addClass("alert alert-warning");
    } else {
      //Build the message structure, convert it to JSON representation and sent it
      var data = {
        "login": login,
        "password": pwd
      };
      var msg = JSON.stringify(data);
      AUTH_WS_CLIENT.send(msg);
    }
  });


  //Add click action for token invalidation button
  $("#tokenInvalidationAction").click(function() {
    //Check state of the connection with the authentication WS endpoint
    configureWSClientForMessaging();
    //Get access token
    var accessToken = sessionStorage.getItem("access_token");
    if (accessToken === null) {
      $("#infoBoard").removeClass();
      $("#infoBoard").text("No access token present in the browser session storage !")
      $("#infoBoard").addClass("alert alert-warning");
    } else {
      //Build the message structure, convert it to JSON representation and sent it
      var data = {
        "token": accessToken,
        "content": "INVALIDATE_TOKEN"
      };
      var msg = JSON.stringify(data);
      MESSAGE_WS_CLIENT.send(msg);
    }
  });

  //Add click action for post of a new message
  $("#postNewMessageAction").click(function() {
    //Check state of the connection with the authentication WS endpoint
    configureWSClientForMessaging();
    var newMsgContent = $("#newMessageContent").val();
    //Get access token
    var accessToken = sessionStorage.getItem("access_token");
    if (accessToken === null) {
      $("#infoBoard").removeClass();
      $("#infoBoard").text("No access token present in the browser session storage !")
      $("#infoBoard").addClass("alert alert-warning");
    } else if (newMsgContent == "") {
      $("#infoBoard").removeClass();
      $("#infoBoard").text("Please specify a message !")
      $("#infoBoard").addClass("alert alert-warning");
    } else {
      //Build the message structure, convert it to JSON representation and sent it
      var data = {
        "token": accessToken,
        "content": newMsgContent
      };
      var msg = JSON.stringify(data);
      MESSAGE_WS_CLIENT.send(msg);
    }
  });



});


//Configure Message WS client
function configureWSClientForMessaging() {
  //Detect if the connection with the message endpoint is still alive
  if (MESSAGE_WS_CLIENT != null && MESSAGE_WS_CLIENT.readyState === MESSAGE_WS_CLIENT.OPEN) {
    //Quick exit
    return;
  }
  //Configure authentication WS client event handlers
  MESSAGE_WS_CLIENT = new WebSocket(BASE_URL + "/msg", "message");
  MESSAGE_WS_CLIENT.onclose = function() {
    $("#infoBoard").removeClass();
    $("#infoBoard").text("Connection closed with the message endpoint.")
    $("#infoBoard").addClass("alert alert-warning");
  };
  MESSAGE_WS_CLIENT.onopen = function() {
    $("#infoBoard").removeClass();
    $("#infoBoard").text("Connection open with the message endpoint.")
    $("#infoBoard").addClass("alert alert-info");
  };
  MESSAGE_WS_CLIENT.onerror = function(evt) {
    $("#infoBoard").removeClass();
    $("#infoBoard").text("Error during communication with message endpoint: " + evt)
    $("#infoBoard").addClass("alert alert-danger");
  };
  MESSAGE_WS_CLIENT.onmessage = function(evt) {
    var data = JSON.parse(evt.data);
    $("#infoBoard").removeClass();
    if (data.isSuccess) {
      var msgList = data.messages;
      $("#messagesBoard").empty();
      for (i = 0; i < msgList.length; i++) {
        $("#messagesBoard").append('<li class="list-group-item">' + msgList[i] + '</li>');
      }
      if (data.errorMessage == "") {
        $("#infoBoard").addClass("alert alert-success");
        $("#infoBoard").text("Messages list loaded.");
      } else {
        $("#infoBoard").addClass("alert alert-warning");
        $("#infoBoard").text(data.errorMessage);
      }
    } else {
      $("#infoBoard").text(data.errorMessage);
      $("#infoBoard").addClass("alert alert-danger");
    }
  }
}


//Configure Authentication WS client
function configureWSClientForAuthentication() {
  //Detect if the connection with the authentication endpoint is still alive
  if (AUTH_WS_CLIENT != null && AUTH_WS_CLIENT.readyState === AUTH_WS_CLIENT.OPEN) {
    //Quick exit
    return;
  }
  //Configure authentication WS client event handlers
  AUTH_WS_CLIENT = new WebSocket(BASE_URL + "/auth", "authentication");
  AUTH_WS_CLIENT.onmessage = function(evt) {
    var data = JSON.parse(evt.data);
    $("#infoBoard").removeClass();
    $("#infoBoard").text(data.message);
    if (data.isSuccess) {
      //Save access token in the browser session storage
      sessionStorage.setItem("access_token", data.token);
      $("#infoBoard").addClass("alert alert-success");
    } else {
      $("#infoBoard").addClass("alert alert-danger");
    }
  };
  AUTH_WS_CLIENT.onclose = function() {
    $("#infoBoard").removeClass();
    $("#infoBoard").text("Connection closed with the authentication endpoint.")
    $("#infoBoard").addClass("alert alert-warning");
  };
  AUTH_WS_CLIENT.onopen = function() {
    $("#infoBoard").removeClass();
    $("#infoBoard").text("Connection open with the authentication endpoint.")
    $("#infoBoard").addClass("alert alert-info");
  };
  AUTH_WS_CLIENT.onerror = function(evt) {
    $("#infoBoard").removeClass();
    $("#infoBoard").text("Error during communication with authentication endpoint: " + evt)
    $("#infoBoard").addClass("alert alert-danger");
  };

}
