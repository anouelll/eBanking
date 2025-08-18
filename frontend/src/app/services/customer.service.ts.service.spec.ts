import { TestBed } from '@angular/core/testing';

import { CustomerServiceTsService } from './customer.service.ts.service';

describe('CustomerServiceTsService', () => {
  let service: CustomerServiceTsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustomerServiceTsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
