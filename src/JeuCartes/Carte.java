package JeuCartes;

import java.io.Serializable;

public class Carte implements Serializable {
	public String figure;
	public String couleur;
	
	public Carte(String figure, String couleur){
		this.figure = figure;
		this.couleur = couleur;
	}
	
	public void affiche() {
		System.out.println("Carte: " + figure + " de " + couleur);
	}

}
