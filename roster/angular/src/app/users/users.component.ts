import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { User } from '../models/user';
import { UsersService } from '../services/users.service';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {LoginService} from "../services/login.service";

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {

  constructor(private _usersService: UsersService, private _route: ActivatedRoute, private _router: Router, private _modalService: NgbModal, private _loginService: LoginService) { }

  private _users: User[] | undefined;
  public addingUser: boolean = false;
  public editing: number = 0;
  private _modal: any;
  public deletingUser: number = 0;

  public async ngOnInit() {
    this._users = await this._usersService.getAllUsers().toPromise();
    let addUser = this._route.snapshot.queryParams['addUser'];
    if (addUser) {
      this.addingUser = true;
      const qParams: Params = {};
      this._router.navigate([], {
          relativeTo: this._route,
          queryParams: qParams,
          queryParamsHandling: ''
      });
    }
  }

  public get users() {
    return this._users;
  }

  public async toggleForm() {
    this.addingUser = !this.addingUser;
    if (!this.addingUser) {
      await this.ngOnInit();
    }
  }

  public closeEdit() {
    this.editing = 0;
    this.ngOnInit();
  }

  public getEditingUser() {
    let tmp = this._users?.find(x => x.userID == this.editing);
    return tmp ? tmp : new User(0, "", "", "", null, "", 0, "", false);
  }

  public async deleteUser(userID: number) {
    await this._usersService.delete(userID).toPromise();
    await this.closeModal();
    await this.ngOnInit();
  }

  public openDeleteModal(content: any, userID: number) {
    this.deletingUser = userID;
    this._modal = this._modalService.open(content, {});
  }

  public closeModal() {
    this._modal.dismiss();
  }

  public get getDeletingUser() {
    return this._users?.find(x => x.userID == this.deletingUser) || new User(0, "", "", "", "", "", 0, "", false);
  }

  public get currentUser() {
    return this._loginService.user || new User(0, "", "", "", "", "", 0, "", false);
  }
}
