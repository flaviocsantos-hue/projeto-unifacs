package com.sistema.eventos.util;

import com.sistema.eventos.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sistema.eventos.model.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateUtil {

    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = buildSessionFactory();
        } catch (Exception e) {
            log.error("Erro ao criar SessionFactory: {}", e.getMessage(), e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();

            // Configurações do Hibernate para MySQL
            Properties settings = new Properties();

            // Configuração do MySQL
            settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
            settings.put(Environment.URL, "jdbc:mysql://localhost:3306/gerenciamento_tarefas?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
            settings.put(Environment.USER, "root");
            settings.put(Environment.PASS, "root123");

            // Dialeto MySQL
            settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");

            // Mostrar SQL
            settings.put(Environment.SHOW_SQL, "true");
            settings.put(Environment.FORMAT_SQL, "true");

            // Atualizar esquema automaticamente
            settings.put(Environment.HBM2DDL_AUTO, "update");

            // Pool de conexões (C3P0)
            settings.put(Environment.C3P0_MIN_SIZE, "5");
            settings.put(Environment.C3P0_MAX_SIZE, "20");
            settings.put(Environment.C3P0_TIMEOUT, "300");
            settings.put(Environment.C3P0_MAX_STATEMENTS, "50");
            settings.put(Environment.C3P0_IDLE_TEST_PERIOD, "3000");

            // Configurações adicionais
            settings.put(Environment.ENABLE_LAZY_LOAD_NO_TRANS, "true");
            settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

            // Pool de conexões HikariCP (alternativa ao C3P0)
            settings.put("hibernate.hikari.minimumIdle", "5");
            settings.put("hibernate.hikari.maximumPoolSize", "20");
            settings.put("hibernate.hikari.idleTimeout", "300000");

            configuration.setProperties(settings);

            // Adiciona as classes anotadas
            configuration.addAnnotatedClass(Usuario.class);
            configuration.addAnnotatedClass(Projeto.class);
            configuration.addAnnotatedClass(Equipe.class);
            configuration.addAnnotatedClass(Tarefa.class);
            configuration.addAnnotatedClass(Dashboard.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            log.info("SessionFactory criada com sucesso para MySQL!");
            return configuration.buildSessionFactory(serviceRegistry);

        } catch (Throwable ex) {
            log.error("Falha ao criar SessionFactory: {}", ex.getMessage(), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory não foi inicializada!");
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            log.info("SessionFactory fechada");
        }
    }
}