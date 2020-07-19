package Formatter;

import java.awt.image.*;

import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import komponente.Layer;
import slika.Slika;

public abstract class Formatter {
	
	protected Layer sloj;
	public enum MoguciFormati {GRESKA, bmp, pam, xml};
	
	public Formatter()
	{
		sloj = null;
	}
	public abstract void ucitaj(String putanja) throws GNePostojiFajl;
	public abstract void sacuvaj(String putanja, Slika slika);
	public Layer getLayer()
	{
		return sloj;
	}
	
	public static MoguciFormati nadjiFormatFajla(String putanja) throws GNePostojiFajl
	{
		Pattern p = Pattern.compile("^(.*)\\.(...)$");
		Matcher m = p.matcher(putanja);
		String format;
		Formatter.MoguciFormati enumFormat;
		if(m.matches())
			format = m.group(2);
		else
			throw new GNePostojiFajl();
		try
		{
			if(format.equals(new String("GRESKA"))) // Proveri da nije lazni format ".GRESKA"
				throw new IllegalArgumentException();
			enumFormat = Formatter.MoguciFormati.valueOf(format);
		}
		catch(IllegalArgumentException e) // neodgovarajuci format
		{
			enumFormat = Formatter.MoguciFormati.GRESKA;
		}
		return enumFormat;
	}
}
