import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { N1neTokenService, N1neTokenResponse } from '../../service/n1ne-token.service';
import {
  NotificationPlatform,
  NotificationConfig,
  NotificationService
} from '../../service/notification.service';
import { FormsModule } from '@angular/forms';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzSwitchModule } from 'ng-zorro-antd/switch';
import { CommonModule } from '@angular/common';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { HeaderComponent } from '../../shared/template/header/header.component';
import { SidenavComponent } from '../../shared/template/sidenav/sidenav.component';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { RouterModule } from '@angular/router';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzMessageService } from 'ng-zorro-antd/message';
import { UiConfigService } from '../../shared/util/ui-config.service';

@Component({
  selector: 'app-notification-manager',
  templateUrl: './notification-manager.component.html',
  styleUrls: ['./notification-manager.component.less'],
  imports: [
    NzLayoutModule,
    NzGridModule,
    FormsModule,
    NzCardModule,
    NzSwitchModule,
    CommonModule,
    NzLayoutModule,
    HeaderComponent,
    SidenavComponent,
    NzInputModule,
    NzButtonModule,
    RouterModule,
    NzIconModule
  ]
})
export class NotificationManagerComponent implements OnInit {

  EMAIL: string = 'email';
  MICROSOFT_TEAMS: string = 'msteams';
  SLACK: string = 'slack';
  DISCORD: string = 'discord';
  TELEGRAM: string = 'telegram';

  token: N1neTokenResponse = {
    id: 0,
    userId: 0,
    organizationId: 0,
    n1Token: '',
    name: '',
    lastUsedAt: '', // ISO 8601 format
    revoked: true
  };
  platforms: { [key: string]: { enabled: boolean; configs: any[] } } = {
    email: { enabled: false, configs: [] },
    msteams: { enabled: false, configs: [] },
    slack: { enabled: false, configs: [] },
    discord: { enabled: false, configs: [] },
    telegram: { enabled: false, configs: [] }
  };

  constructor(
    private route: ActivatedRoute,
    private n1neTokenService: N1neTokenService,
    private notificationService: NotificationService,
    private msg: NzMessageService,
    public UiConfigService: UiConfigService,
  ) {}

  ngOnInit(): void {
    const tokenId = this.route.snapshot.paramMap.get('id');
    if (tokenId) {
      this.n1neTokenService.getTokenById(+tokenId).subscribe(token => {
        this.token = token;
        console.log('TOKEN', this.token);
        this.loadConfigurations(token.id);
      });
    }
  }

  loadConfigurations(tokenId: number): void {
    this.notificationService.getConfigurations(tokenId).subscribe(configs => {
      console.log('NOTIFICATION CONFIGS', configs);
      this.platforms[this.EMAIL].configs = configs.filter(c => c.platform === this.EMAIL);
      this.platforms[this.MICROSOFT_TEAMS].configs = configs.filter(c => c.platform === this.MICROSOFT_TEAMS);
      this.platforms[this.SLACK].configs = configs.filter(c => c.platform === this.SLACK);
      this.platforms[this.DISCORD].configs = configs.filter(c => c.platform === this.DISCORD);
      this.platforms[this.TELEGRAM].configs = configs.filter(c => c.platform === this.TELEGRAM);

      this.platforms[this.EMAIL].enabled = this.platforms[this.EMAIL].configs.length > 0;
      this.platforms[this.MICROSOFT_TEAMS].enabled = this.platforms[this.MICROSOFT_TEAMS].configs.length > 0;
      this.platforms[this.SLACK].enabled = this.platforms[this.SLACK].configs.length > 0;
      this.platforms[this.DISCORD].enabled = this.platforms[this.DISCORD].configs.length > 0;
      this.platforms[this.TELEGRAM].enabled = this.platforms[this.TELEGRAM].configs.length > 0;
    });
  }

  onPlatformToggle(platform: string): void {
    if (this.platforms[platform].enabled && this.platforms[platform].configs.length === 0) {
      this.addConfig(platform);
    }
  }

  addConfig(platform: string): void {
    let newConfig = {};
    switch (platform) {
      case 'email':
        newConfig = { details: { address: '' }};
        break;
      case 'msteams':
      case 'discord':
        newConfig = { details: { webhookUrl: '' }};
        break;
      case 'slack':
        newConfig = { 
          details: {
            botToken: '', channel: '' 
          }
        };
        break;
      case 'telegram':
        newConfig = {
          details: {
            botToken: '', chatId: '' 
          }
        };
        break;
    }
    this.platforms[platform].configs.push(newConfig);
  }

  removeConfig(platform: string, index: number): void {
    this.platforms[platform].configs.splice(index, 1);
    if (this.platforms[platform].configs.length === 0) {
      this.platforms[platform].enabled = false;
    }
  }

  saveConfigurations(): void {
    if (!this.token) return;

    const allConfigs: NotificationConfig[] = [];
    for (const platformName in this.platforms) {
      if (this.platforms[platformName].enabled) {
        this.platforms[platformName].configs.forEach(config => {
          allConfigs.push({
            tokenId: this.token.id,
            platform: platformName as NotificationPlatform,
            details: config.details
          });
        });
      }
    }

    this.notificationService.saveConfigurations(this.token.id, allConfigs).subscribe({
      next: () => {
        this.msg.success('Token notification configurations saved.');
      },
      error: (err) => {
        this.msg.error('There was an error saving token notification configurations. Please try again.');
      }
    });
  }

  getMsTeamsWebhookUrl(config: any) {
    if (config.details.webhookUrl) {
      return config.details.webhookUrl;
    }
    else return undefined;
  }
}
