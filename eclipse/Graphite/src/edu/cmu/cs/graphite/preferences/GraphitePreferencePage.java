package edu.cmu.cs.graphite.preferences;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.cmu.cs.graphite.dialogs.AddPaletteAssocDialog;

public class GraphitePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private final class TableDoubleClickListener extends MouseAdapter {
		private final Shell shell;
		private final Table table;
		
		private final String title;
		private final String nameDescription;
		private final BasePaletteAssociations paletteAssociations;
		
		private TableDoubleClickListener(Shell shell, Table table,
				String title, String nameDescription, BasePaletteAssociations paletteAssociations) {
			this.shell = shell;
			this.table = table;
			
			this.title = title;
			this.nameDescription = nameDescription;
			this.paletteAssociations = paletteAssociations;
		}
		
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			TableItem item = this.table.getItem(new Point(e.x, e.y));
			if (item == null) { return; }
			
			PaletteAssociation element = this.paletteAssociations.getElement(item.getText(0));
			
			AddPaletteAssocDialog dialog = new AddPaletteAssocDialog(this.shell, this.title, this.nameDescription);
			dialog.setName(element.name);
			dialog.setDisplayString(element.displayString);
			dialog.setUrl(element.url);
			dialog.setDescription(element.description);
			dialog.setInitialWidth(element.initWidth);
			dialog.setInitialHeight(element.initHeight);
			
			dialog.open();
			
			if (dialog.getReturnValue() == SWT.CANCEL) { return; }

			// This will eventually replace the previous element
			this.paletteAssociations.addElement(dialog.getName(), dialog.getDisplayString(), dialog.getUrl(), dialog.getDescription(), dialog.getInitialWidth(), dialog.getInitialHeight());
			
			syncTable(this.table, this.paletteAssociations);
		}
	}

	private final class RemovePaletteAssocsSelectionAdapter extends
			SelectionAdapter {
		private final Shell shell;
		private final Table table;
		
		private BasePaletteAssociations paletteAssociations;

		private RemovePaletteAssocsSelectionAdapter(Shell shell, Table table,
				BasePaletteAssociations paletteAssociations) {
			this.shell = shell;
			this.table = table;
			
			this.paletteAssociations = paletteAssociations;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			int[] selectionIndices = table.getSelectionIndices();
			if (selectionIndices.length == 0) { return; }
			
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			messageBox.setText("Remove palette associations");
			messageBox.setMessage("Do you really want to remove the selected palette associations?");
			int returnValue = messageBox.open();
			if (returnValue == SWT.NO) { return; }
			
			HashSet<String> names = new HashSet<String>();
			for (int selectionIndex : selectionIndices) {
				String name = (String)table.getItem(selectionIndex).getText(0);
				names.add(name);
			}
			
			for (String name : names) {
				PaletteAssociations.getInstance().removeElement(name);
			}
			
			syncTable(table, paletteAssociations);
		}
	}

	private final class AddPaletteAssocSelectionAdapter extends SelectionAdapter {
		private final Shell shell;
		private final Table table;
		
		private final String title;
		private final String nameDescription;
		
		private BasePaletteAssociations paletteAssociations;

		private AddPaletteAssocSelectionAdapter(Shell shell, Table table,
				String title, String nameDescription, BasePaletteAssociations paletteAssociations) {
			this.shell = shell;
			this.table = table;
			
			this.title = title;
			this.nameDescription = nameDescription;
			
			this.paletteAssociations = paletteAssociations;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			AddPaletteAssocDialog dialog = new AddPaletteAssocDialog(shell, title, nameDescription);
			dialog.open();
			
			if (dialog.getReturnValue() == SWT.OK) {
				// First, add to the database
				this.paletteAssociations.addElement(dialog.getName(), dialog.getDisplayString(), dialog.getUrl(), dialog.getDescription(), dialog.getInitialWidth(), dialog.getInitialHeight());
				
				// Sync the table
				syncTable(this.table, this.paletteAssociations);
			}
		}
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		
		TabItem tabGeneral = new TabItem(tabFolder, SWT.NONE);
		tabGeneral.setText("General");
		
		TabItem tabPaletteAssoc = new TabItem(tabFolder, SWT.NONE);
		tabPaletteAssoc.setText("PaletteAssociation");
		
		createPaletteAssocTabPage(tabFolder, tabPaletteAssoc, "Fully qualified name of the class", "Add new palette association", PaletteAssociations.getInstance());
		
		TabItem tabKeywords = new TabItem(tabFolder, SWT.NONE);
		tabKeywords.setText("Keywords");
		
		createPaletteAssocTabPage(tabFolder, tabKeywords, "Keyword", "Add new keyword", Keywords.getInstance());
		
		return tabFolder;
//		return null;
	}
	
	private void syncTable(Table table, BasePaletteAssociations paletteAssociations) {
		table.removeAll();
		
		PaletteAssociation[] elements = paletteAssociations.values().toArray(new PaletteAssociation[0]);
		Arrays.sort(elements, new Comparator<PaletteAssociation>() {

			@Override
			public int compare(PaletteAssociation e1, PaletteAssociation e2) {
				return e1.name.compareToIgnoreCase(e2.name);
			}
		});
		
		for (PaletteAssociation element : elements) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, element.name);
			item.setText(1, element.displayString);
			item.setText(2, element.url);
			item.setText(3, element.description);
			item.setText(4, Integer.toString(element.initWidth));
			item.setText(5, Integer.toString(element.initHeight));
		}
	}

	private void createPaletteAssocTabPage(TabFolder tabFolder,
			TabItem tab, String nameDescription, String dialogTitle, BasePaletteAssociations paletteAssociations) {
		final Shell shell = tabFolder.getShell();
		
		Composite compPaletteAssoc = new Composite(tabFolder, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		compPaletteAssoc.setLayout(gridLayout);
		compPaletteAssoc.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Table table = new Table(compPaletteAssoc, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// Set the columns
		String[] cols = { nameDescription, "Display string", "Url of Palette", "Description", "Initial width", "Initial height" };
		for (String columnName : cols) {
			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			tableColumn.setText(columnName);
			tableColumn.pack();
		}
		
		// Initialize items
		syncTable(table, paletteAssociations);
		
		table.addMouseListener(new TableDoubleClickListener(shell, table, "Edit " + dialogTitle, nameDescription, paletteAssociations));
		
		tab.setControl(compPaletteAssoc);
		
		// Add two buttons
		Composite compButtons = new Composite(compPaletteAssoc, SWT.NONE);
		compButtons.setLayout(new GridLayout());
		compButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		
		Button buttonAdd = new Button(compButtons, SWT.PUSH);
		buttonAdd.setText("&Add");
		buttonAdd.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		buttonAdd.addSelectionListener(
				new AddPaletteAssocSelectionAdapter(
						shell, table,
						"Add " + dialogTitle, nameDescription,
						paletteAssociations));
		
		Button buttonRemove = new Button(compButtons, SWT.PUSH);
		buttonRemove.setText("&Remove");
		buttonRemove.addSelectionListener(new RemovePaletteAssocsSelectionAdapter(shell, table, paletteAssociations));
	}
	
	@Override
	public boolean performOk() {
		PaletteAssociations.getInstance().saveToFile();
		Keywords.getInstance().saveToFile();
		return true;
	}

	@Override
	protected void performDefaults() {
		// TODO Auto-generated method stub
		super.performDefaults();
	}

}
