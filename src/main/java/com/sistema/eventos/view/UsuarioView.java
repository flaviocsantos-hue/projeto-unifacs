package com.sistema.eventos.view;

import com.sistema.eventos.model.Projeto;
import com.sistema.eventos.model.StatusTarefa;
import com.sistema.eventos.model.Tarefa;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.service.ProjetoService;
import com.sistema.eventos.service.TarefaService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import javax.swing.border.EmptyBorder;



public class UsuarioView extends JFrame {

    private Usuario usuarioLogado;
    private TarefaService tarefaService;
    private ProjetoService projetoService;
    private JTable tarefaTable;
    private DefaultTableModel tarefaTableModel;
    private JComboBox<String> filtroStatusCombo;

    public UsuarioView(Usuario usuario) {
        this.usuarioLogado = usuario;
        this.tarefaService = new TarefaService();
        this.projetoService = new ProjetoService();
        initComponents();
        carregarTarefas();

        // Garantir que ao fechar a janela pelo "X" volte para o login
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                voltarParaLogin();
            }
        });
    }

    private void initComponents() {
        setTitle("Usuário - Minhas Tarefas");
        setSize(1000, 700);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        // Topo
        JPanel topPanel = criarTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Painel de filtros
        JPanel filterPanel = criarFilterPanel();
        add(filterPanel, BorderLayout.NORTH);

        // Tabela de tarefas
        String[] colunas = {"ID", "Descrição", "Status", "Horas Estimadas", "Horas Trabalhadas", "Projeto"};
        tarefaTableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tarefaTable = new JTable(tarefaTableModel);
        tarefaTable.setFont(new Font("Arial", Font.PLAIN, 12));
        tarefaTable.setRowHeight(30);
        tarefaTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(tarefaTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Minhas Tarefas"));
        add(scrollPane, BorderLayout.CENTER);

        // Botões
        JPanel buttonPanel = criarButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel criarTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(33, 150, 243));
        topPanel.setPreferredSize(new Dimension(1000, 80));

        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setBackground(new Color(33, 150, 243));

        JLabel titleLabel = new JLabel("Sistema de Gerenciamento de Tarefas");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomePanel.add(titleLabel);

        JLabel userLabel = new JLabel(" - Usuário: " + usuarioLogado.getNome());
        userLabel.setForeground(new Color(255, 255, 200));
        userLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        welcomePanel.add(userLabel);

        topPanel.add(welcomePanel, BorderLayout.WEST);

        // Painel de botões do topo
        JPanel topButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        topButtonsPanel.setBackground(new Color(33, 150, 243));

        JButton logoutButton = new JButton("Sair do Sistema");
        logoutButton.setBackground(new Color(244, 67, 54));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(130, 40));
        logoutButton.addActionListener(e -> logout());
        topButtonsPanel.add(logoutButton);

        topPanel.add(topButtonsPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel criarFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(new EmptyBorder(10, 10, 5, 10));

        filterPanel.add(new JLabel("Filtrar por status:"));

        filtroStatusCombo = new JComboBox<>();
        filtroStatusCombo.addItem("Todos");
        for (StatusTarefa status : StatusTarefa.values()) {
            filtroStatusCombo.addItem(status.getDescricao());
        }
        filtroStatusCombo.addActionListener(e -> carregarTarefas());
        filterPanel.add(filtroStatusCombo);

        JButton btnRefresh = new JButton("Atualizar Lista");
        btnRefresh.setBackground(new Color(33, 150, 243));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 11));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> carregarTarefas());
        filterPanel.add(btnRefresh);

        return filterPanel;
    }

    private JPanel criarButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton addTarefa = criarBotao("Nova Tarefa", new Color(76, 175, 80));
        JButton editTarefa = criarBotao("Editar Tarefa", new Color(33, 150, 243));
        JButton deleteTarefa = criarBotao("Excluir Tarefa", new Color(244, 67, 54));
        JButton registrarHoras = criarBotao("Registrar Horas", new Color(255, 152, 0));
        JButton atualizarStatus = criarBotao("Atualizar Status", new Color(156, 39, 176));
        JButton detalhesTarefa = criarBotao("Detalhes da Tarefa", new Color(96, 125, 139));

        addTarefa.addActionListener(e -> adicionarTarefa());
        editTarefa.addActionListener(e -> editarTarefa());
        deleteTarefa.addActionListener(e -> excluirTarefa());
        registrarHoras.addActionListener(e -> registrarHoras());
        atualizarStatus.addActionListener(e -> atualizarStatus());
        detalhesTarefa.addActionListener(e -> mostrarDetalhes());

        buttonPanel.add(addTarefa);
        buttonPanel.add(editTarefa);
        buttonPanel.add(deleteTarefa);
        buttonPanel.add(registrarHoras);
        buttonPanel.add(atualizarStatus);
        buttonPanel.add(detalhesTarefa);

        return buttonPanel;
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(150, 40));
        return botao;
    }

    private void carregarTarefas() {
        tarefaTableModel.setRowCount(0);
        List<Tarefa> tarefas = tarefaService.listarMinhasTarefas(usuarioLogado);

        String filtro = (String) filtroStatusCombo.getSelectedItem();

        for (Tarefa t : tarefas) {
            // Aplicar filtro
            if (filtro != null && !filtro.equals("Todos")) {
                if (!t.getStatus().getDescricao().equals(filtro)) {
                    continue;
                }
            }

            tarefaTableModel.addRow(new Object[]{
                    t.getId(),
                    t.getDescricao(),
                    t.getStatus().getDescricao(),
                    t.getHorasEstimadas(),
                    t.getHorasTrabalhadas(),
                    t.getProjeto() != null ? t.getProjeto().getNome() : "Sem projeto"
            });
        }

        System.out.println("Tarefas carregadas: " + tarefas.size());
    }

    private void adicionarTarefa() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField descricaoField = new JTextField(20);
        JSpinner horasSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

        // Carregar projetos disponíveis (projetos que o usuário participa)
        List<Projeto> projetos = projetoService.listarProjetos(usuarioLogado);
        JComboBox<String> projetoBox = new JComboBox<>();
        for (Projeto p : projetos) {
            projetoBox.addItem(p.getId() + " - " + p.getNome());
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Descrição:*"), gbc);
        gbc.gridx = 1;
        panel.add(descricaoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Horas Estimadas:*"), gbc);
        gbc.gridx = 1;
        panel.add(horasSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Projeto:*"), gbc);
        gbc.gridx = 1;
        panel.add(projetoBox, gbc);

        int option = JOptionPane.showConfirmDialog(this, panel, "Nova Tarefa",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION && projetoBox.getSelectedItem() != null) {
            try {
                String descricao = descricaoField.getText().trim();
                if (descricao.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "A descrição é obrigatória!",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selected = (String) projetoBox.getSelectedItem();
                int projetoId = Integer.parseInt(selected.split(" - ")[0]);
                var projetoOpt = projetoService.buscarPorId(projetoId);

                if (projetoOpt.isPresent()) {
                    Tarefa tarefa = new Tarefa(descricao, StatusTarefa.PENDENTE, (int) horasSpinner.getValue());
                    tarefa.setResponsavel(usuarioLogado);

                    Tarefa saved = tarefaService.criarTarefa(tarefa, projetoOpt.get(), usuarioLogado);

                    if (saved != null) {
                        carregarTarefas();
                        JOptionPane.showMessageDialog(this,
                                "Tarefa criada com sucesso!",
                                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Erro ao criar tarefa!",
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void editarTarefa() {
        int selectedRow = tarefaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tarefaTableModel.getValueAt(selectedRow, 0);
        var tarefaOpt = tarefaService.buscarPorId(id);

        if (tarefaOpt.isPresent()) {
            Tarefa t = tarefaOpt.get();

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JTextField descricaoField = new JTextField(t.getDescricao(), 20);
            JSpinner horasSpinner = new JSpinner(new SpinnerNumberModel(t.getHorasEstimadas(), 1, 1000, 1));

            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Descrição:"), gbc);
            gbc.gridx = 1;
            panel.add(descricaoField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("Horas Estimadas:"), gbc);
            gbc.gridx = 1;
            panel.add(horasSpinner, gbc);

            int option = JOptionPane.showConfirmDialog(this, panel, "Editar Tarefa",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                t.setDescricao(descricaoField.getText().trim());
                t.setHorasEstimadas((int) horasSpinner.getValue());

                boolean atualizado = tarefaService.atualizarTarefa(t, usuarioLogado);

                if (atualizado) {
                    carregarTarefas();
                    JOptionPane.showMessageDialog(this,
                            "Tarefa atualizada com sucesso!",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erro ao atualizar tarefa!",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void excluirTarefa() {
        int selectedRow = tarefaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tarefaTableModel.getValueAt(selectedRow, 0);
        String descricao = (String) tarefaTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir a tarefa '" + descricao + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean excluido = tarefaService.excluirTarefa(id, usuarioLogado);

            if (excluido) {
                carregarTarefas();
                JOptionPane.showMessageDialog(this,
                        "Tarefa excluída com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro ao excluir tarefa!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void registrarHoras() {
        int selectedRow = tarefaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tarefaTableModel.getValueAt(selectedRow, 0);
        String descricao = (String) tarefaTableModel.getValueAt(selectedRow, 1);

        String horasStr = JOptionPane.showInputDialog(this,
                "Quantas horas trabalhou na tarefa '" + descricao + "'?\n" +
                        "Horas já registradas: " + tarefaTableModel.getValueAt(selectedRow, 4),
                "Registrar Horas",
                JOptionPane.QUESTION_MESSAGE);

        if (horasStr != null && !horasStr.trim().isEmpty()) {
            try {
                int horas = Integer.parseInt(horasStr);
                if (horas <= 0) {
                    JOptionPane.showMessageDialog(this, "Digite um valor positivo!",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                var tarefaOpt = tarefaService.buscarPorId(id);
                if (tarefaOpt.isPresent()) {
                    boolean registrado = tarefaService.registrarHorasTarefa(tarefaOpt.get(), horas, usuarioLogado);

                    if (registrado) {
                        carregarTarefas();
                        JOptionPane.showMessageDialog(this,
                                "Horas registradas com sucesso!",
                                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Erro ao registrar horas!",
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Digite um número válido!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void atualizarStatus() {
        int selectedRow = tarefaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tarefaTableModel.getValueAt(selectedRow, 0);
        String descricao = (String) tarefaTableModel.getValueAt(selectedRow, 1);

        StatusTarefa[] statuses = {StatusTarefa.PENDENTE, StatusTarefa.EM_ANDAMENTO, StatusTarefa.CONCLUIDA, StatusTarefa.BLOQUEADA};
        String[] statusNomes = {
                StatusTarefa.PENDENTE.getDescricao(),
                StatusTarefa.EM_ANDAMENTO.getDescricao(),
                StatusTarefa.CONCLUIDA.getDescricao(),
                StatusTarefa.BLOQUEADA.getDescricao()
        };

        String novoStatusNome = (String) JOptionPane.showInputDialog(this,
                "Selecione o novo status para a tarefa '" + descricao + "':\nStatus atual: " + tarefaTableModel.getValueAt(selectedRow, 2),
                "Atualizar Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statusNomes,
                statusNomes[0]);

        if (novoStatusNome != null) {
            StatusTarefa novoStatus = null;
            for (StatusTarefa s : statuses) {
                if (s.getDescricao().equals(novoStatusNome)) {
                    novoStatus = s;
                    break;
                }
            }

            if (novoStatus != null) {
                var tarefaOpt = tarefaService.buscarPorId(id);
                if (tarefaOpt.isPresent()) {
                    boolean atualizado = tarefaService.atualizarStatusTarefa(tarefaOpt.get(), novoStatus, usuarioLogado);

                    if (atualizado) {
                        carregarTarefas();
                        JOptionPane.showMessageDialog(this,
                                "Status atualizado com sucesso!",
                                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Erro ao atualizar status!",
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private void mostrarDetalhes() {
        int selectedRow = tarefaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tarefaTableModel.getValueAt(selectedRow, 0);
        var tarefaOpt = tarefaService.buscarPorId(id);

        if (tarefaOpt.isPresent()) {
            Tarefa t = tarefaOpt.get();

            StringBuilder detalhes = new StringBuilder();
            detalhes.append("=== DETALHES DA TAREFA ===\n\n");
            detalhes.append("ID: ").append(t.getId()).append("\n");
            detalhes.append("Descrição: ").append(t.getDescricao()).append("\n");
            detalhes.append("Status: ").append(t.getStatus().getDescricao()).append("\n");
            detalhes.append("Horas Estimadas: ").append(t.getHorasEstimadas()).append("\n");
            detalhes.append("Horas Trabalhadas: ").append(t.getHorasTrabalhadas()).append("\n");
            detalhes.append("Progresso: ").append(t.getHorasTrabalhadas() * 100 / Math.max(t.getHorasEstimadas(), 1)).append("%\n");
            detalhes.append("Projeto: ").append(t.getProjeto() != null ? t.getProjeto().getNome() : "Sem projeto").append("\n");
            detalhes.append("Responsável: ").append(t.getResponsavel() != null ? t.getResponsavel().getNome() : "Não atribuído").append("\n");

            JTextArea textArea = new JTextArea(detalhes.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Detalhes da Tarefa",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void voltarParaLogin() {
        // Verificar se a tela de login já não está aberta
        for (Frame frame : Frame.getFrames()) {
            if (frame instanceof LoginView && frame.isVisible()) {
                frame.toFront();
                return;
            }
        }
        new LoginView().setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair do sistema?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            voltarParaLogin();
        }
    }
}