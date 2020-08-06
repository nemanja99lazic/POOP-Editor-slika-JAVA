package slika;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;

public class DijalogObrada extends Dialog {

	public DijalogObrada(Frame owner) {
		super(owner, false);
		this.setSize(200,200);
		this.add(new Label("Obrada..."), BorderLayout.CENTER);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.toFront();
	}

}
