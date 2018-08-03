package com.inspq.emr.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.inspq.emr.domain.EmrApp;
import com.inspq.emr.rest.EmrResource;
import com.inspq.emr.service.EmrService;

@RunWith(SpringRunner.class)
@WebMvcTest(EmrResource.class)
public class EmrResourceTest {
	
	@Autowired
	private MockMvc mvc;
 
	@MockBean
	private EmrService service;
 
    @Test
	public void testEmrEndpoint() throws Exception {
    	
    	List<EmrApp> list = mockEmrInfo();
    	
    	// given
		given(service.getAll()).willReturn(list);
    	
    	// when
		MockHttpServletResponse response = mvc.perform(get("/emr/")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		
		// then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(200);
		
	}
    
    @Test
	public void testEmrPublicationEndpoint() throws Exception {
    	
    	List<String> list = Arrays.asList("Ethics", "Health Economics", "Immunization");
    	
    	// given
		given(service.getPublications()).willReturn(list);
    	
    	// when
		MockHttpServletResponse response = mvc.perform(get("/emr/publications")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		
		// then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(200);
		
	}
    
    private List<EmrApp> mockEmrInfo() {
    	List<EmrApp> list = new ArrayList<>();
		EmrApp app = new EmrApp();
		app.setAppId("1234");;
		app.setApplicationNumber("EMR-1234");
		app.setApplicationType("NEW");
		app.setDescription("A new application");
		list.add(app);
		return list;
    }
 

}
