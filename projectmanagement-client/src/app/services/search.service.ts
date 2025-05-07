import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {AppResponse} from "../utils/app.response";
import {Constant} from "../utils/constant";

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  constructor(private readonly http: HttpClient) { }

  searchQuery(query: string): Observable<AppResponse> {
    let q = `${Constant.SEARCH_URL}?q=${query}`;
    console.log(q)
    return this.http.get<AppResponse>(q);
  }
}
