package com.inspq.emr.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.ServerRequest;
import org.keycloak.representations.AccessTokenResponse;
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
import com.inspq.emr.repository.RefreshTokenDAO;
import com.inspq.emr.util.KeycloakUtil;

public @Controller
class ProductController {
	
	@Autowired
	ServletContext servletContext;
	
	@Autowired
	KeycloakUtil keycloakUtil;
	
	@GetMapping(path = "/publications")
	public String getProducts(Model model, HttpServletRequest request) throws Exception {
		keycloakUtil.storeToken(request);
		String result = loadPublicationsData(request, keycloakUtil.getAccessToken(request));
		
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("publications", Arrays.asList(mapper.readValue(result, String[].class)));
		
		return "publications";
	}

	@GetMapping(path = "/offline")
	public String getEmr(Model model, HttpServletRequest request) throws Exception {
		String refreshToken = RefreshTokenDAO.loadToken();
		System.out.println("Refresh token to be loaded is \n" + refreshToken);
		String result = loadOfflinePublicationsData(request, refreshToken, servletContext);
		
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("publications", Arrays.asList(mapper.readValue(result, String[].class)));
		
		return "publications";
	}
	
	private String loadOfflinePublicationsData(HttpServletRequest req, String refreshToken, ServletContext servletContext) throws ServletException, IOException {
        // Retrieve accessToken first with usage of refresh (offline) token from DB
        String accessToken = null;
        try {
            KeycloakDeployment deployment = keycloakUtil.getDeployment(req, servletContext);
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
		headers.set("Authorization", "Bearer "+ accessToken);
		
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List<String>> response =
		        restTemplate.exchange("http://localhost:8082/emr/publications",
		                    HttpMethod.GET, entity, new ParameterizedTypeReference<List<String>>() {
		            });
		
		List<String> list = response.getBody();
		System.out.println("Publication response: " + list);
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(list);
    }
	
	
	private String loadPublicationsData(HttpServletRequest req, String accessToken) throws ServletException, IOException {
        // Retrieve accessToken first with usage of refresh (offline) token from DB
        HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer "+ accessToken);
		
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List<String>> response =
		        restTemplate.exchange("http://localhost:8082/emr/publications",
		                    HttpMethod.GET, entity, new ParameterizedTypeReference<List<String>>() {
		            });
		
		List<String> list = response.getBody();
		System.out.println("Publication response: " + list);
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(list);
    }
	
	
	
}