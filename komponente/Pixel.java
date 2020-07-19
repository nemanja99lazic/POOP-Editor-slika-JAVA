package komponente;

public class Pixel {
	private int r, g, b, a; // svaka komponenta je DVOBAJTNA
	
	public Pixel()
	{
		r = 255;
		g = 255;
		b = 255;
		a = 0;
	}
	
	public Pixel(int r, int g, int b, int a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}
	
	
}
