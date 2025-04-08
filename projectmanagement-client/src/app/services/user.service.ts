import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {AppResponse} from "../utils/app.response";
import {Constant} from "../utils/constant";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private readonly http: HttpClient) { }

  getUserById(userId: number): Observable<AppResponse> {
    return this.http.get<AppResponse>(`${Constant.USERS_URL}/${userId}`);
  }

  getAllUsers():Observable<AppResponse> {
    return this.http.get<AppResponse>(`${Constant.USERS_URL}`)
  }

  searchUsers(username: string): Observable<AppResponse> {
    return this.http.get<AppResponse>(`${Constant.USERS_URL}/search?username=${username}`);
  }
}
