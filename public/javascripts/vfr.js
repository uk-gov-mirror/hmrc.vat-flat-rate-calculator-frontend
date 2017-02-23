$(document).ready($(function() {

    $('[dataMetrics]').each(function() {
        var metrics = $(this).attr('dataMetrics');
        var parts = metrics.split(':');
        ga('send', 'event', parts[0], parts[1], parts[2]);
    });

}));