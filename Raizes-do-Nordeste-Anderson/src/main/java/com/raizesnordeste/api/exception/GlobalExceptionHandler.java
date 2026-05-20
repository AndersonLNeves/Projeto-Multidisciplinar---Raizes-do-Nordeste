package com.raizesnordeste.api.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Data @Builder
    public static class ApiError {
        private int status;
        private String erro;
        private String mensagem;
        private LocalDateTime timestamp;
        private Map<String, String> campos;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            campos.put(field, error.getDefaultMessage());
        });
        return ResponseEntity.status(422).body(ApiError.builder()
                .status(422).erro("Erro de validação")
                .mensagem("Um ou mais campos são inválidos")
                .timestamp(LocalDateTime.now()).campos(campos).build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String mensagem = "Valor inválido para o parâmetro '" + ex.getName() + "': " + ex.getValue();
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            mensagem += ". Valores aceitos: " + java.util.Arrays.toString(ex.getRequiredType().getEnumConstants());
        }
        return ResponseEntity.badRequest().body(ApiError.builder()
                .status(400).erro("Parâmetro inválido")
                .mensagem(mensagem).timestamp(LocalDateTime.now()).build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(401).body(ApiError.builder()
                .status(401).erro("Não autorizado")
                .mensagem("Email ou senha inválidos")
                .timestamp(LocalDateTime.now()).build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(403).body(ApiError.builder()
                .status(403).erro("Acesso negado")
                .mensagem("Você não tem permissão para realizar esta ação")
                .timestamp(LocalDateTime.now()).build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ApiError.builder()
                .status(400).erro("Requisição inválida")
                .mensagem(ex.getMessage())
                .timestamp(LocalDateTime.now()).build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(409).body(ApiError.builder()
                .status(409).erro("Conflito de regra de negócio")
                .mensagem(ex.getMessage())
                .timestamp(LocalDateTime.now()).build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Recurso não encontrado";
        HttpStatus status = msg.contains("não encontrad") ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(ApiError.builder()
                .status(status.value()).erro(status == HttpStatus.NOT_FOUND ? "Não encontrado" : "Erro interno")
                .mensagem(msg).timestamp(LocalDateTime.now()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return ResponseEntity.status(500).body(ApiError.builder()
                .status(500).erro("Erro interno do servidor")
                .mensagem("Ocorreu um erro inesperado. Tente novamente.")
                .timestamp(LocalDateTime.now()).build());
    }
}
