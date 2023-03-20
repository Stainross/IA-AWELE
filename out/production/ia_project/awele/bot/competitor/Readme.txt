La classe principale du bot est minmaxIAwaligator. 
S'il y a une erreur pour le temps de décision trop long, il faut modifier la fonction learn pour commenter la ligne : learnWithDynamicDepth();
Et decommenter la ligne : this.MAX_DEPTH = 8; (globalement une profondeur max égale à 2 de plus que le minmax initiale s'execute entre 0.9 à 1.5 fois plus de temps que Minmax)
Attention il ne faut pas choisir une valeur impaire pour la profondeur.
Cette archive contient aussi la version modifié de la classe main permettant de faire des threads. 