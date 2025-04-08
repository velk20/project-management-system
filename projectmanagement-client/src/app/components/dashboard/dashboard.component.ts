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

  getProjectForTask(task: Task): Project | undefined {
    return this.projects.find(project => project.id === task.projectId);
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

  onProjectClick(project: Project) {
    this.taskService.getAllTasksForProject(project.id).subscribe(res=>{
      const tasks = res.data as Task[];
      console.log(tasks)
      this.router.navigate(['/tasks'],{
        state: {tasks: tasks},
      })
    })
  }

  resolveUserById(creatorId: number): string {
    let username: string = "";

    this.userService.getUserById(creatorId).subscribe(res => {
        const user = res.data as User;
        username = user.username;
    })

    return username;
  }
}
