import { Component } from '@angular/core';
import { SharedModule } from '../../../shared/shared.module';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-create-test',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './create-test.component.html',
  styleUrl: './create-test.component.scss'
})
export class CreateTestComponent {
  constructor(private fb: FormBuilder,
    private devicesService: AdminService,
    private notification: NzNotificationService,
    private router: Router,
  ){}

  testForm!: FormGroup;

  ngOnInit(){
    this.testForm = this.fb.group({
      title: [null, Validators.required],
      description: [null, [Validators.required]],
      time: [null, [Validators.required]],
    })
  }

  submitForm(){
    if(this.testForm.valid){
      this.devicesService.createTest(this.testForm.value).subscribe(res=>{
        this.notification
          .success(
            `SUCCESS`,
            `Test Created Successfully.`,
            { nzDuration: 5000}
          );
          this.router.navigateByUrl("/admin/dashboard");
      }, error=>{
        this.notification
        .error(
          `ERROR`,
          `${error.error}`,
          { nzDuration: 5000}
        );
      })
    }
  }
}
