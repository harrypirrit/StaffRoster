import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UsersService } from '../services/users.service';

@Component({
  selector: 'app-set-password',
  templateUrl: './set-password.component.html'
})
export class SetPasswordComponent implements OnInit {

  constructor(private _route: ActivatedRoute, private _userService: UsersService, private _router: Router) { }

  private _token: string = "";

  public isTokenValid: boolean | undefined = true;
  public showForm: boolean = false;

  public password: string | null = null;
  public confirmPassword: string | null = null;

  public message: string | null = null;

  public async ngOnInit() {
    this._route.queryParamMap.subscribe(params => {
      let token = params.get("token");
        this._token = token != null ? token : "";
    });

    this.isTokenValid = await this._userService.verifyPasswordToken(this._token).toPromise();
    if (this.isTokenValid) {
      this.showForm = true;
    } else {
      this.message = "Your link has expired. Please request a new one.";
    }
  }

  public async setPassword() {
    if (this.password != "" && this.confirmPassword != "") {
      if (this.password == this.confirmPassword) {
        let success = await this._userService.setPassword(this._token, this.password).toPromise();
        if (success) {
          this._router.navigate(['/login']);
        } else {
          this.message = "There was an error and your password was not set.";
        }
      } else {
        this.message = "The password you entered do not match.";
      }
    } else {
      this.message = "Please fill in the fields."
    }

  }

}
