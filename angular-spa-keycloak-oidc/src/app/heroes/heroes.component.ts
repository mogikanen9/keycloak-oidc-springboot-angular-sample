import { Component, OnInit } from '@angular/core';
import { HeroProfile, HeroServiceService } from './hero-service.service';

@Component({
  selector: 'app-heroes',
  templateUrl: './heroes.component.html',
  styleUrls: ['./heroes.component.css']
})
export class HeroesComponent implements OnInit {

  heroes: HeroProfile[] = [];

  constructor(private heroService: HeroServiceService) { }

  ngOnInit(): void {
    this.heroService.getHeroes()
    .subscribe(heroes => this.heroes = heroes);
  }

}
