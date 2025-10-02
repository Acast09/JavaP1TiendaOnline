/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ClienteTiendaGUI extends JFrame {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private JTextArea areaTexto;
    private JTextField campoEntrada;

    public ClienteTiendaGUI(String servidor, int puerto) {
        setTitle("Cliente Tienda Online");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Área de salida
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll, BorderLayout.CENTER);

        // Panel inferior con entrada y botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 4, 5, 5));

        JButton btnListar = new JButton("Listar Inventario");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnListarTipo = new JButton("Listar por Tipo");
        JButton btnAgregar = new JButton("Agregar al carrito");
        JButton btnCarrito = new JButton("Ver Carrito");
        JButton btnEliminar = new JButton("Eliminar del carrito");
        JButton btnFinalizar = new JButton("Finalizar Compra");
        JButton btnSalir = new JButton("Salir");

        panelBotones.add(btnListar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnListarTipo);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnCarrito);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnFinalizar);
        panelBotones.add(btnSalir);

        add(panelBotones, BorderLayout.SOUTH);

        // Conexión al servidor
        try {
            socket = new Socket(servidor, puerto);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            leerRespuesta(); // mensaje de bienvenida
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error conectando al servidor", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Listeners para cada botón
        btnListar.addActionListener(e -> {
            out.println("LISTAR");
            leerRespuesta();
        });

        btnBuscar.addActionListener(e -> {
            String texto = JOptionPane.showInputDialog(this, "Ingrese nombre o marca:");
            if (texto != null && !texto.trim().isEmpty()) {
                out.println("BUSCAR " + texto);
                leerRespuesta();
            }
        });

        btnListarTipo.addActionListener(e -> {
            String tipo = JOptionPane.showInputDialog(this, "Ingrese tipo:");
            if (tipo != null && !tipo.trim().isEmpty()) {
                out.println("LISTARTIPO " + tipo);
                leerRespuesta();
            }
        });

        btnAgregar.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "ID del artículo:");
            String cant = JOptionPane.showInputDialog(this, "Cantidad (enter=1):");
            if (id != null && !id.trim().isEmpty()) {
                if (cant == null || cant.trim().isEmpty())
                    out.println("AGREGAR " + id);
                else
                    out.println("AGREGAR " + id + " " + cant);
                leerRespuesta();
            }
        });

        btnCarrito.addActionListener(e -> {
            out.println("VER_CARRITO");
            leerRespuesta();
        });

        btnEliminar.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "ID del artículo a eliminar:");
            String cant = JOptionPane.showInputDialog(this, "Cantidad (enter=1):");
            if (id != null && !id.trim().isEmpty()) {
                if (cant == null || cant.trim().isEmpty())
                    out.println("ELIMINAR " + id);
                else
                    out.println("ELIMINAR " + id + " " + cant);
                leerRespuesta();
            }
        });

        btnFinalizar.addActionListener(e -> {
            out.println("FINALIZAR");
            leerRespuesta();
        });

        btnSalir.addActionListener(e -> {
            out.println("SALIR");
            leerRespuesta();
            try { socket.close(); } catch (IOException ignored) {}
            System.exit(0);
        });
    }

    // Lee respuesta hasta "END" y la imprime en el área de texto
    private void leerRespuesta() {
        try {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                if ("END".equals(line)) break;
                sb.append(line).append("\n");
            }
            areaTexto.append(sb.toString() + "\n");
        } catch (IOException e) {
            areaTexto.append("Error leyendo respuesta del servidor\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteTiendaGUI gui = new ClienteTiendaGUI("localhost", 5000);
            gui.setVisible(true);
        });
    }
}

