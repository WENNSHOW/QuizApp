import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

const BASIC_URL = "https://quizapp-18pr.onrender.com/";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  register(data): Observable<any>{
    return this.http.post(BASIC_URL + "api/auth/sign-up", data);
  }

  login(loginRequest: any): Observable<any>{
    return this.http.post(BASIC_URL + "api/auth/login", loginRequest);
  }
}
