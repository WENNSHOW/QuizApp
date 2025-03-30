import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NzMessageService } from 'ng-zorro-antd/message';
import { Chart, ChartConfiguration, ChartData } from 'chart.js/auto';
import { SharedModule } from '../../../shared/shared.module';

@Component({
  selector: 'app-test-results-chart',
  templateUrl: './test-results-chart.component.html',
  standalone: true,
  imports: [SharedModule],
  styleUrls: ['./test-results-chart.component.scss']
})
export class TestResultsChartComponent implements OnInit {
  testName: string = '';
  totalQuestions: number = 0;
  correctAnswers: number = 0;
  incorrectAnswers: number = 0;
  correctPercentage: number = 0;
  incorrectPercentage: number = 0;

  constructor(
    private router: Router,
    private message: NzMessageService
  ) {}

  ngOnInit(): void {
    // Извлекаем данные из sessionStorage
    const dataString = sessionStorage.getItem('testResult');
    const data = dataString ? JSON.parse(dataString) : null;
    
    if (data) {
      this.testName = data.testName;
      this.totalQuestions = data.totalQuestions;
      this.correctAnswers = data.correctAnswers;
      this.incorrectAnswers = this.totalQuestions - this.correctAnswers;
      this.correctPercentage = (this.correctAnswers / this.totalQuestions) * 100;
      this.incorrectPercentage = 100 - this.correctPercentage;
      this.renderChart();
    } else {
      this.message.error('Нет данных теста для отображения диаграммы');
      this.router.navigate(['/user/view-test-results']);
    }
  }

  renderChart(): void {
    // Получаем Canvas по ID
    const canvas = document.getElementById('resultsChart') as HTMLCanvasElement;
    // Формируем данные для диаграммы
    const data: ChartData<'doughnut'> = {
      labels: ['Правильные', 'Неправильные'],
      datasets: [{
        data: [this.correctPercentage, this.incorrectPercentage],
        backgroundColor: ['#28a745', '#dc3545']
      }]
    };
    // Конфигурация диаграммы
    const config: ChartConfiguration<'doughnut'> = {
      type: 'doughnut',
      data: data,
      options: {
        plugins: {
          legend: { display: true }
        }
      }
    };

    // Создаём диаграмму
    new Chart(canvas, config);
  }

  next(): void {
    this.router.navigate(['/user/view-test-results']);
  }
}
