package com.mycompany.apicativo.modelo;

import java.util.Date;

/**
 * Representa um registro do historico (tabela colmeia_manejo).
 * Os ids sao gravados no banco; os nomes (codigo da colmeia, tipo do manejo,
 * nome do tecnico e setor) vem da view vw_historico_completo e servem so pra
 * mostrar na tela.
 */
public class Historico {

    private int id;
    private int idColmeia;
    private int idManejo;
    private int idTecnico;
    private Date dataRealizacao;
    private String observacoes;

    private String codigoColmeia;
    private String tipoProcedimento;
    private String nomeTecnico;
    private String nomeSetor;

    public Historico() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdColmeia() {
        return idColmeia;
    }

    public void setIdColmeia(int idColmeia) {
        this.idColmeia = idColmeia;
    }

    public int getIdManejo() {
        return idManejo;
    }

    public void setIdManejo(int idManejo) {
        this.idManejo = idManejo;
    }

    public int getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(int idTecnico) {
        this.idTecnico = idTecnico;
    }

    public Date getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(Date dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getCodigoColmeia() {
        return codigoColmeia;
    }

    public void setCodigoColmeia(String codigoColmeia) {
        this.codigoColmeia = codigoColmeia;
    }

    public String getTipoProcedimento() {
        return tipoProcedimento;
    }

    public void setTipoProcedimento(String tipoProcedimento) {
        this.tipoProcedimento = tipoProcedimento;
    }

    public String getNomeTecnico() {
        return nomeTecnico;
    }

    public void setNomeTecnico(String nomeTecnico) {
        this.nomeTecnico = nomeTecnico;
    }

    public String getNomeSetor() {
        return nomeSetor;
    }

    public void setNomeSetor(String nomeSetor) {
        this.nomeSetor = nomeSetor;
    }
}
