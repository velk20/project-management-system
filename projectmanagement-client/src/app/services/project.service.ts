import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {AppResponse} from "../utils/app.response";
import {Constant} from "../utils/constant";

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  constructor(private readonly http: HttpClient) { }

  getAllProjectsForUser(userId: number): Observable<AppResponse> {
    return this.http.get<AppResponse>(`${Constant.PROJECTS_URL}/user/${userId}`);
  }

  getProjectById(projectId: number): Observable<AppResponse> {
    return this.http.get<AppResponse>(`${Constant.PROJECTS_URL}/${projectId}`);
  }
}
