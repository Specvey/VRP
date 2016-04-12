import java.util.ArrayList;

import org.graphstream.algorithm.util.FibonacciHeap.Node;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;

public class Individu {
	
	private int[] genes;
	private int nombreDeGenes;
	private double scorFitness;
	private double scorDivercite;
	private double chanceDeMutation;
	private int forceDeMutation;
	
	/**
	 * 
	 * Constructeur permétant de crée un individu avec des gènes aléatoires
	 * 
	 * @param nombreDeClient
	 */
	public Individu(int nombreDeClient){
		nombreDeGenes = nombreDeClient+(int)(Math.random()*(nombreDeClient/4))+2;
		genes = new int[nombreDeGenes];
		ArrayList<Integer> genesDispo = new ArrayList<Integer>();
		int indice;
		for(int i=0;i<nombreDeClient;i++){
			genesDispo.add(i);
		}
		for(int i=nombreDeClient;i<nombreDeGenes-2;i++){
			genesDispo.add(0);
		}
		for(int i=1;i<nombreDeGenes-1;i++){
			indice = (int)(Math.random()*(genesDispo.size()+1));
			genes[i] = genesDispo.get(indice);
			genesDispo.remove(indice);
		}
		genes[0] = 0;
		genes[nombreDeGenes-1] = 0;
		chanceDeMutation = Math.random();
		forceDeMutation = (int)(Math.random()*(genes.length/4));
		scorFitness = 0;
		scorDivercite = 0;
	}
	
	/**
	 * 
	 * Constructeur permétant de crée un individu à partir d'un tableau de gènes
	 * 
	 * @param genes
	 */
	public Individu(int[] genes){
		this.genes = genes;
		nombreDeGenes = genes.length;
		chanceDeMutation = Math.random();
		forceDeMutation = (int)(Math.random()*(genes.length/4));
		scorFitness = 0;
		scorDivercite = 0;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int[] getGenes() {
		return genes;
	}

	/**
	 * 
	 * @param genes
	 */
	public void setGenes(int[] genes) {
		this.genes = genes;
	}

	/**
	 * 
	 * @return
	 */
	public int getNombreDeGenes() {
		return nombreDeGenes;
	}

	/**
	 * 
	 * @return
	 */
	public double getScorFitness() {
		return scorFitness;
	}

	/**
	 * 
	 * @return
	 */
	public double getScorDivercite() {
		return scorDivercite;
	}

	/**
	 * 
	 * initialise la fitness de l'individu
	 * 
	 * @param g
	 */
	public void initFitness(Graph g){
		for(int i=1;i<nombreDeGenes;i++){
			scorFitness +=(double)(g.getNode(""+genes[i-1]).getEdgeBetween(""+genes[i]).getAttribute("poids"));
		}
	}

	
	/**
	 * 
	 * effectue une optimisation locale de l'individu
	 * 
	 */
	public void optimisationLocale(Graph g){
		// on trouve et délimite la tournée à optimisé
		int noeud = (int)Math.random()*(nombreDeGenes);
		int tailleTournee = 1;
		while(genes[noeud] != 0){
			noeud--;
		}
		while(genes[noeud+tailleTournee] != 0){
			tailleTournee++;
		}
		// si il y a suffisament de noeuds dans la tournée
		if(tailleTournee > 4){
			int fitnessTournee=0,fitnessNouvelleTournee=0;
			// on crée la tournée
			int[] tournee = new int[tailleTournee];
			for(int i=0;i<tailleTournee;i++) tournee[i] = genes[noeud+i];
			// on calcule sa fitness
			for(int i=1;i<tailleTournee;i++){
				fitnessTournee += (double)(g.getNode(""+tournee[i-1]).getEdgeBetween(""+tournee[i]).getAttribute("poids"));
			}
			// on choisi les noeud à inverser
			int n1 = (int)Math.random()*(tailleTournee-1)+1;
			int n2=0;
			if(n1>tailleTournee/2){
				n2 =  (int)Math.random()*(tailleTournee/2-1)+1;
			}else{
				n2 =  (int)Math.random()*(tailleTournee/2-1)+tailleTournee/2+2;
			}
			// crée la nouvelle tournée
			int[] nouvelleTournee = new int[tailleTournee];
			for(int i=0;i<tailleTournee;i++){
				if(i==n1) nouvelleTournee[i] = tournee[n2];
				else if(i==n2) nouvelleTournee[i] = tournee[n1];
				else nouvelleTournee[i] =tournee[i];
			}
			// on calcule sa fitness
			for(int i=1;i<tailleTournee;i++){
				fitnessNouvelleTournee += (double)(g.getNode(""+nouvelleTournee[i-1]).getEdgeBetween(""+nouvelleTournee[i]).getAttribute("poid"));
			}
			//si la nouvelle tournée est mieux que l'ancienne la remplace dans les genes
			if(fitnessNouvelleTournee<fitnessTournee){
				for(int i=0;i<tailleTournee;i++) genes[noeud+i] = nouvelleTournee[i];
			}
		}
	}

}
