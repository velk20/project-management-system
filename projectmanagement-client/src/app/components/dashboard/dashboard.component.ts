import {Component, OnInit} from '@angular/core';
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatListModule} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {NgForOf, NgIf, NgSwitch, NgSwitchCase, NgSwitchDefault} from "@angular/common";
import {TaskService} from "../../services/task.service";
import {PageableTasks, Task} from "../../models/task";
import {JwtPayload} from "../../models/auth";
import {AuthService} from "../../services/auth.service";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {animate, style, transition, trigger} from "@angular/animations";
import {Project} from "../../models/project";
import {ProjectService} from "../../services/project.service";
import {Router, RouterLink} from "@angular/router";
import {UserService} from "../../services/user.service";
import {User} from "../../models/user";
import {TasksComponent} from "../tasks/tasks.component";
import Swal from 'sweetalert2';
import {TaskStatus} from "../../models/task-status.enum";
import {TaskType} from "../../models/task-type.enum";
import {Pageable} from "../../models/page";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    NgForOf,
    NgSwitchCase,
    NgSwitch,
    NgSwitchDefault,
    MatCard,
    MatCardTitle,
    MatCardContent,
    MatCardHeader,
    RouterLink,
    TasksComponent,
    NgIf
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  animations:[
    trigger('contentAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class DashboardComponent implements OnInit {
  constructor(private readonly taskService: TaskService,
              private readonly projectService: ProjectService,
              private readonly userService: UserService,
              private readonly authService: AuthService,
              private readonly router: Router) {
  }

  tasks: Task[] = [];
  projects: Project[] = [];
  allProjects: Project[] = [];
  users: User[] = [];
  finishedTasks: Task[] = [];
  pendingTasks: Task[] = [];

  pageable: Pageable={ page:0, size: 10}
  totalPages: number = 0;

  menuItems = [
    { name: 'Dashboard', icon: 'dashboard', color: '#3498db' },
    { name: 'Projects', icon: 'folder', color: '#2ecc71' },
    { name: 'Your Tasks', icon: 'checklist', color: '#e74c3c' }
  ];
  selectedMenu: string = 'Dashboard';

  isAdmin:boolean = false;

  ngOnInit(): void {
    const user: JwtPayload  = this.authService.getUserFromJwt();
    this.isAdmin = this.authService.isAdmin();

    this.getUserTasks(user.id)
    this.getUserProjects(user.id)
    this.getAllProjects()
    this.getFinishedTasks(user.id)
    this.getPendingTasks(user.id)
  }

  onMenuItemClick(item: any): void {
    this.selectedMenu = item.name;
  }

  private getUserTasks(userId: number){
    this.taskService.getAllTasksForUser(userId, this.pageable).subscribe(res=>{
      let data = res.data as PageableTasks;
      this.totalPages = data.totalPages;
      this.tasks = data.tasks;
    });
  }

  private getUserProjects(userId: number) {
    this.projectService.getAllProjectsForUser(userId).subscribe(res=>{
      this.projects = res.data as Project[];
    })
  }

  onCreateProject() {
    Swal.fire({
      title: 'Create New Project',
      html:
        `<input id="project-name" class="swal2-input" placeholder="Project Name (required)">` +
        `<textarea id="project-description" class="swal2-textarea" placeholder="Project Description (optional)"></textarea>`,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Create',
      preConfirm: () => {
        const name = (document.getElementById('project-name') as HTMLInputElement).value.trim();
        const description = (document.getElementById('project-description') as HTMLTextAreaElement).value.trim();

        if (!name) {
          Swal.showValidationMessage('Project name is required');
          return false;
        }

        return { name, description };
      }
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        this.createProject(result);
      }
    });
  }

  private createProject(result:any) {
    const {name, description} = result.value;
    const newProject: Project = {
      name: name,
      description: description,
      ownerId: this.authService.getUserFromJwt().id
    };

    this.projectService.createProject(newProject).subscribe(res => {
      let data = res.data as Project;
      this.projects.push(data);

      Swal.fire('Success', `You have added "${data.name}" project!`, 'success');
    }, error => {
      Swal.fire('Error', error.error.message, 'error');
    })
  }

  onCreateTask() {
    this.getAllUsers();

    const statusOptions = Object.values(TaskStatus)
      .map(status => `<option value="${status}">${status.replace(/_/g, ' ')}</option>`)
      .join('');

    const typeOptions = Object.values(TaskType)
      .map(type => `<option value="${type}">${type}</option>`)
      .join('');

    const projectOptions = this.projects.map(prj=> `<option value="${prj.id}">${prj.name}</option>`).join('');

    Swal.fire({
      title: 'Create New Task',
      html: `<input id="task-title" class="swal2-input" placeholder="Title (required)">` +
        `<textarea id="task-description" class="swal2-textarea" placeholder="Description (optional)" rows="5" style="height: 120px; resize: vertical;"></textarea>` +
        `<select id="task-status" class="swal2-select">${statusOptions}</select>` +
        `<select id="task-type" class="swal2-select">${typeOptions}</select>` +
        `<select id="task-project" class="swal2-select">${projectOptions}</select>` +
        `<input id="task-assignee-id" class="swal2-input" hidden="hidden" placeholder="Assignee" type="number">` +
        `<input id="task-assignee" class="swal2-input" placeholder="Assignee" type="text">
        <div id="assignee-suggestions" style="max-height: 100px; overflow-y: auto; border: 1px solid #ddd; border-radius: 4px; display: none; background: white; position: relative; z-index: 9999;"></div>`,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Create',
      didOpen: () => {
        const assigneeInput = document.getElementById('task-assignee') as HTMLInputElement;
        const assigneeId = document.getElementById('task-assignee-id') as HTMLInputElement;
        const suggestionsContainer = document.getElementById('assignee-suggestions') as HTMLDivElement;

        assigneeInput.addEventListener('input', () => {
          const query = assigneeInput.value.toLowerCase();
          suggestionsContainer.innerHTML = '';

          if (!query) {
            suggestionsContainer.style.display = 'none';
            return;
          }

          const filteredUsers = this.users.filter(user => user.username.toLowerCase().includes(query));

          if (filteredUsers.length === 0) {
            suggestionsContainer.style.display = 'none';
            return;
          }

          filteredUsers.forEach(user => {
            const option = document.createElement('div');
            option.textContent = `${user.username}`;
            option.style.padding = '5px 10px';
            option.style.cursor = 'pointer';
            option.addEventListener('click', () => {
              assigneeInput.value = user.username;
              assigneeId.value = user.id?.toString() ?? '0';
              suggestionsContainer.style.display = 'none';
            });
            suggestionsContainer.appendChild(option);
          });

          suggestionsContainer.style.display = 'block';
        });
      },
      preConfirm: () => {
        const title = (document.getElementById('task-title') as HTMLInputElement).value.trim();
        const description = (document.getElementById('task-description') as HTMLTextAreaElement).value.trim();
        const status = (document.getElementById('task-status') as HTMLSelectElement).value;
        const type = (document.getElementById('task-type') as HTMLSelectElement).value;
        const projectId = (document.getElementById('task-project') as HTMLSelectElement).value;
        const assigneeId = (document.getElementById('task-assignee-id') as HTMLInputElement).value;

        if (!title) {
          Swal.showValidationMessage('Title is required');
          return false;
        }

        const newTask: Task = {
          title,
          description,
          status: Object.values(TaskStatus).find(value => value === status) ?? TaskStatus.New,
          type: Object.values(TaskType).find(value => value === type) ?? TaskType.Story,
          creatorId: this.authService.getUserFromJwt().id,
          assigneeId: Number(assigneeId) || undefined,
          projectId: Number(projectId)
        }

        return newTask;
      }
    }).then((result: any) => {
      if (result.isConfirmed && result.value) {
        const newTask: Task = result.value;
        this.taskService.createTask(newTask).subscribe(res => {
          this.tasks.push(res.data as Task);
        })
      }
    });
  }

  private getAllUsers(){
    this.userService.getAllUsers().subscribe(res => {
      this.users = res.data as User[];
    })
  }

  previousPage() {
    const user: JwtPayload  = this.authService.getUserFromJwt();
    this.pageable.page -= 1;
    if (this.pageable.page < 1) {
      this.pageable.page = 0;
    }
    this.getUserTasks(user.id)
  }

  nextPage() {
    const user: JwtPayload  = this.authService.getUserFromJwt();
    this.pageable.page += 1;

    this.getUserTasks(user.id)
  }

  private getFinishedTasks(userId: number) {
    this.taskService.searchTask(userId, '', TaskStatus.Closed,'', this.pageable).subscribe(res => {
      let tasks = res.data as PageableTasks;
      this.finishedTasks = tasks.tasks;
    });
  }

  private getPendingTasks(userId: number) {
    this.taskService.searchTask(userId, '','','', this.pageable).subscribe(res => {
      let tasks = res.data as PageableTasks;
      this.pendingTasks = tasks.tasks.filter(t => t.status !== TaskStatus.Closed);
    });
  }

  protected readonly TaskStatus = TaskStatus;

  private getAllProjects() {
    this.projectService.getAll().subscribe(res => {
      this.allProjects = res.data as Project[];
    }, error => {
      Swal.fire('Error', error.error.message, 'error');
    })
  }
}
