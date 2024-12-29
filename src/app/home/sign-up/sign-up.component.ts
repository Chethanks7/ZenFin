import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ApiService } from 'src/app/api.service';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss']
})
export class SignUpComponent {

  //Sevices
  api = inject(ApiService)



  firstFormGroup = this._formBuilder.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  secondFormGroup = this._formBuilder.group({
    otp: ['', Validators.required],
  });




  value: any;
  otpConfig = {
    length: 6,
    allowNumbersOnly: true,
    inputStyles: {
      width: '50px',
      height: '50px',
    }
  }
  constructor(private _formBuilder: FormBuilder) { }

  onRegister() {
    console.log("register called ...");
    const registrationReq = {
      firstname: this.firstFormGroup.controls.firstName.value,
      lastname: this.firstFormGroup.controls.lastName.value,
      email: this.firstFormGroup.controls.email.value,
      password: this.firstFormGroup.controls.firstName.value,
    }
    this.api.register(registrationReq).subscribe({
      next: (respose) => {
        console.log(respose)
      },
      error: (error) => {
        console.log(error)
      }
    });

  }


}
