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
	 * Constructeur perm�tant de cr�e un individu avec des g�nes al�atoires
	 * 
	 * @param nombreDeClient
	 */
	public Individu(int nombreDeClient){
		nombreDeGenes = nombreDeClient+(int)(Math.random()*(nombreDeClient/4)+2);
		genes = new int[nombreDeGenes];
		for(int i=0;i<nombreDeGenes;i++){
			genes[i] = (int)(Math.random()*(nombreDeClient));
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
	 * Constructeur perm�tant de cr�e un individu � partir d'un tableau de g�nes
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
			scorFitness += Double.parseDouble(g.getNode(""+genes[i-1]).getEdgeBetween(""+genes[i]).getAttribute("poid").toString());
		}
	}

	/**
	 * 
	 * @param population
	 * @return
	 */
	public int rangFitness(Individu[] population){
		int rang = 0;
		for(int i=0;i<population.length;i++) if(population[i].getScorFitness()<scorFitness) rang++;
		return rang;
	}
	
	
	/**
	 * 
	 * effectue une optimisation locale de l'individu
	 * 
	 */
	public void optimisationLocale(Graph g){
		// on trouve et d�limite la tourn�e � optimis�
		int noeud = (int)Math.random()*(nombreDeGenes-2);
		int tailleTournee = 1;
		while(genes[noeud] != 0){
			noeud--;
		}
		while(genes[noeud+tailleTournee] != 0){
			tailleTournee++;
		}
		// si il y a suffisament de noeuds dans la tourn�e
		if(tailleTournee > 4){
			int fitnessTournee=0,fitnessNouvelleTournee=0;
			// on cr�e la tourn�e
			int[] tournee = new int[tailleTournee];
			for(int i=0;i<tailleTournee;i++) tournee[i] = genes[noeud+i];
			// on calcule sa fitness
			for(int i=1;i<tailleTournee;i++){
				fitnessTournee += Double.parseDouble(g.getNode(""+tournee[i-1]).getEdgeBetween(""+tournee[i]).getAttribute("poid").toString());
			}
			// on choisi les noeud � inverser
			int n1 = (int)Math.random()*(tailleTournee-3)+1;
			int n2=0;
			if(n1>tailleTournee/2){
				n2 =  (int)Math.random()*(tailleTournee/2-1)+1;
			}else{
				n2 =  (int)Math.random()*(tailleTournee/2-2)+tailleTournee/2;
			}
			// cr�e la nouvelle tourn�e
			int[] nouvelleTournee = new int[tailleTournee];
			for(int i=0;i<tailleTournee;i++){
				if(i==n1) nouvelleTournee[i] = tournee[n2];
				else if(i==n2) nouvelleTournee[i] = tournee[n1];
				else nouvelleTournee[i] =tournee[i];
			}
			// on calcule sa fitness
			for(int i=1;i<tailleTournee;i++){
				fitnessNouvelleTournee += Double.parseDouble(g.getNode(""+nouvelleTournee[i-1]).getEdgeBetween(""+nouvelleTournee[i]).getAttribute("poid").toString());
			}
			//si la nouvelle tourn�e est mieux que l'ancienne la remplace dans les genes
			if(fitnessNouvelleTournee<fitnessTournee){
				for(int i=0;i<tailleTournee;i++) genes[noeud+i] = nouvelleTournee[i];
			}
		}
	}

}
