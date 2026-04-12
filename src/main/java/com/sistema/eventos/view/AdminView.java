package com.sistema.eventos.view;

import com.sistema.eventos.model.PerfilAcesso;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.service.UsuarioService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;



public class AdminView extends JFrame {

    private Usuario usuarioLogado;
    private UsuarioService usuarioService;
    private JTable table;
    private DefaultTableModel tableModel;

    // Componentes do formulário
    private JTextField nomeField;
    private JTextField emailField;
    private JPasswordField senhaField;
    private JPasswordField confirmarSenhaField;
    private JComboBox<PerfilAcesso> perfilCombo;
    private JButton saveButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton;
    private JCheckBox showPasswordCheckBox;

    private int currentUserId = 0;

    public AdminView(Usuario usuario) {
        this.usuarioLogado = usuario;
        this.usuarioService = new UsuarioService();
        initComponents();
        carregarUsuarios();
    }

    private void initComponents() {
        setTitle("Admin - Gerenciamento de Usuários");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        // Painel superior
        JPanel topPanel = criarTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Painel central com formulário e tabela
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Formulário de usuário
        JPanel formPanel = criarFormPanel();
        centerPanel.add(formPanel, BorderLayout.NORTH);

        // Tabela de usuários
        JPanel tablePanel = criarTablePanel();
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Painel de botões inferiores
        JPanel bottomPanel = criarBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel criarTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(33, 150, 243));
        topPanel.setPreferredSize(new Dimension(1000, 80));

        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setBackground(new Color(33, 150, 243));

        JLabel titleLabel = new JLabel("Sistema de Gestão de Projetos");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomePanel.add(titleLabel);

        JLabel adminLabel = new JLabel(" - Administrador: " + usuarioLogado.getNome());
        adminLabel.setForeground(new Color(255, 255, 200));
        adminLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        welcomePanel.add(adminLabel);

        topPanel.add(welcomePanel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Sair do Sistema");
        logoutButton.setBackground(new Color(244, 67, 54));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel criarFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(33, 150, 243)),
                "Dados do Usuário",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(33, 150, 243)
        ));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campo Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        JLabel nomeLabel = new JLabel("Nome:");
        nomeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(nomeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        nomeField = new JTextField(20);
        nomeField.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(nomeField, gbc);

        // Campo Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(emailField, gbc);

        // Campo Senha
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(senhaLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        senhaField = new JPasswordField(20);
        senhaField.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(senhaField, gbc);

        // Campo Confirmar Senha
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        JLabel confirmarSenhaLabel = new JLabel("Confirmar Senha:");
        confirmarSenhaLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(confirmarSenhaLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        confirmarSenhaField = new JPasswordField(20);
        confirmarSenhaField.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(confirmarSenhaField, gbc);

        // Checkbox para mostrar senha
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        showPasswordCheckBox = new JCheckBox("Mostrar senha");
        showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 11));
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());
        formPanel.add(showPasswordCheckBox, gbc);

        // Campo Perfil
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel perfilLabel = new JLabel("Perfil de Acesso:");
        perfilLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(perfilLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        perfilCombo = new JComboBox<>(PerfilAcesso.values());
        perfilCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(perfilCombo, gbc);

        // Painel de botões do formulário
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        saveButton = new JButton("Salvar Usuário");
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(140, 35));
        saveButton.addActionListener(e -> salvarUsuario());

        updateButton = new JButton("Atualizar Usuário");
        updateButton.setBackground(new Color(33, 150, 243));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFont(new Font("Arial", Font.BOLD, 12));
        updateButton.setFocusPainted(false);
        updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateButton.setPreferredSize(new Dimension(140, 35));
        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> atualizarUsuario());

        clearButton = new JButton("Limpar Formulário");
        clearButton.setBackground(new Color(158, 158, 158));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearButton.setFocusPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.setPreferredSize(new Dimension(140, 35));
        clearButton.addActionListener(e -> limparFormulario());

        formButtonPanel.add(saveButton);
        formButtonPanel.add(updateButton);
        formButtonPanel.add(clearButton);

        formPanel.add(formButtonPanel, gbc);

        return formPanel;
    }

    private JPanel criarTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(33, 150, 243)),
                "Lista de Usuários Cadastrados",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(33, 150, 243)
        ));

        // Tabela de usuários
        String[] colunas = {"ID", "Nome", "Email", "Perfil"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(33, 150, 243, 50));
        table.setSelectionBackground(new Color(33, 150, 243, 100));
        table.setSelectionForeground(Color.BLACK);

        // Evento de clique na tabela
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarFormulario();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel criarBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

        refreshButton = new JButton("Atualizar Lista");
        refreshButton.setBackground(new Color(0, 120, 215));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(150, 35));
        refreshButton.addActionListener(e -> carregarUsuarios());

        deleteButton = new JButton("Excluir Usuário");
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.setPreferredSize(new Dimension(150, 35));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> excluirUsuario());

        JLabel infoLabel = new JLabel("Dica: Selecione um usuário na tabela para editar ou excluir");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);

        bottomPanel.add(refreshButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(infoLabel);

        return bottomPanel;
    }

    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            senhaField.setEchoChar((char) 0);
            confirmarSenhaField.setEchoChar((char) 0);
        } else {
            senhaField.setEchoChar('*');
            confirmarSenhaField.setEchoChar('*');
        }
    }

    private void carregarUsuarios() {
        try {
            tableModel.setRowCount(0);
            List<Usuario> usuarios = usuarioService.listarTodosUsuarios(usuarioLogado);

            if (usuarios != null && !usuarios.isEmpty()) {
                for (Usuario u : usuarios) {
                    tableModel.addRow(new Object[]{
                            u.getId(),
                            u.getNome(),
                            u.getEmail(),
                            u.getPerfilAcesso() != null ? u.getPerfilAcesso().getDescricao() : "N/A"
                    });
                }
                System.out.println("Usuários carregados: " + usuarios.size());
            } else {
                System.out.println("Nenhum usuário encontrado");
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar usuários: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarFormulario() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            currentUserId = (int) tableModel.getValueAt(selectedRow, 0);
            String nome = (String) tableModel.getValueAt(selectedRow, 1);
            String email = (String) tableModel.getValueAt(selectedRow, 2);
            String perfil = (String) tableModel.getValueAt(selectedRow, 3);

            nomeField.setText(nome);
            emailField.setText(email);
            senhaField.setText("");
            confirmarSenhaField.setText("");

            // Selecionar o perfil correto no combo
            for (PerfilAcesso p : PerfilAcesso.values()) {
                if (p.getDescricao().equals(perfil)) {
                    perfilCombo.setSelectedItem(p);
                    break;
                }
            }

            // Habilitar/Desabilitar botões
            saveButton.setEnabled(false);
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }
    }

    private void salvarUsuario() {
        try {
            String nome = nomeField.getText().trim();
            String email = emailField.getText().trim();
            String senha = new String(senhaField.getPassword());
            String confirmarSenha = new String(confirmarSenhaField.getPassword());
            PerfilAcesso perfil = (PerfilAcesso) perfilCombo.getSelectedItem();

            // Validações
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O campo Nome é obrigatório!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                nomeField.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O campo Email é obrigatório!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                emailField.requestFocus();
                return;
            }

            // Validar formato do email
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this, "Email inválido!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O campo Senha é obrigatório!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                senhaField.requestFocus();
                return;
            }

            if (senha.length() < 6) {
                JOptionPane.showMessageDialog(this, "A senha deve ter no mínimo 6 caracteres!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                senhaField.requestFocus();
                return;
            }

            if (!senha.equals(confirmarSenha)) {
                JOptionPane.showMessageDialog(this, "As senhas não conferem!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                senhaField.requestFocus();
                return;
            }

            Usuario novo = new Usuario();
            novo.setNome(nome);
            novo.setEmail(email);
            novo.setSenha(senha);
            novo.setPerfilAcesso(perfil);
            novo.setGrupo("");

            Usuario saved = usuarioService.cadastrarUsuario(novo, usuarioLogado);

            if (saved != null && saved.getId() > 0) {
                carregarUsuarios();
                limparFormulario();
                JOptionPane.showMessageDialog(this,
                        "Usuário cadastrado com sucesso!\nID: " + saved.getId(),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro ao cadastrar usuário!\nVerifique se o email já existe.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void atualizarUsuario() {
        if (currentUserId == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para atualizar!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String nome = nomeField.getText().trim();
            String email = emailField.getText().trim();
            String senha = new String(senhaField.getPassword());
            PerfilAcesso perfil = (PerfilAcesso) perfilCombo.getSelectedItem();

            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O campo Nome é obrigatório!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                nomeField.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O campo Email é obrigatório!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                emailField.requestFocus();
                return;
            }

            Usuario usuario = new Usuario();
            usuario.setId(currentUserId);
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setPerfilAcesso(perfil);

            // Se a senha foi preenchida, atualiza, senão mantém a atual
            if (!senha.isEmpty()) {
                if (senha.length() < 6) {
                    JOptionPane.showMessageDialog(this, "A senha deve ter no mínimo 6 caracteres!",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    senhaField.requestFocus();
                    return;
                }
                usuario.setSenha(senha);
            }
            usuario.setGrupo("");

            Usuario updated = usuarioService.editarUsuario(usuario, usuarioLogado);

            if (updated != null) {
                carregarUsuarios();
                limparFormulario();
                JOptionPane.showMessageDialog(this,
                        "Usuário atualizado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro ao atualizar usuário!\nVerifique se o email já existe para outro usuário.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void excluirUsuario() {
        if (currentUserId == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para excluir!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Impedir exclusão do próprio admin
        if (currentUserId == usuarioLogado.getId()) {
            JOptionPane.showMessageDialog(this, "Você não pode excluir seu próprio usuário!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nome = nomeField.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir o usuário '" + nome + "'?\n" +
                        "Esta ação não pode ser desfeita!",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            usuarioService.excluirUsuario(currentUserId, usuarioLogado);
            carregarUsuarios();
            limparFormulario();
            JOptionPane.showMessageDialog(this,
                    "Usuário excluído com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void limparFormulario() {
        nomeField.setText("");
        emailField.setText("");
        senhaField.setText("");
        confirmarSenhaField.setText("");
        perfilCombo.setSelectedIndex(0);
        showPasswordCheckBox.setSelected(false);
        senhaField.setEchoChar('*');
        confirmarSenhaField.setEchoChar('*');
        table.clearSelection();
        currentUserId = 0;

        saveButton.setEnabled(true);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        nomeField.requestFocus();
        System.out.println("Formulário limpo");
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair do sistema?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginView().setVisible(true);
        }
    }
}