package com.sistema.eventos.view;


import com.sistema.eventos.model.StatusTarefa;
import com.sistema.eventos.model.Tarefa;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.service.ProjetoService;
import com.sistema.eventos.service.TarefaService;
import com.sistema.eventos.service.UsuarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TarefasProjetoDialog extends JDialog {

    private int projetoId;
    private Usuario usuarioLogado;
    private TarefaService tarefaService;
    private JTable tarefaTable;
    private DefaultTableModel tableModel;

    public TarefasProjetoDialog(JFrame parent, int projetoId, Usuario usuarioLogado) {
        super(parent, "Tarefas do Projeto", true);
        this.projetoId = projetoId;
        this.usuarioLogado = usuarioLogado;
        this.tarefaService = new TarefaService();
        initComponents();
        carregarTarefas();
    }

    private void initComponents() {
        setSize(800, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        String[] colunas = {"ID", "Descrição", "Status", "Horas Estimadas", "Horas Trabalhadas", "Responsável"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tarefaTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tarefaTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton novaTarefa = new JButton("Nova Tarefa");
        JButton fechar = new JButton("Fechar");

        novaTarefa.addActionListener(e -> adicionarTarefa());
        fechar.addActionListener(e -> dispose());

        buttonPanel.add(novaTarefa);
        buttonPanel.add(fechar);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void carregarTarefas() {
        tableModel.setRowCount(0);
        List<Tarefa> tarefas = tarefaService.listarTarefasPorProjeto(projetoId, usuarioLogado);
        for (Tarefa t : tarefas) {
            tableModel.addRow(new Object[]{
                    t.getId(), t.getDescricao(), t.getStatus().getDescricao(),
                    t.getHorasEstimadas(), t.getHorasTrabalhadas(),
                    t.getResponsavel() != null ? t.getResponsavel().getNome() : "Não atribuído"
            });
        }
    }

    private void adicionarTarefa() {
        JTextField descricaoField = new JTextField();
        JSpinner horasSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

        // Listar usuários para atribuir
        List<Usuario> usuarios = new UsuarioService().listarTodosUsuarios(usuarioLogado);
        JComboBox<String> usuarioBox = new JComboBox<>();
        for (Usuario u : usuarios) {
            usuarioBox.addItem(u.getId() + " - " + u.getNome());
        }

        Object[] message = {
                "Descrição:", descricaoField,
                "Horas Estimadas:", horasSpinner,
                "Responsável:", usuarioBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Nova Tarefa", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                var projetoOpt = new ProjetoService().buscarPorId(projetoId);
                if (projetoOpt.isPresent()) {
                    Tarefa tarefa = new Tarefa(descricaoField.getText(), StatusTarefa.PENDENTE, (int) horasSpinner.getValue());
                    tarefa.setProjeto(projetoOpt.get());

                    if (usuarioBox.getSelectedItem() != null) {
                        String selected = (String) usuarioBox.getSelectedItem();
                        int usuarioId = Integer.parseInt(selected.split(" - ")[0]);
                        var usuarioOpt = new UsuarioService().buscarPorId(usuarioId);
                        usuarioOpt.ifPresent(tarefa::setResponsavel);
                    }

                    tarefaService.criarTarefa(tarefa, projetoOpt.get(), usuarioLogado);
                    carregarTarefas();
                    JOptionPane.showMessageDialog(this, "Tarefa criada!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        }
    }
}