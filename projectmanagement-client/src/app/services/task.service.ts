import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AppResponse, AppResponseWithMessage} from "../utils/app.response";
import {Observable} from "rxjs";
import {Constant} from "../utils/constant";
import { Task } from '../models/task';

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

  createTask(newTask: Task): Observable<AppResponseWithMessage> {
    return this.http.post<AppResponseWithMessage>(`${Constant.TASKS_URL}`, newTask);
  }

  getTaskById(taskId: string) : Observable<AppResponse> {
    return this.http.get<AppResponse>(`${Constant.TASKS_URL}/${taskId}`);
  }

  updateTask(id: number | undefined, task: Task): Observable<AppResponseWithMessage> {
    return this.http.put<AppResponseWithMessage>(`${Constant.TASKS_URL}/${id}`, task);
  }
}
