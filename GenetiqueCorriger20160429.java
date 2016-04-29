import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.graphstream.graph.Graph;

// TODO vérifier que les constructeurs initialisent bien tout

// TODO @see dans les champs et les méthodes

// TODO .clear() les arraylist temporaires

/**
 * La classe Genetique correspond à la classe qui fait évoluer la population d'individu sur plusieurs générations
 * 
 * @author Xavier MAUGY & Alexandre MANCHON
 * @version 29/04/2016 Genetique avant màj "Genetique constructeur" et "fin paramètres génétiques"
 * 
 */
public class Genetique
{
	/**
	 * ArrayList contenant les individus de la génération en cours
	 * @see Genetique#taillePopulation
	 */
	private ArrayList<Individu> population;
	
	/**
	 * Graphe non orienté complet représentant une carte où se situe géographiquement le dépôt et les clients.
	 * Le noeud d'identifiant 0 représente le dépôt. Tous les autres noeuds représente les clients.
	 * Chaque noeud a un attribut demande qui correspond à la demande d'un client.
	 * Chaque arête représente une route entre deux clients et a un attribut poids correspondant au temps de parcours entre deux noeuds.
	 */
	private Graph g;
	
	/**
	 * Nombre de clients, le nombre de noeuds - 1 (le dépôt)
	 */
	private int nombreClients;
	
	/**
	 * La contrainte capacité du véhicule représentant la capacité maximale que peut contenir le véhicule dans une seule tournée.
	 */
	private int capaciteVehicule;
	
	/**
	 * Le nombre d'individus ayant la meilleure fitness
	 * @see Genetique#initNombreElites(ArrayList)
	 */
	private int nbrIndividusElites;
	
	/**
	 * Solution représente le meilleur individu (c'est-à-dire celui avec la meilleure fitness) valide rencontré au cours des générations
	 * @see Genetique#initSolution()
	 */
	private Individu solution;
	
	
	// Les paramètres génétiques :
	
	/**
	 * Taille de la population
	 * @see Genetique#population
	 */
	private int taillePopulation;
	
	/**
	 * (Rappel : + le scoreFitness est faible, + la fitness est intéressante.)
	 * Un individu élite a un scoreFitness de meilleurFitness + meilleurFitness * pourcentageFitnessIndividusElites
	 * @see Genetique#initNombreElites(ArrayList)
	 */
	private double pourcentageFitnessIndividusElites;
	
	/**
	 * Pourcentage de chance pour qu'un nouveau individu effectue ou non une mutation
	 * @see Individu#chanceDeMutation
	 * @see Individu#mutation(double)
	 */
	private double chanceDeMutationDeBase;
	
	/**
	 * pourcentageDiminutionChanceMutation baisse chanceDeMutation d'un individu à chaque fois qu'il effectue une mutation
	 * chanceDeMutation = chanceDeMutation - chanceDeMutation * pourcentageDiminutionChanceMutation
	 * @see Individu#chanceDeMutation
	 * @see Individu#mutation(int, double)
	 */
	private double pourcentageDiminutionChanceMutation;
	
	/**
	 * La force ou l'impact de la mutation sur l'individu, l'entier correspond au nombre de permutations
	 * @see Individu#mutation(int, double)
	 */
	private int forceDeMutation;
	
	/**
	 * + partCroisementMeilleursIndividus est élevé, + on fait reproduire que les meilleurs individus
	 * @see Genetique#croisements()
	 */
	private double partCroisementMeilleursIndividus;
	
	// Xavier TODO Fais tous les paramètres génétiques du dessous
	// Ce sont tous les paramètres utilisés dans nouvelleGeneration() et evolution()
	// Corige ta nouvelleGeneration() au passage comme ça
	
	// Pour chaque nouvelle génération, les % de :
	// vérifier que la somme de tout ne dépasse pas 100%
	// si moins de 100%, le reste individus aléatoires

	private double partMeilleurFitnessValide;
	private double partMeilleurFitnessBiaisee;
	private double partMeilleurDiversite;
	
	// Pour la réinjection d'individus aléatoires si pas d'amélioration
	private int cycleOptimisation;
	private double pourcentageAmeliorationFitness;
	private double partReinjectionIndRand;
	
	// Conditions d'arrêt
	private int nombreGenerationsMax;
	private int objectifFitness;
	private double tempsExecutionMax;
	/*
	 * long startTime = System.currentTimeMillis();
    myfunction();
    long currentTime = System.currentTimeMillis();
    long searchTime = currentTime - startTime;
	 */
	
	
	/**
	 * 
	 * Faire dimension-1 pour avoir le nombre de clients
	 * De même pour la capacité du véhicule
	 * A partir d'un fichier, on crée directement le graphe.
	 * Créer tous les noeuds avec leurs x y
	 * Sur chaque noeud, on lui donne sa demande
	 * Créer toutes les arêtes avec comme poids la distance cartésienne entre les deux noeuds
	 * 
	 * Faire un grand tour en partant du dépôt, et cherchant le noeud le + proche à chaque fois et revenant à la fin au dépôt
	 * Ensuite, faire des split différents avec un premier noeud différent
	 * On fait Min(taillePopulation/3 , nombreClients) de split le reste des individus aléatoires jusqu'à taillePopulation
	 * @param nomFichier
	 */
	public Genetique(String nomFichier)
	{
		
		
		
		
	}
	
	
	/**
	 * (Rappel : + le scoreFitness est faible, + la fitness est intéressante.)
	 * Un individu élite a un scoreFitness de meilleurFitness + meilleurFitness * pourcentageFitnessIndividusElites
	 * Tous les individus élites représentent donc les individus ayant la meilleure fitness.
	 * @see Genetique#nbrIndividusElites
	 */
	public void initNombreElites(ArrayList<Individu> tab)
	{
		// initRangFitness a été appelé précédemment et a trié tab par fitness
		
		int taille = tab.size();
		// On rappelle que + scoreFitness est faible, + la fitness est intéressante
		// Un individu élite a au plus fitnessEliteMax scoreFitness
		double fitnessEliteMax = tab.get(0).getScoreFitness() + tab.get(0).getScoreFitness() * pourcentageFitnessIndividusElites;
		nbrIndividusElites = 1;
		
		// initRangFitness a précédemment trié tab par fitness.
		// Il suffit de parcourir l'arraylist tab et de s'arrêter lorsque l'on a trouvé une fitness trop élevée.
		while(nbrIndividusElites<taille && tab.get(nbrIndividusElites).getScoreFitness() < fitnessEliteMax)
		{
			nbrIndividusElites++;
		}
	}

	/**
	 * Calcule pour chaque individu de tab sa fitness
	 * Tri croissant des individus en fonction du scoreFitness
	 * Parcours tab et pour chaque individu, initiliase le rang relatif en terme de fitness
	 * @param tab une arraylist d'individus
	 * @see Individu#setRangFitness(int)
	 */
	public void initRangFitness(ArrayList<Individu> tab)
	{
		// Calcule pour chaque individu de tab sa fitness
		for(Individu ind:tab)
		{
			ind.initFitness(g);
		}
		
		// Tri croissant des individus en fonction du scoreFitness
		Collections.sort(tab, new Comparator<Individu>()
		{
		    @Override
		    public int compare(Individu ind1, Individu ind2) {
		        return ind1.compareToScoreFitness(ind2);
		    }
		});
		
		int taille = tab.size();
		// Parcours tab et pour chaque individu, initiliase le rang relatif en terme de fitness
		for(int i=0;i<taille;i++)
		{
			tab.get(i).setRangFitness(i);
		}
	}
	
	/**
	 * Calcule pour chaque individu de tab sa diversité
	 * Tri croissant des individus en fonction du scoreDiversite
	 * Parcours tab et pour chaque individu, initiliase le rang relatif en terme de contribution à la diversité de tous les individus
	 * @param tab une arraylist d'individus
	 * @see Individu#setRangDiversite(int)
	 */
	public void initRangDiversite(ArrayList<Individu> tab)
	{
		// Calcule pour chaque individu de tab sa diversité
		for(Individu ind:tab)
		{
			ind.initDiversite(tab);
		}
		
		// Tri croissant des individus en fonction du scoreDiversite
		Collections.sort(tab, new Comparator<Individu>()
		{
		    @Override
		    public int compare(Individu ind1, Individu ind2) {
		        return ind1.compareToScoreDiversite(ind2);
		    }
		});
		
		int taille = tab.size();
		// Parcours tab et pour chaque individu, initiliase le rang relatif en terme de contribution à la diversité de tous les individus
		for(int i=0;i<taille;i++)
		{
			tab.get(i).setRangDiversite(i);
		}
	}
	
	/**
	 * initLesIncoherences initialise le nombre d'incoherences pour chaque individu de tab
	 * @param tab
	 * @see Individu#initIncoherences(Graph, int)
	 */
	public void initLesIncoherences(ArrayList<Individu> tab)
	{
		for(Individu unIndividu:tab)
		{
			unIndividu.initIncoherences(g, capaciteVehicule);
		}
	}
	
	/**
	 * initLesFitnessBiaisees initialise la fitnessBiaisee pour chaque individu de tab
	 * @param tab
	 * @see Individu#initFitnessBiaisee(int, int)
	 */
	public void initLesFitnessBiaisees(ArrayList<Individu> tab)
	{
		int taille = tab.size();
		for(Individu unIndividu:tab)
		{
			unIndividu.initFitnessBiaisee(nbrIndividusElites, taille);
		}
	}
	
	/**
	 * triFitnessBiaisee tri l'arraylist d'individus en fonction de la fitness biaisée
	 * @param tab
	 */
	public void triFitnessBiaisee(ArrayList<Individu> tab)
	{
		// Tri croissant des individus de tab en fonction de la fitness biaisée
		Collections.sort(tab, new Comparator<Individu>()
		{
		    @Override
		    public int compare(Individu ind1, Individu ind2)
		    {
		        return ind1.compareToFitnessBiaisee(ind2);
		    }
		});
	}
	
	
	
	/**
	 * croisement fait se reproduire deux individus
	 * @param i1
	 * @param i2
	 * @return l'enfant des deux individus
	 */
	public Individu croisement(Individu i1, Individu i2)
	{
		int coupure = (int)(Math.random()*(nombreClients+2));
		int[] nouveauxGenes = new int[i2.getNombreDeGenes()];
		for(int i=0;i<coupure;i++)
		{
			nouveauxGenes[i] = i1.getGenes()[i];
		}
		for(int i=coupure;i<i2.getNombreDeGenes();i++)
		{
			nouveauxGenes[i] = i2.getGenes()[i];
		}
		return new Individu(nouveauxGenes, this.chanceDeMutationDeBase);
	}
	
	/**
	 * croisements fait se reproduire la population
	 * + partCroisementMeilleursIndividus est élevé, + on fait reproduire que les meilleurs individus
	 * @return les enfants de la population
	 */
	public ArrayList<Individu> croisements()
	{
		// On trie la population en fonction de la fitness biaisée
		triFitnessBiaisee(this.population);
		
		ArrayList<Individu> enfants = new ArrayList<Individu>();
		int indiceMeilleurIndividu = (int) (taillePopulation - taillePopulation * partCroisementMeilleursIndividus);
		for(int i=0;i<indiceMeilleurIndividu;i++)
		{
			for(int j=i;j<taillePopulation;j++)
			{
				enfants.add(croisement(population.get(i), population.get(j)));
			}
		}
		return enfants;
	}
	
	/**
	 * mutations fait muter les individus du paramètre
	 * @param individus
	 * @return individus après leur mutation
	 */
	public ArrayList<Individu> mutations(ArrayList<Individu> individus)
	{
		ArrayList<Individu> mutants = new ArrayList<Individu>(individus);
		for(Individu i : mutants) i.mutation(this.forceDeMutation, this.pourcentageDiminutionChanceMutation);
		return mutants;
	}
	
	/**
	 * nouvelleGeneration remplace la population par la génération suivante
	 */
	public void nouvelleGeneration()
	{
		ArrayList<Individu> enfants = mutations(croisements());
		ArrayList<Individu> individus = new ArrayList<Individu>(enfants);
		ArrayList<Individu> nouvelleGeneration = new ArrayList<Individu>();
		individus.addAll(population);
		initRangFitness(individus);
		initLesIncoherences(individus);
		
		// prendre 1/3
		int j=0;
		for(int i=0;i<taillePopulation*partMeilleurFitnessValide;i++)
		{
			if(individus.get(j).getIncoherences()==0)
			{
				nouvelleGeneration.add(individus.get(j));
				individus.remove(j);
				i++;
			}
			else
			{
				j++;
			}
		}
		initNombreElites(individus);
		initRangDiversite(individus);
		//prendre 1/3
		for(int i=0;i<taillePopulation*partMeilleurDiversite;i++)
		{
			nouvelleGeneration.add(individus.get(0));
			individus.remove(0);
		}
		initLesFitnessBiaisees(individus);
		triFitnessBiaisee(individus);
		//prendre 1/3
		for(int i=0;i<taillePopulation*partMeilleurFitnessBiaisee;i++)
		{
			nouvelleGeneration.add(individus.get(0));
			individus.remove(0);
		}
		//remplir le vide par des indiv aléa
		for(int i=nouvelleGeneration.size();i<taillePopulation;i++)
		{
			nouvelleGeneration.add(new Individu(this.nombreClients, this.chanceDeMutationDeBase));
		}
		population = nouvelleGeneration;
		individus.clear();
	}
	
	/**
	 * evolution fait évoluer la population génération par génération pour trouver un meilleur individu
	 */
	public void evolution()
	{
		boolean arret = false;
		Individu meilleurSolution = new Individu(this.nombreClients, this.chanceDeMutationDeBase);
		int nombreDeGeneration = 1;
		initRangFitness(population);
		initNombreElites(population);
		initRangDiversite(population);
		initLesFitnessBiaisees(population);
		while(!arret)
		{
			
			if(nombreDeGeneration%cycleOptimisation==0)
			{
				
				if(meilleurSolution.getScoreFitness() - population.get(0).getScoreFitness()>pourcentageAmeliorationFitness) // 10 est l'amélioration pour laquelle on peut dire que la progression diminue
				{
					// intégrer de nouveaux individus
					ArrayList<Individu> nouvellePopulation = new ArrayList<Individu>();
					nouvellePopulation.add(meilleurSolution);
					for(int i=1;i<taillePopulation*(1-partReinjectionIndRand);i++)
					{
						nouvellePopulation.add(population.get(i));
					}
					for(int i=taillePopulation*(1-partReinjectionIndRand);i<taillePopulation-1;i++)
					{
						nouvellePopulation.add(new Individu(nombreClients, this.chanceDeMutationDeBase));
					}
				}
			}
			else
			{
				nouvelleGeneration();
				if(meilleurSolution.getScoreFitness()-population.get(0).getScoreFitness()>0)
				{
					meilleurSolution = population.get(0);
				}
			}
			nombreDeGeneration++;
			if(nombreDeGeneration==nombreGenerationsMax) // condition d'arret
			{
				arret = true;
				population.set(0, meilleurSolution);
			}
		}
		solution = meilleurSolution;
	}
	
	
}
