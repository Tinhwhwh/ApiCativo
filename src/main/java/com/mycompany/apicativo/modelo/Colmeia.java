package com.mycompany.apicativo.modelo;

import java.util.Date;

/**
 * Representa uma linha da tabela colmeia.
 * O nomeSetor vem do JOIN com localizacao e serve so pra mostrar na tela.
 */
public class Colmeia {

    private int id;
    private String codigo;
    private Date dataInstalacao;
    private String status;
    private String tamanhoCaixa;
    private String estiloCaixa;
    private int idLocalizacao;
    private String nomeSetor;

    public Colmeia() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getDataInstalacao() {
        return dataInstalacao;
    }

    public void setDataInstalacao(Date dataInstalacao) {
        this.dataInstalacao = dataInstalacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTamanhoCaixa() {
        return tamanhoCaixa;
    }

    public void setTamanhoCaixa(String tamanhoCaixa) {
        this.tamanhoCaixa = tamanhoCaixa;
    }

    public String getEstiloCaixa() {
        return estiloCaixa;
    }

    public void setEstiloCaixa(String estiloCaixa) {
        this.estiloCaixa = estiloCaixa;
    }

    public int getIdLocalizacao() {
        return idLocalizacao;
    }

    public void setIdLocalizacao(int idLocalizacao) {
        this.idLocalizacao = idLocalizacao;
    }

    public String getNomeSetor() {
        return nomeSetor;
    }

    public void setNomeSetor(String nomeSetor) {
        this.nomeSetor = nomeSetor;
    }
}
