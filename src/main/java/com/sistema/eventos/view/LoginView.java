package com.sistema.eventos.view;

import com.sistema.eventos.model.PerfilAcesso;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.awt.*;


public class LoginView extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(LoginView.class);
    private UsuarioService usuarioService;
    private JTextField emailField;
    private JPasswordField senhaField;

    public LoginView() {
        this.usuarioService = new UsuarioService();
        initComponents();
        carregarUsuariosExemplo();
    }

    private void initComponents() {
        setTitle("Sistema de Gerenciamento de Tarefas - Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título
        JLabel titleLabel = new JLabel("Sistema de Gerenciamento de Tarefas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0, 70, 140));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Subtítulo
        JLabel subtitleLabel = new JLabel("Faça login para acessar o sistema");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        panel.add(subtitleLabel, gbc);

        // Campo Email
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(emailLabel, gbc);
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Campo Senha
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Senha:"), gbc);
        senhaField = new JPasswordField(20);
        senhaField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        panel.add(senhaField, gbc);

        // Botão Entrar
        JButton loginButton = new JButton("Entrar");
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(120, 35));
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        // Painel de botões inferiores
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        bottomPanel.setBackground(new Color(240, 248, 255));

        // Botão Esqueci minha senha
        JButton forgotPasswordButton = new JButton("Esqueci minha senha");
        forgotPasswordButton.setBackground(new Color(255, 152, 0));
        forgotPasswordButton.setForeground(Color.WHITE);
        forgotPasswordButton.setFont(new Font("Arial", Font.BOLD, 11));
        forgotPasswordButton.setFocusPainted(false);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.setPreferredSize(new Dimension(150, 30));

        bottomPanel.add(forgotPasswordButton);

        gbc.gridy = 5;
        panel.add(bottomPanel, gbc);

        loginButton.addActionListener(e -> login());
        forgotPasswordButton.addActionListener(e -> abrirRecuperarSenha());

        add(panel);
    }

    private void login() {
        String email = emailField.getText().trim();
        String senha = new String(senhaField.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Digite email e senha!",
                    "Campos Obrigatórios",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        var usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isPresent() && usuarioOpt.get().getSenha().equals(senha)) {
            Usuario usuario = usuarioOpt.get();
            log.info("Login realizado: {} - Perfil: {}", usuario.getNome(), usuario.getPerfilAcesso());

            dispose();

            switch (usuario.getPerfilAcesso()) {
                case ADMIN:
                    new AdminView(usuario).setVisible(true);
                    break;
                case GERENTE:
                    new GerenteView(usuario).setVisible(true);
                    break;
                case USUARIO:
                    new UsuarioView(usuario).setVisible(true);
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Email ou senha inválidos!\nVerifique seus dados e tente novamente.",
                    "Erro de Login",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirRecuperarSenha() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField emailRecuperacaoField = new JTextField(15);
        JPasswordField novaSenhaField = new JPasswordField(15);
        JPasswordField confirmarNovaSenhaField = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1;
        panel.add(emailRecuperacaoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Nova Senha:*"), gbc);
        gbc.gridx = 1;
        panel.add(novaSenhaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Confirmar Nova Senha:*"), gbc);
        gbc.gridx = 1;
        panel.add(confirmarNovaSenhaField, gbc);

        JLabel infoLabel = new JLabel("Digite seu email e uma nova senha para redefinir seu acesso.");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(infoLabel, gbc);

        int option = JOptionPane.showConfirmDialog(this, panel, "Recuperar Senha",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String email = emailRecuperacaoField.getText().trim();
            String novaSenha = new String(novaSenhaField.getPassword());
            String confirmarSenha = new String(confirmarNovaSenhaField.getPassword());

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Digite seu email!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (novaSenha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Digite a nova senha!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (novaSenha.length() < 4) {
                JOptionPane.showMessageDialog(this, "A senha deve ter no mínimo 4 caracteres!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!novaSenha.equals(confirmarSenha)) {
                JOptionPane.showMessageDialog(this, "As senhas não conferem!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Buscar usuário por email
            var usuarioOpt = usuarioService.buscarPorEmail(email);

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                usuario.setSenha(novaSenha);

                // Criar um admin temporário para a operação
                Usuario tempAdmin = new Usuario();
                tempAdmin.setId(1);
                tempAdmin.setPerfilAcesso(PerfilAcesso.ADMIN);

                Usuario updated = usuarioService.editarUsuario(usuario, tempAdmin);

                if (updated != null) {
                    JOptionPane.showMessageDialog(this,
                            "Senha atualizada com sucesso!\n\n" +
                                    "Email: " + email + "\n" +
                                    "Nova senha definida.\n\n" +
                                    "Faça login com sua nova senha.",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Limpar campos de login
                    emailField.setText(email);
                    senhaField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erro ao atualizar senha!\nTente novamente mais tarde.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Email não encontrado no sistema!\n\n" +
                                "Os usuários devem ser cadastrados pelo administrador.\n" +
                                "Entre em contato com o administrador do sistema.",
                        "Usuário não encontrado",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void carregarUsuariosExemplo() {
        try {
            // Verificar se já existem usuários
            if (usuarioService.listarTodosUsuarios(new Usuario()).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setEmail("admin@email.com");
                admin.setSenha("admin123");
                admin.setPerfilAcesso(PerfilAcesso.ADMIN);
                admin.setGrupo("Administração");

                Usuario gerente = new Usuario();
                gerente.setNome("Gerente");
                gerente.setEmail("gerente@email.com");
                gerente.setSenha("gerente123");
                gerente.setPerfilAcesso(PerfilAcesso.GERENTE);
                gerente.setGrupo("Gerência");

                Usuario joao = new Usuario();
                joao.setNome("João");
                joao.setEmail("joao@email.com");
                joao.setSenha("joao123");
                joao.setPerfilAcesso(PerfilAcesso.USUARIO);
                joao.setGrupo("Desenvolvimento");

                Usuario maria = new Usuario();
                maria.setNome("Maria");
                maria.setEmail("maria@email.com");
                maria.setSenha("maria123");
                maria.setPerfilAcesso(PerfilAcesso.USUARIO);
                maria.setGrupo("Desenvolvimento");

                usuarioService.cadastrarUsuario(admin, admin);
                usuarioService.cadastrarUsuario(gerente, admin);
                usuarioService.cadastrarUsuario(joao, admin);
                usuarioService.cadastrarUsuario(maria, admin);

                log.info("Usuários de exemplo carregados");
            }
        } catch (Exception e) {
            log.error("Erro ao carregar usuários exemplo", e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}