var
    userSession = {
        //Logout Settings
        inactiveTimeout: 10000,     //(ms) The time until we display a warning message
        warningTimeout: 10000,      //(ms) The time until we log them out
        minWarning: 5000,           //(ms) If they come back to page (on mobile), The minumum amount, before we just log them out
        warningStart: null,         //Date time the warning was started
        warningTimer: null,         //Timer running every second to countdown to logout
        maxSessionTimer: 1800 * 1000, //Timer for max allowed session for user
        logoutUrl: "/api/-/users/me/runnables/" ,
        containerID: null,
        logout: function () {       //Logout function once warningTimeout has expired
            //window.location = settings.autologout.logouturl;
            $.idleTimer("destroy");
            clearTimeout(userSession.keepaliveTimer);
            clearTimeout(userSession.warningTimer);
            $("#mdSessionWarning").modal("hide");
            $("#mdlLoggedOut").modal("show");
            $.ajax({ url: userSession.logoutUrl + userSession.containerID + "/killMe" });
        },
        //Keepalive Settings
        keepaliveTimer: null,
        keepaliveUrl: "/api/-/users/me/runnables/" ,
        keepaliveInterval: 30000,     //(ms) the interval to call said url
        keepAlive: function () {
            console.log('Let me live');
            $.ajax({ url: userSession.keepaliveUrl + userSession.containerID + "/extendSession" });
        },
        extendSession: function () {
            $("#mdSessionWarning").modal("hide");
            clearTimeout(userSession.warningTimer);
            console.log('Session extended');
        }
    }
;

change_idle('Initial');

$( document ).on( "idle.idleTimer", function(event, elem, obj){
    change_idle('idle');
    //Get time when user was last active
    var
        diff = (+new Date()) - obj.lastActive - obj.timeout,
        warning = (+new Date()) - diff
    ;
    
    //On mobile js is paused, so see if this was triggered while we were sleeping
    if (diff >= userSession.warningTimeout || warning <= userSession.minWarning) {
        userSession.logout();
    } else {
        //Show dialog, and note the time
        $('#sessionSecondsRemaining').html(Math.round((userSession.warningTimeout - diff) / 1000));
        $("#mdSessionWarning").modal("show");
        userSession.warningStart = (+new Date()) - diff;
        //Update counter downer every second
        userSession.warningTimer = setInterval(function () {
            var remaining = Math.round((userSession.warningTimeout / 1000) - (((+new Date()) - userSession.warningStart) / 1000));
            if (remaining >= 0) {
                console.log('Your life in this dimension ends in: ' + remaining);
                $('#sessionSecondsRemaining').html(remaining);
            } else {
                userSession.logout();
            }
        }, 1000)
    }
});

//User clicked ok to extend userSession
$("#extendSession").click(function () {
    userSession.extendSession();
});

//User clicked logout
$("#logoutSession").click(function () {
    userSession.logout();
});

$( document ).on( "active.idleTimer", function(event, elem, obj, triggerevent){
    change_idle('active');
    userSession.extendSession();
});

change_idle('Timer Active');

function change_idle(idle_state){
    idle_status=idle_state;
    $("#idle_state").html(idle_status);
};

updateTerminalStatus = function(e) {
    var action = e.data.split(':')[0]
    if (action == 'idle_status') {
        $('#rmsg').html(e.data.split(':')[1]);
    } else {
        console.log("Unknown message: " + e.data);
    }
}

//Recieve response
var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
var eventer = window[eventMethod];
var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";

// Listen to message from child window
eventer(messageEvent, function(e) {
    var key = e.message ? "message" : "data";
    var data = e[key];
    //run function//
    updateTerminalStatus(e);
}, false);
