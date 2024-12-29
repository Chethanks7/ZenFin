import { NgModule } from '@angular/core';
import { SignUpComponent } from './sign-up.component';
import { MatStepperModule } from '@angular/material/stepper';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { SignUpRoutingModule } from './sign-up-routing.module';
import { MatCommonModule } from '@angular/material/core';
import { NgOtpInputModule } from 'ng-otp-input';
import { CommonModule } from '@angular/common';

@NgModule({
  declarations: [SignUpComponent],
  imports: [
         // Required for stepper functionality
    MatFormFieldModule,       // Required for mat-label and form fields
    MatInputModule, 
    MatStepperModule,
    FormsModule,
    ReactiveFormsModule,
    SignUpRoutingModule,
    MatCommonModule,
    NgOtpInputModule,CommonModule
    
  ],exports:[SignUpComponent]
})
export class SignUpModule { }
