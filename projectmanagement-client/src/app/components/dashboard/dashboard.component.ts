import { Component } from '@angular/core';
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatListModule} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    NgForOf
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  menuItems = [
    { name: 'Dashboard', icon: 'dashboard' },
    { name: 'Projects', icon: 'folder' },
    { name: 'Tasks', icon: 'task' },
    { name: 'Team', icon: 'group' }
  ];
  selectedMenu: string = 'Dashboard';

  onMenuItemClick(item: any): void {
    this.selectedMenu = item.name;
  }
}
