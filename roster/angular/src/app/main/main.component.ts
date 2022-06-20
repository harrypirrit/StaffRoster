import { Component, OnInit } from '@angular/core';
import {LoginService} from "../services/login.service";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html'
})
export class MainComponent implements OnInit {

  constructor(private _loginService: LoginService) { }

  ngOnInit() {
  }



  public get isAdmin() {
    return this._loginService.isAdmin;
  }

}
