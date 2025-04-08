import {Component, OnInit} from '@angular/core';
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatListModule} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {DatePipe, NgClass, NgForOf, NgIf, NgSwitch, NgSwitchCase, NgSwitchDefault} from "@angular/common";
import {TaskService} from "../../services/task.service";
import {Task} from "../../models/task";
import {JwtPayload} from "../../models/auth";
import {AuthService} from "../../services/auth.service";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {animate, style, transition, trigger} from "@angular/animations";
import {Project} from "../../models/project";
import {ProjectService} from "../../services/project.service";
import {Router, RouterLink} from "@angular/router";
import {TypeComponent} from "../type/type.component";
import {StatusComponent} from "../status/status.component";
import {UserService} from "../../services/user.service";
import {User} from "../../models/user";
import {TasksComponent} from "../tasks/tasks.component";
import Swal from 'sweetalert2';

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
    NgIf,
    NgSwitchCase,
    NgSwitch,
    NgSwitchDefault,
    MatCard,
    MatCardTitle,
    MatCardContent,
    MatGridList,
    MatCardHeader,
    MatGridTile,
    NgClass,
    RouterLink,
    DatePipe,
    TypeComponent,
    StatusComponent,
    TasksComponent
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
  finishedTasks: number = 0;
  pendingTasks: number = 0;

  menuItems = [
    { name: 'Dashboard', icon: 'dashboard', color: '#3498db' },
    { name: 'Projects', icon: 'folder', color: '#2ecc71' },
    { name: 'Your Tasks', icon: 'checklist', color: '#e74c3c' }
  ];
  selectedMenu: string = 'Dashboard';


  ngOnInit(): void {
    const user: JwtPayload  = this.authService.getUserFromJwt();

    this.getUserTasks(user.id)
    this.getUserProjects(user.id)
  }

  onMenuItemClick(item: any): void {
    this.selectedMenu = item.name;
  }

  private getUserTasks(userId: number){
    this.taskService.getAllTasksForUser(userId).subscribe(res=>{
      this.tasks = res.data as Task[];
      this.finishedTasks = this.tasks.filter(task => task.status === 'Closed').length;
      this.pendingTasks = this.tasks.length - this.finishedTasks;
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
}
