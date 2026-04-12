package com.sistema.eventos.view;


import com.sistema.eventos.model.Projeto;
import com.sistema.eventos.model.StatusProjeto;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.service.EquipeService;
import com.sistema.eventos.service.ProjetoService;
import com.sistema.eventos.service.UsuarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;


public class GerenteView extends JFrame {

    private Usuario usuarioLogado;
    private ProjetoService projetoService;
    private EquipeService equipeService;
    private UsuarioService usuarioService;
    private JTable projetoTable;
    private DefaultTableModel projetoTableModel;

    public GerenteView(Usuario usuario) {
        this.usuarioLogado = usuario;
        this.projetoService = new ProjetoService();
        this.equipeService = new EquipeService();
        this.usuarioService = new UsuarioService();
        initComponents();
        carregarProjetos();
    }

    private void initComponents() {
        setTitle("Gerente - Gerenciamento de Projetos");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Topo
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(0, 120, 215));
        topPanel.setPreferredSize(new Dimension(900, 60));

        JLabel titleLabel = new JLabel("Gerente - " + usuarioLogado.getNome());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel);

        JButton logoutButton = new JButton("Sair");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton);

        add(topPanel, BorderLayout.NORTH);

        // Tabela de projetos
        String[] colunas = {"ID", "Nome", "Status", "Progresso", "Data Início", "Data Fim"};
        projetoTableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        projetoTable = new JTable(projetoTableModel);
        JScrollPane scrollPane = new JScrollPane(projetoTable);
        add(scrollPane, BorderLayout.CENTER);

        // Botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addProjeto = criarBotao("Novo Projeto", new Color(40, 167, 69));
        JButton editProjeto = criarBotao("Editar Projeto", new Color(255, 193, 7));
        JButton deleteProjeto = criarBotao("Excluir Projeto", new Color(220, 53, 69));
        JButton gerenciarEquipe = criarBotao("Gerenciar Equipe", new Color(0, 123, 255));
        JButton verTarefas = criarBotao("Ver Tarefas", new Color(23, 162, 184));
        JButton refresh = criarBotao("Atualizar", new Color(108, 117, 125));

        addProjeto.addActionListener(e -> adicionarProjeto());
        editProjeto.addActionListener(e -> editarProjeto());
        deleteProjeto.addActionListener(e -> excluirProjeto());
        gerenciarEquipe.addActionListener(e -> gerenciarEquipe());
        verTarefas.addActionListener(e -> verTarefas());
        refresh.addActionListener(e -> carregarProjetos());

        buttonPanel.add(addProjeto);
        buttonPanel.add(editProjeto);
        buttonPanel.add(deleteProjeto);
        buttonPanel.add(gerenciarEquipe);
        buttonPanel.add(verTarefas);
        buttonPanel.add(refresh);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        return botao;
    }

    private void carregarProjetos() {
        projetoTableModel.setRowCount(0);
        List<Projeto> projetos = projetoService.listarProjetos(usuarioLogado);
        for (Projeto p : projetos) {
            projetoTableModel.addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    p.getStatus().getDescricao(),
                    String.format("%.1f", p.getProgresso()) + "%",
                    p.getDataInicio(),
                    p.getDataFim()
            });
        }
    }

    private void adicionarProjeto() {
        JTextField nomeField = new JTextField();
        JComboBox<StatusProjeto> statusBox = new JComboBox<>(StatusProjeto.values());

        Object[] message = {
                "Nome do Projeto:", nomeField,
                "Status:", statusBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Novo Projeto", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O nome do projeto é obrigatório!");
                return;
            }

            Projeto projeto = new Projeto(nome, new Date(), new Date(),
                    (StatusProjeto) statusBox.getSelectedItem(), 0);

            Projeto resultado = projetoService.criarProjeto(projeto, usuarioLogado);

            if (resultado != null) {
                carregarProjetos();
                JOptionPane.showMessageDialog(this, "Projeto criado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao criar projeto!");
            }
        }
    }

    private void editarProjeto() {
        int selectedRow = projetoTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto!");
            return;
        }

        int id = (int) projetoTableModel.getValueAt(selectedRow, 0);
        var projetoOpt = projetoService.buscarPorId(id);

        if (projetoOpt.isPresent()) {
            Projeto p = projetoOpt.get();

            JTextField nomeField = new JTextField(p.getNome());
            JComboBox<StatusProjeto> statusBox = new JComboBox<>(StatusProjeto.values());
            statusBox.setSelectedItem(p.getStatus());

            Object[] message = {
                    "Nome:", nomeField,
                    "Status:", statusBox
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Editar Projeto", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String novoNome = nomeField.getText().trim();
                if (novoNome.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "O nome do projeto é obrigatório!");
                    return;
                }

                p.setNome(novoNome);
                p.setStatus((StatusProjeto) statusBox.getSelectedItem());

                boolean atualizado = projetoService.editarProjeto(p, usuarioLogado);

                if (atualizado) {
                    carregarProjetos();
                    JOptionPane.showMessageDialog(this, "Projeto atualizado com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar projeto!");
                }
            }
        }
    }

    private void excluirProjeto() {
        int selectedRow = projetoTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Excluir projeto e todas as tarefas associadas?\nEsta ação não pode ser desfeita!",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) projetoTableModel.getValueAt(selectedRow, 0);
            boolean excluido = projetoService.excluirProjeto(id, usuarioLogado);

            if (excluido) {
                carregarProjetos();
                JOptionPane.showMessageDialog(this, "Projeto excluído com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir projeto!");
            }
        }
    }

    private void gerenciarEquipe() {
        int selectedRow = projetoTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto primeiro!");
            return;
        }

        int projetoId = (int) projetoTableModel.getValueAt(selectedRow, 0);
        new GerenciarEquipeDialog(this, projetoId, usuarioLogado).setVisible(true);
    }

    private void verTarefas() {
        int selectedRow = projetoTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto!");
            return;
        }

        int projetoId = (int) projetoTableModel.getValueAt(selectedRow, 0);
        new TarefasProjetoDialog(this, projetoId, usuarioLogado).setVisible(true);
    }

    private void logout() {
        dispose();
        new LoginView().setVisible(true);
    }
}