package br.com.orbitapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TarefaConsistenteValidator.class)
public @interface TarefaConsistente {

    String message() default "Revise os campos da tarefa!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}