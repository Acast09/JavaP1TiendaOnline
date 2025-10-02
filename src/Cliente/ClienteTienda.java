/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClienteTienda {
    public static void main(String[] args) {
        String servidor = "localhost";
        int puerto = 5000;

        try (Socket socket = new Socket(servidor, puerto);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {
            leerRespuesta(in);

            int opcion;
            do {
                mostrarMenu();
                opcion = Integer.parseInt(sc.nextLine());
                switch (opcion) {
                    case 1:
                        out.println("LISTAR");
                        leerRespuesta(in);
                        break;
                    case 2:
                        System.out.print("Ingrese nombre o marca: ");
                        String texto = sc.nextLine();
                        out.println("BUSCAR " + texto);
                        leerRespuesta(in);
                        break;
                    case 3:
                        System.out.print("Ingrese tipo: ");
                        String tipo = sc.nextLine();
                        out.println("LISTARTIPO " + tipo);
                        leerRespuesta(in);
                        break;
                    case 4:
                        System.out.print("ID del artículo a agregar: ");
                        String idAgregar = sc.nextLine();
                        System.out.print("Cantidad (enter = 1): ");
                        String cantAg = sc.nextLine();
                        if (cantAg.trim().isEmpty()) out.println("AGREGAR " + idAgregar);
                        else out.println("AGREGAR " + idAgregar + " " + cantAg);
                        leerRespuesta(in);
                        break;
                    case 5:
                        out.println("VER_CARRITO");
                        leerRespuesta(in);
                        break;
                    case 6:
                        System.out.print("ID del artículo a eliminar: ");
                        String idEliminar = sc.nextLine();
                        System.out.print("Cantidad (enter = 1): ");
                        String cantEl = sc.nextLine();
                        if (cantEl.trim().isEmpty()) out.println("ELIMINAR " + idEliminar);
                        else out.println("ELIMINAR " + idEliminar + " " + cantEl);
                        leerRespuesta(in);
                        break;
                    case 7:
                        out.println("FINALIZAR");
                        leerRespuesta(in);
                        break;
                    case 0:
                        out.println("SALIR");
                        leerRespuesta(in);
                        System.out.println("Saliendo cliente...");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } while (opcion != 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n--- MENU CLIENTE ---");
        System.out.println("1. Mostrar inventario");
        System.out.println("2. Buscar articulo por nombre/marca");
        System.out.println("3. Listar articulos por tipo");
        System.out.println("4. Agregar al carrito");
        System.out.println("5. Ver carrito");
        System.out.println("6. Eliminar del carrito");
        System.out.println("7. Finalizar compra (obtener ticket)");
        System.out.println("0. Salir");
        System.out.print("Elige opcion: ");
    }

    // lee hasta la línea "END"
    private static void leerRespuesta(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            if ("END".equals(line)) break;
            System.out.println(line);
        }
    }
}

