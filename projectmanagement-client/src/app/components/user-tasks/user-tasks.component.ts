import {Component, OnInit} from '@angular/core';
import {NgForOf, NgIf, Location} from "@angular/common";
import {TaskListItemComponent} from "../task-list-item/task-list-item.component";
import {TaskService} from "../../services/task.service";
import {AuthService} from "../../services/auth.service";
import {Pageable} from "../../models/page";
import {JwtPayload} from "../../models/auth";
import {PageableTasks, Task} from '../../models/task';
import { ActivatedRoute } from '@angular/router';
import {TaskStatus} from "../../models/task-status.enum";

@Component({
  selector: 'app-user-tasks',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    TaskListItemComponent
  ],
  templateUrl: './user-tasks.component.html',
  styleUrl: './user-tasks.component.css'
})
export class UserTasksComponent implements OnInit {
  user!: JwtPayload;
  pageable: Pageable={ page:0, size: 10}
  tasks: Task[] = [];
  totalPages: number = 0;
  statuss: TaskStatus | null = null;
  nonStatus: TaskStatus | null = null;
  filter: boolean = false;

  constructor(private readonly taskService: TaskService,
              private readonly authService: AuthService,
              private readonly location: Location,
              private readonly activatedRoute: ActivatedRoute,) {
    this.activatedRoute.queryParams.subscribe(params => {
      if (params['status']) {
        this.statuss = params['status'] as TaskStatus;
      } else if (params['not_status']) {
        this.nonStatus = params['not_status'] as TaskStatus;
        this.filter = true;
      }
    });
  }

  ngOnInit(): void {
    this.user = this.authService.getUserFromJwt();
    this.getTaskAssignedToUser(this.user.id);

  }

  goBack() {
    this.location.back();
  }

  previousPage() {
    this.pageable.page -= 1;
    if (this.pageable.page < 1) {
      this.pageable.page = 0;
    }
    this.getTaskAssignedToUser(this.user.id)
  }

  nextPage() {
    this.pageable.page += 1;

    this.getTaskAssignedToUser(this.user.id)
  }

  private getTaskAssignedToUser(userId: number) {
    this.taskService.searchTask(userId, '', this.filter ? '' : this.statuss ?? '', '', this.pageable).subscribe(res => {
      let data = res.data as PageableTasks;
      console.log(data)
      if (this.filter) {
        console.log(Object.values(TaskStatus).find(value => value == this.statuss))
        this.tasks = data.tasks.filter(t => t.status !== this.nonStatus);
      } else {
        this.tasks = data.tasks;
      }
      this.totalPages = data.totalPages;
    });
  }
}
