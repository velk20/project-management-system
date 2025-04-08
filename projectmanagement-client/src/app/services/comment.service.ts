import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TaskComment} from "../models/comment";
import {Observable} from "rxjs";
import {AppResponseWithMessage, AppResponseWithNoData} from "../utils/app.response";
import {Constant} from "../utils/constant";

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private readonly http: HttpClient) { }

  createComment(comment: TaskComment):Observable<AppResponseWithMessage> {
    return this.http.post<AppResponseWithMessage>(`${Constant.TASKS_URL}/${comment.taskId}/comments`, comment);
  }

  updateComment(comment: TaskComment):Observable<AppResponseWithMessage> {
    return this.http.put<AppResponseWithMessage>(`${Constant.TASKS_URL}/${comment.taskId}/comments/${comment.id}`, comment);
  }

  deleteComment(id: number | undefined, taskId: number):Observable<AppResponseWithNoData> {
    return this.http.delete<AppResponseWithNoData>(`${Constant.TASKS_URL}/${taskId}/comments/${id}`);
  }
}
