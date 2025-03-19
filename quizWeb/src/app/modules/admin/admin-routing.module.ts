import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { CreateTestComponent } from './components/create-test/create-test.component';
import { AddQuestionInTestComponent } from './components/add-question-in-test/add-question-in-test.component';
import { AuthGuard } from '../auth/guards/auth.guard';
import { AdminGuard } from '../auth/guards/admin.guard';

const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard, AdminGuard]},
  { path: 'create-test', component: CreateTestComponent, canActivate: [AuthGuard, AdminGuard]},
  { path: 'add-question/:id', component: AddQuestionInTestComponent, canActivate: [AuthGuard, AdminGuard]},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
