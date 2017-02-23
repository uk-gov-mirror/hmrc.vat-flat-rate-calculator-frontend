$(document).ready($(function() {

    $('[dataMetrics]').each(function() {
        var metrics = $(this).attr('dataMetrics');
        var parts = metrics.split(':');
        console.log("!!!!!!!!!!!!!!!!!!!!!!");
        console.log("WORKING");
        console.log("!!!!!!!!!!!!!!!!!!!!!!");
        ga('send', 'event', parts[0], parts[1], parts[2]);
    });

}));