package edu.cmu.cs.graphite.preferences;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import edu.cmu.cs.graphite.core.Activator;

public abstract class BasePaletteAssociations {
	
	protected BasePaletteAssociations() {
		this.elements = new HashMap<String, PaletteAssociation>();
	}
	
	private HashMap<String, PaletteAssociation> elements;
	
	public final void addElement(String name, String displayString, String url, String description, int initWidth, int initHeight) {
		PaletteAssociation element = new PaletteAssociation();
		element.name = name;
		element.displayString = displayString;
		element.url = url;
		element.description = description;
		element.initWidth = initWidth;
		element.initHeight = initHeight;
		
		elements.put(name, element);
	}
	
	protected abstract String getFilename();
	
	public final void removeElement(String name) {
		elements.remove(name);
	}
	
	public final PaletteAssociation getElement(String name) {
		return elements.get(name);
	}
	
	public final Set<Map.Entry<String, PaletteAssociation>> entrySet() {
		return elements.entrySet();
	}
	
	public final Collection<PaletteAssociation> values() {
		return elements.values();
	}
	
	public final int size() {
		return elements.size();
	}
	
	public File getMetadataFolder() {
		File location = Activator.getDefault().getStateLocation().toFile();
//		if (!location.exists()) {
//			location.mkdirs();
//		}
		
		return location;
	}
	
	public void loadFromFile() {
		File file = new File(getMetadataFolder(), getFilename());
		if (!file.exists()) { return; }
		
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			
			this.elements.clear();
			
			Element root = doc.getDocumentElement();
			for (int i = 0; i < root.getChildNodes().getLength(); ++i) {
				Node node = root.getChildNodes().item(i);
				if (node.getNodeType() != Node.ELEMENT_NODE) { continue; }
				
				addElement(
						node.getAttributes().getNamedItem("name").getNodeValue(),
						node.getAttributes().getNamedItem("displayString").getNodeValue(),
						node.getAttributes().getNamedItem("url").getNodeValue(),
						node.getAttributes().getNamedItem("description").getNodeValue(),
						Integer.parseInt(node.getAttributes().getNamedItem("initWidth").getNodeValue()),
						Integer.parseInt(node.getAttributes().getNamedItem("initHeight").getNodeValue()));
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveToFile() {
		File file = new File(getMetadataFolder(), getFilename());
		Document doc = getXmlDocument();
		if (doc != null) {
			writeXmlDocument(file, doc);
		}
	}
	
	private void writeXmlDocument(File file, Document doc) {
		try {
			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// create string from xml tree
			FileWriter writer = new FileWriter(file);
			StreamResult result = new StreamResult(writer);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			
			writer.close();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Document getXmlDocument() {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			// create the root element and add it to the document
			Element root = doc.createElement("paletteAssociations");
			doc.appendChild(root);
			
			for (PaletteAssociation element : values()) {
				Element xmlElement = doc.createElement("element");
				xmlElement.setAttribute("name", element.name);
				xmlElement.setAttribute("displayString", element.displayString);
				xmlElement.setAttribute("url", element.url);
				xmlElement.setAttribute("description", element.description);
				xmlElement.setAttribute("initWidth", Integer.toString(element.initWidth));
				xmlElement.setAttribute("initHeight", Integer.toString(element.initHeight));
				
				root.appendChild(xmlElement);
			}

			return doc;

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
