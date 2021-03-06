/*
 * Programacion Interactiva
 * Mini proyecto 3: Juego de poker clasico.
 */
package classicPoker;

import pokerView.*;
import classicPoker.Carta.Palos;
import classicPoker.Jugador.TipoJugador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.UIManager;
import java.awt.EventQueue;
import java.sql.Time;

/**
 * Clase que modela el juego de poker clasico.
 */
public class PokerGame implements Runnable {

    private Map<Jugador, Integer> mesaDeApuesta;
    private MazoDeCartas mazo;
    private int turnoPrimerJugador; // de tipo int o Jugador <- pendiente.
    private List<Jugador> jugadores;
    private List<Jugador> jugadoresBase;
    private int apuestaInicial;
    private int numeroRondaApuesta = 0;
    private PokerView pokerView;
    private boolean juegoNuevo = false;

    private Random aleatorio;

    /**
     * Instantiates a new poker game.
     */
    public PokerGame() {
        this.mesaDeApuesta = new HashMap<Jugador, Integer>();
        this.mazo = new MazoDeCartas();
        this.apuestaInicial = 1000;
        this.aleatorio = new Random();
        this.jugadores = new ArrayList<Jugador>();

        // crear los jugadores y darles el dinero inicial
        jugadoresBase = new ArrayList<Jugador>();
        jugadoresBase.add(new Jugador(getRandomMoney(), Jugador.TipoJugador.Simulado, mazo, "P1"));
        jugadoresBase.add(new Jugador(getRandomMoney(), Jugador.TipoJugador.Simulado, mazo, "P2"));
        jugadoresBase.add(new Jugador(getRandomMoney(), Jugador.TipoJugador.Simulado, mazo, "P3"));
        jugadoresBase.add(new Jugador(getRandomMoney(), Jugador.TipoJugador.Simulado, mazo, "P4"));
        jugadoresBase.add(new Jugador(getRandomMoney(), Jugador.TipoJugador.Usuario, mazo, "User"));

        CardImage.loadImage(this);
        Resources.loadCasino(getClass().getResourceAsStream("/fonts/CasinoFlat.ttf"));
        Resources.loadLounge(this);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                pokerView = new PokerView(jugadoresBase);
                for (Jugador jugador : jugadoresBase)
                    jugador.defineView(pokerView);
            }
        });
    }

    /**
     * Inicia el hilo de pokerGame y ejecuta los metodos en su interior.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(TimeControl.LOADING);

            while (pokerView == null)
                Thread.sleep(100);

            pokerView.initGUI();
            do {
                iniciarRonda();
            } while (juegoNuevo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Empieza la ronda de juego.
     * @throws InterruptedException
     */
    private void iniciarRonda() throws InterruptedException {
        pokerView.iniciarRonda();

        // Hacer la apuesta inicial
        pokerView.showBigMessage("Apuesta inicial: $" + apuestaInicial);
        for (Jugador jugador : jugadoresBase)
            mesaDeApuesta.put(jugador, jugador.aportar(apuestaInicial));

        pokerView.showMessage("Cada jugador ha hecho su apuesta inicial", TimeControl.APUESTA_INICIAL);
        pokerView.showBigMessage("Monto de la apuesta: " + getMontoApuestas());
        repartirCartas();

        // Selecciona el jugador “mano”.
        turnoPrimerJugador = aleatorio.nextInt(5);
        getOrdenJugadores(turnoPrimerJugador);
        pokerView.showMessage("Arranca el jugador " + jugadores.get(0).getName(), TimeControl.RONDA_DE_APUESTA);
        do {
            rondaDeApuesta();
        } while (!seHanIgualadoTodasLasApuestas() && !quedaUnJugador());
        if (!quedaUnJugador()) {
            rondaDeDescarte();
            do {
                rondaDeApuesta();
            } while (!seHanIgualadoTodasLasApuestas() && !quedaUnJugador());
        }
        determinarJuego();
    }

    /**
     * Reparte una determinada cantidad de cartas a cada jugador.
     */
    private void repartirCartas() {
        //Para pruebas.
        /*
        jugadoresBase.get(1).recibirCartas(mazo.manoEscaleraReal(Palos.corazones));
        jugadoresBase.get(0).recibirCartas(mazo.manoEscaleraReal(Palos.diamantes));
        jugadoresBase.get(2).recibirCartas(mazo.manoEscaleraReal(Palos.treboles));
        jugadoresBase.get(3).recibirCartas(mazo.manoEscaleraReal(Palos.corazones));
        jugadoresBase.get(4).recibirCartas(mazo.manoEscaleraReal(Palos.picas));
        */

        //jugadores.get(0).recibirCartas(mazo.sacarCartas(5));
        //jugadores.get(1).recibirCartas(mazo.sacarCartas(5));
        //jugadores.get(2).recibirCartas(mazo.sacarCartas(5));
        //jugadores.get(3).recibirCartas(mazo.sacarCartas(5));
        //jugadores.get(4).recibirCartas(mazo.sacarCartas(5));

        // Por defecto

        for (Jugador jugador : jugadoresBase)
            jugador.recibirCartas(mazo.sacarCartas(5));
    }

    /**
     * Controla el orden de los eventos de la ronda de apuesta.
     * @throws InterruptedException
     */
    private void rondaDeApuesta() throws InterruptedException {
        numeroRondaApuesta += 1;
        int apuestaMasAlta = Collections.max(mesaDeApuesta.values());
        boolean ultimaRonda = numeroRondaApuesta == 2 ? true : false;

        if (numeroRondaApuesta == 1)
            pokerView.showMessage("Empieza la ronda de apuesta", TimeControl.RONDA_DE_APUESTA);
        else
            pokerView.showMessage("Empieza otra ronda de apuesta", TimeControl.RONDA_DE_APUESTA);

        for (Jugador jugador : jugadores) {
            if (quedaUnJugador())
                break;
            if (jugador.seHaRetirado())
                continue;
            if (revisarDineroJugador(jugador))
                continue;
            if (apuestaMasAlta == apuestaInicial)
                mesaDeApuestaUpdate(jugador, jugador.apostar());
            else
                mesaDeApuestaUpdate(jugador, jugador.apostar(apuestaMasAlta - mesaDeApuesta.get(jugador), ultimaRonda));
            revisarDineroJugador(jugador);
            apuestaMasAlta = Collections.max(mesaDeApuesta.values());
            pokerView.showBigMessage("Monto de la apuesta: " + getMontoApuestas());
        }
    }

    /**
     * Controla el orden de los eventos de la ronda de descarte.
     * @throws InterruptedException
     */
    private void rondaDeDescarte() throws InterruptedException {
        pokerView.showMessage("Empieza la ronda de descarte", 1000);

        for (Jugador jugador : jugadores) {
            if (!jugador.seHaRetirado())
                jugador.descartar();
        }
        numeroRondaApuesta = 0;
    }

    /**
     * Muestra el contenido de las cartas de los jugadores.
     */
    private void descubrirCartas() {
        for (Jugador jugador : jugadores) {
            if (!jugador.seHaRetirado())
                jugador.descubrirCartas();
        }
    }

    /**
     * Determina el o los ganadores y reparte el premio.
     * @throws InterruptedException
     */
    private void determinarJuego() throws InterruptedException {
        List<Jugador> ganadores = new ArrayList<Jugador>();
        List<Jugador> enJuego = new ArrayList<Jugador>();

        descubrirCartas();

        for (Jugador jugador : jugadores) {
            if (!jugador.seHaRetirado())
                enJuego.add(jugador);
        }

        //# Determinar ganador/ganadores.

        if (enJuego.size() == 1)
            ganadores = enJuego;

        Jugador auxiliar;
        for (int i = 0; i < enJuego.size() - 1; i++) {
            if (i == 0)
                auxiliar = PokerRules.determinarMano(enJuego.get(0), enJuego.get(1));
            else
                auxiliar = PokerRules.determinarMano(ganadores.get(ganadores.size() - 1), enJuego.get(i + 1));

            if (auxiliar != null) { // Posible unico ganador.
                if (i == 0)
                    ganadores.add(auxiliar);
                if (auxiliar == enJuego.get(i + 1)) { // ha ganado el jugador de la lista enJuego.
                    ganadores.add(auxiliar);
                    if (ganadores.size() > 1) { // eliminar los posibles ganadores anteriores.
                        for (int j = ganadores.size() - 2; j >= 0; j--)
                            ganadores.remove(j);
                    }
                }
            } else if (auxiliar == null) { // empate, se agregan los dos jugadores a la lista de ganadores.
                if (i == 0) {
                    ganadores.add(enJuego.get(0));
                    ganadores.add(enJuego.get(1));
                } else
                    ganadores.add(enJuego.get(i + 1));
            }
        }

        //# Repartir el premio.
        int dineroARecibir = getMontoApuestas();

        if (ganadores.size() == 1) {
            if (ganadores.get(0).getTipo() == TipoJugador.Simulado) {
                pokerView.showBigMessage("¡El jugador " + ganadores.get(0).getName() + " ha ganado!");
                pokerView.showMessage("recibe " + dineroARecibir, 0);
            } else {
                pokerView.showBigMessage("¡Has ganado!");
                pokerView.showMessage("Recibes " + dineroARecibir, 0);
            }
            ganadores.get(0).recibirDinero(dineroARecibir);
        } else {
            String nombres = "";
            dineroARecibir /= ganadores.size();

            for (Jugador jugador : ganadores) {
                jugador.recibirDinero(dineroARecibir);
                nombres += jugador.getName() + " ";
            }
            pokerView.showBigMessage("Los jugadores " + nombres + "han ganado!");
            pokerView.showMessage("Recibe " + dineroARecibir + " cada uno", 0);
        }
        jugarDeNuevo();
    }

    /**
     * Realiza las modificaciones necesarias en caso de jugar de nuevo.
     * @throws InterruptedException
     */
    private void jugarDeNuevo() throws InterruptedException {
        if (pokerView.nuevaRonda()) {
            for (Jugador p : jugadores)
                p.jugarDeNuevo();
            mesaDeApuesta.clear();
            mazo.combinarMazos();
            this.numeroRondaApuesta = 0;
            this.juegoNuevo = true;
        }
    }

    /**
     * Obtiene el monto de apuestas de la mesa de apuesta.
     * @return la suma de los valores de la mesa de apuestas.
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

    /**
     * Determinar si todas las apuestan son iguales.
     * @return true si se han igualado las apuestas, false en caso contrario.
     */
    private boolean seHanIgualadoTodasLasApuestas() {
        int apuestaMasAlta = Collections.max(mesaDeApuesta.values());
        for (Jugador jugador : jugadores)
            if (!jugador.seHaRetirado())
                if (mesaDeApuesta.get(jugador) != apuestaMasAlta)
                    return false;
        return true;
    }

    /**
     * Determinar si queda un jugador en juego.
     * @return true si 4 de los jugadores se han retirado.
     */
    private boolean quedaUnJugador() {
        int contador = 0;
        for (Jugador jugador : jugadores)
            if (!jugador.seHaRetirado())
                contador++;
        return contador == 1;
    }

    /**
     * Determinar si el jugador se ha quedado sin dinero.
     * @param jugador el jugador ha revisar.
     * @return true si el jugador se ha quedado sin dinero.
     * @throws InterruptedException
     */
    private boolean revisarDineroJugador(Jugador jugador) throws InterruptedException {
        if (jugador.getDinero() == 0) {
            jugador.retirarse();
            if (jugador.getTipo() == TipoJugador.Usuario)
                pokerView.showMessage("Has perdido.", TimeControl.TIEMPO_MENSAJE_PERDER);
            return true;
        }
        return false;
    }

    /**
     * @return Una cantidad aleatoria de dinero entre el min y el max.
     */
    private int getRandomMoney() {
        int MAX_MONEY = 70000;
        int MIN_MONEY = 30000;
        return (aleatorio.nextInt(MAX_MONEY) + MIN_MONEY) / 100 * 100;
    }

    /**
    * Actualiza el valor de la apuesta de un jugador en la mesaDeApuesta.
    * @param jugador jugador que hace la apuesta.
    * @param val valor de la apuesta.
    */
    private void mesaDeApuestaUpdate(Jugador jugador, int val) {
        mesaDeApuesta.replace(jugador, mesaDeApuesta.get(jugador) + val);
    }

    /**
     * Ordena la lista de jugadores de acuerdo al jugador mano.
     * @param primerTurno posicion del jugador mano en la lista base de jugadores.
     */
    private void getOrdenJugadores(int primerTurno) {
        List<Jugador> nuevaListaJugadores = new ArrayList<Jugador>();

        List<Jugador> aux1 = jugadoresBase.subList(primerTurno, jugadoresBase.size());
        List<Jugador> aux2 = jugadoresBase.subList(0, primerTurno);

        nuevaListaJugadores.addAll(aux1);
        nuevaListaJugadores.addAll(aux2);

        jugadores = nuevaListaJugadores;
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
