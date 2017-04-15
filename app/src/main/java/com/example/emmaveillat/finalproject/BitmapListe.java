package com.example.emmaveillat.finalproject;

/**
 * La classe  BitmapList est une classe permettant la création d'une liste d'images ayant lieu
 * d'historique de modifications.
 * Ainsi, l'utilisateur peut "revenir en arrière" dans ses modifications ou bien retrouver la plus
 * récente.
 * La liste d'images est limitée par sa taille, ce qui efface de la mémoire les modifications trop
 * anciennes.
 * De plus, si l'utilisateur est revenu sur une modification antérieure et en a appliqué une
 * nouvelle, il ne peut retrouver celles qu'il a effectuées auparavant.
 */
public class BitmapListe {

    /**
     * position actuelle dans le tableau qui correspond à l'image visible par l'utilisateur dans
     * l'application.
     */
    public int courant;

    /**
     * taille de la liste de bitmaps utilisée. Elle est limitée à 64.
     */
    private int tailleMax = 64;

    /**
     * liste de bitmaps de taille maixesize = 64.
     */
    MonBitmap [] liste = new MonBitmap[tailleMax];

    /**
     * maximum d'images stockées dans la liste actuellement. Cela correspond aux modifications
     * réalisées après un "retour en arrière" par l'utilisateur.
     */
    public int maxNbImgConnu;

    /**
     * nombre d'images à supprimer de la liste si sa mémoire est pleine.
     */
    private int jetable = 16;

    /**
     * fonction qui initialise la liste à partir d'une première image. La position courante dans le
     * tableau correspond donc à l'image visible par l'utilisateur et il n'y a pas plus d'image
     * modifiée antérieurement par l'utilisateur.
     * @param bmp la première image stockée dans la liste
     */
    public BitmapListe(MonBitmap bmp){
        liste[0] = bmp;
        courant = 0;
        maxNbImgConnu = 0;
    }

    /**
     * fonction qui libère la mémoire de la liste si celle-ci est pleine. Elle décale de 16
     * éléments toute la liste vers la gauche et place la position actuelle à 16 places à gauche.
     * Les modifications suivantes écraseront celles placées entre les places 47 et 63.
     */
    private void liberationMemoire(){
        for (int i = 0; i< tailleMax - jetable; i++){
            liste[i] = liste[jetable +i];

        }
        courant = tailleMax - jetable;
    }

    /**
     * fonction qui donne l'image contenant la dernière modification effectuée avant l'image
     * actuelle.
     * Le curseur courant est donc placée sur la position précédente et on retourne l'image
     * correspondante.
     * @return l'image contenant les modifications précédentes
     */
    public MonBitmap getPrecedent(){
        courant--;
        return liste[courant];
    }

    /**
     * fonction qui donne l'image vue actuellement par l'utilisateur.
     * @return l'image actuelle
     */
    public MonBitmap getCourant(){
        return liste[courant];
    }

    /**
     * fonction qui donne l'image suivante dans la liste, c'est-à-dire l'image avec des
     * modifications plus récentes que celle actuellement vue par l'utilisateur.
     * @return l'image suivante dans la liste
     */
    public MonBitmap getSuivant(){
        courant++;
        return liste[courant];
    }

    /**
     * fonction qui ajoute une nouvelle image modifiée à la liste et actualise ainsi sa position.
     * Si la liste est pleine, la fonction fait appelle à "freespace" pour libérer de la mémoire.
     * Sinon, l'image est ajoutée après l'image actuelle et la valeur max d'images dans la liste et
     * le curseur prennent leur valeur +1.
     * @param bmp l'image à ajouter dans la liste
     */
    public void setSuivant(MonBitmap bmp){
        if (courant == tailleMax){
            liberationMemoire();
        }
        else{
            courant++;
            liste[courant] = bmp;
            maxNbImgConnu = courant;
        }
    }
}
