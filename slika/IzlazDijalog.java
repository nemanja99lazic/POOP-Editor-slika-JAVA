package slika;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import prozori.OpcijeEksportovanje;

public class IzlazDijalog extends Dialog {

	private Slika owner;
	private OpcijeEksportovanje opcijeEksportovanje;
	
	private Label labelaIspis;
	private Button dugmeIzadji;
	private Button dugmeOtkazi;
	private Button dugmeSacuvaj;
	private Panel panelZaButtone;
	
	public IzlazDijalog(Frame owner, OpcijeEksportovanje opcijeEksportovanje) {
		super(owner, "Izlaz", true);
		this.owner = (Slika)owner;
		this.opcijeEksportovanje = opcijeEksportovanje;
		labelaIspis = new Label("Projekat nije sacuvan. Zelite li da nastavite?");
		this.add(labelaIspis, BorderLayout.CENTER);
		
		dugmeIzadji = new Button("Izadji");
		dugmeOtkazi = new Button("Otkazi");
		dugmeSacuvaj = new Button("Sacuvaj");
		
		panelZaButtone = new Panel(new FlowLayout());
		panelZaButtone.add(dugmeSacuvaj);
		panelZaButtone.add(dugmeIzadji);
		panelZaButtone.add(dugmeOtkazi);
		this.add(panelZaButtone, BorderLayout.SOUTH);
		
		dodajListenere();
		
		setSize(300, 110);
		setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void dodajListenere()
	{
		dugmeSacuvaj.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				opcijeEksportovanje.setVisible(true);
				dispose();
			}
			
		});
		
		dugmeIzadji.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) {
				owner.dispose();
			}
			
		});
		
		dugmeOtkazi.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
			
		});
	}
}
