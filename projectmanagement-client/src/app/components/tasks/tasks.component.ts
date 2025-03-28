import {Component, OnInit} from '@angular/core';
import {NgForOf} from "@angular/common";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {Task} from "../../models/task";

// @ts-ignore
@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [
    NgForOf
  ],
  templateUrl: './tasks.component.html',
  styleUrl: './tasks.component.css'
})
export class TasksComponent implements OnInit {
  constructor(private http: HttpClient, private router: Router) {
  }

  tasks: Task[] = [];

  ngOnInit(): void {
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras.state) {
      //TODO check why I cannot extract the data
      this.tasks = navigation.extras.state['tasks'] as Task[];
      console.log(this.tasks);
    }
    }

}
