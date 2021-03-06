package cm.groupe7.tpe1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Multiplication de deux polynômes");
		
		System.out.println("Entrez la valeur du degré du premier polynôme : ");
		Scanner in = new Scanner(System.in);
		int n1 = in.nextInt();
		System.out.println("Entrez la valeur du degré du second polynôme : ");
		int n2 = in.nextInt();
		System.out.println("Entrez le majorant de l'intervalle où les coefficients seront choisis aléatoirement : ");
		int x = in.nextInt();
		in.close();
		
		System.out.println("Génération des polynômes...");
		int[] p = polynomialGenerator(n1, x);
		int[] q = polynomialGenerator(n2, x);
		String pText = stringRepresentation(p);
		String qText = stringRepresentation(q);
		System.out.println("Génération terminée.");
		System.out.println("Le premier polynome est : " + pText);
		System.out.println("\nLe second est : " + qText);
		
		// On ramène les polynômes au même degré
		if (p.length != q.length) {
			if (equals(greater(p, q), p)) {
				q = mute(q, p.length);
			}
			if (equals(greater(p, q), q)) {
				p = mute(p, q.length);
			}
		}
		
		// Si p et q ont des degrés qui ne sont pas des puissances de 2 on complète avec des 0 pour faciliter les calculs
		if((p.length-1)%2 != 0) {
			p = mute(p, nextPowerTwo(p.length)+1);
		}
		if((q.length-1)%2 != 0) {
			q = mute(q, nextPowerTwo(q.length)+1);
		}
		
		// Variables pour estimer la durée
		long d1, f1, d2, f2, dureeNaif, dureeKara;
		
		d1 = System.nanoTime();
		int[] r1 = naif(p, q);
		f1 = System.nanoTime();
		String r1Text = stringRepresentation(r1);
		dureeNaif = f1 - d1;
		System.out.println("Résultat par la méthode naïve : " + r1Text);
		System.out.println("\nDurée d'exécution de l'algorithme naïf " + dureeNaif);
		d2 = System.nanoTime();
		int[] r2 = karatsuba(p, q);
		f2 = System.nanoTime();
		String r2Text = stringRepresentation(r2);
		dureeKara = f2 - d2;
		System.out.println("Résultat par la méthode de Karatsuba : " + r2Text);
		System.out.println("\nDurée d'exécution de l'algorithme de Karatsuba " + dureeKara);
		
		// On stocke les résultats dans un fichier
		try {
			FileWriter myWriter = new FileWriter("results.txt");
		    myWriter.write("MULTIPLICATION DE DEUX POLYNÔMES P(x) ET Q(x) \n");
			myWriter.write("P(x) = " + pText + "\n");
			myWriter.write("Q(x) = " + qText + "\n");
			myWriter.write("Résultat par la méthode naïve : \n");
			myWriter.write("R1(x) = " + r1Text +"\n");
			myWriter.write("Temps d'exécution en nanosecondes : " + dureeNaif + "\n");
			myWriter.write("Résultat par la méthode de Karatsuba : \n");
			myWriter.write("R2(x) = " + r2Text +"\n");
			myWriter.write("Temps d'exécution en nanosecondes : " + dureeKara);
			myWriter.close();
		    System.out.println("Les résultats sont disponibles dans le fichier results.txt.");
		} catch (IOException e) {
			System.out.println("Une erreur est survenue.");
		    e.printStackTrace();
		}
	}
	
	/**
	 * Algorithme naif pour la multiplication de deux polynomes
	 * @param a un polynome
	 * @param b un autre polynome
	 * @return le produit des deux polynomes
	 */
	public static int[] naif(int[] a, int[] b) {
		int[] res = new int[a.length+b.length-1];
		for (int i=0; i<res.length; i++) {
			res[i] = 0;
		}
		for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                res[i + j] += a[i] * b[j];
            }
        }
		return res;
	}
	
	/**
	 * Algorithme de Karatsuba
	 * @param A : un polynome
	 * @param B : un autre polynome
	 * @return le produit des deux premiers
	 */
	public static int[] karatsuba(int[] A, int[] B) {
		if (A.length == 2 && B.length == 2) {
			// cas de base, calcul des coefficients du polynome resultant
			int[] res = new int[3];
			int R1 = A[0]*B[0];
			int R2 = A[1]*B[1];
			int R3 = (A[0]+A[1])*(B[0]+B[1]);
			res[2] = R2;
			res[1] = R3-R1-R2;
			res[0] = R1;
			return res;
		}
		
		// creation des Pi et Qi
		int[] A1 = new int[((A.length-1)/2)+1], A2 = new int[((A.length-1)/2)+1];
		int[] B1 = new int[((B.length-1)/2)+1], B2 = new int[((B.length-1)/2)+1];
		
		// Remplissage des Pi et Qi
		LinkedList<int[]> l = decompose(A);
		A1 = l.getFirst();
		A2 = l.getLast();
		l = decompose(B);
		B1 = l.getFirst();
		B2 = l.getLast();
		
		// Recursif
		int[] R1 = karatsuba(A1, B1);
		int[] R2 = karatsuba(A2, B2);
		int[] R3 = karatsuba(sumpoly(A1, A2), sumpoly(B1, B2));
		
		// Construction des resultats
		int[] res = new int[A.length+B.length-1];
		int [] R4 = difpoly(R3, R1, R2);
		R4 = mulxpow(R4, (int)((A.length-1)/2));
		res = sumpoly(R1, R4);
		R2 = mulxpow(R2, A.length-1);
		res = sumpoly(res, R2);
		return res;
	}
	
	/**
	 * Permet de décomposer un polynomes de taille n en deux polynomes de taille n/2
	 * @param X : le polynome
	 * @return une liste avec les deux polynomes resultants
	 */
	public static LinkedList<int[]> decompose(int[] X) {
		LinkedList<int[]> res = new LinkedList<int[]>();
		int[] A1 = new int[((X.length-1)/2)+1], A2 = new int[((X.length-1)/2)+1];
		int i=0;
		while (i<A1.length-1) {
			A1[i] = X[i];
			i+=1;
		}
		A1[i] = 0;
		int t= i;
		while (i<X.length) {
			A2[i-t] = X[i];
			i+=1;
		}
		res.add(A1);
		res.add(A2);
		return res;
	}
	
	/**
	 * Permet de faire la somme de deux polynomes independamment de leur degré respectif
	 * @param a : le premier polynome
	 * @param b : le second
	 * @return le poynome résultant
	 */
	public static int[] sumpoly(int[] a, int[] b) {
		if (a.length == b.length) {
			int[] res = new int[a.length];
			for (int i = 0; i<a.length; i++) {
				res[i] = a[i] + b[i];
			}
			return res;
		}
		else {
			int[] res = new int[max(a.length, b.length)];
			if (equals(greater(a, b), a)) {
				b = mute(b, a.length);
			}
			if (equals(greater(a, b), b)) {
				a = mute(a, b.length);
			}
			for (int i = 0; i<a.length; i++) {
				res[i] = a[i] + b[i];
			}
			return res;
		}
	}
	
	/**
	 * Sert à effectuer l'opération R3 - R2 - R1
	 * @param a : R3
	 * @param b : R2
	 * @param c : R1
	 * @return le polynome resultant
	 */
	public static int[] difpoly(int[] a, int[] b, int[] c) {
		int[] res = new int[a.length];
		for (int i = 0; i<a.length; i++) {
			res[i] = a[i] - b[i] - c[i];
		}
		return res;
	}
	
	/**
	 * Determine le maximum entre dexu entiers naturels
	 * @param a : un entier
	 * @param b : un autre entier
	 * @return le maximum
	 */
	public static int max(int a, int b) {
		if(a==b) {
			return a;
		} else if (a<b) {
			return b;
		} else {
			return a;
		}
	}
	
	/**
	 * Determine le polynome de plus haut degré
	 * @param a : un polynome
	 * @param b : un polynome
	 * @return le polynome de plus haut degré
	 */
	public static int[] greater(int[] a, int[] b) {
		int n = max(a.length, b.length);
		if (n==a.length) {
			return a;
		} else {
			return b;
		}
	}
	
	/**
	 * Cette fonction me permet de multiplier un polynome par une puissance de x
	 * Par exemple, (2x+4) * x² = 2x³+4x²
	 * Elle intervient lors de la restitution du résultat final
	 * @param a : un polynôme
	 * @param n : le dégré de la puissance de x
	 * @return un polynôme de dégré d(a) + n
	 */
	public static int[] mulxpow(int[] a, int n) {		
		int[] res = new int[a.length+n];
		int i = 0;
		while (i<a.length) {
			res[i+n] = a[i];
			i += 1;
		}
		i = 0;
		while (i<n) {
			res[i] = 0;
			i+=1;
		}
		return res;
	}
	
	/**
	 * Sert à rajouter des 0 pour passer de 2x+3 à 0x³ + 0x² + 2x + 3 par exemple
	 * Utile pour l'addition de deux polynomes de dégrés différents
	 * @param a : le polynome à compléter
	 * @param n : le degré du polynome à obtenir
	 * @return le polynome resultant
	 */
	public static int[] mute(int[] a, int n) {
		if(a.length == n || a.length > n) {
			return a;
		}
		else {
			int[] res = new int[n];
			int i = 0;
			while (i<a.length) {
				res[i] = a[i];
				i+=1;
			}
			while (i<n) {
				res[i] = 0;
				i+=1;
			}
			return res;
		}
	}
	
	/**
	 * Permet de vérifier l'égalité de deux polynômes
	 * @param a : le premier polynome
	 * @param b : le second polynome
	 * @return le resultant du test
	 */
	public static boolean equals(int[] a, int[] b) {
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			if (a[i]!=b[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Permet d'afficher de facon plus esthétique un polynôme
	 * @param a un tableau représentant un polynôme
	 */
	public static void printPolynomial(int[] a) {
		int i = a.length-1;
		while (i>=0) {
			if (i==0) {
				System.out.print(a[i]);
				break;
			}
			if(a[i] == 0) {
				i -= 1;
				continue;
			}
			if(a[i] == 1) {
				System.out.print("x^" + i + "+");
				i-=1;
				continue;
			}
			System.out.print(a[i] + "x^" + i + "+");
			i-=1;
		}
	}
	
	/**
	 * Nous renvoit une représentation textuelle du polynome
	 * Celle qu'on écrira plus tard dans le fichier
	 * @param a : le tableau de coefficients représentant le polynôme
	 * @return La représentation
	 */
	public static String stringRepresentation(int[] a) {
		int i = a.length-1;
		String res = "";
		while (i>=0) {
			if (i==0) {
				res += a[i];
				break;
			}
			if(a[i] == 0) {
				i -= 1;
				continue;
			}
			if(a[i] == 1) {
				res = res + "x^" + i + "+";
				i-=1;
				continue;
			}
			res = res + a[i] + "x^" + i + "+";
			i-=1;
		}
		return res;
	}
	
	/**
	 * On génère un polynôme avec des entiers de manière aléatoire, le but étant de construire rapidement un polynôme de n'importe quel ordre
	 * L'objet Random permet de créer un objet aléatoire et sa méthode nextInt nous permet d'obtenir un entier aléatoire
	 * @param n la degré du polynôme qu'on veut obtenir
	 * @param o l'ordre de grandeur des entiers. Les entiers générés seront compris entre 0 et o
	 * @return le polynôme généré
	 */
	public static int[] polynomialGenerator(int n, int o) {
		int[] res = new int[(int) (n+1)];
        Random rand = new Random();
        for (int i = 0; i<n; i++) {
        	res[i] = rand.nextInt(o);
        }
		return res;
	}
	
	/**
	 * Méthode qui permet d'obtenir la puissance de 2 directement supérieure à n
	 * @param n un entier
	 * @return la prochaine puissance de 2
	 */
	public static int nextPowerTwo(int n) {
		int res = 1;
		while (res < n) {
			res = res*2;
		}
		return res;
	}
}

