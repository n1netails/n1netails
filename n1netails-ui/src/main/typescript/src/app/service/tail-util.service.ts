import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TailUtilService {

  private typeColorMap: { [type: string]: string } = {};

  getLevelColor(level: string): string {
    switch (level?.toUpperCase()) {
      case 'INFO': return 'blue';
      case 'SUCCESS': return 'green';
      case 'WARN': return 'orange';
      case 'ERROR': return 'red';
      case 'CRITICAL': return 'volcano';
      default: return 'default';
    }
  }

  getStatusColor(status: string): string {
    switch (status?.toUpperCase()) {
      case 'NEW': return 'green';
      case 'IN_PROGRESS': return 'gold';
      case 'BLOCKED': return 'red';
      case 'RESOLVED': return 'blue';
      default: return 'orange';
    }
  }

  getTypeColor(type: string): string {
    if (!type) return 'default';
    const key = type.toLowerCase();

    const zorroColors = [
      'geekblue', 'purple', 'magenta', 'red', 'volcano', 'orange', 'gold',
      'lime', 'green', 'cyan', 'blue'
    ];

    let hash = 0;
    for (let i = 0; i < key.length; i++) {
      hash = key.charCodeAt(i) + ((hash << 5) - hash);
    }
    const color = zorroColors[Math.abs(hash) % zorroColors.length];
    this.typeColorMap[key] = color;
    return color;
  }

  getKudaAvatar(level: string): string {
    switch (level?.toUpperCase()) {
      case 'INFO': return 'kuda1_info.jpg';
      case 'SUCCESS': return 'kuda1_success.jpg';
      case 'WARN': return 'kuda1_warn.jpg';
      case 'ERROR': return 'kuda1_error.jpg';
      case 'CRITICAL': return 'kuda1_critical.jpg';
      default: return 'kuda1.jpg';
    }
  }
}