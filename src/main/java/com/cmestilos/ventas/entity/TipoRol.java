package com.cmestilos.ventas.entity;

public enum TipoRol {
    Administrador("Administrador"),
    Cajero("Cajero");

    private final String valor;

    TipoRol(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public String getAuthority() {
        return "ROLE_" + name().toUpperCase(java.util.Locale.ROOT);
    }

    public static TipoRol fromValor(String valor) {
        for (TipoRol tipo : values()) {
            if (tipo.valor.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Rol no válido: " + valor);
    }
}
