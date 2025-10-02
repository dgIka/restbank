package com.example.bankcards.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.bankcards.security.SecurityCnstants.*;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(final JwtService jwtService, final UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(AUTH_HEADER);

        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            try {
                // 1) Валидируем токен (сигнатура/срок)
                var claims = jwtService.parse(token).getBody();

                // 2) Достаём логин (subject) и грузим UserDetails
                String email = claims.getSubject();
                var userDetails = userDetailsService.loadUserByUsername(email);

                // 3) Кладём аутентификацию в контекст
                var authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // Ничего не делаем: контекст пустой → дальше поймается как 401/403 на безопасности
                SecurityContextHolder.clearContext();
            }
        }

        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Пропускаем фильтр для открытых эндпоинтов
        String uri = request.getRequestURI();
        for (String open : OPEN_ENDPOINTS) {
            String prefix = open.replace("**", "");
            if (uri.startsWith(prefix)) return true;
        }
        return false;
    }
}
