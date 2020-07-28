package main;

import slika.*;
/*
 * Napomene:
 * - promeniti setVisible u svim dijalozima sa opcijama visibility na false
 */


/*
 *	Dodaj listenere za OpcijeSelekcije
 */

public class Main {

	public static void main(String[] args) {
		Slika slika = new Slika();
		//slika.dodajSloj("E:\\FAKS\\4. semestar\\POOP\\Projekat C++\\Projekat1\\Projekat1\\Resources\\Examples\\Shapes.pam");
		/*slika.sacuvaj("C:\\Users\\ln180\\Desktop\\shapesproba3.pam");
		System.out.println("Zavrsio..");*/
		
		slika.ucitajSacuvaniProjekat("sacuvano.xml");
		
		slika.sacuvaj("nijesve.xml");
		System.out.println("Zavrsio..");
	}
}
