package com.sistema.eventos.util;

import com.sistema.eventos.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sistema.eventos.model.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import java.util.Properties;
import com.sistema.eventos.model.*;

import java.io.InputStream;
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

            // Carrega o arquivo hibernate.properties
            Properties properties = new Properties();
            InputStream inputStream = HibernateUtil.class.getClassLoader()
                    .getResourceAsStream("hibernate.properties");

            if (inputStream != null) {
                properties.load(inputStream);
                configuration.setProperties(properties);
                log.info("Arquivo hibernate.properties carregado com sucesso!");
            } else {
                log.error("Arquivo hibernate.properties não encontrado!");
                throw new RuntimeException("hibernate.properties não encontrado");
            }

            // Adiciona as classes anotadas
            configuration.addAnnotatedClass(Usuario.class);
            configuration.addAnnotatedClass(Projeto.class);
            configuration.addAnnotatedClass(Equipe.class);
            configuration.addAnnotatedClass(Tarefa.class);
            configuration.addAnnotatedClass(Dashboard.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            log.info("SessionFactory criada com sucesso!");
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