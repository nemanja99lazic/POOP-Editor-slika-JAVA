package operacija;

import java.util.LinkedList;

public abstract class Operacija {

	protected int cnst = -1;
	protected String name = "";
	protected String path = "";
	protected LinkedList<Operacija> listaOperacija;
}
