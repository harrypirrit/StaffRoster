import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Event } from 'src/app/models/event';
import { Status } from 'src/app/models/status';
import { User } from 'src/app/models/user';
import { EventService } from 'src/app/services/event.service';
import { LoginService } from 'src/app/services/login.service';
import { UsersService } from 'src/app/services/users.service';

@Component({
  selector: 'app-add-event',
  templateUrl: './add-event.component.html'
})
export class AddEventComponent implements OnInit {

  constructor(private _usersService: UsersService, private _loginService: LoginService, private _eventService: EventService) { }

  public users: User[] | undefined;
  public newAssignees: number[] = [];
  public newEvent = new Event(0, 0, "", "", 0, "", 0, new Status(0, ""), [], new User(0, "", "", "", null, "", 0, "", false));
  public date: Date = new Date();
  public dateError: string = "";
  public locationError: string = "";
  public durationError: string = "";
  public descriptionError: string = "";
  public isSaving: boolean = false;

  @Output()
  public close: EventEmitter<any> = new EventEmitter<any>();

  public async ngOnInit() {
    this.date.setSeconds(0);
    this.users = await this._usersService.getAllUsers().toPromise();
  }

  public async addEvent() {
    this.isSaving = true;
    let tmpUsers: User[] = [];
    this.newAssignees.forEach(id => {
      tmpUsers.push(new User(id, "", "", "", null, "", 0, "", false));
    });
    this.newEvent.assignees = tmpUsers;
    this.newEvent.userID = this._loginService.user ? this._loginService.user.userID : 0;
    this.newEvent.date = this.date.getFullYear() + "-" + (this.date.getMonth() + 1) + "-" + this.date.getDate() + " " + this.date.getHours() + ":" + this.date.getMinutes();

    console.log(this.newEvent.date);
    if (this.newEvent.location && this.newEvent.duration && this.newEvent.date && this.newEvent.description) {
      this.locationError = "";
      this.durationError = "";
      this.dateError = "";
      this.descriptionError = "";

      await this._eventService.addEvent(this.newEvent).toPromise();
      this.close.emit();
    } else {
      if (!this.newEvent.description) {
        this.descriptionError = "Please enter the description.";
      }
      if (!this.newEvent.location) {
        this.locationError = "Please enter the event location.";
      }
      if (!this.newEvent.duration) {
        this.durationError = "Please enter the event duration.";
      }
      if (!this.newEvent.date) {
        this.dateError = "Please enter the event start date.";
      }
    }
    this.isSaving = false;
  }

  public get currentUser() {
    return this._loginService.user || new User(0, "", "", "", "", "", 0, "", false);
  }
}
