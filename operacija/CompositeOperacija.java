package operacija;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import Formatter.Formatter;
import greske.GNeodgovarajuciFormatFajla;

public class CompositeOperacija extends Operacija {
	
	public CompositeOperacija(String putanja, String ime)
	{
		this.path = putanja;
		this.cnst = -1;
		this.name = ime;
		this.listaOperacija = new LinkedList<Operacija>();
	}
	
	public static boolean proveriIspravnostFormata(String putanja)
	{
		File file = new File(putanja);
		Pattern p = Pattern.compile("^(.*)\\.(...)$");
		Matcher m = p.matcher(putanja);
		String format = "";
		if(m.matches())
			format = m.group(2);
		if(format.equals(new String("fun")))
			return true;
		return false;
	}
	
	public void sacuvaj(String putanja)
	{
		try 
		{
			if(proveriIspravnostFormata(putanja) == false)
				throw new GNeodgovarajuciFormatFajla();			
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.newDocument();
				
				Element root = doc.createElement("Function");
				root.setAttribute("name", this.name);
				
				doc.appendChild(root);
				
				for(Operacija op : this.listaOperacija)
				{
					if(op.listaOperacija == null)
					{
						Element nodeBasicFunkcija = doc.createElement("BasicFunction");
						nodeBasicFunkcija.setAttribute("cnst", String.valueOf(op.cnst));
						nodeBasicFunkcija.appendChild(doc.createTextNode(op.name));
						
						root.appendChild(nodeBasicFunkcija);
					}
					else
					{
						Element nodeCompositeFunction = doc.createElement("CompositeFunction");
						nodeCompositeFunction.setAttribute("name", op.name);
						
						Element nodePath = doc.createElement("Path");
						nodePath.appendChild(doc.createTextNode(op.path));
						
						nodeCompositeFunction.appendChild(nodePath);
						root.appendChild(nodeCompositeFunction);
					}
				}
				
				//Upisi doc u xml fajl
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Postavi uvlacenje cvorova
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); // Kolicina spejsova za uvlacenje
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(putanja));
				transformer.transform(source, result);
			}
			catch(ParserConfigurationException | TransformerException e)
			{
				
			}
		}
		catch(GNeodgovarajuciFormatFajla e)
		{
			
		}
	}
	
	public void add(Operacija operacija)
	{
		listaOperacija.add(operacija);
	}
	
	public void remove(int indeks)
	{
		listaOperacija.remove(indeks);
	}
	
	public void postaviIme(String ime)
	{
		this.name = ime;
	}
	
	public String dohvIme()
	{
		return this.name;
	}
	
	/**
	 * Proverava da li postoji .fun fajl
	 */
	public static boolean postojiFajlSaOperacijom(String putanja)
	{
		
		File file = new File(putanja);
		Pattern p = Pattern.compile("^(.*)\\.(...)$");
		Matcher m = p.matcher(putanja);
		String format = "";
		if(m.matches())
			format = m.group(2);
		return file.exists() && format.equals(new String("fun"));
	}
	
	/**
	 * Otvara xml fajl i iz njega cita i vraca samo ime operacije
	 */
	public static String odrediImeOperacije(String putanja)
	{
		File xmlDoc = new File(putanja);
		String imeOperacije = "";
		DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuild = dbFact.newDocumentBuilder();
			Document doc = dBuild.parse(xmlDoc);
			Element root = doc.getDocumentElement();
			imeOperacije = root.getAttribute("name");
		}
		catch(IOException | SAXException | ParserConfigurationException e)
		{
		}
		return imeOperacije;
	}
		
	
	
}
