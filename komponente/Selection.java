package komponente;

import java.util.Vector;
import java.awt.Rectangle;

public class Selection {
	private Vector<Rectangle> rec_vector;
	private boolean active;
	
	public Selection(Vector<Rectangle> rec_vector, boolean active)
	{
		this.rec_vector = rec_vector;
		this.active = active;
	}
	
	public void setToActive()
	{
		active = true;
	}
	
	public boolean getActive()
	{
		return active;
	}
	
	public void setToInactive()
	{
		active = false;
	}
	
	public void deleteSelection()
	{
		rec_vector.clear();
		active = false;
	}
	
	public Vector<Rectangle> getRectangleVector()
	{
		return rec_vector;
	}
	
	public void fillWithColor(int red, int green, int blue, Vector<Layer> layers)
	{
		for(Rectangle rec : rec_vector) 
			for(Layer l : layers)
				l.obojPikseleUnutarPravougaonika(red, green, blue, rec);
	}
}
