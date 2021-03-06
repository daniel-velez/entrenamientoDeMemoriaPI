/*
 * Programacion Interactiva
 * Mini proyecto 4: Juego de Blackjack.
 */
package clientebj;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import comunes.Carta;
import comunes.estadoJugador;

/**
 * JPanel que muestra las cartas y la información de un jugador.
 */
public class JManoPanel extends JPanel {

    private JLabel nombre, dinero;
    protected JLabel icono;
    protected JCartasPanel cartas;
    protected Dimension JCartasSize;
    private int moneyImg;

    /**
     * Instantiates a new JManoPanel.
     */
    public JManoPanel() {
        this(1);
    }

    /**
     * Instantiates a new JManoPanel
     * @param moneyImg
     */
    public JManoPanel(int moneyImg) {
        Resources.setJPanelSize(this, new Dimension(350, 220));
        JCartasSize = new Dimension(310, 120); //125
        this.setOpaque(false);
        this.moneyImg = moneyImg;
        initGUI();
    }

    /**
     * Inits the GUI.
     */
    protected void initGUI() {
        nombre = new JLabel();
        dinero = new JLabel();
        icono = new JLabel(Resources.getImage("m" + moneyImg + ".png"));
        icono.setVisible(false);

        nombre.setFont(Resources.HelveticaNeue.deriveFont(17f));
        nombre.setForeground(Color.WHITE);

        dinero.setFont(Resources.HelveticaNeue.deriveFont(17f));
        dinero.setForeground(Color.WHITE);

        add(nombre);
        add(Box.createRigidArea(new Dimension(92, 0)));
        add(icono);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(dinero);

        cartas = new JCartasPanel(JCartasSize);
        add(cartas);
    }

    /**
     * Muestra las cartas.
     * @param mano
     */
    public void mostrarCartas(List<Carta> mano) {
        cartas.mostrarCartas(mano);
    }

    /**
     * Establece el nombre.
     * @param nombre String del nombre del jugador.
     */
    public void setNombre(String nombre) {
        this.nombre.setText(nombre);
        icono.setVisible(true);
    }

    /**
     * Establece el dinero.
     * @param montoDinero String con el dinero del jugador.
     */
    public void setDinero(String montoDinero) {
        this.dinero.setText("$" + montoDinero);
        this.dinero.revalidate();
        this.dinero.repaint();
    }

    /**
     * Muestra un mensaje en el panel de cartas del jugador.
     * @param msj String del mensaje a mostrar.
     * @param estado El estado actual del jugador.
     */
    public void mostrarMensaje(String msj, estadoJugador estado) {
        cartas.mostrarMensaje(msj, estado);
    }

    /**
     * Reinicia el panel de cartas.
     */
    public void reset() {
        cartas.reset();
    }
}
