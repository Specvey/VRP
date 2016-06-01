import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * La classe Fenetre crée une interface graphique permettant à l'utilisateur d'utiliser le programme.
 * 
 * @author Xavier MAUGY & Alexandre MANCHON
 * @version 31/05/2016
 */
public class Fenetre
{
	// La fenêtre
	
	private JFrame fenetre;
	
	// Les boutons
	
	JButton boutonGenFichier;
	JButton boutonGenRand;
	JButton boutonExec;
	
	// Les JTextField
	
	JTextField texteInstance;
	JTextField texteParam1;
	JTextField texteNbrClients;
	JTextField texteCapacite;
	JTextField texteParam2;
	
	// L'objet génétique
	
	Genetique genetique;
	
	public Fenetre()
	{
		// La fenêtre
		
		fenetre = new JFrame();
		fenetre.setTitle("VRP");
		fenetre.setPreferredSize(new Dimension(1100,400));
		fenetre.setLocationRelativeTo(null);
		
		// Les panneaux principaux
		
		JPanel panneau = new JPanel();
		panneau.setLayout(new BoxLayout(panneau,BoxLayout.Y_AXIS));
		JPanel ligne1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel ligne2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel ligne3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel ligne4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel ligne5 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		// Génération à partir d'une instance
		
		boutonGenFichier = new JButton("Génération à partir d'une instance");
		boutonGenFichier.addActionListener(new genFichierListener());
		JLabel labelInstance = new JLabel("Nom de l'instance : data/");
		texteInstance = new JTextField(30);
		JLabel labelParam1 = new JLabel("Nom du fichier paramètres : data/");
		texteParam1 = new JTextField(30);
		ligne1.add(boutonGenFichier);
		ligne2.add(labelInstance);
		ligne2.add(texteInstance);
		ligne2.add(labelParam1);
		ligne2.add(texteParam1);
		
		// Génération aléatoire
		
		boutonGenRand = new JButton("Génération aléatoire");
		boutonGenRand.addActionListener(new genRandListener());
		JLabel labelNbrClients = new JLabel("Nombre de clients :");
		texteNbrClients = new JTextField(10);
		JLabel labelCapacite = new JLabel("Capacité du véhicule :");
		texteCapacite = new JTextField(10);
		JLabel labelParam2 = new JLabel("Nom du fichier paramètres : data/");
		texteParam2 = new JTextField(30);
		ligne3.add(boutonGenRand);
		ligne4.add(labelNbrClients);
		ligne4.add(texteNbrClients);
		ligne4.add(labelCapacite);
		ligne4.add(texteCapacite);
		ligne4.add(labelParam2);
		ligne4.add(texteParam2);
		
		// Bouton exécution au centre
		
		boutonExec = new JButton("Exécution");
		boutonExec.addActionListener(new execListener());
		ligne5.add(boutonExec);
		
		// On ajoute tous les composants aux panneaux
		
		panneau.add(ligne1);
		panneau.add(ligne2);
		panneau.add(ligne3);
		panneau.add(ligne4);
		panneau.add(ligne5);
		fenetre.setContentPane(panneau);
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fenetre.setVisible(true);
		fenetre.pack();
	}

	
	class genFichierListener extends AbstractAction
	{
	    public void actionPerformed(ActionEvent arg0)
	    {
	    	File f1 = new File("data/"+texteInstance.getText());
	    	File f2 = new File("data/"+texteParam1.getText());
	    	if(texteInstance.getText().isEmpty())
	    	{
	    		JOptionPane.showMessageDialog(new Frame(),
	    			    "Veuillez rentrer le nom du fichier d'instance.",
	    			    "Aucune instance",
	    			    JOptionPane.ERROR_MESSAGE);
	    	}
	    	else if(texteInstance.getText().isEmpty())
	    	{
	    		JOptionPane.showMessageDialog(new Frame(),
	    			    "Veuillez rentrer le nom du fichier d'instance.",
	    			    "Aucun fichier d'instance",
	    			    JOptionPane.ERROR_MESSAGE);
	    	}
	    	else if(texteParam1.getText().isEmpty())
	    	{
	    		JOptionPane.showMessageDialog(new Frame(),
	    			    "Veuillez rentrer le nom du fichier de paramètres.",
	    			    "Aucun fichier de paramètres",
	    			    JOptionPane.ERROR_MESSAGE);
	    	}
	    	else if(!f1.exists())
	    	{
	    		JOptionPane.showMessageDialog(new Frame(),
	    			    "Veuillez rentrer un nom du fichier d'instance valide.",
	    			    "Aucun fichier d'instance avec un tel nom trouvé.",
	    			    JOptionPane.ERROR_MESSAGE);
	    	}
	    	else if(!f2.exists())
	    	{
	    		JOptionPane.showMessageDialog(new Frame(),
	    			    "Veuillez rentrer un nom du fichier de paramètres valide.",
	    			    "Aucun fichier de paramètres avec un tel nom trouvé.",
	    			    JOptionPane.ERROR_MESSAGE);
	    	}
	    	else
	    	{
	    		boutonGenFichier.setSelected(true);
	    		boutonGenRand.setSelected(false);
	    		boutonExec.setSelected(false);
	    		genetique = new Genetique("data/"+texteInstance.getText(),"data/"+texteParam1.getText());
	    	}
	    }
	}
	
	class genRandListener extends AbstractAction
	{
	    public void actionPerformed(ActionEvent arg0)
	    {
	    	File f = new File("data/"+texteParam2.getText());
	    	if(!texteNbrClients.getText().matches("[0-9]+"))
	    	{
	    		JOptionPane.showMessageDialog(new Frame(),
	    			    "Veuillez rentrer un nombre pour le nombre de clients.",
	    			    "Nombre de clients",
	    			    JOptionPane.ERROR_MESSAGE);
	    		
	    	}
	    	else if(!texteCapacite.getText().matches("[0-9]+"))
	    	{
	    		JOptionPane.showMessageDialog(new Frame(),
	    			    "Veuillez rentrer un nombre pour la capacité du véhicule.",
	    			    "Capacité du véhicule",
	    			    JOptionPane.ERROR_MESSAGE);
	    		
	    	}
	    	else if(texteParam2.getText().isEmpty())
	    	{
	    		JOptionPane.showMessageDialog(new Frame(),
	    			    "Veuillez rentrer le nom du fichier de paramètres.",
	    			    "Aucun fichier de paramètres",
	    			    JOptionPane.ERROR_MESSAGE);
	    	}
	    	else if(!f.exists())
	    	{
	    		JOptionPane.showMessageDialog(new Frame(),
	    			    "Veuillez rentrer un nom du fichier de paramètres valide.",
	    			    "Aucun fichier de paramètres avec un tel nom trouvé.",
	    			    JOptionPane.ERROR_MESSAGE);
	    	}
	    	else
	    	{
	    		boutonGenFichier.setSelected(false);
	    		boutonGenRand.setSelected(true);
	    		boutonExec.setSelected(false);
	    		genetique = new Genetique(Integer.parseInt(texteNbrClients.getText()),Integer.parseInt(texteCapacite.getText()),"data/"+texteParam2.getText());
	    	}
	    }
	}
	
	class execListener extends AbstractAction
	{
	    public void actionPerformed(ActionEvent arg0)
	    {
	    	if(!boutonGenFichier.isSelected() && !boutonGenRand.isSelected())
	    	{
	    		JOptionPane.showMessageDialog(new Frame(),
	    			    "Veuillez générer une instance avant d'exécuter.",
	    			    "Aucune instance générée",
	    			    JOptionPane.ERROR_MESSAGE);
	    	}
	    	else
	    	{
	    		boutonGenFichier.setSelected(false);
	    		boutonGenRand.setSelected(false);
	    		boutonExec.setSelected(true);
	    		genetique.evolution();
	    		boutonExec.setSelected(false);
	    	}
	    }
	}
	
	public static void createAndShowGUI()
	{
		new Fenetre();
	}
	
}
