import {Component, Input, OnInit} from '@angular/core';
import {DatePipe} from "@angular/common";
import {StatusComponent} from "../status/status.component";
import {TypeComponent} from "../type/type.component";
import {Task} from "../../models/task";
import {UserService} from "../../services/user.service";
import {User} from "../../models/user";

@Component({
  selector: '[app-task-list-item]',
  standalone: true,
  imports: [
    DatePipe,
    StatusComponent,
    TypeComponent
  ],
  templateUrl: './task-list-item.component.html',
  styleUrl: './task-list-item.component.css'
})
export class TaskListItemComponent implements OnInit {

  @Input({required: true}) task!: Task;
  fullUserName: string = '';

  constructor(private readonly userService: UserService) {
  }

  ngOnInit(): void {
    this.resolveUserById(this.task.creatorId);
  }


  resolveUserById(creatorId: number) {
    this.userService.getUserById(creatorId).subscribe(res => {
      const user = res.data as User;
      this.fullUserName = user.firstName + " " + user.lastName;
    });
  }
}
