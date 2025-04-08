import { Component } from '@angular/core';
import {FormsModule} from "@angular/forms";
import {Router} from "@angular/router";
import {UserService} from "../../services/user.service";
import {ToastrService} from "ngx-toastr";
import {AuthService} from "../../services/auth.service";
import {ChangeUserPassword} from "../../models/user";

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css'
})
export class ChangePasswordComponent {
  constructor(private router: Router,
              private userService: UserService,
              private toastr: ToastrService,
              private authService: AuthService) {
  }
  passwordData = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  onChangePassword() {
    const changePassword: ChangeUserPassword = this.passwordData;
    this.userService.changePassword(changePassword).subscribe(res=>{
      this.toastr.success(res.message, 'Success');
      this.toastr.info('Please login with your username and new password', 'Info');
      this.logoutUser();
    }, error=>{
      if (error.status === 400 && error.error.errors) {
        let errors: string[] = error.error.errors;
        for (const err of errors) {
          this.toastr.error(err, 'Error');
        }
      }
      else{
        this.toastr.error(error.error.message, 'Error');
      }

      this.resetForm();
    })

  }

  onCancelChangePassword() {
    this.resetForm();
    this.router.navigate(['/profile'])
  }

  private resetForm() {
    // Clear the form and reset the object
    this.passwordData = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    };
  }

  private logoutUser() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
