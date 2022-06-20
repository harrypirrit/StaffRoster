import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Router } from '@angular/router';
import { catchError, EMPTY, map, Observable, throwError } from 'rxjs';
import { User } from '../models/user';
import { ResponseType } from '../models/response-type';

@Injectable({ providedIn: 'root' })
export class LoginService {
    constructor(private _http: HttpClient, private _router: Router) {
            this._url = window.location.origin + "/api";
    }

    private _url: string;
    private _currentUser: User | undefined | null = null;

    /**
     * Calls method to login and then handles redirection and token storage
     * @param user 
     * @param returnUrl 
     * @returns 
     */
    public async login(user: User, returnUrl: string) {
        let result: ResponseType | undefined = await this._login(user).toPromise();
        if (result) {
            if (result.result) {
                sessionStorage.setItem("token", result.result);
                console.log("Succeeded to login");
                this._currentUser = await this._getLoggedInUser().toPromise();
                this._router.navigate([returnUrl]);
            } else {
                console.log("Failed to login.");
            }
            return result.message
        } else {
            console.log("Failed to login.");
            return null;
        }
    }

    /**
     * Checks users access token
     * @returns 
     */
    public async checkLogin() {
        this._currentUser = await this._getLoggedInUser().toPromise();
        if (this._currentUser) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks login details with backend
     * @param user 
     * @returns 
     */
    private _login(user: User): Observable<ResponseType> {
        return this._http.post<ResponseType>(this._url + "/user/login", user)
            .pipe(
                catchError(this.handleError)
            );
    }

    /**
     * Gets logged in users details
     * @returns 
     */
    private _getLoggedInUser(): Observable<User> {
        return this._http.get<User>(this._url + "/user")
            .pipe(
                catchError(this.handleError)
            );
    }

    /**
     * Kills session
     * @param returnUrl 
     */
    public logout(returnUrl: string = "/") {
        sessionStorage.removeItem("token");
        this._currentUser = null;
        this._router.navigate(['/login'], { queryParams: { returnUrl: returnUrl } });
    }

    public get user() {
        return this._currentUser;
    }

    public get isAdmin(): boolean {
        return this.user?.roleName === "Admin";
    }

    public handleError(error: Response) {
        return EMPTY;
    }
}
