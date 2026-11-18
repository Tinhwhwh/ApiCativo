package com.mycompany.apicativo.modelo;

/**
 * Representa uma linha da tabela usuario.
 * A senha nao fica aqui: so o hash dela fica no banco.
 */
public class Usuario {

    private int id;
    private String nome;
    private String login;

    public Usuario() {
    }

    public Usuario(int id, String nome, String login) {
        this.id = id;
        this.nome = nome;
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return nome + " (" + login + ")";
    }
}
