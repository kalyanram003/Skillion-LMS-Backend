
package com.lms.lms.Config;

import com.lms.lms.Service.IdempotencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;

/**
 * Enforces presence of Idempotency-Key header for POST requests that create resources.
 * - If missing on POST: returns 400 with uniform error JSON.
 * - If present and key already stored, attaches existing resource id to the request attributes
 *   under "idempotentResourceId" so controllers can return the stored resource instead of creating a duplicate.
 *
 * Controllers still must use IdempotencyService to save mapping after successful create.
 */
@Component
public class IdempotencyInterceptor implements HandlerInterceptor {

    private final IdempotencyService idempotencyService;
    private final ObjectMapper mapper = new ObjectMapper();

    public IdempotencyInterceptor(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    private void writeMissingIdempotencyResponse(HttpServletResponse response) throws IOException {
        response.setStatus(400);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = Map.of("error", Map.of(
                "code", "FIELD_REQUIRED",
                "field", "Idempotency-Key",
                "message", "Idempotency-Key header is required for create operations"
        ));
        response.getWriter().write(mapper.writeValueAsString(body));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // Only enforce for POST requests under /api/ (resource creation endpoints).
        // Exclude auth endpoints from idempotency requirement
        if ("POST".equalsIgnoreCase(request.getMethod()) && 
            request.getRequestURI().startsWith("/api/") && 
            !request.getRequestURI().startsWith("/api/auth/")) {
            String key = request.getHeader("Idempotency-Key");
            if (key == null || key.isBlank()) {
                writeMissingIdempotencyResponse(response);
                return false;
            }

            // If key exists in store, attach its resource id to request attributes for controllers to reuse.
            if (idempotencyService.exists(key)) {
                String resourceId = idempotencyService.get(key);
                // set attribute so controllers can read and return existing resource
                request.setAttribute("idempotentResourceId", resourceId);
            }
        }

        return true;
    }
}
