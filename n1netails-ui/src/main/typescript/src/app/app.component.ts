import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UiConfigService } from './shared/util/ui-config.service';
import { TutorialService } from './service/tutorial.service';
import { UserService } from './service/user.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.less'
})
export class AppComponent implements OnInit {

  title = 'N1netails';

  constructor(
    private uiConfigService: UiConfigService,
    private tutorialService: TutorialService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.uiConfigService.loadConfig().then(() => {
      console.log('API URL loaded:', this.uiConfigService.getApiUrl());
      // this.userService.getSelf().subscribe(user => {
      //   if (!user.tutorialCompleted) {
      //     this.tutorialService.startTutorial();
      //   }
      // });
    });
  }
}
