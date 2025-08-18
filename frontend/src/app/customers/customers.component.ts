import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { CustomerServiceTsService } from '../services/customer.service.ts.service';
import { Customer } from '../model/customer.model';
import { catchError, Observable, throwError } from 'rxjs';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-customers',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css',
  standalone: true
})
export class CustomersComponent implements OnInit {

  customers!: Observable<Array<Customer>>;
  errorMessage!: string;
  searchFormGroup!: FormGroup;
  constructor(private customerService: CustomerServiceTsService, private fb: FormBuilder) {}

  ngOnInit(): void {

    this.searchFormGroup = this.fb.group({
      keyword: this.fb.control(null)
    })

   this.handleSearchCustomers();
  }

  handleSearchCustomers(){
    let kw = this.searchFormGroup.value.keyword;
    this.customers = this.customerService.searchCustomers(kw).pipe(
      catchError(err => {
        this.errorMessage = err.message;
        return throwError(() => this.errorMessage);
      })
    );
  }

  handleDeleteCustomer(customer: Customer) {
  if (confirm("Are you sure you want to delete this customer?")) {
    this.customerService.deleteCustomer(customer).subscribe({
      next: () => {
        console.log('Customer deleted successfully');
        alert('Customer deleted successfully');
        this.handleSearchCustomers();
      },
      error: (err) => {
        console.error('Error deleting customer:', err);
      }
    });
  }
}
}