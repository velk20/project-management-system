import { Routes } from '@angular/router';
import {homeGuard} from "./auth/home.guard";
import {HomeComponent} from "./components/home/home.component";
import {NotfoundComponent} from "./components/notfound/notfound.component";

export const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: '**', component: NotfoundComponent}
];
