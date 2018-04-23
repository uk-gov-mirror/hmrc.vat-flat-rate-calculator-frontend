$(document).ready($(function() {

    $('[dataMetrics]').each(function() {
        var metrics = $(this).attr('dataMetrics');
        var parts = metrics.split(':');
        ga('send', 'event', parts[0], parts[1], parts[2]);
    });
// =====================================================
// Handle the CGT UR panel dismiss link functionality
// =====================================================

    function setCookie (name, value, duration, domain) {
        var secure = window.location.protocol.indexOf('https') ? '' : '; secure'

        var cookieDomain = ""
        if (domain) {
            cookieDomain = '; domain=' + domain
        }

        var expires = ""
        if (duration) {
            var date = new Date()
            date.setTime(date.getTime() + (duration * 24 * 60 * 60 * 1000))
            expires = '; expires=' + date.toGMTString()
        }

        document.cookie = name + '=' + value + expires + cookieDomain + '; path=/' + secure
    }

    var cookieData=GOVUK.getCookie("mdtpurr");
    if (cookieData==null) {
        $("#ur-panel").addClass("banner-panel--show");
    }

    $(".banner-panel__close").on("click", function(e) {
        e.preventDefault();
        GOVUK.setCookie("mdtpurr", "suppress_for_all_services", 99999999999);
        setCookie("mdtpurr", "suppress_for_all_services", 28);
        $("#ur-panel").removeClass("banner-panel--show");
    });

}));