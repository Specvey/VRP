import java.util.ArrayList;

import org.graphstream.graph.Graph;

public class Genetique {
	
	
	private int taillePopulation;
	private ArrayList<Individu> population;
	private Graph g;
	private int nombreClients;
	private int capaciteVehicule;
	private int nbrIndividusElites;
	private Individu solution;
	
	public Genetique(String nomFichier){}
	
	// partie Alex
	
	public void initNombreElites(ArrayList<Individu> tab){}
	
	public void initRangFitness(ArrayList<Individu> tab){}
	
	public void initRangDiversite(ArrayList<Individu> tab){}
	
	public void initLesIncoherences(ArrayList<Individu> tab){}
	
	public void initLesFitnessBiaisees(ArrayList<Individu> tab){}
	
	public void triFitnessBiaisee(ArrayList<Individu> tab){}
	
	public void initSolution(){}
	
	// partie Xavier
	/**
	 * fait se reproduir deux individus
	 * @param i1
	 * @param i2
	 * @return l'enfant des deux individus
	 */
	public Individu croisement(Individu i1, Individu i2){
		int coupure = (int)(Math.random()*(nombreClients+2));
		int[] nouveauxGenes = new int[i2.getNombreDeGenes()];
		for(int i=0;i<coupure;i++){
			nouveauxGenes[i] = i1.getGenes()[i];
		}
		for(int i=coupure;i<i2.getNombreDeGenes();i++){
			nouveauxGenes[i] = i2.getGenes()[i];
		}
		return new Individu(nouveauxGenes);
	}
	/**
	 * Fait se reproduir la population
	 * @return les enfant de la population
	 */
	public ArrayList<Individu> croisements(){
		ArrayList<Individu> enfants = new ArrayList<Individu>();
		for(int i=0;i<taillePopulation/4;i++){
			for(int j=i;j<taillePopulation;j++){
				enfants.add(croisement(population.get(i), population.get(j)));
			}
		}
		return enfants;
	}
	/**
	 * Fait muter les individus du paramètre
	 * @param individus
	 * @return individus après leur mutation
	 */
	public ArrayList<Individu> mutations(ArrayList<Individu> individus){
		ArrayList<Individu> mutants = new ArrayList<Individu>(individus);
		for(Individu i : mutants) i.mutation();
		return mutants;
	}
	/**
	 * Remplace la population par la génération suivante
	 */
	public void nouvelleGeneration(){
		ArrayList<Individu> enfants = mutations(croisements());
		ArrayList<Individu> individus = new ArrayList<Individu>(enfants);
		ArrayList<Individu> nouvelleGeneration = new ArrayList<Individu>();
		individus.addAll(population);
		initRangFitness(individus);
		initLesIncoherences(individus);
		// prendre 1/3
		int j=0;
		for(int i=0;i<taillePopulation/3;i++){
			if(individus.get(j).getIncoherences()==0){
				nouvelleGeneration.add(individus.get(j));
				individus.remove(j);
				i++;
			}else{
				j++;
			}
		}
		initNombreElites(individus);
		initRangDiversite(individus);
		//prendre 1/3
		for(int i=0;i<taillePopulation/3;i++){
			nouvelleGeneration.add(individus.get(i));
			individus.remove(i);
			i++;
		}
		initLesFitnessBiaisees(individus);
		triFitnessBiaisee(individus);
		//prendre 1/3
		for(int i=0;i<taillePopulation/3;i++){
			nouvelleGeneration.add(individus.get(i));
			individus.remove(i);
			i++;
		}
		//remplir le vide par des iniv aléa
		for(int i=nouvelleGeneration.size();i<taillePopulation;i++){
			nouvelleGeneration.add(new Individu(nombreClients));
		}
		population = nouvelleGeneration;
	}
	/**
	 * Fait évoluer la population génération par génération pour trouver un meilleur individu
	 */
	public void evolution(){
		boolean arret = false;
		Individu meilleurSolution = new Individu(nombreClients);
		int nombreDeGeneration = 1;
		while(!arret){
			if(nombreDeGeneration%20==0){
				if(meilleurSolution.getScoreFitness()-population.get(0).getScoreFitness()>10){//10 est l'amélioration pour la quel on peut dire que la progretion diminue
					//intégré de nouveaux individus
					ArrayList<Individu> nouvellePopulation = new ArrayList<Individu>();
					nouvellePopulation.add(meilleurSolution);
					for(int i=1;i<taillePopulation/10;i++){
						nouvellePopulation.add(population.get(i));
					}
					for(int i=taillePopulation/10;i<taillePopulation-1;i++){
						nouvellePopulation.add(new Individu(nombreClients));
					}
				}
			}else{
				nouvelleGeneration();
				if(meilleurSolution.getScoreFitness()-population.get(0).getScoreFitness()>0){
					meilleurSolution = population.get(0);
				}
			}
			nombreDeGeneration++;
			if(nombreDeGeneration==200){
				arret = true;
				population.set(0, meilleurSolution);
			}
		}
	}

}
