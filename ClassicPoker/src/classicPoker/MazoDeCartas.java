/*
 * Programacion Interactiva
 * Autor: Julian Andres Orejuela Erazo - 1541304 
 * Autor: Daniel Felipe Velez Cuaical - 1924306
 * Mini proyecto 3: Juego de poker clasico.
 */
package classicPoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import classicPoker.Carta.Palos;

/**
 * Clase que modela un mazo de cartas.
 */
public class MazoDeCartas {
    private List<Carta> cartas;
    private List<Carta> mazoDescarte;

    /**
     * Instantiates a new mazo de cartas.
     */
    public MazoDeCartas() {
        this.cartas = new ArrayList<Carta>();
        this.mazoDescarte = new ArrayList<Carta>();
        crearBaraja();
        revolverMazo();
    }

    /**
     * Crea una baraja de cartas organizada.
     */
    public void crearBaraja() {
        for (Carta.Palos palo : Carta.Palos.values()) //crear las cartas de cada palo
            for (int i = 2; i <= 14; i++)
                cartas.add(new Carta(i, palo));
    }

    /**
     * Revuelve el mazo de manera aleatoria.
     */
    public void revolverMazo() {
        Collections.shuffle(cartas);
    }

    /**
     * retorna una lista de n cartas.
     * @param n
     */
    //! qué pasa si se intentan sacar 0 cartas?
    public List<Carta> sacarCartas(int n) {
        List<Carta> mano = new ArrayList<Carta>();
        mano.addAll(cartas.subList(0, n));
        cartas.removeAll(mano);
        return mano;
    }

    public Carta sacarCarta() {
        return cartas.remove(0);
    }

    /**
     * agrega una lista de cartas para descartar al mazoDescarte.
     * @param cartas 
     */
    public void descartar(List<Carta> cartas) {
        for (Carta carta : cartas)
            mazoDescarte.add(carta);
    }

    /**
     * agrega una carta para descartar al mazoDescarte.
     * @param carta
     */
    public void descartar(Carta carta) {
        mazoDescarte.add(carta);
    }



    public static List<Carta> manoColor(Palos palo) {
        List<Carta> mano = new ArrayList<Carta>();

        for (int i = 0; i < 5; i++)
            mano.add(new Carta(i, palo));
        return mano;
    }

    

}
