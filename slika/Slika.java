package slika;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Scrollbar;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.border.Border;

import Formatter.BMPFormatter;
import Formatter.Formatter;
import Formatter.GNePostojiFajl;
import komponente.Layer;
import komponente.Selection;
import prozori.*;

public class Slika extends Frame{
	/*
	 AWT DEO
	 */
	private PocetniDijalog pocetniDijalog;
	private String putanjaDoPrvogSloja;
	private Formatter formatter;
	private MestoZaSliku slikaCanvas;
	private Scrollbar horizontalni = null, vertikalni = null;
	private Panel glavniPanel;
	private OpcijeSlojevi opcijeSlojevi = null;
	private OpcijeSelekcije opcijeSelekcije;
	//private OpcijeOperacije opcijeOperacije
	//private OpcijeEksportovanje opcijeEksportovanje
	
	/*
	 NEGRAFICKE KOMPONENTE
	 */
	//private static Slika instance = null;
	private Vector<Layer> slojevi;	
	private int startrecX, startrecY, widthRec, heightRec;  // Koristi se za dodavanje selekcija kod listenera
	private Vector<Rectangle> recVector;
	private Rectangle rectangle;
	private HashMap<String, Selection> selekcije;
	
	/*public static Slika getInstance()
	{
		if(instance == null)
		{
			instance = new Slika();
		}
		return instance;
		
	}*/
	
	public Slika()
	{
		super("Slika");
		formatter = null;
		slojevi = new Vector<Layer>();
		recVector = new Vector<Rectangle>();
		selekcije = new HashMap<String, Selection>();
		setSize(800, 600);
		zatvaranjeNaX();
		konfigurisiGlavnoPlatno();
		kreirajPodprozoreDijaloge();
		dodajMeni();
		//konfigurisiPocetniDijalog(); // ODBLOKIRAJ!! MOGUCI PROBLEM - objekat Slike jos nije kreiran, a poslat kao parametar Dijalogu
		setVisible(true);
		//test();
	}
	
	/**
	 * Helper metoda za dodavanje menija sa dijalozima
	 */
	private void dodajMeni()
	{
		MenuBar bar = new MenuBar();
		Menu menu = new Menu("Opcije");
		this.setMenuBar(bar);
		bar.add(menu);
		MenuItem dialogSlojevi = new MenuItem("Slojevi");
		menu.add(dialogSlojevi);
		dialogSlojevi.addActionListener(
				e->{
						opcijeSlojevi.setVisible(true);
				});
		menu.addSeparator();
		MenuItem dialogSelekcije = new MenuItem("Selekcije");
		menu.add(dialogSelekcije);
		dialogSelekcije.addActionListener(
				e->{
						opcijeSelekcije.setVisible(true);
						opcijeSelekcije.azuriraj(); // zbog true visibility
				});
		menu.addSeparator();
		MenuItem dialogOperacije = new MenuItem("Operacije");
		menu.add(dialogOperacije);
		dialogOperacije.addActionListener(
				e->{
					//Sta se radi na klik na opciju Operacije
				});
		menu.addSeparator();
		MenuItem opcijeEksportovanje = new MenuItem("Eksportovanje");
		menu.add(opcijeEksportovanje);
		opcijeEksportovanje.addActionListener(
				e->{
					//Sta se radi na klik na opciju Eksportovanje
				});
	}
	
	public MestoZaSliku dohvCanvas()
	{
		return this.slikaCanvas;
	}
	
	public Vector<Layer> dohvSlojeve()
	{
		return this.slojevi;
	}
	
	private void test()
	{
		this.dodajSloj("E:\\FAKS\\4. semestar\\POOP\\Projekat C++\\Projekat1\\Projekat1\\Resources\\Examples\\Shapes.bmp");	
	}
	
	private void zatvaranjeNaX()
	{
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}
	
	private void konfigurisiGlavnoPlatno()
	{                                 // OVDE JE PROBLEM
		glavniPanel = new Panel(new BorderLayout());
		
		slikaCanvas = new MestoZaSliku(this.slojevi);
		
		glavniPanel.add(slikaCanvas, BorderLayout.CENTER);
		this.dodajListenereNaSlikaCanvas();
		
		horizontalni = new Scrollbar(Scrollbar.HORIZONTAL);
		vertikalni = new Scrollbar(Scrollbar.VERTICAL);
		glavniPanel.add(horizontalni, BorderLayout.SOUTH);
		glavniPanel.add(vertikalni, BorderLayout.EAST);
		
		horizontalni.addAdjustmentListener(e->{
			int x = e.getValue();
			int y = vertikalni.getValue();
			if(x < slikaCanvas.dohvatiSirinuSlike())
				slikaCanvas.pomeriSliku(x, y);
		});
		
		vertikalni.addAdjustmentListener(e->{
			int y = e.getValue();
			int x = horizontalni.getValue();
			if(y < slikaCanvas.dohvatiVisinuSlike())
				slikaCanvas.pomeriSliku(x, y);
		});
		
		add(glavniPanel);
	}

	private void azurirajSlojevePosleDodavanja()
	{
		int max_visina = slojevi.get(0).getVisina();
		int max_sirina = slojevi.get(0).getSirina();
		for(Layer sloj : slojevi)
		{
			if(sloj.getVisina() > max_visina)
				max_visina = sloj.getVisina();
			if(sloj.getSirina() > max_sirina)
				max_sirina = sloj.getSirina();
		}
		for(Layer sloj : slojevi)
		{
			sloj.dopuniProvidnimPikselima(max_sirina, max_visina);// metodi su tako implementirani da sami postavljaju providne piksele
										// tamo gde je potrebno
		}
	}
	
	/**
	 * Helper metoda za kreiranje dijaloga za dodavanje 
	 * 	prvog sloja, konfigurisanje glavnog platna i dodavanje sloja
	 */
	private void konfigurisiPocetniDijalog()
	{
		
		pocetniDijalog = new PocetniDijalog(this, "Putanja");
		putanjaDoPrvogSloja = pocetniDijalog.dohvPutanju();

		dodajSloj(putanjaDoPrvogSloja);
	}
	
	public void dodajSloj(String putanja)
	{
		try{
			Formatter.MoguciFormati format = Formatter.nadjiFormatFajla(putanja);
			Formatter formater = null;
			if(slojevi == null)
				slojevi = new Vector<Layer>();
			
			switch(format)
			{
			case GRESKA:
				throw new GNePostojiFajl();
			case bmp:
				formater = new BMPFormatter();
				break;
			/*case pam:
				formater = new PAMFormatter();
				break;
			case xml:
				throw new GNePostojiFajl();*/
			}
			if(formater != null)
			{
				formater.ucitaj(putanja);
				
				if(slojevi.isEmpty())
					slojevi.add(formater.getLayer());
				else
				{	
					slojevi.add(0, formater.getLayer());
					azurirajSlojevePosleDodavanja();
				}
				slikaCanvas.azuriraj();
				opcijeSlojevi.azuriraj();
			}
		}
		catch(GNePostojiFajl e)
		{
			System.out.println("Fajl ne postoji, UBACI DIJALOG KOJI CE DA ISKOCI ODAVDE");
			return;
		}
	}
	
	public void setAktivanSloj(int ind, boolean aktivan)
	{
		this.slojevi.get(ind).setAktivan(aktivan);
		this.slikaCanvas.azuriraj();
	}
	
	public void izbrisiSloj(int ind) {
		this.slojevi.remove(ind);
		this.slikaCanvas.azuriraj();
	}
	
	public void promeniProzirnostSloju(int ind, int novaVrednost)
	{
		slojevi.get(ind).setNeprozirnost(novaVrednost);
		this.slikaCanvas.azuriraj();
	}
	
	/**
	 * Helper
	 */
	private void kreirajPodprozoreDijaloge()
	{
		this.opcijeSlojevi = new OpcijeSlojevi(this);
		this.opcijeSelekcije = new OpcijeSelekcije(this);
	}
	
	public void promeniAktivnostSloju(int ind, boolean novaAktivnost)
	{
		slojevi.get(ind).setAktivan(novaAktivnost);
		this.slikaCanvas.azuriraj();
	}
	
	private void dodajListenereNaSlikaCanvas()
	{
		slikaCanvas.addMouseListener(new MouseListener()
		{

			/*
			 * Izbrisi sve pravougaonike sa slike i isprazni vektor pravougaonika
			 */
			public void mouseClicked(MouseEvent e) { 
				rectangle = null;
				recVector.clear();
				slikaCanvas.azuriraj();
				//repaint(); - bice pozvano iz azuriraj()
			}
			

			/*
			 * Inicijalizuj pravougaonik koji ce potencijalno pripadati selekciji
			 */
			public void mousePressed(MouseEvent e) {
				rectangle = new Rectangle();
				startrecY = e.getY();
				startrecX = e.getX();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			
				/*Nacrtaj pravougaonik na sliku direktno i dodaj sliku u vektor pravougaonika koji ce potencijalno
				* pripadati selekciji
				*/
				if(rectangle.width != 0 || rectangle.height != 0)
				{	
					Graphics2D g2d = (Graphics2D)slikaCanvas.getBufferedImage().getGraphics();
					float dash1[] = {10.0f};
					Stroke dashed = new BasicStroke(1.0f,
			                BasicStroke.CAP_BUTT,
			                BasicStroke.JOIN_MITER,
			                10.0f, dash1, 0.0f);
					g2d.setStroke(dashed);
					g2d.setColor(Color.BLACK);
					g2d.draw(rectangle);
					
					recVector.add(rectangle);
					rectangle = null;
				}
				
				slikaCanvas.repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) { // NE RADI NISTA
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});

		
	slikaCanvas.addMouseMotionListener(new MouseMotionAdapter() {
		
		/*
		 * Iscrtavaj pravougaonik na Canvas, ali ne i na sliku direktno
		 */
		public void mouseDragged(MouseEvent e) {
			
			startrecX = Math.min(startrecX, e.getX());
			startrecY = Math.min(startrecY, e.getY());
			widthRec = Math.abs(startrecX - e.getX());
			heightRec = Math.abs(startrecY - e.getY());
			rectangle.setBounds(startrecX, startrecY, widthRec, heightRec);
			slikaCanvas.paintRectangle(rectangle);
			slikaCanvas.repaint();
		}
	
		});
	
	slikaCanvas.addKeyListener(new KeyAdapter()
	{

		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode())
			{
			case KeyEvent.VK_ESCAPE : 
				{
				if(!recVector.isEmpty())
					rectangle = null;
					recVector.clear();
					slikaCanvas.azuriraj();
					//repaint(); - bice pozvano iz azuriraj()
				}	 break;
			case KeyEvent.VK_ENTER :
				{
				   new DodajPravouganikeDijalog(Slika.this);
				   slikaCanvas.azuriraj();
				   opcijeSelekcije.azuriraj();
				} break;
			}
			
		}
		
	});

	}
	
	public Vector<Rectangle> dohvVektorPravougaonika()
	{
		return this.recVector;
	}
	
	public HashMap<String, Selection> dohvMapuSelekcija()
	{
		return this.selekcije;
	}

public static void main(String[] args)
{
	Slika slika = new Slika();
	slika.dodajSloj("E:\\FAKS\\4. semestar\\POOP\\Projekat C++\\Projekat1\\Projekat1\\Resources\\Examples\\Shapes.bmp");
}
}