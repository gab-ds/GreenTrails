#!/bin/sh
#./mvnw verify -P openjml-utenze       > openjml-utenze.txt 2>&1
#./mvnw verify -P openjml-attivita       > openjml-attivita.txt 2>&1
./mvnw verify -P openjml-itinerari    > openjml-itinerari.txt 2>&1
./mvnw verify -P openjml-prenotazioni > openjml-prenotazioni.txt 2>&1
./mvnw verify -P openjml-ricerca      > openjml-ricerca.txt 2>&1
./mvnw verify -P openjml-segnalazioni > openjml-segnalazioni.txt 2>&1
./mvnw verify -P openjml-upload > openjml-upload.txt 2>&1
