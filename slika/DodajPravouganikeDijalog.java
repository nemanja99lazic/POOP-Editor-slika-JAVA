package slika;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Vector;

import komponente.Selection;

public class DodajPravouganikeDijalog extends Dialog{
	
	Vector<Rectangle> recVector;
	HashMap<String, Selection> selekcije;
	
	CheckboxGroup radioButtonGrupa = new CheckboxGroup();
	Checkbox radioDodajSelekciju;
	Checkbox radioDodajUPostojecu;
	TextField imeNoveSelekcije;
	Choice padajucaListaSelekcije;
	Panel panel;
	Button dugmeOK;
	
	public DodajPravouganikeDijalog(Frame owner) {
		super(owner, "Dodavanje pravougaonika u selekciju", true);
		setSize(400,139);
		
		recVector = ((Slika)owner).dohvVektorPravougaonika();
		selekcije = ((Slika)owner).dohvMapuSelekcija();
		
		radioDodajSelekciju = new Checkbox("Dodaj novu selekciju", radioButtonGrupa, true);
		radioDodajUPostojecu = new Checkbox("Dodaj u postojecu selekciju", radioButtonGrupa, false);
		imeNoveSelekcije = new TextField("Upisi ime selekcije");
		padajucaListaSelekcije = new Choice();
		
		// Upisi imena selekcija u padajucu listu
		if(!selekcije.isEmpty())
		{
			for(String imeSelekcije : selekcije.keySet())
			{
				padajucaListaSelekcije.add(imeSelekcije);
			}
		}
		
		panel = new Panel(new GridLayout(2,2));
		panel.add(radioDodajSelekciju);
		panel.add(imeNoveSelekcije);
		panel.add(radioDodajUPostojecu);
		panel.add(padajucaListaSelekcije);
		this.add(panel, BorderLayout.CENTER);
		
		dugmeOK = new Button("OK");
		this.add(dugmeOK, BorderLayout.SOUTH);
		
		this.addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
			
		});
		
		// Listener za pritisnuto dugme
		dugmeOK.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) {
				if(radioDodajSelekciju.getState() == true)
				{
					String imeSelekcije = imeNoveSelekcije.getText();
					if(!selekcije.containsKey(new String(imeSelekcije)))
					{
						selekcije.put(imeSelekcije, null);
					}
					
					if(!recVector.isEmpty())
					{
						Vector<Rectangle> recVectorCopy = (Vector<Rectangle>)recVector.clone();
						selekcije.put(imeSelekcije, new Selection(recVectorCopy, true));
					}
				}
				if(radioDodajUPostojecu.getState() == true)
				{
					String imeSelekcije = padajucaListaSelekcije.getSelectedItem();
					for(Rectangle rec : recVector)
						selekcije.get(imeSelekcije).getRectangleVector().add(rec);
					
				}
				dispose();
			}
		});
		setVisible(true);
	}

}