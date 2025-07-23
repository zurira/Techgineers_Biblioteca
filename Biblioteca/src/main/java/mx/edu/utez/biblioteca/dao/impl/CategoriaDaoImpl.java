package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.ICategoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaDaoImpl implements ICategoria {
    @Override
    public List<String> obtenerNombresCategorias() {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT NOMBRE FROM CATEGORIA ORDER BY NOMBRE";

        return categorias;

    }
}
