import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

export interface HeroProfile {
  id: number;
  name: string;
}

export const HEROES: HeroProfile[] = [
  { id: 12, name: 'Dr. Nice' },
  { id: 13, name: 'Bombasto' },
  { id: 14, name: 'Celeritas' },
  { id: 15, name: 'Magneta' },
  { id: 16, name: 'RubberMan' },
  { id: 17, name: 'Dynama' },
  { id: 18, name: 'Dr. IQ' },
  { id: 19, name: 'Magma' },
  { id: 20, name: 'Tornado' }
];

@Injectable({
  providedIn: 'root'
})
export class HeroServiceService {

  constructor(private httpClient: HttpClient) { }

  getHeroes(): Observable<HeroProfile[]> {

    // TODO - replace with a call to secured REST API
    const headers = new HttpHeaders().set('Content-type', 'application/json');

    return this.httpClient.get('http://localhost:8090/api/heroes',
      { headers }).pipe(map((data) => {
        //const jsonObject = JSON.parse(data);

        // Extract the array of objects from the parsed JSON
        
        console.log("Fetched hero profiles", data);
        //(data as HeroProfile[]).forEach((element: any) => console.log(element));

        return (data as HeroProfile[]);  
      }));


    /* const heroes = of(HEROES);
    console.log("Fetched hero profiles");
    return heroes; */
  }
}
