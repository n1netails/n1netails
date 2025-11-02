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

@Component({
  selector: 'app-notification-manager',
  templateUrl: './notification-manager.component.html',
  styleUrls: ['./notification-manager.component.less'],
  imports: [
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
  token: N1neTokenResponse | null = null;
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
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    const tokenId = this.route.snapshot.paramMap.get('id');
    if (tokenId) {
      this.n1neTokenService.getTokenById(+tokenId).subscribe(token => {
        this.token = token;
        this.loadConfigurations(token.id);
      });
    }
  }

  loadConfigurations(tokenId: number): void {
    this.notificationService.getConfigurations(tokenId).subscribe(configs => {
      this.platforms.email.configs = configs.filter(c => c.platform === 'email');
      this.platforms.msteams.configs = configs.filter(c => c.platform === 'msteams');
      this.platforms.slack.configs = configs.filter(c => c.platform === 'slack');
      this.platforms.discord.configs = configs.filter(c => c.platform === 'discord');
      this.platforms.telegram.configs = configs.filter(c => c.platform === 'telegram');

      this.platforms.email.enabled = this.platforms.email.configs.length > 0;
      this.platforms.msteams.enabled = this.platforms.msteams.configs.length > 0;
      this.platforms.slack.enabled = this.platforms.slack.configs.length > 0;
      this.platforms.discord.enabled = this.platforms.discord.configs.length > 0;
      this.platforms.telegram.enabled = this.platforms.telegram.configs.length > 0;
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
        newConfig = { address: '' };
        break;
      case 'msteams':
      case 'discord':
        newConfig = { webhookUrl: '' };
        break;
      case 'slack':
        newConfig = { botToken: '', channel: '' };
        break;
      case 'telegram':
        newConfig = { botToken: '', chatId: '' };
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
            details: config
          });
        });
      }
    }

    this.notificationService.saveConfigurations(this.token.id, allConfigs).subscribe(() => {
      // Show success message
    });
  }
}
