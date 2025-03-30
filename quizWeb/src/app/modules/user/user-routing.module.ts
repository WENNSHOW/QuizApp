import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AuthGuard } from '../auth/guards/auth.guard';
import { TakeTestComponent } from './components/take-test/take-test.component';
import { ViewMyTestResultsComponent } from './components/view-my-test-results/view-my-test-results.component';
import { TestResultsChartComponent } from './components/test-results-chart/test-results-chart.component';

const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard]},
  { path: 'view-test-results', component: ViewMyTestResultsComponent, canActivate: [AuthGuard]},
  { path: 'test-results-chart', component: TestResultsChartComponent, canActivate: [AuthGuard]},
  { path: 'take-test/:id', component: TakeTestComponent, canActivate: [AuthGuard]},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
