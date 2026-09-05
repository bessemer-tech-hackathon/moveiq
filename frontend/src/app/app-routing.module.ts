import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OperationsPageComponent } from './operations-page.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: OperationsPageComponent, data: { page: 'dashboard' } },
  { path: 'alerts', component: OperationsPageComponent, data: { page: 'alerts' } },
  { path: 'alerts/:id', component: OperationsPageComponent, data: { page: 'alert-detail' } },
  { path: 'reports', component: OperationsPageComponent, data: { page: 'reports' } },
  { path: 'reports/:id', component: OperationsPageComponent, data: { page: 'report-detail' } },
  { path: 'cases', component: OperationsPageComponent, data: { page: 'cases' } },
  { path: 'vendors', component: OperationsPageComponent, data: { page: 'vendors' } },
  { path: 'employees', component: OperationsPageComponent, data: { page: 'employees' } }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
