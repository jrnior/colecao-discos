package br.com.colecao.discos.config;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ApiSecurityFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String API_KEY_SECRETA = "minha-chave-secreta-123";
    private static final String IDEMPOTENCY_HEADER = "X-Idempotency-Key";

    private final Set<String> chavesIdempotenciaProcessadas = ConcurrentHashMap.newKeySet();
    private final Map<String, AtomicInteger> rateLimitMap = new ConcurrentHashMap<>();
    private final Map<String, Long> rateLimitTimestamps = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 50;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/docs.html") ||
                path.startsWith("/css/")) {

            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey == null || !apiKey.equals(API_KEY_SECRETA)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Acesso negado: Chave de API ausente ou invalida.");
            return;
        }

        String clientIp = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        rateLimitMap.putIfAbsent(clientIp, new AtomicInteger(0));
        rateLimitTimestamps.putIfAbsent(clientIp, currentTime);

        if (currentTime - rateLimitTimestamps.get(clientIp) > 60000) {
            rateLimitMap.get(clientIp).set(0);
            rateLimitTimestamps.put(clientIp, currentTime);
        }

        if (rateLimitMap.get(clientIp).incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            response.getWriter().write("Limite de requisicoes excedido. Tente novamente mais tarde.");
            return;
        }

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String idempotencyKey = request.getHeader(IDEMPOTENCY_HEADER);
            if (idempotencyKey != null) {
                if (chavesIdempotenciaProcessadas.contains(idempotencyKey)) {
                    response.setStatus(HttpStatus.CONFLICT.value());
                    response.getWriter().write("Requisicao ignorada: Chave de idempotencia ja processada.");
                    return;
                }
                chavesIdempotenciaProcessadas.add(idempotencyKey);
            }
        }

        filterChain.doFilter(request, response);
    }
}