import { Component } from '@angular/core';
import { SharedModule } from '../../../shared/shared.module';
import { ActivatedRoute, Router } from '@angular/router';
import { NzMessageService } from 'ng-zorro-antd/message';
import { TestService } from '../../services/test.service';
import { UserStorageService } from '../../../auth/services/user-storage.service';

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

  selectedAnswers: {[key:number] : string} = {};

  timeRemaining: number = 0;
  
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

        this.timeRemaining = res.testDTO.time || 0;
      })
    })
  }

  getFormattedTime(): string{
    const minutes = Math.floor(this.timeRemaining / 60);
    const seconds = this.timeRemaining % 60;
    return `${minutes}:${seconds < 10 ? '0': ''}${seconds}`;
  }

  onAnswerChange(questionId:number, selectedOption:string){
    this.selectedAnswers[questionId] = selectedOption;
    console.log(this.selectedAnswers);
  }

  submitAnswers(){
    const answerList = Object.keys(this.selectedAnswers).map(questionId =>{
      return {
        questionId: +questionId,
        selectedOption: this.selectedAnswers[questionId]
      }
    })

    const data = {
      testId: this.testId,
      userId: UserStorageService.getUserId(),
      responses: answerList
    }

    this.testService.submitTest(data).subscribe(res=>{
      this.message
      .success(
        `Test Submitted Successfully`,
        { nzDuration: 5000 }
      );
      this.router.navigateByUrl("/user/view-test-results");
    }, error=>{
      this.message
      .error(
        `${error.error}`,
        { nzDuration: 5000 }
      );
    })
  }
}
