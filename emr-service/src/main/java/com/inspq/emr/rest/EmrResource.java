package com.inspq.emr.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.inspq.emr.domain.EmrApp;
import com.inspq.emr.service.EmrService;

@RestController
@CrossOrigin
@RequestMapping("/emr")
public class EmrResource {

	private EmrService service;

	@Autowired
	public EmrResource(EmrService service) {
		this.service = service;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
	public List<EmrApp> getAll() {
		
		return service.getAll();
	}
	
	@RequestMapping(value = "/publications", method = RequestMethod.GET, produces = "application/json")
	public List<String> getPublications() {
		
		return service.getPublications();
	}
	


}
