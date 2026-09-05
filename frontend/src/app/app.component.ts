import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Metric {
  title: string;
  value: number;
  unit: string;
  sla: number;
  trend: number;
  status: string;
}

interface CaseItem {
  caseNumber: string;
  metricKey: string;
  vendor: string;
  status: string;
}

interface AlertItem {
  severity: string;
  eventType: string;
  status: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  currentDate = '';
  baseUrl = 'http://localhost:8081';

  constructor(private http: HttpClient) {
    this.http.get<{ currentDate: string }>(`${this.baseUrl}/api/simulation/status`).subscribe(result => this.currentDate = result.currentDate);
  }

  advanceDay(): void {
    this.http.post<{ newDate: string }>(`${this.baseUrl}/api/simulation/advance-day`, {}).subscribe(result => this.currentDate = result.newDate);
  }
}
