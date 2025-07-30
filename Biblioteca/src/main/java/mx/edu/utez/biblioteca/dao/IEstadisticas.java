package mx.edu.utez.biblioteca.dao;

import java.util.List;
import java.util.Map;

public interface IEstadisticas {
    List<Map<String, Object>> getTop5MostBorrowedBooks();
    List<Map<String, Object>> getTop5MostActiveClients();
}