/**
 * La classe Main permet d'exécuter le programme.
 * 
 * @author Xavier MAUGY & Alexandre MANCHON
 * @version 31/05/2016
 */
public class Main
{
	public static void main(String args[])
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Fenetre f = new Fenetre();
			}
		});
		
		//Genetique genetique = new Genetique(21,6000,"data/parametres9.data");
		
		//Genetique genetique = new Genetique("data/E-n22-k4.vrp","data/parametres9.data");
		//Genetique genetique = new Genetique("data/E-n101-k14.vrp","data/parametres9.data");
		
		//genetique.afficherGraphe();
		//genetique.evolution();
	}
}
