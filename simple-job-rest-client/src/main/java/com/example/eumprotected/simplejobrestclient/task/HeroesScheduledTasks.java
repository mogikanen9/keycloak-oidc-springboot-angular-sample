package com.example.eumprotected.simplejobrestclient.task;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.eumprotected.simplejobrestclient.service.MyHeroesService;
import com.example.eumprotected.simplejobrestclient.service.MyHeroesService.HeroProfile;
import com.nimbusds.oauth2.sdk.ParseException;

@Component
public class HeroesScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(HeroesScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private MyHeroesService mhs;

	public HeroesScheduledTasks(MyHeroesService mhs) {
		this.mhs = mhs;
	}

	@Scheduled(fixedRate = 10000)
	public void reportCurrentTime() throws ParseException, URISyntaxException, IOException {
		log.info("The time is now {}", dateFormat.format(new Date()));
		HeroProfile[] rs = mhs.fetchData();
		for (HeroProfile heroProfile : rs) {
			log.info(String.format("profile->%s", heroProfile));
		}
	}
}