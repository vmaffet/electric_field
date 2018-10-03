package cdce;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Carte est la classe regroupant tout le programme de visualisation de champs magnetiques sous diverses formes
 * 
 * Cette classe herite de JFrame afin d'afficher notre contenu dans une fenetre et implemente l'interface MouseListener pour gerer les clics de l'utilisateur
 * 
 * @author Vincent Maffet
 * @version 1.0
 */
public class Carte extends JFrame implements MouseListener {
	
	private static final long serialVersionUID = 7457122273108953633L;
	
	/**
	 * choixCharge contient l'affichage du choix de charge a ajouter, type d'affichage 
	 * affichage contient l'affichage du champ electrostatique
	 */
	JPanel choixCharge, affichage;
	
	/**
	 * Liste contenant toutes les charges posees actuellement sur la carte
	 */
	ArrayList<Charge> registreCharges;
	
	/**
	 * Boutons de selection du signe de la charge ainsi que le type d'affichage
	 * pos : charge positive 
	 * neg : charge negatice
	 * vect : affichage en champ de vecteurs
	 * lignes : affichage en lignes de champ
	 * equi : affichage du potentiel 
	 */
	JRadioButton pos, neg, vect, lignes, equi;
	
	/**
	 * Bouton permettant de faire table rase
	 */
	JButton raz;
	
	/**
	 * Groupes de boutons permettant de n'avoir qu'une seule selection a la fois
	 * signe : boutons pos et neg pour le choix du signe
	 * typeAff : vect, ligne et equi pour le choix de l'affichage
	 */
	ButtonGroup signe, typeAff;
	
	/**
	 * Constantes informant le type de charge a ajouter lors d'un clic ainsi que le type d'affichage voulu 
	 * qActuel : valeur de la prochaine charge a ajouter
	 * tA : type d'affichae selectionne
	 */
	int qActuel, tA;
	
	/**
     *Main de la classe qui est uniquement charge de lancer le programme 
     * 
     * @param args
     * 		Parametres au lancement           
     */
	public static void main(String[] args) {
		/**
		 * Nouvelle instance de la fenetre 
		 */
		new Carte();
	}
	
	/**
     * Constructeur de la classe :
     * cree la fenetre, dispose les elements dessus, initialise les differentes constantes, boutons et cree les ecouteurs associes(handlers) 
     *  
     */
	public Carte () {
		/**
		 * Creation de la fenetre 
		 */
		super("Carte de champ electrostatique");
		setSize(1100, 700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1100, 200));
		
		/**
		 * Initialisation des variables de classe
		 */
		registreCharges= new ArrayList<Charge>();
		qActuel= 100000;
		tA= 0;
		
		/**
		 * Initialisation des variables propres au choix d'affichage et de type de charge avec des ecouteurs qui vont bien
		 */
		choixCharge= new JPanel();
		JLabel j1= new JLabel("Choisissez le type de charges a ajouter et le type d'affichage");
		pos= new JRadioButton("Charge positive", true);
		neg= new JRadioButton("Charge negative", false);
		vect= new JRadioButton("Champ de vecteurs", true);
		lignes= new JRadioButton("Lignes de champ", false);
		equi= new JRadioButton("Potentiel", false);
		pos.addItemListener(new ItemListener(){
			public void itemStateChanged (ItemEvent e) {
				qActuel= qActuel < 0 ? -qActuel : qActuel;
			}
		});
		neg.addItemListener(new ItemListener(){
			public void itemStateChanged (ItemEvent e) {
				qActuel= qActuel > 0 ? -qActuel : qActuel;
			}
		});
		vect.addItemListener(new ItemListener(){
			public void itemStateChanged (ItemEvent e) {
				tA= 0;
				affichage.repaint();
			}
		});
		lignes.addItemListener(new ItemListener(){
			public void itemStateChanged (ItemEvent e) {
				tA= 1;
				affichage.repaint();
			}
		});
		equi.addItemListener(new ItemListener(){
			public void itemStateChanged (ItemEvent e) {
				tA= 2;
				affichage.repaint();
			}
		});
		signe= new ButtonGroup();
		signe.add(pos);
		signe.add(neg);
		typeAff= new ButtonGroup();
		typeAff.add(vect);
		typeAff.add(lignes);
		typeAff.add(equi);
		raz= new JButton("RAZ");
		raz.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e) {
				registreCharges.clear();
				affichage.repaint();
			}
		});
		choixCharge.add(j1);
		choixCharge.add(pos);
		choixCharge.add(neg);
		choixCharge.add(vect);
		choixCharge.add(lignes);
		choixCharge.add(equi);
		choixCharge.add(raz);
		
		/**
		 * Initialisation de la zone d'affichage et reecriture de la fonction paint pour afficher dans le bon cadre de la fenetre et non toute la fenetre
		 */
		affichage= new JPanel() {
			private static final long serialVersionUID = 8178000019787474409L;
			
			@Override
			public void paint (Graphics g) {
				switch (tA) {
					case 0:
						peindreVect(g);
						break;
					case 1:
						peindreLignes(g);
						break;
					case 2:
						peindrePotentiels(g);
						break;
					default:
						peindreVect(g);
						return;
				}
			}
		};
		
		/**
		 * Ajout a la fenetre et positionnement
		 */
		add(affichage, BorderLayout.CENTER);
		add(choixCharge, BorderLayout.NORTH);
		affichage.addMouseListener(this);
		
		setVisible(true);
	}
	
	/**
     * Passe la coordonnee horizontale d'un point dans la base d'affichage du JPanel
     * 
     * @param x
     * 		Position horizontale dans le repere classique  
     * @return 
     * 		Coordonnee horizontale dans le repere du JPanel
     */
	public int posL (int x) {
		return x+affichage.getWidth()/2;
	}
	
	/**
     * Passe la coordonnee verticale d'un point dans le base d'affichage du JPanel
     * 
     * @param y
     * 		Position verticale dans le repere classique   
     * @return 
     * 		Coordonnee verticale dans le repere du JPanel
     */
	public int posH (int y) {
		return (affichage.getHeight()/2)-y;
	}
	
	/**
     * Methode permettant de faire l'affichage sous forme de champ de vecteurs. 
     * 
     * @param g
     * 		Objet Graphics du JPanel permettant de faire l'affichage au bon endroit de la fenetre     
     */
	public void peindreVect (Graphics g) {
		/**
		 * Fond blanc
		 */
		g.setColor(Color.white);
		g.fillRect(0, 0, affichage.getWidth(), affichage.getHeight());
		
		Vector<Double> v, w;
		double normeV, normeW;
		
		/**
		 * Affichage des charges
		 */
		for (Charge c : registreCharges) {
			if (c.q < 0) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.blue);
			}
			g.fillOval(posL(c.posX)-5, posH(c.posY)-5, 10, 10);
		}
		
		/**
		 * Calcul et affichage des vecteurs
		 */
		for (int i= -affichage.getWidth()/2; i<affichage.getWidth()/2; i+= 20) {
			for (int j= -affichage.getHeight()/2; j<affichage.getHeight()/2; j+= 20) {
				v= new Vector<Double>(2);
				v.add(0, (double)0);
				v.add(1, (double)0);
				/**
				 * Calcul pour un point donne le champ resultant de toutes les charges (Theoreme de superposition
				 */
				for (Charge c : registreCharges) {
					w= c.valChamp(i, j);
					v.set(0, v.get(0)+w.get(0));
					v.set(1, v.get(1)+w.get(1));
				}
				/**
				 * Affichage du vecteur correspondant
				 */
				normeV= Math.pow(v.get(0)*v.get(0)+v.get(1)*v.get(1), 0.5);
				if (normeV < 90 && (int)normeV != 0 && (int)normeV != 1) {
					g.setColor(Color.black);
					g.drawLine(posL(i), posH(j), posL(i+v.get(0).intValue()), posH(j+v.get(1).intValue()));
					w= new Vector<Double>(2);
					w.add(0, -v.get(1)/v.get(0));
					w.add(1, (double)1);
					/**
					 * Fleche
					 */
					normeW= Math.pow(w.get(0)*w.get(0)+w.get(1)*w.get(1), 0.5);
					g.drawLine(posL(i+v.get(0).intValue()), posH(j+v.get(1).intValue()), posL((int)(i+0.8*v.get(0)+0.1*normeV*w.get(0)/normeW)), posH((int)(j+0.8*v.get(1)+0.1*normeV*w.get(1)/normeW)));
					g.drawLine(posL(i+v.get(0).intValue()), posH(j+v.get(1).intValue()), posL((int)(i+0.8*v.get(0)-0.1*normeV*w.get(0)/normeW)), posH((int)(j+0.8*v.get(1)-0.1*normeV*w.get(1)/normeW)));
				}
				v.clear();
			}
		}
	}
	
	/**
     * Methode permettant de faire l'affichage du potentiel electrostatique
     * 
     * @param g
     *		Objet Graphics du JPanel permettant de faire l'affichage au bon endroit de la fenetre 
     */
	public void peindrePotentiels (Graphics g) {
		int col;
		double val;
		/**
		 * Affichage pour chaque point du potentiel avec une couleur suivant une fonction exponentielle
		 */
		for (int i= -affichage.getWidth()/2; i<affichage.getWidth()/2; i++) {
			for (int j= -affichage.getHeight()/2; j<affichage.getHeight()/2; j++) {
				val= 0;
				/**
				 * Calcul
				 */
				for (Charge c : registreCharges) {
					val+= c.valPotentiel(i, j);
				}
				/**
				 * Calcul de la couleur associee et affichage
				 */
				col= (int)(255*Math.exp(-Math.abs(val)/10000));
				if (val < 0) {
					g.setColor(new Color(255, col, 255));
				} else {
					g.setColor(new Color(col, 255, col));
				}
				g.drawLine(posL(i), posH(j), posL(i), posH(j));
			}
		}
		/**
		 * Affichage des charges
		 */
		for (Charge c : registreCharges) {
			if (c.q < 0) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.blue);
			}
			g.fillOval(posL(c.posX)-5, posH(c.posY)-5, 10, 10);
		}
	}
	
	/**
     * Methode permettant de faire l'affichage des lignes de champ 
     * 
     * @param g
     *		Objet Graphics du JPanel permettant de faire l'affichage au bon endroit de la fenetre 
     */
	public void peindreLignes (Graphics g) {
		/**
		 * Fond blanc
		 */
		g.setColor(Color.white);
		g.fillRect(0, 0, affichage.getWidth(), affichage.getHeight());
		/**
		 * Affichage des charges
		 */
		for (Charge c : registreCharges) {
			if (c.q < 0) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.blue);
			}
			g.fillOval(posL(c.posX)-5, posH(c.posY)-5, 10, 10);
		}
		
		g.setColor(Color.black);
		int rayon= 20;
		double x, y;
		boolean bonEndroit= true;
		/**
		 * Selection des points de depart des lignes
		 */
		for (Charge c : registreCharges) {
			if (c.q > 0) {
				for (int i= 0; i<6; i++) {
					x= c.posX+rayon*Math.cos(Math.PI*i/(double)3);
					y= c.posY+rayon*Math.sin(Math.PI*i/(double)3);
					/**
					 * Verification d'un positionnement non problematoire
					 */
					for (Charge d : registreCharges) {
						if (Math.pow((x-d.posX)*(x-d.posX)+(y-d.posY)*(y-d.posY), 0.5) < rayon-1) {
							bonEndroit= false;
							break;
						}
					}
					if (bonEndroit) {
						ligne(x, y, g);
					}
					bonEndroit= true;
				}
			}
		}
	}
	
	/**
	 * Methode recursive qui fait concretement l'affichage d'un bout de ligne de champ a partir d'un point donne 
	 * 
	 * @param x
	 * 		Coordonnee horizontale du point de depart 
	 * @param y
	 * 		Coordonnee verticale du point de depart
	 * @param g
	 * 		Objet Graphics du JPanel permettant de faire l'affichage au bon endroit de la fenetre
	 */
	public void ligne (double x, double y, Graphics g) {
		Vector<Double> v= new Vector<Double>(2);
		Vector<Double> w= new Vector<Double>(2);
		int indent= 1;
		double normeV, nouvX, nouvY;
		boolean bonEndroit= true;
		v.add(0, (double)0);
		v.add(1, (double)0);
		/**
		 * Calcul de la valeur du champ
		 */
		for (Charge c : registreCharges) {
			w= c.valChamp((int)x, (int)y);
			v.set(0, v.get(0)+w.get(0));
			v.set(1, v.get(1)+w.get(1));
		}
		normeV= Math.pow(v.get(0)*v.get(0)+v.get(1)*v.get(1), 0.5);
		if (normeV == 0) {
			return;
		}
		/**
		 * Calcul de la position du prochain point 
		 */
		nouvX= x+indent*v.get(0)/normeV;
		nouvY= y+indent*v.get(1)/normeV;
		for (Charge d : registreCharges) {
			if (Math.pow((nouvX-d.posX)*(nouvX-d.posX)+(nouvY-d.posY)*(nouvY-d.posY), 0.5) < 19) {
				bonEndroit= false;
				break;
			}
		}
		/**
		 * Affichage d'une portion du trait puis nouvel appel de la methode pour finir le trait
		 */
		if (bonEndroit && posL((int)nouvX) < affichage.getWidth() && posL((int)nouvX) > 0 && posH((int)nouvY) < affichage.getHeight() && posH((int)nouvY) > 0) {
			g.drawLine(posL((int)x), posH((int)y), posL((int)nouvX), posH((int)nouvY));
			ligne(nouvX, nouvY, g);
		}
	}
	
	/**
	 * Implementation de la methode provenant de l'interface MouseListener, non utilisee dans ce cas mais obligatoirement reecrite
	 */
	public void mouseClicked (MouseEvent e) {}
	
	/**
	 * Implementation de la methode provenant de l'interface MouseListener, non utilisee dans ce cas mais obligatoirement reecrite
	 */
	public void mouseEntered (MouseEvent e) {}
	
	/**
	 * Implementation de la methode provenant de l'interface MouseListener, non utilisee dans ce cas mais obligatoirement reecrite
	 */
	public void mouseExited (MouseEvent e) {}
	
	/**
	 * Implementation de la methode provenant de l'interface MouseListener, non utilisee dans ce cas mais obligatoirement reecrite
	 */
	public void mouseReleased (MouseEvent e) {}
	
	/**
	 * Implementation de la methode provenant de l'interface MouseListener qui nous interesse dans notre cas pour detecter l'endroit ou l'utilisateur veut poser une charge
	 */
	public void mousePressed (MouseEvent e) {
		registreCharges.add(new Charge(e.getX()-affichage.getWidth()/2, -e.getY()+affichage.getHeight()/2, qActuel));
		affichage.repaint();
	}
	
	/**
	 * Cette classe permet de representer les charges facilement
	 * 
	 * @author Vincent Maffet
	 */
	public class Charge {
		
		/**
		 * posX est la coordonnee horizontale de la position de la charge
		 * posY est la coordonnee verticale de la position de la charge
		 * q est la valeur de la charge
		 */
		int posX, posY, q;
		
		/**
		 * Constructeur de la classe Charge, initialise les variables de classe
		 * 
		 * @param x
		 * 		position horizontale
		 * @param y
		 * 		position verticale
		 * @param qTemp
		 * 		valeur de la charge
		 */
		public Charge (int x, int y, int qTemp) {
			posX= x;
			posY= y;
			q= qTemp;
		}
		
		/**
		 * Methode permettant de calculer le champ electrostatique cree par la charge a un point de l'espace donne
		 * 
		 * @param x
		 * 		position horizontale du point ou calculer le champ
		 * @param y
		 * 		position verticale du point ou calculer le champ
		 * @return
		 * 		Vecteur a deux composantes contenant la valeur du champ electrostatique
		 */
		public Vector<Double> valChamp (int x, int y) {
			Vector<Double> res= new Vector<Double>(2);
			res.add(0, q*((x-posX)*Math.pow((x-posX)*(x-posX)+(y-posY)*(y-posY), -1.5)));
			res.add(1, q*((y-posY)*Math.pow((x-posX)*(x-posX)+(y-posY)*(y-posY), -1.5)));
			return res;
		}
		
		/**
		 * Methode permettant de calculer le potentiel cree en un point de l'espace par la charge par rapport a l'infini
		 * 
		 * @param x
		 * 		position horizontale du point ou calculer le potentiel
		 * @param y
		 * 		position verticale du point ou calculer le potentiel
		 * @return
		 * 		valeur du potentiel 
		 */
		public double valPotentiel (int x, int y) {
			return q*(Math.pow((x-posX)*(x-posX)+(y-posY)*(y-posY), -0.5));
		}
	}
}
