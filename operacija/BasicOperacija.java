package operacija;

public class BasicOperacija extends Operacija {
	
	public enum BasicOperacije
	{
		Addition, 
		Subtraction, 
		InverseSubtraction,
		Multiplication,
		Division,
		InverseDivision,
		Power,
		Logarithm,
		Abs,
		Min,
		Max,
		Invert,
		Grayscale,
		BlackAndWhite,
		Median
	}
	
	public enum BasicOperacijeBezKonstante
	{
		Abs,
		Invert,
		Grayscale,
		BlackAndWhite,
		Median
	}
	
	public BasicOperacija(BasicOperacije ime, int cnst)
	{
		this.name = ime.toString();
		this.cnst = cnst;
		this.listaOperacija = null;
	}
	
}
