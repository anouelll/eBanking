// my-accounts.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { CustomerServiceTsService } from '../services/customer.service.ts.service';
import { AccountsService } from '../services/accounts.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-my-accounts',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './my-accounts.component.html',
  styleUrl: './my-accounts.component.css'
})
export class MyAccountsComponent implements OnInit {

  accountsWithOperations: any[] = [];
  operationsFormGroup!: FormGroup;

  constructor(
    private customerService: CustomerServiceTsService,
    private authService: AuthService,
    private accountsService: AccountsService,
    private fb: FormBuilder
  ) { }

  ngOnInit(): void {
    this.initOperationsForm();
    this.loadAccountsWithOperations();
  }

  initOperationsForm(): void {
    this.operationsFormGroup = this.fb.group({
      operationType: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      description: ['', Validators.required],
      accountDestination: [''] // Required seulement pour TRANSFER
    });
  }

  loadAccountsWithOperations(): void {
    this.customerService.getCustomerAccounts(this.authService.userId).subscribe({
      next: (accounts) => {
        const accountDetails$ = accounts.map(account => 
          this.accountsService.getAccountById(account.id, 0, 5).pipe(
            map(detailedAccount => ({
              ...account,
              ...detailedAccount,
              accountId: account.id
            }))
          )
        );
        
        forkJoin(accountDetails$).subscribe({
          next: (combinedAccounts) => {
            this.accountsWithOperations = combinedAccounts;
          },
          error: (error) => {
            console.error('❌ Error loading accounts:', error);
          }
        });
      }
    });
  }

  handleAccountOperation(accountId: string): void {
    if (this.operationsFormGroup.valid) {
      const formValue = this.operationsFormGroup.value;
      
      console.log('💰 Processing operation:', {
        accountId: accountId,
        operation: formValue
      });

      // Validation pour TRANSFER
      if (formValue.operationType === 'TRANSFER' && !formValue.accountDestination) {
        alert('Destination account is required for transfers');
        return;
      }

      switch (formValue.operationType) {
        case 'DEBIT':
          this.accountsService.debit(accountId, formValue.amount, formValue.description).subscribe({
            next: () => {
              console.log('✅ Debit successful');
              this.loadAccountsWithOperations(); // Recharger les données
              this.operationsFormGroup.reset();
            },
            error: (error) => console.error('❌ Debit failed:', error)
          });
          break;

        case 'CREDIT':
          this.accountsService.credit(accountId, formValue.amount, formValue.description).subscribe({
            next: () => {
              console.log('✅ Credit successful');
              this.loadAccountsWithOperations();
              this.operationsFormGroup.reset();
            },
            error: (error) => console.error('❌ Credit failed:', error)
          });
          break;

        case 'TRANSFER':
          this.accountsService.transfer(accountId, formValue.accountDestination, formValue.amount, formValue.description).subscribe({
            next: () => {
              console.log('✅ Transfer successful');
              this.loadAccountsWithOperations();
              this.operationsFormGroup.reset();
            },
            error: (error) => console.error('❌ Transfer failed:', error)
          });
          break;
      }
    } else {
      console.log('❌ Form is invalid');
    }
  }
}