package mx.edu.utez.biblioteca.dao;

public interface IBibliotecario {
    List<Bibliotecario> findAll()throws Exception;
    Bibliotecario findBy(int id)throws Exception;
    void create(Bibliotecario bibliotecario) throws Exception;
    void update(Bibliotecario bibliotecario) throws Exception;
    void delete(int id) throws Exception;
    List<Bibliotecario> search(String searchTerm) throws Exception;
    boolean updateStatus(int idBibliotecario, String estado) throws Exception;
}
