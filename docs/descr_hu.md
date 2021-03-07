Spring State Machine demo application
=====================================

Ez az alkalmazás a [Spring State Machine](https://projects.spring.io/spring-statemachine/) 
mutatja be. Az állapotgép bár egy ősrégi minta és nem is nagyon javasolt, de 
mégis sokszor hasznos, emiatt érdemes ismerni.

Demó alkalmazás leírása
-----------------------
Egy nagyon egyszerű aukciós alkalmazást valósítok meg. Amíg a market 
nyitva van, addig lehet beküldeni az ajánlatokat, amikor pedig lezár, akkor 
kiértékeljük a befutott ajánlatokat, és a legjobb ajánlatot elmentjük. Azonos
ár esetén az első beküldő a nyertes.

Két actort különböztetünk meg, mégpedig felhasználót és adminisztrátort. A 
felhasználónak mindössze ár beírására van lehetősége, és amennyiben a market
nyitva, beküldheti az ajánlatát.

Az adminisztrátor manuálisan el tudja indítani és le tudja állítani a marketet,
illetve képes listázni a sikeres alkukat. A piacnyitás időzítve minden öt 
percben indul és két percig van nyitva. Egy ajánlat bekérés akkor sikeres, ha
jött érvényes ajánlat, egyébként sikertelen.

State Machine
-------------
A képen látható a rendkívül egyszerűsített állapotgép. A megvalósítása 
[StateMachineConfig](../src/main/java/hu/lsm/smdemo/configuration/StateMachineConfig.java)
osztályban található. Ne felejtsük el az *@EnableStateMachine* annotációt
hozzáadni a Spring Boot fő indító osztályhoz. 
([SmDemoApplication](../src/main/java/hu/lsm/smdemo/SmDemoApplication.java))

![State machine](bargain.png)

A Spring State Machine néhány alapvető  *State, Event, Transition, Guard, Action, Fork és 
Join* elemekkel operál (a teljesség igénye nélkül). A State egyértelmú, az Event esemény
válthat ki egy változást az állapotgépen. A Transition definíció legfontosabb, ez 
dönti el, hogy mikor mi történhet, melyik állapotból melyikbe mehetünk. A Guard segítségével
az elágazások implementálhatók, lényegében két irányba lehet tovább ugrani. 
A Fork és Join segítségével több feladat is "összevárható".

```java
// ez egy Transiton definició
transition.withExternal()
        .source(AppState.RUNNING)
        .target(AppState.COMPLETED)
        .event(AppEvent.COMPLETE)
        .action(valamiAction)
```
Egyes átmenetekhez, de állapothoz is hozzá lehet kötni Action-t, ami egy 
önálló kódrészlet, önálló funkcionalitást láthat el.

Érdemes megemlíteni az *stateContext.getExtendedState().getVariables()* 
Map-et is, mivel az ebben tárolt elemeket az adott context-ben bárhonnan el
lehet érni. Most csak a kezdő időpontot tettem bele, de gyakorlailag bármire
alkalmas.

Remek és könnyen érthető példák érhetők el a 
[Spring State Machine dokumentációs](https://docs.spring.io/spring-statemachine/docs/2.4.x/reference/#statemachine-examples)
oldalán.

Néhány szóban az itt használt állapotokról. Az INIT csak az egész alkalmazás
indulásakor tűnik fel, automatikusan át is lép a WAITING-be. Amíg 
RUNNING-ban állunk, addig van nyitva a market, addig érkezhetnek az ajánlatok.
Amennyiben volt ajánlat, akkor COMPLETED-be megyünk, majd vissza a kiinduló 
WAITING-be. Ha nem érkezett semmi, akkor FAILED vagyis sikertelen volt az 
aukció és visszatérünk az induláshoz.

Annak érdekében, hogy ne mindenütt közvetlenül érjük el az állapotgépünket, 
ezért használjuk a 
[StateMachineManager](../src/main/java/hu/lsm/smdemo/service/StateMachineManager.java) 
osztályt.

A teszteléshez egy olyan Listener született, ami az aktuális állapotokat
mindig eltárolja egy listában. Ezek után könnyen visszakövethetőek az
változások.

Security
--------
Annak érdekében, hogy meg tudjuk különböztetni a felhasználót az adminisztrátortól,
valamilyen bejelentkezést és authentikációt kellett alkalmazni. A példa kedvéért
BasicAuth segítségével az alap felhasználónév és jelszó párossal lehet belépni.
_Éles rendszerben ne használjunk ilyet!_ 

Amennyiben hozzáadjuk a függőségek közé a security csomagot, a Spring Boot
rögtön aktiválja is és ezen azért meg lehet lepődni. 

A [SecurityConfig](../src/main/java/hu/lsm/smdemo/configuration/SecurityConfig.java)
osztályban található minden beállítás. A Spring Boot mindenre kínál 
alapértelmezett megoldást ([itt](https://dzone.com/articles/spring-security-basic-authentication-example-1)),
de ha valami miatt mégis egyszerű, saját definicióra van szükség, akkor
ne felejtsük el kikapcsolni a beépítettet.
```java
@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class
})
```

Inkább [ezt](https://www.javadevjournal.com/spring/basic-authentication-with-spring-security/) 
a mintát alkalmazva viszonylag egyszerűen össze lehet rakni egy működő
beállítást.

A tesztelése közel sem ennyire triviális, érdemes ránézni a 
[SecurityConfigTest](../src/test/java/hu/lsm/smdemo/integration/configuration/SecurityConfigTest.java)
implementációjára. Amennyiben nincs szükség a tesztekben a secure elérésre, 
akkor legegyszerűbb kikapcsolni.

```java
@AutoConfigureMockMvc(addFilters = false)
```

Web UI
------
Bár az rögtön látszik, hogy Frontend terén volna hova tovább, de valamilyen 
minimális felületnek elérhetőnek kell lennie, nem csak a Swagger REST
kliensén keresztül kéne demózni.

A [VueJS](https://vuejs.org/) nagyon kényelmes kis webalkalmazásokhoz,
bőségesen elegendő erre a néhány REST hívásra.

Az aktuális állapotot időzítve kérdezgetjük (poll-ozás), nem egy igazi
reaktív megoldás, de nem ez most a fókusz. Az 
[offer](../src/main/resources/static/offer/offer.html) oldalon látható
az időzítő a Vue *created()* funkciójára épít.

```javascript
var app = new Vue({
    el: '#app',
    data() { 
      ...
      timer: ''
    },
    methods: {
      ...
      getCurrentState: function() {...}
    },
    created () {
        this.getCurrentState();
        this.timer = setInterval(this.getCurrentState, 5000);
    }
 });
```

A favicon-t a [https://favicon.io/favicon-generator/](https://favicon.io/favicon-generator/)
segítségével generáltattam.

Képernyő képek
--------------
![index page](bargain_index.png)
![offer page](bargain_offer.png)
![admin page](bargain_admin.png)

Felhasznált cikkek
------------------
* [Spring State Machine dokumentáció](https://docs.spring.io/spring-statemachine/docs/2.4.x/reference/#statemachine-examples)
* számos [Baeldung](https://www.baeldung.com/) cikk