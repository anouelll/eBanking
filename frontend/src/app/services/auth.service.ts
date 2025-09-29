import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { jwtDecode } from 'jwt-decode';
import { windowWhen } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
 

  constructor(private http: HttpClient, private router : Router) { }

   isAuthenticated : boolean = false;
  username : any;
  roles : any;
  accessToken : any;
  userId : any;
  public login(username : string, password : string){

    const body = {
      username: username,
      password: password
    };

      // Ajoutez les headers HTTP
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
   
    return this.http.post(environment.backEndHost + '/auth/login', body, { headers: headers });
  }

  public loadProfile(response: any) {
    this.isAuthenticated = true;
    this.accessToken = response["access_token"];
    window.localStorage.setItem("access_token", this.accessToken);

    let decodeJwt: any = jwtDecode(this.accessToken);
    this.username = decodeJwt.sub;
    this.roles = decodeJwt.scope;
    this.userId = decodeJwt.userId;
    console.log(this.userId)
  }

  public logout(){
    this.isAuthenticated = false;
    this.username = null;
    this.roles = null;
    this.accessToken = null;
    this.userId = null;
    window.localStorage.removeItem("access_token");
    this.router.navigateByUrl('/login');
    
  }

   public loadJwtTokenFromLocalStorage() {
  
    let token = window.localStorage.getItem("access_token");
    if(token){
      this.loadProfile({ "access_token": token });
      // this.router.navigateByUrl('/admin/customers');
    }
  }

  public register(username: string, email: string, password: string){
    const body = {
      username: username,
      email: email,
      password: password
    };

    // Ajoutez les headers HTTP
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post(environment.backEndHost + '/auth/register', body, { headers: headers });
  }
}