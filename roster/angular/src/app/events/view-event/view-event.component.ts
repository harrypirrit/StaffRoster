import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Event} from "../../models/event";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {LoginService} from "../../services/login.service";
import {User} from "../../models/user";
import {UsersService} from "../../services/users.service";
import {EventService} from "../../services/event.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Status} from "../../models/status";

@Component({
  selector: 'app-view-event',
  templateUrl: './view-event.component.html'
})
export class ViewEventComponent implements OnInit {

  constructor(private _modalService: NgbModal, private _loginService: LoginService, private _usersService: UsersService, private _eventService: EventService, private _router: Router, private _route: ActivatedRoute) { }

  @Input() public event: Event = new Event(0, 0, "", "", 0, "", 0, new Status(0, ""), [], new User(0, "", "", "", "", "", 0, "", false));
  @Output() public close: EventEmitter<any> = new EventEmitter<any>();
  @Output() public delete: EventEmitter<any> = new EventEmitter<any>();

  public users: User[] = [];
  public newAssignees: number[] = [];
  public statuses: Status[] = [];

  private _modal: any;
  public inviting: boolean = false;
  public isSaving: boolean = false;
  public message: string = "";

  public async ngOnInit() {
    this.users = await this._usersService.getAllUsers().toPromise() || [];
    this.statuses = await this._eventService.getAllStatuses().toPromise() || [];
    this.event.assignees.sort((a, b) => a.firstName.localeCompare(b.firstName));
  }


  public openDeleteModal(content: any) {
    this._modal = this._modalService.open(content, {});
  }

  public closeModal() {
    this._modal.dismiss();
  }

  public async deleteEvent() {
    this.closeModal();
    this.delete.emit();
  }

  public get usersID() {
    return this._loginService.user?.userID;
  }

  public get isAdmin() {
    return this._loginService.isAdmin;
  }

  public get getUsers(): User[] {
    return this.users.filter(x => !this.event?.assignees.find(z => z.userID == x.userID));
  }

  public isUserAlreadyInvited(id: number): boolean {
    return this.event?.assignees.find(x => x.userID == id) != null || false;
  }

  public async changeAssignees() {
    this.message = "";
    this.isSaving = true;
    if (this.event && this.newAssignees.length >= 1) {
      this.event.assignees = [];
      for (let i = 0; i < this.newAssignees.length; i++) {
        this.event.assignees.push(new User(this.newAssignees[i], "", "", "", "", "", 0, "", false));
      }
      await this._eventService.inviteUsers(this.event).toPromise();
      this.close.emit();
      await this._router.navigate(["/events"], {queryParams: {'viewEvent': this.event.eventID}});

    } else {
      this.message = "Please select a user";
    }
    this.isSaving = false;
  }

  public toggleInvite() {
    this.inviting = !this.inviting;
  }

  public async changeStatus() {
    await this._eventService.changeStatus(this.event?.eventID || 0, this.event?.statusID || 0).toPromise();
    await this._router.navigate(["/events"], {queryParams: {'viewEvent': this.event.eventID}});
  }

  public async unInvite(userID: number) {
    await this._eventService.unInvite(this.event?.eventID || 0, userID).toPromise();
    await this._router.navigate(["/events"], {queryParams: {'viewEvent': this.event.eventID}});
  }

  public get statusName() {
    return this.statuses.find(x => x.statusID == this.event.statusID)?.statusName || "";
  }
}
