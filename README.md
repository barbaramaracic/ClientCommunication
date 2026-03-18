📊 Client Communication Preferences Management Tool
📌 ## Opis projekta

Client Communication Preferences Management Tool je Java aplikacija razvijena za učinkovito upravljanje preferencijama komunikacije klijenata. Sustav omogućuje praćenje načina na koji klijenti žele biti kontaktirani (email, SMS, poziv itd.), evidenciju komunikacije te analizu učinkovitosti komunikacijskih kanala.

🚀 ## Glavne funkcionalnosti

✅ Upravljanje profilima klijenata

✅ Postavljanje i izmjena komunikacijskih preferencija

✅ Evidencija komunikacije (logovi)

✅ Generiranje izvještaja o učinkovitosti komunikacije

✅ Autentifikacija korisnika (login sustav) - ADMIN i USER

✅ Pregled povijesti promjena (audit log)

✅ Višedretveno osvježavanje podataka

🧩 Korištene tehnologije i koncepti

🔹 Objektno-orijentirano programiranje

Apstraktne klase

Sučelja (interfaces)

Zapečaćena sučelja (sealed interfaces)

Zapisi (records)

Builder pattern

Generičke klase

Kolekcije i lambda izrazi

🔹 Rad s iznimkama

Kreirane:

✔️ 2 označene (checked) iznimke

✔️ 2 neoznačene (unchecked) iznimke

Sve iznimke se logiraju pomoću Logback biblioteke

📂 Rad s podacima
📝 Tekstualne datoteke

Spremaju korisnička imena i hashirane lozinke

Koriste se za autentifikaciju korisnika

💾 Binarne datoteke

Serijalizacija i deserijalizacija promjena u sustavu

Praćenje povijesti izmjena podataka


🖥️ JavaFX korisničko sučelje
🔐 Login ekran

Učitava korisnike iz tekstualne datoteke

Podržava najmanje 2 korisničke role (npr. ADMIN, USER)

📊 Upravljanje entitetima

Za svaki entitet omogućeno:

pregled (TableView)

pretraga i filtriranje (lambda izrazi)

dodavanje

uređivanje

brisanje (uz potvrdu)

🕓 Povijest promjena

Prikaz svih izmjena:

stara vrijednost

nova vrijednost

korisnička rola

datum i vrijeme

🔄 Niti (Threads)

Implementirano:

Automatsko osvježavanje podataka na UI-u
