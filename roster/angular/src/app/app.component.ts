import { Component, Inject } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { LoginService } from './services/login.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'rosterApp';

  constructor(private _router: Router, private _loginService: LoginService) {

  }

  public ngOnInit() {
  //   this._router.events.subscribe(event => {
  //     if (event instanceof NavigationEnd) {
  //         this._loginService.checkLogin();
  //     }
  // });
  }
}
