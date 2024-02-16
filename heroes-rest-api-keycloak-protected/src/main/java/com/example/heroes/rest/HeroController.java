package com.example.heroes.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class HeroController {
	
	@GetMapping("/heroes")
	public List<HeroProfile> listHeroes(Principal principal) {

		log.info(String.format("Principal->%s", principal != null ? principal.getName() : "N/A"));

		List<HeroProfile> rs = new ArrayList<>();

		rs.add(new HeroProfile(1, "Wonder Woman"));

		rs.add(new HeroProfile(2, "Batman"));

		return rs;
	}
}

record HeroProfile(int id, String name) {
}