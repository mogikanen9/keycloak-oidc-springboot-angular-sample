import { TestBed } from '@angular/core/testing';

import { MyAuthGuardGuard } from './my-auth-guard.guard';

describe('MyAuthGuardGuard', () => {
  let guard: MyAuthGuardGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(MyAuthGuardGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
