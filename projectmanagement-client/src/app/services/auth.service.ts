import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { JwtPayload, JwtTokenResponse, LoginUser, RegisterUser } from '../models/auth';
import { Constant } from '../utils/constant';
import { AppResponseWithMessage } from '../utils/app.response';
import {jwtDecode} from "jwt-decode";

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private jwtSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

  constructor(private http: HttpClient) {
    const token = localStorage.getItem('token');
    this.jwtSubject.next(token);
  }

  registerUser(user: RegisterUser): Observable<AppResponseWithMessage> {
    return this.http.post<AppResponseWithMessage>(`${Constant.AUTH_URL}/register`, user);
  }

  loginUser(user: LoginUser): Observable<AppResponseWithMessage> {
    return this.http.post<AppResponseWithMessage>(`${Constant.AUTH_URL}/authenticate`, user);
  }

  login(tokenResponse: JwtTokenResponse): void {
    localStorage.setItem('token', tokenResponse.token);
    this.jwtSubject.next(tokenResponse.token);
  }

  logout(): void {
    localStorage.removeItem('token');
    this.jwtSubject.next(null);
  }

  getJwtToken(): string | null {
    return localStorage.getItem('token');
  }

  getJwtTokenAsObservable(): Observable<string | null> {
    return this.jwtSubject.asObservable();
  }

  getUserFromJwt(): JwtPayload {
    let token = localStorage.getItem('token');
    if (token != null) {
      return jwtDecode<JwtPayload>(token);
    }
    throw new Error('Unable to get user from Jwt');
  }

  getUserRole(): string | null {
    return this.getUserFromJwt().role;
  }

  isAdmin(): boolean {
    return this.getUserFromJwt().role == 'ADMIN';
  }

  isLoggedIn(): boolean {
    return !!this.getJwtToken();
  }
}
