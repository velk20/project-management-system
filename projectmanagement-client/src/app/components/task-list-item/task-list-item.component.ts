import {Component, Input, OnInit} from '@angular/core';
import {DatePipe, NgIf} from "@angular/common";
import {StatusComponent} from "../status/status.component";
import {TypeComponent} from "../type/type.component";
import {Task} from "../../models/task";
import {UserService} from "../../services/user.service";
import {User} from "../../models/user";
import {RouterLink} from "@angular/router";

@Component({
  selector: '[app-task-list-item]',
  standalone: true,
  imports: [
    DatePipe,
    StatusComponent,
    TypeComponent,
    RouterLink,
    NgIf
  ],
  templateUrl: './task-list-item.component.html',
  styleUrl: './task-list-item.component.css'
})
export class TaskListItemComponent implements OnInit {

  @Input({required: true}) task!: Task;
  fullUserName: string = '';
  fullAssigneeName: string = '';

  constructor(private readonly userService: UserService) {
  }

  ngOnInit(): void {
    this.resolveUserById(this.task.creatorId);
    this.resolveAssigneeById(this.task.assigneeId ?? 0);
  }


  resolveUserById(creatorId: number) {
    this.userService.getUserById(creatorId).subscribe(res => {
      const user = res.data as User;
      this.fullUserName = user.firstName + " " + user.lastName;
    });
  }

  resolveAssigneeById(assigneeId: number) {
    this.userService.getUserById(assigneeId).subscribe(res => {
      const user = res.data as User;
      this.fullAssigneeName = user.firstName + " " + user.lastName;
    })
  }
}
