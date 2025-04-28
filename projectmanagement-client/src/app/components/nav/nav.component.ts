import {Component, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [
    NgIf,
    RouterLinkActive,
    RouterLink
  ],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css'
})
export class NavComponent implements OnInit {
  isLoggedIn: boolean = false;

  constructor(private authService: AuthService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.authService.getJwtTokenAsObservable().subscribe((jwtToken) => {
      this.isLoggedIn = !!jwtToken;
    });
  }

  getLoggedUserUsername(){
    return this.authService.getUserFromJwt().username;
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() == 'ADMIN';
  }

  onLogout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }


}
