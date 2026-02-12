import { RecensioneFormComponent } from './componenti/pagina-attivita/recensioni/recensione-form/recensione-form.component';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA, APP_INITIALIZER } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { NgbCarouselModule, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ConfigService } from './config/config.service';
import { setApiBaseUrl } from './config/api-config';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { PaginaAttivitaComponent } from './componenti/pagina-attivita/pagina-attivita.component';
import { InfoAttivitaComponent } from './componenti/pagina-attivita/info-attivita/info-attivita.component';
import { CardAttivitaComponent } from './componenti/pagina-attivita/info-attivita/card-attivita/card-attivita.component';
import { RecensioniComponent } from './componenti/pagina-attivita/recensioni/recensioni.component';
import { DescrizioneAttivitaComponent } from './componenti/pagina-attivita/descrizione-attivita/descrizione-attivita.component';
import { ValutazioneAttivitaComponent } from './componenti/pagina-attivita/info-attivita/valutazione-attivita/valutazione-attivita.component';
import { PoliticheEcosostenibiliAttivitaComponent } from './componenti/pagina-attivita/info-attivita/politiche-ecosostenibili-attivita/politiche-ecosostenibili-attivita.component';
import { SlideshowComponent } from './componenti/pagina-attivita/descrizione-attivita/slideshow/slideshow.component';
import { MapComponent } from './componenti/pagina-attivita/info-attivita/map/map.component';

// Material Form Controls
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSliderModule } from '@angular/material/slider';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// Material Navigation
import { MatMenuModule } from '@angular/material/menu';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
// Material Layout
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatListModule } from '@angular/material/list';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTreeModule } from '@angular/material/tree';
// Material Buttons & Indicators
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatBadgeModule } from '@angular/material/badge';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatNativeDateModule, MatRippleModule } from '@angular/material/core';
// Material Popups & Modals
import { MatBottomSheetModule } from '@angular/material/bottom-sheet';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
// Material Data tables
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { HttpClientModule } from '@angular/common/http';
import { LoginComponent } from './componenti/login/login.component';
import { RegistrazioneComponent } from './componenti/registrazione/registrazione.component';
import { QuestionarioComponent } from './componenti/questionario/questionario.component';
import { PopUpQuestionarioComponent } from './componenti/questionario/pop-up-questionario/pop-up-questionario.component';
import { PopupRecensioneComponent } from './componenti/pagina-attivita/recensioni/popup-recensione/popup-recensione.component';
import { Ng2SearchPipeModule } from 'ng2-search-filter';
import { CookieService } from 'ngx-cookie-service';
import { HomePageComponent } from './componenti/home-page/home-page.component';
import { GalleryDialogComponent } from './componenti/pagina-attivita/recensioni/gallery-dialog/gallery-dialog.component';
import { VideoDialogComponent } from './componenti/pagina-attivita/recensioni/video-dialog/video-dialog.component';
import { ToolbarComponent } from './componenti/toolbar/toolbar.component';
import { ToolbarHomepageComponent } from './componenti/toolbar-homepage/toolbar-homepage.component';
import { GestionePrenotazioniAttiveComponent } from './componenti/gestione-prenotazioni-attive/gestione-prenotazioni-attive.component';
import { AreaRiservataComponent } from './componenti/area-riservata/area-riservata.component';
import { PopupErrorPassComponent } from './componenti/login/popup-errorPass/popup-errorPass.component';
import { IconToolbarComponent } from './componenti/icon-toolbar/icon-toolbar.component';
import { RicercaComponent } from './componenti/ricerca/ricerca.component';
import { RisultatiComponent } from './componenti/ricerca/risultati/risultati.component';
import { GestioneAttivitaComponent } from './componenti/gestione-attivita/gestione-attivita.component';
import { PopupEliminazioneComponent } from './componenti/gestione-attivita/popup-eliminazione-attivita/popup-eliminazione-attivita.component';
import { InserimentoAttivitaComponent } from './componenti/inserimento-attivita/inserimento-attivita.component';
import { PopUpConfermaComponent } from './componenti/inserimento-attivita/pop-up-conferma/pop-up-conferma.component';
import { PopUpAlloggioComponent } from './componenti/inserimento-attivita/pop-up-alloggio/pop-up-alloggio.component';
import { PopupModificaComponent } from './componenti/gestione-attivita/popup-modifica-attivita-turistica/popup-modifica-attivita-turistica.component';
import { PopupModificaAlloggioComponent } from './componenti/gestione-attivita/popup-modifica-alloggio/popup-modifica-alloggio.component';
import { PopupConfermaModificaComponent } from './componenti/gestione-attivita/popup-conferma-modifica/popup-conferma-modifica.component';
import { PopupModificaDatiAlloggioComponent } from './componenti/gestione-attivita/popup-modifica-dati-alloggio/popup-modifica-dati-alloggio.component';
import { PopupEliminazioneCameraComponent } from './componenti/gestione-attivita/popup-eliminazione-camera/popup-eliminazione-camera.component';
import { GestioneValoriComponent } from './componenti/gestione-attivita/gestione-valori/gestione-valori.component';
import { PopupComponent } from './componenti/gestione-attivita/gestione-valori/popup/popup.component';
import { PrenotazioniComponent } from './componenti/pagina-attivita/prenotazioni/prenotazioni.component';
import { PopUpPrenotazioneComponent } from './componenti/pagina-attivita/pop-up-prenotazione/pop-up-prenotazione.component';
import { PrenotazioniAttivitaComponent } from './componenti/pagina-attivita/prenotazioni-attivita/prenotazioni-attivita.component';
import { PopUpRegistrazioneComponent } from './componenti/registrazione/pop-up-registrazione/pop-up-registrazione.component';
import { PopUpCategorieComponent } from './componenti/inserimento-attivita/pop-up-categorie/pop-up-categorie.component';
import { PopupModificaCategorieComponent } from './componenti/gestione-attivita/popup-modifica-categorie/popup-modifica-categorie.component';
import { PopupEliminazioneCategorieComponent } from './componenti/gestione-attivita/popup-eliminazione-categorie/popup-eliminazione-categorie.component';


import { ChisiamoComponent } from './componenti/chisiamo/chisiamo.component';
import { IntroduzioneComponent } from './componenti/chisiamo/introduzione/introduzione.component';
import { PoliticheEcoComponent } from './componenti/chisiamo/politiche-eco/politiche-eco.component';
import { FineComponent } from './componenti/chisiamo/fine/fine.component';
import { EffettuataComponent } from './componenti/popupsegnalazione/effettuata/effettuata.component';
import { PopupsegnalazioneComponent } from './componenti/popupsegnalazione/popupsegnalazione.component';import { PopupDeleteConfermaComponent } from './componenti/gestione-prenotazioni-attive/popupDeleteConferma/popupDeleteConferma.component';
import { PopupDettagliComponent } from './componenti/gestione-prenotazioni-attive/popupDettagli/popupDettagli.component';
import { PopupDettagliAttivitaComponent } from './componenti/gestione-prenotazioni-attive/popupDettagliAttivita/popupDettagliAttivita.component';
import { CalendariopopupComponent } from './componenti/generazione-automatica/calendariopopup/calendariopopup.component';
import { GenerazioneAutomaticaComponent } from './componenti/generazione-automatica/generazione-automatica.component';
import { ModificaValoriAdminComponent } from './componenti/modifica-valori-admin/modifica-valori-admin.component';
import { ListaSegnalazioniComponent } from './componenti/lista-segnalazioni/lista-segnalazioni.component';
import { TopbuttonComponent } from './componenti/topbutton/topbutton.component';
import { CookieDialogComponent } from './componenti/cookiedialog/cookiedialog.component';
import { PopUpErroriComponent } from './componenti/pagina-attivita/prenotazioni/pop-up-errori/pop-up-errori.component';
import { PopupRecensioneFailComponent } from './componenti/pagina-attivita/recensioni/popup-recensione-fail/popup-recensione-fail.component';
import { PopuperroreComponent } from './componenti/modifica-valori-admin/popuperrore/popuperrore.component';
import { ErrorPopupComponent } from './componenti/generazione-automatica/error-popup/error-popup.component';

export function initializeApp(configService: ConfigService) {
  return () => configService.loadConfig().toPromise().then(config => {
    if (config) {
      setApiBaseUrl(config.apiBaseUrl);
    }
  });
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    PaginaAttivitaComponent,
    InfoAttivitaComponent,
    CardAttivitaComponent,
    RecensioniComponent,
    DescrizioneAttivitaComponent,
    ValutazioneAttivitaComponent,
    PoliticheEcosostenibiliAttivitaComponent,
    SlideshowComponent,
    MapComponent,
    RegistrazioneComponent,
    QuestionarioComponent,
    PopUpQuestionarioComponent,
    RecensioneFormComponent,
    PaginaAttivitaComponent,
    PopupErrorPassComponent,
    PopupRecensioneComponent,
    RegistrazioneComponent,
    HomePageComponent,
    ToolbarComponent,
    ToolbarHomepageComponent,
    GestionePrenotazioniAttiveComponent,
    AreaRiservataComponent,
    PopupErrorPassComponent,
    IconToolbarComponent,
    RicercaComponent,
    RisultatiComponent,
    GestioneAttivitaComponent,
    PopupEliminazioneComponent,
    InserimentoAttivitaComponent,
    PopUpConfermaComponent,
    PopUpAlloggioComponent,
    PopupModificaComponent,
    PopupModificaAlloggioComponent,
    PopupConfermaModificaComponent,
    PopupModificaDatiAlloggioComponent,
    PopupEliminazioneCameraComponent,
    GestioneValoriComponent,
    PopupComponent,
    PrenotazioniComponent,
    PopUpPrenotazioneComponent,
    PrenotazioniAttivitaComponent,
    PopUpRegistrazioneComponent,
    GestionePrenotazioniAttiveComponent,
    AreaRiservataComponent,
    CalendariopopupComponent,
    RicercaComponent,
    QuestionarioComponent,
    RisultatiComponent,
    GestionePrenotazioniAttiveComponent,
    GalleryDialogComponent,
    VideoDialogComponent,
    PopUpCategorieComponent,
    PopupModificaCategorieComponent,
    PopupEliminazioneCategorieComponent,
    PopupModificaCategorieComponent,
    PopupEliminazioneCategorieComponent,
    LoginComponent,
    ChisiamoComponent,
    IntroduzioneComponent,
    PoliticheEcoComponent,
    FineComponent,
    EffettuataComponent,
    PopupsegnalazioneComponent,
    PopupComponent,
    ModificaValoriAdminComponent,
    ListaSegnalazioniComponent,
    PopupDeleteConfermaComponent,
    PopupDettagliComponent,
    PopupDettagliAttivitaComponent,
    GenerazioneAutomaticaComponent,
    TopbuttonComponent,
    CookieDialogComponent,
    PopupRecensioneFailComponent,
    PopuperroreComponent,
    PopUpErroriComponent,
    PopupRecensioneFailComponent,
    ErrorPopupComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    CommonModule,
    MatAutocompleteModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    MatInputModule,
    MatRadioModule,
    MatSelectModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatMenuModule,
    MatSidenavModule,
    MatToolbarModule,
    MatCardModule,
    MatDividerModule,
    MatExpansionModule,
    MatGridListModule,
    MatListModule,
    MatStepperModule,
    MatTabsModule,
    MatTreeModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatBadgeModule,
    MatChipsModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    MatRippleModule,
    MatBottomSheetModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatPaginatorModule,
    MatSortModule,
    MatTableModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    NgbCarouselModule,
    MatTooltipModule,
    HttpClientModule,
    MatNativeDateModule,
    MatIconModule,
    Ng2SearchPipeModule,
  ],
  exports: [
    MatAutocompleteModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatRadioModule,
    MatSelectModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatMenuModule,
    MatSidenavModule,
    MatToolbarModule,
    MatCardModule,
    MatDividerModule,
    MatExpansionModule,
    MatGridListModule,
    MatListModule,
    MatStepperModule,
    MatTabsModule,
    MatTreeModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatBadgeModule,
    MatChipsModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    MatRippleModule,
    MatBottomSheetModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatPaginatorModule,
    MatSortModule,
    MatTableModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    NgbCarouselModule,
    MatTooltipModule,
    HttpClientModule,
    MatNativeDateModule,
    Ng2SearchPipeModule,
    MatIconModule,
    Ng2SearchPipeModule
  ],
  providers: [
    CookieService,
    ConfigService,
    {
      provide: APP_INITIALIZER,
      useFactory: initializeApp,
      deps: [ConfigService],
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
