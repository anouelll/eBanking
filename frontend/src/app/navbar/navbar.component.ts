import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule} from '@angular/router';
import { AuthService } from '../services/auth.service';


@Component({
  selector: 'app-navbar',
  imports: [CommonModule,RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css', 
  standalone: true
})
export class NavbarComponent implements OnInit {

  constructor(public authService : AuthService, private route : Router) { }

  ngOnInit(): void {
  }

  handleLogout(): void {
  this.authService.logout();
  this.route.navigateByUrl('/login');
  }
}
