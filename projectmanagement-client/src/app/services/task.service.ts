import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AppResponse, AppResponseWithMessage} from "../utils/app.response";
import {Observable} from "rxjs";
import {Constant} from "../utils/constant";
import { Task } from '../models/task';
import {Pageable} from "../models/page";

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  constructor(private http: HttpClient) {

  }

  getAllTasksForUser(userId: number,  pageable: Pageable): Observable<AppResponse> {
    return this.http.get<AppResponse>(`${Constant.TASKS_URL}/user/${userId}`, {
      params: {
        page: pageable.page,
        size: pageable.size
      }
    });
  }

  getAllTasksForProject(projectId: number, pageable: Pageable): Observable<AppResponse> {
    return this.http.get<AppResponse>(`${Constant.TASKS_URL}/project/${projectId}`, {
      params: {
        page: pageable.page,
        size: pageable.size
      }
    });
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

  searchTask(userId: number, title: string, status: string, type:string, pageable: Pageable): Observable<AppResponse> {
    return this.http.get<AppResponse>(`${Constant.TASKS_URL}/search?userId=${userId}&title=${title}&status=${status}&type=${type}`, {
      params: {
        page: pageable.page,
        size: pageable.size
      }
    });
  }
}
