Graphite
--------
An active code completion system for Java.

* Video: http://www.cs.cmu.edu/~comar/graphite-demo.mp4
* Paper draft: http://www.cs.cmu.edu/~comar/graphite-icse12.pdf

Plug-In Installation
--------------------
The .jar file in the /eclipse/plugins directory can be dropped into the dropins directory in your Eclipse installation. You'll see a Graphite entry in the Eclipse Preferences pane if it worked.

The @GraphitePalette Annotation
-------------------------------
To annotate your Java classes with the GraphitePalette annotation, its definition must be available. This is available as a .jar file in /java.

Palette Development
-------------------
Palettes are written using the standard browser-based stack. The graphite.js file found in the js/ directory must be included to support integration into the editor using Graphite (and debugging of Graphite API calls in the browser.)

Example palettes are available in the palettes directory.

Plug-In Development
-------------------
The /eclipse directory can be imported as a project into Eclipse if you have the Plug-In Development tools installed (you can find it using Eclipse's built-in plug-in installer, for example.)

Pretty simple all-around! Contact us if you have any trouble.

Contact
-------
* Cyrus Omar <comar@cs.cmu.edu>
* YoungSeok Yoon <youngseok@cs.cmu.edu>
