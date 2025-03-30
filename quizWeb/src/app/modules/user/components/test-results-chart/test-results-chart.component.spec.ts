import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestResultsChartComponent } from './test-results-chart.component';

describe('TestResultsChartComponent', () => {
  let component: TestResultsChartComponent;
  let fixture: ComponentFixture<TestResultsChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestResultsChartComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TestResultsChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
