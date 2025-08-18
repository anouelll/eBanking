import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Account } from '../model/account.model';
import { AccountsService } from '../services/accounts.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-accounts',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.css',
  standalone: true
})
export class AccountsComponent {

page: number = 1;
size: number = 5;
  constructor(private fb: FormBuilder, private accountsService: AccountsService){}
  pageOperations: number = 0;
  accountFormGroup!: FormGroup;
  accountObservable!: Observable<Account>;
  operationsFormGroup!: FormGroup;

  ngOnInit(): void {
    this.accountFormGroup = this.fb.group({
      accountId: this.fb.control(null)
    })

    this.operationsFormGroup = this.fb.group({
operationType: this.fb.control(null),
amount: this.fb.control(0),
description: this.fb.control(null),
accountDestination: this.fb.control(null)
    })


  }

  handleSearchAccount(){
   const accountId = this.accountFormGroup.value.accountId;
   this.accountObservable = this.accountsService.getAccountById(accountId, this.pageOperations, this.size)
  }

  goToPage(page: number) {
    this.pageOperations = page;
    this.handleSearchAccount();
  }

  handleAccountOperation() {
    let accountId: string = this.accountFormGroup.value.accountId;
    let operationType: string = this.operationsFormGroup.value.operationType;
    let amount: number = this.operationsFormGroup.value.amount;
    let description: string = this.operationsFormGroup.value.description;
    let accountDestination: string = this.operationsFormGroup.value.accountDestination;
   if (operationType === 'TRANSFER') {
     
     this.accountsService.transfer(accountId, amount, description, accountDestination).subscribe({
      next: () => {
        alert("Succes Transfer")
        this.handleSearchAccount();
      },
      error: (err)=> {
        console.log(err);
      }
     });
      
   } else if (operationType === 'DEBIT') {
     this.accountsService.debit(accountId, amount, description).subscribe({
      next: () => {
        alert("Succes Debit")
        this.handleSearchAccount();
      },
      error: (err)=> {
        console.log(err);
      }
     });
   } else if (operationType === 'CREDIT') {
     this.accountsService.credit(accountId, amount, description).subscribe({
       next: () => {
         alert("Succes Credit")
         this.handleSearchAccount();
       },
       error: (err)=> {
         console.log(err);
       }
     });
   }
  }
}
