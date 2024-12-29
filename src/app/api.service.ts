import { Injectable } from '@angular/core';
import { Enviornment } from './env.dev';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  baseUrl = Enviornment.baseUrl
  constructor(private http: HttpClient) { }

  register(registrationReq: any) :Observable<any>{
    return this.http.post<any>(this.baseUrl + `register`, registrationReq);
  }



}
