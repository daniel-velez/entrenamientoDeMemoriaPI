/*
 * Programacion Interactiva
 * Mini proyecto 4: Juego de Blackjack.
 */
package comunes;

import java.io.Serializable;

/**
 * Estructura utilizada para el envio de datos de la solicitud de un cliente.
 */
public class ClientRequest implements Serializable {

    public requests request;
    public String nombre;
    public int apuesta;

    /**
     * Instantiates a new ClientRequest.
     * @param request
     */
    public ClientRequest(requests request) {
        this.request = request;
    }

    /**
     * Instantiates a new ClientRequest.
     * @param request
     * @param nombre
     */
    public ClientRequest(requests request, String nombre) {
        this.request = request;
        this.nombre = nombre;
    }

    /**
     * Instantiates a new ClientRequest.
     * @param request
     * @param apuesta
     */
    public ClientRequest(requests request, int apuesta) {
        this.request = request;
        this.apuesta = apuesta;
    }
}