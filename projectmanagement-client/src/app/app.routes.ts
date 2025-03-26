import { Routes } from '@angular/router';
import {homeGuard} from "./auth/home.guard";
import {HomeComponent} from "./components/home/home.component";
import {NotfoundComponent} from "./components/notfound/notfound.component";
import {RegisterComponent} from "./components/register/register.component";
import {LoginComponent} from "./components/login/login.component";

export const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'login', component: LoginComponent},
  {path: '**', component: NotfoundComponent}
];
