import java.util.ArrayList;

import org.graphstream.graph.Graph;

/**
 * La classe Individu représente une entité dans la population de l'algorithme génétique.
 * 
 * @author Xavier MAUGY & Alexandre MANCHON
 * @version 31/05/2016
 */
public class Individu
{
	/**
	 * Taille du tableau genes
	 * @see Individu#genes
	 */
	private int nombreDeGenes;
	
	/**
	 * Tableau contenant tous les genes de cet individu : 0 pour le dépôt et les autres nombres pour le numéro de client.
	 * Ce tableau commence forcément par 0 et finit forcément par 0 car l'ensemble des tournées commencent obligatoirement par le dépôt et finit par celui-ci.
	 */
	private int genes[];
	
	/**
	 * Performance d'un individu correspondant au temps pour parcourir toutes ses tournées.
	 * Ainsi, plus le score fitness est faible, plus l'individu est performant.
	 * @see Individu#initFitness(Graph)
	 */
	private double scoreFitness;
	
	/**
	 * Le rang relatif en terme de fitness de cet individu.
	 */
	private int rangFitness;
	
	/**
	 * Diversite d'un individu c'est-à-dire s'il est différent des autres.
	 * Ainsi, plus le score diversité est faible, plus l'individu est diversifié.
	 * @see Individu#initDiversite(Individu[])
	 */
	private double scoreDiversite;
	
	/**
	 * Le rang relatif en terme de contribution à la diversité de cet individu.
	 */
	private int rangDiversite;
	
	/**
	 * L'évaluation d'un individu est un compromis entre son rang relatif en terme de fitness et son rang relatif en terme de contribution à la diversité.
	 * La fitness biaisée est calculée avec une formule mathématiques créée par Thibaut VIDAL.
	 * @see Individu#initFitnessBiaisee(int, int)
	 */
	private double fitnessBiaisee;
	
	/**
	 * incoherences est le nombre d'incohérence de cet individu.
	 * Une incohérence est le non-respect d'une contrainte.
	 * @see Individu#initIncoherences(Graph, int)
	 */
	private int incoherences;
	
	/**
	 * Constructeur permettant de créer un individu avec des gènes aléatoires
	 * @param nombreDeClient
	 */
	public Individu(int nombreDeClient)
	{
		ArrayList<Integer> genesDispo = new ArrayList<Integer>();
		for(int i=1;i<=nombreDeClient;i++)
		{
			genesDispo.add(i);
		}
		int indiceGenes = 0;
		int indiceGenesDispo;
		ArrayList<Integer> genesList = new ArrayList<Integer>();
		genesList.add(0);
		double mettreDepot;
		while(!genesDispo.isEmpty())
		{
			mettreDepot = Math.random();
			// Si il y a un dépôt précedemment
			if(genesList.get(indiceGenes) == 0)
			{
				indiceGenesDispo = (int)(Math.random()*(genesDispo.size()));
				genesList.add(genesDispo.get(indiceGenesDispo));
				genesDispo.remove(indiceGenesDispo);
			}
			else
			{
				if(mettreDepot<0.25)
				{
					genesList.add(0);
				}
				else
				{
					indiceGenesDispo = (int)(Math.random()*(genesDispo.size()));
					genesList.add(genesDispo.get(indiceGenesDispo));
					genesDispo.remove(indiceGenesDispo);
				}
			}
			indiceGenes++;
		}
		genesList.add(0);
		this.nombreDeGenes = genesList.size();
		genes = new int[nombreDeGenes];
		for(int i=0;i<nombreDeGenes;i++)
		{
			genes[i]=genesList.get(i);
		}
		this.scoreFitness = 0;
		this.scoreDiversite = 0;
		genesDispo.clear();
	}
	
	/**
	 * Constructeur permettant de créer un individu à partir d'un tableau de gènes
	 * @param genes
	 */
	public Individu(int[] genes)
	{
		this.genes = genes;
		this.nombreDeGenes = genes.length;
		this.scoreFitness = 0;
		this.scoreDiversite = 0;
	}
	
	/**
	 * @return nombreDeGenes le nombre de genes
	 * @see Individu#nombreDeGenes
	 */
	public int getNombreDeGenes()
	{
		return nombreDeGenes;
	}
	
	/**
	 * @return genes le tableau contenant les genes
	 * @see Individu#genes
	 */
	public int[] getGenes()
	{
		return genes;
	}

	/**
	 * @param genes nouveaux genes venant remplacer les anciens. On met à jour le nombre de genes.
	 * @see Individu#genes
	 */
	public void setGenes(int[] genes)
	{
		this.genes = genes;
		this.nombreDeGenes = genes.length;
	}
	
	
	/**
	 * @return scoreFitness le score de la fitness
	 * @see Individu#scoreFitness
	 */
	public double getScoreFitness()
	{
		return scoreFitness;
	}

	/**
	 * @return rangFitness le rang de la fitness
	 * @see Individu#rangFitness
	 */
	public int getRangFitness()
	{
		return rangFitness;
	}
	
	/**
	 * On remplace le rangFitness par un nouveau
	 * @see Individu#rangFitness
	 * @param rang
	 */
	public void setRangFitness(int rang)
	{
		this.rangFitness = rang;
	}
	
	/**
	 * @return scoreDiversite le score de la diversité
	 * @see Individu#scoreDiversite
	 */
	public double getScoreDiversite()
	{
		return scoreDiversite;
	}

	/**
	 * @return rangDiversite le rang de la diversité
	 * @see Individu#rangDiversite
	 */
	public int getRangDiversite()
	{
		return rangDiversite;
	}
	
	/**
	 * On remplace le rangDiversite par un nouveau
	 * @see Individu#rangDiversite
	 * @param rang
	 */
	public void setRangDiversite(int rang)
	{
		this.rangDiversite = rang;
	}
	
	/**
	 * @return fitnessBiaisee la fitness biaisée
	 * @see Individu#fitnessBiaisee
	 */
	public double getFitnessBiaisee()
	{
		return fitnessBiaisee;
	}
	
	/**
	 * @return incoherences un entier correspondant au nombre d'incohérences trouvées dans cet individu.
	 * @see Individu#incoherences
	 */
	public int getIncoherences()
	{
		return incoherences;
	}
	
	/**
	 * initialise la fitness de cet individu
	 * @param g
	 * @see Individu#scoreFitness
	 */
	public void initFitness(Graph g)
	{
		this.scoreFitness = 0;
		reparation(g.getNodeCount()-1);
		for(int i=1;i<nombreDeGenes;i++)
		{
			// On ajoute le poids entre ces deux noeuds
			scoreFitness +=(double)(g.getNode(""+genes[i-1]).getEdgeBetween(""+genes[i]).getAttribute("poids"));
		}
	}
	
	/**
	 * Réparation sur cet individu.
	 * Correction des incohérences si deux gènes consécutifs sont identiques :
	 * - Si deux dépôts : on remplace le second dépôt par un client aléatoire
	 * - Si deux clients : on remplace le second client par un dépôt
	 */
	public void reparation(int nombreClients)
	{
		for(int i=1;i<nombreDeGenes;i++)
		{
			// Si deux gènes consécutifs sont égaux
			if(genes[i-1]==genes[i])
			{
				// Si ces deux gènes sont des dépôts
				if(genes[i-1]==0)
				{
					// On remplace le second dépôt par un client aléatoire
					if(i!=nombreDeGenes-1)
					{
						genes[i]=(int)(Math.random()*nombreClients)+1;
					}
					// Si on se trouve à la fin, on remplace le premier dépôt par un client aléatoire différent du précédent
					else
					{
						do
						{
							genes[i-1]=(int)(Math.random()*nombreClients)+1;
						}
						while(genes[i-1]==genes[i-2]);
					}
				}
				// Sinon (ce sont des clients)
				else
				{
					genes[i]=0;
				}
			}
		}
	}
	
	/**
	 * initIncoherences calcule le nombre d'incohérences d'un individu.
	 * Une incohérence est le non-respect d'une contrainte.
	 * (Ici l'uniquement contrainte est la capacité du véhicule, autrement dit chaque tournée ne doit pas dépasser cette capacité).
	 * Dans chaque tournée, c'est-à-dire entre deux dépôts pour chaque client dépassant la capacité du véhicule, on incrémente le nombre d'incohéreces
	 * Ainsi, un individu avec 0 incohérence est un individu possible respectant toutes les contraintes.
	 * 
	 * Il faut aussi desservir une et une seule fois chaque client, sinon incohérence
	 * @param g Graphe contenant tous les noeuds représentant les clients
	 * @param capacite Contrainte du VRP représentant la capacité maximale que peut contenir un véhicule par tournée
	 * @see Individu#incoherences
	 */
	public void initIncoherences(Graph g, int capacite)
	{
		incoherences = 0;
		
		ArrayList<Integer> clientsParcourus = new ArrayList<Integer>();
		
		// Pour une tournée, on fait la somme de toutes les demandes des clients.
		int demandeTourneeEnCours = 0;
		
		for(int gene:genes)
		{
			// Si le gene est 0, on se trouve au dépôt. C'est une nouvelle tournée. On réinitialise alors la demande de la tournée à 0.
			if(gene == 0)
			{
				demandeTourneeEnCours = 0;
			}
			// Sinon nous sommes sur un autre noeud donc sur un client qui a une demande.
			else
			{
				// On incrémente incoherences si plusieurs fois le même client
				if(clientsParcourus.isEmpty()) clientsParcourus.add(gene);
				else
				{
					if(clientsParcourus.contains(gene)) incoherences++;
					else clientsParcourus.add(gene);
				}
				// Pour la capacité :
				// On ajoute la demande de ce client à la somme
				demandeTourneeEnCours += Integer.parseInt(g.getNode(gene).getAttribute("demande"));
			}
			// à chaque fois que la demande dépasse la capacité, on incrémente le nombre d'incohérences.
			// dans une même tournée, nous pouvons le faire plusieurs fois. Pour chaque client en plus après avoir dépassé la capacité.
			if(demandeTourneeEnCours > capacite)
			{
				incoherences++;
			}
		}
		
		// Parcours clientsParcourus et vérifier qu'il y a tous les clients
		incoherences+=(g.getNodeCount()-1)-clientsParcourus.size();
	}
	
	/**
	 * differences compare le tableau de genes de cet individu avec les genes d'un autre individu
	 * @param autresGenes tableau de genes appartenant à un autre individu
	 * @return diff le nombre de tournees differentes
	 */
	public double differences(int[] autresGenes)
	{
		double diff = 0;
		int nombreDiffLocale = 0;
		int nombreGenesLocaux = 0;
		int indiceGenes = 0;
		int indiceAutresGenes = 0;
		int tailleAutresGenes = autresGenes.length;
		// Comparaison de chaque gene des deux tableaux de genes
		while(indiceGenes<this.nombreDeGenes && indiceAutresGenes<tailleAutresGenes)
		{
			// si un des genes est 0 alors nous sommes dans une nouvelle tournee
			if(genes[indiceGenes] == 0 || autresGenes[indiceAutresGenes] == 0)
			{
				if(nombreGenesLocaux!=0) diff += nombreDiffLocale/nombreGenesLocaux;
				nombreDiffLocale = 0;
				nombreGenesLocaux = 0;
				// on parcourt les genes jusqu'à prochain dépôt afin d'être au dépôt pour les deux gènes
				// de façon à être sur le premier client d'une nouvelle tournée
				while(indiceGenes<this.nombreDeGenes && genes[indiceGenes] == 0) indiceGenes++;
				while(indiceAutresGenes<tailleAutresGenes && autresGenes[indiceAutresGenes] == 0) indiceAutresGenes++;
			}
			else
			{
				if(genes[indiceGenes] != autresGenes[indiceAutresGenes])
				{
					nombreDiffLocale++;
				}
				nombreGenesLocaux++;
				indiceGenes++;
				indiceAutresGenes++;
			}
		}
		return diff;
	}
	
	
	/**
	 * initDiversite initialise le rang relatif en terme de contribution à la diversité d'un individu.
	 * La diversité consiste à comparer cet individu avec tous les autres individus de la génération.
	 * Un individu est proche d'un autre s'il a des tournées identiques ou presque identiques à un autre.
	 * @param lesIndividus tableau d'individus représentant toute la génération
	 * @see Individu#scoreDiversite
	 */
	public void initDiversite(ArrayList<Individu> lesIndividus)
	{
		scoreDiversite = 0;
		
		for(Individu autreIndividu:lesIndividus)
		{
			int[] autresGenes = autreIndividu.getGenes();
			scoreDiversite += differences(autresGenes);
		}
	}
	
	
	/**
	 * initFitnessBiaisee initialise l'attribut fitnessBiaisee
	 * Formule mathématiques créée par Thibaut VIDAL permettant de calculer la fitness biaisée.
	 * L'évaluation d'un individu est un compromis entre son rang relatif en terme de fitness et son rang relatif en terme de contribution à la diversité.
	 * @param nbrElites nombre d'individus ayant la meilleure fitness dans la génération
	 * @param nbrIndividus nombre d'individus ayant la meilleure fitness dans la génération
	 * @see Individu#fitnessBiaisee
	 */
	public void initFitnessBiaisee(int nbrElites, int nbrIndividus)
	{
		this.fitnessBiaisee = rangFitness + ( 1 - ( nbrElites / nbrIndividus )) * rangDiversite;
	}
	
	/**
	 * Mutation sur cet individu. Transforme cet individu en un autre en effectuant forceDeMutation permutations.
	 * Si la chance de mutation est plus élevé qu'un pourcentage aléatoire, on effectue la mutation. Sinon, il n'y a pas de mutation.
	 * @see Individu#chanceDeMutation
	 * @param chanceDeMutation
	 * @param forceDeMutation
	 */
	public void mutation(double chanceDeMutation, int forceDeMutation)
	{
		if(chanceDeMutation > Math.random())
		{
			// On applique forceDeMutation permutation sur les gènes de cet individu
			for(int i=1;i<=forceDeMutation;i++)
			{ // 0 1 2 3 0
				/*
				 * indice1 et indice2 représente les indices que l'on va permuter. Ils sont choisis aléatoirement mais ne peuvent correspondre
				 * au premier indice et au dernier indice du tableau de genes car ceux-ci doivent être toujours être à 0 (le dépôt)
				 */
				int indice1 = (int) (Math.random()*(nombreDeGenes-2)) + 1 ; // nbr aléatoire entre 1 inclus et nombreDeGenes -1 exclu
				int indice2;
				do
				{
					indice2 = (int) (Math.random()*(nombreDeGenes-2)) + 1 ; // nbr aléatoire entre 1 inclus et nombreDeGenes -1 exclu
				}
				while(indice1 == indice2);
				
				int tmp = genes[indice1];
				genes[indice1] = genes[indice2];
				genes[indice2] = tmp;
			}
		}
	}
	
	/**
	 * Compare cet individu avec l'individu passé en paramètre en fonction de la fitness
	 * @see Individu#scoreFitness
	 * @param ind
	 * @return -1 si la fitness de cet individu est plus petite (plus intéressante) donc sera placé avant dans la comparaison. 0 si égale. 1 sinon.
	 */
	public int compareToScoreFitness(Individu ind)
	{
		double difference = this.scoreFitness - ind.getScoreFitness();
		if(difference < 0) return -1;
		else if(difference == 0) return 0;
		else return 1;
	}
	
	/**
	 * Compare cet individu avec l'individu passé en paramètre en fonction de la diversité
	 * @see Individu#scoreDiversite
	 * @param ind
	 * @return -1 si la diversité de cet individu est plus petite (plus intéressante) donc sera placé avant dans la comparaison. 0 si égale. 1 sinon.
	 */
	public int compareToScoreDiversite(Individu ind)
	{
		double difference = this.scoreDiversite - ind.getScoreDiversite();
		if(difference < 0) return -1;
		else if(difference == 0) return 0;
		else return 1;
	}
	
	/**
	 * Compare cet individu avec l'individu passé en paramètre en fonction de la fitness biaisée
	 * @see Individu#fitnessBiaisee
	 * @param ind
	 * @return -1 si la fitness biaisée de cet individu est plus petite (plus intéressante) donc sera placé avant dans la comparaison. 0 si égale. 1 sinon.
	 */
	public int compareToFitnessBiaisee(Individu ind)
	{
		double difference = this.fitnessBiaisee - ind.getFitnessBiaisee();
		if(difference < 0) return -1;
		else if(difference == 0) return 0;
		else return 1;
	}
	
	/**
	 * @see Individu#genes
	 * @return une chaîne de caractères contenant les gènes de l'individu
	 */
	public String toString()
	{
		String s = "";
		for(int i:this.genes)
		{
			s+=i+" ";
		}
		return s;
	}
}
