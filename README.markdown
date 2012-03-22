Graphite
--------
An active code completion system for Java. See the webpage for more information, a video and a paper:

  http://www.cs.cmu.edu/~NatProg/graphite.html

Installation
------------
The .jar file in the /eclipse/plugins directory can be dropped into the dropins directory in your Eclipse installation. You'll see a Graphite entry in the Eclipse Preferences pane if it worked.

Annotation
----------
To annotate your Java classes with the @GraphitePalette annotation, its definition must be available. This is available as a .jar file in /java.

Palette Development
-------------------
Palettes are written using the standard browser-based HTML5 stack. The graphite.js file found in the js/ directory must be included to support integration into the editor using Graphite (and debugging of Graphite API calls in the browser.)

Example palettes are available in the palettes directory.

Plug-In Development
-------------------
The /eclipse directory can be imported as a project into Eclipse if you have the Plug-In Development tools installed (you can find it using Eclipse's built-in plug-in installer, for example.)

Pretty simple all-around! Contact us if you have any trouble.

Contributors
------------
* Cyrus Omar <http://www.cs.cmu.edu/~comar>
* YoungSeok Yoon <http://www.cs.cmu.edu/~yyoon1>

Citation
--------
If you use Graphite in an academic paper, we'd appreciate a citation:

Cyrus Omar, YoungSeok Yoon, Thomas D. LaToza, Brad A. Myers, [Active Code Completion](http://www.cs.cmu.edu/~comar/graphite-icse12.pdf). ICSE'2012: 34nd International Conference on Software Engineering, Zurich, Switzerland, 2-9 June 2012. To appear.

License
-------
THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.

The full license can be viewed at http://www.eclipse.org/org/documents/epl-v10.html.
