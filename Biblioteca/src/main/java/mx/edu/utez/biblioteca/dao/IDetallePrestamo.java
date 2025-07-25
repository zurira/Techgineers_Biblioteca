package mx.edu.utez.biblioteca.dao;

public interface IDetallePrestamo {
    public void insertarEjemplar(int idPrestamo, int idEjemplar);
    public void marcarDevuelto(int idDetallePrestamo, boolean devuelto);
}
