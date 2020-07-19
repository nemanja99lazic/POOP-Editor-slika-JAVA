package komponente;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;


import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Formatter.BMPFormatter;
import Formatter.Formatter;
import Formatter.GNePostojiFajl;


public class Layer {

	private Vector<Vector<Pixel>> pxmat;
	private int visina, sirina;
	private boolean aktivan;
	private int neprozirnost;
	
	public Layer(int visina, int sirina)
	{
		this.visina = visina;
		this.sirina = sirina;
		aktivan = true;
		neprozirnost = 100;
		pxmat = new Vector<Vector<Pixel>>();
		for(int i = 0; i<this.visina; i++)
			pxmat.add(new Vector<Pixel>());
	}
	
	public Layer(int visina, int sirina, boolean aktivan, int neprozirnost)
	{
		this.sirina = sirina;
		this.visina = visina;
		this.setAktivan(aktivan);
		this.setNeprozirnost(neprozirnost);
		pxmat = new Vector<Vector<Pixel>>();
		for(int i = 0; i<this.visina; i++)
			pxmat.add(new Vector<Pixel>());
	}

	public int getVisina() {
		return visina;
	}
	
	public Vector<Vector<Pixel>> getPixelMatrix()
	{
		return pxmat;
	}
	
	public void obojPikseleUnutarPravougaonika(int red, int green, int blue, Rectangle rec)
	{
		for(int i = rec.y; i < rec.getHeight(); i++)
			for(int j = rec.x; j < rec.getWidth(); j++)
				{
					pxmat.get(i).get(j).setR(red);
					pxmat.get(i).get(j).setG(green);
					pxmat.get(i).get(j).setB(blue);
				}
	}

	public int getSirina() {
		return sirina;
	}
	
	public void dopuniProvidnimPikselima(int nova_sirina, int nova_visina)
	{
		for(Vector<Pixel> v : pxmat)
		{
			for(int i = this.sirina; i < nova_sirina; i++)
				v.add(new Pixel());
		}
		for(int i = visina; i<nova_visina; i++)
		{
			pxmat.add(new Vector<Pixel>());
			for(int j = 0; j<nova_sirina; j++)
				pxmat.get(i).add(new Pixel());
		}
		sirina = nova_sirina;
		visina = nova_visina;
	}

	public boolean isAktivan() {
		return aktivan;
	}

	public void setAktivan(boolean aktivan) {
		this.aktivan = aktivan;
	}

	public int getNeprozirnost() {
		return neprozirnost;
	}

	public void setNeprozirnost(int neprozirnost) {
		if(neprozirnost < 0)
			neprozirnost = 0;
		if(neprozirnost > 100)
			neprozirnost = 100;
		this.neprozirnost = neprozirnost;
	}
		
}
