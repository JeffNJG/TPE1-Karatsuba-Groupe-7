package cm.groupe7.tpe1;

import java.util.LinkedList;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Hello world !");
		
		/*Scanner sc = new Scanner(System.in);
		System.out.println("Entrer la valeur de n :");
		int n = sc.nextInt();
		System.out.println(n);
		sc.close();
		int[] P = new int[n+1];
		int[] Q = new int[n+1];
		for (int i = 0; i<=n; i++) {
			System.out.println("Definition de P");
			Scanner s = new Scanner(System.in);
			System.out.println("Entrer le coefficient du monome de dégré " + i);
			P[i] = s.nextInt();
			s.close();
		}
		for (int i = 0; i<=n; i++) {
			System.out.println("Definition de Q");
			Scanner s = new Scanner(System.in);
			System.out.println("Entrer le coefficient du monome de dégré " + i);
			Q[i] = s.nextInt();
			s.close();
		}
		
		int[] res = karatsuba(P, Q);
		printPolynomial(res);*/
		
		int[] p = {4,-2,1};
		int[] q = {2,0,3};
		
		int[] r = karatsuba(p, q);
		printPolynomial(r);
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
			System.out.println("Cas de base");
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
		System.out.println("Creation");
		int[] A1 = new int[((A.length-1)/2)+1], A2 = new int[((A.length-1)/2)+1];
		int[] B1 = new int[((B.length-1)/2)+1], B2 = new int[((B.length-1)/2)+1];
		
		// Remplissage des Pi et Qi
		System.out.println("Remplissage");
		LinkedList<int[]> l = decompose(A);
		A1 = l.getFirst();
		A2 = l.getLast();
		l = decompose(B);
		B1 = l.getFirst();
		B2 = l.getLast();
		
		// Recursif
		System.out.println("Appels");
		int[] R1 = karatsuba(A1, B1);
		int[] R2 = karatsuba(A2, B2);
		int[] R3 = karatsuba(sumpoly(A1, A2), sumpoly(B1, B2));
		
		// Construction des resultats
		System.out.println("Resultats");
		int[] res = new int[A.length+B.length-1];
		System.out.println(res.length);
		int [] R4 = difpoly(R3, R1, R2);
		R4 = mulxpow(R4, (int)((A.length-1)/2));
		res = sumpoly(R1, R4);
		R2 = mulxpow(R2, A.length-1);
		res = sumpoly(res, R2);
		System.out.println(res.length);
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
		System.out.println();
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
}
