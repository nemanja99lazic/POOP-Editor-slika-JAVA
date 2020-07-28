package slika;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Scrollbar;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Formatter.*;
import Formatter.GNePostojiFajl;
import komponente.Layer;
import komponente.Pixel;
import komponente.Selection;

public class MestoZaSliku extends Canvas {

	private Vector<Layer> slojevi;
	private Scrollbar horizontalni, vertikalni;
	private BufferedImage zaIscrtavanje = null;
	private boolean crtajPravougaonik = false;
	
	// pre ovoga treba pozvati metodu Layer.dopuniProvidnimPikselima
	public void azuriraj()
	{
		if(slojevi.isEmpty())
			zaIscrtavanje = null;
		else 
		{
			double udeo,r,g,b,a, total_transparency;
			int visina = slojevi.get(0).getVisina(); // visina i sirina svih slojeva su jednake 
			int sirina = slojevi.get(0).getSirina(); 
			int[][] boje = new int[visina][sirina];
			for(int i = 0; i < visina; i++)
				for(int j = 0; j< sirina; j++)
				{
					r = g = b = a = 0;
					udeo = 1;
					for(Layer l : slojevi)
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
					boje[i][j] = (((byte)a) & 0xff) << 24 | (((byte)r) & 0xff) << 16 | (((byte)g) & 0xff) << 8 | (((byte)b) & 0xff) << 0;
				}
			zaIscrtavanje = new BufferedImage(sirina, visina, BufferedImage.TYPE_INT_ARGB);
			
			for(int i = 0; i < visina; i++)
				for(int j = 0; j < sirina; j++)
					zaIscrtavanje.setRGB(j, i, boje[i][j]);
		}
		repaint();
	}
	
	public MestoZaSliku(Vector<Layer> slojevi)
	{
		this.slojevi = slojevi;
	}
	
	/**
	 * Dodavanje listenera za selektovanje pravougaonika na canvasu:
	 * mousePressed - gornje levo teme pravougaonika
	 * mouseReleased - donje desno teme pravouganika
	 * mouseDragged - repaint Pravougaonika sa isprekidanim linijama
	 * 
	 */

	public void paintRectangle(Rectangle rectangle)
	{
		
		Graphics2D g2d = (Graphics2D)this.getGraphics().create();
		float dash1[] = {10.0f};
		Stroke dashed = new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, dash1, 0.0f);
		g2d.setStroke(dashed);
		g2d.draw(rectangle);
		g2d.dispose();
	}
	
	public void paintRectangleOnImage(Rectangle rectangle)
	{
		Graphics2D g2d = (Graphics2D)this.getBufferedImage().getGraphics();
		float dash1[] = {10.0f};
		Stroke dashed = new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, dash1, 0.0f);
		g2d.setStroke(dashed);
		g2d.setColor(Color.BLACK);
		g2d.draw(rectangle);
	}
	
	public BufferedImage getBufferedImage()
	{
		return this.zaIscrtavanje;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(zaIscrtavanje != null)
			g.drawImage(zaIscrtavanje, 0, 0, null);
	}
	
	
	/**
	 * iscrtava sliku tako sto joj postavi gornji levi piksel u koordinati (xGore, yGore)
	 * @param xGore - x koordinata gornjeg levog piksela slike
	 * @param yGore - y koordinata gornjeg levog piksela slike
	 */
	public void pomeriSliku(int xGore, int yGore)
	{
		Graphics g = this.getGraphics();
		g.drawImage(zaIscrtavanje, -xGore, -yGore, null);
	}
	
	public int dohvatiVisinuSlike()
	{
		return this.zaIscrtavanje.getHeight();
	}
	
	public int dohvatiSirinuSlike()
	{
		return this.zaIscrtavanje.getHeight();
	}
	
}
