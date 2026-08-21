package com.tkcoder.authify.filter;


import com.tkcoder.authify.service.AppUserDetailsService;
import com.tkcoder.authify.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_URL = List.of(
            "/login",
            "/logout",
            "/refresh-token",
            "/sent-reset-otp",
            "/reset-password",
            "/register");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getServletPath();

        // public endpoint
        if (PUBLIC_URL.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = null;

        //1. check the authorization header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
        {
            jwt = authorizationHeader.substring(7);
        }

        //2. Validate token type access and set security context
        if (jwt != null) {

            try {
                String tokenType = jwtUtil.extractByType(jwt);
                // Only ACCESS tokens are accepted here
                if ("access".equals(tokenType)) {
                    String email = jwtUtil.extractByEmail(jwt);

                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                        UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);

                        if (jwtUtil.validateAccessToken(jwt, userDetails)) {
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );

                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                }

            } catch (ExpiredJwtException e) {

                // Access token expired.
                SecurityContextHolder.clearContext();

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("""
                        {
                            "error": true,
                            "code": "TOKEN_EXPIRED",
                            "message": "Access token has expired"
                        }
                        """);
                return;
            } catch (JwtException e) {

                // Jwt is malformed, invalid, or has an invalid signature.
                SecurityContextHolder.clearContext();

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("""
                        {
                            "error": true,
                            "code": "INVALID_TOKEN",
                            "message": "Invalid access token"
                        }
                        """);
                return;
            } catch (Exception e){
                // Unexpected authentication error.
                SecurityContextHolder.clearContext();

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("""
                        {
                            "error": true,
                            "code": "AUTHENTICATION_ERROR",
                            "message": "Authentication failed"
                        }
                        """);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
