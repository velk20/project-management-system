import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

import { registerLocaleData } from '@angular/common';
import localeBg from '@angular/common/locales/bg';

registerLocaleData(localeBg);
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
