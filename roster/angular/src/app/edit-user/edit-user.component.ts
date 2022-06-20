import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Role } from '../models/role';
import { User } from '../models/user';
import { UsersService } from '../services/users.service';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html'
})
export class EditUserComponent implements OnInit {

  constructor(private _userService: UsersService) { }

  @Input() public user: User = new User(0, "", "", "", null, "", 1, "", false);
  @Output() public close: EventEmitter<any> = new EventEmitter();
  public message: string | null = null;
  public roles: Role[] | undefined;
  public isSaving: boolean = false;
  public firstNameError: string = ""
  public lastNameError: string = ""
  public emailError: string = ""
  public roleError: string = ""
 
  public async ngOnInit() {
    this.roles = await this._userService.getRoles().toPromise();
    this.user = JSON.parse(JSON.stringify(this.user));
  }

  public async submit() {
    this.isSaving = true;
    if (this.user.firstName && this.user.lastName && this.user.email && this.user.roleID) {
      this.firstNameError = "";
      this.lastNameError = "";
      this.emailError = "";
      this.roleError = "";
      let success = await this._userService.update(this.user).toPromise();
      if (success) {
        this.close.emit();
      } else {
        this.message = "A problem has occurred and the user was not altered."
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
