import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Customer } from '../model/customer.model';
import { CustomerServiceTsService } from '../services/customer.service.ts.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-new-customers',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './new-customers.component.html',
  styleUrl: './new-customers.component.css',
  standalone: true
})
export class NewCustomersComponent {

  constructor(private fb : FormBuilder, private customerService: CustomerServiceTsService) { }

  newCustomerForm! : FormGroup;


  ngOnInit(): void {
   this.newCustomerForm = this.fb.group({
    name : this.fb.control(null, [Validators.required, Validators.minLength(3)]),
    email: this.fb.control(null, [Validators.required, Validators.email])
   })
  }

handleSaveNewCustomer(){
 const newCustomer: Customer = this.newCustomerForm.value;
 this.customerService.saveCustomer(newCustomer).subscribe({
   next: (savedCustomer: Customer) => {
     console.log('Customer saved successfully:', savedCustomer);
     alert('Customer saved successfully!');
    //  this.newCustomerForm.reset(); // Reset the form after successful save
   },
   error: (error : Error) => {
     console.error('Error saving customer:', error);
   }
 });
}
}
