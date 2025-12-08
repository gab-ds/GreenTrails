import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PopupDeleteConfermaComponent } from '../popupDeleteConferma/popupDeleteConferma.component';

@Component({
  selector: 'app-popupDettagliAttivita',
  templateUrl: './popupDettagliAttivita.component.html',
  styleUrls: ['./popupDettagliAttivita.component.css']
})
export class PopupDettagliAttivitaComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<PopupDeleteConfermaComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  formatStato(stato: string): string {
    return stato.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase());
  }
  formatDateTime(dateTimeString: string): string {
    const [time, milliseconds] = dateTimeString.split('.');
    
    const today = new Date();
    
    const [hours, minutes, seconds] = time.split(':');
    today.setHours(+hours, +minutes, +seconds);
    
    const formattedDate = this.formatDate(today);
    const formattedTime = this.formatTime(today);
  
    return `${formattedDate} ${formattedTime}`;
  }
  
  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
  
  formatTime(date: Date): string {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
  }
}


