import {Component, Input, OnInit} from '@angular/core';
import { Task } from '../../models/task';
import {DatePipe, NgForOf, Location, NgClass} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {TaskService} from "../../services/task.service";
import {TaskType} from "../../models/task-type.enum";
import {TaskStatus} from "../../models/task-status.enum";

@Component({
  selector: 'app-task-view',
  standalone: true,
  imports: [
    NgForOf,
    FormsModule,
    DatePipe,
    NgClass
  ],
  templateUrl: './task-view.component.html',
  styleUrl: './task-view.component.css'
})
export class TaskViewComponent implements OnInit {
  task!: Task;
  taskStatuses = Object.values(TaskStatus);
  taskTypes = Object.values(TaskType);

  constructor(private readonly route: ActivatedRoute,
              private readonly taskService: TaskService,
              private readonly location: Location,) {
  }

  ngOnInit(): void {
    const taskId = this.route.snapshot.paramMap.get('id');
    if (taskId) {
      this.taskService.getTaskById(taskId).subscribe(res => {
        this.task = res.data as Task;
      })
    }
  }

  newComment: string = '';

  addComment() {
    if (this.newComment.trim()) {
      const comment = {
        author: 'Current User', // You can later integrate with auth user
        text: this.newComment.trim(),
        createdAt: new Date().toISOString()
      };

      this.task.comments = this.task.comments || [];
      this.task.comments.push(comment as any); // Casting to any for simplicity

      this.newComment = '';
    }
  }

  getStatusClass(status: TaskStatus): string {
    switch (status) {
      case TaskStatus.New:
        return 'bg-primary text-white';
      case TaskStatus.Reopened:
        return 'bg-warning text-dark';
      case TaskStatus.Feedback:
        return 'bg-info text-dark';
      case TaskStatus.InProgress:
        return 'bg-success text-white';
      case TaskStatus.Resolved:
        return 'bg-primary text-white';
      case TaskStatus.Closed:
        return 'bg-secondary text-white';
      case TaskStatus.Rejected:
        return 'bg-danger text-white';
      case TaskStatus.Duplicate:
        return 'bg-info text-white';
      default:
        return 'bg-dark text-white';
    }
  }

  getTypeClass(type: TaskType): string {
    switch (type) {
      case TaskType.Feature:
        return 'text-success';
      case TaskType.Story:
        return 'text-primary';
      case TaskType.Bug:
        return 'text-warning';
      case TaskType.Epic:
        return 'text-danger';
      case TaskType.Test:
        return 'text-info';
      case TaskType.Idea:
        return 'text-secondary';
      default:
        return 'bg-dark text-white';
    }
  }

  goBack() {
    this.location.back();
  }
}
