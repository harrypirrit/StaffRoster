import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Role } from '../models/role';
import { User } from '../models/user';
import { UsersService } from '../services/users.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent implements OnInit {

  constructor(private _userService: UsersService) { }

  @Output()
  public toggleForm: EventEmitter<any> = new EventEmitter<any>();

  public user: User = new User(0, "", "", "", null, "", 1, "", false);
  public message: string | undefined = undefined;
  public roles: Role[] | undefined;
  public isSaving: boolean = false;
  public firstNameError: string = ""
  public lastNameError: string = ""
  public emailError: string = ""
  public roleError: string = ""

  public async ngOnInit() {
    this.roles = await this._userService.getRoles().toPromise();
  }

  public close() {
    this.toggleForm.emit()
  }

  public async submit() {
    this.isSaving = true;
    if (this.user.firstName && this.user.lastName && this.user.email && this.user.roleID) {
      this.firstNameError = "";
      this.lastNameError = "";
      this.emailError = "";
      this.roleError = "";
      let response = await this._userService.register(this.user).toPromise();
      if (response) {
        if (response.message) {
          this.message = response.message;
        }
        else {
          this.close();
        }
      } else {
        this.message = "A problem has occurred and the user was not created."
      }
    } else {
      if (!this.user.firstName) {
        this.firstNameError = "Please enter their first name.";
      }
      if (!this.user.lastName) {
        this.lastNameError = "Please enter their last name.";
      }
      if (!this.user.email) {
        this.emailError = "Please enter their email address.";
      }
      if (!this.user.roleID) {
        this.roleError = "Please select their role.";
      }
    }

    this.isSaving = false;
  }
}
