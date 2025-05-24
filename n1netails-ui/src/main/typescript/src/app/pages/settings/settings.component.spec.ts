import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core'; // To ignore unknown elements like app-header

import { SettingsComponent } from './settings.component';
import { N1neTokenService, N1neTokenResponse, CreateTokenRequest as ActualCreateTokenRequest } from '../../service/n1ne-token.service';
import { AuthenticationService } from '../../service/authentication.service';
import { TailLevelService, TailLevelResponse } from '../../service/tail-level.service';
import { TailStatusService, TailStatusResponse } from '../../service/tail-status.service';
import { TailTypeService, TailTypeResponse } from '../../service/tail-type.service';
import { User } from '../../model/user';

import { of, throwError } from 'rxjs';

// NG-ZORRO Modules (import specific ones used or use NO_ERRORS_SCHEMA if many/complex)
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzCheckboxModule } from 'ng-zorro-antd/checkbox';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzMessageService } from 'ng-zorro-antd/message';


const mockUser: User = {
  id: 123,
  userId: 'testUser',
  firstName: 'Test',
  lastName: 'User',
  username: 'testuser',
  email: 'test@example.com',
  password: '', // Not typically stored or used like this
  lastLoginDate: new Date(),
  lastLoginDateDisplay: new Date(),
  joinDate: new Date(),
  profileImageUrl: '',
  active: true,
  notLocked: true,
  role: 'ROLE_USER',
  authorities: []
};

const mockTokens: N1neTokenResponse[] = [
  { id: 1, name: 'Token 1', token: 'uuid1', userId: 123, organizationId: 1, createdAt: new Date().toISOString(), expiresAt: new Date(Date.now() + 86400000).toISOString(), lastUsedAt: null, revoked: false },
  { id: 2, name: 'Token 2', token: 'uuid2', userId: 123, organizationId: 1, createdAt: new Date().toISOString(), expiresAt: null, lastUsedAt: new Date().toISOString(), revoked: true },
];

describe('SettingsComponent', () => {
  let component: SettingsComponent;
  let fixture: ComponentFixture<SettingsComponent>;
  let mockN1neTokenService: jasmine.SpyObj<N1neTokenService>;
  let mockAuthService: jasmine.SpyObj<AuthenticationService>;
  let mockTailLevelService: jasmine.SpyObj<TailLevelService>;
  let mockTailStatusService: jasmine.SpyObj<TailStatusService>;
  let mockTailTypeService: jasmine.SpyObj<TailTypeService>;
  let mockNzMessageService: jasmine.SpyObj<NzMessageService>;

  beforeEach(async () => {
    mockN1neTokenService = jasmine.createSpyObj('N1neTokenService', ['getAllTokens', 'createToken', 'revokeToken', 'enableToken', 'deleteToken']);
    mockAuthService = jasmine.createSpyObj('AuthenticationService', ['getUserFromLocalCache']);
    mockTailLevelService = jasmine.createSpyObj('TailLevelService', ['getTailLevels', 'createTailLevel', 'deleteTailLevel']);
    mockTailStatusService = jasmine.createSpyObj('TailStatusService', ['getTailStatusList', 'createTailStatus', 'deleteTailStatus']);
    mockTailTypeService = jasmine.createSpyObj('TailTypeService', ['getTailTypes', 'createTailType', 'deleteTailType']);
    mockNzMessageService = jasmine.createSpyObj('NzMessageService', ['success', 'error', 'warning']);


    // Default success returns
    mockN1neTokenService.getAllTokens.and.returnValue(of([...mockTokens]));
    mockN1neTokenService.createToken.and.callFake((req: ActualCreateTokenRequest) => of({ id: 3, ...req, token: 'newUUID', createdAt: new Date().toISOString(), lastUsedAt: null, revoked: false }));
    mockN1neTokenService.revokeToken.and.returnValue(of(undefined));
    mockN1neTokenService.enableToken.and.returnValue(of(undefined));
    mockN1neTokenService.deleteToken.and.returnValue(of(undefined));

    mockAuthService.getUserFromLocalCache.and.returnValue(mockUser);
    mockTailLevelService.getTailLevels.and.returnValue(of([] as TailLevelResponse[]));
    mockTailStatusService.getTailStatusList.and.returnValue(of([] as TailStatusResponse[]));
    mockTailTypeService.getTailTypes.and.returnValue(of([] as TailTypeResponse[]));
    mockTailLevelService.createTailLevel.and.returnValue(of({} as TailLevelResponse));
    mockTailStatusService.createTailStatus.and.returnValue(of({} as TailStatusResponse));
    mockTailTypeService.createTailType.and.returnValue(of({} as TailTypeResponse));


    await TestBed.configureTestingModule({
      imports: [
        FormsModule,
        CommonModule,
        NoopAnimationsModule,
        HttpClientTestingModule, // N1neTokenService might still be trying to inject HttpClient if not mocked properly at component level
        NzCardModule,
        NzFormModule,
        NzTableModule,
        NzInputModule,
        NzButtonModule,
        NzDividerModule,
        NzCheckboxModule,
        NzLayoutModule,
        SettingsComponent // Import the standalone component
      ],
      providers: [
        { provide: N1neTokenService, useValue: mockN1neTokenService },
        { provide: AuthenticationService, useValue: mockAuthService },
        { provide: TailLevelService, useValue: mockTailLevelService },
        { provide: TailStatusService, useValue: mockTailStatusService },
        { provide: TailTypeService, useValue: mockTailTypeService },
        { provide: NzMessageService, useValue: mockNzMessageService } // If SettingsComponent uses NzMessageService
      ],
      schemas: [NO_ERRORS_SCHEMA] // To ignore app-header, app-sidenav if they are not imported/declared
    }).compileComponents();

    fixture = TestBed.createComponent(SettingsComponent);
    component = fixture.componentInstance;
    // fixture.detectChanges(); // Initial ngOnInit call
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Token Loading', () => {
    it('should call loadTokens on ngOnInit and display tokens', fakeAsync(() => {
      fixture.detectChanges(); // Triggers ngOnInit
      tick(); // Allow async operations like service calls to complete
      fixture.detectChanges(); // Update view with loaded data

      expect(mockN1neTokenService.getAllTokens).toHaveBeenCalled();
      expect(component.tokens.length).toBe(2);
      expect(component.tokens[0].name).toBe('Token 1');
      const tableRows = fixture.nativeElement.querySelectorAll('tbody tr');
      // Each token + potentially a "no data" row if table is empty, but here we expect 2 tokens.
      expect(tableRows.length).toBeGreaterThanOrEqual(2); 
    }));

    it('should display error message if loadTokens fails', fakeAsync(() => {
      mockN1neTokenService.getAllTokens.and.returnValue(throwError(() => new Error('Failed to load')));
      fixture.detectChanges(); // ngOnInit
      tick();
      fixture.detectChanges(); // Update view with error

      expect(component.errorMessage).toBe('Failed to load tokens.');
      const errorDiv = fixture.nativeElement.querySelector('.error-message');
      expect(errorDiv.textContent).toContain('Failed to load tokens.');
    }));
  });

  describe('Create Token', () => {
    beforeEach(fakeAsync(() => {
        fixture.detectChanges(); // Initial load
        tick();
        fixture.detectChanges();
    }));

    it('should call n1neTokenService.createToken with correct parameters and refresh list', fakeAsync(() => {
      component.newTokenRequestForm = { name: 'New Test Token', expiresAt: '2025-12-31T10:00' };
      fixture.detectChanges(); // Update form bindings

      const createButton = fixture.nativeElement.querySelector('form button[type="submit"]');
      createButton.click();
      tick(); // For service call and subsequent loadTokens
      fixture.detectChanges();

      const expectedRequest: ActualCreateTokenRequest = {
        name: 'New Test Token',
        userId: mockUser.id,
        expiresAt: new Date('2025-12-31T10:00').toISOString()
      };
      expect(mockN1neTokenService.createToken).toHaveBeenCalledWith(jasmine.objectContaining(expectedRequest));
      expect(mockN1neTokenService.getAllTokens).toHaveBeenCalledTimes(2); // Initial load + after create
      expect(component.newTokenRequestForm.name).toBeUndefined(); // Form reset
    }));

    it('should display error if createToken fails', fakeAsync(() => {
      mockN1neTokenService.createToken.and.returnValue(throwError(() => new Error('Create failed')));
      component.newTokenRequestForm = { name: 'Fail Token' };
      fixture.detectChanges();

      const createButton = fixture.nativeElement.querySelector('form button[type="submit"]');
      createButton.click();
      tick();
      fixture.detectChanges();

      expect(component.errorMessage).toBe('Failed to create token.');
    }));

    it('should require token name for creation', fakeAsync(() => {
        component.newTokenRequestForm = { name: '' }; // No name
        fixture.detectChanges();
  
        const createButton = fixture.nativeElement.querySelector('form button[type="submit"]');
        // Form validation should disable the button or component logic should prevent submission
        expect(createButton.disabled).toBeTrue(); // Assuming ngForm validation disables it
        
        // If button not disabled by form, test component logic
        if(!createButton.disabled) {
            createButton.click();
            tick();
            fixture.detectChanges();
            expect(mockN1neTokenService.createToken).not.toHaveBeenCalled();
            expect(component.errorMessage).toBe('Token name is required.');
        }
      }));
  });

  describe('Token Actions (Revoke, Enable, Delete)', () => {
    beforeEach(fakeAsync(() => {
        fixture.detectChanges(); // Initial load
        tick();
        fixture.detectChanges();
    }));

    it('should call revokeToken and refresh list', fakeAsync(() => {
      const tokenToRevoke = mockTokens[0]; // Assuming this token is not revoked
      const revokeButton = fixture.nativeElement.querySelectorAll('tbody tr')[0].querySelectorAll('button')[0]; // First token, first button (Revoke)
      
      expect(revokeButton.textContent).toContain('Revoke');
      revokeButton.click();
      tick();
      fixture.detectChanges();

      expect(mockN1neTokenService.revokeToken).toHaveBeenCalledWith(tokenToRevoke.id);
      expect(mockN1neTokenService.getAllTokens).toHaveBeenCalledTimes(2); // Initial + after revoke
    }));

    it('should call enableToken and refresh list for a revoked token', fakeAsync(() => {
        const tokenToEnable = mockTokens[1]; // Assuming this token is revoked
        const enableButton = fixture.nativeElement.querySelectorAll('tbody tr')[1].querySelectorAll('button')[0]; // Second token, first button (Enable)

        expect(enableButton.textContent).toContain('Enable');
        enableButton.click();
        tick();
        fixture.detectChanges();
  
        expect(mockN1neTokenService.enableToken).toHaveBeenCalledWith(tokenToEnable.id);
        expect(mockN1neTokenService.getAllTokens).toHaveBeenCalledTimes(2);
      }));

    it('should call deleteToken and refresh list', fakeAsync(() => {
      const tokenToDelete = mockTokens[0];
      // Assuming delete is the last button (Revoke/Enable, Delete)
      const deleteButton = fixture.nativeElement.querySelectorAll('tbody tr')[0].querySelectorAll('button')[1];
      
      deleteButton.click();
      tick();
      fixture.detectChanges();

      expect(mockN1neTokenService.deleteToken).toHaveBeenCalledWith(tokenToDelete.id);
      expect(mockN1neTokenService.getAllTokens).toHaveBeenCalledTimes(2);
    }));
  });

  describe('UI Rendering and Button States', () => {
    it('should correctly render token data', fakeAsync(() => {
      fixture.detectChanges();tick();fixture.detectChanges();
      const firstTokenRow = fixture.nativeElement.querySelectorAll('tbody tr')[0];
      expect(firstTokenRow.cells[0].textContent).toContain(mockTokens[0].name);
      expect(firstTokenRow.cells[1].textContent).toContain(mockTokens[0].id.toString());
      // Token value is in an input
      const tokenValueInput = firstTokenRow.cells[2].querySelector('input');
      expect(tokenValueInput.value).toBe(mockTokens[0].token);
      expect(firstTokenRow.cells[6].textContent).toContain('Active'); // mockTokens[0] is not revoked
    }));

    it('should display "Revoke" button for active tokens and "Enable" for revoked ones', fakeAsync(() => {
        fixture.detectChanges();tick();fixture.detectChanges();
        const firstTokenButtons = fixture.nativeElement.querySelectorAll('tbody tr')[0].querySelectorAll('button');
        const secondTokenButtons = fixture.nativeElement.querySelectorAll('tbody tr')[1].querySelectorAll('button');
  
        expect(firstTokenButtons[0].textContent).toContain('Revoke'); // mockTokens[0] is active
        expect(secondTokenButtons[0].textContent).toContain('Enable'); // mockTokens[1] is revoked
      }));

    it('should disable action buttons when isLoading', fakeAsync(() => {
        fixture.detectChanges(); // ngOnInit
        tick();
        component.isLoading = true;
        fixture.detectChanges();
  
        const createButton = fixture.nativeElement.querySelector('form button[type="submit"]');
        expect(createButton.disabled).toBeTrue();
  
        const revokeButton = fixture.nativeElement.querySelector('tbody tr button'); // First action button
        if (revokeButton) { // If tokens exist
            expect(revokeButton.disabled).toBeTrue();
        }
      }));
  });

  describe('Alert Type Management', () => {
    const mockTailTypesInitial: TailTypeResponse[] = [
      { id: 1, name: 'TypeA', description: 'Description A' },
      { id: 2, name: 'TypeB', description: 'Description B' },
      { id: 3, name: 'TypeC', description: 'Description C' },
    ];

    const mockTailTypesAfterRemove: TailTypeResponse[] = [
      { id: 1, name: 'TypeA', description: 'Description A' },
      { id: 3, name: 'TypeC', description: 'Description C' },
    ];

    beforeEach(() => {
      // Reset relevant service mocks before each test in this suite
      mockTailTypeService.getTailTypes.and.returnValue(of([...mockTailTypesInitial]));
      mockTailTypeService.deleteTailType.and.returnValue(of(undefined)); // Default success for delete
    });

    it('should load tail types and populate alertTypeOptions on ngOnInit', fakeAsync(() => {
      fixture.detectChanges(); // Calls ngOnInit
      tick(); // Complete observables

      expect(mockTailTypeService.getTailTypes).toHaveBeenCalled();
      expect(component.tailTypes.length).toBe(3);
      expect(component.tailTypes[0].name).toBe('TypeA');
      expect(component.alertTypeOptions.length).toBe(3);
      expect(component.alertTypeOptions[0]).toEqual({ label: 'TypeA', value: 'TypeA' });
      expect(component.alertTypeOptions[1]).toEqual({ label: 'TypeB', value: 'TypeB' });
    }));

    it('should update alertTypeOptions when updateAlertTypeOptions is called', fakeAsync(() => {
      const customMockTypes: TailTypeResponse[] = [{ id: 10, name: 'CustomType', description: 'Custom Desc' }];
      mockTailTypeService.getTailTypes.and.returnValue(of(customMockTypes));

      component.updateAlertTypeOptions();
      tick(); // Complete observable

      expect(mockTailTypeService.getTailTypes).toHaveBeenCalled();
      expect(component.alertTypeOptions.length).toBe(1);
      expect(component.alertTypeOptions[0]).toEqual({ label: 'CustomType', value: 'CustomType' });
    }));

    it('should log preferredAlertTypes on onSavePreferredTypes', () => {
      spyOn(console, 'log');
      component.preferredAlertTypes = ['TypeA', 'TypeC'];
      component.onSavePreferredTypes();

      expect(console.log).toHaveBeenCalledWith('Saving preferred tail types:', ['TypeA', 'TypeC']);
    });

    describe('removeAlertType', () => {
      beforeEach(fakeAsync(() => {
        // Initial setup for removeAlertType tests
        component.tailTypes = [...mockTailTypesInitial];
        component.preferredAlertTypes = ['TypeA', 'TypeB']; // TypeB will be removed
         // updateAlertTypeOptions called in constructor, then ngOnInit, then here
        mockTailTypeService.getTailTypes.and.returnValue(of([...mockTailTypesInitial]));
        fixture.detectChanges(); // ngOnInit, populates alertTypeOptions initially
        tick();
        // For the specific call within removeAlertType after deletion
        mockTailTypeService.getTailTypes.and.returnValue(of([...mockTailTypesAfterRemove]));
      }));

      it('should call deleteTailType, update tailTypes, preferredAlertTypes, and alertTypeOptions', fakeAsync(() => {
        const typeNameToRemove = 'TypeB';
        const typeIdToRemove = mockTailTypesInitial.find(t => t.name === typeNameToRemove)!.id;

        component.removeAlertType(typeNameToRemove);
        tick(); // Complete delete and subsequent getTailTypes

        expect(mockTailTypeService.deleteTailType).toHaveBeenCalledWith(typeIdToRemove);
        
        // Check tailTypes updated in component
        expect(component.tailTypes.length).toBe(2);
        expect(component.tailTypes.find(t => t.name === typeNameToRemove)).toBeUndefined();
        
        // Check preferredAlertTypes updated
        expect(component.preferredAlertTypes.length).toBe(1);
        expect(component.preferredAlertTypes.includes(typeNameToRemove)).toBeFalse();
        expect(component.preferredAlertTypes[0]).toBe('TypeA');

        // Check alertTypeOptions updated (based on mockTailTypesAfterRemove)
        expect(component.alertTypeOptions.length).toBe(2);
        expect(component.alertTypeOptions.find(opt => opt.value === typeNameToRemove)).toBeUndefined();
        expect(component.alertTypeOptions[0]).toEqual({ label: 'TypeA', value: 'TypeA' });
        expect(component.alertTypeOptions[1]).toEqual({ label: 'TypeC', value: 'TypeC' });
      }));

      it('should not change preferredAlertTypes if removed type is not preferred', fakeAsync(() => {
        const typeNameToRemove = 'TypeC'; // Not in preferredAlertTypes ['TypeA', 'TypeB']
        const typeIdToRemove = mockTailTypesInitial.find(t => t.name === typeNameToRemove)!.id;
        
        // Update mock for getTailTypes to reflect TypeC removal for alertTypeOptions update
        const typesAfterRemovingC = mockTailTypesInitial.filter(t => t.name !== typeNameToRemove);
        mockTailTypeService.getTailTypes.and.returnValue(of(typesAfterRemovingC));

        component.removeAlertType(typeNameToRemove);
        tick();

        expect(mockTailTypeService.deleteTailType).toHaveBeenCalledWith(typeIdToRemove);
        expect(component.preferredAlertTypes.length).toBe(2); // Unchanged
        expect(component.preferredAlertTypes).toEqual(['TypeA', 'TypeB']);
        expect(component.alertTypeOptions.find(opt => opt.value === typeNameToRemove)).toBeUndefined();
      }));

      it('should handle removal of non-existent type gracefully', fakeAsync(() => {
        const typeNameToRemove = 'TypeNonExistent';
        spyOn(console, 'warn'); // Spy on console.warn for the message

        component.removeAlertType(typeNameToRemove);
        tick();

        expect(mockTailTypeService.deleteTailType).not.toHaveBeenCalled();
        expect(console.warn).toHaveBeenCalledWith('TailType not found or id missing for type:', typeNameToRemove);
        expect(component.tailTypes.length).toBe(3); // Unchanged from initial
        expect(component.preferredAlertTypes.length).toBe(2); // Unchanged
      }));
    });
  });
});
