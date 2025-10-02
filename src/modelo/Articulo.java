/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
public class Articulo {
    private final int id;
    private final String nombre;
    private final String marca;
    private final String tipo;
    private final double precio;
    private int stock;
    private int reservado;

    public Articulo(int id, String nombre, String marca, String tipo, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.marca = marca;
        this.tipo = tipo;
        this.precio = precio;
        this.stock = stock;
        this.reservado = 0;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getMarca() { return marca; }
    public String getTipo() { return tipo; }
    public double getPrecio() { return precio; }

    // Devuelve stock total actual
    public synchronized int getStock() { return stock; }

    // Devuelve lo disponible (stock - reservado)
    public synchronized int getDisponible() { return stock - reservado; }

    // Reserva "cantidad" unidades si hay disponible (no cambia stock hasta la compra)
    public synchronized boolean reservar(int cantidad) {
        if (cantidad <= 0) return false;
        if (stock - reservado >= cantidad) {
            reservado += cantidad;
            return true;
        } else {
            return false;
        }
    }

    // Libera reservas (cuando cliente quita del carrito o se desconecta)
    public synchronized boolean liberar(int cantidad) {
        if (cantidad <= 0) return false;
        if (reservado >= cantidad) {
            reservado -= cantidad;
            return true;
        } else {
            return false;
        }
    }

    // Confirma compra: decrementa stock y reduce reservado
    public synchronized void confirmarCompra(int cantidad) {
        if (cantidad <= 0) return;
        int aplicable = Math.min(cantidad, reservado);
        aplicable = Math.min(aplicable, stock);
        stock -= aplicable;
        reservado -= aplicable;
    }

    @Override
    public String toString() {
        return String.format("%d - %s (%s) - %s - $%.2f | Stock: %d | Disponible: %d",
                id, nombre, marca, tipo, precio, stock, getDisponible());
    }
}

