import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  registerFormGroup!: FormGroup;

  
  constructor(private fb: FormBuilder, private authService : AuthService, private router : Router) { }

  ngOnInit(): void {
    this.registerFormGroup = this.fb.group({
      username: this.fb.control(null ,[Validators.required, Validators.minLength(4)]),
      email: this.fb.control(null ,[Validators.required, Validators.email]),
      password: this.fb.control(null ,[Validators.required, Validators.minLength(6)]),
      role: this.fb.control(null ,[Validators.required])
    });
  }

  handleRegister(){
    if(this.registerFormGroup.valid){
      const { username, email, password } = this.registerFormGroup.value;
      this.authService.register(username, email, password).subscribe({
        next: (response) => {
          console.log('User registered successfully:', response);
          this.router.navigateByUrl('/login');
        },
        error: (error) => {
          console.error('Error registering user:', error);
        }
      });
    }
  }

  navigateToLogin(){
    this.router.navigateByUrl('/login');
  }
}
