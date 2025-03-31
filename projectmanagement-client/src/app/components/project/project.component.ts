import {Component, OnInit} from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatList, MatListItem} from "@angular/material/list";
import {NgForOf} from "@angular/common";
import {ActivatedRoute} from "@angular/router";
import {Project} from "../../models/project";
import {ProjectService} from "../../services/project.service";

@Component({
  selector: 'app-project',
  standalone: true,
  imports: [
    MatCardTitle,
    MatCardHeader,
    MatCard,
    MatCardContent,
    MatListItem,
    MatList,
    NgForOf
  ],
  templateUrl: './project.component.html',
  styleUrl: './project.component.css'
})
export class ProjectComponent implements OnInit {
  constructor(private readonly route: ActivatedRoute,
              private readonly projectService: ProjectService,) {}

  project: Project | undefined = undefined;

  ngOnInit(): void {
    const projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.projectService.getProjectById(projectId).subscribe(res => {
      this.project = res.data as Project;
    })
  }
}
