import { Component } from '@angular/core';
import { SharedModule } from '../../../shared/shared.module';
import { ActivatedRoute, Router } from '@angular/router';
import { NzMessageService } from 'ng-zorro-antd/message';
import { TestService } from '../../services/test.service';

@Component({
  selector: 'app-take-test',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './take-test.component.html',
  styleUrl: './take-test.component.scss'
})
export class TakeTestComponent {
  questions: any[] = [];
  
  testId: any;
  
  constructor(private testService: TestService,
    private activateRoute: ActivatedRoute,
    private message: NzMessageService,
    private router: Router
  ){}

  ngOnInit(){
    this.activateRoute.paramMap.subscribe(params =>{
      this.testId = +params.get('id');

      this.testService.getTestQuestions(this.testId).subscribe(res =>{
        this.questions = res.questions;
        console.log(this.questions);
      })
    })
  }
}
