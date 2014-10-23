package JeuCartes;

public class mainCartes {
	
	public static void main(String[] args){
		
		JeuCartes jc = new JeuCartes();
		
		while ( jc.taille() != 0 ){
			jc.nvlleCarte().affiche();
		}
	}

}
