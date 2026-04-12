package com.sistema.eventos.view;

import com.sistema.eventos.model.Equipe;
import com.sistema.eventos.model.Projeto;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.service.EquipeService;
import com.sistema.eventos.service.ProjetoService;
import com.sistema.eventos.service.UsuarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;


public class GerenciarEquipeDialog extends JDialog {

    private int projetoId;
    private Usuario usuarioLogado;
    private EquipeService equipeService;
    private UsuarioService usuarioService;
    private ProjetoService projetoService;
    private JTable equipeTable;
    private DefaultTableModel equipeTableModel;
    private JList<String> membrosList;
    private DefaultListModel<String> membrosModel;
    private Equipe equipeSelecionada;

    public GerenciarEquipeDialog(JFrame parent, int projetoId, Usuario usuarioLogado) {
        super(parent, "Gerenciar Equipe", true);
        this.projetoId = projetoId;
        this.usuarioLogado = usuarioLogado;
        this.equipeService = new EquipeService();
        this.usuarioService = new UsuarioService();
        this.projetoService = new ProjetoService();
        initComponents();
        carregarEquipes();
    }

    private void initComponents() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel de equipes
        JPanel equipePanel = new JPanel(new BorderLayout());
        equipePanel.setBorder(BorderFactory.createTitledBorder("Equipes do Projeto"));

        String[] colunasEquipe = {"ID", "Nome da Equipe", "Qtd Membros"};
        equipeTableModel = new DefaultTableModel(colunasEquipe, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        equipeTable = new JTable(equipeTableModel);
        equipeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = equipeTable.getSelectedRow();
                if (selectedRow != -1) {
                    int equipeId = (int) equipeTableModel.getValueAt(selectedRow, 0);
                    equipeService.buscarPorId(equipeId).ifPresent(equipe -> {
                        this.equipeSelecionada = equipe;
                        carregarMembros();
                    });
                } else {
                    equipeSelecionada = null;
                    membrosModel.clear();
                }
            }
        });

        JScrollPane equipeScroll = new JScrollPane(equipeTable);
        equipeScroll.setPreferredSize(new Dimension(350, 300));
        equipePanel.add(equipeScroll, BorderLayout.CENTER);

        JPanel equipeButtons = new JPanel(new FlowLayout());
        JButton btnNovaEquipe = new JButton("Nova Equipe");
        JButton btnDeletarEquipe = new JButton("Deletar Equipe");
        JButton btnRefresh = new JButton("Atualizar");

        btnNovaEquipe.addActionListener(e -> criarEquipe());
        btnDeletarEquipe.addActionListener(e -> deletarEquipe());
        btnRefresh.addActionListener(e -> carregarEquipes());

        equipeButtons.add(btnNovaEquipe);
        equipeButtons.add(btnDeletarEquipe);
        equipeButtons.add(btnRefresh);
        equipePanel.add(equipeButtons, BorderLayout.SOUTH);

        // Painel de membros
        JPanel membroPanel = new JPanel(new BorderLayout());
        membroPanel.setBorder(BorderFactory.createTitledBorder("Membros da Equipe"));

        membrosModel = new DefaultListModel<>();
        membrosList = new JList<>(membrosModel);
        membrosList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane membroScroll = new JScrollPane(membrosList);
        membroScroll.setPreferredSize(new Dimension(350, 300));
        membroPanel.add(membroScroll, BorderLayout.CENTER);

        JPanel membroButtons = new JPanel(new FlowLayout());
        JButton btnAddMembro = new JButton("Adicionar Membro");
        JButton btnRemoveMembro = new JButton("Remover Membro");

        btnAddMembro.addActionListener(e -> adicionarMembro());
        btnRemoveMembro.addActionListener(e -> removerMembro());

        membroButtons.add(btnAddMembro);
        membroButtons.add(btnRemoveMembro);
        membroPanel.add(membroButtons, BorderLayout.SOUTH);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, equipePanel, membroPanel);
        splitPane.setDividerLocation(380);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Informacoes do projeto
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informacoes"));

        projetoService.buscarPorId(projetoId).ifPresent(projeto -> {
            JLabel lblProjeto = new JLabel("Projeto: " + projeto.getNome());
            lblProjeto.setFont(new Font("Arial", Font.BOLD, 12));
            infoPanel.add(lblProjeto);

            JLabel lblGerente = new JLabel("Gerente: " + usuarioLogado.getNome());
            lblGerente.setFont(new Font("Arial", Font.PLAIN, 12));
            infoPanel.add(lblGerente);
        });

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Botão fechar
        JPanel bottomPanel = new JPanel();
        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        bottomPanel.add(btnFechar);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void carregarEquipes() {
        System.out.println("=== CARREGANDO EQUIPES DO PROJETO ID: " + projetoId + " ===");
        equipeTableModel.setRowCount(0);

        List<Equipe> equipes = equipeService.buscarPorProjeto(projetoId);
        System.out.println("Equipes encontradas: " + equipes.size());

        for (Equipe eq : equipes) {
            int qtdMembros = eq.getMembros() != null ? eq.getMembros().size() : 0;
            equipeTableModel.addRow(new Object[]{
                    eq.getId(),
                    eq.getNomeEquipe(),
                    qtdMembros
            });
            System.out.println("  - ID: " + eq.getId() + " | Nome: " + eq.getNomeEquipe() + " | Membros: " + qtdMembros);
        }

        equipeSelecionada = null;
        membrosModel.clear();
        System.out.println("================================");
    }

    private void carregarMembros() {
        membrosModel.clear();

        if (equipeSelecionada != null) {
            System.out.println("Carregando membros da equipe: " + equipeSelecionada.getNomeEquipe());

            List<Usuario> membros = equipeSelecionada.getMembros();
            if (membros != null) {
                for (Usuario u : membros) {
                    membrosModel.addElement(String.format("%d - %s (%s)", u.getId(), u.getNome(), u.getEmail()));
                    System.out.println("  - Membro: " + u.getNome());
                }
            }
            System.out.println("Total de membros: " + membrosModel.size());
        }
    }

    private void deletarEquipe() {
        int selectedRow = equipeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma equipe para deletar!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int equipeId = (int) equipeTableModel.getValueAt(selectedRow, 0);
        String equipeNome = (String) equipeTableModel.getValueAt(selectedRow, 1);
        int qtdMembros = (int) equipeTableModel.getValueAt(selectedRow, 2);

        System.out.println("=== DELETANDO EQUIPE ===");
        System.out.println("ID: " + equipeId);
        System.out.println("Nome: " + equipeNome);
        System.out.println("Membros: " + qtdMembros);

        String mensagem = "Deseja realmente deletar a equipe '" + equipeNome + "'?\n" +
                "Membros na equipe: " + qtdMembros + "\n" +
                "Esta acao nao pode ser desfeita!";

        int confirm = JOptionPane.showConfirmDialog(this,
                mensagem,
                "Confirmar Exclusao",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean deletado = equipeService.excluirEquipe(equipeId, usuarioLogado);

            if (deletado) {
                System.out.println("Equipe deletada com sucesso!");
                carregarEquipes();
                membrosModel.clear();
                equipeSelecionada = null;
                JOptionPane.showMessageDialog(this,
                        "Equipe deletada com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.err.println("Erro ao deletar equipe!");
                JOptionPane.showMessageDialog(this,
                        "Erro ao deletar equipe!\nVerifique se a equipe nao possui tarefas associadas.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        System.out.println("================================");
    }

    private void adicionarMembro() {
        if (equipeSelecionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma equipe primeiro!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("=== ADICIONANDO MEMBRO A EQUIPE: " + equipeSelecionada.getNomeEquipe() + " ===");

        List<Usuario> todosUsuarios = usuarioService.listarTodosUsuarios(usuarioLogado);
        System.out.println("Total de usuarios no sistema: " + todosUsuarios.size());

        List<Usuario> usuariosDisponiveis = todosUsuarios.stream()
                .filter(u -> equipeSelecionada.getMembros() == null ||
                        !equipeSelecionada.getMembros().stream().anyMatch(m -> m.getId() == u.getId()))
                .collect(Collectors.toList());

        System.out.println("Usuarios disponiveis: " + usuariosDisponiveis.size());

        if (usuariosDisponiveis.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos os usuários já estão nesta equipe!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomes = usuariosDisponiveis.stream()
                .map(u -> u.getId() + " - " + u.getNome() + " (" + u.getEmail() + ")")
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "Selecione um usuário para adicionar a equipe:",
                "Adicionar Membro",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nomes,
                nomes[0]);

        if (selected != null) {
            int usuarioId = Integer.parseInt(selected.split(" - ")[0]);
            var usuarioOpt = usuarioService.buscarPorId(usuarioId);

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                System.out.println("Adicionando usuario: " + usuario.getNome());

                // CORRIGIDO: Método void - não atribuir a variável
                equipeService.adicionarMembroEquipe(equipeSelecionada, usuario, usuarioLogado);

                // Recarregar a equipe para pegar os membros atualizados
                equipeService.buscarPorId(equipeSelecionada.getId()).ifPresent(eq -> {
                    this.equipeSelecionada = eq;
                    carregarMembros();
                });

                // Atualizar a tabela para mostrar a nova quantidade de membros
                carregarEquipes();

                JOptionPane.showMessageDialog(this,
                        "Membro adicionado com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
        System.out.println("================================");
    }

    private void removerMembro() {
        if (equipeSelecionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma equipe primeiro!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedMembro = membrosList.getSelectedValue();
        if (selectedMembro == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um membro para remover!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("=== REMOVENDO MEMBRO DA EQUIPE: " + equipeSelecionada.getNomeEquipe() + " ===");

        int usuarioId = Integer.parseInt(selectedMembro.split(" - ")[0]);
        String usuarioNome = selectedMembro.split(" - ")[1].split(" \\(")[0];

        System.out.println("Removendo usuario: " + usuarioNome);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente remover o membro '" + usuarioNome + "' da equipe?",
                "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            var usuarioOpt = usuarioService.buscarPorId(usuarioId);

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();

                // CORRIGIDO: Método void - não atribuir a variável
                equipeService.removerMembroEquipe(equipeSelecionada, usuario, usuarioLogado);

                // Recarregar a equipe para pegar os membros atualizados
                equipeService.buscarPorId(equipeSelecionada.getId()).ifPresent(eq -> {
                    this.equipeSelecionada = eq;
                    carregarMembros();
                });

                // Atualizar a tabela para mostrar a nova quantidade de membros
                carregarEquipes();

                JOptionPane.showMessageDialog(this,
                        "Membro removido com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
        System.out.println("================================");
    }

    private void criarEquipe() {
        String nome = JOptionPane.showInputDialog(this,
                "Digite o nome da equipe:",
                "Nova Equipe",
                JOptionPane.QUESTION_MESSAGE);

        if (nome != null && !nome.trim().isEmpty()) {
            System.out.println("=== CRIANDO NOVA EQUIPE ===");
            System.out.println("Nome: " + nome);
            System.out.println("Projeto ID: " + projetoId);

            var projetoOpt = projetoService.buscarPorId(projetoId);
            if (projetoOpt.isPresent()) {
                Projeto projeto = projetoOpt.get();
                Equipe novaEquipe = new Equipe();
                novaEquipe.setNomeEquipe(nome.trim());

                Equipe saved = equipeService.criarEquipe(novaEquipe, usuarioLogado);

                if (saved != null && saved.getId() > 0) {
                    System.out.println("Equipe criada com ID: " + saved.getId());

                    // CORRIGIDO: Método void - não atribuir a variável
                    equipeService.alocarEquipeProjeto(saved, projeto, usuarioLogado);
                    System.out.println("Equipe alocada ao projeto: " + projeto.getNome());

                    // Recarregar lista
                    carregarEquipes();

                    JOptionPane.showMessageDialog(this,
                            "Equipe '" + nome + "' criada e alocada ao projeto com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    System.err.println("Erro: Equipe nao foi salva!");
                    JOptionPane.showMessageDialog(this,
                            "Erro ao criar equipe! Verifique os logs.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.err.println("Erro: Projeto nao encontrado com ID: " + projetoId);
                JOptionPane.showMessageDialog(this,
                        "Projeto nao encontrado!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
            System.out.println("================================");
        }
    }
}