package prozori;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.List;

import komponente.Layer;
import slika.DijalogObrada;
import slika.Slika;

public class OpcijeSlojevi extends Dialog{
	
	private Slika owner;
	private List listaAktivan;
	private Button dugmePromeniAktivan;
	private Panel panelAktivan;
	//private ScrollPane scrollPane;
	private Vector<Checkbox> checkboxSlojevi;
	private Label labelaBrisanje;
	private Choice padajucaListaBrisanje;
	private Button dugmeIzbrisi;
	private Panel panelBrisanje;
	private Label labelaProzirnost;
	private Choice padajucaListaProzirnost;
	private TextField textfieldPromeniProzirnost;
	private Button dugmePromeniProzirnost;
	private Panel panelProzirnost;
	private Label labelaDodajSloj;
	private TextField textfieldPutanja;
	private Button dugmeDodajSloj;
	
	public OpcijeSlojevi(Slika owner)
	{
		super(owner, "Slojevi", false);
		this.owner = owner;
		setSize(218, 307);
		zatvaranjeNaX();
		konfigurisi();
		setVisible(false);
	}
	
	/**
	 * Postavice pocetnu konfiguraciju dijaloga pre dodavanja slojeva
	 * 
	 */
	private void konfigurisi()
	{
		
		listaAktivan = new List();
		dugmePromeniAktivan = new Button("Promeni aktivnost");
		panelAktivan = new Panel(new GridLayout(1, 2));
		panelAktivan.add(listaAktivan);
		panelAktivan.add(dugmePromeniAktivan);
		
		checkboxSlojevi = new Vector<Checkbox>();
		
		labelaBrisanje = new Label("Brisanje:");
		padajucaListaBrisanje = new Choice();
		dugmeIzbrisi = new Button("Izbrisi");
		panelBrisanje = new Panel(new GridLayout(1, 2));
		panelBrisanje.add(padajucaListaBrisanje);
		panelBrisanje.add(dugmeIzbrisi);
		
		labelaProzirnost = new Label("Neprozirnost:");
		padajucaListaProzirnost = new Choice();
		textfieldPromeniProzirnost = new TextField("");
		dugmePromeniProzirnost = new Button("Promeni");
		panelProzirnost = new Panel(new GridLayout(1, 3));
		panelProzirnost.add(padajucaListaProzirnost);
		panelProzirnost.add(textfieldPromeniProzirnost);
		panelProzirnost.add(dugmePromeniProzirnost);
		
		labelaDodajSloj = new Label("Dodaj sloj:");
		textfieldPutanja = new TextField("Putanja do sloja");
		dugmeDodajSloj = new Button("Dodaj");
		
		//Dodavanje komponenti na prozor:
		this.setLayout(new GridLayout(8, 1));
		this.add(panelAktivan);
		this.add(labelaBrisanje);
		this.add(panelBrisanje);
		this.add(labelaProzirnost);
		this.add(panelProzirnost);
		this.add(labelaDodajSloj);
		this.add(textfieldPutanja);
		this.add(dugmeDodajSloj);
		
		//Dodavanje listenera za button PromeniAktivan
		dugmePromeniAktivan.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DijalogObrada obrada = new DijalogObrada(owner);
				int ind = listaAktivan.getSelectedIndex();
				boolean staraAktivnost = owner.dohvSlojeve().get(ind).isAktivan();
				String dodatak = !staraAktivnost == true ? "AKTIVAN" : "NEAKTIVAN";
				owner.promeniAktivnostSloju(ind, !staraAktivnost);
				listaAktivan.remove(ind);
				listaAktivan.add("Layer" + (ind + 1) + " - " + dodatak, ind);
				obrada.dispose();
			}
			
		});
		
		//Dodavanje listenera za button Izbrisi
		dugmeIzbrisi.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				DijalogObrada obrada = new DijalogObrada(owner);
				int ind = padajucaListaBrisanje.getSelectedIndex();
				owner.izbrisiSloj(ind);
				azuriraj();
				obrada.dispose();
			}
		});
		
		//Dodavanje listenera za button Promeni:
		dugmePromeniProzirnost.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				DijalogObrada obrada = new DijalogObrada(owner);
				int novaProzirnost = Integer.parseInt(textfieldPromeniProzirnost.getText());
				int ind = padajucaListaProzirnost.getSelectedIndex();
				owner.promeniProzirnostSloju(ind, novaProzirnost);
				//Nema potrebe za azuriranjem ovog prozora, jer se nista nece promeniti na grafici
				obrada.dispose();
			}
			
		});
		
		//Dodavanje listenera za button DodajSloj:
		dugmeDodajSloj.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DijalogObrada obrada = new DijalogObrada(owner);
				String putanja = textfieldPutanja.getText();
				if(!putanja.equals(new String("Putanja do sloja")))
					owner.dodajSloj(putanja);
				// ne mora da se azurira, jer se vec azuriralo u objektu slike
				obrada.dispose();
			}
			
		});
	}
	
	/**
	 * Metoda koju ce pozvati objekat slike da odradi potrebna azuriranja u prozoru sa opcijama
	 */
	public void azuriraj()
	{
		
		//Azuriranje vektora checkboxova
		Vector<Layer> slojevi = owner.dohvSlojeve();
		checkboxSlojevi.removeAllElements();
		for(int i = 0; i<slojevi.size(); i++)
			{
				checkboxSlojevi.add(new Checkbox("Layer" + (i + 1), slojevi.get(i).isAktivan()));
				Checkbox dodati = checkboxSlojevi.get(i);
				int redbr = i; // Mora ovo da se koristi, jer i nece biti vidljivo u bezimenoj klasi
				dodati.addItemListener(new ItemListener()
						{
							public void itemStateChanged(ItemEvent e)
							{
								boolean novoStanje = dodati.getState();
								owner.setAktivanSloj(redbr, novoStanje);
							}
						});
			}
		
		//Azuriranje liste slojeva sa aktivnoscu
		
		listaAktivan.removeAll();
		for(int i = 0; i<owner.dohvSlojeve().size(); i++)
		{
			String dodatak = owner.dohvSlojeve().get(i).isAktivan() ? "AKTIVAN" : "NEAKTIVAN";
			listaAktivan.add("Layer" + (i + 1) + " - " + dodatak);
		}
		
		//Azuriraj Brisanje:
		padajucaListaBrisanje.removeAll();
		for(Checkbox box : checkboxSlojevi)
			padajucaListaBrisanje.add(box.getLabel());
		
		//Azuriraj Prozirnost:
		padajucaListaProzirnost.removeAll();
		for(Checkbox box : checkboxSlojevi)
			padajucaListaProzirnost.add(box.getLabel());
	}
	
	private void zatvaranjeNaX()
	{
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
	}

}
