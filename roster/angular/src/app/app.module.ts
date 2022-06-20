import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainComponent } from './main/main.component';
import { HeaderComponent } from './header/header.component';
import { LoginComponent } from './login/login.component';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LoginService } from './services/login.service';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { RegisterComponent } from './register/register.component';
import { UsersComponent } from './users/users.component';
import { SetPasswordComponent } from './set-password/set-password.component';
import { JwtInterceptor } from './helpers/jwt.interceptor';
import { ErrorInterceptor } from './helpers/error.interceptor';
import { EventsComponent } from './events/events.component';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { EventService } from './services/event.service';
import { AddEventComponent } from './events/add-event/add-event.component';import {
  NgxMatDatetimePickerModule,
  NgxMatNativeDateModule,
  NgxMatTimepickerModule
} from '@angular-material-components/datetime-picker';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { EditUserComponent } from './edit-user/edit-user.component';
import { ViewInviteComponent } from './events/view-invite/view-invite.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ContactComponent } from './contact/contact.component';
import { ViewEventComponent } from './events/view-event/view-event.component';

@NgModule({
  // Import all components here
  declarations: [
    AppComponent,
    LoginComponent,
    HeaderComponent,
    MainComponent,
    RegisterComponent,
    UsersComponent,
    SetPasswordComponent,
    EventsComponent,
    AddEventComponent,
    EditUserComponent,
    ViewInviteComponent,
    ContactComponent,
    ViewEventComponent
   ],
  // Import any components which only get called by their 'selector' here
  entryComponents: [
    RegisterComponent,
    AddEventComponent,
    EditUserComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    RouterModule,
    CommonModule,
    HttpClientModule,
    CalendarModule.forRoot({
      provide: DateAdapter,
      useFactory: adapterFactory,
    }),
    MatFormFieldModule,
    MatDatepickerModule,
    NgxMatDatetimePickerModule,
    NgxMatTimepickerModule,
    NgxMatNativeDateModule,
    NoopAnimationsModule,
    MatInputModule,
    NgbModule
  ],
  providers: [
    // Interceptors for http requests
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    LoginService,
    EventService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
