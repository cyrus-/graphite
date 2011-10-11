package edu.cmu.cs.graphite.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.cmu.cs.graphite.preferences.PaletteAssociation;
import edu.cmu.cs.graphite.preferences.PaletteAssociations;

public class CompletionProposalComputer implements
		IJavaCompletionProposalComputer {
	
	// Uses default empty constructor

	@SuppressWarnings("rawtypes")
	@Override
	public List computeCompletionProposals(ContentAssistInvocationContext contextIn,
			IProgressMonitor progMon) {
		ArrayList<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		
		// Find the expected type.
		JavaContentAssistInvocationContext context = (JavaContentAssistInvocationContext) contextIn;		
		IType expType = context.getExpectedType();		
		if (expType == null) return proposals;
		
		// Figure out annotation information, if it exists.
		// TODO: Make sure its the right "Palette", 
		// 	     not clear how to do that from documentation of IPalette
		String url = null;
		String displayString = null;
		String description = null;
		int initWidth = -1;
		int initHeight = -1;
		
		boolean annotationFound = false;
		
		try {
			IAnnotation annotation = expType.getAnnotation("GraphitePalette");
			if (annotation != null) {
				IMemberValuePair[] pairs = annotation.getMemberValuePairs();

				for (IMemberValuePair pair : pairs) {
					if (pair.getMemberName().equals("url")) {
						url = (String) pair.getValue();
					} else if (pair.getMemberName().equals("displayString")) {
						displayString = (String) pair.getValue();
					} else if (pair.getMemberName().equals("description")) {
						description = (String) pair.getValue();
					} else if (pair.getMemberName().equals("initWidth")) {
						initWidth = (Integer) pair.getValue();
					} else if (pair.getMemberName().equals("initHeight")) {
						initHeight = (Integer) pair.getValue();
					}
				}
				
				if (url != null) { annotationFound = true; }
			}
		} catch (JavaModelException e) {
		}
		
		// See if there is a palette association defined via preference page
		if (annotationFound == false) {
			PaletteAssociation element = PaletteAssociations.getInstance().getElement(expType.getFullyQualifiedName());
			if (element == null) { return proposals; }
			
			displayString = element.displayString;
			url = element.url;
			description = element.description;
			initWidth = element.initWidth;
			initHeight = element.initHeight;
		}
		
		// Create new proposal.
		// TODO: offset, image, displayString
		Shell shell = context.getViewer().getTextWidget().getShell();
		int replacementOffset = context.getInvocationOffset();
		int replacementLength = 0;
		
		String leadingWhitespaces = "";
		try
		{
			IDocument document = context.getDocument();
			IRegion lineRegion = document.getLineInformationOfOffset(replacementOffset);
			String currentLine = document.get(lineRegion.getOffset(), lineRegion.getLength());
			leadingWhitespaces = currentLine.substring(0, currentLine.indexOf(currentLine.trim()));
		} catch (Exception e) {
		}
		
		URL iconURL = Activator.getDefault().getBundle().getEntry("/icon/graphiteIcon.png");
		Image graphiteIcon = (Image) ImageDescriptor.createFromURL(iconURL).createResource(Display.getCurrent());
		
		if (displayString == null || displayString.equals("")) {
			displayString = "Active code completion available";
		}
		PaletteCompletionProposal proposal = new PaletteCompletionProposal(
				replacementOffset, replacementLength, leadingWhitespaces, graphiteIcon, displayString,
				10000, shell, url, description, initWidth, initHeight);
		proposals.add(proposal);
		return proposals;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List computeContextInformation(ContentAssistInvocationContext arg0,
			IProgressMonitor arg1) {
		// TODO Auto-generated method stub
		return new ArrayList();
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sessionEnded() {

	}

	@Override
	public void sessionStarted() {

	}

}
