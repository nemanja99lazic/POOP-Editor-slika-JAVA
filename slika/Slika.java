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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import Formatter.PAMFormatter;
import Formatter.XMLFormatter;
import greske.GNeodgovarajuciFormatFajla;
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
	private boolean imaAktivnihSelekcija;
	private boolean eksportovana;
	
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
		imaAktivnihSelekcija = false;
		eksportovana = false;
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
		
		Menu sacuvaj = new Menu("Sacuvaj");
		MenuItem sacuvajProjekat = new MenuItem("Sacuvaj projekat");
		sacuvaj.add(sacuvajProjekat);
		bar.add(sacuvaj);
		
		sacuvajProjekat.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e) {
						new SacuvajProjekatDijalog(Slika.this);
					}
					
				});
		
		Menu ucitaj = new Menu("Ucitaj");
		MenuItem ucitajProjekat = new MenuItem("Ucitaj projekat");
		ucitaj.add(ucitajProjekat);
		bar.add(ucitaj);
		
		ucitajProjekat.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				new UcitajProjekatDijalog(Slika.this);
			}
			
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
	
	/*private void test()
	{
		this.dodajSloj("E:\\FAKS\\4. semestar\\POOP\\Projekat C++\\Projekat1\\Projekat1\\Resources\\Examples\\Shapes.bmp");	
	}*/
	
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
	{                                 
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
		try {
			Formatter.MoguciFormati format = Formatter.nadjiFormatFajla(putanjaDoPrvogSloja);
			switch(format)
			{
			case GRESKA:
				throw new GNePostojiFajl();
			case xml:
				ucitajSacuvaniProjekat(putanjaDoPrvogSloja);
			default:
				dodajSloj(putanjaDoPrvogSloja);
			}
		} catch (GNePostojiFajl e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Ucitava sacuvani projekat iz xml fajla
	 * @throws GNePostojiFajl 
	 */
	public void ucitajSacuvaniProjekat(String putanja)
	{
		Formatter formater = new XMLFormatter(this);
		try {
			formater.ucitaj(putanja);
		} catch (GNePostojiFajl e) {
			//DODAJ DIJALOG ZA GRESKU
			e.printStackTrace();
		}
		
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
			case pam:
				formater = new PAMFormatter();
				break;
			case xml:
				throw new GNePostojiFajl();
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
					
					slikaCanvas.paintRectangleOnImage(rectangle);
					
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
				   DodajPravouganikeDijalog dijalogPravougaonici = new DodajPravouganikeDijalog(Slika.this);
				   
				   //Proveri da li ima aktivnih selekcija
				   boolean imaAktivnih = false;
				   for(Selection s : Slika.this.selekcije.values())
				   {
					   if(s.getActive())
					   {
						   imaAktivnih = true;
						   break;
					   }
				   }
				   Slika.this.imaAktivnihSelekcija = imaAktivnih;
				   
				   slikaCanvas.azuriraj();
				   rectangle = null;
				   recVector.clear();
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
	
	public void promeniAktivnostSelekcije(String imeSelekcije, boolean aktivnost)
	{
		Selection selekcija = selekcije.get(imeSelekcije);
		
		if(aktivnost == true)
			{
				selekcija.setToActive();
				this.imaAktivnihSelekcija = true;
			}
		else
			{
				selekcija.setToInactive();
				boolean imaAktivnih = false;
				for(Selection s : this.dohvMapuSelekcija().values())
				{
					if(s.getActive())
						{
							imaAktivnih = true;
							break;
						}
				}
				imaAktivnihSelekcija = imaAktivnih;
			}
		slikaCanvas.azuriraj();
	}
	
	public void izbrisiSelekciju(String imeSelekcije)
	{
		selekcije.remove(imeSelekcije);
		slikaCanvas.azuriraj();
	}
	
	/**
	 * Moze da sacuva sliku u BMP ili PAM formatu
	 * 	ili da sacuva projakat kao XML fajl
	 * @param putanja
	 */
	public void sacuvaj(String putanja)
	{
		Formatter formater = null;
		try {
			Formatter.MoguciFormati format = Formatter.nadjiFormatFajla(putanja);
			switch(format)
			{
			case GRESKA:
				throw new GNeodgovarajuciFormatFajla();
			case bmp:
				formater = new BMPFormatter();
				break;
			case pam:
				formater = new PAMFormatter();
				break;
			case xml:
				formater = new XMLFormatter(this);
				break;
			}
			if(formater != null)
			{
				formater.sacuvaj(putanja, this);
				this.eksportovana = true;
			}
		} catch (GNePostojiFajl | GNeodgovarajuciFormatFajla e) {
			e.printStackTrace();
		}
		
	}
	
	public void postaviImaAktivnihSelekcija(boolean value)
	{
		this.imaAktivnihSelekcija = value;
	}
	
	public void postaviMapuSelekcija(HashMap<String, Selection> mapa)
	{
		this.selekcije = mapa;
	}
	
	public void reset()
	{
		formatter = null;
		imaAktivnihSelekcija = false;
		eksportovana = false;
		slojevi = new Vector<Layer>();
		recVector = new Vector<Rectangle>();
		selekcije = new HashMap<String, Selection>();
		azurirajSve();
	}
	
	public void azurirajSve()
	{
		this.slikaCanvas.azuriraj();
		this.opcijeSelekcije.azuriraj();
		this.opcijeSlojevi.azuriraj();
	}
	
	public boolean dohvImaAktivnihSelekcija()
	{
		return this.imaAktivnihSelekcija;
	}
	
	public boolean dohvEksportovana()
	{
		return this.eksportovana;
	}

public static void main(String[] args)
{
	Slika slika = new Slika();
	slika.dodajSloj("E:\\FAKS\\4. semestar\\POOP\\Projekat C++\\Projekat1\\Projekat1\\Resources\\Examples\\Shapes.bmp");
}
}