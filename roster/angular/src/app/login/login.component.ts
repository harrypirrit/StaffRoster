import { Component, OnInit } from '@angular/core';
import { LoginService } from '../services/login.service';
import { User } from '../models/user';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {
  constructor(private _service: LoginService, private _route: ActivatedRoute, private _router: Router) {
  }

  public user: User = new User(0, "", "", "", null, "", 0, "", false);
  public message: string | null = null;
  private _returnUrl: string = "/";

  public async ngOnInit() {
    this._returnUrl = this._route.snapshot.queryParams['returnUrl'] || '/';
    if (await this._service.checkLogin()) {
      this._router.navigate([this._returnUrl]);
    }
  }

  public async login() {
    this.message = await this._service.login(this.user, this._returnUrl);
  }

  public get currentUser() {
    return this._service.user;
  }
}
