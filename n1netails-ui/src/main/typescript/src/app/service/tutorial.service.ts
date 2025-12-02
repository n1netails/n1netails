import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ShepherdService } from 'angular-shepherd';
import { UserService } from './user.service';
import { NzModalService } from 'ng-zorro-antd/modal';
import { AddTailModalComponent } from '../shared/components/add-tail-modal/add-tail-modal.component';

@Injectable({
  providedIn: 'root'
})
export class TutorialService {

  constructor(
    private shepherdService: ShepherdService,
    private userService: UserService,
    private router: Router,
    private modalService: NzModalService
  ) { }

  startTutorial() {
    this.shepherdService.defaultStepOptions = {
      classes: 'custom-class-name-1 custom-class-name-2',
      scrollTo: true,
      cancelIcon: {
        enabled: true
      }
    };

    this.shepherdService.modal = true;
    this.shepherdService.confirmCancel = false;

    this.shepherdService.addSteps([
      {
        id: 'welcome',
        title: 'Welcome to N1neTails!',
        text: ['N1neTails is a platform for managing and monitoring your applications. This tutorial will guide you through the key features.'],
        buttons: [
          {
            classes: 'shepherd-button-secondary',
            text: 'Skip',
            action: () => {
              this.completeTutorial();
              this.shepherdService.complete();
            }
          },
          {
            classes: 'shepherd-button-primary',
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'settings',
        title: 'Settings',
        text: ['First, let\'s set up your notification preferences.'],
        attachTo: {
          element: '.ant-menu-item a[href="/settings"]',
          on: 'right'
        },
        beforeShowPromise: () => {
          return new Promise((resolve) => {
            this.router.navigate(['/settings']).then(() => {
              setTimeout(() => {
                resolve(null);
              }, 500);
            });
          });
        },
        buttons: [
          {
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'notification-methods',
        title: 'Preferred Notification Methods',
        text: ['Choose your preferred notification methods and click "Save Preferences".'],
        attachTo: {
          element: '[data-tutorial-id="notification-methods-form"]',
          on: 'top'
        },
        buttons: [
          {
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'create-token',
        title: 'Create a N1ne Token',
        text: ['Now, let\'s create a N1ne token. Click the "+ Create Token" button.'],
        attachTo: {
          element: '[data-tutorial-id="create-token-button"]',
          on: 'bottom'
        },
        buttons: [
          {
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'manage-tokens',
        title: 'Manage N1ne Tokens',
        text: ['You can manage your N1ne tokens here. You can manage, revoke, or delete them.'],
        attachTo: {
          element: '[data-tutorial-id="manage-tokens-table"]',
          on: 'top'
        },
        buttons: [
          {
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'notification-manager',
        title: 'Notification Manager',
        text: ['Next, let\'s go to the Notification Manager. Click the "Manage" button for a token.'],
        attachTo: {
          element: '.ant-table-tbody > tr:first-child > td:nth-child(9) > button',
          on: 'right'
        },
        buttons: [
          {
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
        // buttons: [
        //   {
        //     text: 'Waiting for click...',
        //     disabled: true
        //   }
        // ],
        // beforeShowPromise: () => {
        //   return new Promise(resolve => {
        //     // Wait for Angular to finish rendering
        //     setTimeout(() => {
        //       const button = document.querySelector(
        //         '.ant-table-tbody > tr:first-child > td:nth-child(9) > button'
        //       );

        //       if (button) {
        //         const handler = () => {
        //           button.removeEventListener('click', handler);
        //           resolve(null); // Shepherd will move to next step automatically
        //         };

        //         button.addEventListener('click', handler);
        //       }
        //     }, 300);
        //   });
        // }
      },
      {
        id: 'enable-notifications',
        title: 'Enable Notifications',
        text: ['You can enable notifications for Email, Microsoft Teams, Slack, Discord, and Telegram here.'],
        attachTo: {
          element: '[data-tutorial-id="enable-notifications"]',
          on: 'top'
        },
        buttons: [
          {
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'how-to-create',
        title: 'How to Create...',
        text: ['If you want to learn more about how to create a notification channel, click on the "How to create..." links.'],
        attachTo: {
          element: '[data-tutorial-id="how-to-create-link"]',
          on: 'bottom'
        },
        buttons: [
          {
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'add-new-tail',
        title: 'Add New Tail',
        text: ['Now, let\'s add a new tail. Click the "+" button on the left sidenav.'],
        attachTo: {
          element: 'a.add-tail-button',
          on: 'right'
        },
        beforeShowPromise: () => {
          return new Promise((resolve) => {
            this.router.navigate(['/dashboard']).then(() => {
              setTimeout(() => {
                resolve(null);
              }, 500);
            });
          });
        },
        buttons: [
          {
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'add-new-tail-modal',
        title: 'Add New Tail',
        text: ['Select an organization and a N1ne token.'],
        attachTo: {
          element: '[data-tutorial-id="add-new-tail-modal-form"]',
          on: 'top'
        },
        beforeShowPromise: () => {
          return new Promise((resolve) => {
            this.modalService.create({
              nzContent: AddTailModalComponent
            });
            setTimeout(() => {
              resolve(null);
            }, 500);
          });
        },
        buttons: [
          {
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'add-new-tail-inputs',
        title: 'Add New Tail',
        text: ['Fill in the rest of the inputs for your new tail.'],
        attachTo: {
          element: '[data-tutorial-id="add-new-tail-modal-form"]',
          on: 'top'
        },
        buttons: [
          {
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'send-manual-alert',
        title: 'Send a Manual Alert',
        text: ['Now you know how to send manual alerts. Don\'t forget you can use the N1ne tokens to configure where you want to send your notifications to.'],
        buttons: [
          {
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'learn-more',
        title: 'Learn More',
        text: ['If you want to learn how to send a POST request to N1neTails for your web services, check out our documentation at <a href="https://n1netails.com" target="_blank">N1netails Doc</a>.'],
        buttons: [
          {
            text: 'Finish',
            action: () => {
              this.completeTutorial();
              this.shepherdService.complete();
            }
          }
        ]
      }
    ]);

    this.shepherdService.start();
  }

  completeTutorial() {
    this.userService.completeTutorial().subscribe();
  }
}
