import {Component, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {SearchService} from "../../services/search.service";
import {SearchResult} from "../../models/search";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [
    NgIf,
    RouterLinkActive,
    RouterLink,
    FormsModule,
    NgForOf
  ],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css'
})
export class NavComponent implements OnInit {
  isLoggedIn: boolean = false;
  searchQuery: string = '';
  searchResults: SearchResult[] = [];


  constructor(private readonly authService: AuthService,
              private readonly router: Router,
              private readonly searchService: SearchService,
              private readonly toastrService: ToastrService,) {
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


  onSearch() {
    if (this.searchQuery.trim().length < 2) {
      this.searchResults = [];
      return;
    }

   this.searchService.searchQuery(this.searchQuery).subscribe(
      results => {
        this.searchResults = results.data as SearchResult[]
      },
      error => this.toastrService.error(error.error.message)
    );
  }

  clearSearch(item: SearchResult) {
    this.searchQuery = '';
    this.searchResults = [];

    const route = item.type === 'task' ? '/task/' + item.id : '/project/' + item.id;
    this.router.navigate([route]);
  }
}
