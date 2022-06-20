import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { throwError } from 'rxjs/internal/observable/throwError';
import { catchError } from 'rxjs/internal/operators/catchError';
import { Role } from '../models/role';
import { User } from '../models/user';
import {ResponseType} from "../models/response-type";

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  constructor(private _http: HttpClient) {
      this._url = window.location.origin + "/api";
  }

  private _url: string;

  /**
   * Fetches all users along with their roleName
   * @returns
   */
  public getAllUsers(): Observable<User[]> {
    return this._http.get<User[]>(this._url + "/users")
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Creates a new user
   * @param user
   * @returns
   */
  public register(user: User): Observable<ResponseType> {
    return this._http.post<ResponseType>(this._url + "/user/register", user)
    .pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Fetches all roles
   * @returns
   */
  public getRoles(): Observable<Role[]> {
    return this._http.get<Role[]>(this._url + "/roles")
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Verifies that the user clicked the link within a certain time and confirms user is valid
   * @param token
   * @returns
   */
  public verifyPasswordToken(token: string): Observable<boolean> {
    return this._http.get<boolean>(this._url + "/user/verifyPasswordToken/" + token)
    .pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Sets users password
   * @param token
   * @param password
   * @returns
   */
  public setPassword(token: string, password: string | null): Observable<boolean> {
    return this._http.put<boolean>(this._url + "/user/setPassword/" + token, password)
    .pipe(
      catchError(this.handleError)
    );
  }

  public update(user: User): Observable<boolean> {
    return this._http.put<boolean>(this._url + "/user/update", user)
    .pipe(
      catchError(this.handleError)
    );
  }

  public delete(userID: number) {
    return this._http.delete(this._url + "/user/" + userID)
      .pipe(
        catchError(this.handleError)
      );
  }

  public handleError(error: Response) {
    return throwError(error);
  }
}
