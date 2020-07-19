package Formatter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.*;
import javax.imageio.ImageIO;

import komponente.Layer;
import komponente.Pixel;
import slika.Slika;

public class BMPFormatter extends Formatter{
	
	private class BMPHeader
	{
		byte id_field[] = {0x42,0x4d};
		byte size_of_file[] = new byte[4]; // promenljivo
		byte unused1[] = {0x00, 0x00};
		byte unused2[] = {0x00, 0x00};
		byte start_of_pixels_array[] = {0x7a,0x00,0x00,0x00};
		byte bytes_in_DIB_header[] = {0x6c,0x00,0x00,0x00};  
		byte width[] = new byte[4];  //treba da se promeni
		byte height[] = new byte[4]; // treba da se promeni
		byte number_of_color_planes[] = {0x01,0x00}; 
		byte number_of_bits_per_pixel[] = {0x20,0x00}; 
		byte BI_RGB[] = {0x03,0x00,0x00,0x00};
		byte size_of_bitmap_in_bytes[] = new byte[4];  // promenljivo
		byte unused3_fixed[] = {0x13,0x0b,0x00,0x00};
		byte unused4_fixed[] = {0x13,0x0b,0x00,0x00};
		byte unused5_fixed[] = {0x00, 0x00, 0x00, 0x00};
		byte unused6_fixed[] = {0x00, 0x00, 0x00, 0x00};
		//pocetak niza piksela za 24-bit
		//dodatak za 32-bit
	}
	
	private class BMP32addition
	{
		byte red_mask[] = {0x00,0x00,(byte)0xff,0x00};
		byte green_mask[] = {0x00,(byte)0xff,0x00,0x00};
		byte blue_mask[] = {(byte)0xff,0x00,0x00,0x00};
		byte alpha_mask[] = {0x00,0x00,0x00,(byte)0xff};
		byte unused1_fixed[] = {0x42, 0x47, 0x52, 0x73};
		byte unusedarr[] = new byte[16*4];
		
		public BMP32addition()
		{
			for(int i = 0; i<64; i++)
			{
				unusedarr[i] = 0;
			}
		}
	}
	
	private class BMPPixel
	{
		byte blue;
		byte green;
		byte red;
		byte alpha;
	}
	
	public BMPFormatter()
	{
		super();
	}
	
	/**
	 * Glavna metoda za ucitavanje
	 */
	public void ucitaj(String putanja) throws GNePostojiFajl {
		File file = new File(putanja);
		if(!file.exists())
			throw new GNePostojiFajl();
		try {
			BMPHeader header = new BMPHeader();
			RandomAccessFile inputfile = new RandomAccessFile(file, "r");
			
			ucitajHeader(inputfile, header);
			int visina = byteArrToInt(header.height);
			int sirina = byteArrToInt(header.width);
			this.sloj = new Layer(visina, sirina);
			
			inputfile.seek((long)byteArrToInt(header.start_of_pixels_array));
			
			int padding = byteArrToInt(header.number_of_bits_per_pixel) == 32 ? 0 : sirina % 4;
			
			BMPPixel px = new BMPPixel();
			
			for(int i = visina - 1; i >= 0; i--)
			{	for(int j = 0; j<sirina; j++)
				{
					px.blue = inputfile.readByte();
					px.green = inputfile.readByte();
					px.red = inputfile.readByte();
					if(byteArrToInt(header.number_of_bits_per_pixel) == 32)
						px.alpha = inputfile.readByte();
					else
						px.alpha = (byte)255;
					sloj.getPixelMatrix().get(i).add(new Pixel(px.red & 0xff, px.green & 0xff, px.blue & 0xff, px.alpha & 0xff));
				}
				for(int k = 0; k < padding; k++)
					inputfile.readByte();
			}
			inputfile.close();
		} catch (IOException e) {
			// Ne radi nista, jer su vec obradjeni izuzeci
			System.out.println("Nekim cudom uslo u obradu izuzetka");
		}
	}
	/**
	 * Konvertovanje niza od 4 bajta u int
	 * @param b
	 * @return
	 */
	private static int byteArrToInt(byte b[])
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
	 * Helper metoda za ucitavanje headera
	 * @param inputfile
	 * @param header
	 */
	private void ucitajHeader(RandomAccessFile inputfile, BMPHeader header)
	{
	try {
		inputfile.read(header.id_field);
		inputfile.read(header.size_of_file);
		inputfile.read(header.unused1);
		inputfile.read(header.unused2);
		inputfile.read(header.start_of_pixels_array);
		inputfile.read(header.bytes_in_DIB_header);
		inputfile.read(header.width);
		inputfile.read(header.height);
		inputfile.read(header.number_of_color_planes);
		inputfile.read(header.number_of_bits_per_pixel);
		inputfile.read(header.BI_RGB);
		inputfile.read(header.size_of_bitmap_in_bytes);
		inputfile.read(header.unused3_fixed);
		inputfile.read(header.unused4_fixed);
		inputfile.read(header.unused5_fixed);
		inputfile.read(header.unused6_fixed);
		}
		catch(IOException e)
		{
			System.out.println("Unutrasnje stanje fajla nije dobro");
		}
	}
	
	/**
	 * Treba da se implementira
	 * @param putanja
	 */
	public void sacuvaj(String putanja, Slika slika) {
		
	}
	
}
