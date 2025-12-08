import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-icon-toolbar',
  templateUrl: './icon-toolbar.component.html',
  styleUrls: ['./icon-toolbar.component.css']
})
export class IconToolbarComponent implements OnInit {

  constructor(public router: Router, private cookieService: CookieService) { }

  isLoggedIn = false;

  ngOnInit(): void {
    this.isLoggedIn = this.cookieService.get('user') !== ''
  }

}
