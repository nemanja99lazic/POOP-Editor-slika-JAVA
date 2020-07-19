package Formatter;

import java.lang.Exception;

public class GNePostojiFajl extends Exception {

	public String toString() {
		return "Putanja do fajla nije u redu ili format nije dobar";
	}
}
