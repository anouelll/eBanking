import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule,CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  standalone: true
})
export class LoginComponent implements OnInit {

 

  loginFormGroup !: FormGroup;

  constructor(private fb : FormBuilder, private authService : AuthService, private router : Router) {}
  ngOnInit(): void {
    this.loginFormGroup = this.fb.group({
      username: this.fb.control(null),
      password: this.fb.control(null)
    });
  }

  handleLogin(){
    let username = this.loginFormGroup.value.username;
    let pwd = this.loginFormGroup.value.password;

    this.authService.login(username, pwd).subscribe({
      next: (response) => {
        this.authService.loadProfile(response);
        const roles = this.authService.roles;
        this.router.navigateByUrl("/my-accounts");
        
        if (roles.includes('ROLE_ADMIN') || roles.includes('ADMIN')) {
          this.router.navigateByUrl("/admin/customers");
        } else if (roles.includes('ROLE_USER') || roles.includes('USER')) {
          this.router.navigateByUrl("/my-accounts");
        } else {
          this.router.navigateByUrl("/notAuthorized");
        }
      },
      error: (error) => {
        console.error('Login failed', error);
      }
    });
  }

  }
  

