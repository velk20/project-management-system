import {Component, OnInit} from '@angular/core';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle
} from "@angular/material/card";
import {MatList, MatListItem} from "@angular/material/list";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {ActivatedRoute} from "@angular/router";
import {Project} from "../../models/project";
import {ProjectService} from "../../services/project.service";
import {MatButton} from "@angular/material/button";
import {StatusComponent} from "../status/status.component";
import {TypeComponent} from "../type/type.component";

@Component({
  selector: 'app-project',
  standalone: true,
  imports: [
    MatCardTitle,
    MatCardHeader,
    MatCardSubtitle,
    MatCard,
    MatCardContent,
    MatListItem,
    MatList,
    NgForOf,
    DatePipe,
    MatCardActions,
    NgIf,
    MatButton,
    StatusComponent,
    TypeComponent
  ],
  templateUrl: './project.component.html',
  styleUrl: './project.component.css'
})
export class ProjectComponent implements OnInit {
  constructor(private readonly route: ActivatedRoute,
              private readonly projectService: ProjectService,) {}

  project: Project  = {} as Project;

  ngOnInit(): void {
    const projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.projectService.getProjectById(projectId).subscribe(res => {
      this.project = res.data as Project;
    })
  }

  getOwnerName(ownerId: number): string {
    // Mocked function to return owner name
    const users = [
      { id: 1, name: 'John Doe' },
      { id: 2, name: 'Jane Smith' }
    ];
    const user = users.find(user => user.id === ownerId);
    return user ? user.name : 'Unknown';
  }
}
