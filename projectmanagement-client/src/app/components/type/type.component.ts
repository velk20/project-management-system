import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-type',
  standalone: true,
  imports: [],
  templateUrl: './type.component.html',
  styleUrl: './type.component.css'
})
export class TypeComponent {
  @Input({required: true}) type!: string;
}
