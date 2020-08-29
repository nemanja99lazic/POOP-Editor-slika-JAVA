package prozori;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import operacija.BasicOperacija;
import operacija.CompositeOperacija;
import operacija.Operacija;
import slika.DijalogObrada;
import slika.Slika;
import java.util.LinkedList;

import Formatter.Formatter;
import Formatter.GNePostojiFajl;
import Formatter.XMLFormatter;
import Formatter.GNePostojiFajl.NePostojiFajlDialog;


public class OpcijeOperacije extends Dialog {

	private Slika owner;
	private CompositeOperacija operacija = new CompositeOperacija("placeholderputanja.fun", "placeholderime");
	private static String putanjaDoTempProjekta = "tempproj.xml";
	private static String putanjaDoTempFunkcije = "tempfun.fun";
	private static String args = "Projekat1.exe " + putanjaDoTempProjekta + " " + putanjaDoTempFunkcije;

	private Label labelaOsnovneOperacije;
	private Label labelaKonstanta;
	private Choice listaOperacija;
	private TextField fieldVrednost;
	private Button dugmeDodajOsnovnu;
	private Panel panelOsnovneOperacije;
	private Panel panelZaDugmeDodajOsnovnu;
	
	private Label labelaKompozitneOperacije;
	private TextField fieldPutanjaDoOperacije;
	private Button dugmeDodajKompozitnu;
	private Panel panelZaDugmeDodajKompozitnu;
	
	private Label labelaIzabraneOperacije;
	private List listaIzabranihOperacija;
	private Button dugmeUkloniIzListe;
	private Panel panelZaDugmeUkloni;
	
	private Label labelaSacuvajOperaciju;
	private TextField fieldPutanjaZaCuvanje;
	private TextField fieldImeOperacije;
	private Button dugmeSacuvaj;
	private Panel panelSacuvajOperaciju;
	
	private Button dugmeIzvrsiOperaciju;
	
	
	public OpcijeOperacije(Frame owner) {
		super(owner, "Operacije", false);
		this.owner = (Slika)owner;
		setSize(300, 470);
		zatvaranjeNaX();
		konfigurisi();
		dodajListenere();
		setVisible(false);
	}
	
	private void zatvaranjeNaX()
	{
		this.addWindowListener(new WindowAdapter()
			{

				@Override
				public void windowClosing(WindowEvent e) {
					dispose();
				}
				
			});
	}
	
	private void konfigurisi()
	{
		this.setLayout(new GridLayout(11, 1));
		
		labelaOsnovneOperacije = new Label("Osnovne operacije");
		labelaKonstanta = new Label("Konstanta");
		this.listaOperacija = new Choice();
		
		//Dodavanje svih osnovnih operacija
		for(BasicOperacija.BasicOperacije op : BasicOperacija.BasicOperacije.values())
			this.listaOperacija.add(op.toString());
			
		this.fieldVrednost = new TextField("0");
		
		this.panelOsnovneOperacije = new Panel(new GridLayout(2,2));
		panelOsnovneOperacije.add(labelaOsnovneOperacije);
		panelOsnovneOperacije.add(labelaKonstanta);
		panelOsnovneOperacije.add(listaOperacija);
		panelOsnovneOperacije.add(fieldVrednost);
		this.add(panelOsnovneOperacije);
		
		this.dugmeDodajOsnovnu = new Button("Dodaj operaciju");
		this.panelZaDugmeDodajOsnovnu = new Panel(new FlowLayout(FlowLayout.CENTER));
		panelZaDugmeDodajOsnovnu.add(dugmeDodajOsnovnu);
		this.add(panelZaDugmeDodajOsnovnu);
		
		labelaKompozitneOperacije = new Label("Ucitaj operaciju iz .fun fajla");
		this.add(labelaKompozitneOperacije);
		fieldPutanjaDoOperacije = new TextField();
		this.add(fieldPutanjaDoOperacije);
		dugmeDodajKompozitnu = new Button("Ucitaj operaciju");
		panelZaDugmeDodajKompozitnu = new Panel(new FlowLayout(FlowLayout.CENTER));
		panelZaDugmeDodajKompozitnu.add(dugmeDodajKompozitnu);
		this.add(panelZaDugmeDodajKompozitnu);
		
		labelaIzabraneOperacije = new Label("Izabrane operacije:");
		this.add(labelaIzabraneOperacije);
		listaIzabranihOperacija = new List();
		this.add(listaIzabranihOperacija);
		dugmeUkloniIzListe = new Button("Ukloni");
		panelZaDugmeUkloni = new Panel(new FlowLayout(FlowLayout.CENTER));
		panelZaDugmeUkloni.add(dugmeUkloniIzListe);
		this.add(panelZaDugmeUkloni);
		
		labelaSacuvajOperaciju = new Label("Sacuvaj operaciju u .fun formatu");
		this.add(labelaSacuvajOperaciju);
		panelSacuvajOperaciju = new Panel(new GridLayout(1, 3));
		fieldPutanjaZaCuvanje = new TextField("unesi_putanju.fun");
		fieldImeOperacije = new TextField("ime_operacije");
		dugmeSacuvaj = new Button("Sacuvaj");
		panelSacuvajOperaciju.add(fieldPutanjaZaCuvanje);
		panelSacuvajOperaciju.add(fieldImeOperacije);
		panelSacuvajOperaciju.add(dugmeSacuvaj);
		this.add(panelSacuvajOperaciju);
		
		dugmeIzvrsiOperaciju = new Button("Izvrsi operaciju");
		this.add(dugmeIzvrsiOperaciju, BorderLayout.SOUTH);
	}
	
	private void dodajListenere()
	{
		dugmeDodajOsnovnu.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) {
				BasicOperacija.BasicOperacije basic = BasicOperacija.BasicOperacije.valueOf(listaOperacija.getSelectedItem());
				for(BasicOperacija.BasicOperacijeBezKonstante bezKonstante : BasicOperacija.BasicOperacijeBezKonstante.values())
					if(bezKonstante.toString().equals(basic.toString()))
					{
						listaIzabranihOperacija.add(listaOperacija.getSelectedItem() + "(" + ")");
						operacija.add(new BasicOperacija(basic, -1));
						return;
					}
				listaIzabranihOperacija.add(listaOperacija.getSelectedItem() + "(" + fieldVrednost.getText() + ")");
				operacija.add(new BasicOperacija(basic, Integer.parseInt(fieldVrednost.getText())));
			}
			
			
		});
		
		dugmeDodajKompozitnu.addActionListener(new ActionListener() 
		{

			public void actionPerformed(ActionEvent e) {
				String putanja = fieldPutanjaDoOperacije.getText();
				String imeOperacije;
				try{
					if(CompositeOperacija.postojiFajlSaOperacijom(putanja))
					{
						imeOperacije = CompositeOperacija.odrediImeOperacije(putanja);
						listaIzabranihOperacija.add("KOMPOZITNA - " + imeOperacije);
						operacija.add(new CompositeOperacija(putanja, imeOperacije));
					}
					else
						throw new GNePostojiFajl();
				}
				catch(GNePostojiFajl greska)
				{	
					greska.new NePostojiFajlDialog(Slika.getInstance());
				}
			
			}
		});
		
		dugmeUkloniIzListe.addActionListener(new ActionListener() 
		{

			public void actionPerformed(ActionEvent e) {
				int indeks = listaIzabranihOperacija.getSelectedIndex();
				operacija.remove(indeks);
				listaIzabranihOperacija.remove(indeks);
			}
			
		});
		
		dugmeSacuvaj.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				DijalogObrada obrada = new DijalogObrada(OpcijeOperacije.this.owner);
				String putanjaZaCuvanje = fieldPutanjaZaCuvanje.getText();
				String staroImeOperacije = operacija.dohvIme();
				String novoImeOperacije = fieldImeOperacije.getText();
				operacija.postaviIme(novoImeOperacije);
				operacija.sacuvaj(putanjaZaCuvanje);
				operacija.postaviIme(staroImeOperacije);
				obrada.dispose();
			}
			
		});
		
		dugmeIzvrsiOperaciju.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) {
				DijalogObrada obrada = new DijalogObrada(owner);
				//glavni kod
				Formatter formater = new XMLFormatter(owner);
				formater.sacuvaj(putanjaDoTempProjekta, owner);
				operacija.sacuvaj(putanjaDoTempFunkcije);
				
				// poziv C++ programa
				Runtime runtime = Runtime.getRuntime();
				
					try {
						Process process = runtime.exec(args);
						
						process.waitFor();
						
						formater.ucitaj(putanjaDoTempProjekta);
					} catch (IOException | InterruptedException | GNePostojiFajl e1) {
						e1.printStackTrace();
					}
				
				// Brisanje temp fajlova
				/*File dir = new File("prog");
				String imecppprograma = "editor.exe";
				
				for(File file : dir.listFiles())
					if(!file.getName().equals(imecppprograma))
						file.delete();
				*/
				
				obrada.dispose();
			}
			
		});

	}
}
