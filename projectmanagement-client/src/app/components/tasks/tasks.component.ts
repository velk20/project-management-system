import {Component, Input, OnInit} from '@angular/core';
import {DatePipe, NgForOf} from "@angular/common";
import {Task} from "../../models/task";
import {TypeComponent} from "../type/type.component";
import {StatusComponent} from "../status/status.component";
import {TaskListItemComponent} from "../task-list-item/task-list-item.component";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [
    NgForOf,
    TypeComponent,
    StatusComponent,
    DatePipe,
    TaskListItemComponent,
    RouterLink
  ],
  templateUrl: './tasks.component.html',
  styleUrl: './tasks.component.css'
})
export class TasksComponent {

  @Input() tasks: Task[] = [];

}
