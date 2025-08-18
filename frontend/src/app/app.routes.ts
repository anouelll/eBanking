import { Routes } from '@angular/router';
import { CustomersComponent } from './customers/customers.component';
import { AccountsComponent } from './accounts/accounts.component';
import { NewCustomersComponent } from './new-customers/new-customers.component';

export const routes: Routes = [
    {path: 'accounts', component: AccountsComponent},
    {path: 'customers', component: CustomersComponent},
    {path: 'new-customer', component: NewCustomersComponent}
];

