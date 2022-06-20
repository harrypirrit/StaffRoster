import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { LoginService } from '../services/login.service';

/**
 * This intercepts all responses and checks if the backend returns unauthorised. If so it will log the user out
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(private _loginService: LoginService) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(catchError(err => {
            if (err.status === 401) {
                this._loginService.logout();
            }

            const error = err.error.message || err.statusText;
            return throwError(error);
        }))
    }
}