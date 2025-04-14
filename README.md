# MultiWeb - NamelessMC Minecraft Integratie

MultiWeb is een Minecraft plugin die integratie biedt tussen je Minecraft server en je NamelessMC website. Met deze plugin kunnen spelers zich registreren, hun account verifiëren en website notificaties ontvangen in de game.

## Functies

- **Registratie**: Spelers kunnen zich registreren op je NamelessMC website vanuit de game
- **Verificatie**: Spelers kunnen hun account verifiëren met een code vanuit de game
- **Notificaties**: Spelers ontvangen een melding in de game wanneer ze ongelezen notificaties hebben op de website
- **Automatische controle**: Controleert of spelers geregistreerd zijn wanneer ze de server joinen
- **Beloning systeem**: Mogelijkheid om commando's uit te voeren na succesvolle verificatie

## Commando's

- `/register <email>` - Registreer je account op de website
- `/verify <code>` - Verifieer je account met een verificatiecode
- `/nameless help` - Toon hulp voor admin commando's
- `/nameless reload` - Herlaad de plugin configuratie
- `/nameless status` - Controleer de API verbindingsstatus

## Permissies

- `multiweb.register` - Toestemming om het /register commando te gebruiken (standaard: iedereen)
- `multiweb.verify` - Toestemming om het /verify commando te gebruiken (standaard: iedereen)
- `multiweb.admin` - Toestemming om admin commando's te gebruiken (standaard: operators)
- `multiweb.bypass` - Bypass registratiecontroles en notificaties (standaard: operators)

## Installatie

1. Download het MultiWeb.jar bestand
2. Plaats het in de plugins map van je server
3. Start de server of gebruik /reload
4. Bewerk het config.yml bestand om je NamelessMC API URL en API sleutel in te stellen
5. Herstart de server of gebruik /nameless reload

## Configuratie

De plugin heeft een uitgebreid configuratiebestand waarmee je alle aspecten van de plugin kunt aanpassen. Zie `config.yml` voor alle opties.

## Vereisten

- Bukkit/Spigot/Paper Minecraft server (1.16 of hoger)
- NamelessMC v2 website met API ingeschakeld

## Ondersteuning

Als je problemen ondervindt of vragen hebt, kun je contact opnemen via:
- GitHub Issues
- Discord: [Link naar je Discord server]

## Licentie

Deze plugin is uitgebracht onder de [Jouw Licentie] licentie.
```

```markdown:UITLEG.md
# MultiWeb - Technische Uitleg

Dit document biedt een technische uitleg van de MultiWeb plugin en hoe deze communiceert met de NamelessMC API.

## Architectuur

De plugin is opgebouwd uit verschillende componenten:

### 1. Hoofdklasse (MultiWeb.java)

De hoofdklasse initialiseert de plugin, laadt de configuratie, registreert commando's en event listeners, en beheert de API verbinding. Het bevat ook hulpmethoden voor het formatteren van berichten en het beheren van cooldowns.

### 2. API Communicatie (NamelessAPI.java)

Deze klasse handelt alle communicatie met de NamelessMC API af. Het gebruikt HTTP POST requests om gegevens uit te wisselen met de website. De volgende API endpoints worden gebruikt:

- `/register` - Voor het registreren van nieuwe gebruikers
- `/verify` - Voor het verifiëren van gebruikers met een code
- `/userinfo` - Voor het ophalen van gebruikersinformatie
- `/notifications` - Voor het ophalen van notificaties
- `/info` - Voor het controleren van de API verbinding

### 3. Commando's

De plugin bevat drie hoofdcommando's:

- **NamelessCommand**: Beheert admin commando's zoals reload en status
- **RegisterCommand**: Handelt gebruikersregistratie af
- **VerifyCommand**: Handelt gebruikersverificatie af

### 4. Event Listeners

De **PlayerListener** klasse luistert naar speler join events om te controleren of spelers geregistreerd zijn op de website.

### 5. Taken

De **NotificationTask** klasse controleert periodiek of online spelers ongelezen notificaties hebben op de website.

## Dataflow

1. Wanneer een speler het `/register` commando gebruikt:
   - De plugin valideert het e-mailadres
   - Controleert of de speler al geregistreerd is
   - Stuurt een registratieverzoek naar de NamelessMC API
   - Toont een succesbericht en instructies voor verificatie

2. Wanneer een speler het `/verify` commando gebruikt:
   - De plugin stuurt de verificatiecode naar de NamelessMC API
   - Bij succes kan de plugin geconfigureerde commando's uitvoeren
   - Toont een succesbericht aan de speler

3. Wanneer een speler de server joint:
   - De plugin controleert of de speler geregistreerd is
   - Toont registratie-instructies indien nodig

4. Periodiek voor alle online spelers:
   - De plugin controleert op ongelezen notificaties
   - Toont een bericht als er notificaties zijn

## API Integratie

De plugin communiceert met de NamelessMC API v2 via HTTP POST requests. Elke request bevat een autorisatie header met de API sleutel uit de configuratie. De gegevens worden verzonden en ontvangen in JSON formaat.

### Voorbeeld van een API request:

```
POST /api/v2/register
Authorization: Bearer your_api_key_here
Content-Type: application/json

{
  "username": "PlayerName",
  "uuid": "player-uuid-here",
  "email": "player@example.com"
}
```

## Configuratie Uitleg

De plugin gebruikt een uitgebreid configuratiebestand met de volgende secties:

- **API instellingen**: URL en sleutel voor de NamelessMC API
- **Cooldown**: Wachttijd tussen commando's om spam te voorkomen
- **Registratie instellingen**: Opties voor het registratieproces
- **Verificatie instellingen**: Opties voor het verificatieproces, inclusief beloningscommando's
- **Join check instellingen**: Opties voor controles bij het joinen
- **Notificatie instellingen**: Opties voor het controleren van notificaties
- **Berichten**: Alle berichten die door de plugin worden getoond

## Uitbreidingsmogelijkheden

De plugin kan worden uitgebreid met:

1. Ondersteuning voor meer NamelessMC API endpoints
2. Integratie met andere plugins zoals placeholders
3. Meer geavanceerde beloningssystemen
4. Webshop integratie
5. Forum integratie

## Probleemoplossing

Als de plugin niet correct werkt, controleer dan:

1. Of de API URL en sleutel correct zijn ingesteld
2. Of de NamelessMC API is ingeschakeld op je website
3. Of je server toegang heeft tot het internet
4. De server logs voor eventuele foutmeldingen
5. Of de plugin up-to-date is met je NamelessMC versie
