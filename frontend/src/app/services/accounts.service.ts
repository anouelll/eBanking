import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Account } from '../model/account.model';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AccountsService {
  constructor(private httpClient: HttpClient) {}

  public getAccountById(
    accountId: string,
    page: number,
    size: number
  ): Observable<Account> {
    return this.httpClient.get<Account>(
      environment.backEndHost +
        `/accounts/${accountId}/pageOperations?page=${page}&size=${size}`
    );
  }

  public debit(
    accountId: string,
    amount: number,
    description: string
  ): Observable<void> {
    return this.httpClient.post<void>(
      `${environment.backEndHost}/accounts/debit`,
      { accountId, amount, description }
    );
  }

  public credit(
    accountId: string,
    amount: number,
    description: string
  ): Observable<void> {
    return this.httpClient.post<void>(
      `${environment.backEndHost}/accounts/credit`,
      { accountId, amount, description }
    );
  }

  public transfer(
    accountId: string,
    amount: number,
    description: string,
    accountDestination: string
  ): Observable<void> {
    return this.httpClient.post<void>(
      `${environment.backEndHost}/accounts/transfer`,
      { accountId, accountDestination, amount, description }
    );
  }
}
