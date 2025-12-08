import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-politiche-eco',
  templateUrl: './politiche-eco.component.html',
  styleUrls: ['./politiche-eco.component.css']
})
export class PoliticheEcoComponent implements OnInit {
  cols: number = 3;
  constructor(private breakpointObserver: BreakpointObserver) { }

  ngOnInit(): void {  
    this.breakpointObserver.observe([
      Breakpoints.Handset,
      Breakpoints.Tablet,
      Breakpoints.Web,
      Breakpoints.HandsetPortrait,
      Breakpoints.TabletPortrait,
      Breakpoints.WebPortrait,
    ]).subscribe(result => {
      this.adjustGridCols(result);
    });
  }

  // Funzione per regolare il numero di colonne in base alle dimensioni dello schermo
  private adjustGridCols(breakpointState: any): void {
    if (breakpointState.breakpoints[Breakpoints.Handset] || breakpointState.breakpoints[Breakpoints.HandsetPortrait]) {
      this.cols = 1;
    } else if (breakpointState.breakpoints[Breakpoints.Tablet] || breakpointState.breakpoints[Breakpoints.TabletPortrait]) {
      this.cols = 2;
    } else {
      this.cols = 3; // Imposta il numero massimo di colonne per schermi più grandi
    }
  }
}

