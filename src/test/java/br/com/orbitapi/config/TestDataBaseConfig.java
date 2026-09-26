package br.com.orbitapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@TestConfiguration
public class TestDataBaseConfig {

    @Value("${DATABASE_IP:localhost}")
    private String ip;

    @Value("${DATABASE_PORT:5433}")
    private String porta;

    @Value("${DATABASE_USER:postgres}")
    private String usuario;

    @Value("${DATABASE_TEST_NAME:orbit_teste}")
    private String nome;

    @Value("${DATABASE_PASSWORD:orbitAPI@2026}")
    private String senha;

    private String getUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s", ip, porta, nome);
    }

    @Bean
    public DataSource dataSourceTest() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();

        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url(getUrl());
        dataSourceBuilder.username(usuario);
        dataSourceBuilder.password(senha);

        return dataSourceBuilder.build();
    }
}