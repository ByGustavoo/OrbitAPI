package br.com.orbitapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SessaoConsistenteValidator.class)
public @interface SessaoConsistente {

    String message() default "Revise os campos da sessão!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}