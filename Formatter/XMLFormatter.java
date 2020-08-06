package Formatter;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.xml.sax.SAXException;

import greske.GNeodgovarajuciFormatFajla;
import komponente.Layer;
import komponente.Pixel;
import komponente.Selection;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import slika.Slika;

public class XMLFormatter extends Formatter{

	private static final String ext = "_layer";
	private static final String format = ".svdlayer";
	
	public XMLFormatter(Slika slika)
	{
		super();
		this.slika = slika;
	}
	
	@Override
	public void ucitaj(String putanja) throws GNePostojiFajl {
		File xmlDoc = new File(putanja);
		int visina, sirina;
		DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuild = dbFact.newDocumentBuilder();
			Document doc = dBuild.parse(xmlDoc);
			
			//slika.reset(); // Napravi prazan projekat
			
			//doc.getDoctype().normalize();
			Element root = doc.getDocumentElement(); // vraca root "Slika"
			
			sirina = Integer.parseInt(root.getElementsByTagName("Width").item(0).getTextContent());
			visina = Integer.parseInt(root.getElementsByTagName("Height").item(0).getTextContent());
			
			int imaAktivnihSelekcija = Integer.parseInt(root.getElementsByTagName("HasActiveSelection").item(0).getTextContent());
			if(imaAktivnihSelekcija == 1)
				slika.postaviImaAktivnihSelekcija(true);
			else
				slika.postaviImaAktivnihSelekcija(false);
				
			NodeList listSelectionMap = ((Element)root.getElementsByTagName("SelectionMap").item(0)).getElementsByTagName("Selection");
			HashMap<String, Selection> mapaSelekcija = new HashMap<String, Selection>();
			for(int i = 0; i < listSelectionMap.getLength(); i++)
			{
				Element elemSelekcija = (Element)listSelectionMap.item(i);
				
				int aktivna;
				boolean boolAktivna;
				String ime = elemSelekcija.getAttribute("name");
				aktivna = Integer.parseInt(elemSelekcija.getElementsByTagName("IsActiveSelection").item(0).getTextContent());
				
				Vector<Rectangle> rec_vector = new Vector<Rectangle>();
				NodeList listRectangles = elemSelekcija.getElementsByTagName("Rectangle");
				
				for(int ir = 0; ir < listRectangles.getLength(); ir++)
				{
					int up, left, height, width;
					Element nodeRec = (Element)listRectangles.item(ir);
					up = Integer.parseInt(nodeRec.getElementsByTagName("Up").item(0).getTextContent());
					left = Integer.parseInt(nodeRec.getElementsByTagName("Left").item(0).getTextContent());
					height = Integer.parseInt(nodeRec.getElementsByTagName("Height").item(0).getTextContent());
					width = Integer.parseInt(nodeRec.getElementsByTagName("Width").item(0).getTextContent());
					rec_vector.add(new Rectangle(left, up, width,height));
					
				}
				if(aktivna == 1)
					boolAktivna = true;
				else
					boolAktivna = false;
				mapaSelekcija.put(ime, new Selection(rec_vector, boolAktivna));
			}
			slika.postaviMapuSelekcija(mapaSelekcija);
			
			//Ostalo ucitavanje slojeva
			slika.dohvSlojeve().clear();
			NodeList listLayers = ((Element)root.getElementsByTagName("Layers").item(0)).getElementsByTagName("Layer");
			for(int i = 0; i < listLayers.getLength(); i++)
			{
				Element nodeLayer = (Element) listLayers.item(i);
				int sirinal = Integer.parseInt(nodeLayer.getElementsByTagName("Width").item(0).getTextContent());
				int visinal = Integer.parseInt(nodeLayer.getElementsByTagName("Height").item(0).getTextContent());
				int aktivan = Integer.parseInt(nodeLayer.getElementsByTagName("IsActive").item(0).getTextContent());
				int neprozirnost = Integer.parseInt(nodeLayer.getElementsByTagName("Opacity").item(0).getTextContent());
				String path = nodeLayer.getElementsByTagName("Path").item(0).getTextContent();
				String pathToSavedLayer = ((Element)nodeLayer.getElementsByTagName("PixelList").item(0)).getElementsByTagName("PathToSavedLayer").item(0).getTextContent();
				boolean boolAktivan = aktivan == 1 ? true : false;
				
				Layer lay = new Layer(visinal, sirinal, boolAktivan, neprozirnost);
				RandomAccessFile file = new RandomAccessFile(new File(pathToSavedLayer), "rw");
				
				byte r, g, b, a;
				for(int il = 0; il < visinal; il++)
					for(int jl = 0; jl < sirinal; jl++)
					{
						r = file.readByte();
						g = file.readByte();
						b = file.readByte();
						a = file.readByte();
						
						lay.getPixelMatrix().get(il).add(new Pixel((int)r & 0xff, (int)g & 0xff, (int)b & 0xff, (int)a & 0xff));
					}
				slika.dohvSlojeve().add(lay);
			}
			
			slika.azurirajSve();
		} 
		catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void sacuvaj(String putanja, Slika slika) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			
			Element root = doc.createElement("Slika");
			doc.appendChild(root);
			
			int visina = slika.dohvSlojeve().get(0).getVisina();
			int sirina = slika.dohvSlojeve().get(0).getSirina();
			
			Element nodeWidthS = doc.createElement("Width");
			nodeWidthS.appendChild(doc.createTextNode(String.valueOf(sirina)));
			root.appendChild(nodeWidthS);
			
			Element nodeHeightS = doc.createElement("Height");
			nodeHeightS.appendChild(doc.createTextNode(String.valueOf(visina)));
			root.appendChild(nodeHeightS);
			
			int hasActiveSelection = slika.dohvImaAktivnihSelekcija() ? 1 : 0;
			Element nodeHasActiveSelection = doc.createElement("HasActiveSelection");
			nodeHasActiveSelection.appendChild(doc.createTextNode(String.valueOf(hasActiveSelection)));
			root.appendChild(nodeHasActiveSelection);
			
			int exported = slika.dohvEksportovana() ? 1 : 0;
			Element nodeExported = doc.createElement("Exported");
			nodeExported.appendChild(doc.createTextNode(String.valueOf(exported)));
			root.appendChild(nodeExported);
			
			Element nodeOperation = doc.createElement("Operation");
			nodeOperation.appendChild(doc.createTextNode("nullptr"));
			root.appendChild(nodeOperation);
			
			Element nodeSelectionMap = doc.createElement("SelectionMap");
			
			for(String ime : slika.dohvMapuSelekcija().keySet())
			{
				Element nodeSelection = doc.createElement("Selection");
				Selection selekcija = slika.dohvMapuSelekcija().get(ime);
				nodeSelection.setAttribute("name", ime);
				
				int isActiveSel = selekcija.getActive() ? 1 : 0;
				Element nodeIsActiveSelection = doc.createElement("IsActiveSelection");
				nodeIsActiveSelection.appendChild(doc.createTextNode(String.valueOf(isActiveSel)));
				nodeSelection.appendChild(nodeIsActiveSelection);
				
				for(Rectangle rec : selekcija.getRectangleVector())
				{
					Element nodeRectangle = doc.createElement("Rectangle");
					
					Element nodeUp = doc.createElement("Up");
					nodeUp.appendChild(doc.createTextNode(String.valueOf(rec.y)));
					nodeRectangle.appendChild(nodeUp);
					
					Element nodeLeft = doc.createElement("Left");
					nodeLeft.appendChild(doc.createTextNode(String.valueOf(rec.x)));
					nodeRectangle.appendChild(nodeLeft);
					
					Element nodeHeight = doc.createElement("Height");
					nodeHeight.appendChild(doc.createTextNode(String.valueOf(rec.height)));
					nodeRectangle.appendChild(nodeHeight);
					
					Element nodeWidth = doc.createElement("Width");
					nodeWidth.appendChild(doc.createTextNode(String.valueOf(rec.width)));
					nodeRectangle.appendChild(nodeWidth);
					
					nodeSelection.appendChild(nodeRectangle);
				}
				
				nodeSelectionMap.appendChild(nodeSelection);
			}
			root.appendChild(nodeSelectionMap);
			
			Element nodeLayers = doc.createElement("Layers");
			
			int redBrSloja = 0;
			for(Layer layer : this.slika.dohvSlojeve())
			{
				Element nodeLayer = doc.createElement("Layer");
				
				int lVisina, lSirina, lAktivan, lNeprozirnost;
				String lPutanja = " ";
				
				lVisina = layer.getVisina();
				lSirina = layer.getSirina();
				lAktivan = layer.isAktivan() ? 1 : 0;
				lNeprozirnost = layer.getNeprozirnost();
			
				Element nodeWidth = doc.createElement("Width");
				nodeWidth.appendChild(doc.createTextNode(Integer.toString(lSirina)));
				nodeLayer.appendChild(nodeWidth);
				
				Element nodeHeight = doc.createElement("Height");
				nodeHeight.appendChild(doc.createTextNode(Integer.toString(lVisina)));
				nodeLayer.appendChild(nodeHeight);
				
				Element nodeIsActive = doc.createElement("IsActive");
				nodeIsActive.appendChild(doc.createTextNode(Integer.toString(lAktivan)));
				nodeLayer.appendChild(nodeIsActive);
				
				Element nodeOpacity = doc.createElement("Opacity");
				nodeOpacity.appendChild(doc.createTextNode(Integer.toString(lNeprozirnost)));
				nodeLayer.appendChild(nodeOpacity);
				
				Element nodePath = doc.createElement("Path");
				nodePath.appendChild(doc.createTextNode(lPutanja));
				nodeLayer.appendChild(nodePath);
				
				Element nodePixelList = doc.createElement("PixelList");
				
				Pattern rx = Pattern.compile("(.*)\\.xml");
				Matcher m = rx.matcher(putanja);
				String template_putanja_svd = "";
				String putanjaMatricePiksela;
				if(m.matches())
					template_putanja_svd = m.group(1);
				// Nece biti else, jer ce greska, ako postoji, biti uhvacena pre poziva metode
				
				template_putanja_svd += XMLFormatter.ext;
				putanjaMatricePiksela = template_putanja_svd + redBrSloja + XMLFormatter.format;
				redBrSloja++;
				
				RandomAccessFile file = new RandomAccessFile(new File(putanjaMatricePiksela), "rw");
				for(int i = 0; i < lVisina; i++)
					for(int j = 0; j < lSirina; j++)
					{
						Pixel px = layer.getPixelMatrix().get(i).get(j);
						file.write(px.getR() & 0xff);
						file.write(px.getG() & 0xff);
						file.write(px.getB() & 0xff);
						file.write(px.getA() & 0xff);
						
					}
				
				Element nodePathToSavedLayer = doc.createElement("PathToSavedLayer");
				nodePathToSavedLayer.appendChild(doc.createTextNode(putanjaMatricePiksela));
				
				nodePixelList.appendChild(nodePathToSavedLayer);

				nodeLayer.appendChild(nodePixelList);
			
				nodeLayers.appendChild(nodeLayer);
			}
		
			root.appendChild(nodeLayers);
			
			//Upisi doc u xml fajl
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Postavi uvlacenje cvorova
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); // Kolicina spejsova za uvlacenje
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(putanja));
			transformer.transform(source, result);
			
			
		} catch (ParserConfigurationException | TransformerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
