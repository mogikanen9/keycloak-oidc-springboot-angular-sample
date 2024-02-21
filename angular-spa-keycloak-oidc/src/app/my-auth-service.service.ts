import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

import { AuthConfig, OAuthService } from 'angular-oauth2-oidc';


import { jwtDecode } from "jwt-decode";

export const authCodeFlowConfig: AuthConfig = {
  // Url of the Identity Provider
  issuer: 'http://localhost:8080/realms/myrealm',

  // URL of the SPA to redirect the user to after login
  redirectUri: window.location.origin + '/',

  // The SPA's id. The SPA is registerd with this id at the auth-server
  // clientId: 'server.code',
  clientId: 'MyAngularSpaApp',

  // Just needed if your auth server demands a secret. In general, this
  // is a sign that the auth server is not configured with SPAs in mind
  // and it might not enforce further best practices vital for security
  // such applications.
  // dummyClientSecret: 'secret',

  responseType: 'code',

  // set the scope for the permissions the client should request
  // The first four are defined by OIDC.
  // Important: Request offline_access to get a refresh token
  // The api scope is a usecase specific one
  scope: 'openid profile email offline_access access_to_heroes_api',

  showDebugInformation: true,
};

export interface MyAuthProfile {
  fullName: string;
}

@Injectable({
  providedIn: 'root'
})
export class MyAuthServiceService {

  // current auth state in the app
  private auth = false;

  // Tip: never expose the Subject itself.
  private profileSubject = new Subject<MyAuthProfile>();

  /** Observable of all messages */
  profiles$ = this.profileSubject.asObservable();

  constructor(private oauthService: OAuthService) { }

  private addProfile(profile: MyAuthProfile) {
    if (profile) {
      this.profileSubject.next(profile);
      this.auth = true;
    }
  }

  isAuth(): boolean {
    return this.auth;
  }

  public configure() {
    this.oauthService.configure(authCodeFlowConfig);
    this.oauthService.loadDiscoveryDocumentAndTryLogin();

    this.oauthService.events.subscribe(event => {
      console.debug('oauth/oidc event', event);
      if (event.type === 'token_received') {
        let accesToken = this.oauthService.getAccessToken();
        console.debug('accesToken->', accesToken);

        // Decode the base64 token
        let decodedToken = jwtDecode(accesToken);

        console.log('tokenData->', decodedToken);
        console.log('tokenData.family_name->', decodedToken["family_name"]);

        this.addProfile({ fullName: decodedToken["family_name"] });

        const realm_access = decodedToken["realm_access"];
        console.log('tokenData.realm_access->', realm_access);

        const roles = realm_access["roles"];
        console.log('tokenData.roles->', roles);

        const subar = roles.filter((role: string) => role.startsWith("clinic"));
        console.log('subar->', subar);

        const isClinicManager = subar.length > 0;
        console.log('isClinicManager->', isClinicManager);
      }
    });

  }

  public login() {
    this.oauthService.initCodeFlow();
  }

  public logout() {
    this.oauthService.logOut();
  }
}
