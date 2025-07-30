package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.Ejemplar;

import java.util.List;

public interface IDetallePrestamo {
    public boolean insertarEjemplar(int idPrestamo, int idEjemplar);
    public boolean marcarDevuelto(int idDetallePrestamo, boolean devuelto);
    public boolean eliminarDetallesPorPrestamo(int idPrestamo);
    public List<Ejemplar> findEjemplaresByPrestamoId(int prestamoId);

}
