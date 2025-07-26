package mx.edu.utez.biblioteca.model;

public class Usuario {

        private String nombreCompleto;
        private String fechaNacimiento;
        private String email;
        private String telefono;
        private String direccion;

        // Constructor
        public Usuario(String nombreCompleto, String fechaNacimiento, String email, String telefono, String direccion) {
            this.nombreCompleto = nombreCompleto;
            this.fechaNacimiento = fechaNacimiento;
            this.email = email;
            this.telefono = telefono;
            this.direccion = direccion;
        }

        // Getters y Setters

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public void setNombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
        }

        public String getFechaNacimiento() {
            return fechaNacimiento;
        }

        public void setFechaNacimiento(String fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }
    }