import { Component, OnInit } from '@angular/core';
import { UserService, UserResponse, Page } from '../../service/user.service';
import { NzMessageService } from 'ng-zorro-antd/message';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  // styleUrls: ['./user-list.component.less'] // Add if specific styles are needed
})
export class UserListComponent implements OnInit {

  users: UserResponse[] = [];
  currentPage: number = 0; // API is 0-indexed
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 0;
  loadingUsers: boolean = true;

  constructor(
    private userService: UserService,
    private message: NzMessageService
  ) { }

  ngOnInit(): void {
    this.loadUsers(this.currentPage, this.pageSize);
  }

  loadUsers(page: number, size: number): void {
    this.loadingUsers = true;
    this.userService.getUsers(page, size).subscribe(
      (response: Page<UserResponse>) => {
        this.users = response.content;
        this.currentPage = response.number;
        this.pageSize = response.size;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.loadingUsers = false;
      },
      error => {
        this.loadingUsers = false;
        this.message.error('Failed to load users.');
        console.error('Error loading users:', error);
      }
    );
  }

  onPageIndexChange(pageIndex: number): void {
    // nz-pagination pageIndex is 1-based, API is 0-based
    this.loadUsers(pageIndex - 1, this.pageSize);
  }

  // Placeholder for future actions like edit, delete, etc.
  editUser(user: UserResponse): void {
    this.message.info(`Editing user: ${user.firstName} ${user.lastName}`);
    // Actual implementation would navigate to an edit page or open a modal
  }

  toggleUserStatus(user: UserResponse): void {
    this.message.info(`Toggling status for user: ${user.email}`);
    // Actual implementation would call a service method to update user status
  }
}
