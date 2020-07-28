package Formatter;

import java.awt.image.*;

import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import komponente.Layer;
import slika.Slika;

public abstract class Formatter {
	
	protected Layer sloj;
	protected Slika slika; // treba za XMLFormatter
	public enum MoguciFormati {GRESKA, bmp, pam, xml};
	
	public Formatter()
	{
		sloj = null;
		slika = null;
	}
	public abstract void ucitaj(String putanja) throws GNePostojiFajl;
	public abstract void sacuvaj(String putanja, Slika slika);
	public Layer getLayer()
	{
		return sloj;
	}
	
	/**
	 * Konvertovanje niza od 4 bajta u int
	 * @param b - niz bajtova
	 * @return
	 */
	protected static int byteArrToInt(byte b[])
	{
		int broj = 0;
		int stepen = 0;
		for(int i = 0; i<b.length; i++)
			{
				broj = broj | (b[i] & 0xff) << stepen;
				stepen+=8;
			}
		return broj;
	}
	
	
	/**
	 * Konvertovanje broja u niz bajtova
	 * @param num - broj
	 * @param b - niz bajtova
	 */
	protected static void intToByteArr(int num, byte b[])
	{
		for(int i = 0; i < b.length; i++)
		{
			b[i] = (byte)(num & 0xff);
			num = num >> 8;
		}
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
