package slika;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import Formatter.Formatter;
import Formatter.GNePostojiFajl;
import Formatter.XMLFormatter;
import greske.GNeodgovarajuciFormatFajla;

public class SacuvajProjekatDijalog extends Dialog {

	private TextField fieldPutanja;
	private Button dugmeOK;
	private Slika owner;
	
	public SacuvajProjekatDijalog(Frame owner) {
		super(owner, "Sacuvaj projekat", true);
		this.owner = (Slika)owner;
		setSize(485,100);
		this.add(new Label("Upisi putanju do .xml fajla:"), BorderLayout.NORTH);
		fieldPutanja = new TextField();
		this.add(fieldPutanja, BorderLayout.CENTER);
		dugmeOK = new Button("OK");
		dugmeOK.setSize(100, 30);
		this.add(dugmeOK, BorderLayout.SOUTH);
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
			
		});
		
		dugmeOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DijalogObrada obrada = new DijalogObrada(owner);
				try {
				String putanja = fieldPutanja.getText();
				Formatter.MoguciFormati format = Formatter.nadjiFormatFajla(putanja);
				if(format == Formatter.MoguciFormati.xml)
				{
					Formatter formater = new XMLFormatter(SacuvajProjekatDijalog.this.owner);
					formater.sacuvaj(putanja, (Slika)owner);
					SacuvajProjekatDijalog.this.owner.postaviEksportovana(true);
				}
				else
					throw new GNeodgovarajuciFormatFajla();
				}
				catch(GNeodgovarajuciFormatFajla | GNePostojiFajl e2)
				{
					//DODAJ DIJALOG ZA GRESKU!!!!!!!!!!!!!!!!!!!!!!!
					e2.printStackTrace();
				}
				finally
				{
					obrada.dispose();
					dispose();
				}
			}
			
		});
		
		setVisible(true);
	}

}
