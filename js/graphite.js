// Graphite Library
var graphite = { };

try {
    // Being run in an IDE
    graphite.resizeTo = __GRAPHITE__resizeTo;
    graphite.resizeBy = __GRAPHITE__resizeBy;
    window.resizeTo = __GRAPHITE__resizeTo;
    window.resizeBy = __GRAPHITE__resizeBy;

    graphite.insert = __GRAPHITE__insert;
    graphite.cancel = __GRAPHITE__cancel;

    graphite.getIDE = __GRAPHITE__getIDE;
    graphite.getLanguage = __GRAPHITE__getLanguage;

    graphite.getSelectedText = __GRAPHITE__getSelectedText;    
} catch (e) {
    // Being run in a web browser.
    graphite.resizeTo = function(width, height) {
    	// TODO: Not sure if this is cross-browser compatible
    	window.innerWidth = width;
    	window.innerHeight = height;
        // window.resizeTo.apply(window, arguments);
    };
    
    graphite.resizeBy = function(dWidth, dHeight) {
    	// TODO: Not sure if this is cross-browser compatible
    	window.innerWidth += dWidth;
    	window.innerHeight += dHeight;
        // window.resizeBy.apply(window, arguments);
    };
    
    var alertFn;
    if (console.log) {
        console.log("GRAPHITE: Running Graphite palette in a non-editor environment.");
        alertFn = function() {
            console.log.apply(console, arguments);
        };
    } else {
        alertFn = alert;
    }
    
    graphite.insert = function(str) {
        alertFn("GRAPHITE: insert('" + str + "')");
    };
    
    graphite.cancel = function() {
        alertFn("GRAPHITE: cancel()");
    };

    graphite.getIDE = function() {
        alertFn("GRAPHITE: getIDE()");
    };

    graphite.getLanguage = function() {
        alertFn("GRAPHITE: getLanguage()");
    };

    graphite.getCurrentLineFromCursor = function() {
        alertFn("GRAPHITE: getCurrentLineFromCursor()");
    };

    graphite.getFollowingComment = function() {
        alertFn("GRAPHITE: getFollowingComment()");
    };

    graphite.getSelectedText = function() {
        alertFn("GRAPHITE: getSelectedText()");
    };
}
