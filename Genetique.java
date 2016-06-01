import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;


/**
 * La classe Genetique est le cœur du programme qui contient l’algorithme génétique.
 * On fait évoluer la population d'individus sur plusieurs générations
 * 
 * @author Xavier MAUGY & Alexandre MANCHON
 * @version 31/05/2016
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
	 * @see Genetique#evolution()
	 */
	private Individu solution;
	
	/*
	 * Les paramètres génétiques :
	 */
	
	/**
	 * Taille de la population
	 * @see Genetique#population
	 */
	private int taillePopulation;
	
	/**
	 * (Rappel : + le scoreFitness est faible, + la fitness est intéressante.)
	 * Un individu élite a un scoreFitness entre meilleurFitness + meilleurFitness * pourcentageFitnessIndividusElites et meilleurFitness
	 * @see Genetique#initNombreElites(ArrayList)
	 */
	private double pourcentageFitnessIndividusElites;
	
	// La mutation
	
	/**
	 * Pourcentage de chance pour qu'un nouveau individu effectue ou non une mutation
	 * @see Individu#mutation(int, int)
	 */
	private double chanceDeMutation;
	
	/**
	 * La force ou l'impact de la mutation sur l'individu, l'entier correspond au nombre de permutations
	 * @see Individu#mutation(int, double)
	 */
	private int forceDeMutation;
	
	/**
	 * partCroisementMeilleursIndividus des individus ayant la meilleure fitness biaisée vont + se reproduire
	 * @see Genetique#croisements()
	 */
	private double partCroisementMeilleursIndividus;
	
	// parts des individus pris dans la génération suivante
	
	/**
	 * partMeilleurFitnessValide représente la part des individus ayant la meilleure fitness valide qui seront pris dans la génération suivante
	 * @see Genetique#nouvelleGeneration()
	 */
	private double partMeilleurFitnessValide;
	
	/**
	 * partMeilleurFitnessBiaisee représente la part des individus ayant la meilleure fitness biaisée qui seront pris dans la génération suivante
	 * @see Genetique#nouvelleGeneration()
	 */
	private double partMeilleurFitnessBiaisee;
	
	/**
	 * partMeilleurDiversite représente la part des individus ayant la meilleure diversité qui seront pris dans la génération suivante
	 * @see Genetique#nouvelleGeneration()
	 */
	private double partMeilleurDiversite;
	
	// On regarde toutes les cycleOptimisation générations, si la fitness ne s'est pas améliorée de pourcentageAmeliorationFitness %
	// alors on réinjecte partReinjectionIndRand % d'individus aléatoires dans la population
	
	/**
	 * cycleOptimisation représente le pas de génération où on regarde si la fitness s'est améliorée de beaucoup ou non
	 * @see Genetique#evolution()
	 */
	private int cycleOptimisation;
	
	/**
	 * pourcentageAmeliorationFitness représente le % d'amélioration souhaité de la fitness
	 * @see Genetique#evolution()
	 */
	private double pourcentageAmeliorationFitness;
	
	/**
	 * partReinjectionIndRand représente la part d'individus aléatoires réinjectés dans la population
	 * @see Genetique#evolution()
	 */
	private double partReinjectionIndRand;
	
	// Conditions d'arrêt
	
	/**
	 * nombreGenerationsMax représente le nombre de générations maximum que l'algorithme va effectuer
	 * @see Genetique#evolution()
	 */
	private int nombreGenerationsMax;
	
	/**
	 * objectifFitness représente la fitness objectif. Une fois atteinte, l'algorithme s'arrête
	 * @see Genetique#evolution()
	 */
	private int objectifFitness;
	
	/**
	 * tempsExecutionMax représente le temps (en minutes) d'exécution maximum de l'algorithme
	 * @see Genetique#evolution()
	 */
	private long tempsExecutionMax;
	
	
	/**
	 * Constructeur qui crée un graphe, instancie les paramètres génétiques et la première génération de la population
	 * @param instanceVRP nom du fichier contenant l'instance VRP
	 * @param paramGenetique nom du fichier contenant les paramètres génétiques
	 */
	public Genetique(String instanceVRP, String paramGenetique)
	{
		creationGraphe(instanceVRP);
		lectureParametres(paramGenetique);
		initPopulation();
	}
	
	/**
	 * Constructeur qui crée un graphe, instancie les paramètres génétiques et la première génération de la population
	 * @param nombreClients
	 * @param capaciteVehicule
	 * @param paramGenetique nom du fichier contenant les paramètres génétiques
	 */
	public Genetique(int nombreClients, int capaciteVehicule, String paramGenetique)
	{
		this.nombreClients = nombreClients;
		this.capaciteVehicule = capaciteVehicule;
		creationGrapheAleatoire();
		lectureParametres(paramGenetique);
		initPopulation();
	}
	
	/**
	 * A partir d'un fichier, on crée le graphe.
	 * Chaque noeud a des coordonnées x y et une demande.
	 * Chaque arête a un poids correspondant au temps (distance cartésienne entre ses extrêmités)
	 * @param instanceVRP
	 * @see Genetique#Genetique(String, String)
	 */
	public void creationGraphe(String instanceVRP)
	{
		// On crée le graphe
		g = new SingleGraph("Graphe");
		g.addAttribute("ui.stylesheet","url('data/style.css')");
		
	    FileInputStream fisInstanceVRP = null;
	    InputStreamReader isrInstanceVRP = null;
	    BufferedReader brInstanceVRP = null;
	    String ligneInstanceVRP;
	    // lectureCoordNoeud : Doit-on lire les coordonnées des noeuds ? Oui/Non
	    boolean lectureCoordNoeud = false;
	    // lectureDemandeNoeud : Doit-on lire les demandes des noeuds ? Oui/Non
	    boolean lectureDemandeNoeud = false;
	    // numeroNoeud correspond au numéro du noeud parcouru lors de la lecture
	    int numeroNoeud = 1;
	    try
	    {
	    	fisInstanceVRP = new FileInputStream(new File(instanceVRP));
	    	isrInstanceVRP = new InputStreamReader(fisInstanceVRP);
	    	brInstanceVRP = new BufferedReader(isrInstanceVRP);
	    	// Tant que l'on est pas à la fin du fichier, on continue à lire
	    	while((ligneInstanceVRP = brInstanceVRP.readLine()) != null && !(ligneInstanceVRP.equals("DEPOT_SECTION")))
	    	{
	    		// Lecture du nombre de clients (dim-1 car dim = tous les clients + le dépôt)
	    		if(ligneInstanceVRP.contains("DIMENSION"))
	    		{
	    			this.nombreClients = Integer.parseInt(ligneInstanceVRP.substring(12)) - 1;
	    		}
	    		// Lecture de la contrainte de capacité du véhicule
	    		if(ligneInstanceVRP.contains("CAPACITY"))
	    		{
	    			this.capaciteVehicule = Integer.parseInt(ligneInstanceVRP.substring(11));
	    		}
	    		// Lecture des demandes des noeuds
	    		if(lectureDemandeNoeud)
	    		{
	    			// On ajoute l'attribut demande à chaque noeud
	    			g.getNode(""+(numeroNoeud-1)).addAttribute("demande",ligneInstanceVRP.substring((""+numeroNoeud).length()+1, ligneInstanceVRP.length()));
	    			numeroNoeud++;
	    		}
	    		// Préparation des booléens et réinitialisation du numéro du noeud pour la lecture des demandes des noeuds
	    		if(ligneInstanceVRP.contains("DEMAND_SECTION"))
	    		{
	    			lectureCoordNoeud = false;
	    			lectureDemandeNoeud = true;
	    			numeroNoeud = 1;
	    		}
	    		// Lecture des coordonnées des noeuds
	    		if(lectureCoordNoeud)
	    		{
	    			// On crée un noeud avec ses coordonées :
	    			g.addNode(""+(numeroNoeud-1));
	    			String coordonnees = ligneInstanceVRP.substring((""+numeroNoeud).length()+1);
	    			String tabCoordonnes[] = coordonnees.split(" ");
	    			int x = Integer.parseInt(tabCoordonnes[0]);
	    			int y = Integer.parseInt(tabCoordonnes[1]);
	    			//int x = Integer.parseInt((ligneInstanceVRP.substring((""+numeroNoeud).length()+1, (""+numeroNoeud).length()+4)));
	    			//int y = Integer.parseInt(ligneInstanceVRP.substring((""+numeroNoeud).length()+5, (""+numeroNoeud).length()+8));
	    			g.getNode(""+(numeroNoeud-1)).addAttribute("xy",x,y);
	    			numeroNoeud++;
	    		}
	    		// Préparation du booléen pour la lecture des coordonnées des noeuds
	    		if(ligneInstanceVRP.contains("NODE_COORD_SECTION"))
	    		{
	    			lectureCoordNoeud = true;
	    		}
	    	}
	    	brInstanceVRP.close();
	    }
	    catch(FileNotFoundException e) // Si aucun fichier n'a été trouvé
	    {
	    	e.printStackTrace();
	    }
	    catch(IOException e) // Si erreur d'écriture ou de lecture
	    {
	    	e.printStackTrace();
	    }
	    finally
	    {
	    	try
	        {
	    		if(fisInstanceVRP != null)
	            {
	    			fisInstanceVRP.close();
	            }
	         }
	         catch(IOException e)
	         {
	            e.printStackTrace();
	         }
	    }
	    
	    // Tous les noeuds sont maintenant créés
	    // On parcourt le graphe et on crée les arêtes
	    // Chaque arête a un attribut poids correspondant au temps pour parcourir cette arête
	    // Ici le temps correspond à la distance qui est la distance cartésienne entre les noeuds extrêmités
	    // On calcule grâce aux coordonnées : AB = Racine (xB - xA)² + (yB - yA)²
	    for(int i=0;i<nombreClients;i++)
	    {
	    	for(int j=i+1;j<=nombreClients;j++)
	    	{
	    		g.addEdge(""+i+" "+j,""+i,""+j);
	    		Object[] tabA = g.getNode(""+i).getArray("xy");
	    		int xA = (int) tabA[0];
	    		int yA = (int) tabA[1];
	    		Object[] tabB = g.getNode(""+j).getArray("xy");
	    		int xB = (int) tabB[0];
	    		int yB = (int) tabB[1];
	    		double poids = Math.sqrt( Math.pow(xB - xA,2) + Math.pow(yB - yA,2) );
	    		g.getEdge(""+i+" "+j).addAttribute("poids",poids);
	    	}
	    }
	    // Mise en place de l'affichage des id des noeuds
	    for(Node n:g)
		{
			n.addAttribute("ui.label",n.getId());
		}
		/*
		for(Edge e:g.getEachEdge())
		{
			e.addAttribute("ui.label",(double)e.getAttribute("poids"));
		}*/
	}
	
	/**
	 * On crée le graphe aléatoirement.
	 * Chaque noeud a des coordonnées x y et une demande.
	 * Chaque arête a un poids correspondant au temps (distance cartésienne entre ses extrêmités)
	 * @see Genetique#Genetique(int, int, String)
	 */
	public void creationGrapheAleatoire()
	{
		// On crée le graphe
		g = new SingleGraph("Graphe");
		g.addAttribute("ui.stylesheet","url('data/style.css')");
	    for(int i=0;i<=nombreClients;i++)
	    {
	    	g.addNode(""+i); 
	    }
	    g.getNode("0").addAttribute("demande","0");
	    for(int i=1;i<=nombreClients;i++)
	    {
	    	g.getNode(""+i).addAttribute("demande",""+(int)((Math.random()*(capaciteVehicule/2))+1));
	    }
	    for(Node n : g)
	    {
	    	n.addAttribute("xy",(int)(Math.random()*nombreClients*15),(int)(Math.random()*nombreClients*15));
	    }
	    
	    // Tous les noeuds sont maintenant créés
	    // On parcourt le graphe et on crée les arêtes
	    // Chaque arête a un attribut poids correspondant au temps pour parcourir cette arête
	    // Ici le temps correspond à la distance qui est la distance cartésienne entre les noeuds extrêmités
	    // On calcule grâce aux coordonnées : AB = Racine (xB - xA)² + (yB - yA)²
	    for(int i=0;i<nombreClients;i++)
	    {
	    	for(int j=i+1;j<=nombreClients;j++)
	    	{
	    		g.addEdge(""+i+" "+j,""+i,""+j);
	    		Object[] tabA = g.getNode(""+i).getArray("xy");
	    		int xA = (int) tabA[0];
	    		int yA = (int) tabA[1];
	    		Object[] tabB = g.getNode(""+j).getArray("xy");
	    		int xB = (int) tabB[0];
	    		int yB = (int) tabB[1];
	    		double poids = Math.sqrt( Math.pow(xB - xA,2) + Math.pow(yB - yA,2) );
	    		g.getEdge(""+i+" "+j).addAttribute("poids",poids);
	    	}
	    }
	    // Mise en place de l'affichage des id des noeuds
	    for(Node n:g)
		{
			n.addAttribute("ui.label",n.getId());
		}
	    
		/*
		for(Edge e:g.getEachEdge())
		{
			e.addAttribute("ui.label",(double)e.getAttribute("poids"));
		}*/
	}
	
	/**
	 * On instancie les paramètres génétiques
	 * @param paramGenetique
	 * @see Genetique#Genetique(String, String)
	 */
	public void lectureParametres(String paramGenetique)
	{
		//Lecture des paramètres
		FileInputStream fileParam = null;
		InputStreamReader inputParam = null;
		BufferedReader buffParam = null;
		String ligneParam;
		try
		{
			fileParam = new FileInputStream(new File(paramGenetique));
		   	inputParam = new InputStreamReader(fileParam);
		    buffParam = new BufferedReader(inputParam);
		    while((ligneParam = buffParam.readLine()) != null)
		    {
		    	if(ligneParam.contains("taillePopulation"))
		    	{
		    		this.taillePopulation = Integer.parseInt(ligneParam.substring("taillePopulation".length()+3));
		    	}
		    		
		    	if(ligneParam.contains("pourcentageFitnessIndividusElites"))
		    	{
		    		this.pourcentageFitnessIndividusElites = Double.parseDouble(ligneParam.substring("pourcentageFitnessIndividusElites".length()+3));
		    	}
		    		
		    	if(ligneParam.contains("chanceDeMutation"))
		    	{
		    		this.chanceDeMutation = Double.parseDouble(ligneParam.substring("chanceDeMutation".length()+3));
		    	}
		    		
		    	if(ligneParam.contains("forceDeMutation"))
		    	{
		    		this.forceDeMutation = Integer.parseInt(ligneParam.substring("forceDeMutation".length()+3));
		    	}
		    		
		    	if(ligneParam.contains("partCroisementMeilleursIndividus"))
		    	{
		    		this.partCroisementMeilleursIndividus = Double.parseDouble(ligneParam.substring("partCroisementMeilleursIndividus".length()+3));
		    	}
		    		
		    	if(ligneParam.contains("partMeilleurFitnessValide"))
		    	{
		    		this.partMeilleurFitnessValide = Double.parseDouble(ligneParam.substring("partMeilleurFitnessValide".length()+3));
		    	}
		    		
		    	if(ligneParam.contains("partMeilleurFitnessBiaisee"))
		    	{
		    		this.partMeilleurFitnessBiaisee = Double.parseDouble(ligneParam.substring("partMeilleurFitnessBiaisee".length()+3));
		    	}
		    		
		    	if(ligneParam.contains("partMeilleurDiversite"))
		    	{
		    		this.partMeilleurDiversite = Double.parseDouble(ligneParam.substring("partMeilleurDiversite".length()+3));
		    	}
		    		
		    	if(ligneParam.contains("cycleOptimisation"))
		    	{
		    		this.cycleOptimisation = Integer.parseInt(ligneParam.substring("cycleOptimisation".length()+3));
		    	}
		    		
		    	if(ligneParam.contains("pourcentageAmeliorationFitness"))
		    	{
		    		this.pourcentageAmeliorationFitness = Double.parseDouble(ligneParam.substring("pourcentageAmeliorationFitness".length()+3));
		    	}
		    		
		    	if(ligneParam.contains("partReinjectionIndRand"))
		    	{
		    		this.partReinjectionIndRand = Double.parseDouble(ligneParam.substring("partReinjectionIndRand".length()+3));
		    	}
		    		
		    	if(ligneParam.contains("nombreGenerationsMax"))
		    	{
		    		this.nombreGenerationsMax = Integer.parseInt(ligneParam.substring("nombreGenerationsMax".length()+3));
		    	}
		    	if(ligneParam.contains("objectifFitness"))
		    	{
		    		this.objectifFitness = Integer.parseInt(ligneParam.substring("objectifFitness".length()+3));
		    	}
		    	if(ligneParam.contains("tempsExecutionMax"))
		    	{
		    		this.tempsExecutionMax = Long.parseLong(ligneParam.substring("tempsExecutionMax".length()+3));
		    	}
		    }
		    buffParam.close();
		}
		catch(FileNotFoundException e) // Si aucun fichier n'a été trouvé
		{
			e.printStackTrace();
		}
		catch(IOException e) // Si erreur d'écriture ou de lecture
		{
			e.printStackTrace();
		}
		finally
		{
			try
		    {
		    	if(fileParam != null)
		        {
		    		fileParam.close();
		        }
		    }
		    catch(IOException e)
		    {
		        e.printStackTrace();
		    }
		}
	}
	
	/**
	 * On crée une première génération pour la population
	 * avec un grand tour (parcourt de chaque client sans revenir au dépôt)
	 * puis un split (on met un dépôt entre les clients dès que l'on dépasse la capacité)
	 * @see Genetique#Genetique(String, String)
	 * @see Genetique#Genetique(int, int, String)
	 */
	public void initPopulation()
	{
		// Grand tour
		int grandTour[] = new int[nombreClients];
		ArrayList<Node> NoeudsDejaPris = new ArrayList<Node>(nombreClients);
		NoeudsDejaPris.add(g.getNode(0));
		Node n1 = g.getNode(0);
		double poids = 0;
		for(int i=0;i<nombreClients;i++)
		{ // création du grand tour contenant tous les clients : 
			double poidsMin = 0;
			Node nMin = g.getNode(0);
			for(Node n2 : g)
			{ // à chaque étape, on regarde tous les noeuds ne faisant pas parti du grand tour pour trouver le plus proche du dernier ajouté
				if(!NoeudsDejaPris.contains(n2))
				{ // un client ne peut pas etre deux fois dans le grand tour
					poids = n1.getEdgeBetween(n2).getAttribute("poids");
					if(poidsMin == 0)
					{ // initialisation de la recherche du noeud le plus proche
						poidsMin = poids;
						nMin = n2;
					}
					else
					{ // recherche du noeud le plus proche
						if(poidsMin>poids)
						{
							poidsMin = poids;
							nMin = n2;
						}
					}
				}
			}
			// le noeud le plus proche trouvé, on le rajoute au grand tour
			grandTour[i] = Integer.parseInt(nMin.getId());
			NoeudsDejaPris.add(nMin);
		}
		NoeudsDejaPris.clear();
		
		// Split
		population = new ArrayList<Individu>();
		
		int individusCreesParSplit = Math.min(taillePopulation, nombreClients);
		for(int i=0;i<individusCreesParSplit;i++)
		{
			ArrayList<Integer> genes = new ArrayList<Integer>();
			genes.add(0);
			int indiceGrandTour = i%nombreClients;
			double capaciteDeLaTournee = 0;
			double capaciteSupplementaire = 0;
			do
			{
				capaciteSupplementaire = Double.parseDouble(g.getNode(""+grandTour[indiceGrandTour]).getAttribute("demande"));
				if(capaciteDeLaTournee+capaciteSupplementaire > capaciteVehicule)
				{
					capaciteDeLaTournee = 0;
					genes.add(0);
				}
				else
				{
					capaciteDeLaTournee += capaciteSupplementaire;
					genes.add(grandTour[indiceGrandTour]);
					indiceGrandTour = (indiceGrandTour + 1)%nombreClients;
				}
			}while(indiceGrandTour!=i);
			genes.add(0);
			int nouveauxGenes[] = new int[genes.size()];
			for(int j=0;j<genes.size();j++) nouveauxGenes[j] = genes.get(j);
			population.add(new Individu(nouveauxGenes));
		}
		// On crée des individus aléatoires pour le reste de la population
		for(int i=individusCreesParSplit;i<taillePopulation; i++)
		{
			population.add(new Individu(nombreClients));
		}
	}
	
	/**
	 * (Rappel : + le scoreFitness est faible, + la fitness est intéressante.)
	 * Un individu élite a un scoreFitness entre meilleurFitness + meilleurFitness * pourcentageFitnessIndividusElites et meilleurFitness
	 * Tous les individus élites représentent donc les individus ayant la meilleure fitness.
	 * @see Genetique#nbrIndividusElites
	 */
	public void initNombreElites(ArrayList<Individu> tab)
	{
		// initRangFitness a été appelé précédemment et a trié tab par fitness
		
		int taille = tab.size();
		// On rappelle que + scoreFitness est faible, + la fitness est intéressante
		// Pour être individu élite il faut au maximul avoir un scoreFitness de fitnessEliteMax
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
		    public int compare(Individu ind1, Individu ind2)
		    {
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
		    public int compare(Individu ind1, Individu ind2)
		    {
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
	 * Supression des doublons des individus de tab
	 * @param tab
	 */
	public ArrayList<Individu> supressionDoublons(ArrayList<Individu> tab)
	{
		ArrayList<Individu> newTab = new ArrayList<Individu>();
		for(Individu ind:tab)
		{
			if(!newTab.contains(ind))
			{
				newTab.add(ind);
			}
		}
		return newTab;
	}
	
	/**
	 * croisement fait se reproduire deux individus
	 * @param i1
	 * @param i2
	 * @return l'enfant des deux individus
	 */
	public Individu croisement(Individu i1, Individu i2)
	{ // 0 1 2 3 0
		int coupure = (int)(Math.random()*nombreClients)+1;
		int[] nouveauxGenes = new int[i2.getNombreDeGenes()];
		for(int i=0;i<coupure;i++)
		{
			nouveauxGenes[i] = i1.getGenes()[i];
		}
		for(int i=coupure;i<i2.getNombreDeGenes();i++)
		{
			nouveauxGenes[i] = i2.getGenes()[i];
		}
		return new Individu(nouveauxGenes);
	}
	
	/**
	 * croisements fait se reproduire la population
	 * partCroisementMeilleursIndividus des individus ayant la meilleure fitness biaisée vont + se reproduire
	 * @return les enfants de la population
	 */
	public ArrayList<Individu> croisements()
	{
		// On trie la population en fonction de la fitness biaisée
		triFitnessBiaisee(this.population);
		ArrayList<Individu> enfants = new ArrayList<Individu>();
		int indiceMaxMeilleurIndividu = (int) (taillePopulation * partCroisementMeilleursIndividus);
		for(int i=0;i<indiceMaxMeilleurIndividu;i++)
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
		for(Individu i : mutants) i.mutation(this.chanceDeMutation, this.forceDeMutation);
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
		individus = supressionDoublons(individus);
		initRangFitness(individus);
		initLesIncoherences(individus);
		
		int i=0;
		int j=0;
		while(i<(taillePopulation*1.0)*partMeilleurFitnessValide && j<individus.size())
		{
			if(individus.get(j).getIncoherences()==0)
			{
				//individus.get(j).optimisationLocale(g);
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
		for(i=0;i<taillePopulation*partMeilleurDiversite;i++)
		{
			nouvelleGeneration.add(individus.get(0));
			individus.remove(0);
		}
		initLesFitnessBiaisees(individus);
		triFitnessBiaisee(individus);
		for(i=0;i<taillePopulation*partMeilleurFitnessBiaisee;i++)
		{
			nouvelleGeneration.add(individus.get(0));
			individus.remove(0);
		}
		//remplir le vide par des indiv aléa
		for(i=nouvelleGeneration.size();i<taillePopulation;i++)
		{
			nouvelleGeneration.add(new Individu(this.nombreClients));
		}
		population = nouvelleGeneration;
		individus.clear();
		enfants.clear();
	}
	
	/**
	 * evolution fait évoluer la population génération par génération pour trouver un meilleur individu
	 */
	public void evolution()
	{
		long tempsDebut = System.currentTimeMillis();
		long tempsCourant;
		boolean arret = false;
		int nombreDeGeneration = 1;
		initRangFitness(population);
		initLesIncoherences(population);
		this.solution = population.get(0);
		Individu meilleurFitnessPreviouslyCycle = solution;
		initNombreElites(population);
		initRangDiversite(population);
		initLesFitnessBiaisees(population);
		System.out.println("Génération n°1\n Meilleur fitness : "+solution.getScoreFitness());
		while(!arret)
		{
			// Tous les cycleOptimisation générations
			if(nombreDeGeneration%cycleOptimisation==0)
			{
				System.out.println("Génération n°"+nombreDeGeneration+"\n Meilleur fitness : "+solution.getScoreFitness());
				// Si la fitness ne s'est pas suffisamment améliorée
				if( (1 - (solution.getScoreFitness() / meilleurFitnessPreviouslyCycle.getScoreFitness())) < pourcentageAmeliorationFitness)
				{
					meilleurFitnessPreviouslyCycle = solution;
					// On intégre de nouveaux individus
					ArrayList<Individu> nouvellePopulation = new ArrayList<Individu>();
					for(int i=0;i<(int)(taillePopulation*(1-partReinjectionIndRand));i++)
					{
						nouvellePopulation.add(population.get(i));
					}
					for(int i=(int)(taillePopulation*(1-partReinjectionIndRand));i<taillePopulation;i++)
					{
						nouvellePopulation.add(new Individu(nombreClients));
					}
					this.population = nouvellePopulation;
				}
				// Si la fitness s'est suffisamment améliorée
				else
				{
					meilleurFitnessPreviouslyCycle = solution;
					nouvelleGeneration();
					if(solution.getScoreFitness() > population.get(0).getScoreFitness())
					{
						solution = population.get(0);
					}
				}
			}
			else
			{
				nouvelleGeneration();
				if(solution.getScoreFitness() > population.get(0).getScoreFitness())
				{
					solution = population.get(0);
				}
			}
			nombreDeGeneration++;
			tempsCourant = System.currentTimeMillis();
			// Conditions d'arrêt
			if(nombreDeGeneration > nombreGenerationsMax)
			{
				System.out.println("Condition d'arrêt : nombre de génération max atteint : "+nombreGenerationsMax);
				arret = true;
			}
			if((tempsCourant - tempsDebut) >= tempsExecutionMax*60000)
			{
				System.out.println("Condition d'arrêt : temps d'exécution max atteint : "+tempsExecutionMax+" minutes.");
				arret = true;
			}
			if(solution.getScoreFitness() <= objectifFitness)
			{
				System.out.println("Condition d'arrêt : fitness inférieure à : "+objectifFitness);
				arret = true;
			}
		}
		System.out.println(solution);
	}
	
	public void afficherGraphe()
	{
		g.display(false);
	}
}
