import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SignUpComponent } from './home/sign-up/sign-up.component';
import { LoginComponent } from './home/login/login.component';
import { HomeModule } from "./home/home.module";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { MatCommonModule } from '@angular/material/core';
import { HttpClientModule } from '@angular/common/http';



@NgModule({
  declarations: [
    AppComponent,


  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HomeModule,
    BrowserAnimationsModule,
    MatStepperModule,
    MatCommonModule,
    ReactiveFormsModule,
    HttpClientModule

  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
