import { TestBed } from '@angular/core/testing';

import { MyAuthServiceService } from './my-auth-service.service';

describe('MyAuthServiceService', () => {
  let service: MyAuthServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MyAuthServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
