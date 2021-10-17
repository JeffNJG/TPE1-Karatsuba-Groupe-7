# TPE1-Karatsuba-Groupe-7

## Implémentation

Détails sur les méthodes :
 - karatsuba : l'algoritme tel que connu.
 - decompose : nous renvoit une liste chaînée avec deux polynômes de degré n/2 lorsqu'on lui a passé un polynôme de degré n.
 - sumpoly : : permet d'effectuer la somme de deux polynômes.
 - difpoly : permet de réaliser la différence R3 - R2 - R1. Elle prend donc trois polynômes en entrée.
 - max : détermine le maximum entre deux grandeurs entières.
 - greater : détermine le polynôme de plus haut degré.
 - mulxpow : permet de multiplier un polynôme par une puissance de x. On prend en entrée un polynôme et la puissance et on retourne un polynôme. On passe de x+2 à x³ + 2x² lorsque la puissance passée est 2.
 - mute : elle permet de passer de modifier la repésentation d'un polynôme afin de faciliter l'addition de deux polynômes de degré différent. Par exemple, on passe de 2x+3 à 0x³ + 0x² + 2x + 3.
 - equals : permet de tester l'égalité de deux polynômes. La valeur de retour est soit true soit false.
 - printPolynomial : sert à afficher de façon plus esthétique un polynôme.
