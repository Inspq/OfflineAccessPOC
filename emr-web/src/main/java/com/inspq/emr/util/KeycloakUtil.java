package com.inspq.emr.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.security.cert.X509Certificate;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.spi.AuthenticationError;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.spi.LogoutError;
import org.keycloak.jose.jws.JWSInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inspq.emr.repository.RefreshTokenDAO;

@Component
public class KeycloakUtil {
	
	@Autowired
	RefreshTokenDAO refreshTokenDAO;
	
	public String getAccessToken(HttpServletRequest request) {
		RefreshableKeycloakSecurityContext ctx = (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
		return ctx.getTokenString();
	}
	
	public KeycloakDeployment getDeployment(HttpServletRequest request, ServletContext servletContext) throws ServletException {
        // The facade object is needed just if you have relative "auth-server-url" in keycloak.json. Otherwise you can call deploymentContext.resolveDeployment(null)
		HttpFacade facade = getFacade(request);
        AdapterDeploymentContext deploymentContext = (AdapterDeploymentContext) servletContext.getAttribute(AdapterDeploymentContext.class.getName());
        if (deploymentContext == null) {
            throw new ServletException("AdapterDeploymentContext not set");
        }
        return deploymentContext.resolveDeployment(facade);
    }
	
	public void storeToken(HttpServletRequest req) throws IOException, JWSInputException {
        RefreshableKeycloakSecurityContext ctx = (RefreshableKeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
        String refreshToken = ctx.getRefreshToken();
        refreshTokenDAO.saveToken(refreshToken);
    }
	
	private HttpFacade getFacade(final HttpServletRequest request) {
        return new HttpFacade() {

            @Override
            public Request getRequest() {
                return new Request() {

                    private InputStream inputStream;

                    @Override
                    public String getMethod() {
                        return request.getMethod();
                    }

                    @Override
                    public String getURI() {
                        return request.getRequestURL().toString();
                    }

                    @Override
                    public String getRelativePath() {
                        return request.getServletPath();
                    }

                    @Override
                    public boolean isSecure() {
                        return request.isSecure();
                    }

                    @Override
                    public String getQueryParamValue(String param) {
                        return request.getParameter(param);
                    }

                    @Override
                    public String getFirstParam(String param) {
                        return request.getParameter(param);
                    }

                    @Override
                    public Cookie getCookie(String cookieName) {
                        // not needed
                        return null;
                    }

                    @Override
                    public String getHeader(String name) {
                        return request.getHeader(name);
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
                                return inputStream = new BufferedInputStream(request.getInputStream());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        try {
                            return request.getInputStream();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public String getRemoteAddr() {
                        return request.getRemoteAddr();
                    }

                    @Override
                    public void setError(AuthenticationError error) {
                        request.setAttribute(AuthenticationError.class.getName(), error);

                    }

                    @Override
                    public void setError(LogoutError error) {
                        request.setAttribute(LogoutError.class.getName(), error);
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
