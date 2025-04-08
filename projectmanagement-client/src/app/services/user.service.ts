import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {AppResponse, AppResponseWithMessage, AppResponseWithNoData} from "../utils/app.response";
import {Constant} from "../utils/constant";
import {ChangeUserPassword, User} from "../models/user";

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

  updateProfile(userId: number, user: User): Observable<AppResponseWithMessage> {
    return this.http.put<AppResponseWithMessage>(`${Constant.USERS_URL}/${userId}`, user);
  }

  deleteUser(userId: number): Observable<AppResponseWithNoData> {
    return this.http.delete<AppResponseWithNoData>(`${Constant.USERS_URL}/${userId}`);
  }

  changePassword(changePassword:ChangeUserPassword):Observable<AppResponseWithNoData> {
    return this.http.put<AppResponseWithNoData>(`${Constant.USERS_URL}/change-password`, changePassword);
  }
}
