import {Component, Input, OnInit} from '@angular/core';
import {Task} from '../../models/task';
import {DatePipe, NgForOf, Location, NgClass, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {TaskService} from "../../services/task.service";
import {TaskType} from "../../models/task-type.enum";
import {TaskStatus} from "../../models/task-status.enum";
import {UserService} from "../../services/user.service";
import {AuthService} from "../../services/auth.service";
import {TaskComment} from "../../models/comment";
import {CommentService} from "../../services/comment.service";
import {CommentComponent} from "../comment/comment.component";
import {User} from "../../models/user";
import {debounceTime, Subject} from "rxjs";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-task-view',
  standalone: true,
  imports: [
    NgForOf,
    FormsModule,
    NgClass,
    CommentComponent,
    NgIf,
    DatePipe
  ],
  templateUrl: './task-view.component.html',
  styleUrl: './task-view.component.css'
})
export class TaskViewComponent implements OnInit {
  task!: Task;
  creatorUsername: string = '';
  taskStatuses = Object.values(TaskStatus);
  taskTypes = Object.values(TaskType);

  newComment: string = '';

  assigneeSearch: string = '';
  filteredUsers: User[] = [];
  private readonly searchSubject = new Subject<string>();

  constructor(private readonly route: ActivatedRoute,
              private readonly taskService: TaskService,
              private readonly commentService: CommentService,
              private readonly authService: AuthService,
              private readonly userService: UserService,
              private readonly toaster: ToastrService,
              private readonly location: Location,) {
    // Debounced search
    this.searchSubject.pipe(
      debounceTime(300) // wait 300ms after last keystroke
    ).subscribe(searchText => {
      this.userService.searchUsers(searchText).subscribe(res => {
        this.filteredUsers = res.data as User[];
      });
    });
  }


  ngOnInit(): void {
    const taskId = this.route.snapshot.paramMap.get('id');
    if (taskId) {
      this.loadTaskInfo(taskId);
    }

    //Load new task info if search was used
    this.route.params.subscribe(params => {
      const newTaskId = +params['id'];
      this.loadTaskInfo(newTaskId.toString());
    });
  }

  private loadTaskInfo(taskId: string) {
    this.taskService.getTaskById(taskId).subscribe(res => {
      let task = res.data as Task;
      this.task = task;

      if (task.creatorId) {
        this.userService.getUserById(task.creatorId).subscribe(userRes => {
          const user = userRes.data as User;
          this.creatorUsername = user.username;

          if (task.assigneeId) {
            this.userService.getUserById(task.assigneeId).subscribe(userRes => {
              const user = userRes.data as User;
              this.assigneeSearch = user.username;
            })
          }
        });
      }
    });
  }

  onAssigneeInput() {
    if (this.assigneeSearch.trim()) {
      this.searchSubject.next(this.assigneeSearch.trim());
    } else {
      this.filteredUsers = [];
    }
  }

  selectAssignee(user: User) {
    this.task.assigneeId = user.id;
    this.assigneeSearch = user.username;
    this.filteredUsers = [];
  }


  addComment() {
    if (this.newComment.trim()) {
      const comment: TaskComment = {
        authorId: this.authService.getUserFromJwt().id,
        taskId: this.task.id ?? 0,
        content: this.newComment
      };

      this.commentService.createComment(comment).subscribe(res => {
        let newComment = res.data as TaskComment;
        this.task.comments?.push(newComment);
      })


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

  saveTask() {
      this.taskService.updateTask(this.task.id, this.task).subscribe(res => {
        this.task = res.data as Task;
        this.toaster.success(res.message, 'success');
      }, err => {
        let message = err.error.message;
        this.toaster.error(message);
      })
  }

  onCommentDeleted(deletedCommentId: number) {
    if (this.task.comments?.length) {
      this.task.comments = this.task.comments.filter(c => c.id !== deletedCommentId);
    }
  }
}
