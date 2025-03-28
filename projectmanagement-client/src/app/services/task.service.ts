import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AppResponse} from "../utils/app.response";
import {Observable} from "rxjs";
import {Constant} from "../utils/constant";

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  constructor(private http: HttpClient) {

  }

  getAllTasksForUser(userId: number): Observable<AppResponse> {
    return this.http.get<AppResponse>(`${Constant.TASKS_URL}/user/${userId}`);
  }

  getAllTasksForProject(projectId: number): Observable<AppResponse> {
    return this.http.get<AppResponse>(`${Constant.TASKS_URL}/project/${projectId}`);
  }
}
