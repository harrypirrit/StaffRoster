import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { EMPTY, throwError } from 'rxjs';
import { Observable } from 'rxjs/internal/Observable';
import { catchError } from 'rxjs/internal/operators/catchError';
import { Event } from '../models/event';
import {User} from "../models/user";
import {Status} from "../models/status";

@Injectable({
  providedIn: 'root'
})
export class EventService {
  constructor(private _http: HttpClient, private _router: Router) {
      this._url = window.location.origin + "/api";
  }

  private _url: string;

  public getEvents(): Observable<Event[]> {
    console.log(this._url + "/events");
    return this._http.get<Event[]>(this._url + "/events")
      .pipe(
        catchError(this.handleError)
      );
  }

  public deleteEvent(id: number | string | undefined) {
    return this._http.delete(this._url + "/event/" + id)
      .pipe(
        catchError(this.handleError)
      );
  }

  public addEvent(event: Event) {
    return this._http.post(this._url + "/event", event)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Gets the events details which the user is invited to
   * @param token
   * @returns
   */
  public getInvitedEvent(token: string): Observable<Event> {
    return this._http.get<Event>(this._url + "/event/getInvitedEvent/" + token)
      .pipe(
        catchError(this.handleError)
      );
  }

  public acceptInvite(token: string): Observable<void> {
    return this._http.post<void>(this._url + "/event/acceptInvite", token)
    .pipe(
      catchError(this.handleError)
    );
  }

  public inviteUsers(event: Event) {
    return this._http.put(this._url + "/event/inviteUsers", event)
      .pipe(
        catchError(this.handleError)
      );
  }

  public changeStatus(eventID: number, statusID: number) {
    return this._http.put(this._url + "/event/changeStatus", new Event(eventID, 0, "", "", 0, "", statusID, new Status(0, ""),[], new User(0, "", "", "", "", "", 0, "", false)))
      .pipe(
        catchError(this.handleError)
      );
  }

  public getAllStatuses() {
    return this._http.get<Status[]>(this._url + "/events/statuses")
      .pipe(
        catchError(this.handleError)
      );
  }

  public unInvite(eventID: number, userID: number) {
    return this._http.post(this._url + "/event/uninvite/" + eventID, userID)
      .pipe(
        catchError(this.handleError)
      );
  }

  public handleError(error: Response) {
    return EMPTY;
  }
}
