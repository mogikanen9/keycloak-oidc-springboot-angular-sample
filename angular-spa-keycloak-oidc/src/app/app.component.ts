import { Component } from '@angular/core';
import { MyAuthServiceService } from './my-auth-service.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angular-spa-keycloak-protected';
  profileFullName: string;

  constructor(private myAuthService: MyAuthServiceService) {
    this.myAuthService.configure();

    this.myAuthService.profiles$.subscribe((profile) => {
      this.profileFullName = profile.fullName;
    })

  }

  public login() {
    this.myAuthService.login();
  }

  public logout() {
    this.myAuthService.logout();
  }
}
