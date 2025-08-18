import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Customer } from '../model/customer.model';
import { Observable } from 'rxjs';
import { environment} from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CustomerServiceTsService {

  constructor(private http: HttpClient) { 
  }

  public getCustomers(): Observable<Array<Customer>> {
    return this.http.get<Array<Customer>>(environment.backEndHost + "/customers");
  }

  public searchCustomers(keyword: string): Observable<Array<Customer>> {
    return this.http.get<Array<Customer>>(environment.backEndHost + "/customers/search?keyword=" + keyword);
  }

  public saveCustomer(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(environment.backEndHost + "/customers", customer);
  }

  public deleteCustomer(customer: Customer): Observable<void> {
    return this.http.delete<void>(environment.backEndHost + "/customers/" + customer.id);
  }

}
