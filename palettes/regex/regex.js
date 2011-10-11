// Singleton object containing the state of the palette.
var Palette = { };

$(function() {
    // Palette DOM elements
    var body = $('body');
    var t = Palette.t = $('#t');
    var main = Palette.main = $('#main');
    var entry = Palette.entry = $('#entry');
    var iFlag = Palette.iFlag = $('#i-flag');
    var newPosTest = Palette.newPosTest = $('#new-pos-test');
    var newNegTest = Palette.newNegTest = $('#new-neg-test');
    var posTestListView = Palette.posTestList = $('#pos-test-list');
    var negTestListView = Palette.negTestList = $('#neg-test-list');
    var noPosTests = Palette.noPosTest = $('#no-pos-tests');
    var noNegTests = Palette.noNegTest = $('#no-neg-tests');
    var key = Palette.key = $("#key");
    
    // Managing the palette's dimensions
    var width = Palette.width = 800;
    var minHeight = 250;
    graphite.resizeTo(width, minHeight); // initial reflow
    var resize = Palette.resize = function() {
         var height = t[0].offsetHeight + 0;
         console.log(height);
         if (height < minHeight) height = minHeight;
         graphite.resizeTo(width, height);
    };
    
    // Entry behavior
    var ENTER = 13;
    
    var waiting = false; // have to do it this way to deal with weird bugs
    entry.keydown(function(e) {
    	if (e.keyCode == ENTER) { waiting = true; }
    });
    
    entry.keyup(function(e) {
    	runTests();
    	
    	if (waiting) {
    		waiting = false;
    		enter();
    	}
    });
    
    var enter = Palette.enter = function enter() {
        var pattern = getCurrentPattern();
        var escapedPattern = escapePattern(pattern);
        var flagCode = getFlagCode();
        var exp = 'Pattern.compile("' + escapedPattern + '"' + flagCode + ');\n';
        var comments = makeComments();
        var insertion = exp + comments;
        graphite.insert(insertion);
    };
    
    var getCurrentPattern = Palette.getCurrentPattern = function getCurrentPattern() {
    	return entry.val();	
    }

    var escapePattern = Palette.escapePattern = function escapePattern(pattern) {
        return pattern.replace(/\\/g, "\\\\");
    };
    
    var unescapePattern = Palette.unescapePattern = function unescapePattern(pattern) {
    	return pattern.replace(/\\\\/g, "\\");
    }
        
    var makeComments = function() {
        if (!hasTests()) return "";
        var comments = ["/*"];
        
        if (hasPosTests()) {
        	comments.push(" * Should match: ");
        	makeCommentsFor(comments, posTests);
        	comments.push(" * ");
        }
        
        if (hasNegTests()) {
        	comments.push(" * Should NOT match: ");
        	makeCommentsFor(comments, negTests);
        	comments.push(" * ");
        }
        
        comments.push(" */\n");
        return comments.join("\n");
    }
    
    var makeCommentsFor = function(comments, tests) {
        for (var i=0; i < tests.length; ++i) {
            comments.push(" *   " + tests[i].str);
        }
    }
    
    // Flag behavior
    var getIFlagStatus = Palette.getIFlagStatus = function getIFlagStatus() {
    	return iFlag.is(":checked");
    }
    
    var setIFlagStatus = Palette.setIFlagStatus = function setIFlagStatus(val) {
    	iFlag.attr("checked", val);
    }
    
    iFlag.change(function(e) {
    	runTests();
    });
    
    document.body.onkeydown = function(e) {
    	if (e.ctrlKey && e.keyCode == 73) {
    		iFlag.click();
    		return false;
    	} else if (e.keyCode == 27) {
    		// ESC
    		graphite.cancel();
    	}
    }
    
    var getIFlagStatusJS = Palette.getIFlagStatusJS = function getIFlagStatusJS() {
    	var iFlagStatus = getIFlagStatus();
    	if (iFlagStatus) return "i";
    	else return "";
    }
    
    var getIFlagStatusJava = Palette.getIFlagStatusJava = function getIFlagStatusJava() {
    	var iFlagStatus = getIFlagStatus();
    	if (iFlagStatus) return "Pattern.CASE_INSENSITIVE";
    	else return "";
    }
    
    var getFlagCode = Palette.getFlagCode = function getFlagCode() {
    	var iFlagStatusJava = getIFlagStatusJava();
    	if (iFlagStatusJava) return ", " + iFlagStatusJava;
    	else return "";
    }

	// Class representing a single test case
    function Test(testType, str, list) {
    	str = str.replace(/</, "&lt;");
    	str = str.replace(/>/, "&gt;");
    	
        this.testType = testType; // either Test.POS or Test.NEG
        this.str = str; // Test string
        this.status = null; // true == passed
        this.list = list;

		var container = this.container = $('<div class="test-container">'
		+ '<span class="test-str">' + str + '</span>'
		+ '<span class="test-delete">x</span>'
		+ '</div>');
		
		var test = this;
		container.click(function(e) {
			// deletion on click
			list.removeTest(str);
			runTests();
			list.entry[0].focus();
		});		
    }
    Test.POS = true;
    Test.NEG = false;

    Test.prototype.setStatus = function(status) {
        this.status = status;
        var container = this.container
        if (status) {
            container.addClass('passed');
            container.removeClass('failed');
        } else {
            container.addClass('failed');
            container.removeClass('passed');
        }
    }
    
    // List of tests in each column
    posTests = [ ]; posTests.testType = Test.POS;
    negTests = [ ]; negTests.testType = Test.NEG;
    
    posTests.otherList = negTests;
    negTests.otherList = posTests;
    
    var hasTests = Palette.hasTests = function hasTests() {
    	return hasPosTests() || hasNegTests();
    }
    
    var hasPosTests = Palette.hasPosTests = function hasPosTests() {
    	return posTests.length > 0;
    }

    var hasNegTests = Palette.hasNegTests = function hasNegTests() {
    	return negTests.length > 0;
    }
    
    // Adding test strings
    function setupTestEntry(testEntry, list, listView) {
    	testEntry.list = list;
    	testEntry.listView = listView;
    	list.entry = testEntry;
    	
    	testEntry.keyup(function(e) {
    		if (e.keyCode == ENTER) {
    			var val = testEntry.val();
    			if (val == "") {
    				enter();
    			} else {
    				list.addTest(testEntry.val());
    			}
    		}
    	});
    	
    	testEntry.clear = function() {
    		testEntry.val("");
    		testEntry.trigger("change");
    	}
    	
    	list.addTest = function(str) {
    		if (str != "" && !list.getTest(str)) {
    			var test;
    			var otherList = list.otherList;
    			if (test = otherList.getTest(str)) 
    				otherList.removeTest(str);
    			else 
    				test = new Test(list.testType, str, list);
    			
    			list.push(test);
    			listView.append(test.container);
    		}
    		testEntry.clear();
    		runTests();
    		resize();
    	}
    	
    	list.getTest = function(str) {
    		for (var i=0; i < list.length; ++i) {
    			var test = list[i];
    			if (test.str == str) return test; 
    		}
    		return null;
    	}
    	
    	list.removeTest = function(str) {
    		for (var i=0; i < list.length; ++i) {
    			var test = list[i];
    			if (test.str == str) {
    				list.splice(i, 1);
    				test.container.detach();
    			}
    		}
    	}	
    }
    setupTestEntry(newPosTest, posTests, posTestListView);
    setupTestEntry(newNegTest, negTests, negTestListView);
    
    var runTests = Palette.runTests = function runTests() {
	    // Show/hide the "no (x) tests" string as appropriate
		if (hasPosTests()) noPosTests.addClass("hidden");
		else noPosTests.removeClass("hidden");
		
		if (hasNegTests()) noNegTests.addClass("hidden");
		else noNegTests.removeClass("hidden");
		
		if (hasTests()) key.removeClass("hidden");
		else key.addClass("hidden");
		
    	var pattern = getCurrentPattern();
    	var flags = getIFlagStatusJS();

    	try {
    		var re = new RegExp(pattern, flags);
    	} catch (e) {
    		// Invalid regexp
    		entry.addClass('err');
    		return;
    	}
    	entry.removeClass('err');
    	
    	runTestList(re, posTests);
    	runTestList(re, negTests);
    	
    	resize();
    };
    
    function runTestList(re, list) {
    	for (var i=0; i < list.length; ++i) {
    		re.lastIndex = 0;
    		var test = list[i];
    		var passed = re.test(test.str);
    		test.setStatus(passed);
    	}
    };
    
	function processSelection() { 
	    // Grab editor selection if it exists
	    var selectedText = graphite.getSelectedText();
	    // Extract regular expression and flag
	    var patternRE = /Pattern\s*.\s*compile\s*\(\s*"(.*)"\s*(,[^\)]*Pattern\.CASE_INSENSITIVE)?[^\)]*\)/;
	    var patternM = patternRE.exec(selectedText);
	    if (patternM) {
	        var pattern = patternM[1];
	        var unescaped = unescapePattern(pattern);
	        entry.val(unescaped);
	        var iFlagEntered = patternM[2];
	        setIFlagStatus(!!iFlagEntered);
	    }
	    
		// Get tests from selection
	    if (selectedText) {
	        var matchRE = /^\s*\* Should match:/;
	        var noMatchRE = /^\s*\* Should NOT match:/;
	        var doneRE = /^\s*\**\*\//;
	        var testRE = /^\s*\* \s+(.*)$/;
	        
	        var matching = 0;
	        var lines = selectedText.split("\n");
	        for (var i=0; i < lines.length; ++i) {
	            var line = lines[i];
	            if (matchRE.test(line)) {
	                matching = +1;
	            } else if (noMatchRE.test(line)) {
	                matching = -1;
	            } else if (doneRE.test(line)) {
	                break;
	            } else {
	                if (matching == 0) continue;
	                var testM = testRE.exec(line);
	                if (testM) {
	                    if (matching == +1) {
	                        // extract positive test
	                        posTests.addTest(testM[1]);
	                    } else if (matching == -1) {
	                        // extract negative test
	                        negTests.addTest(testM[1]);
	                    }
	                }
	            }
	        }
	    }
	}
	
	processSelection();    
	runTests();
    entry[0].focus(); // Set focus to entry
});
