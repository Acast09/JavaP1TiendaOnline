/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.util.ArrayList;
import java.util.List;


public class Tienda {
    private final List<Articulo> inventario = new ArrayList<>();

    public void agregarArticulo(Articulo a) {
        inventario.add(a);
    }

    public List<Articulo> getInventario() {
        return new ArrayList<>(inventario);
    }

    public Articulo obtenerPorId(int id) {
        for (Articulo a : inventario) {
            if (a.getId() == id) return a;
        }
        return null;
    }

    public List<Articulo> buscarPorNombreOMarca(String texto) {
        List<Articulo> res = new ArrayList<>();
        String t = texto.toLowerCase();
        for (Articulo a : inventario) {
            if (a.getNombre().toLowerCase().contains(t) || a.getMarca().toLowerCase().contains(t)) {
                res.add(a);
            }
        }
        return res;
    }

    public List<Articulo> listarPorTipo(String tipo) {
        List<Articulo> res = new ArrayList<>();
        String t = tipo.toLowerCase();
        for (Articulo a : inventario) {
            if (a.getTipo().toLowerCase().contains(t)) res.add(a);
        }
        return res;
    }
}
