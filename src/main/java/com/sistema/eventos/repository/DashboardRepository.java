package com.sistema.eventos.repository;


import com.sistema.eventos.model.Dashboard;

public class DashboardRepository extends BaseRepository<Dashboard, Integer> {

    public DashboardRepository() {
        super(Dashboard.class);
    }
}
