package edu.cmu.cs.graphite.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocBrowserInformationControlInput;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class PaletteCompletionProposal extends JavaCompletionProposal {
	public PaletteCompletionProposal(
			int replacementOffset, int replacementLength, String leadingWhitespaces, Image image,
			String displayString, int relevance, Shell shell, String url, String description,
			int width, int height) {
		super("", replacementOffset, replacementLength, image,
				displayString, relevance);
		this.leadingWhitespaces = leadingWhitespaces;
		this.shell = shell;
		this.url = url;
		this.description = description;
		this.initWidth = width;
		this.initHeight = height;
		this.cancelled = false;
	}

	protected Shell shell;
	
	protected String leadingWhitespaces;
	
	protected String url;
	protected String description;
	protected int initWidth;
	protected int initHeight;
	
	protected boolean cancelled;
	
	private static final int DEFAULT_WIDTH = 300;
	private static final int DEFAULT_HEIGHT = 200;
	
	private static final String GRAPHITE_PREFIX = "__GRAPHITE__";
	
	// holds the completion when the insert function is called by the palette
	public String completion = null;
	
	// hack to fix focus problems
	private boolean fixFocus = false;
	
	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		StyledText styledText = viewer.getTextWidget();
		if (styledText == null) {
			return;
		}
		
		final Shell browserShell = new Shell(shell, SWT.APPLICATION_MODAL);
		browserShell.setLayout(new FillLayout());
		
		final Browser browser = new Browser(browserShell, SWT.NONE);

		browserShell.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) { }
			
			@Override
			public void focusGained(FocusEvent e) {
				// we don't want the shell getting focus
				// so before processing the next event in the event loop (see below)
				// set the focus to the browser...
				fixFocus = true;
			}
		});
		
		browserShell.addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					cancelled = true;
				}
			}
			
		});

		// Set the default size
		browserShell.setSize(this.initWidth >= 0 ? this.initWidth : DEFAULT_WIDTH,
				this.initHeight >= 0 ? this.initHeight : DEFAULT_HEIGHT);
		
		// Add Graphite API functions
		new ResizeToFunction(browserShell, browser, GRAPHITE_PREFIX + "resizeTo");
		new ResizeByFunction(browserShell, browser, GRAPHITE_PREFIX + "resizeBy");
		new InsertFunction(this, browserShell, browser, GRAPHITE_PREFIX + "insert");
		new CancelFunction(browserShell, browser, GRAPHITE_PREFIX + "cancel");
		new GetIDEFunction(browser, GRAPHITE_PREFIX + "getIDE");
		new GetLanguageFunction(browser, GRAPHITE_PREFIX + "getLanguage");
		new GetSelectedText(browser, GRAPHITE_PREFIX + "getSelectedText", viewer.getDocument(), viewer.getSelectedRange());

		// move the palette into position
		movePalette(styledText, browserShell);
		
		// show the browser
		browser.setUrl(this.url);
		browserShell.open();
					
		// drop into event loop until the browser shell is disposed
		Display display = shell.getDisplay();
	    while (!browserShell.isDisposed()) {
	    	if (fixFocus) {
	    		fixFocus = false;
	    		browser.setFocus();
	    	}
	    	boolean done = display.readAndDispatch();
	    	if (!done) display.sleep();
	    }
	    
	    // See if the dialog was closed by ESC key.
	    if (this.cancelled) {
	    	return;
	    }
	    
	    // Insert replacement string
	    String completion = this.completion; 
	    if (completion != null) {
	    	super.setReplacementString(completion);
	    	super.setCursorPosition(completion.length());
	    }
	    
		super.apply(viewer, trigger, stateMask, offset);
	}
	
	@Override
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		StringBuffer buffer = new StringBuffer();
		HTMLPrinter.insertPageProlog(buffer, 0, getCSSStyles());
		buffer.append(this.description);
		HTMLPrinter.addPageEpilog(buffer);
		
		monitor.done();
		
		return new JavadocBrowserInformationControlInput(null, null, buffer.toString(), 0);
	}

	private void movePalette(StyledText styledText,	final Shell browserShell) {
		// Set location of the palette
		// TODO: adjust the location based on the size of the palette. 
		
		// Get the relative location and translate it into the screen location.
		int offset = styledText.getCaretOffset();
		Point location = styledText.toDisplay(styledText.getLocationAtOffset(offset));
		// Add caret height
		location.y += styledText.getCaret().getSize().y;
		browserShell.setLocation(location);
	}
	
	private abstract class BaseResizeFunction extends BrowserFunction {
		public BaseResizeFunction(Shell browserShell, Browser browser, String name) {
			super(browser, name);
			
			this.browserShell = browserShell;
		}
		
		protected Shell browserShell;

		protected void resizeBrowser(int width, int height) {
			Rectangle rect = this.browserShell.getDisplay().getClientArea();
			Point location = this.browserShell.getLocation();
			
			if (width < 0) { width = 0; }
			if (height < 0) { height = 0; }
			if (location.x + width > rect.width) { width = rect.width - location.x; }
			if (location.y + height > rect.height) { height = rect.height - location.y; }
			
			this.browserShell.setSize(width, height);
		}
	}
	
	private class ResizeToFunction extends BaseResizeFunction {
		public ResizeToFunction(Shell browserShell, Browser browser, String name) {
			super(browserShell, browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			int width = ((Double) arguments[0]).intValue();
			int height = ((Double) arguments[1]).intValue();
			
			resizeBrowser(width, height);
			return null;
		}
	}
	
	private class ResizeByFunction extends BaseResizeFunction {
		public ResizeByFunction(Shell browserShell, Browser browser, String name) {
			super(browserShell, browser, name);
		}
		
		@Override
		public Object function(Object[] arguments) {
			int cx = ((Double) arguments[0]).intValue();
			int cy = ((Double) arguments[1]).intValue();
			
			Point size = this.browserShell.getSize();
			int width = size.x + cx;
			int height = size.y + cy;
			
			resizeBrowser(width, height);
			return null;
		}
	}
	
	private class InsertFunction extends BrowserFunction {
		public InsertFunction(PaletteCompletionProposal proposal, Shell browserShell, Browser browser, String name) {
			super(browser, name);
			this.proposal = proposal;
			this.browserShell = browserShell;
		}
		
		private PaletteCompletionProposal proposal;
		private Shell browserShell;
		
		@Override
		public Object function(Object[] arguments) {
			String text = ((String) arguments[0]);
			text = text.replaceAll("\r\n", "\r\n" + proposal.leadingWhitespaces);
			text = text.replaceAll("\n", "\n" + proposal.leadingWhitespaces);
			text = text.replaceAll("\r", "\r" + proposal.leadingWhitespaces);
			
			proposal.completion = text;
			browserShell.close();
			return this;
		}
	}
	
	private class CancelFunction extends BrowserFunction {
		private Shell browserShell;

		public CancelFunction(Shell browserShell, Browser browser, String name) {
			super(browser, name);
			this.browserShell = browserShell;
		}
		
		@Override
		public Object function(Object[] arguments) {
			browserShell.close();
			return this;
		}
	}
	
	private class GetIDEFunction extends BrowserFunction {
		public GetIDEFunction(Browser browser, String name) {
			super(browser, name);
		}
		
		@Override
		public Object function(Object[] arguments) {
			return "Eclipse";
		}
	}
	
	private class GetLanguageFunction extends BrowserFunction {
		public GetLanguageFunction(Browser browser, String name) {
			super(browser, name);
		}
		
		@Override
		public Object function(Object[] arguments) {
			// TODO: Add support for other languages
			return "java";
		}
	}
	
	private class GetSelectedText extends BrowserFunction {
		public GetSelectedText(Browser browser, String name, IDocument document, Point selection) {
			super(browser, name);
			
			this.document = document;
			this.selection = selection;
		}
		
		private IDocument document;
		private Point selection;
		
		@Override
		public Object function(Object[] arguments) {
			try {
				return this.document.get(this.selection.x, this.selection.y);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return "";
		}
	}
}
