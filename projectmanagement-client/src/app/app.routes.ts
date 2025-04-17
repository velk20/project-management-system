import { Routes } from '@angular/router';
import {homeGuard} from "./auth/home.guard";
import {HomeComponent} from "./components/home/home.component";
import {NotfoundComponent} from "./components/notfound/notfound.component";
import {RegisterComponent} from "./components/register/register.component";
import {LoginComponent} from "./components/login/login.component";
import {DashboardComponent} from "./components/dashboard/dashboard.component";
import {TasksComponent} from "./components/tasks/tasks.component";
import {ProjectComponent} from "./components/project/project.component";
import {TaskViewComponent} from "./components/task-view/task-view.component";
import {authGuard} from "./auth/auth.guard";
import {ProfileComponent} from "./components/profile/profile.component";
import {ChangePasswordComponent} from "./components/change-password/change-password.component";
import {UserTasksComponent} from "./components/user-tasks/user-tasks.component";

export const routes: Routes = [
  {path: '', component: HomeComponent, canActivate: [homeGuard]},
  {path: 'register', component: RegisterComponent, canActivate: [homeGuard]},
  {path: 'login', component: LoginComponent, canActivate: [homeGuard]},
  {path: 'dashboard', component: DashboardComponent, canActivate: [authGuard]},
  {path: 'tasks', component: TasksComponent, canActivate: [authGuard]},
  {path: 'profile', component: ProfileComponent, canActivate: [authGuard]},
  {path: 'change-password', component: ChangePasswordComponent, canActivate: [authGuard]},
  {path: 'project/:id', component: ProjectComponent, canActivate: [authGuard]},
  {path: 'task/:id', component: TaskViewComponent, canActivate: [authGuard]},
  {path: 'user-tasks', component: UserTasksComponent, canActivate: [authGuard]},
  {path: '**', component: NotfoundComponent}
];
