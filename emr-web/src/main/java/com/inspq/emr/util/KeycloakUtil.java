package com.inspq.emr.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.security.cert.X509Certificate;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.spi.AuthenticationError;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.spi.LogoutError;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.util.TokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.inspq.emr.repository.RefreshTokenDAO;

@Component
public class KeycloakUtil {
	
	public String getAccessToken(HttpServletRequest req) {
		RefreshableKeycloakSecurityContext ctx = (RefreshableKeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
		System.out.println("Access token is " + ctx.getTokenString());
		return ctx.getTokenString();
	}
	
	public KeycloakDeployment getDeployment(HttpServletRequest servletRequest, ServletContext servletContext) throws ServletException {
        // The facade object is needed just if you have relative "auth-server-url" in keycloak.json. Otherwise you can call deploymentContext.resolveDeployment(null)
		HttpFacade facade = getFacade(servletRequest);
        AdapterDeploymentContext deploymentContext = (AdapterDeploymentContext) servletContext.getAttribute(AdapterDeploymentContext.class.getName());
        if (deploymentContext == null) {
            throw new ServletException("AdapterDeploymentContext not set");
        }
        return deploymentContext.resolveDeployment(facade);
    }
	
	public void storeToken(HttpServletRequest req) throws IOException, JWSInputException {
        RefreshableKeycloakSecurityContext ctx = (RefreshableKeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
        String refreshToken = ctx.getRefreshToken();
        System.out.println("Refresh token to be saved is \n" + refreshToken);
        RefreshTokenDAO.saveToken(refreshToken);

        RefreshToken refreshTokenDecoded = TokenUtil.getRefreshToken(refreshToken);
        Boolean isOfflineToken = refreshTokenDecoded.getType().equals(TokenUtil.TOKEN_TYPE_OFFLINE);
        req.setAttribute("isOfflineToken", isOfflineToken);
    }
	
	private HttpFacade getFacade(final HttpServletRequest servletRequest) {
        return new HttpFacade() {

            @Override
            public Request getRequest() {
                return new Request() {

                    private InputStream inputStream;

                    @Override
                    public String getMethod() {
                        return servletRequest.getMethod();
                    }

                    @Override
                    public String getURI() {
                        return servletRequest.getRequestURL().toString();
                    }

                    @Override
                    public String getRelativePath() {
                        return servletRequest.getServletPath();
                    }

                    @Override
                    public boolean isSecure() {
                        return servletRequest.isSecure();
                    }

                    @Override
                    public String getQueryParamValue(String param) {
                        return servletRequest.getParameter(param);
                    }

                    @Override
                    public String getFirstParam(String param) {
                        return servletRequest.getParameter(param);
                    }

                    @Override
                    public Cookie getCookie(String cookieName) {
                        // not needed
                        return null;
                    }

                    @Override
                    public String getHeader(String name) {
                        return servletRequest.getHeader(name);
                    }

                    @Override
                    public List<String> getHeaders(String name) {
                        // not needed
                        return null;
                    }

                    @Override
                    public InputStream getInputStream() {
                        return getInputStream(false);
                    }

                    @Override
                    public InputStream getInputStream(boolean buffered) {
                        if (inputStream != null) {
                            return inputStream;
                        }

                        if (buffered) {
                            try {
                                return inputStream = new BufferedInputStream(servletRequest.getInputStream());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        try {
                            return servletRequest.getInputStream();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public String getRemoteAddr() {
                        return servletRequest.getRemoteAddr();
                    }

                    @Override
                    public void setError(AuthenticationError error) {
                        servletRequest.setAttribute(AuthenticationError.class.getName(), error);

                    }

                    @Override
                    public void setError(LogoutError error) {
                        servletRequest.setAttribute(LogoutError.class.getName(), error);
                    }

                };
            }

            @Override
            public Response getResponse() {
                throw new IllegalStateException("Not yet implemented");
            }

            @Override
            public X509Certificate[] getCertificateChain() {
                throw new IllegalStateException("Not yet implemented");
            }
        };
    }

}
