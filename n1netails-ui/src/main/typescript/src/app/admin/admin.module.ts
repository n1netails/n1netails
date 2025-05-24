import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AdminRoutingModule } from './admin-routing.module';
import { UserListComponent } from './user-list/user-list.component';

// NG-ZORRO Imports
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzBadgeModule } from 'ng-zorro-antd/badge';
import { NzMessageModule } from 'ng-zorro-antd/message';
import { NzPaginationModule } from 'ng-zorro-antd/pagination';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzIconModule } from 'ng-zorro-antd/icon'; // For icons if needed
import { NzButtonModule } from 'ng-zorro-antd/button'; // For action buttons if needed
import { NzSpaceModule } from 'ng-zorro-antd/space'; // For layout if needed


@NgModule({
  declarations: [
    UserListComponent
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    NzTableModule,
    NzBadgeModule,
    NzMessageModule,
    NzPaginationModule,
    NzCardModule,
    NzIconModule,
    NzButtonModule,
    NzSpaceModule
  ]
})
export class AdminModule { }
