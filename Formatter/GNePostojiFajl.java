package Formatter;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.Exception;

public class GNePostojiFajl extends Exception{

	public class NePostojiFajlDialog extends Dialog
	{
		public Label labela = new Label(GNePostojiFajl.this.toString());
		public Button dugme = new Button("OK");
		public Panel panel = new Panel(new FlowLayout());
		
		private void createDialog()
		{
			this.setLayout(new GridLayout(2, 1));
			labela.setAlignment(Label.CENTER);
			this.add(labela);
			this.add(panel);
			panel.add(dugme);
			
			this.addWindowListener(new WindowAdapter()
			{

				@Override
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
			
			dugme.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			this.setLocationRelativeTo(null);
			setSize(330, 100);
			setVisible(true);
			this.toFront();

		}
		
		public NePostojiFajlDialog(Frame owner) {
			super(owner, "GRESKA!", true);
			createDialog();
		}
		
		public NePostojiFajlDialog(Dialog owner) {
			super(owner, "GRESKA!", true);
			createDialog();
		}
	}
	
	public GNePostojiFajl()
	{
		
	}
	
	public String toString() {
		return "Putanja do fajla nije u redu ili format nije dobar";
	}
}
