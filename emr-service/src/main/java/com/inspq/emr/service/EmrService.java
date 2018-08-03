package com.inspq.emr.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.inspq.emr.domain.EmrApp;

@Component
public class EmrService {

	/**
	 * Returns a {@link List} of {@link EmrApp} 
	 * 
	 * 
	 * @return
	 */
	public List<EmrApp> getAll() {
		List<EmrApp> list = new ArrayList<>();
		EmrApp app = new EmrApp();
		app.setAppId("1234");;
		app.setApplicationNumber("EMR-1234");
		app.setApplicationType("NEW");
		app.setDescription("A new application");
		list.add(app);
		return list;
	}
	
	/**
	 * Returns a {@link List} of {@link String} 
	 * 
	 * 
	 * @return
	 */
	public List<String> getPublications(){
		return Arrays.asList("Ethics", "Health Economics", "Immunization");
	}

}
