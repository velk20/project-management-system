import {Component, inject} from '@angular/core';
import {User, UserDetails} from "../../models/user";
import Swal from "sweetalert2";
import {UserService} from "../../services/user.service";
import {NgClass, NgForOf} from "@angular/common";
import {Router} from "@angular/router";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-administration',
  standalone: true,
  imports: [
    NgForOf,
    NgClass
  ],
  templateUrl: './administration.component.html',
  styleUrl: './administration.component.css'
})
export class AdministrationComponent {
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly Math = Math;

  loggedUserId: number = 0;
  users: UserDetails[] = [];
  paginatedUsers: UserDetails[] = [];
  availableRoles: string[] = ['USER', 'ADMIN'];

  currentPage = 0;
  pageSize = 10;

  ngOnInit() {
    this.userService.getAllUsers().subscribe(res => {
      this.users = res.data as UserDetails[];
      this.updatePagination();

      this.loggedUserId = this.authService.getUserFromJwt().id;
    })
  }

  updatePagination() {
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
    this.paginatedUsers = this.users.slice(start, end);
  }

  nextPage() {
    if ((this.currentPage + 1) * this.pageSize < this.users.length) {
      this.currentPage++;
      this.updatePagination();
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.updatePagination();
    }
  }

  async changeRole(user: UserDetails) {
    const { value: selectedRole } = await Swal.fire({
      title: `Change Role for ${user.username}`,
      input: 'select',
      inputOptions: this.availableRoles.reduce((acc, role) => {
        acc[role] = role;
        return acc;
      }, {} as Record<string, string>),
      inputValue: user.role,
      showCancelButton: true,
      customClass: {
        popup: 'border rounded shadow p-3'
      }
    });

    if (selectedRole) {
      user.role = selectedRole;
      this.updateUserDetails(user);
      Swal.fire('Updated!', `${user.username} is now ${selectedRole}`, 'success');
    }
  }

  private updateUserDetails(user: UserDetails) {
    console.log(user)
    this.userService.updateProfile(user.id, user).subscribe(
      res => {
        const updatedUser = res.data as UserDetails;
        const index = this.users.findIndex(user => user.id === updatedUser.id);
        if (index !== -1) {
          this.users[index] = updatedUser;
        }
      }, error => {
        Swal.fire('Error', error.error.message, 'error');
      }
    )
  }

  async disableUser(user: UserDetails) {
    const result = await Swal.fire({
      title: `Disable ${user.username}?`,
      text: "This user will not be able to login.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, disable it!',
      customClass: {
        popup: 'border rounded shadow p-3'
      }
    });

    if (result.isConfirmed) {
      user.active = false;
      this.updateUserDetails(user);
      Swal.fire('Disabled!', `${user.username} has been disabled.`, 'success');
    }
  }

  async enableUser(user: UserDetails) {
    const result = await Swal.fire({
      title: `Enable ${user.username}?`,
      text: "This user will be enabled.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, enable it!',
      customClass: {
        popup: 'border rounded shadow p-3'
      }
    });

    if (result.isConfirmed) {
      user.active = true;
      this.updateUserDetails(user);
      Swal.fire('Enabled!', `${user.username} has been enabled.`, 'success');
    }
  }

  async invalidateAllUsers() {
    const result = await Swal.fire({
      title: 'Are you sure?',
      text: 'This will invalidate ALL users access to the system!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, disable them!',
      customClass: {
        popup: 'border rounded shadow p-3'
      }
    });

    if (result.isConfirmed) {
      this.userService.invalidateUsersTokens().subscribe(res=>{
        let message = res.message;
        Swal.fire('Success', message, 'success')
        this.router.navigate(['/login']);
      }, error => {
        Swal.fire('Error', error.error.message, 'error');
      })
    }
  }

  isUserYou(user: UserDetails) {
    return user.id == this.loggedUserId

  }
}
