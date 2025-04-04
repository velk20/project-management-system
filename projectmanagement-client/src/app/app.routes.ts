import { Routes } from '@angular/router';
import {homeGuard} from "./auth/home.guard";
import {HomeComponent} from "./components/home/home.component";
import {NotfoundComponent} from "./components/notfound/notfound.component";
import {RegisterComponent} from "./components/register/register.component";
import {LoginComponent} from "./components/login/login.component";
import {DashboardComponent} from "./components/dashboard/dashboard.component";
import {TasksComponent} from "./components/tasks/tasks.component";
import {ProjectComponent} from "./components/project/project.component";

export const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'login', component: LoginComponent},
  {path: 'dashboard', component: DashboardComponent},
  {path: 'tasks', component: TasksComponent},
  {path: 'project/:id', component: ProjectComponent},
  {path: '**', component: NotfoundComponent}
];
