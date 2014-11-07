package JeuCartes;

import java.io.Serializable;

public class Carte implements Serializable {
	
	private static final long serialVersionUID = 5809893947672274916L;
	
	public String figure;
	public String couleur;
	
	public Carte(String figure, String couleur){
		this.figure = figure;
		this.couleur = couleur;
	}
	
	public void affiche() {
		System.out.println("Carte: " + figure + " de " + couleur);
	}
        
        @Override
        public String toString() {
            return figure + " de " + couleur;
        }

}
