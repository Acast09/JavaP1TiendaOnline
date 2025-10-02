/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServidorTienda {
    public static final int PUERTO = 5000;
    private static Tienda tienda = new Tienda();

    public static void main(String[] args) {
        // Inventario inicial
        tienda.agregarArticulo(new Articulo(1, "Laptop", "Dell", "Electronica", 15000.0, 3));
        tienda.agregarArticulo(new Articulo(2, "Telefono", "Samsung", "Electronica", 8000.0, 5));
        tienda.agregarArticulo(new Articulo(3, "Camiseta", "Nike", "Ropa", 500.0, 10));
        tienda.agregarArticulo(new Articulo(4, "Audifonos", "Sony", "Electronica", 1200.0, 8));
        tienda.agregarArticulo(new Articulo(5, "Libro Java", "O'Reilly", "Libros", 350.0, 7));
        tienda.agregarArticulo(new Articulo(6, "Tenis Air Max", "Nike", "Ropa", 2000.0, 20));
        tienda.agregarArticulo(new Articulo(7, "Mochila", "Adidas", "Accesorios", 1550.0, 4));
        tienda.agregarArticulo(new Articulo(8, "Pulsera", "Pandora", "Joyeria", 950.0, 11));
        tienda.agregarArticulo(new Articulo(9, "Mouse", "Samsung", "Electronica", 490.0, 12));
        tienda.agregarArticulo(new Articulo(10, "Lentes", "RayBan", "Accesorios", 3800.0, 10));



        try (ServerSocket server = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);
            while (true) {
                Socket cliente = server.accept(); // Bloqueante
                System.out.println("Cliente conectado desde " + cliente.getInetAddress());
                Thread t = new Thread(new ClienteHandler(cliente, tienda));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

