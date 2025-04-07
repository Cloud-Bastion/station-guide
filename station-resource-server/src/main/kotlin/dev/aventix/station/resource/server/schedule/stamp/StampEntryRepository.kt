package dev.aventix.station.resource.server.schedule.stamp

import dev.aventix.station.resource.server.employee.EmployeeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface StampEntryRepository: JpaRepository<StampEntry, UUID> {

    /*
    MAX. 10 Stunden Arbeit, dann 11 Stunden Ruhe

    -----------------------------------------
    Situation:
        Mitarbeiter stempelt sich ein oder soll laut Plan
        eingestempelt werden.

    Nehme alle Stempeleinträge der letzten 11 Stunden
    1. Fall: Keine Einträge
        -> Fertig, kein Verstoß

    2. Fall: Wir finden ein END Stempel
        -> Fertig, Verstoß

    -----------------------------------------
    Situation:
        Mitarbeiter stempelt sich aus -> Prüfen ob nächste geplante
        Schicht erlaubt ist

    Suche nach Schichten für diesen Mitarbeiter in den nächsten
    11 Stunden, bei Minderjährigen 12 Stunden.

    1. Fall: Keine Einträge
        -> Fertig, kein Verstoß
    2. Fall: Warnung wird erzeugt, Notiz mit Begründung muss angehangen werden.

    ------------------------------------------
    Situation:
    Mitarbeiter ist eingestempelt, prüfe jede Minute die Pausen:

    Sammle alle Pausen START und END Stempel und addiere
    nur die Pausenzeiten, die >= 15 Minuten sind.

    1. Fall: Mitarbeiter ist unter 18 Jahre:
        Er hat 4,5 Stunden gearbeitet und muss dann 30 Minuten Pause einlegen
    2. Fall: Mitarbeiter ist unter 18 Jahre:
        Er hat 6 Stunden gearbeitet und muss dann 60 Minuten Pause einlegen
    3. Fall: Mitarbeiter ist über 18 Jahre:
        Er hat 6 Stunden gearbeitet und muss dann 30 Minuten Pause einlegen
    4. Fall: Mitarbeiter ist über 18 Jahre:
        Er hat 9 Stunden gearbeitet und muss dann 45 Minuten Pause einlegen
    5. Fall: Mitarbeiter ist unter 18 Jahre:
        Er hat nicht länger als 8 Stunden gearbeitet
    6. Fall: Mitarbeiter ist über 18 Jahre:
        Er hat nicht länger als 10 Stunden gearbeitet.


    ------------------------------------------
    Zusätzliche Warnungen beim Dienstplan:

    - Keine Arbeit von u18 an Sonn- und Feiertagen außer an
    verkaufsoffenen Sonntagen.
    - u18 dürfen nicht zwischen 22 und 6 Uhr arbeiten
    - u18 nicht mehr als 5x Pro Woche

    - max. 6 Tage am Stück arbeiten
//    - 10 Stunden Schichten nur erlaubt, wenn der 6-monats-
//      Durchschnitt bei 8 Stunden liegt.

     */

    fun findByTimestampAndAssignee(timestamp: Instant, assignee: EmployeeEntity): List<StampEntry>

}