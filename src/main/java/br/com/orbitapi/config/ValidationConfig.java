package br.com.orbitapi.config;

import org.springframework.boot.validation.autoconfigure.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

    @Bean
    public ValidationConfigurationCustomizer mensagensComMetodosDoValorValidado() {
        return configuration -> configuration.addProperty("hibernate.validator.constraint_expression_language_feature_level", "bean-methods");
    }
}