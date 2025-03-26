import { Component } from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-notfound',
  standalone: true,
  imports: [
    RouterLink
  ],
  templateUrl: './notfound.component.html',
  styleUrl: './notfound.component.css'
})
export class NotfoundComponent {
  isLoggedIn: boolean = false;

  constructor(private authService: AuthService,
              private router: Router,) {
  }

  ngOnInit(): void {
    this.authService.getJwtTokenAsObservable().subscribe((jwtToken) => {
      this.isLoggedIn = !!jwtToken;
    });
  }
}
