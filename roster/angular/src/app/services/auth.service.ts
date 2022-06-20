import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { LoginService } from './login.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private _router: Router, private _loginService: LoginService) { }

  /**
   * Confirm that the user is authenticated before granting access to non-public pages
   * @param route 
   * @param state 
   * @returns 
   */
  public async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (await this._loginService.checkLogin()) {
      return true;
    }

    this._loginService.logout(state.url);
    return false;
  }
}