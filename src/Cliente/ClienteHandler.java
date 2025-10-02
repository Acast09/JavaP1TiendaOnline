/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ClienteHandler implements Runnable {
    private Socket socket;
    private Tienda tienda;
    // carrito: idArticulo -> cantidad
    private Map<Integer,Integer> carrito = new HashMap<>();

    public ClienteHandler(Socket socket, Tienda tienda) {
        this.socket = socket;
        this.tienda = tienda;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Mensaje de bienvenida
            out.println("Bienvenido a la Tienda");
            out.println("END");

            String linea;
            while ((linea = in.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    out.println("END");
                    continue;
                }
                String[] partes = linea.split(" ", 3);
                String cmd = partes[0].toUpperCase();

                switch (cmd) {
                    case "LISTAR":
                        for (Articulo a : tienda.getInventario()) out.println(a);
                        out.println("END");
                        break;

                    case "BUSCAR":
                        if (partes.length < 2) { out.println("Uso: BUSCAR <texto>"); out.println("END"); break; }
                        List<Articulo> encontrados = tienda.buscarPorNombreOMarca(partes[1]);
                        if (encontrados.isEmpty()) out.println("No se encontraron artículos.");
                        else for (Articulo a : encontrados) out.println(a);
                        out.println("END");
                        break;

                    case "LISTARTIPO":
                        if (partes.length < 2) { out.println("Uso: LISTARTIPO <tipo>"); out.println("END"); break; }
                        List<Articulo> porTipo = tienda.listarPorTipo(partes[1]);
                        if (porTipo.isEmpty()) out.println("No hay artículos de ese tipo.");
                        else for (Articulo a : porTipo) out.println(a);
                        out.println("END");
                        break;

                    case "AGREGAR": {
                        if (partes.length < 2) { out.println("Uso: AGREGAR <id> [cantidad]"); out.println("END"); break; }
                        String[] p = partes[1].split(" ");
                        int id = Integer.parseInt(p[0]);
                        int cantidad = 1;
                        if (partes.length == 3) {
                            try { cantidad = Integer.parseInt(partes[2]); } catch (Exception e) { cantidad = 1; }
                        } else if (p.length > 1) {
                            try { cantidad = Integer.parseInt(p[1]); } catch (Exception e) { cantidad = 1; }
                        }
                        Articulo art = tienda.obtenerPorId(id);
                        if (art == null) {
                            out.println("Artículo no encontrado.");
                        } else {
                            boolean ok = art.reservar(cantidad); // reserva
                            if (ok) {
                                carrito.put(id, carrito.getOrDefault(id, 0) + cantidad);
                                out.println("Agregado al carrito: " + art.getNombre() + " x" + cantidad);
                            } else {
                                out.println("No hay suficiente stock disponible.");
                            }
                        }
                        out.println("END");
                        break;
                    }

                    case "ELIMINAR": {
                        if (partes.length < 2) { out.println("Uso: ELIMINAR <id> [cantidad]"); out.println("END"); break; }
                        String[] p = partes[1].split(" ");
                        int id = Integer.parseInt(p[0]);
                        int cantidad = 1;
                        if (partes.length == 3) {
                            try { cantidad = Integer.parseInt(partes[2]); } catch (Exception e) { cantidad = 1; }
                        } else if (p.length > 1) {
                            try { cantidad = Integer.parseInt(p[1]); } catch (Exception e) { cantidad = 1; }
                        }

                        Integer enCarrito = carrito.get(id);
                        if (enCarrito == null || enCarrito == 0) {
                            out.println("El artículo no está en tu carrito.");
                        } else {
                            int quitar = Math.min(cantidad, enCarrito);
                            Articulo a = tienda.obtenerPorId(id);
                            if (a != null) {
                                a.liberar(quitar); // libera reserva
                            }
                            if (enCarrito - quitar <= 0) carrito.remove(id);
                            else carrito.put(id, enCarrito - quitar);
                            out.println("Eliminado del carrito: id " + id + " x" + quitar);
                        }
                        out.println("END");
                        break;
                    }

                    case "VER_CARRITO": {
                        if (carrito.isEmpty()) {
                            out.println("Carrito vacío.");
                            out.println("END");
                            break;
                        }
                        double total = 0.0;
                        out.println("Carrito:");
                        for (Map.Entry<Integer,Integer> e : carrito.entrySet()) {
                            Articulo a = tienda.obtenerPorId(e.getKey());
                            if (a != null) {
                                int q = e.getValue();
                                double sub = q * a.getPrecio();
                                total += sub;
                                out.println(String.format("%s x%d - $%.2f = $%.2f", a.getNombre(), q, a.getPrecio(), sub));
                            }
                        }
                        out.println(String.format("TOTAL: $%.2f", total));
                        out.println("END");
                        break;
                    }

                    case "FINALIZAR": {
                        if (carrito.isEmpty()) {
                            out.println("Carrito vacío. No hay nada que comprar.");
                            out.println("END");
                            break;
                        }
                        double total = 0.0;
                        out.println("=== TICKET ===");
                        for (Map.Entry<Integer,Integer> e : carrito.entrySet()) {
                            Articulo a = tienda.obtenerPorId(e.getKey());
                            if (a != null) {
                                int q = e.getValue();
                                double sub = q * a.getPrecio();
                                total += sub;
                                a.confirmarCompra(q); // decrementa stock y reserva
                                out.println(String.format("%s x%d - $%.2f = $%.2f", a.getNombre(), q, a.getPrecio(), sub));
                            }
                        }
                        carrito.clear();
                        out.println(String.format("TOTAL A PAGAR: $%.2f", total));
                        out.println("Gracias por su compra!");
                        out.println("END");
                        break;
                    }

                    case "SALIR": {
                        // liberar reservas antes de desconectar
                        liberarReservas();
                        out.println("Desconectado.");
                        out.println("END");
                        socket.close();
                        return;
                    }

                    default:
                        out.println("Comando no reconocido.");
                        out.println("END");
                } // switch
            } // while in.readLine
        } catch (IOException ioe) {
            System.out.println("Cliente desconectado inesperadamente: " + ioe.getMessage());
        } finally {
            // Si se desconecta sin SALIR, liberar reservas
            liberarReservas();
            try { if (!socket.isClosed()) socket.close(); } catch (IOException ignored) {}
        }
    }

    // libera todas las reservas en el carrito (por desconexión)
    private void liberarReservas() {
        for (Map.Entry<Integer,Integer> e : carrito.entrySet()) {
            Articulo a = tienda.obtenerPorId(e.getKey());
            if (a != null) {
                a.liberar(e.getValue());
            }
        }
        carrito.clear();
    }
}
