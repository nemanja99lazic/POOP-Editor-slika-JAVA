package Formatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import komponente.Layer;
import komponente.Pixel;
import slika.Slika;

public class PAMFormatter extends Formatter{

	private static final byte newline = 0x0a;
	
	public PAMFormatter()
	{
		super();
	}
	
	private class PAMHeader
	{
		/*char[] magic_word = new char[]{'P', '7'};
		byte[] width = new byte[2];
		byte[] height = new byte[4];
		byte[] depth = new byte[4];
		byte[] maxval = new byte[4];
		String tupltype;
		String end_of_header_word = "ENDHDR";*/
		
		String magic_word;
		int width;
		int height;
		int depth;
		int maxval;
		String tupltype;
		String end_of_header_word = "ENDHDR";
	}
	
	private class PAMPixel
	{
		byte blue;
		byte green;
		byte red;
		byte alpha;
	}
	
	
	@Override
	public void ucitaj(String putanja) throws GNePostojiFajl {
		File file = new File(putanja);
		if(!file.exists())
			throw new GNePostojiFajl();
		try 
		{
			PAMHeader header = new PAMHeader();
			PAMPixel px = new PAMPixel();
			RandomAccessFile inputfile = new RandomAccessFile(file, "r");
			
			ucitajHeader(inputfile, header);
			
			this.sloj = new Layer(header.height, header.width);
			
			for(int i = 0; i < header.height; i++)
				for(int j = 0; j < header.width; j++)
				{
					px.red = (byte)inputfile.read();
					px.green = (byte)inputfile.read();
					px.blue = (byte)inputfile.read();
					if (header.depth == 4)
						px.alpha = (byte)inputfile.read();
					else
						px.alpha = (byte)255;
					sloj.getPixelMatrix().get(i).add(new Pixel(px.red & 0xff, px.green & 0xff, px.blue & 0xff, px.alpha & 0xff));
				}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void ucitajHeader(RandomAccessFile inputfile, PAMHeader header)
	{
		String line;
		Pattern rx = Pattern.compile("^[A-Z]* (.*)$");
		Matcher m;
		
		try {
			line = inputfile.readLine(); // ucitaj 1. liniju
			
			line = inputfile.readLine(); // ucitaj 2. liniju
			m = rx.matcher(line);
			if(m.matches())
				header.width = Integer.parseInt(m.group(1));
			
			line = inputfile.readLine(); // ucitaj 3. liniju
			m = rx.matcher(line);
			if(m.matches())
				header.height = Integer.parseInt(m.group(1));
			
			line = inputfile.readLine(); // ucitaj 4. liniju
			m = rx.matcher(line);
			if(m.matches())
				header.depth = Integer.parseInt(m.group(1));
			
			line = inputfile.readLine(); // ucitaj 5. liniju
			m = rx.matcher(line);
			if(m.matches())
				header.maxval = Integer.parseInt(m.group(1));
			
			inputfile.readLine();
			inputfile.readLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void sacuvaj(String putanja, Slika slika) {
		PAMHeader header = new PAMHeader();
		File f = new File(putanja);
		try {
			RandomAccessFile outputfile = new RandomAccessFile(f, "rw");
			header.magic_word = "P7";
			header.width = slika.dohvSlojeve().get(0).getSirina();
			header.height = slika.dohvSlojeve().get(0).getVisina();
			header.depth = 4;
			header.maxval = 255;
			header.tupltype = "RGB_ALPHA";
			upisiHeaderUFajl(outputfile, header);
			
			
			// racunanje i upis piksela
			double udeo,r,g,b,a, total_transparency;
			int visina = slika.dohvSlojeve().get(0).getVisina(); // visina i sirina svih slojeva su jednake 
			int sirina = slika.dohvSlojeve().get(0).getSirina();
			for(int i = 0; i < visina; i++)
			for(int j = 0; j < sirina; j++)
			{
				r = g = b = a = 0;
				udeo = 1;
				for(Layer l : slika.dohvSlojeve())
				{
					if(l.isAktivan())
					{
						Pixel sp = l.getPixelMatrix().get(i).get(j);
						total_transparency = ((double)sp.getA() / 255) * ((double)l.getNeprozirnost() / 100);
						r += sp.getR() * total_transparency * udeo;
						g += sp.getG() * total_transparency * udeo;
						b += sp.getB() * total_transparency * udeo;
						udeo = udeo - udeo * total_transparency;
					}
								
				}
				udeo = 1 - udeo;
				a = udeo * 255;
				if (udeo != 0)
				{
					r = r / udeo;
					g = g / udeo;
					b = b / udeo;
				}
				else
					r = b = g = 0;
				PAMPixel p = new PAMPixel();
				p.red = (byte)r;
				p.green = (byte)g;
				p.blue = (byte)b;
				p.alpha = (byte)a;
				outputfile.write(p.red & 0xff);
				outputfile.write(p.green & 0xff);
				outputfile.write(p.blue & 0xff);
				outputfile.write(p.alpha & 0xff);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void upisiHeaderUFajl(RandomAccessFile outputfile, PAMHeader header)
	{
		String line[] = new String[7];
		
		line[0] = "P7";
		line[1] = "WIDTH " + header.width;
		line[2] = "HEIGHT " + header.height;
		line[3] = "DEPTH " + header.depth;
		line[4] = "MAXVAL " + header.maxval;
		line[5] = "TUPLTYPE " + header.tupltype;
		line[6] = "ENDHDR";
		try {
			for(int i = 0; i < 7; i++)
			{
					outputfile.writeBytes(line[i]);
					outputfile.writeByte(PAMFormatter.newline);	
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
