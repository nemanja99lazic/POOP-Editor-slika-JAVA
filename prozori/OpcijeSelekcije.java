package prozori;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;

import slika.Slika;
import java.awt.List;
import java.awt.Label;
import java.awt.Choice;
import java.awt.Panel;
import java.util.function.BiConsumer;

public class OpcijeSelekcije extends Dialog {

	private Slika owner;
	private CheckboxGroup radioButtonGrupa;
	private Checkbox radioPojedinacniPrikaz;
	private List listaSelekcija;
	private Checkbox radioPrikazSvihSelekcija;
	private Label labelaAktivnost;
	private Choice padajucaListaAktivna;
	private Button dugmePromeniAktivnost;
	private Label labelaBrisanje;
	private Choice padajucaListaBrisanje;
	private Button dugmeIzbrisi;
	private Panel panelAktivnost;
	private Panel panelBrisanje;
	
	public OpcijeSelekcije(Frame owner) {
		super(owner, "Selekcije", false);
		this.owner = (Slika)owner;
		setSize(218, 307);
		zatvaranjeNaX();
		konfigurisi();
		dodajListenere();
		setVisible(true);
	}
	
	private void zatvaranjeNaX()
	{
		owner.dohvCanvas().azuriraj();
		setVisible(false);
	}
	
	private void konfigurisi()
	{
		radioButtonGrupa = new CheckboxGroup();
		radioPojedinacniPrikaz = new Checkbox("Prikaz pojedinacnih aktivnih selekcija:", radioButtonGrupa, true);
		listaSelekcija = new List();
		radioPrikazSvihSelekcija = new Checkbox("Prikaz svih aktivnih selekcija", radioButtonGrupa, false);
		labelaAktivnost = new Label("Aktiviraj/deaktiviraj selekciju");
		
		padajucaListaAktivna = new Choice();
		dugmePromeniAktivnost = new Button("Promeni");
		panelAktivnost = new Panel(new GridLayout(1, 2));
		panelAktivnost.add(padajucaListaAktivna);
		panelAktivnost.add(dugmePromeniAktivnost);
		
		labelaBrisanje = new Label("Brisanje");
		
		padajucaListaBrisanje = new Choice();
		dugmeIzbrisi = new Button("Izbrisi");
		panelBrisanje = new Panel(new GridLayout(1, 2));
		panelBrisanje.add(padajucaListaBrisanje);
		panelBrisanje.add(dugmeIzbrisi);
		
		//Dodavanje na prozor
		this.setLayout(new GridLayout(7, 1));
		this.add(radioPojedinacniPrikaz);
		this.add(listaSelekcija);
		this.add(radioPrikazSvihSelekcija);
		this.add(labelaAktivnost);
		this.add(panelAktivnost);
		this.add(labelaBrisanje);
		this.add(panelBrisanje);
	}
	
	private void dodajListenere()
	{
		if(radioPojedinacniPrikaz.getState())
		{
			
		}
	}
	
	/*
	 * Kad prozor postane vidljiv, azurirati i glavnu sliku i na njoj prikazati izabrane selekcije
	 * - poziva odgovarajuce metode iz klase MestoZaSLiku
	 */
	public void azuriraj()
	{
		if(this.isVisible())
		{
			listaSelekcija.removeAll();
			padajucaListaAktivna.removeAll();
			padajucaListaBrisanje.removeAll();
			for(String ime : owner.dohvMapuSelekcija().keySet())
			{
				//Dodaj u listu Selekcija za prikazivanje (1. opcija)
				if(owner.dohvMapuSelekcija().get(ime).getActive())
				{
					listaSelekcija.add(ime);
				}
				
				//Dodaj u padajucu listu za promenu aktivnosti (2. opcija)
				String dodatak = owner.dohvMapuSelekcija().get(ime).getActive() ? "AKTIVNA" : "NEAKTIVNA";
				padajucaListaAktivna.add(ime + " - " + dodatak);
				
				//Dodaj u padajucu listu za brisanje (3. opcija)
				padajucaListaBrisanje.add(ime);
			}
		}
	}

}
