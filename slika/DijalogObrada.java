package slika;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DijalogObrada extends Dialog {

	private Panel panel;
	
	public DijalogObrada(Frame owner) {
		super(owner, false);
		this.setSize(200,80);
		
		panel = new Panel(new FlowLayout());
		panel.add(new Label("Obrada..."));
		this.add(panel, BorderLayout.CENTER);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.toFront();
		
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				dispose();
			}
			
		});
	}

}
