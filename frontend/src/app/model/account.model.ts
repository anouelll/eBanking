import { Customer } from "./customer.model";

export interface Account {
    type: string;
    id:            string;
    balance:              number;
    createdAt: string;
    customerDTO: Customer;
    status: string | null;
    interestRate?: number;           // Seulement pour SavingAccount
    overDraft?: number;
    currentPage: number;
    totalPages: number;
    pageSize: number;
    accountOperationDTOS: AccountOperation[];
}

export interface AccountOperation {
    id: number;
    operationDate: Date;
    amount: number;
    type: string;
    description: string;
    accountId: string;
}