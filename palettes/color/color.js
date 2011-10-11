$(function() {
    var colors = 
    	[ new RGBColor('black'),
    	  new RGBColor('blue'),
    	  new RGBColor('cyan'),
    	  new RGBColor('darkGray'),
    	  new RGBColor('gray'),
    	  new RGBColor('green'),
    	  new RGBColor('lightGray'),
    	  new RGBColor('magenta'),
    	  new RGBColor('orange'),
    	  new RGBColor('pink'),
    	  new RGBColor('red'),
    	  new RGBColor('white'),
    	  new RGBColor('yellow') ];
    	  
    var colorMap = { }; // map from java string to color
    for (var i=0; i < colors.length; ++i) {
        var color = colors[i];
        var color_string = color.color_string;
        var java_string;
        if (color_string == "darkGray") java_string = "DARK_GRAY";
        else if (color_string == "lightGray") java_string = "LIGHT_GRAY";
        else java_string = color_string.toUpperCase();
        
        color.java_string = java_string;
        colorMap[java_string] = color;
    }
    	  
    function whichStandardColor(color) {
        if (!color.ok) return;
        var colorHex = color.toHex();
        for (var i=0; i < colors.length; ++i) {
            stdColor = colors[i];
            if (colorHex == stdColor.toHex()) 
                return "Color." + stdColor.java_string;
        }
    }
    
    var buildSelector = function(parent) {
        var selector = $("<div id='color_selector'></div>");
        var swatches = selector.swatches = [ ];
        
        $.each(colors, function(i) {
            var color = this;
             swatch = $("<div class='color_swatch'>&nbsp;</div>")
             swatch.css("background-color", this.toHex());
             swatch.bind("click", function(e) {
                 setColor(color);
                 setEntry(color.color_string);
                 entry.focus();
                 entry.select();
             });

             swatch.bind("mouseover", function(e) {
                 colorView.css("background-color", color);
             });

             swatch.bind("mouseout", function(e) {
                 setColor(curColor);
             });

             swatch.appendTo(selector);
             swatches.push(swatch);
         });

         parent.append(selector); 
         return selector;
    };

    var entry = $("#entry");
    var colorView = $("#color-view");
    var errIcon = $("#err-icon");
    var selectorDiv = $("#selector-div");
    var r = $("#r")[0];
    var g = $("#g")[0];
    var b = $("#b")[0];
    var selector = buildSelector(selectorDiv);
    var swatches = selector.swatches;
    var curColor, curColorIdx;
    
    function setColor(color) {
        if (!(color instanceof RGBColor))
            color = new RGBColor(color);
            
        if (color.ok) {
            colorView.css("background-color", color.toHex());
            r.innerHTML = color.r;
            g.innerHTML = color.g;
            b.innerHTML = color.b;
            curColor = color;
            entry.removeClass("err");
            colorView.removeClass("err");
            errIcon.hide();
        } else {
            colorView.css("background-color", "white");
            r.innerHTML = g.innerHTML = b.innerHTML = "-";
            entry.addClass("err");
            colorView.addClass("err");
            errIcon.show();
        }
        curColor = color;
    }
    
    var selectedText = graphite.getSelectedText();
    var initColor;
    if (selectedText) {
        var constantRE = /Color\.(\w+)/;
        var constantM = constantRE.exec(selectedText);
        if (constantM) {
            initColor = constantM[1];
        } else {
            var rgbRE = /new\s+Color\s*\(\s*(\d+),\s+(\d+),\s+(\d+)\)(;\s+\/\/\s+([^\s]+)(\n|$))?/;
            var rgbM = rgbRE.exec(selectedText);
            if (rgbM) {
                var colorName = rgbM[5];

                if (colorName) var colorNameVal = new RGBColor(colorName);
                var rVal = rgbM[1];
                var gVal = rgbM[2];
                var bVal = rgbM[3];
                if (colorName && colorNameVal.r == rVal && colorNameVal.g == gVal && colorNameVal.b == bVal)
                    initColor = colorName;
                else 
                    initColor = "rgb(" + rVal + ", " + gVal + ", " + bVal + ")";
            }
        }
    } else {
        initColor = "white";
    }
	entry.val(initColor);
    setColor(initColor);
    
    var ENTER = 13;
    
    entry.keydown(function(e) {
        var kc = e.keyCode;
        if (kc == ENTER && curColor.ok) {
            var stdColor = whichStandardColor(curColor);
            if (stdColor) {
                graphite.insert(stdColor + ";\n");
            } else {
                graphite.insert('new Color(\n  '
                    + curColor.r + ',\n  '  
                    + curColor.g + ',\n  '
                    + curColor.b + '); // ' + entry[0].value + '\n');
            }
        }
    });
    
    var down = false; // small bug with initial entry into palette in IDEs
    entry.keydown(function(e) {
    	down = true;
    });
    
    entry.keyup(function(e) {
    	if (down) {
	        setColor($('#entry')[0].value);
	        down = false;
	    }
    });
    
    function setEntry(color) {
        entry[0].value = color;
    };
    
    entry.focus(); 
    entry.select();
    
    var width = 300;
    var resize = function() {
         var height = main.offsetHeight + 10;
         window.resizeTo(width, height);
    };
    resize();
    
})

