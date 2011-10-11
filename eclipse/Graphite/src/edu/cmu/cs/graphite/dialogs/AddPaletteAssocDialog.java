package edu.cmu.cs.graphite.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddPaletteAssocDialog {
	public AddPaletteAssocDialog(Shell parentShell, String title, String nameDescription) {
		this.shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.shell.setText(title);
		
		this.nameDescription = nameDescription;
		
		this.returnValue = SWT.CANCEL;
		
		createContents();
	}
	
	private Shell shell;
	
	private String nameDescription;
	
	private Text textName;
	private Text textDisplayString;
	private Text textUrl;
	private Text textDescription;
	private Text textInitWidth;
	private Text textInitHeight;
	
	private String textNameValue;
	private String textDisplayStringValue;
	private String textUrlValue;
	private String textDescriptionValue;
	private String textInitWidthValue;
	private String textInitHeightValue;
	
	private int returnValue;
	
	private void createContents() {
		Composite comp = new Composite(this.shell, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite compEdit = new Composite(comp, SWT.NONE);
		compEdit.setLayout(new GridLayout(2, false));
		compEdit.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label label;
		
		label = new Label(compEdit, SWT.RIGHT);
		label.setText(this.nameDescription + " :");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.textName = new Text(compEdit, SWT.SINGLE | SWT.BORDER);
		this.textName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(compEdit, SWT.RIGHT);
		label.setText("Display String :");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.textDisplayString = new Text(compEdit, SWT.SINGLE | SWT.BORDER);
		this.textDisplayString.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(compEdit, SWT.RIGHT);
		label.setText("URL :");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.textUrl = new Text(compEdit, SWT.SINGLE | SWT.BORDER);
		this.textUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(compEdit, SWT.RIGHT);
		label.setText("Description :");
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		
		this.textDescription = new Text(compEdit, SWT.MULTI | SWT.BORDER);
		this.textDescription.setLayoutData(new GridData(300, 100));
		this.textDescription.addTraverseListener(new TraverseListener() {
		    public void keyTraversed(TraverseEvent e) {
		        if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
		            e.doit = true;
		        }
		    }
		});

		label = new Label(compEdit, SWT.RIGHT);
		label.setText("Initial Width :");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.textInitWidth = new Text(compEdit, SWT.SINGLE | SWT.BORDER);
		this.textInitWidth.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(compEdit, SWT.RIGHT);
		label.setText("Initial Height :");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.textInitHeight = new Text(compEdit, SWT.SINGLE | SWT.BORDER);
		this.textInitHeight.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		compEdit.pack();
		
		// Add OK and Cancel buttons
		Composite compButtons = new Composite(comp, SWT.NONE);
		compButtons.setLayout(new FillLayout());
		compButtons.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		
		Button buttonOK = new Button(compButtons, SWT.PUSH);
		buttonOK.setText("&OK");
		buttonOK.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Validate the input
				int value = -1;
				boolean valid = true;
				
				// name, url must not be empty
				if (textName.getText().isEmpty()) {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
					messageBox.setText("Error");
					messageBox.setMessage("Name must not be empty");
					messageBox.open();
					
					textName.setFocus();
					textName.selectAll();
					return;
				}
				
				if (textUrl.getText().isEmpty()) {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
					messageBox.setText("Error");
					messageBox.setMessage("URL must not be empty");
					messageBox.open();
					
					textUrl.setFocus();
					textUrl.selectAll();
					return;
				}
				
				// initial width
				try {
					value = Integer.parseInt(textInitWidth.getText());
				} catch (NumberFormatException nfe) {
					valid = false;
				}
				
				if (value <= 0) {
					valid = false;
				}
				
				if (!valid) {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
					messageBox.setText("Error");
					messageBox.setMessage("Invalid value for initial width");
					messageBox.open();
					
					textInitWidth.setFocus();
					textInitWidth.selectAll();
					return;
				}

				// initial height
				try {
					value = Integer.parseInt(textInitHeight.getText());
				} catch (NumberFormatException nfe) {
					valid = false;
				}
				
				if (value <= 0) {
					valid = false;
				}
				
				if (!valid) {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
					messageBox.setText("Error");
					messageBox.setMessage("Invalid value for initial height");
					messageBox.open();
					
					textInitHeight.setFocus();
					textInitHeight.selectAll();
					return;
				}
				
				textNameValue = textName.getText();
				textDisplayStringValue = textDisplayString.getText();
				textUrlValue = textUrl.getText();
				textDescriptionValue = textDescription.getText();
				textInitWidthValue = textInitWidth.getText();
				textInitHeightValue = textInitHeight.getText();

				returnValue = SWT.OK;
				shell.close();
			}
			
		});
		
		
		Button buttonCancel = new Button(compButtons, SWT.PUSH);
		buttonCancel.setText("&Cancel");
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
			
		});
		
		compButtons.pack();
		
		comp.pack();
		
		this.shell.pack();
		
		this.shell.setDefaultButton(buttonOK);
	}
	
	public void open() {
		this.shell.open();
		
		while (!this.shell.isDisposed()) {
			if (!this.shell.getDisplay().readAndDispatch()) {
				this.shell.getDisplay().sleep();
			}
		}
	}
	
	public int getReturnValue() {
		return this.returnValue;
	}
	
	public void setName(String name) {
		this.textName.setText(name);
	}
	
	public String getName() {
		return this.textNameValue;
	}
	
	public void setDisplayString(String displayString) {
		this.textDisplayString.setText(displayString);
	}
	
	public String getDisplayString() {
		return this.textDisplayStringValue;
	}
	
	public void setUrl(String url) {
		this.textUrl.setText(url);
	}
	
	public String getUrl() {
		return this.textUrlValue;
	}
	
	public void setDescription(String description) {
		this.textDescription.setText(description);
	}
	
	public String getDescription() {
		return this.textDescriptionValue;
	}
	
	public void setInitialWidth(String initialWidth) {
		this.textInitWidth.setText(initialWidth);
	}
	
	public void setInitialWidth(int initialWidth) {
		setInitialWidth(Integer.toString(initialWidth));
	}
	
	public int getInitialWidth() {
		try {
			return Integer.parseInt(this.textInitWidthValue);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}
	
	public void setInitialHeight(String initialHeight) {
		this.textInitHeight.setText(initialHeight);
	}
	
	public void setInitialHeight(int initialHeight) {
		setInitialHeight(Integer.toString(initialHeight));
	}
	
	public int getInitialHeight() {
		try {
			return Integer.parseInt(this.textInitHeightValue);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}
}
