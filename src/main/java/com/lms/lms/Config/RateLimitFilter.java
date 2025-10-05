package com.lms.lms.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * Simple in-memory sliding-window rate limiter.
 * - 60 requests per 60 seconds per user (X-User-Id header). Falls back to IP.
 *
 * NOTE: For production use a centralized store (Redis) instead of in-memory.
 */
@Component
public class RateLimitFilter implements Filter {

    private static final int LIMIT = 60;
    private static final long WINDOW_MS = 60_000L;

    // user id -> deque of request timestamps (ms)
    private final ConcurrentHashMap<String, Deque<Long>> store = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }

    private void writeRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Per project rule: respond with {"error": {"code":"RATE_LIMIT"}}
        Map<String, Object> body = Map.of("error", Map.of("code", "RATE_LIMIT"));
        response.getWriter().write(mapper.writeValueAsString(body));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String userId = Optional.ofNullable(request.getHeader("X-User-Id"))
                .orElseGet(request::getRemoteAddr);

        long now = Instant.now().toEpochMilli();

        Deque<Long> deque = store.computeIfAbsent(userId, k -> new ConcurrentLinkedDeque<>());

        synchronized (deque) {
            // drop timestamps older than window
            while (!deque.isEmpty() && deque.peekFirst() < now - WINDOW_MS) {
                deque.pollFirst();
            }

            if (deque.size() >= LIMIT) {
                writeRateLimitResponse(response);
                return;
            }

            deque.addLast(now);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no-op
    }
}
