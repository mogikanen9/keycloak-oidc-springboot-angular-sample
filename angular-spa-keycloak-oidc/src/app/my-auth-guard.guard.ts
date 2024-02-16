import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { MyAuthServiceService } from './my-auth-service.service';

@Injectable({
  providedIn: 'root'
})
export class MyAuthGuardGuard implements CanActivate {

  constructor(private myAuthServiceService: MyAuthServiceService) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    if (this.myAuthServiceService.isAuth()) {
      return true;
    } else {

      this.myAuthServiceService.login();

    }

  }

}
