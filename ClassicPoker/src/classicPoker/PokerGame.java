/*
 * Programacion Interactiva
 * Autor: Julian Andres Orejuela Erazo - 1541304 
 * Autor: Daniel Felipe Velez Cuaical - 1924306
 * Mini proyecto 3: Juego de poker clasico.
 */

package classicPoker;

import pokerView.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.UIManager;

import classicPoker.Carta.Palos;
import classicPoker.Jugador.TipoJugador;

import java.awt.EventQueue;

/**
 * Clase que modela el juego de poker clasico.
 */
public class PokerGame implements Runnable {

    private Map<Jugador, Integer> mesaDeApuesta;
    private MazoDeCartas mazo;
    private int turno; // de tipo int o Jugador <- pendiente.
    private List<Jugador> jugadores;
    private int apuestaInicial;
    // private int apuestaMasAlta;
    private int numeroRonda = 0;
    private PokerView pokerView;

    private Random aleatorio;

    /**
     * Instantiates a new poker game.
     */
    public PokerGame() {
        this.mesaDeApuesta = new HashMap<Jugador, Integer>();
        this.mazo = new MazoDeCartas();
        this.apuestaInicial = 1000;
        this.aleatorio = new Random();

        // crear los jugadores y darles el dinero inicial
        jugadores = new ArrayList<Jugador>();
        jugadores.add(new Jugador(getRandomMoney(), Jugador.TipoJugador.Simulado, mazo, "P1"));
        jugadores.add(new Jugador(getRandomMoney(), Jugador.TipoJugador.Simulado, mazo, "P2"));
        jugadores.add(new Jugador(getRandomMoney(), Jugador.TipoJugador.Simulado, mazo, "P3"));
        jugadores.add(new Jugador(getRandomMoney(), Jugador.TipoJugador.Simulado, mazo, "P4"));
        jugadores.add(new Jugador(getRandomMoney(), Jugador.TipoJugador.Usuario, mazo, "User"));

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                pokerView = new PokerView(jugadores);

                for (Jugador jugador : jugadores)
                    jugador.defineView(pokerView);
            }
        });
    }

    /**
     * 
     */
    @Override
    public void run() {
        try {
            while (pokerView == null)
                Thread.sleep(100);

            iniciarRonda();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @throws InterruptedException
     */
    private void iniciarRonda() throws InterruptedException {
        // pokerView.iniciarRonda(); //# TEMPORAL

        // hacer la apuesta inicial
        for (Jugador jugador : jugadores)
            mesaDeApuesta.put(jugador, jugador.aportar(apuestaInicial));

        pokerView.showBigMessage("Monto de la apuesta: " + getMontoApuestas());
        pokerView.showMessage("Apuesta inicial: $" + apuestaInicial, TimeControl.apuestaInicial);
        // pokerView.showMessage("Cada jugador ha hecho su apuesta inicial", 2000);

        repartirCartas();

        // ! voy a comentar shuffle para que el orden de los turnos siempre sea el
        // mismo,
        // ! mientras hacemos las pruebas
        // selecciona el jugador “mano”
        // Collections.shuffle(jugadores); // ! no se tiene en cuenta lo del jugador a
        // la derecha

        /*
        do {
            rondaDeApuesta();
        } while (!seHanIgualadoTodasLasApuestas());
        */
        rondaDeDescarte();
        /*
        do {
            rondaDeApuesta();
        } while (!seHanIgualadoTodasLasApuestas());
        */
        descubrirCartas();
        determinarJuego();
    }

    /**
     * 
     */
    private void repartirCartas() {
        for (Jugador jugador : jugadores)
            jugador.recibirCartas(mazo.sacarCartas(5));

        //jugadores.get(4).recibirMano(MazoDeCartas.manoEscaleraColor(Palos.corazones));
        //jugadores.get(3).recibirMano(MazoDeCartas.manoColor(Palos.diamantes));
    }

    /**
     * 
     * @throws InterruptedException
     */
    private void rondaDeApuesta() throws InterruptedException {
        numeroRonda += 1;
        int apuestaMasAlta = Collections.max(mesaDeApuesta.values());
        boolean ultimaRonda = numeroRonda == 2 ? true : false;

        if (numeroRonda == 1)
            pokerView.showMessage("Empieza la ronda de apuesta", TimeControl.rondaDeApuesta);
        else
            pokerView.showMessage("Empieza otra ronda de apuesta", TimeControl.rondaDeApuesta);

        for (Jugador jugador : jugadores) {
            if (jugador.seHaRetirado())
                continue;

            if (apuestaMasAlta == apuestaInicial)
                mesaDeApuestaUpdate(jugador, jugador.apostar(ultimaRonda));
            else
                mesaDeApuestaUpdate(jugador, jugador.apostar(apuestaMasAlta - mesaDeApuesta.get(jugador), ultimaRonda));

            apuestaMasAlta = Collections.max(mesaDeApuesta.values());
            pokerView.showBigMessage("Monto de la apuesta: " + getMontoApuestas());
        }
    }

    /**
     * 
     * @throws InterruptedException
     */
    private void rondaDeDescarte() throws InterruptedException {
        pokerView.showMessage("Empieza la ronda de descarte", 1000);

        for (Jugador jugador : jugadores) {
            if (!jugador.seHaRetirado())
                jugador.descartar();
        }
        numeroRonda = 0;
    }

    /**
     * Muestra el contenido de las cartas de los jugadores.
     */
    private void descubrirCartas() {
        for (Jugador jugador : jugadores)
            jugador.descubrirCartas();
    }

    private void determinarJuego() throws InterruptedException {
        List <Jugador> enJuego = new ArrayList<Jugador>();
        List <Carta> manoAuxiliar = new ArrayList<Carta>();
        Jugador ganador = null;

        for (Jugador jugador : jugadores) {
            if (!jugador.seHaRetirado()) 
                enJuego.add(jugador);
        }

        for (int i = 1; i<enJuego.size(); i++) {
            if (i == 1) 
                manoAuxiliar = PokerRules.determinarMano(enJuego.get(0).getMano(), enJuego.get(i).getMano());
            else 
                manoAuxiliar = PokerRules.determinarMano(ganador.getMano(), enJuego.get(i).getMano());
            ganador = buscarJugador(enJuego, manoAuxiliar);
        }

        int dineroARecibir = getMontoApuestas();
        if (ganador.getTipo() == TipoJugador.Simulado) {
            pokerView.showBigMessage("¡El jugador " + ganador.getName() + " ha ganado!");
            pokerView.showMessage("recibe " + dineroARecibir, 0);
        }
        else {
            pokerView.showBigMessage("¡Has ganado!");
            pokerView.showMessage("Recibes " + dineroARecibir, 0);
        }
        ganador.recibirDinero(dineroARecibir); 
            
        for (Jugador p : jugadores) 
            mesaDeApuesta.replace(p, 0); //# ¿Qué pasa con el dinero de los que se retiran?.
    }

    /**
     * Busca en una lista de jugadores el jugador al cual le pertenece una mano dada.
     * @param jugadores
     * @param mano
     * @return jugador al que le pertenece la mano especificada, null si se encuentra.
     */
    private Jugador buscarJugador(List<Jugador> jugadores, List<Carta> mano) {
        for (Jugador jugador : jugadores) {
            if (jugador.getMano() == mano)
                return jugador;
        }
        return null;
    }

    /**
     * Obtiene el monto de apuestas de la mesa de apuesta.
     * @return la suma de los valores de la mesa de apuestas
     */
    private int getMontoApuestas() {
        int monto = 0;
        for (Integer val : mesaDeApuesta.values())
            monto += val;
        return monto;
    }

    // #---------------------------------------------------------------------------
    // # FUNCIONES AUXILIARES
    // #---------------------------------------------------------------------------

    private boolean seHanIgualadoTodasLasApuestas() {
        int apuestaMasAlta = Collections.max(mesaDeApuesta.values());
        for (Jugador jugador : jugadores)
            if (!jugador.seHaRetirado())
                if (mesaDeApuesta.get(jugador) != apuestaMasAlta)
                    return false;
        return true;
    }

    /**
     * Devuelve una cantidad aleatoria de dinero entre el min y el max.
     */
    private int getRandomMoney() {
        int MAX_MONEY = 70000;
        int MIN_MONEY = 30000;
        return (aleatorio.nextInt(MAX_MONEY) + MIN_MONEY) / 100 * 100;
    }

    /**
    * Actualiza el valor de la apuesta de un jugador en la mesaDeApuesta
    * @param jugador
    * @param val
    */
    private void mesaDeApuestaUpdate(Jugador jugador, int val) {
        mesaDeApuesta.replace(jugador, mesaDeApuesta.get(jugador) + val);
    }

    /**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		try {
			String className = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(className);
		} catch (Exception e) {
		}

		Thread pokerGame = new Thread(new PokerGame());
        pokerGame.start();
	}
}
