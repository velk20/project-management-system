import { Component } from '@angular/core';
import {JwtTokenResponse, LoginUser} from "../../models/auth";
import {FormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";
import {ToastrService} from "ngx-toastr";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private toastrService: ToastrService,
  ) {
  }


  loginForm = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]],
  });

  onSubmit() {
    let loginUser: LoginUser = {
      username: this.loginForm.value.username || '',
      password: this.loginForm.value.password || '',
    }

    this.authService.loginUser(loginUser).subscribe(
      res => {
        this.authService.login(res.data as JwtTokenResponse);
        this.router.navigate(['/dashboard']);
        this.toastrService.success(res.message, 'Success');
      }
      ,(err) => {
        this.loginForm.get("password")?.reset();
      })
  }

}
