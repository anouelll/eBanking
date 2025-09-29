import { Routes } from '@angular/router';
import { CustomersComponent } from './customers/customers.component';
import { AccountsComponent } from './accounts/accounts.component';
import { NewCustomersComponent } from './new-customers/new-customers.component';
import { LoginComponent } from './login/login.component';
import { AdminTemplateComponent } from './admin-template/admin-template.component';
import { authenticationGuard } from './guards/authentication.guard';
import { authorizationGuard } from './guards/authorization.guard';
import { NotAuthorizedComponent } from './not-authorized/not-authorized.component';
import { MyAccountsComponent } from './my-accounts/my-accounts.component';
import { RegisterComponent } from './register/register.component';

export const routes: Routes = [
    {path: 'register', component : RegisterComponent},
    {path : 'login', component: LoginComponent},
    {path: 'notAuthorized', component: NotAuthorizedComponent},

    {path: 'my-accounts', component: MyAccountsComponent, 
     },
    {path : 'admin', component: AdminTemplateComponent, canActivate: [authenticationGuard, authorizationGuard], data: {roles: ['ADMIN']}, children: [
        {path: 'accounts', component: AccountsComponent},
        {path: 'customers', component: CustomersComponent},
        {path: 'new-customer', component: NewCustomersComponent, canActivate: [authorizationGuard], data: {roles: ['ADMIN']}},
        {path: '', redirectTo: 'customers', pathMatch: 'full'}
    ]},
    {path: '', redirectTo: '/register', pathMatch: 'full'},
    {path: '**', redirectTo: '/login'}
];

