package mx.edu.utez.biblioteca.dao;

public interface IDetallePrestamo {
    public boolean insertarEjemplar(int idPrestamo, int idEjemplar);
    public boolean marcarDevuelto(int idDetallePrestamo, boolean devuelto);
}
