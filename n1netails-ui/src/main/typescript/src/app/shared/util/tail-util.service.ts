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

  getBarChartLevelColor(level: string): string {
    switch (level?.toUpperCase()) {
      case 'INFO': return '#1E90FF';
      case 'SUCCESS': return 'green';
      case 'WARN': return '#FFA500';
      case 'ERROR': return '#FF4500';
      case 'CRITICAL': return '#FF0000';
      default: return '#8B0000';
    }
  }

  getStatusColor(status: string): string {
    switch (status?.toUpperCase()) {
      case 'NEW': return '#1E90FF';
      case 'IN_PROGRESS': return '#FFA500';
      case 'BLOCKED': return '#F00F21';
      case 'RESOLVED': return '#2ECC71';
      default: return 'orange';
    }
  }

  getPieChartStatusColor(status: string): string {
    return this.getStatusColor(status);
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