package slika;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PocetniDijalog extends Dialog {
	
	private Slika owner;
	private Label staTreba;
	private TextField putanjaTF;
	private String putanja;
	private Panel panel;
	
	public PocetniDijalog(Frame owner, String naslov) {
		super(owner, naslov, true);
		setSize(400,80);
		zatvaranjeNaX();
		konfigurisi();
		setVisible(true);
	}
	
	private void zatvaranjeNaX()
	{
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}
	
	private void konfigurisi()
	{
		staTreba = new Label("Unesi putanju do 1. sloja ili do sacuvanog projekta");
		putanjaTF = new TextField();
		putanjaTF.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					putanja = putanjaTF.getText();
					dispose();
				}
			}
		});
		panel = new Panel(new GridLayout(2, 1));
		panel.add(staTreba);
		panel.add(putanjaTF);
		this.add(panel);
	}
	
	public String dohvPutanju()
	{
		return putanja;
	}
	
/*public static void main(String[] args)
{
	new PocetniDijalog(new Slika(), "Dijaloggggg");
}*/
}
