import { Injectable } from '@angular/core';
import { ShepherdService } from 'angular-shepherd';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root'
})
export class TutorialService {

  constructor(
    private shepherdService: ShepherdService,
    private userService: UserService
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
            action: this.shepherdService.next
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
        buttons: [
          {
            text: 'Next',
            action: this.shepherdService.next
          }
        ]
      },
      {
        id: 'notification-methods',
        title: 'Preferred Notification Methods',
        text: ['Choose your preferred notification methods and click "Save Preferences".'],
        attachTo: {
          element: 'app-settings form',
          on: 'top'
        },
        buttons: [
          {
            text: 'Next',
            action: this.shepherdService.next
          }
        ]
      },
      {
        id: 'create-token',
        title: 'Create a N1ne Token',
        text: ['Now, let\'s create a N1ne token. Click the "+ Create Token" button.'],
        attachTo: {
          element: 'app-settings button.ant-btn-primary',
          on: 'bottom'
        },
        buttons: [
          {
            text: 'Next',
            action: this.shepherdService.next
          }
        ]
      },
      {
        id: 'manage-tokens',
        title: 'Manage N1ne Tokens',
        text: ['You can manage your N1ne tokens here. You can edit, delete, or copy them.'],
        attachTo: {
          element: 'app-settings nz-table',
          on: 'top'
        },
        buttons: [
          {
            text: 'Next',
            action: this.shepherdService.next
          }
        ]
      },
      {
        id: 'notification-manager',
        title: 'Notification Manager',
        text: ['Next, let\'s go to the Notification Manager.'],
        attachTo: {
          element: '.ant-menu-item a[href="/notification-manager"]',
          on: 'right'
        },
        buttons: [
          {
            text: 'Next',
            action: this.shepherdService.next
          }
        ]
      },
      {
        id: 'enable-notifications',
        title: 'Enable Notifications',
        text: ['You can enable notifications for Email, Microsoft Teams, Slack, Discord, and Telegram here.'],
        attachTo: {
          element: 'app-notification-manager',
          on: 'top'
        },
        buttons: [
          {
            text: 'Next',
            action: this.shepherdService.next
          }
        ]
      },
      {
        id: 'how-to-create',
        title: 'How to Create...',
        text: ['If you want to learn more about how to create a notification channel, click on the "How to create..." links.'],
        attachTo: {
          element: 'app-notification-manager .ant-card-body a',
          on: 'bottom'
        },
        buttons: [
          {
            text: 'Next',
            action: this.shepherdService.next
          }
        ]
      },
      {
        id: 'add-new-tail',
        title: 'Add New Tail',
        text: ['Now, let\'s add a new tail. Click the "+" button on the left sidenav.'],
        attachTo: {
          element: '.ant-menu-item a[href="/tails/new"]',
          on: 'right'
        },
        buttons: [
          {
            text: 'Next',
            action: this.shepherdService.next
          }
        ]
      },
      {
        id: 'add-new-tail-modal',
        title: 'Add New Tail',
        text: ['Select an organization and a N1ne token.'],
        attachTo: {
          element: 'app-add-tail-modal form',
          on: 'top'
        },
        buttons: [
          {
            text: 'Next',
            action: this.shepherdService.next
          }
        ]
      },
      {
        id: 'add-new-tail-inputs',
        title: 'Add New Tail',
        text: ['Fill in the rest of the inputs for your new tail.'],
        attachTo: {
          element: 'app-add-tail-modal form',
          on: 'top'
        },
        buttons: [
          {
            text: 'Next',
            action: this.shepherdService.next
          }
        ]
      },
      {
        id: 'send-manual-alert',
        title: 'Send a Manual Alert',
        text: ['Once you\'ve created a tail, you can send a manual alert from the tail\'s page.'],
        attachTo: {
          element: 'app-tail-details button.ant-btn-primary',
          on: 'bottom'
        },
        buttons: [
          {
            text: 'Next',
            action: this.shepherdService.next
          }
        ]
      },
      {
        id: 'learn-more',
        title: 'Learn More',
        text: ['If you want to learn how to send a POST request to N1neTails, check out our documentation at https://n1netails.com.'],
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
