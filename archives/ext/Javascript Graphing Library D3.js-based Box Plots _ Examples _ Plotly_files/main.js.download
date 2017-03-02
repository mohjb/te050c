//----
// Smooth Scrolling
//----


// to top right away
if (window.location.hash) scroll(0, 0);
setTimeout(function () {
    scroll(0, 0);
}, 1);

// any position
$(function () {
    // *only* if we have anchor on the url
    if (window.location.hash) {
        // smooth scroll to the anchor id
        $('html, body').animate({
            scrollTop: ($(window.location.hash).offset().top - 148) + 'px'
        }, 1000, 'swing');
    }
});

$("a[href^='#']").on('click', function (event) {
    var target;
    target = this.hash;

    event.preventDefault();

    var navOffset;
    navOffset = 148;

    return $('html, body').animate({
        scrollTop: $(this.hash).offset().top - 128
    }, 300, function () {
        return window.history.pushState(null, null, target);
    });
});


//----
// Mobile
//----

// Mobile Menu toggle

$('.mobile-menu-btn').on('click', function (e) {
    $('header.header-main').toggleClass("mobile-menu");
    $('body').toggleClass("no-scroll");
});

// Sidebar toggle

$('.toggle-sidebar').on('click', function (e) {
    $('.--sidebar-container').toggleClass("show");
    $('.toggle-sidebar').toggleClass("on");

});

$('.--sidebar-container').not('.toggle-sidebar').on('click', function (e) {
    $('.--sidebar-container').removeClass("show");
    $('.toggle-sidebar').removeClass("on");
    //console.log('clicked');
});

//----
// Copy to clipboard
//----

$('.copy').on('click', function (e) {
    var link = $(this).prev();
    copyToClipboard(link);
    $(this).attr('data-tooltip', 'Copied!');
});

$("<span class='copyCode'>Copy to clipboard!</span>").appendTo("pre");


var client = new ZeroClipboard($('span.copyCode'));

client.on('ready', function (event) {

    client.on('copy', function (event) {
        //var target = event.target.previousElementSibling.textContent;
        var target = event.target.parentElement.textContent.replace(/Copy to clipboard!/g, '');

        event.clipboardData.setData('text/plain', target);
        //console.log(event);

        event.target.innerHTML = "Copied!";
        var resetText = function(){
            event.target.innerHTML = "Copy to Clipboard!!";
        };
        setTimeout(resetText, 2500);

    });

    client.on('aftercopy', function (event) {
        //console.log('Copied text to clipboard: ' + event.data['text/plain']);
    });
});

client.on('error', function (event) {
    console.log('ZeroClipboard error of type "' + event.name + '": ' + event.message);
    ZeroClipboard.destroy();
});



// Reset Tooltip Text

$(".copy").hover(
    function () {
        //$(this).attr('data-tooltip', 'Copied!');
    },
    function () {
        $(this).attr('data-tooltip', 'Click to copy direct link.');
    }
);

// Copy functions

function copyToClipboard(element) {
    var $temp = $("<input>");
    $("body").append($temp);

    $temp.val(window.location.href.split('#')[0] + $(element).attr("href")).select();
    document.execCommand("copy");
    $temp.remove();
}



