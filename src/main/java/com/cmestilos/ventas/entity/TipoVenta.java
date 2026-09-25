package com.cmestilos.ventas.entity;

public enum TipoVenta {
    Física("Física"),
    Online("Online");

    private final String valor;

    TipoVenta(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static TipoVenta fromValor(String valor) {
        for (TipoVenta tipo : values()) {
            if (tipo.valor.equalsIgnoreCase(valor) || tipo.name().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de venta no válido: " + valor);
    }
}
