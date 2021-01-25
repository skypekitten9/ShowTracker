För att köra igång programmet:

Öppna projektet i ett IDE som Eclipse eller IntelliJ

Lägg till JAR-filerna som finns i mappen "lib" i projektet

Starta servern genom att köra klassen showtracker.server.Controller
Efter det behöver servern autentiseras mot TheTVDB:
	1. Klicka på knappen "Authenticate"
	2. Kopiera den kod som dyker upp
	3. Klicka på "Open in browser"
	4. Klistra in koden i rutan uppe till höger på hemsidan där det står "JWT Token"
	5. Klicka på "Add Token"
Färdigt! Starta nu servern för klientanvändning genom att välja antalet trådar som ska vara igång, och klicka på "Start server"

Starta en klient genom att köra klassen showtracker.client.ClientController

GitHub repository:
https://github.com/Basir98/ShowTracker