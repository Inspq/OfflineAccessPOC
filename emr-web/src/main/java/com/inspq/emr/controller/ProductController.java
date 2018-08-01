package com.inspq.emr.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.security.cert.X509Certificate;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.ServerRequest;
import org.keycloak.adapters.spi.AuthenticationError;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.spi.LogoutError;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.RefreshToken;
import org.keycloak.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspq.emr.domain.EmrApp;
import com.inspq.emr.repository.RefreshTokenDAO;

public @Controller
class ProductController {
	
	@Autowired
	ServletContext servletContext;

	@GetMapping(path = "/products")
	public String getProducts(Model model, HttpServletRequest request) throws Exception {
		//if ("offline_access".equalsIgnoreCase(request.getParameter("scope")))
        storeToken(request);
        model.addAttribute("products", Arrays.asList("iPhone", "iPad", "Tablet"));
		return "products";
	}

	@GetMapping(path = "/logout")
	public String logout(HttpServletRequest request) throws ServletException {
		request.logout();
		return "/";
	}
	
	@GetMapping(path = "/offline")
	public String getEmr(Model model, HttpServletRequest request) throws Exception {
		String refreshToken = RefreshTokenDAO.loadToken();
		System.out.println("Refresh token to be loaded is \n" + refreshToken);
		String result = loadOfflineEmrData(request, refreshToken, servletContext);
		model.addAttribute("products", Arrays.asList("iPhone1", "iPad1", "Tablet1"));
		return "products";
	}
	
	private void storeToken(HttpServletRequest req) throws IOException, JWSInputException {
        RefreshableKeycloakSecurityContext ctx = (RefreshableKeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
        String refreshToken = ctx.getRefreshToken();
        System.out.println("Refresh token to be saved is \n" + refreshToken);
        RefreshTokenDAO.saveToken(refreshToken);

        RefreshToken refreshTokenDecoded = TokenUtil.getRefreshToken(refreshToken);
        Boolean isOfflineToken = refreshTokenDecoded.getType().equals(TokenUtil.TOKEN_TYPE_OFFLINE);
        req.setAttribute("isOfflineToken", isOfflineToken);
    }
	
	private String loadOfflineEmrData(HttpServletRequest req, String refreshToken, ServletContext servletContext) throws ServletException, IOException {
        // Retrieve accessToken first with usage of refresh (offline) token from DB
        String accessToken = null;
        try {
            KeycloakDeployment deployment = getDeployment(req, servletContext);
            AccessTokenResponse response = ServerRequest.invokeRefresh(deployment, refreshToken);
            accessToken = response.getToken();
            
            System.out.println("New access token is " + accessToken);

            // Uncomment this when you use revokeRefreshToken for realm. In that case each offline token can be used just once. So at this point, you need to
            // save new offline token into DB
            // RefreshTokenDAO.saveToken(response.getRefreshToken());
        } catch (ServerRequest.HttpFailure failure) {
            return "Failed to refresh token. Status from auth-server request: " + failure.getStatus() + ", Error: " + failure.getError();
        }

        HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer "+accessToken);
		
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List<EmrApp>> response =
		        restTemplate.exchange("http://localhost:8082/emr/",
		                    HttpMethod.GET, entity, new ParameterizedTypeReference<List<EmrApp>>() {
		            });
		
		List<EmrApp> list = response.getBody();
		EmrApp app = list.get(0);
		
		String result = null;
		if(app != null) {
			ObjectMapper mapper = new ObjectMapper();
			result = mapper.writeValueAsString(app);
		}
		
		return result;
    }
	
	private KeycloakDeployment getDeployment(HttpServletRequest servletRequest, ServletContext servletContext) throws ServletException {
        // The facade object is needed just if you have relative "auth-server-url" in keycloak.json. Otherwise you can call deploymentContext.resolveDeployment(null)
		HttpFacade facade = getFacade(servletRequest);
        AdapterDeploymentContext deploymentContext = (AdapterDeploymentContext) servletContext.getAttribute(AdapterDeploymentContext.class.getName());
        if (deploymentContext == null) {
            throw new ServletException("AdapterDeploymentContext not set");
        }
        return deploymentContext.resolveDeployment(facade);
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