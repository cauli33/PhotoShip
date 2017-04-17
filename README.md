# PhotoShip


# Qu'est-ce que Photoship ?
Photoship est une application de traitement d'images, développée dans le cadre d'un projet étudiant. 


# À quoi sert cette application ?
Cette application permet à son utilisateur d'appliquer des filtres sur les images de sa galerie. Il peut récupérer ses images à partir de la galerie ou de la caméra de son appareil. Les filtres applicables sont parmi les suivants :
- Filtre gris ![Gris](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/gris.jpg)
- Filtre sépia ![Sepia](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/sepia.jpg)
- Filtre négatif ![Negatif](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/negatif.jpg)
- Filtre flou (moyenneur ou gaussien) ![Flou moyenneur](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/filtre_moyenne.jpg)
- Filtre sobel ![Sobel](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/sobel.jpg)
- Filtre Laplacien ![Lapla](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/laplacien.jpg)
- Égalisation d'histogramme ![EH](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/egalisationhistogramme.jpg)
- Extension de dynamiques ![ED](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/extensiondynamique.jpg)
- Sélection de couleurs ![Selec](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/une_couleur.jpg)
- Filtre coloré ![Couleur](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/rose.jpg)
- 3 effets de dessin crayonné plus ou moins marqué ![Crayon](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/dessin3.jpg)
- Effet cartoon ![Cartoon](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/cartoon.jpg)
- Modification de la teinte, la saturation ou la luminosité de l'image. ![HSV](https://github.com/cauli33/PhotoShip/blob/master/app/src/main/res/drawable/hsv.jpg)

Les filtres peuvent être appliqués automatiquement et intégralement à l'image, ou bien par zone définissable via le tracé du passage du doigt de l'utilisateur.

Des modifications concernant la structure de l'image sont également disponibles comme :
- Zoom en écartant ou rapprochant les doigts
- Rotation de l'image dans le sens horaire ou anti-horaire
- Miroir horizontal ou vertical de l'image
- Rognage de l'image
- Déformation de l'image.

L'utilisateur peut annuler ou rétablir les modifications grâce à l'implémentation d'un historique de modifications. Les images modifiées peuvent ainsi être enregistrés directement dans la galerie de l'appareil.


# Quel est l'historique de développement ?
Une première release de l'application a été déposée courant mars sur le dépôt. Elle comprenait une page d'accueil, un menu de chargement de photo (à partir de la galerie ou de la caméra de l'appareil) et un menu (très rudimentaire) comprenant autant de boutons que de transformations disponibles. Chacun de ses boutons mène à l'activité de transformation correspondante, où sont disponibles trois options : appliquer la modification, annuler la modification, sauvegarder l'image dans la galerie.

La seconde release est celle actuellement disponible sur le dépôt. Le code y a été optimisé, ne laissant plus que 3 activités au total de disponible : la page d'accueil (qui ne laisse apparaître que le logo de l'application une fraction de secondes), le menu de chargement de photo et le menu principal de transformation. Dans ce dernier, toutes les modifications sont disponibles en temps réel, en faisant apparaître et disparaître certains éléments comme des seekbars.

L'application a été développée grâce au logiciel Android Studio, et testée grâce à :
- un portable Motorola G2
- un portable Samsung S7
- un émulateur Asus Zenfone 2
- un émulateur Nexus S
- l'émulateur proposé par défaut par Android Studio.

L'API minimal utilisé pour tester l'application est l'API 22. Mais l'application fonctionne très bien pour toute API supérieure à 3.


# Comment installer l'application ?
Il suffit de cloner le repository git via la commande suivante: 

`git clone https://github.com/cauli33/PhotoShip.git`

# Qui sont les auteurs ?
Les auteurs du code sont trois étudiantes de L3 Mathématiques-Informatique de l'Université de Bordeaux, Collège Sciences et Technologies. 
- VEILLAT Emma
- HONORÉ Cauli
- BESSE Emma


# Qui contacter pour émettre une suggestion ?
Pour émettre une suggestion sur le code, vous pouvez nous contacter via nos profils github : 
- https://github.com/EmmaVeillat
- https://github.com/cauli33


