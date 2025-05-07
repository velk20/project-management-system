import {Component, inject, OnInit} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {UserService} from "../../services/user.service";
import {AuthService} from "../../services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {UserDetails} from "../../models/user";
import {ProjectService} from "../../services/project.service";
import {Project, UpdateProject} from "../../models/project";
import Swal from "sweetalert2";

@Component({
  selector: 'app-project-users',
  standalone: true,
  imports: [
    NgForOf,
    NgIf
  ],
  templateUrl: './project-users.component.html',
  styleUrl: './project-users.component.css'
})
export class ProjectUsersComponent implements OnInit{
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  private readonly projectService = inject(ProjectService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  protected readonly Math = Math;

  loggedUserId: number = 0;
  project: Project | null = null;
  users: UserDetails[] = [];
  paginatedUsers: UserDetails[] = [];
  allUsers: UserDetails[] = [];

  currentPage = 0;
  pageSize = 10;

  ngOnInit() {
    const projectId = Number(this.route.snapshot.paramMap.get('id'));

    this.projectService.getProjectById(projectId).subscribe(res => {
      this.project = res.data as Project;
      this.projectService.getProjectMembersById(projectId).subscribe(res => {
        this.users = res.data as UserDetails[];
        console.log(this.users)
        this.updatePagination();

        this.loggedUserId = this.authService.getUserFromJwt().id;

        this.userService.getAllUsers().subscribe(res => {
          this.allUsers = res.data as UserDetails[];
        })
      })
    })


  }

  updatePagination() {
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
    this.paginatedUsers = this.users.slice(start, end);
  }

  nextPage() {
    if ((this.currentPage + 1) * this.pageSize < this.users.length) {
      this.currentPage++;
      this.updatePagination();
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.updatePagination();
    }
  }

  onAddUserToProject() {
    this.getAllProjectUsers();

    Swal.fire({
      title: 'Add member to project',
      html:
        `<input id="task-assignee-id" class="swal2-input" hidden="hidden" placeholder="Assignee" type="number">` +
        `<input id="task-assignee" class="swal2-input" placeholder="Assignee" type="text">
        <div id="assignee-suggestions" style="max-height: 100px; overflow-y: auto; border: 1px solid #ddd; border-radius: 4px; display: none; background: white; position: relative; z-index: 9999;"></div>`,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Add',
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

          const filteredUsers = this.allUsers.filter(user =>
            user.username.toLowerCase().includes(query) &&
            user.id !== this.project?.ownerId &&
            !this.project?.teamMembers?.some(m => m.id === user.id));

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
        const assigneeId = (document.getElementById('task-assignee-id') as HTMLInputElement).value;

        if (!assigneeId) {
          Swal.showValidationMessage('Username of user is required');
          return false;
        }

        const tasksIds: number[] = (this.project?.tasks || []).map(task => task.id as number);
        const membersIds: number [] = this.project?.teamMembers?.map(teamMember => teamMember.id as number) ?? [];
        membersIds.push(Number(assigneeId));

        const project: UpdateProject = {
          name: this.project?.name || '',
          description: this.project?.description,
          tasksId: tasksIds,
          teamMembersId: membersIds,

        }
        console.log(project)
        return project;
      }
    }).then((result: any) => {
      if (result.isConfirmed && result.value) {
        const updatedProject: UpdateProject = result.value;
        this.projectService.updateProject(this.project?.id ?? 0, updatedProject).subscribe(res => {
          this.project = res.data as Project;
          this.projectService.getProjectMembersById(this.project?.id || 0).subscribe(res => {
            this.users = res.data as UserDetails[];
            console.log(this.users)
            this.updatePagination();

            this.loggedUserId = this.authService.getUserFromJwt().id;

            this.userService.getAllUsers().subscribe(res => {
              this.allUsers = res.data as UserDetails[];
            })
          })
          Swal.fire('Success', `User was added to project members!`, 'success');
        }, error => {
          Swal.fire('Error', error.error.message, 'error');
        })
      }
    });
  }

  private getAllProjectUsers() {
    this.projectService.getProjectMembersById(this.project?.id ?? 0).subscribe(res => {
      this.users = res.data as UserDetails[];
      this.updatePagination();
    })
  }

  async removeUserFromProject(user: UserDetails) {
    const result = await Swal.fire({
      title: 'Are you sure?',
      text: 'This will remove this user from the project members!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, remove!',
      customClass: {
        popup: 'border rounded shadow p-3'
      }
    });

    if (result.isConfirmed) {
      let updatedUsers = this.project?.teamMembers?.filter(u => u.id !== user.id).map(u => u.id) ?? [] as number [];
      if (updatedUsers && this.project) {
        const updateProject: UpdateProject = {
          name: this.project.name,
          teamMembersId: (updatedUsers ?? []) as number[],
        }
        this.projectService.updateProject(this.project?.id || 0, updateProject ).subscribe(res=>{
          let message = res.message;
          this.project = res.data as Project;
          this.projectService.getProjectMembersById(this.project?.id || 0).subscribe(res => {
            this.users = res.data as UserDetails[];
            this.updatePagination();


            this.userService.getAllUsers().subscribe(res => {
              this.allUsers = res.data as UserDetails[];
            })
          })
          Swal.fire('Success', message, 'success')
        }, error => {
          Swal.fire('Error', error.error.message, 'error');
        })
      }

    }
  }

}
