package com.mycompany.apicativo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Janelinha que aparece quando o sistema nao consegue conectar no banco
 * (por exemplo, num PC onde a senha do PostgreSQL nao e "postgres").
 * Feita na mao, sem .form, porque e so um JOptionPane com campos.
 */
public class DialogoConfiguracaoBanco {

    private DialogoConfiguracaoBanco() {
    }

    /**
     * @param atual   os dados que ja estavam sendo usados (aparecem preenchidos)
     * @param motivo  por que nao conectou, pra pessoa saber o que corrigir
     * @return a nova configuracao, ou null se a pessoa clicou em Cancelar
     */
    public static ConfiguracaoBanco pedir(ConfiguracaoBanco atual, String motivo) {
        JTextField txtHost = new JTextField(atual.getHost(), 18);
        JTextField txtPorta = new JTextField(String.valueOf(atual.getPorta()), 18);
        JTextField txtBanco = new JTextField(atual.getBanco(), 18);
        JTextField txtUsuario = new JTextField(atual.getUsuario(), 18);
        JPasswordField txtSenha = new JPasswordField(atual.getSenha(), 18);

        JPanel campos = new JPanel(new GridLayout(0, 2, 6, 6));
        campos.add(new JLabel("Servidor:"));
        campos.add(txtHost);
        campos.add(new JLabel("Porta:"));
        campos.add(txtPorta);
        campos.add(new JLabel("Banco:"));
        campos.add(txtBanco);
        campos.add(new JLabel("Usuario:"));
        campos.add(txtUsuario);
        campos.add(new JLabel("Senha:"));
        campos.add(txtSenha);

        JLabel lblMotivo = new JLabel("<html><body style='width:280px'>"
                + "Nao consegui conectar no banco.<br>" + motivo
                + "<br><br>Confira os dados do PostgreSQL deste computador:</body></html>");
        lblMotivo.setForeground(new Color(255, 120, 120));

        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.add(lblMotivo, BorderLayout.NORTH);
        painel.add(campos, BorderLayout.CENTER);

        while (true) {
            int opcao = JOptionPane.showConfirmDialog(null, painel, "ApiCativo - Configurar banco",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opcao != JOptionPane.OK_OPTION) {
                return null;
            }
            String erro = ConfiguracaoBanco.validar(txtHost.getText(), txtPorta.getText(),
                    txtBanco.getText(), txtUsuario.getText());
            if (erro == null) {
                return new ConfiguracaoBanco(txtHost.getText().trim(), Integer.parseInt(txtPorta.getText().trim()),
                        txtBanco.getText().trim(), txtUsuario.getText().trim(), new String(txtSenha.getPassword()));
            }
            JOptionPane.showMessageDialog(null, erro, "Dados invalidos", JOptionPane.WARNING_MESSAGE);
        }
    }
}
