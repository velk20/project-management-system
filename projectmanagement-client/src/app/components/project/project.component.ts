import {Component, OnInit} from '@angular/core';
import {Location, NgForOf, NgIf} from "@angular/common";
import {ActivatedRoute} from "@angular/router";
import {Project} from "../../models/project";
import {ProjectService} from "../../services/project.service";
import {TaskListItemComponent} from "../task-list-item/task-list-item.component";
import {AuthService} from "../../services/auth.service";
import Swal from "sweetalert2";
import {Task} from '../../models/task';
import {TaskStatus} from "../../models/task-status.enum";
import {TaskType} from "../../models/task-type.enum";
import {TaskService} from "../../services/task.service";

@Component({
  selector: 'app-project',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    TaskListItemComponent
  ],
  templateUrl: './project.component.html',
  styleUrl: './project.component.css'
})
export class ProjectComponent implements OnInit {
  constructor(private readonly route: ActivatedRoute,
              private readonly authService: AuthService,
              private readonly taskService: TaskService,
              private readonly projectService: ProjectService,
              private readonly location: Location) {}

  project: Project  = {} as Project;

  ngOnInit(): void {
    const projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.projectService.getProjectById(projectId).subscribe(res => {
      this.project = res.data as Project;
    })
  }

  goBack() {
    this.location.back();
  }

  isUserOwnerProject() {
    return this.project.ownerId === this.authService.getUserFromJwt().id;
  }

  onProjectUpdate() {
    Swal.fire({
      title: 'Update Project',
      html:
        `<input id="project-name" value="${this.project.name}" class="swal2-input" placeholder="Project Name (required)">` +
        `<textarea id="project-description" class="swal2-textarea" placeholder="Project Description (optional)">${this.project.description || ''}</textarea>`,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Update',
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
        this.updateProject(result);
      }
    });
  }

  private updateProject(result: any) {
    const { name, description } = result.value;
    const updatedProject: Project = {
      name: name,
      description: description,
      ownerId: this.authService.getUserFromJwt().id
    }

    this.projectService.updateProject(this.project.id || 0, updatedProject).subscribe(res => {
      this.project = res.data as Project;

      Swal.fire('Success', `You have updated project!`, 'success');
    }, error => {
      Swal.fire('Error', error.error.message, 'error');
    })
  }

  onCreateTask() {
    const statusOptions = Object.values(TaskStatus)
      .map(status => `<option value="${status}">${status.replace(/_/g, ' ')}</option>`)
      .join('');

    const typeOptions = Object.values(TaskType)
      .map(type => `<option value="${type}">${type}</option>`)
      .join('');

    Swal.fire({
      title: 'Create New Task',
      html:
        `<input id="task-title" class="swal2-input" placeholder="Title (required)">` +
        `<textarea id="task-description" class="swal2-textarea" placeholder="Description (optional)" rows="5" style="height: 120px; resize: vertical;"></textarea>` +
        `<select id="task-status" class="swal2-select">${statusOptions}</select>` +
        `<select id="task-type" class="swal2-select">${typeOptions}</select>` +
        `<input id="task-assignee" class="swal2-input" placeholder="Assignee ID (optional)" type="number">`,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Create',
      preConfirm: () => {
        const title = (document.getElementById('task-title') as HTMLInputElement).value.trim();
        const description = (document.getElementById('task-description') as HTMLTextAreaElement).value.trim();
        const status = (document.getElementById('task-status') as HTMLSelectElement).value;
        const type = (document.getElementById('task-type') as HTMLSelectElement).value;
        const assigneeId = (document.getElementById('task-assignee') as HTMLInputElement).value;

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
          projectId: this.project.id ?? 0
        }

        return newTask;
      }
    }).then((result: any) => {
      if (result.isConfirmed && result.value) {
        const newTask: Task = result.value;
       this.taskService.createTask(newTask).subscribe(res => {
         if (this.project.tasks){
           this.project.tasks.push(res.data as Task);
         }
       })
      }
    });
  }
}
