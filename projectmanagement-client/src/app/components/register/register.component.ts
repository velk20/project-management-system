import { Component } from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {NgIf} from "@angular/common";
import {RegisterUser} from "../../models/auth";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private toastrService: ToastrService,
  ) {
  }

  registerForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', Validators.required],
    username: ['', Validators.required],
    password: ['', Validators.required],
    repassword: ['', [Validators.required]]
  });


  onSubmit() {
    let password = this.registerForm.value.password;
    let repassword = this.registerForm.value.repassword;

    if (password !== repassword) {
      this.toastrService.error(`Passwords do not match.`);
      this.registerForm.get('password')?.reset();
      this.registerForm.get('repassword')?.reset();
      return;
    }

    let user: RegisterUser = {
      firstName: this.registerForm.value.firstName || '',
      lastName: this.registerForm.value.lastName || '',
      email: this.registerForm.value.email || '',
      username: this.registerForm.value.username || '',
      role: 'user',
      password: this.registerForm.value.password || '',
    };

    this.authService.registerUser(user).subscribe(
      (res) => {
        this.router.navigate(['/login']);
        this.toastrService.success(res.message, 'Success');
      },
      (err) => {
        let errors: string[] = err.error.errors;

        errors.forEach(error => {
          this.toastrService.error(error);
        });
      }
    );

  }
}
