import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserListComponent } from './user-list/user-list.component';

const routes: Routes = [
  {
    path: '', // Default route for the admin module
    redirectTo: 'users', // Redirect to users list by default
    pathMatch: 'full'
  },
  {
    path: 'users',
    component: UserListComponent
  }
  // Add other admin routes here if needed in the future
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
