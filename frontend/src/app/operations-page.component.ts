import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({ selector: 'app-operations-page', templateUrl: './operations-page.component.html', styleUrls: ['./operations-page.component.scss'] })
export class OperationsPageComponent implements OnInit {
  baseUrl = 'http://localhost:8081';
  page = 'dashboard'; caseId = ''; detail: any; timeline: any[] = []; data: any[] = []; metrics: any[] = []; loading = true; error = '';
  constructor(private route: ActivatedRoute, private http: HttpClient) {}
  ngOnInit(): void { this.route.data.subscribe(data => { this.page = data['page']; this.caseId = this.route.snapshot.paramMap.get('id') || ''; this.load(); }); }
  load(): void {
    this.loading = true; this.error = '';
    if (this.caseId) {
      this.http.get<any>(`${this.baseUrl}/api/cases/${this.caseId}`).subscribe({ next: result => { this.detail = result; this.http.get<any[]>(`${this.baseUrl}/api/cases/${this.caseId}/timeline`).subscribe(timeline => { this.timeline = timeline; this.loading = false; }); }, error: () => { this.loading = false; this.error = 'The case detail could not be loaded.'; } });
      return;
    }
    const endpoint: Record<string, string> = { alerts: `${this.baseUrl}/api/alerts`, reports: `${this.baseUrl}/api/reports`, cases: `${this.baseUrl}/api/cases`, vendors: `${this.baseUrl}/api/vendors`, employees: `${this.baseUrl}/api/employees` };
    const request = this.page === 'dashboard' ? this.http.get<any[]>(`${this.baseUrl}/api/metrics`) : this.http.get<any[]>(endpoint[this.page]);
    request.subscribe({ next: result => { if (this.page === 'dashboard') this.metrics = result; else this.data = result; this.loading = false; }, error: () => { this.loading = false; this.error = 'The backend data service is unavailable or returned an error.'; } });
  }
  title(): string { return this.page === 'alert-detail' ? 'Alert detail' : this.page === 'report-detail' ? 'Report detail' : this.page.charAt(0).toUpperCase() + this.page.slice(1); }
  value(item: any): string { return item.value === undefined ? (item.status || item.rows || '-') : `${item.value} ${item.unit || ''}`; }
}
