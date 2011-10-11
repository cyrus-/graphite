package edu.cmu.cs.graphite.preferences;

public class PaletteAssociations extends BasePaletteAssociations {
	
	private static PaletteAssociations _instance = null;
	
	public static PaletteAssociations getInstance() {
		if (_instance == null) {
			_instance = new PaletteAssociations();
		}
		
		return _instance;
	}
	
	@Override
	protected String getFilename() {
		return "paletteAssociations.xml";
	}

}
