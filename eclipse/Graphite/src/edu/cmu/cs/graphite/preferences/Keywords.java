package edu.cmu.cs.graphite.preferences;

public class Keywords extends BasePaletteAssociations {
	
	private static Keywords _instance = null;
	
	public static Keywords getInstance() {
		if (_instance == null) {
			_instance = new Keywords();
		}
		
		return _instance;
	}
	
	@Override
	protected String getFilename() {
		return "keywords.xml";
	}

}
