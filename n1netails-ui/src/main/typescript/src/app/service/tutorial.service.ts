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
      classes: 'custom-shepherd-theme',
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
        title: 'Welcome to N1netails!',
        text: [
          `
          <img class="tutorial-image" src="/tutorial/Fox_full_transparent.png"/>
          <p>N1netails is a platform for managing and monitoring your application\'s notifications. This tutorial will guide you through the key features.</p>
          `
          // 'N1netails is a platform for managing and monitoring your applications. This tutorial will guide you through the key features.'
        ],
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
        text: [
          `
          <img class="tutorial-image" src="/tutorial/fox-cog-transparent.png"/>
          <p>Welcome to the settings page take a look around. Here you can create new n1ne tokens, set notification preferences, and various other things. First, let\'s set up your notification preferences.</p>
          `
        ],
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
            classes: 'shepherd-button-primary',
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'notification-methods',
        title: 'Preferred Notification Methods',
        text: ['These preferences can be used to set what type of notifications you want to send and will override any n1ne token settings. Choose your preferred notification methods and click "Save Preferences".'],
        attachTo: {
          element: '[data-tutorial-id="notification-methods-form"]',
          on: 'top'
        },
        buttons: [
          {
            classes: 'shepherd-button-primary',
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      // TODO:: add steps here on creating Token Name and selecting Organization
      // steps to add token name
      { 
        id: 'add-token-name',
        title: 'Create N1ne Token Name',
        text: ['Provide a name for your new token. For example you can create a test token called `notification-test-token`.'],
        attachTo: {
          element: '[data-tutorial-id="add-token-name"]',
          on: 'bottom'
        },
        buttons: [
          {
            classes: 'shepherd-button-primary',
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      // steps to add organization
      { 
        id: 'add-token-orignaization',
        title: 'Set N1ne Token Orgianization',
        text: ['Select the organization this token is associated with. Select the n1netails organization if this is token is for you.'],
        attachTo: {
          element: '[data-tutorial-id="add-token-organization"]',
          on: 'bottom'
        },
        buttons: [
          {
            classes: 'shepherd-button-primary',
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'create-token',
        title: 'Create a N1ne Token',
        text: [
          `
          <img class="tutorial-image-coin" src="/tutorial/n1ne-token-transparent.png"/>
          <p>Now, let\'s create a N1ne token. Click the "+ Create Token" button. Make sure to save the token value that is produce in the response modal as it will not be saved and you will need it for your application\'s POST request to n1netails.</p>
          `
        ],
        attachTo: {
          element: '[data-tutorial-id="create-token-button"]',
          on: 'bottom'
        },
        buttons: [
          {
            classes: 'shepherd-button-primary',
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
            classes: 'shepherd-button-primary',
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'notification-manager',
        title: 'Notification Manager',
        text: ['This is the Notification Manager. Click “Manage” under Settings → Active Tokens to open the Token Notification Manager. Here, you can configure which platforms each token is allowed to send notifications to. (If you haven’t created a n1ne token yet, please do so before continuing.)'],
        attachTo: {
          element: '.ant-table-tbody > tr:first-child > td:nth-child(9) > button, [data-tutorial-id="notification-manager"]',
          on: 'right'
        },
        buttons: [
          {
            classes: 'shepherd-button-primary',
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'enable-notifications',
        title: 'Enable Notifications',
        text: ['You can enable token notifications for Email, Microsoft Teams, Slack, Discord, and Telegram in the Token Notification Manager.'],
        attachTo: {
          element: '[data-tutorial-id="enable-notifications"]',
          on: 'top'
        },
        buttons: [
          {
            classes: 'shepherd-button-primary',
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'how-to-create',
        title: 'How to Create...',
        text: ['If you want to learn more about how to create a notification channel, click on the "How to create..." links provided by the Token Notification Manager. You can also learn more about how to configure them in the <a href="https://n1netails.com" target="_blank">N1netails Doc</a>'],
        attachTo: {
          element: '[data-tutorial-id="how-to-create-link"]',
          on: 'bottom'
        },
        buttons: [
          {
            classes: 'shepherd-button-primary',
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'add-new-tail',
        title: 'Add New Tail',
        text: [
          `
          <img class="tutorial-image" src="/tutorial/Fox_tail5.png"/>
          <p>Now, let\'s add a new tail. Usually if you want to add a new tail you can click on the "+" button on the left sidenav. For now click Next.</p>
          `
        ],
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
            classes: 'shepherd-button-primary',
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
            classes: 'shepherd-button-primary',
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
            classes: 'shepherd-button-primary',
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'send-manual-notification',
        title: 'Send a Manual Notification',
        text: ['Send your first manual notification by pressing the "Add" button. Now you know how to send manual notifications. Don\'t forget you can use the N1ne tokens to configure where you want to send your notifications to.'],
        buttons: [
          {
            classes: 'shepherd-button-primary',
            text: 'Next',
            action: () => this.shepherdService.next()
          }
        ]
      },
      {
        id: 'learn-more',
        title: 'Learn More',
        text: [
          `
          <img class="tutorial-image" src="/tutorial/fox-reading-transparent.png"/>
          <p>If you want to learn how to send a POST request to N1neTails for your web services, check out the documentation at <a href="https://n1netails.com/docs/n1netails/n1netails-post-tail-alert" target="_blank">Post Tail Alerts to N1netails</a>. Click Finish to make sure this tutorial does not pop up again.</p>
          `
        ],
        buttons: [
          {
            classes: 'shepherd-button-primary',
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
