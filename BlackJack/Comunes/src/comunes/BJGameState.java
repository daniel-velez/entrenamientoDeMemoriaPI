/*
 * Programacion Interactiva
 * Mini proyecto 4: Juego de Blackjack.
 */
package comunes;

import java.io.Serializable;
import java.util.List;

/**
 * Estructura utilizada para enviar el estado del juego a cada cliente.
 */
public class BJGameState implements Serializable {

	public String[] nombres;
	public int[] dinero;
	public List<Carta>[] manos;
	public List<Carta> manoDealer;
	public GameStates estadoJuego;
	public int turno;
	public estadoJugador[] estadoJugadores;

	/**
	 * Instantiates a new BJGameState.
	 */
	public BJGameState() {

	}

	/**
	 * Instantiates a new BJGameState.
	 * @param turnoJugador la posicion del jugador en turno.
	 * @param nombres array con los nombres de cada jugador.
	 * @param manoDealer lista de cartas de la mano del Dealer.
	 * @param apuesta array con la apuesta de cada jugador.
	 * @param gameState atributo de tipo GameStates para saber el estado actual del juego.
	 * @param mano array con las manos de cada jugador.
	 * @param estado array que almacena el estado de cada jugador.
	 */
	public BJGameState(int turnoJugador, String[] nombres, List<Carta> manoDealer, int[] dinero, GameStates gameState,
			List<Carta>[] mano, estadoJugador[] estado) {
		this.turno = turnoJugador;
		this.nombres = nombres;
		this.manoDealer = manoDealer;
		this.dinero = dinero;
		this.estadoJuego = gameState;
		this.manos = mano;
		this.estadoJugadores = estado;
	}
}
