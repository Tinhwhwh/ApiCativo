package com.mycompany.apicativo.modelo;

/**
 * Representa uma linha da tabela localizacao (um setor da fazenda).
 */
public class Localizacao {

    private int id;
    private String nomeSetor;
    private String descricao;

    public Localizacao() {
    }

    public Localizacao(int id, String nomeSetor, String descricao) {
        this.id = id;
        this.nomeSetor = nomeSetor;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeSetor() {
        return nomeSetor;
    }

    public void setNomeSetor(String nomeSetor) {
        this.nomeSetor = nomeSetor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
