package JeuCartes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class JeuCartes {
	
	Queue<Carte> jeu;

	public JeuCartes() {
		jeu = new LinkedList<Carte>();

		jeu.add(new Carte("As","Pique"));
		jeu.add(new Carte("2","Pique"));
		jeu.add(new Carte("3","Pique"));
		jeu.add(new Carte("4","Pique"));
		jeu.add(new Carte("5","Pique"));
		jeu.add(new Carte("6","Pique"));
		jeu.add(new Carte("7","Pique"));
		jeu.add(new Carte("8","Pique"));
		jeu.add(new Carte("9","Pique"));
		jeu.add(new Carte("10","Pique"));
		jeu.add(new Carte("Valet","Pique"));
		jeu.add(new Carte("Dame","Pique"));
		jeu.add(new Carte("Roi","Pique"));
		
		jeu.add(new Carte("As","Coeur"));
		jeu.add(new Carte("2","Coeur"));
		jeu.add(new Carte("3","Coeur"));
		jeu.add(new Carte("4","Coeur"));
		jeu.add(new Carte("5","Coeur"));
		jeu.add(new Carte("6","Coeur"));
		jeu.add(new Carte("7","Coeur"));
		jeu.add(new Carte("8","Coeur"));
		jeu.add(new Carte("9","Coeur"));
		jeu.add(new Carte("10","Coeur"));
		jeu.add(new Carte("Valet","Coeur"));
		jeu.add(new Carte("Dame","Coeur"));
		jeu.add(new Carte("Roi","Coeur"));
		
		jeu.add(new Carte("As","Carreau"));
		jeu.add(new Carte("2","Carreau"));
		jeu.add(new Carte("3","Carreau"));
		jeu.add(new Carte("4","Carreau"));
		jeu.add(new Carte("5","Carreau"));
		jeu.add(new Carte("6","Carreau"));
		jeu.add(new Carte("7","Carreau"));
		jeu.add(new Carte("8","Carreau"));
		jeu.add(new Carte("9","Carreau"));
		jeu.add(new Carte("10","Carreau"));
		jeu.add(new Carte("Valet","Carreau"));
		jeu.add(new Carte("Dame","Carreau"));
		jeu.add(new Carte("Roi","Carreau"));
		
		jeu.add(new Carte("As","Trefle"));
		jeu.add(new Carte("2","Trefle"));
		jeu.add(new Carte("3","Trefle"));
		jeu.add(new Carte("4","Trefle"));
		jeu.add(new Carte("5","Trefle"));
		jeu.add(new Carte("6","Trefle"));
		jeu.add(new Carte("7","Trefle"));
		jeu.add(new Carte("8","Trefle"));
		jeu.add(new Carte("9","Trefle"));
		jeu.add(new Carte("10","Trefle"));
		jeu.add(new Carte("Valet","Trefle"));
		jeu.add(new Carte("Dame","Trefle"));
		jeu.add(new Carte("Roi","Trefle"));
			
		Collections.shuffle((List<?>) jeu);
	}
	
	public Carte nvlleCarte() {
		
		Carte carte = jeu.poll();
		return carte;
	}
	
	public int taille(){
		
		return jeu.size();
	}
	
	public void ajoutCarte(Carte carte) {
		
		jeu.add(carte);
		return;
	}
	
}
