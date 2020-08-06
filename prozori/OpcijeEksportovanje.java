package prozori;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import Formatter.*;

import slika.Slika;

public class OpcijeEksportovanje extends Dialog {
	
	private Slika owner;
	private Label labela;
	private TextField fieldPutanja;
	private Button dugmeSacuvaj;
	private Choice listaFormata;
	
	private Panel panelCentar;
	private Panel panelZaButton;
	
	public OpcijeEksportovanje(Frame owner)
	{
		super(owner, "Eksportovanje", true);
		this.owner = (Slika)owner;
		
		zatvaranjeNaX();
		
		labela = new Label("Unesi putanju za cuvanje slike bez ekstenzije:");
		this.add(labela, BorderLayout.NORTH);
		
		fieldPutanja = new TextField();
		listaFormata = new Choice();
		for(Formatter.MoguciFormati format : Formatter.MoguciFormati.values())
		{
			if(!format.toString().equals(new String("GRESKA")))
				listaFormata.add(format.toString());
		}
		
		panelCentar = new Panel(new BorderLayout());
		panelCentar.add(fieldPutanja, BorderLayout.CENTER);
		panelCentar.add(listaFormata, BorderLayout.EAST);
		this.add(panelCentar, BorderLayout.CENTER);
		
		dugmeSacuvaj = new Button("Sacuvaj");
		panelZaButton = new Panel(new FlowLayout());
		panelZaButton.add(dugmeSacuvaj);
		this.add(panelZaButton, BorderLayout.SOUTH);
		
		dodajListener();
		
		setSize(320, 110);
		setLocationRelativeTo(null);
		setVisible(false);
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
	
	private void dodajListener()
	{
		dugmeSacuvaj.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String format = listaFormata.getSelectedItem();
				String putanja = fieldPutanja.getText() + "." + format;
				
				Formatter.MoguciFormati formatEnum = Formatter.MoguciFormati.valueOf(format);
				Formatter formater = null;
				switch(formatEnum)
				{
				case bmp:
					formater = new BMPFormatter();
					break;
				case pam:
					formater = new PAMFormatter();
					break;
				case xml:
					formater = new XMLFormatter(owner);
				}
				
				formater.sacuvaj(putanja, owner);
				owner.postaviEksportovana(true);
				
				dispose();
			}
			
		});
	}
}
