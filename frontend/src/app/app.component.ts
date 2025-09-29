import { Component, OnInit } from '@angular/core';
import { RouterModule} from '@angular/router';
import { NavbarComponent } from './navbar/navbar.component';
import { AuthService } from './services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterModule,CommonModule,NavbarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  standalone: true
})
export class AppComponent implements OnInit {
  title = 'digital-banking';

  constructor(public authService : AuthService){}

  ngOnInit() {
   this.authService.loadJwtTokenFromLocalStorage();
  }
}
