import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Event } from 'src/app/models/event';
import { EventService } from 'src/app/services/event.service';

@Component({
  selector: 'app-view-invite',
  templateUrl: './view-invite.component.html',
  styleUrls: ['./view-invite.component.css']
})
export class ViewInviteComponent implements OnInit {

  constructor(private _route: ActivatedRoute, private _router: Router, private _eventService: EventService) { }

  private _token: string = "";

  public showForm: boolean = false;

  public message: string | null = null;

  public event: Event | undefined = undefined;

  public async ngOnInit() {
    this._route.queryParamMap.subscribe(params => {
      let token = params.get("token");
      if (token == null) {
        this._router.navigate(["/login"]);
      }
        this._token = token != null ? token : "";
    });

    this.event = await this._eventService.getInvitedEvent(this._token).toPromise();
    if (this.event) {
      this.showForm = true;
    } else {
      this.message = "Either you have already accepted or the link is invalid. Redirecting...";
      setTimeout(() => {
        this._router.navigate(["/login"]);
      }, 3000);
    }
  }

  public async acceptInvite() {
    await this._eventService.acceptInvite(this._token).toPromise();
    this.showForm = false;
    this.message = "The invite has been accepted. Redirecting...";
    setTimeout(() => {
      this._router.navigate(["/login"]);
    }, 3000);
  }
}
