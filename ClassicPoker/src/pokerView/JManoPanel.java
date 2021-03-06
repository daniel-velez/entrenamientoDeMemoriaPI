/*
 * Programacion Interactiva
 * Mini proyecto 3: Juego de poker clasico.
 */

package pokerView;

import classicPoker.*;
import classicPoker.Jugador.TipoJugador;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * JPanel que muestra las cartas y la información de un jugador.
 */
public class JManoPanel extends JPanel {

    private List<JButton> JMano;
    private Jugador jugador;
    private JLabel nombre, dinero;
    private boolean isHuman;
    private Dimension JCartaSize;
    private Dimension panelSize;

    private Boolean ordenarCartas = false;
    private List<JButton> cartasSeleccionadas;

    private Escucha listener;

    /**
     * Instantiates a new JManoPanel.
     * @param jugador
     */
    public JManoPanel(Jugador jugador) {
        this.jugador = jugador;
        this.cartasSeleccionadas = new ArrayList<JButton>();
        this.isHuman = (jugador.getTipo() == TipoJugador.Usuario) ? true : false;
        this.JCartaSize = (isHuman) ? new Dimension(88, 134) : new Dimension(58, 89); // base: 350x530
        this.panelSize = (isHuman) ? new Dimension(650, 157) : new Dimension(325, 136);
        this.setOpaque(false);

        JMano = new ArrayList<JButton>();

        for (int i = 0; i < 5; i++)
            JMano.add(new JButton());

        initGUI();
    }

    /**
     * Inits the GUI
     */
    private void initGUI() {
        this.setPreferredSize(panelSize);
        //this.setBackground(Color.ORANGE);

        listener = new Escucha();

        nombre = new JLabel(isHuman ? "Dinero" : jugador.getName());
        dinero = new JLabel(Integer.toString(jugador.getDinero()));

        float fontSize = isHuman ? 20f : 15f;
        nombre.setFont(Resources.RobotoBold.deriveFont(fontSize));
        nombre.setForeground(Color.WHITE);
        dinero.setFont(Resources.RobotoBold.deriveFont(fontSize));
        dinero.setForeground(Color.WHITE);

        this.add(nombre);
        if (!isHuman)
            this.add(Box.createRigidArea(new Dimension(235, 0)));
        this.add(dinero);

        for (JButton JCarta : JMano) {
            JCarta.setSize(JCartaSize);
            JCarta.setMinimumSize(JCartaSize);
            JCarta.setPreferredSize(JCartaSize);
            JCarta.setIcon(isHuman ? CardImage.cartaTapadaMax : CardImage.cartaTapada);
            JCarta.setContentAreaFilled(false);
            JCarta.setBorder(null);
            if (isHuman)
                JCarta.addActionListener(listener);
            this.add(JCarta);
        }
    }

    // #---------------------------------------------------------------------------
    // # MÉTODOS
    // #---------------------------------------------------------------------------

    /**
     * Muestra el dinero del jugador en pantalla
     */
    public void mostrarDinero() {
        dinero.setText(jugador.getDinero().toString());
        revalidate();
        repaint();
    }

    /**
     * Muestra el contenido de las cartas del jugador
     * @param mano
     */
    public void descubrirCartas(List<Carta> mano) {
        for (int i = 0; i < JMano.size(); i++) {
            JMano.get(i).setIcon(CardImage.get(mano.get(i), JCartaSize));
            JMano.get(i).setEnabled(true);
        }
    }

    /**
     * Tapar las cartas
     */
    public void taparCartas() {
        for (JButton JCarta : JMano)
            JCarta.setIcon(isHuman ? CardImage.cartaTapadaMax : CardImage.cartaTapada);
    }

    /**
     * Descarta visualmente todas las cartas
     */
    public void descartar() {
        for (JButton JCarta : JMano)
            JCarta.setIcon(null);
    }

    /**
     * Descarta visualmente la carta de indice i
     * @param index
     */
    public void descartar(int index) {
        JMano.get(index).setIcon(null);
    }

    /**
     * Retorna la lista de enteros que corresponden a la posicion de las cartas que el usuario ha seleccionado.
     */
    public List<Integer> getCartasSeleccionadas() {
        List<Integer> cartas = new ArrayList<Integer>();
        for (JButton JCarta : cartasSeleccionadas) 
            cartas.add(JMano.indexOf(JCarta));
        cartasSeleccionadas.clear();
        return cartas;
    }

    // #---------------------------------------------------------------------------
    // # GETTERS & SETTERS
    // #---------------------------------------------------------------------------

    /**
     * @return El JLabel con el nombre del jugador.
     */
    public JLabel getUserName() {
        return nombre;
    }

    /**
     * @return El JLabel con el dinero del jugador.
     */
    public JLabel getUserMoney() {
        return dinero;
    }

    /**
     * Establece el valor del atributo ordenarCartas de acuerdo al valor ingresado.
     * @param flag true -> puede ordenar cartas.
     */
    public void setOrdenarCartas(boolean flag) {
        ordenarCartas = flag;
    }

    /**
     * @return El entero correspondiente a la cantidad de cartas seleccionadas.
     */
    public int getCartasSeleccionadasSize() {
        return cartasSeleccionadas.size();
    }

    // #---------------------------------------------------------------------------
    // # EVENTS
    // #---------------------------------------------------------------------------

    /**
     * Evento al hacer click sobre una carta
     * @param JCarta
     */
    private void onJCartaClick(JButton JCarta) {

        if (ordenarCartas) {
            if (cartasSeleccionadas.size() == 0)
                cartasSeleccionadas.add(JCarta);
            else if (cartasSeleccionadas.size() == 1) {
                int index1 = JMano.indexOf(cartasSeleccionadas.get(0));
                int index2 = JMano.indexOf(JCarta);
                jugador.cambiarCartas(index1, index2);
                cartasSeleccionadas.clear();
            }
        } else if (cartasSeleccionadas.indexOf(JCarta) != -1) {
            cartasSeleccionadas.remove(JCarta);
            JCarta.setBackground(null);
        } else {
            cartasSeleccionadas.add(JCarta);
            JCarta.setBackground(Color.WHITE);
        }
    }

    /**
     * The Class Escucha
     */
    private class Escucha implements ActionListener {
        /**
         * Action performed.
         * @param event the event
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            if (!jugador.seHaRetirado()) {
                for (JButton boton : JMano) {
                    if (event.getSource() == boton)
                        onJCartaClick(boton);
                }
            }
        }
    }
}