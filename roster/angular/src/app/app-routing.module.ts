import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { EventsComponent } from './events/events.component';
import { ViewInviteComponent } from './events/view-invite/view-invite.component';
import { LoginComponent } from './login/login.component';
import { MainComponent } from './main/main.component';
import { AuthService } from './services/auth.service';
import { SetPasswordComponent } from './set-password/set-password.component';
import { UsersComponent } from './users/users.component';
import { ContactComponent } from './contact/contact.component';

const routes: Routes = [
  // Routes which do not require the user to be authenticated
  { path: 'login', component: LoginComponent },
  { path: 'login/set-password', component: SetPasswordComponent },

  // Routes that require the user to be authenticated
  { path: '', component: MainComponent, canActivate: [AuthService] },
  { path: 'users', component: UsersComponent, canActivate: [AuthService] },
  { path: 'events', component: EventsComponent, canActivate: [AuthService] },
  { path: 'events/view-invite', component: ViewInviteComponent },
  { path: 'contact', component: ContactComponent, canActivate: [AuthService] },

  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      enableTracing: true
    }),
    FormsModule
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
