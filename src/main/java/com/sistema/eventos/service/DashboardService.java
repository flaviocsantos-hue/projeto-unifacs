package com.sistema.eventos.service;

import com.sistema.eventos.model.Dashboard;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.repository.DashboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);
    private DashboardRepository dashboardRepository;

    public DashboardService() {
        this.dashboardRepository = new DashboardRepository();
    }

    public void visualizarDashboard(Dashboard dashboard, Usuario usuario) {
        dashboard.visualizarDashboards(usuario);
    }

    public void tomarDecisao(Dashboard dashboard, Usuario usuario) {
        dashboard.tomarDecisao(usuario);
    }

    public void adicionarIndicador(Dashboard dashboard, String indicador, Usuario usuario) {
        dashboard.adicionarIndicador(indicador, usuario);
        dashboardRepository.atualizar(dashboard);
    }

    public void adicionarProjetoPortfolio(Dashboard dashboard, String projetoNome, Usuario usuario) {
        dashboard.adicionarProjetoPortfolio(projetoNome, usuario);
        dashboardRepository.atualizar(dashboard);
    }
}
