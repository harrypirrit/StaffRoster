import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginService } from '../services/login.service';

/**
 * This intercepts all requests and adds the access token to the authorisation header
 */
@Injectable()
export class JwtInterceptor implements HttpInterceptor {
    constructor(private _loginService: LoginService) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        let currentToken = sessionStorage.getItem("token");
        if (currentToken) {
            request = request.clone({
                setHeaders: {
                    Authorization: currentToken
                }
            });
        }

        return next.handle(request);
    }
}