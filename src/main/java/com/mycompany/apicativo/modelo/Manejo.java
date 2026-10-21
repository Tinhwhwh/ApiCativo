package com.mycompany.apicativo.modelo;

/**
 * Representa uma linha da tabela manejo (um tipo de procedimento).
 */
public class Manejo {

    private int id;
    private String tipoProcedimento;
    private String descricao;

    public Manejo() {
    }

    public Manejo(int id, String tipoProcedimento, String descricao) {
        this.id = id;
        this.tipoProcedimento = tipoProcedimento;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoProcedimento() {
        return tipoProcedimento;
    }

    public void setTipoProcedimento(String tipoProcedimento) {
        this.tipoProcedimento = tipoProcedimento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
