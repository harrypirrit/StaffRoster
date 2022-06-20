import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../services/login.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html'
})
export class HeaderComponent implements OnInit {
  constructor(private _loginService: LoginService, private _router: Router) { }

  ngOnInit() {
  }

  public get user() {
    return this._loginService.user;
  }

  public logout() {
    this._loginService.logout();
  }

  public location(path: string): boolean {
    if (this._router.url === path) {
      return true;
    } else {
      return false;
    }
  }

  public get isAdmin() {
    return this._loginService.isAdmin;
  }
}
