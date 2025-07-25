import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UiConfigService } from './shared/util/ui-config.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.less'
})
export class AppComponent implements OnInit {

  title = 'N1netails';

  constructor(private uiConfigService: UiConfigService) {}

  ngOnInit() {
    this.uiConfigService.loadConfig().then(() => {
      console.log('API URL loaded:', this.uiConfigService.getApiUrl());
    });
  }
}
