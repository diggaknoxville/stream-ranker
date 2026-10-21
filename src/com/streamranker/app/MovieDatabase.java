package com.streamranker.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class MovieDatabase {
    public static final int SORT_IMDB = 0;
    public static final int SORT_ROTTEN_TOMATOES = 1;
    public static final int SORT_PERLE = 2;
    public static final int SORT_YEAR = 3;

    private static MovieDatabase instance;
    private final List<Movie> masterList = new ArrayList<>();

    public static synchronized MovieDatabase getInstance() {
        if (instance == null) {
            instance = new MovieDatabase();
        }
        return instance;
    }

    private MovieDatabase() {
        populateDefaultCatalog();
    }

    private void populateDefaultCatalog() {
        masterList.add(new Movie(
            "tt33998607", "One Piece Fan Letter", 2024, 9.2, 25659,
            99, 95.5,
            "Action & Abenteuer, Animation, Komödien", 25, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81947352", false, null,
            "Ein junges Mädchen, das im Zeitalter der Piraten lebt, hegt keinerlei Sympathie für die gewalttätigen Unruhestifter, ist jedoch Namis größter Fan. Als sie er...",
            "https://images.justwatch.com/poster/321588681/s332/one-piece-fan-letter.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0068646", "Der Pate", 1972, 9.2, 2266388,
            97, 94.5,
            "Krimi, Drama", 175, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.468f8466-d958-4e43-82bd-4375149d48a8", false, null,
            "Don Vito Corleone ist der Kopf einer der einflußreichsten Mafia-Familien New Yorks. Als die anderen Familien der Stadt beschließen, neben Glücksspiel und Sch...",
            "https://images.justwatch.com/poster/281942212/s332/Der-Pate.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0071562", "Der Pate - Teil II", 1974, 9.0, 1520043,
            96, 93.0,
            "Krimi, Drama", 202, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.c4a9f6db-167c-7b3a-b6cc-870ac83848d3", false, null,
            "Michael Corleone ist jetzt der Pate seiner Familie und führt diese Aufgabe mit der nötigen Gnadenlosigkeit aus. Als er in Las Vegas und auf Kuba expandieren ...",
            "https://images.justwatch.com/poster/281941946/s332/Der-Pate-2.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0468569", "The Dark Knight", 2008, 9.1, 3241362,
            94, 92.5,
            "Action & Abenteuer, Krimi, Mystery & Thriller", 152, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.4aa9f68b-5e9d-1955-a451-d4d0087ef56c", false, null,
            "Gemeinsam mit Bezirksstaatsanwalt Harvey Dent und Lieutenant James Gordon versucht der im Fledermauskostüm agierende Milliardär Bruce Wayne dem Verbrechen in...",
            "https://images.justwatch.com/poster/304822991/s332/The-Dark-Knight.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0095327", "Die letzten Glühwürmchen", 1988, 8.5, 321469,
            100, 92.5,
            "Animation, Drama, Krieg & Militär", 89, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/557010", true, "Nur noch 6 Tage im Stream!",
            "Japan, 1945. Das Land steht kurz vor der Kapitulation. Täglich werfen amerikanische Flieger Bomben vom Himmel und setzen ganze Landstriche in Brand. Im Infer...",
            "https://images.justwatch.com/poster/249368294/s332/Die-letzten-Gluehwuermchen.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt11989890", "David Attenborough: Mein Leben auf unserem Planeten", 2020, 8.9, 36493,
            96, 92.5,
            "Dokumentationen, Drama", 83, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80216393", false, null,
            "Ein Tierfilmer reflektiert über sein Leben, die Evolution des Lebens auf Erden, das Verschwinden unberührter Orte in der Natur und seine Vision für die Zukunft.",
            "https://images.justwatch.com/poster/210082203/s332/David-Attenborough-A-Life-on-Our-Planet.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt0167260", "Der Herr der Ringe - Die Rückkehr des Königs", 2003, 9.0, 2202248,
            94, 92.0,
            "Action & Abenteuer, Drama, Fantasy", 201, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60004484", false, null,
            "Saurons Heer will Minas Tirith angreifen, die in Felsen errichtete Hauptstadt von Gondor. Angesichts der Bedrohlichkeit des feindlichen Heeres wiegt es umso ...",
            "https://images.justwatch.com/poster/177554906/s332/Der-Herr-der-Ringe-Die-Rueckkehr-des-Koenigs.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0051201", "Zeugin der Anklage", 1958, 8.4, 165324,
            100, 92.0,
            "Krimi, Drama, Mystery & Thriller", 116, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.4ca9f65c-05b6-7662-cb13-c666563f6a14", false, null,
            "Eigentlich ist Sir Wilfrid Robarts gesundheitlich noch gar nicht imstande, Leonard Vole vor einem Londoner Schwurgericht zu verteidigen. Aber der berühmte An...",
            "https://images.justwatch.com/poster/343223129/s332/Zeugin-der-Anklage.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt13524234", "folklore: the long pond studio sessions", 2020, 8.4, 5900,
            100, 92.0,
            "Dokumentationen, Drama, Musik & Musical", 106, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-f60f29fe-2809-4238-ac7b-7f5721ac15cd", false, null,
            "In intimer Konzertatmosphäre performt Taylor Swift alle Songs ihres Bestseller-Albums „folklore“. Taylor filmte dieses einzigartige Event begleitet von ihren...",
            "https://images.justwatch.com/poster/238555949/s332/Taylor-Swift-Folklore-The-Long-Pond-Studio-Sessions.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt33022710", "Ozean mit David Attenborough", 2025, 8.4, 5798,
            100, 92.0,
            "Dokumentationen", 98, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-1d8f1a4a-72dc-4532-a0e0-d35daa00d3a7", false, null,
            "Mit spektakulären Aufnahmen von Korallenriffen, Seetangwäldern und dem offenen Meer zeigt David Attenborough eindrucksvoll, wie entscheidend ein gesunder Oze...",
            "https://images.justwatch.com/poster/331841314/s332/ozean-mit-david-attenborough.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt0167261", "Der Herr der Ringe - Die zwei Türme", 2002, 8.8, 1991880,
            95, 91.5,
            "Action & Abenteuer, Drama, Fantasy", 179, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60004483", false, null,
            "Der Weg der Gefährten hat sich geteilt. Ringträger Frodo und sein treuer Begleiter Sam treffen in den Bergen von Emyn Muil auf Gollum, dem einst Bilbo Beutli...",
            "https://images.justwatch.com/poster/177505836/s332/Der-Herr-der-Ringe-Die-zwei-Tuerme.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0114709", "Toy Story", 1995, 8.3, 1203922,
            100, 91.5,
            "Animation, Komödien, Fantasy", 81, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-f6174ebf-cb92-453c-a52b-62bb3576e402", false, null,
            "Als Andys Lieblingspuppe hat Woody im Kinderzimmer das Sagen. Kaum ist der Junge nicht da, erwacht die Cowboy-Figur zum Leben, und mit ihm auch all das ander...",
            "https://images.justwatch.com/poster/326048817/s332/Toy-Story.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0022100", "M - Eine Stadt sucht einen Mörder", 1931, 8.3, 184941,
            100, 91.5,
            "Krimi, Drama, Mystery & Thriller", 110, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.30b080a4-e593-4309-9e28-c8035ff6cba8", false, null,
            "Berlin 1931: Ein psychopathischer Kindermörder ängstigt die Bevölkerung, narrt die Polizei und versetzt die Unterwelt in Aufregung: Ständige Razzien und Kont...",
            "https://images.justwatch.com/poster/300965760/s332/M-Eine-Stadt-sucht-einen-Moerder.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt21626284", "Laapataa Ladies", 2024, 8.3, 62615,
            100, 91.5,
            "Komödien, Drama, Romantik", 122, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81760391", true, "Nur noch 5 Tage im Stream!",
            "Im ländlichen Indien des Jahres 2001 werden zwei junge Bräute auf einer Zugfahrt versehentlich vertauscht.  In dem darauffolgenden Chaos treffen sie auf eine...",
            "https://images.justwatch.com/poster/322129290/s332/Laapataa-Ladies.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt5613056", "Piper", 2016, 8.3, 34303,
            100, 91.5,
            "Animation, Kinder & Familie", 7, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-e4dcaf0f-3770-4965-91e6-2977cc1b2926", false, null,
            "Ein kleines Strandläuferküken, das seine Angst vor Wasser überwinden will, schaut sich ein paar hilfreiche Tricks von einem Krebs ab und lernt dabei, später ...",
            "https://images.justwatch.com/poster/278631332/s332/Piper.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt25433734", "Hi Nanna", 2023, 8.3, 27750,
            100, 91.5,
            "Drama, Kinder & Familie, Romantik", 155, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81682028", false, null,
            "Als der Vater der sechsjährigen Mahi ein wichtiges Versprechen bricht, läuft sie von zu Hause weg – und begegnet einer Frau, mit deren Hilfe sie der Wahrheit...",
            "https://images.justwatch.com/poster/311292828/s332/nani30.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt5192124", "Bo Burnham: Make Happy", 2016, 8.3, 16654,
            100, 91.5,
            "Komödien, Musik & Musical", 60, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80106124", false, null,
            "In diesem Stand-up-Special vermittelt Bo Burnham mit einzigartigem Witz, selbstironischem Humor und eigens komponierter Musik seine ganz persönliche Lebensan...",
            "https://images.justwatch.com/poster/310088984/s332/Bo-Burnham-Make-Happy.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt8652818", "Violet Evergarden: Der Film", 2020, 8.3, 15439,
            100, 91.5,
            "Animation, Drama, Fantasy", 140, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81193214", false, null,
            "Einige Jahre sind vergangen, seit der Krieg Verwüstung angerichtet und Narben in den Herzen der Menschen hinterlassen hat. Auch die ehemalige Soldatin Violet...",
            "https://images.justwatch.com/poster/268715269/s332/Violet-Evergarden-Der-Film.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0245429", "Chihiros Reise ins Zauberland", 2001, 8.6, 990193,
            96, 91.0,
            "Animation, Fantasy, Kinder & Familie", 125, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60023642", false, null,
            "Als die kleine Chihiro mit ihren Eltern während eines Ausflugs einen dunklen Tunnel durchquert, ahnt sie noch nicht, was sie auf der anderen Seite erwarten w...",
            "https://images.justwatch.com/poster/302517125/s332/Chihiros-Reise-ins-Zauberland.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt3823690", "Bill Burr: I'm Sorry You Feel That Way", 2014, 8.2, 9666,
            100, 91.0,
            "Komödien", 80, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80004478", false, null,
            "Bill Burr überlebt die Zombie-Apokalypse, erklärt, warum Rom-Coms tollen Sex ruinieren, und erläutert, warum zu viele Umarmungen als Kind sich im späteren Le...",
            "https://images.justwatch.com/poster/191732328/s332/Bill-Burr-Im-Sorry-You-Feel-That-Way.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt26420234", "Music by John Williams", 2024, 8.2, 6409,
            100, 91.0,
            "Dokumentationen, Musik & Musical", 106, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-d8dbdfd3-4943-439f-b05c-9762e4fdbdea", false, null,
            "Seine unvergesslichen und unverzichtbaren Filmkompositionen, die im Laufe seiner jahrzehntelangen Karriere entstanden, gehören zu einigen der beliebtesten Fi...",
            "https://images.justwatch.com/poster/321222798/s332/music-by-john-williams.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt7320560", "Cuba and the Cameraman", 2017, 8.2, 6035,
            100, 91.0,
            "Dokumentationen", 113, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80126449", false, null,
            "Der Emmy-gekürte Filmemacher Jon Alpert porträtiert das Schicksal dreier kubanischer Familien über vier turbulente Jahrzehnte in der kubanischen Geschichte h...",
            "https://images.justwatch.com/poster/30170827/s332/Cuba-and-the-Cameraman.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt1049413", "Oben", 2009, 8.3, 1260425,
            98, 90.5,
            "Action & Abenteuer, Animation, Komödien", 96, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-f820c0a3-e646-4b75-8dd1-87f6d776c32b", true, "Nur noch 4 Tage im Stream!",
            "Carl Fredricksen hat Zeit seines Lebens mit seiner Frau davon geträumt, die Welt zu bereisen, Abenteuer zu erleben und in Südamerika die Paradise-Fälle zu be...",
            "https://images.justwatch.com/poster/300886669/s332/Oben.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0119217", "Good Will Hunting: Der gute Will Hunting", 1997, 8.4, 1249055,
            97, 90.5,
            "Drama, Romantik", 127, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/17405997", false, null,
            "Der aus einfachen Verhältnissen stammende Will Hunting jobbt als Reinigungskraft am Massachusetts Institute of Technology und keiner ahnt, dass in ihm ein ma...",
            "https://images.justwatch.com/poster/427298/s332/Good-Will-Hunting-Der-gute-Will-Hunting.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0266543", "Findet Nemo", 2003, 8.2, 1130601,
            99, 90.5,
            "Action & Abenteuer, Animation, Komödien", 100, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-37b62808-2368-4688-9410-2dcf7461e258", false, null,
            "Im Schutze eines großen Riffs lebt die Familie von Clownfisch Marlin relativ sicher und trotzdem werden seine Frau und nahezu alle seine Kinder Opfer eines g...",
            "https://images.justwatch.com/poster/177870758/s332/Findet-Nemo-3D.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0435761", "Toy Story 3 ", 2010, 8.3, 994247,
            98, 90.5,
            "Animation, Komödien, Fantasy", 103, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-95e7b2ce-5f45-4923-976d-b7e9968a7357", false, null,
            "Der Moment des Abschieds ist gekommen: Andy ist den Kinderschuhen entwachsen und macht sich bereit, aufs College zu gehen. Für sein stets getreues Spielzeug ...",
            "https://images.justwatch.com/poster/230534022/s332/Toy-Story-3.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt4633694", "Spider-Man: A New Universe", 2018, 8.4, 821420,
            97, 90.5,
            "Action & Abenteuer, Animation, Fantasy", 117, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-e4cd7619-7c84-4b4d-acf0-f53ca1b442a3", false, null,
            "Miles Morales wurde von einer genetisch veränderten Spinne gebissen. Das macht ihn schnell zum neuen Spider-Man mit den bekannten Fähigkeiten. Aber Miles ist...",
            "https://images.justwatch.com/poster/345754047/s332/Spider-Man-A-New-Universe.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt2380307", "Coco - Lebendiger als das Leben!", 2017, 8.4, 731554,
            97, 90.5,
            "Animation, Komödien, Drama", 109, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-ce1ccdca-f468-4960-b67c-026b01ba42ab", false, null,
            "Miguel ist zwölf Jahre alt und ein großer Fan von Musik – aber leider hasst seine Schusterfamilie alles, was mit Tönen und Instrumenten zu tun hat. Miguels U...",
            "https://images.justwatch.com/poster/64900634/s332/Coco-2017.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0119488", "L.A. Confidential", 1997, 8.2, 665553,
            99, 90.5,
            "Krimi, Drama, Mystery & Thriller", 138, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.80a9f767-ab86-a7c9-373b-0032fe01bf70", false, null,
            "Sechs Tote, darunter ein Cop: Ein scheinbar sinnloses Blutbad bringt die Polizei von Los Angeles zum Kochen. Die drei Top-Agenten der Stadt, der gerade beför...",
            "https://images.justwatch.com/poster/337913436/s332/LA-Confidential-Jeder-hat-seinen-Preis-1997.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0082096", "Das Boot", 1981, 8.3, 291249,
            98, 90.5,
            "Drama, Historisch, Krieg & Militär", 150, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/319867", false, null,
            "Der Kriegsberichterstatter Werner geht 1941 an Bord von U-96, das den Auftrag hat englische Transportschiffe zu versenken. Werner soll der Heimatfront von de...",
            "https://images.justwatch.com/poster/52586973/s332/Das-Boot.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt8503618", "Hamilton", 2020, 8.3, 118022,
            98, 90.5,
            "Drama, Historisch", 160, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-f7f12d79-cfbe-4eb4-b275-091fd34b6dde", false, null,
            "Die Verfilmung der Broadway-Sensation „Hamilton“ vereint das Beste aus Live-Show, Spielfilm und Streaming in einem mitreißenden Mix aus Hip-Hop, Jazz, R&B un...",
            "https://images.justwatch.com/poster/222778943/s332/Hamilton-2020.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt32820897", "Demon Slayer: Kimetsu no Yaiba Infinity Castle", 2025, 8.3, 88277,
            98, 90.5,
            "Action & Abenteuer, Animation, Fantasy", 155, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/82723106", true, "Nur noch 3 Tage im Stream!",
            "Während die Mitglieder des Dämonentöter-Korps und die Hashira in Vorbereitung auf den bevorstehenden Kampf gegen die Dämonen ein gemeinsames Krafttrainingspr...",
            "https://images.justwatch.com/poster/331745112/s332/demon-slayer-kimetsu-no-yaiba-infinity-castle.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt14544192", "Bo Burnham: Innen", 2021, 8.6, 56050,
            95, 90.5,
            "Komödien, Dokumentationen, Drama", 88, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81289483", false, null,
            "Bo Burnham hat dieses Special – eine Musik-Komödie – im Laufe eines sehr ungewöhnlichen Jahres ganz allein aufgeführt und gedreht.",
            "https://images.justwatch.com/poster/245230451/s332/Bo-Burnham-Inside.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt8465676", "Hannah Gadsby: Nanette", 2018, 8.1, 14138,
            100, 90.5,
            "Komödien, Dokumentationen", 69, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80233611", false, null,
            "Australierin Hannah Gadsby erfindet die Kunst der Comedy neu – durch gelungene Pointen und persönliche Enthüllungen über Geschlecht, Sexualität und Kindheits...",
            "https://images.justwatch.com/poster/69363194/s332/Hannah-Gadsby-Nanette.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt3455224", "Virunga", 2014, 8.1, 12882,
            100, 90.5,
            "Dokumentationen, Krieg & Militär", 100, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80009431", false, null,
            "Der Film beobachtet die französische Journalistin Mélanie Gouby und mehrere Park-Ranger im Virunga-Nationalpark, die versuchen, diesen ältesten Nationalpark ...",
            "https://images.justwatch.com/poster/347125460/s332/Virunga.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt8999372", "Sir Alex Ferguson: Nie aufgeben", 2021, 8.1, 6744,
            100, 90.5,
            "Dokumentationen, Sport", 108, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-62f3f4e2-9579-4d98-9ed0-02d5080f9111", false, null,
            "Eine aufschlussreiche und äußerst persönliche Dokumentation über das Leben von Sir Alex Ferguson, von seinen Wurzeln in der Glasgower Arbeiterklasse bis zu d...",
            "https://images.justwatch.com/poster/329442799/s332/Sir-Alex-Ferguson-Never-Give-In.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt8637504", "Disclosure - Hollywoods Bild von Transgender", 2020, 8.1, 6547,
            100, 90.5,
            "Dokumentationen", 107, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81284247", false, null,
            "In der Dokumentation Disclosure: Trans Lives on Screen wirft Regisseur Sam Feder einen Blick auf Hollywoods Darstellung von Transgender-Personen in den letzt...",
            "https://images.justwatch.com/poster/188951872/s332/Disclosure-Trans-Lives-On-Screen.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt0120737", "Der Herr der Ringe - Die Gefährten", 2001, 8.9, 2246360,
            91, 90.0,
            "Action & Abenteuer, Drama, Fantasy", 179, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60004480", false, null,
            "Dem Hobbit Bilbo Beutlin fällt zufällig ein machtvoller Ring in die kleinen Hände. Dieses Schmuckstück erlaubte es dem finsteren Herrscher Sauron einst, die ...",
            "https://images.justwatch.com/poster/177544729/s332/Der-Herr-der-Ringe-Die-Gefaehrten.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0080684", "Das Imperium schlägt zurück", 1980, 8.7, 1529752,
            93, 90.0,
            "Action & Abenteuer, Fantasy, Science-Fiction", 124, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-0f5c5223-f4f6-46ef-ba8a-69cb0e17d8d3", false, null,
            "Nach der Zerstörung des Todessterns geht der Kampf zwischen den Rebellen und dem herrschenden Imperium weiter. Die Rebellen haben sich auf den Eisplaneten Ho...",
            "https://images.justwatch.com/poster/245126224/s332/Star-Wars-Episode-V-Das-Imperium-schlaegt-zurueck.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0253474", "Der Pianist", 2002, 8.5, 1029223,
            95, 90.0,
            "Drama, Musik & Musical, Krieg & Militär", 150, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60025061", false, null,
            "Warschau, 1939. Die Besetzung Polens durch die Deutschen bedeutet für den gefeierten polnisch-jüdischen Pianisten Wladyslaw Szpilman den Beginn eines langen ...",
            "https://images.justwatch.com/poster/102327271/s332/Der-Pianist.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0047296", "Die Faust im Nacken", 1954, 8.1, 181912,
            99, 90.0,
            "Krimi, Drama, Mystery & Thriller", 108, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60000711", true, "Nur noch 2 Tage im Stream!",
            "Marlon Brando spielt die Hauptrolle des Terry Malloy, einem gescheiterten Preisboxer, der dank der Beziehungen seines Bruders Charley, ein Anwalt für eine ko...",
            "https://images.justwatch.com/poster/327551565/s332/Die-Faust-im-Nacken.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt2576852", "Die Legende der Prinzessin Kaguya", 2013, 8.0, 63446,
            100, 90.0,
            "Animation, Drama, Fantasy", 137, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80013552", false, null,
            "Ein armer Bambussammler findet eines Tages in einem leuchtenden Bambushalm ein winziges Mädchen, nicht größer als ein Däumling, das er mit nach Hause nimmt. ...",
            "https://images.justwatch.com/poster/249368324/s332/Die-Legende-der-Prinzessin-Kaguya.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt6333054", "Chasing Coral", 2017, 8.0, 6008,
            100, 90.0,
            "Dokumentationen", 93, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80168188", false, null,
            "Taucher, Wissenschaftler und Fotografen weltweit dokumentieren auf einer legendären Unterwassermission das Sterben der Korallenriffe.",
            "https://images.justwatch.com/poster/244512173/s332/Chasing-Coral.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt0910970", "WALL·E - Der Letzte räumt die Erde auf", 2008, 8.4, 1346630,
            95, 89.5,
            "Animation, Kinder & Familie, Science-Fiction", 96, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-280395a4-d5ef-4dd0-bd09-d91c31593d3d", false, null,
            "Wall·E ist ein kleiner Haushaltsroboter, der klaglos seinen Dienst verrichtet und Müll zusammenpresst, um ihn zu Wolkenkratzern aufzustapeln. Denn der kleine...",
            "https://images.justwatch.com/poster/176265731/s332/WALL-E-Der-Letzte-raeumt-die-Erde-auf.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0042876", "Rashomon - Das Lustwäldchen", 1950, 8.1, 199327,
            98, 89.5,
            "Krimi, Drama, Mystery & Thriller", 88, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.56b8a544-ef0e-482b-adfc-789f7b6b37b5", false, null,
            "Drei Menschen suchen vor einem Wolkenbruch in einer Tempelruine Schutz: ein Zen-Priester, ein Holzfäller und ein Knecht. Ihr Gespräch kreist um ein grausiges...",
            "https://images.justwatch.com/poster/63285549/s332/Rashomon-Das-Lustwaeldchen.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt5895028", "Der 13.", 2016, 8.2, 39704,
            97, 89.5,
            "Krimi, Dokumentationen, Historisch", 100, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80091741", false, null,
            "Ein tiefer Einblick in das Gefängnissystem der Vereinigten Staaten von Amerika und wie es die Rassenungleichheit der Nation zeigt.",
            "https://images.justwatch.com/poster/319218033/s332/The-13th.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt9098872", "The Rescue: Das Höhlenunglück in Thailand", 2021, 8.3, 21279,
            96, 89.5,
            "Action & Abenteuer, Dokumentationen, Drama", 107, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-1b136cb2-58f7-4bb1-b15f-e33908201352", false, null,
            "In ihrem Dokumentarfilm schildern die Filmemacher Jimmy Chin und Elizabeth Chai Vasarhelyi die packende Geschichte, die 2018 die Welt in Atem hielt: die wagh...",
            "https://images.justwatch.com/poster/256791710/s332/The-Rescue-2021.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt0088763", "Zurück in die Zukunft", 1985, 8.5, 1483063,
            93, 89.0,
            "Komödien, Science-Fiction", 116, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.30a9f73b-becf-e11e-b945-01fc05bd6430", false, null,
            "Der Teenager Marty McFly wird mit der Zeitmaschine seines Freundes, dem skurrilen Wissenschaftler Dr. Emmett Brown, versehentlich ins Jahr 1955 geschleudert....",
            "https://images.justwatch.com/poster/260046581/s332/Zurueck-in-die-Zukunft.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt4154796", "Avengers 4: Endgame", 2019, 8.4, 1300272,
            94, 89.0,
            "Action & Abenteuer, Drama, Science-Fiction", 182, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-b39aa962-be56-4b09-a536-98617031717f", false, null,
            "Thanos hat also tatsächlich Wort gehalten, seinen Plan in die Tat umgesetzt und die Hälfte allen Lebens im Universum ausgelöscht. Die Avengers? Machtlos. Iro...",
            "https://images.justwatch.com/poster/143482309/s332/Avengers-Endgame.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1392190", "Mad Max: Fury Road", 2015, 8.1, 1224799,
            97, 89.0,
            "Action & Abenteuer, Science-Fiction", 121, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.0c9da1a1-af0f-41d7-be82-0bb2fd6e7f32", true, "Nur noch 6 Tage im Stream!",
            "In einem postapokalyptischen Ödland hilft Max einer Rebellin und einer Gruppe entkommener Frauen auf der Flucht vor der Tyrannei und bei der Rückkehr in ihre...",
            "https://images.justwatch.com/poster/751444/s332/Mad-Max-Fury-Road.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0073195", "Der weiße Hai", 1975, 8.1, 752520,
            97, 89.0,
            "Drama, Horror, Mystery & Thriller", 125, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.4ea9f78d-717c-d675-f5d5-2163986b8c3a", false, null,
            "Ein weißer Hai terrorisiert eine Badeinsel an der amerikanischen Atlantikküste. Besonnene Einheimische unter Führung von Polizeichef Brody wollen alle Stränd...",
            "https://images.justwatch.com/poster/190430717/s332/Der-weisse-Hai.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0089881", "Ran", 1985, 8.2, 155413,
            96, 89.0,
            "Action & Abenteuer, Drama, Historisch", 160, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.d0cef792-33db-4761-974d-bcb83cc6949a", false, null,
            "Nach langen Jahren der Regentschaft beschließt Fürst Hidetora, sich zurückzuziehen. Sein riesiges Reich teilt er unter seinen drei Söhnen auf. Doch schon bal...",
            "https://images.justwatch.com/poster/316698879/s332/Ran.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0055630", "Yojimbo - Der Leibwächter", 1961, 8.2, 145186,
            96, 89.0,
            "Action & Abenteuer, Drama, Mystery & Thriller", 110, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.514e6a84-88ee-463f-ad78-b8593d73579e", false, null,
            "Der Samurai Sanjuro gerät in einem japanischen Bergdorf in einen Krieg zwischen zwei Familien. Das macht er sich zu Nutze, indem er mal hier und mal dort Par...",
            "https://images.justwatch.com/poster/8682065/s332/Yojimbo-Der-Leibwaechter.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt7775622", "Free Solo", 2018, 8.1, 89057,
            97, 89.0,
            "Action & Abenteuer, Dokumentationen, Sport", 100, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-5bc87ba3-cda0-4f46-b81c-4c3ea170e9d1", false, null,
            "Free-Solo-Kletterer Alex Honnold bereitet sich im Sommer 2017 auf die Erfüllung seines Lebenstraums vor. Er will den bekanntesten Felsen der Welt erklimmen,...",
            "https://images.justwatch.com/poster/124054476/s332/Free-Solo.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt10280296", "Sardar Udham", 2021, 8.3, 51498,
            95, 89.0,
            "Krimi, Drama, Historisch", 162, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.e3418f6a-f850-4b8c-b61f-035e9a4171b2", false, null,
            "„Sardar Udham“ ist ein biografischer Krimi, der die Geschichte des indischen Titelrevolutionärs erzählt, der dafür bekannt ist, den Tod unschuldiger Indianer...",
            "https://images.justwatch.com/poster/252160161/s332/Sardar-Udham.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt19811010", "Das fantastische Leben des Ibelin", 2024, 8.1, 15586,
            97, 89.0,
            "Animation, Dokumentationen", 103, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81759420", false, null,
            "Nach dem Tod eines „World of Warcraft“-Spielers wird sein geheimes Leben lebendig nachgestellt, als sich sein Online-Freundeskreis bei seiner Familie meldet.",
            "https://images.justwatch.com/poster/321636785/s332/ibelin.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt0088275", "Wer war Harvey Milk?", 1984, 8.2, 7319,
            96, 89.0,
            "Dokumentationen, Historisch", 90, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.dd207136-59c8-41d9-8584-bce64110331b", false, null,
            "San Francisco in den 70er Jahren: Rund um die Castro Street entsteht die erste von Schwulen und Lesben geprägte Nachbarschaft der USA. Zum Symbol für das wac...",
            "https://images.justwatch.com/poster/332501149/s332/The-Times-of-Harvey-Milk.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt37969426", "Die Muppet Show", 2026, 8.0, 6876,
            98, 89.0,
            "Komödien, Kinder & Familie, Musik & Musical", 32, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-e7487044-477c-4a57-ae45-afc689f2a346", false, null,
            "Hier ist „Die Muppet Show“! Kermit, Miss Piggy und die beliebten Muppets melden sich mit einem brandneuen Special zurück. Musik, Comedy und jede Menge Chaos ...",
            "https://images.justwatch.com/poster/341566675/s332/the-muppet-show.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0110357", "Der König der Löwen", 1994, 8.5, 1282555,
            92, 88.5,
            "Action & Abenteuer, Animation, Drama", 89, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-a3ae7371-39a5-4c0b-a1f2-29a70b372848", true, "Nur noch 5 Tage im Stream!",
            "Unter der Obhut seines Vaters Mufasa wächst der Löwenjunge Simba unbeschwert heran und soll eines Tages dessen Platz als König einnehmen. Aber bis dahin ist ...",
            "https://images.justwatch.com/poster/71872948/s332/Der-Koenig-der-Loewen.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0198781", "Die Monster AG", 2001, 8.1, 1095333,
            96, 88.5,
            "Animation, Komödien, Fantasy", 92, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-3c90b85f-ba5e-4351-be87-e625d5706952", false, null,
            "In der Monster-AG-Fabrik gehen die Bösewichte eifrig ihrer Arbeit nach: Über Schranktüren schleichen sie sich in Kinderzimmer ein und sammeln die Angstschrei...",
            "https://images.justwatch.com/poster/177286113/s332/Die-Monster-AG.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0078748", "Alien – Das unheimliche Wesen aus einer fremden Welt", 1979, 8.4, 1080531,
            93, 88.5,
            "Horror, Science-Fiction", 117, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-27389b6b-bf27-45a6-afdf-cef0fe723cff", false, null,
            "Die Besatzung des Weltraumfrachters Nostromo - darunter der dritte Offizier Ellen Ripley - befindet sich im künstlichen Tiefschlaf. Plötzlich empfängt ihr Sc...",
            "https://images.justwatch.com/poster/315640392/s332/Alien.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0382932", "Ratatouille", 2007, 8.1, 970520,
            96, 88.5,
            "Animation, Komödien, Fantasy", 111, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-ab7c4e29-04f1-46dd-931a-d43be1ce0f8c", false, null,
            "Der kleine Remy träumt davon, ein berühmter Chefkoch zu werden. Dabei hat er nicht nur mit den Vorurteilen seiner Familie zu kämpfen, sondern auch mit dem of...",
            "https://images.justwatch.com/poster/230068591/s332/Ratatouille.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0090605", "Aliens - Die Rückkehr", 1986, 8.4, 850837,
            93, 88.5,
            "Action & Abenteuer, Mystery & Thriller, Science-Fiction", 137, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-b2ae8903-e3c5-4ae1-a9f7-885c04985f96", false, null,
            "Nach der ersten Begegnung Ripleys mit dem Alien sind 57 Jahre vergangen. Ziellos treibt sie sich immer noch im Kälteschlaf befindlich durchs All. Zufällig wi...",
            "https://images.justwatch.com/poster/141187752/s332/Aliens-Die-Rueckkehr.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt12042730", "Der Astronaut - Project Hail Mary", 2026, 8.2, 581731,
            95, 88.5,
            "Action & Abenteuer, Komödien, Science-Fiction", 157, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.414eb1af-ee27-476c-bc46-bedd48595f59", false, null,
            "Als Ryland Grace erwacht, muss er feststellen, dass er ganz allein ist. Er ist anscheinend der einzige Überlebende einer Raumfahrtmission, Millionen Kilomete...",
            "https://images.justwatch.com/poster/355132102/s332/der-astronaut.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0050212", "Die Brücke am Kwai", 1957, 8.1, 252209,
            96, 88.5,
            "Drama, Krieg & Militär", 156, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60002970", false, null,
            "Britische Kriegsgefangene der Japaner und ihr Kommandant, Colonel Nicholson werden gezwungen, eine Eisenbahnbrücke über den River Kwai zu bauen. Gegen die un...",
            "https://images.justwatch.com/poster/300609472/s332/Die-Bruecke-am-Kwai.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt4729430", "Klaus", 2019, 8.2, 243882,
            95, 88.5,
            "Animation, Komödien, Fantasy", 97, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80183187", false, null,
            "Ein eigennütziger Postbote und ein eigenbrötlerischer Spielzeugmacher freunden sich an und bescheren einem kalten, dunklen Dorf die Freude, die es so bitter ...",
            "https://images.justwatch.com/poster/338913234/s332/Klaus.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0092067", "Das Schloss im Himmel", 1986, 8.0, 201336,
            97, 88.5,
            "Action & Abenteuer, Animation, Fantasy", 125, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60027393", false, null,
            "Unvorstellbarer Reichtum und eine geheimnisvolle Technologie, mit der sich die Welt beherrschen lässt. All dies soll man finden können auf Laputa, dem sagenu...",
            "https://images.justwatch.com/poster/249341203/s332/Das-Schloss-im-Himmel.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt2125608", "Searching for Sugar Man - Die unglaubliche Geschichte des Sixto Rodriguez", 2012, 8.2, 77055,
            95, 88.5,
            "Dokumentationen, Musik & Musical, Produziert in Europa", 86, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.4979c844-6a51-4b9e-9da8-9f85fa602752", true, "Nur noch 4 Tage im Stream!",
            "Sixto Rodriguez ... noch nie gehört? Dabei war der mexikanischstämmige Singer-Songwriter einst gefeiert wie Bob Dylan und Elvis Presley zusammen – allerdings...",
            "https://images.justwatch.com/poster/241756150/s332/Searching-for-Sugar-Man.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt0120382", "Die Truman Show", 1998, 8.2, 1414042,
            94, 88.0,
            "Komödien, Drama", 103, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/11819086", false, null,
            "Die Truman Show ist seit beinahe dreißig Jahren ein TV-Klassiker auf der ganzen Welt. Seit seiner Geburt ist Truman Burbank der Hauptdarsteller der Serie – o...",
            "https://images.justwatch.com/poster/341633361/s332/Die-Truman-Show.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0095016", "Stirb langsam", 1988, 8.2, 1042742,
            94, 88.0,
            "Action & Abenteuer, Mystery & Thriller", 132, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-efc8250c-aeed-4ab7-a492-07064c826bc1", false, null,
            "Eigentlich möchte der New Yorker Polizist John McClane dieses Weihnachten nur seine Noch-Ehefrau Holly, welche in Los Angeles in einer großen, erfolgreichen ...",
            "https://images.justwatch.com/poster/176814704/s332/Stirb-langsam.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt15239678", "Dune: Part Two", 2024, 8.4, 800517,
            92, 88.0,
            "Action & Abenteuer, Drama, Science-Fiction", 167, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81602836", false, null,
            "Paul Atreides begibt sich mithilfe von Chani und den Fremen auf einen Rachefeldzug gegen die Verschwörer, die seine Familie vernichtet haben. Der junge Paul ...",
            "https://images.justwatch.com/poster/335919365/s332/Dune-Part-Two.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0119698", "Prinzessin Mononoke", 1997, 8.3, 443134,
            93, 88.0,
            "Action & Abenteuer, Animation, Fantasy", 133, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/28630857", false, null,
            "Japan im frühen Mittelalter. Der junge Krieger Ashitaka tötet in Notwehr einen dämonischen Eber und wird darauf mit einem Fluch belegt. Auf der Suche nach He...",
            "https://images.justwatch.com/poster/58411299/s332/Prinzessin-Mononoke.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0057115", "Gesprengte Ketten", 1963, 8.2, 280950,
            94, 88.0,
            "Drama, Mystery & Thriller, Krieg & Militär", 168, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.bd3bb3ec-1f99-4558-ad2e-95d6c3350db3", false, null,
            "Mitten im Zweiten Weltkrieg planen einige hundert Kriegsgefangene aus den USA und England die Flucht aus einem deutschen Kriegsgefangenenlager. Ihr Vorhaben ...",
            "https://images.justwatch.com/poster/188395713/s332/Gesprengte-Ketten.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0101414", "Die Schöne und das Biest", 1991, 8.0, 510703,
            95, 87.5,
            "Animation, Fantasy, Kinder & Familie", 84, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-97babebc-7013-455a-b377-aa3d7a6e79c1", false, null,
            "Eine Fee verwandelt einen bösen Prinzen in ein “Biest”, und alle Schloßbewohner in diverse Haushaltsgegenstände. Erst wenn sich ein Mädchen in den verzaubert...",
            "https://images.justwatch.com/poster/48052969/s332/Die-Schoene-und-das-Biest-1991.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0096283", "Mein Nachbar Totoro", 1988, 8.1, 441842,
            94, 87.5,
            "Animation, Komödien, Fantasy", 86, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60032294", false, null,
            "Die 8-jährige Satsuki und ihre kleine Schwester Mei ziehen mit ihrem Vater, einem Universitätsprofessor, auf das Land. Sie wollen in der Nähe ihrer Mutter se...",
            "https://images.justwatch.com/poster/302447277/s332/Mein-Nachbar-Totoro.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt2106476", "Die Jagd", 2012, 8.3, 421037,
            92, 87.5,
            "Drama, Produziert in Europa", 116, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.96e9485b-c956-4b5d-b0a6-0c1ec5f675e3", false, null,
            "Lucas musste einige Schicksalsschläge hinnehmen, doch jetzt geht es in seinem Leben langsam wieder bergauf. Er hat eine neue Stelle in einem Kindergarten gef...",
            "https://images.justwatch.com/poster/252037410/s332/Die-Jagd.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt23289160", "Godzilla Minus One", 2023, 7.6, 233238,
            99, 87.5,
            "Action & Abenteuer, Drama, Horror", 125, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81767635", true, "Nur noch 3 Tage im Stream!",
            "Japan, 1945: Kurz nach dem Ende des Zweiten Weltkriegs täuscht der Kamikaze-Pilot Koichi Shishima technische Probleme bei seinem Flugzeug vor und landet auf ...",
            "https://images.justwatch.com/poster/333251143/s332/godzilla-minus-one.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt34365591", "Mahavatar Narsimha", 2025, 8.5, 49142,
            90, 87.5,
            "Action & Abenteuer, Animation, Drama", 130, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/82131939", false, null,
            "Hiranyakashyap, der tyrannische Dämon, fordert Vishnu heraus und erklärt sich selbst zum Gott. Sein Sohn Prahlad bleibt jedoch Vishnu treu, der als Narsimha ...",
            "https://images.justwatch.com/poster/333848378/s332/mahavatar-narsimha.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt26758372", "Meiyazhagan", 2024, 8.5, 24661,
            90, 87.5,
            "Drama, Kinder & Familie", 177, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81713677", false, null,
            "Ein Mann reist nach 22 Jahren zurück in seine Heimatstadt, wo er einen beharrlichen, aber gutherzigen Verwandten trifft, dessen Namen er vergessen hat.",
            "https://images.justwatch.com/poster/321819302/s332/meiyazhagan.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt22010428", "BTS: PERMISSION TO DANCE ON STAGE - LA", 2022, 8.5, 6609,
            90, 87.5,
            "Dokumentationen, Musik & Musical", 130, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-efb001f4-3aac-43ad-a7de-e1547dfc640f", false, null,
            "Ende 2021 trat die koreanische Band BTS im SoFi-Stadion in Los Angeles auf. Dabei performten sie natürlich auch ihre größten Hits „Dynamite“, „Butter“ und „P...",
            "https://images.justwatch.com/poster/300639478/s332/BTS-Permission-to-Dance-On-Stage-LA.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt10872600", "Spider-Man: No Way Home", 2021, 8.1, 1082879,
            93, 87.0,
            "Action & Abenteuer, Fantasy, Science-Fiction", 148, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-295fb45a-c8bb-477e-816f-b0706b4d9984", false, null,
            "Peter Parker ist demaskiert und kann sein normales Leben nicht mehr von den hohen Einsätzen als Superheld trennen. Als er Doctor Strange um Hilfe bittet, wir...",
            "https://images.justwatch.com/poster/257194135/s332/Spider-Man-No-Way-Home.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt3315342", "Logan - The Wolverine", 2017, 8.1, 944369,
            93, 87.0,
            "Action & Abenteuer, Drama, Mystery & Thriller", 137, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-7c1743ec-7cd6-4da4-9e8c-b713ad4e634a", false, null,
            "In naher Zukunft schützt ein abgekämpfter Logan einen gebrochenen Professor X in einem Versteck nahe der mexikanischen Grenze. Doch Logans Versuche, sich vor...",
            "https://images.justwatch.com/poster/10465299/s332/Logan.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1285016", "The Social Network", 2010, 7.8, 833114,
            96, 87.0,
            "Drama", 121, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.bca9f7a2-28a5-0078-5aa9-a7e7e3286e37", false, null,
            "Harvard Student Mark Zuckerberg ist alles andere als sozial: der schüchterne, außergewöhnlich intelligente Computernerd hat außer seinem Kommilitonen Eduardo...",
            "https://images.justwatch.com/poster/8716701/s332/The-Social-Network.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0381061", "James Bond 007 - Casino Royale", 2006, 8.0, 756833,
            94, 87.0,
            "Action & Abenteuer, Mystery & Thriller, Produziert in Europa", 144, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.67d67132-66a8-46fb-9b4d-3ebe25602945", false, null,
            "Sein erster Auftrag, nachdem er die Lizenz zum Töten erhalten hat, führt den MI6-Agenten James Bond nach Madagaskar, wo er auf den Terroristen Mollaka angese...",
            "https://images.justwatch.com/poster/39421213/s332/James-Bond-007-Casino-Royale.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0075148", "Rocky", 1976, 8.1, 704752,
            93, 87.0,
            "Drama, Sport", 115, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.b051ef6a-6336-4a7a-a2aa-39e4ed092974", false, null,
            "Rocky Balboa ist ein begnadeter Boxer. Doch der Weg zum Ruhm und dem großen Geld blieb ihm immer verwehrt. Als armer Geldeintreiber muss er ein schäbiges Das...",
            "https://images.justwatch.com/poster/174243/s332/Rocky.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt12888462", "Mein Lehrer, der Krake", 2020, 8.1, 72961,
            93, 87.0,
            "Dokumentationen, Drama", 85, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81045007", false, null,
            "Acht Jahre lang hat der Freitaucher Craig Foster die seltene Tierwelt vor der Küste Südafrikas erforscht.  Seine täglichen Beobachtungen eines wilden Tintenf...",
            "https://images.justwatch.com/poster/307614463/s332/My-Octopus-Teacher.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt0053946", "Wer den Wind sät", 1960, 8.1, 36076,
            93, 87.0,
            "Drama, Historisch", 128, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.f358c256-3c90-4652-9220-b78ebeedc70f", false, null,
            "Der Chicagoer Anwalt Henry Drummond und der bibeltreue Volksheld Matthew Harrison Brady liefern sich einen packenden Kampf der Ideologien. Auslöser ist der a...",
            "https://images.justwatch.com/poster/173842178/s332/Wer-den-Wind-saet.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt2278388", "Grand Budapest Hotel", 2014, 8.1, 990070,
            92, 86.5,
            "Komödien, Krimi, Drama", 100, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.efbd217f-438b-486c-910f-3a8c122fdf95", false, null,
            "Gustave H. ist der legendäre Concierge eines Luxushotels in Europa. In den 20er Jahren des 20. Jahrhunderts baut er eine enge Freundschaft zu einem jungen Ho...",
            "https://images.justwatch.com/poster/300636763/s332/Grand-Budapest-Hotel.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0796366", "Star Trek - Die Zukunft hat begonnen", 2009, 7.9, 642195,
            94, 86.5,
            "Action & Abenteuer, Science-Fiction", 127, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/70101276", false, null,
            "Wir schreiben das 23. Jahrhundert. Der junge James Tiberius Kirk ist ein Draufgänger, der keiner Kneipenschlägerei aus den Weg geht und bevorzugt mit dem Kop...",
            "https://images.justwatch.com/poster/324531210/s332/Star-Trek-XI.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1950186", "Le Mans 66 - Gegen jede Chance", 2019, 8.1, 495749,
            92, 86.5,
            "Action & Abenteuer, Drama, Historisch", 153, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-b0e9e97b-402a-452d-979e-95714b91e480", false, null,
            "Der Film basiert auf der wahren Geschichte des visionären Sportwagenherstellers Carroll Shelby und des furchtlosen Rennfahrers Ken Miles. Gemeinsam kämpfen s...",
            "https://images.justwatch.com/poster/166717141/s332/Le-Mans-66-Gegen-jede-Chance.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt8267604", "Capernaum - Stadt der Hoffnung", 2018, 8.4, 127614,
            89, 86.5,
            "Drama", 126, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.083da5a9-8e8c-483d-80d4-191562df41c5", false, null,
            "Der zwölfjährige Zain ist nicht auf den Mund gefallen. Statt wie andere Kinder zur Schule zu gehen, muss Zain – als Sohn einer kinderreichen Familie aus ärms...",
            "https://images.justwatch.com/poster/130753381/s332/Capernaum.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1954470", "Gangs of Wasseypur - Teil 2", 2012, 8.2, 116019,
            91, 86.5,
            "Action & Abenteuer, Komödien, Krimi", 160, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.f45297d3-41af-4951-b423-1730ce92b2bf", false, null,
            "Die Clans von Shahid Khan und Ramadhir Singh bekriegen sich weiter in der Region von Wasseypur. Und jetzt liegt es an Shahids Enkel, Faizal, die Spitze des C...",
            "https://images.justwatch.com/poster/290010600/s332/Gangs-of-Wasseypur-Teil-2.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt0017925", "Der General", 1926, 8.1, 106554,
            92, 86.5,
            "Action & Abenteuer, Komödien, Drama", 67, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.bb0599fa-ea74-474d-a4fc-2f5299cad7c1", false, null,
            "Der General ist eine US-amerikanische Stummfilmkomödie von Buster Keaton aus dem Jahr 1927, der zwar seinerzeit floppte, heute jedoch als Höhepunkt der Stumm...",
            "https://images.justwatch.com/poster/354603839/s332/Der-General.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0048021", "Rififi", 1955, 8.1, 39832,
            92, 86.5,
            "Krimi, Drama, Mystery & Thriller", 118, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.9c6a9cf8-835f-4f18-a083-cb3001a02cc8", false, null,
            "Vier geübte und spezialisierte Verbrecher sind in Paris unterwegs. Darunter ist auch der bis vor Kurzem inhaftierte Toni. Die vier Diebe rauben ein Juwelierg...",
            "https://images.justwatch.com/poster/164560698/s332/Rififi.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt5074352", "Dangal - Die Hoffnung auf den großen Sieg", 2016, 8.3, 249347,
            89, 86.0,
            "Action & Abenteuer, Drama, Sport", 161, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80166185", false, null,
            "Mahavir ist leidenschaftlicher Ringer, und auf dem Weg nach ganz oben. Sein Vater zwingt ihn jedoch, den Kampfsport aufzugeben. Also will Mahavir später sein...",
            "https://images.justwatch.com/poster/244662457/s332/dNgl.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt17009710", "Anatomie eines Falls", 2023, 7.6, 239327,
            96, 86.0,
            "Krimi, Drama, Mystery & Thriller", 145, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81713297", false, null,
            "Sandra, Samuel und ihr elfjähriger sehbehinderter Sohn Daniel leben seit einem Jahr weit weg von jeglicher Zivilisation in den Bergen. Eines Tages wird Samue...",
            "https://images.justwatch.com/poster/310513510/s332/anatomie-dune-chute.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt3863552", "Bajrangi Bhaijaan", 2015, 8.1, 114256,
            91, 86.0,
            "Komödien, Drama, Musik & Musical", 159, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80065328", false, null,
            "Ein junges stummes Mädchen aus Pakistan verliert sich in Indien, ohne die Möglichkeit, zurückzukehren. Ein hingebungsvoller Mann mit einem großherzigen Geist...",
            "https://images.justwatch.com/poster/339073421/s332/bajrangi-bhaijaan.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt4908644", "Winter on Fire: Ukraine's Fight for Freedom", 2015, 8.3, 21636,
            89, 86.0,
            "Dokumentationen, Historisch", 98, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80031666", false, null,
            "Was als friedliche Studentendemos in der Ukraine begann, entwickelte sich im Verlauf von 93 Tagen zu einer gewaltsamen Revolution und umfassenden Bürgerrecht...",
            "https://images.justwatch.com/poster/269451656/s332/Winter-on-Fire-Ukraines-Fight-for-Freedom.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt37895011", "Rao Bahadur", 2026, 8.4, 7358,
            88, 86.0,
            "Drama, Horror, Mystery & Thriller", 145, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/82787446", false, null,
            "Set against the backdrop of a fading aristocracy, Made in Telugu, for the world. Remember: Doubt is a Demon.",
            "https://images.justwatch.com/poster/341437868/s332/rao-bahadur.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt1345836", "The Dark Knight Rises", 2012, 8.4, 2033990,
            87, 85.5,
            "Krimi, Mystery & Thriller", 164, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.1ea9f6f7-7781-df4e-60bf-d24b8365b375", false, null,
            "Nach dem Tod des Staatsanwalts Harvey Dent alias Two-Face nahm Batman die Schuld von dessen Verbrechen auf sich, um seinen Ruf zu schützen. Daher wurde er vo...",
            "https://images.justwatch.com/poster/322593149/s332/Batman-The-Dark-Knight-Rise.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0848228", "Marvel's The Avengers", 2012, 8.0, 1588597,
            91, 85.5,
            "Action & Abenteuer, Science-Fiction", 143, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-d2ab699b-67da-4906-a7a4-5bc542c953cf", false, null,
            "Nick Fury ist der Anführer der Organisation S.H.I.E.L.D., einer internationalen Friedensorganisation. Die Behörde ist ein Who's Who der Marvel Superhelden, m...",
            "https://images.justwatch.com/poster/339002193/s332/Marvels-The-Avengers.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt3659388", "Der Marsianer - Rettet Mark Watney", 2015, 8.0, 1034156,
            91, 85.5,
            "Drama, Science-Fiction", 142, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.26a9f717-f71e-b58b-6a4a-7a62543eab90", false, null,
            "Die NASA treibt mit der „Ares III“-Mission die Erforschung des Mars voran. Die Astronauten Mark Watney, Commander Lewis, Rick Martinez, Chris Beck, Alex Voge...",
            "https://images.justwatch.com/poster/178407326/s332/Der-Marsianer-Rettet-Mark-Watney.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0088247", "Terminator", 1984, 8.1, 1021984,
            90, 85.5,
            "Action & Abenteuer, Mystery & Thriller, Science-Fiction", 108, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.28a714d9-9c98-4a7b-ab30-9613fee0e89f", false, null,
            "Ein Cyborg aus der Zukunft wird auf eine tödliche Mission in die heutige Gegenwart geschickt. Er soll Sarah Connor töten, eine junge Frau, die eine entscheid...",
            "https://images.justwatch.com/poster/163097943/s332/Terminator.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt3783958", "La La Land", 2016, 8.0, 782188,
            91, 85.5,
            "Komödien, Drama, Musik & Musical", 129, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.a2b315ef-feed-47dd-9fd2-41e50afbb891", false, null,
            "Mia ist eine leidenschaftliche Schauspielerin, die ihr Glück in Los Angeles sucht. Sebastian will dort ebenfalls seinen Durchbruch schaffen, allerdings nicht...",
            "https://images.justwatch.com/poster/8863378/s332/La-La-Land-2016.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0780504", "Drive", 2011, 7.8, 776901,
            93, 85.5,
            "Action & Abenteuer, Krimi, Drama", 100, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.a2a9f73b-b849-bf2e-e971-43b3b4410167", false, null,
            "Tagsüber arbeitet 'Driver' unauffällig als Stuntfahrer in Hollywood, abends holt er sich seinen Kick, indem er Aufträge für die Mafia erledigt und sein Kön...",
            "https://images.justwatch.com/poster/8646885/s332/Drive-2011.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt6644200", "A Quiet Place", 2018, 7.5, 672007,
            96, 85.5,
            "Drama, Horror, Science-Fiction", 90, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80213226", false, null,
            "Die Welt ist von rätselhaften, scheinbar unverwundbaren Kreaturen eingenommen worden, die durch jedes noch so kleine Geräusch angelockt werden und sich auf d...",
            "https://images.justwatch.com/poster/246462340/s332/A-Quiet-Place.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt5027774", "Three Billboards Outside Ebbing, Missouri", 2017, 8.1, 618331,
            90, 85.5,
            "Komödien, Krimi, Drama", 115, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-8251c5a0-0cfb-425e-aefd-5bedbe5cb641", false, null,
            "Die Tochter von Mildred Hayes wurde vor Monaten ganz in der Nähe ihres Zuhauses vergewaltigt und ermordet, aber noch immer tut sich in dem Fall nichts. Von e...",
            "https://images.justwatch.com/poster/335814477/s332/Three-Billboards-Outside-Ebbing-Missouri.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0098258", "Teen Lover", 1989, 7.3, 104817,
            98, 85.5,
            "Komödien, Drama, Romantik", 100, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60022318", false, null,
            "Der 19jährige Lloyd, ein mittelmäßiger Schüler ohne berufliche Ambitionen, verliebt sich ausgerechnet in die wohlbehütete Diane, der alle Türen zu einer groß...",
            "https://images.justwatch.com/poster/321801028/s332/Teen-Lover.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1562872", "Man lebt nur einmal", 2011, 8.2, 97962,
            89, 85.5,
            "Komödien, Drama, Romantik", 159, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/70202336", false, null,
            "Kabir und Natasha wollen heiraten. Doch weil man vielleicht nur einmal lebt, will Kabir vorher noch mit seinen beiden besten Freunden eine Junggesellenreise ...",
            "https://images.justwatch.com/poster/315260027/s332/Zindagi-Na-Milegi-Dobara-Man-lebt-nur-einmal.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt0871510", "Chak De! India - Ein unschlagbares Team", 2007, 8.1, 92989,
            90, 85.5,
            "Drama, Kinder & Familie, Sport", 148, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/70077853", false, null,
            "Kabir Khan war Spieler einer indischen Hockey-Nationalmannschaft. Im Finale gegen Pakistan verschoss er den entscheidenen Strafstoß und wurde seitdem als Vat...",
            "https://images.justwatch.com/poster/320761469/s332/Chak-De-India.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt0083658", "Der Blade Runner", 1982, 8.1, 894502,
            89, 85.0,
            "Action & Abenteuer, Drama, Mystery & Thriller", 118, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.98a9f73a-f078-b2ed-17d1-91d6ab15b397", false, null,
            "Man schreibt das Jahr 2019. Den Replikanten, künstlichen Menschen, ist bei Todesstrafe die Rückkehr zur Erde verboten. Doch trotzdem kehren eines Tages einig...",
            "https://images.justwatch.com/poster/178056054/s332/Der-Blade-Runner.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt8579674", "1917", 2019, 8.2, 789690,
            88, 85.0,
            "Drama, Historisch, Krieg & Militär", 119, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.d4b77af3-b88f-1d33-46a2-671197806e21", false, null,
            "Auf dem Höhepunkt des Ersten Weltkriegs wird Schofield und Blake, zwei jungen britischen Soldaten, eine scheinbar unmögliche Mission gegeben: In einem Wettla...",
            "https://images.justwatch.com/poster/327046362/s332/1917.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt0347149", "Das wandelnde Schloss", 2004, 8.2, 524057,
            88, 85.0,
            "Action & Abenteuer, Animation, Fantasy", 119, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/70028883", false, null,
            "Die junge Hutmacherin Sophie lebt in einer Kleinstadt im Lande Ingary, einer Welt voller Magie und Fabelwesen, die aber auch schon erste Flugzeuge und Dampfm...",
            "https://images.justwatch.com/poster/302445454/s332/Das-wandelnde-Schloss.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0092005", "Stand By Me - Das Geheimnis eines Sommers", 1986, 8.1, 496917,
            89, 85.0,
            "Drama", 89, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/994992", false, null,
            "USA, Ende der 50er Jahre: Vern, Gordie, Chris, und Teddy wollen endlich einmal heraus aus ihrem langweiligen Heimatkaff Castle Rock. Da bietet es sich doch a...",
            "https://images.justwatch.com/poster/196433260/s332/Das-Geheimnis-eines-Sommers.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1245104", "Presto", 2008, 8.3, 27045,
            87, 85.0,
            "Animation, Komödien, Fantasy", 5, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-d2f998e4-4adb-43e6-a3a6-17b7678c9299", false, null,
            "Der Zauberer Presto will in seiner Show mit einem magischen Hut den Hasen Alec herbeizaubern, dieser geht aber in eine Art Hungerstreik, und will ohne seine ...",
            "https://images.justwatch.com/poster/321657646/s332/Pixars-komplette-Kurzfilm-Collection-Volume-2-Presto.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt10214826", "Agent Sai Srinivasa Athreya", 2019, 8.3, 22633,
            87, 85.0,
            "Komödien, Krimi, Mystery & Thriller", 148, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.b2b62752-578d-18ff-d457-a10910a69e56", false, null,
            "Agent Sai Srinivasa Athreya ist ein authentischer humorvoller Enthüllungsthriller, der sich um die Abenteuer eines Detektivs aus Nellore dreht.",
            "https://images.justwatch.com/poster/138758973/s332/eejeNtt-saayi-shriinivaas-atreey.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt13391710", "Jana Gana Mana", 2022, 8.3, 22519,
            87, 85.0,
            "Krimi, Drama, Mystery & Thriller", 162, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81477034", false, null,
            "Nach dem brutalen Mord an einer Professorin, der zu Protesten führt, leitet ein Polizist die Ermittlungen ein und ein Anwalt fordert im Gerichtssaal Gerechti...",
            "https://images.justwatch.com/poster/320375365/s332/Jana-Gana-Mana.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt28035641", "Once Upon a Studio", 2023, 8.3, 8580,
            87, 85.0,
            "Animation, Komödien, Drama", 9, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-ed8f308d-b613-48b4-aefc-e20f0c84fc64", false, null,
            "Geliebte Figuren aus 100 Jahren Disney Animation treffen sich zu einem unvergesslichen Wiedersehen.",
            "https://images.justwatch.com/poster/308813093/s332/once-upon-a-studio.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0131636", "Vuk: Der kleine Fuchs", 1981, 8.3, 6508,
            87, 85.0,
            "Animation, Komödien, Drama", 74, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.705e264b-9e01-41f2-975a-8810ae131f68", false, null,
            "Die Geschichte beginnt im Bau einer großen Fuchsfamilie. Die Eltern Kag und Ìny haben viele kleine Junge, wovon Vuk, der neugierigste und aktivste von allen,...",
            "https://images.justwatch.com/poster/696555/s332/Vuk-Der-kleine-Fuchs.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0137523", "Fight Club", 1999, 8.8, 2674719,
            81, 84.5,
            "Krimi, Drama, Mystery & Thriller", 139, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/26004747", false, null,
            "Ein Yuppie findet beim charismatischen Tyler Durden Unterschlupf, nachdem seine Wohnung in die Luft gejagt wird. Ein Gerangel zwischen den beiden entwickelt ...",
            "https://images.justwatch.com/poster/344847036/s332/Fight-Club.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt4154756", "Avengers 3: Infinity War", 2018, 8.4, 1418829,
            85, 84.5,
            "Action & Abenteuer, Science-Fiction", 149, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-9a136e06-852a-41bf-b71d-fa061cb43225", false, null,
            "Der mächtige Titan Thanos  hat es sich zur Aufgabe gemacht das halbe Universum auszulöschen. Um diesen perfiden Plan Realität werden zu lassen, benötigt er a...",
            "https://images.justwatch.com/poster/307167911/s332/Avengers-Infinity-War.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0425112", "Hot Fuzz - Zwei abgewichste Profis", 2007, 7.8, 574291,
            91, 84.5,
            "Action & Abenteuer, Komödien, Krimi", 121, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/70056431", false, null,
            "Der Polizist Nicholas Angel wird von London in das Provinznest Sandford versetzt, weil er seine Kollegen immer in den Schatten stellte. Zunächst langweilt er...",
            "https://images.justwatch.com/poster/105139732/s332/Hot-Fuzz-Zwei-abgewichste-Profis.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt14230458", "Poor Things", 2023, 7.7, 425967,
            92, 84.5,
            "Komödien, Drama, Romantik", 141, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.a3a243ae-6d81-48c1-a02b-0bfa3e2a4e22", false, null,
            "Eine junge Frau namens Bella Baxter wird von dem unkonventionellen Wissenschaftler Dr. Godwin Baxter zurück ins Leben gebracht. Unter Führung des brillanten ...",
            "https://images.justwatch.com/poster/312118753/s332/poor-things.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt32565993", "Glennkill: Ein Schafskrimi", 2026, 7.4, 122482,
            95, 84.5,
            "Action & Abenteuer, Komödien, Kinder & Familie", 109, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.c78b3cef-7f5d-4cc9-907c-455e713d1649", false, null,
            "Schäfer George liest seinen geliebten Schafen jeden Abend Krimis vor und nimmt an, dass sie kein Wort davon verstehen. Doch als ein mysteriöser Vorfall das f...",
            "https://images.justwatch.com/poster/346646512/s332/glennkill-ein-schafskrimi.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt8618654", "Kiss the Ground", 2020, 8.1, 8254,
            88, 84.5,
            "Dokumentationen", 85, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.502949df-491e-465a-b245-b4c2bbec0f8f", false, null,
            "Wissenschaftler und berühmte Umweltbotschafter erklären, warum der Erdboden im Kampf gegen den Klimawandel zur Rettung unseres Planeten beitragen könnte.",
            "https://images.justwatch.com/poster/207487185/s332/Kiss-the-Ground.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt0081505", "Shining", 1980, 8.4, 1249166,
            84, 84.0,
            "Drama, Horror, Mystery & Thriller", 119, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.c7f0ed7a-2486-4013-a07d-c614a3622632", false, null,
            "Die Hausmeistertätigkeit in einem leeren Hotel außerhalb der Saison bekommt Jack Torrance nicht: Er wird wahnsinnig und terrorisiert seine Frau und seinen kl...",
            "https://images.justwatch.com/poster/191571071/s332/Shining.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0167404", "The Sixth Sense - Nicht jede Gabe ist ein Segen", 1999, 8.2, 1171364,
            86, 84.0,
            "Drama, Mystery & Thriller", 107, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/26797528", false, null,
            "Der Kinderpsychologe Dr. Malcolm Crowe feiert mit seiner Frau eine für ihn bedeutsame Auszeichnung. An diesem Abend wird er von Vincent Grey überrascht, eine...",
            "https://images.justwatch.com/poster/302975038/s332/The-Sixth-Sense-Der-Sechste-Sinn.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1856101", "Blade Runner 2049", 2017, 8.0, 772969,
            88, 84.0,
            "Action & Abenteuer, Drama, Mystery & Thriller", 164, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80185760", false, null,
            "Der Inhalt eines versteckten Grabs erregt das Interesse eines Wirtschaftsmagnaten und veranlasst Replikanten-Jäger Officer K zur Suche nach einer verschollen...",
            "https://images.justwatch.com/poster/8160492/s332/Blade-Runner-2049.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt3397884", "Sicario", 2015, 7.7, 552978,
            91, 84.0,
            "Action & Abenteuer, Krimi, Drama", 122, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.a6b886eb-47c1-90f9-7508-d37c218381bb", false, null,
            "Eine FBI-Agentin beteiligt sich an einem Undercover-Einsatz gegen einen mexikanischen Drogenbaron. Doch gewisse Maßnahmen der Mission kollidieren mit ihren M...",
            "https://images.justwatch.com/poster/94598840/s332/Sicario.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0074285", "Carrie - Des Satans jüngste Tochter", 1976, 7.4, 235265,
            94, 84.0,
            "Horror, Mystery & Thriller", 94, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.8f307ce3-ad92-4a89-a873-50c81766c649", false, null,
            "Die junge Carrie ist vom Leben stark gebeutelt: die Mutter ist eine religiöse Fanatikerin und in der Schule gilt das schüchterne Mädchen bei allen nur als Fr...",
            "https://images.justwatch.com/poster/300737571/s332/Carrie-Des-Satans-juengste-Tochter.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt33014583", "Dhurandhar", 2025, 8.2, 152300,
            86, 84.0,
            "Action & Abenteuer, Krimi, Drama", 214, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/82813816", false, null,
            "Dhurandhar spielt in der Welt des indischen Geheimdienstes und folgt einem Geheimagenten auf einer hochriskanten Mission, die auf historischen Ereignissen ba...",
            "https://images.justwatch.com/poster/348808996/s332/dhurandhar.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt10431500", "7. Kogustaki Mucize", 2019, 8.2, 64583,
            86, 84.0,
            "Drama", 132, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81239779", false, null,
            "Der geistig zurückgebliebene, liebende Vater einer Tochter wird für den Mord an der Tochter des Polizeipräsidenten verurteilt. Wird er seine Unschuld beweise...",
            "https://images.justwatch.com/poster/175616829/s332/7-Kogustaki-Mucize.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt10847306", "Bill Burr: Paper Tiger", 2019, 8.0, 13449,
            88, 84.0,
            "Komödien", 67, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81060174", false, null,
            "Bill Burr lässt sich in diesem amüsanten Stand-up-Special in London über die Wutkultur, männlichen Feminismus, kulturellen Diebstahl, Robotersex und andere P...",
            "https://images.justwatch.com/poster/191750077/s332/Bill-Burr-Paper-Tiger.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0416716", "Empire of Dreams - Die Geschichte der Star Wars Trilogie", 2004, 8.2, 5730,
            86, 84.0,
            "Dokumentationen, Historisch, Science-Fiction", 151, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-7928984a-41c1-4c6c-879d-42f7d9053a89", false, null,
            "Dokumentarfilm über die Entstehungsgeschichte der ersten drei 'Star Wars'-Filme und die Vorgeschichte der legendären Sternen-Saga. Die Doku gibt interessan...",
            "https://images.justwatch.com/poster/84404446/s332/Empire-of-Dreams-Die-Geschichte-der-Star-Wars-Trilogie.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt28607951", "Anora", 2024, 7.4, 311855,
            93, 83.5,
            "Komödien, Drama, Romantik", 139, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.c3fe4c70-a2f4-4f32-8306-3613baeac4a7", false, null,
            "Die selbstbewusste Anora, eine junge Stripperin aus Brooklyn, erhält die Chance auf einen Ausstieg, als sie Ivan, den Sohn eines Oligarchen, kennenlernt und ...",
            "https://images.justwatch.com/poster/324493218/s332/anora.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0098635", "Harry und Sally", 1989, 7.7, 276834,
            90, 83.5,
            "Komödien, Drama, Romantik", 96, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.ed63c965-c434-44ac-8e84-c9827c58aead", false, null,
            "Harry nervt Sally und Sally nervt Harry. Das war während ihrer gemeinsamen unfreiwilligen 18-Stunden-Fahrt nach New York so, und bleibt auch so fünf Jahre sp...",
            "https://images.justwatch.com/poster/260305710/s332/Harry-und-Sally.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0044008", "Eine Weihnachtsgeschichte", 1951, 8.1, 29414,
            86, 83.5,
            "Drama, Fantasy, Kinder & Familie", 51, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.c8baeeff-e27a-c795-4bb9-24099ed12447", false, null,
            "Seit dem Tod seines Partners Marley betreibt der geizige Scrooge sein Geschäft allein. Kaltherzig und rücksichtslos quält er unglückliche Schuldner, demütigt...",
            "https://images.justwatch.com/poster/304052443/s332/Charles-Dickens-Eine-Weihnachtsgeschichte.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0086190", "Die Rückkehr der Jedi-Ritter", 1983, 8.3, 1227296,
            83, 83.0,
            "Action & Abenteuer, Fantasy, Science-Fiction", 132, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-4b6e7cda-daa5-4f2d-9b61-35bbe562c69c", false, null,
            "Wesen aus allen Ecken der Galaxis treffen die Vorbereitungen für die entscheidende Schlacht gegen die Streitkräfte des Imperiums. Ziel der Aktion ist die tot...",
            "https://images.justwatch.com/poster/245126516/s332/Star-Wars-VI-Die-Rueckkehr-der-Jedi-Ritter.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0097576", "Indiana Jones und der letzte Kreuzzug", 1989, 8.2, 873790,
            84, 83.0,
            "Action & Abenteuer", 127, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60010487", false, null,
            "Wir schreiben das Jahr 1938: Doktor Jones wird vom Mäzen eines Museums beauftragt, den Heiligen Gral und somit die wichtigste Reliquie der Christenheit zu fi...",
            "https://images.justwatch.com/poster/184280302/s332/Indiana-Jones-und-der-letzte-Kreuzzug.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0097165", "Der Club der toten Dichter", 1989, 8.1, 673308,
            85, 83.0,
            "Komödien, Drama", 129, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-58ab534f-7a68-40f8-b703-654d224dc80b", false, null,
            "Der neue Literaturlehrer John Keating bringt 1959 frischen Wind in das Traditionsinternat Welton und inspiriert seine ansonsten zur Disziplin erzogenen Schül...",
            "https://images.justwatch.com/poster/309136915/s332/Der-Club-der-toten-Dichter.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1517451", "A Star is Born", 2018, 7.6, 454010,
            90, 83.0,
            "Drama, Musik & Musical, Romantik", 136, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.74ba6528-8bcd-3fb0-3235-a0f9088211b1", false, null,
            "Eine aufstrebende Sängerin beginnt eine Beziehung mit einem erfahrenen Musiker. Während ihre Karriere plötzlich floriert, treten seine Laster eine Abwärtsspi...",
            "https://images.justwatch.com/poster/293727597/s332/A-Star-Is-Born-2018.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0063442", "Planet der Affen", 1968, 8.0, 211772,
            86, 83.0,
            "Action & Abenteuer, Drama, Science-Fiction", 112, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-cc1ecc55-f081-40d0-82c7-50e67b85995c", false, null,
            "Astronaut Taylor überlebt den Raum-Zeitsprung durch das All – 2000 Jahre in die Zukunft. Eine Notlandung verschlägt ihn auf einen entfernten Planeten, der vo...",
            "https://images.justwatch.com/poster/240269884/s332/Planet-der-Affen-1.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt26548265", "Maharaja", 2024, 8.3, 86597,
            83, 83.0,
            "Action & Abenteuer, Krimi, Drama", 141, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81690671", false, null,
            "Nachdem bei einem Friseur eingebrochen wurde, sinnt er auf Rache. Kryptisch berichtet er der Polizei, dass seine „Lakshmi“ gestohlen wurde, und lässt sie im ...",
            "https://images.justwatch.com/poster/319725378/s332/maharaja-2024.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt0172495", "Gladiator", 2000, 8.5, 1889828,
            80, 82.5,
            "Action & Abenteuer, Drama", 155, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.ddc039e3-a01f-4530-b91e-014a7846926b", false, null,
            "Er sollte dem Kaiser auf den Thron folgen, doch stattdessen wird der römische General vom machthungrigen Sohn des verstorbenen Herrschers zum Tode verdammt.",
            "https://images.justwatch.com/poster/347149368/s332/Gladiator.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt2119532", "Hacksaw Ridge - Die Entscheidung", 2016, 8.1, 700505,
            84, 82.5,
            "Drama, Historisch, Krieg & Militär", 139, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80108975", false, null,
            "Die wahre Geschichte über den amerikanischen Army-Sanitäter Desmond T. Doss, der sich im Zweiten Weltkrieg freiwillig als Soldat meldete, aber keine Waffen t...",
            "https://images.justwatch.com/poster/177128450/s332/Hacksaw-Ridge-Die-Entscheidung.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt3799694", "The Nice Guys", 2016, 7.4, 426823,
            91, 82.5,
            "Action & Abenteuer, Komödien, Krimi", 116, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.a933e15e-83fd-4f7f-8cf5-97ab55ee2d91", false, null,
            "Als in den 1970ern in Los Angeles ein Porno-Star umkommt, gerät ein ungleiches Detektiv-Duo in einen Fall voll von schwarzem Humor und einer bösen Verschwörung.",
            "https://images.justwatch.com/poster/818307/s332/The-Nice-Guys.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt9620292", "Promising Young Woman", 2020, 7.5, 260939,
            90, 82.5,
            "Krimi, Drama, Mystery & Thriller", 114, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.88882855-29e9-4f9b-be93-841ec0963fbe", false, null,
            "Cassie ist eine vielversprechende junge Medizinstudentin, bis ein Schicksalsschlag sie eines Tages komplett aus der Bahn wirft. Sie bricht ihr Studium ab, su...",
            "https://images.justwatch.com/poster/257420275/s332/Promising-Young-Woman.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt8999762", "Der Brutalist", 2024, 7.2, 165330,
            93, 82.5,
            "Drama", 215, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.7001327b-1953-4f40-9a25-2754bb33e8c7", false, null,
            "Visionäre Ideen, ein radikaler Neuanfang und die Frage nach dem wahren Preis des Erfolgs: Die Geschichte des jüdisch-ungarischen Architekten László Toth, der...",
            "https://images.justwatch.com/poster/324736945/s332/the-brutalist.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt2082197", "Barfi - Liebe braucht keine Worte", 2012, 8.1, 95228,
            84, 82.5,
            "Komödien, Drama, Romantik", 151, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.35183890-abc7-4873-9e83-17713d30adbd", false, null,
            "Der gehörlose Barfi (Kapoor) erobert das Herz der schönen Shruti (Ileana D’Cruz), doch auf Druck der Eltern heiratet sie einen „Normalen“. Jahre später triff...",
            "https://images.justwatch.com/poster/333158535/s332/Barfi.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt40829432", "Neon Genesis Evangelion: The End of Evangelion", 1997, 8.1, 79605,
            84, 82.5,
            "Action & Abenteuer, Animation, Drama", 87, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60024788", false, null,
            "Die Geheimorganisation SEELE greift NERV an! Die Invasion der Geofront hat begonnen, und SEELEs einziges Ziel ist die totale Vernichtung, insbesondere der dr...",
            "https://images.justwatch.com/poster/345826683/s332/Neon-Genesis-Evangelion-The-End-of-Evangelion.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt4934950", "Guilty", 2015, 8.1, 44157,
            84, 82.5,
            "Krimi, Drama, Mystery & Thriller", 132, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80080618", false, null,
            "Das indische Drama Guilty mit Irrfan Khan als Ermittler thematisiert einen Doppelmord, der sich 2008 in Noida, Indien, ereignete. Der Mordfall, bei dem die 1...",
            "https://images.justwatch.com/poster/316242235/s332/Talvar.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt3302820", "Cowspiracy: Das Geheimnis der Nachhaltigkeit", 2014, 8.1, 27534,
            84, 82.5,
            "Dokumentationen", 90, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80033772", false, null,
            "Erfahren Sie, warum industrielle Viehhaltung unsere natürlichen Ressourcen erschöpft – und warum große Umweltschutzorganisationen das Problem zu ignorieren s...",
            "https://images.justwatch.com/poster/246878716/s332/Cowspiracy-Das-Geheimnis-der-Nachhaltigkeit.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt3689498", "Liebe geht durch den Magen", 2014, 8.1, 25159,
            84, 82.5,
            "Animation, Komödien, Drama", 6, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-4cbbecbb-9d30-4e08-8e79-4adee45055de", false, null,
            "Die Geschichte des Liebeslebens eines Mannes wird durch die Augen seines Hundes und besten Freundes Winston erzählt. Mit jedem Bissen, den die beiden bei ihr...",
            "https://images.justwatch.com/poster/270313954/s332/Liebe-geht-durch-den-Magen.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt27118357", "Amaran", 2024, 8.1, 24655,
            84, 82.5,
            "Action & Abenteuer, Drama, Krieg & Militär", 167, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81728596", false, null,
            "Amaran ist ein Biopic, das das persönliche und berufliche Leben des AC-Preisträgers Major Mukund Varadarajran erzählt, der am 25. April 2014 bei der Operatio...",
            "https://images.justwatch.com/poster/321829305/s332/amaran.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt36596965", "Mandaadi", 2026, 8.1, 11365,
            84, 82.5,
            "Action & Abenteuer, Drama, Sport", 153, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/82787383", false, null,
            "Der Film erzählt die Geschichte von Muthukaali, gespielt von Soori, einem Fischer aus der Küstenregion Tamil Nadus. Als Mandaadi wird ein erfahrener Anführer...",
            "https://images.justwatch.com/poster/354526592/s332/mandaadi.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt3784160", "Yosemite – Tal der Kletterer", 2014, 8.1, 6821,
            84, 82.5,
            "Action & Abenteuer, Dokumentationen, Historisch", 87, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.22b71bb2-b58b-8e2a-3c92-bd3c549f08bb", false, null,
            "Das Yosemite Valley in Kalifornien gilt als Wiege der amerikanischen Kletterszene. Hier befinden sich weltbekannte Felsen wie der Half Dome oder El Capitan. ...",
            "https://images.justwatch.com/poster/265397440/s332/Valley-Uprising.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt10919132", "Love Mocktail", 2020, 8.1, 5066,
            84, 82.5,
            "Drama, Romantik", 153, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.18b842c5-9888-bd62-a026-dc01c5928261", false, null,
            "Love Mocktail ist ein romantischer und unterhaltsamer Film über die Suche des Protagonisten Aadi nach Liebe. Das Leben führt ihn durch eine Achterbahnfahrt u...",
            "https://images.justwatch.com/poster/330514044/s332/Love-Mocktail.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt12037194", "Furiosa: A Mad Max Saga", 2024, 7.4, 329183,
            90, 82.0,
            "Action & Abenteuer, Science-Fiction", 148, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.3f53de6b-4d3f-42c2-8f21-dbf813fffa66", false, null,
            "Als die Welt untergeht, wird die junge Furiosa vom Grünen Ort der vielen Mütter entführt und fällt in die Hände einer großen Bikerhorde unter der Führung des...",
            "https://images.justwatch.com/poster/316152514/s332/furiosa.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1160419", "Dune", 2021, 8.0, 1096753,
            83, 81.5,
            "Action & Abenteuer, Drama, Science-Fiction", 155, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81199139", false, null,
            "„Dune“ erzählt die packende Geschichte des brillanten jungen Helden Paul Atreides, dem das Schicksal eine Rolle vorherbestimmt hat, von der er niemals geträu...",
            "https://images.justwatch.com/poster/253886605/s332/Dune-2020.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1392214", "Prisoners", 2013, 8.2, 964260,
            81, 81.5,
            "Krimi, Drama, Mystery & Thriller", 153, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/70273235", false, null,
            "Als seine kleine Tochter und deren Freundin verschwinden, gerät ein verzweifelter Vater mit dem verantwortlichen Kommissar aneinander und nimmt die Dinge sel...",
            "https://images.justwatch.com/poster/260833848/s332/Prisoners.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1877830", "Der Batman", 2022, 7.8, 954228,
            85, 81.5,
            "Action & Abenteuer, Krimi, Mystery & Thriller", 177, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81500601", false, null,
            "Zwei Jahre lang jagte Bruce Wayne als Batman Verbrecher auf der Straße und geriet immer tiefer in die Schattenwelt Gothams. Im korrupten Netzwerk der Beamten...",
            "https://images.justwatch.com/poster/266282103/s332/The-Batman.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt7784604", "Hereditary - Das Vermächtnis", 2018, 7.3, 473944,
            90, 81.5,
            "Drama, Horror, Mystery & Thriller", 128, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.ec303ad9-797e-432e-889f-47ea585d939e", false, null,
            "Annie verliert ihre Mutter, die zugleich die Matriarchin der Familie war. Das Dahinscheiden der alten Dame hat für die Familie Graham, zu der auch Ehemann St...",
            "https://images.justwatch.com/poster/343809338/s332/Hereditary.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt8332922", "A Quiet Place 2", 2021, 7.2, 330559,
            91, 81.5,
            "Drama, Horror, Mystery & Thriller", 97, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81186806", false, null,
            "Die Abbotts müssen ihre Heimat verlassen und wagen sich in eine Welt voller unerwarteter Gefahren und tödlicher Kreaturen hinaus, die von Geräuschen angezoge...",
            "https://images.justwatch.com/poster/250620201/s332/A-Quiet-Place-Part-II.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt6499752", "Upgrade", 2018, 7.5, 239499,
            88, 81.5,
            "Action & Abenteuer, Mystery & Thriller, Science-Fiction", 100, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.52b288aa-8781-29d4-b258-3404642ac2af", false, null,
            "In der nahen Zukunft hat der technische Fortschritt nahezu alle Bereiche des menschlichen Lebens übernommen. Doch der Protagonist Grey Trace versucht, in sei...",
            "https://images.justwatch.com/poster/155159747/s332/Upgrade.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt2338151", "PK - Andere Sterne, andere Sitten", 2014, 8.1, 227001,
            82, 81.5,
            "Komödien, Drama, Science-Fiction", 153, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/70303496", false, null,
            "Der gutmütige Außerirdische PK kommt aus einer entfernten Galaxis und landet eines Tages auf der Erde, um den blauen Planeten und seine Bewohner zu studieren...",
            "https://images.justwatch.com/poster/256792895/s332/PK-Andere-Sterne-andere-Sitten.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt32141377", "28 Years Later: The Bone Temple", 2026, 7.2, 132377,
            91, 81.5,
            "Horror, Mystery & Thriller, Science-Fiction", 109, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/82622299", false, null,
            "Nia DaCosta knüpft an die Welt an, die Danny Boyle und Alex Garland in „28 Years Later“ geschaffen haben – und stellt sie auf den Kopf. In der Fortsetzung di...",
            "https://images.justwatch.com/poster/341016836/s332/28-years-later-the-bone-temple.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt24226474", "Jigarthanda Double X", 2023, 8.0, 12199,
            83, 81.5,
            "Action & Abenteuer, Komödien, Drama", 172, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81626533", false, null,
            "In den 1970ern tut sich ein unkonventioneller Filmemacher mit einem berüchtigten Gangster mit einer Leidenschaft für Hollywood-Westernfilme zusammen und träu...",
            "https://images.justwatch.com/poster/309388953/s332/jigarthanda-doublex.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt3958494", "Jim Jefferies: BARE", 2014, 8.0, 6153,
            83, 81.5,
            "Komödien", 76, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80002621", false, null,
            "Dem australischen Comedian Jim Jefferies ist nichts heilig. Die Mutter seines Kindes, behinderte Schauspieler, Waffenkontrolle – alles wird auf die Schippe g...",
            "https://images.justwatch.com/poster/152906894/s332/Jim-Jefferies-BARE.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0482571", "Prestige - Die Meister der Magie", 2006, 8.5, 1635358,
            77, 81.0,
            "Drama, Mystery & Thriller, Science-Fiction", 131, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.f4a9f755-ff35-fd0a-d38e-80f9da908114", false, null,
            "Im viktorianischen Zeitalter, zur Zeit der Jahrhundertwende, lernen sich die konkurrierenden Magier Robert Angier und Alfred Borden kennen. Aus freundschaftl...",
            "https://images.justwatch.com/poster/188393792/s332/Prestige-Die-Meister-der-Magie.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt2911666", "John Wick", 2014, 7.5, 843706,
            87, 81.0,
            "Action & Abenteuer, Krimi, Mystery & Thriller", 101, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.92d7a7e6-e316-4803-8c09-9025604a6278", false, null,
            "John Wick genießt seinen frühen Ruhestand in der Vorstadt. Doch als seine Frau einer tödlichen Krankheit erliegt, verfällt er in Trauer. Nur sein Hund bleibt...",
            "https://images.justwatch.com/poster/916466/s332/John-Wick.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt12087624", "Sincerely Louis C.K.", 2020, 8.2, 7073,
            80, 81.0,
            "Komödien", 60, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/82888453", false, null,
            "Louis C.K. muses on religion, terrorism, small towns, Florida, disabilities, dogs, Auschwitz, marriage, sex, vegans, and his personal sexual controversy, in ...",
            "https://images.justwatch.com/poster/294897751/s332/sincerely-louis-c-k.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0369339", "Collateral", 2004, 7.5, 475938,
            86, 80.5,
            "Action & Abenteuer, Krimi, Drama", 120, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60036246", false, null,
            "Vincent ist ein cooler, berechnender Auftragskiller und sehr gefragt im Business. Max ist ein Taxifahrer mit großen Träumen und ohne Erfolg. In dieser schick...",
            "https://images.justwatch.com/poster/102332018/s332/Collateral.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0230600", "The Others", 2001, 7.6, 434259,
            85, 80.5,
            "Horror, Mystery & Thriller, Produziert in Europa", 101, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60021097", false, null,
            "Jersey, England. 1945. Grace, deren Ehemann nach dem Ende des Zweiten Weltkriegs noch immer nicht von der Front zurückgekehrt ist, lebt mit ihren beiden Kind...",
            "https://images.justwatch.com/poster/263279189/s332/The-Others.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0325980", "Fluch der Karibik", 2003, 8.1, 1350066,
            79, 80.0,
            "Action & Abenteuer, Fantasy", 143, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-2d6036d7-f3b4-42c3-b1d3-cd5dd79bf307", false, null,
            "Im 17.Jahrhundert sucht der Pirat Jack Sparrow nach einem sagenumwobenen Schatz in der Karibik, als ihn das Schicksal mit dem jungen Will Turner zusammenführ...",
            "https://images.justwatch.com/poster/259690396/s332/Fluch-der-Karibik.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1675434", "Ziemlich beste Freunde", 2011, 8.5, 1046315,
            75, 80.0,
            "Komödien, Drama", 112, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/70232180", false, null,
            "Philippe ist zwar reich und intelligent, aber er benötigt im Alltag auch Hilfe, da er vom Hals abwärts gelähmt ist. Als er sich einen neuen Pfleger engagiert...",
            "https://images.justwatch.com/poster/193315912/s332/Ziemlich-beste-Freunde.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0086250", "Scarface - Toni, das Narbengesicht", 1983, 8.3, 1039153,
            77, 80.0,
            "Action & Abenteuer, Krimi, Drama", 170, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60029681", false, null,
            "1980 wandert der politisch verfolgte Kubaner Tony Montana nach Florida in den USA aus. Bald wird er seinem Job als Tellerwäscher überdrüssig und beteiligt si...",
            "https://images.justwatch.com/poster/8724541/s332/Scarface-Toni-das-Narbengesicht.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt6900644", "Hasan Minhaj: Homecoming King", 2017, 8.2, 8207,
            78, 80.0,
            "Komödien", 72, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80134781", false, null,
            "Comic Hasan Minhaj of 'The Daily Show' shares personal stories about racism, immigrant parents, prom night horrors and more in this stand-up special.",
            "https://images.justwatch.com/poster/306075372/s332/Hasan-Minhaj-Homecoming-King.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0112573", "Braveheart", 1995, 8.3, 1175088,
            74, 78.5,
            "Drama, Historisch, Krieg & Militär", 178, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.9aa9f6e0-5e39-1a7b-2567-fcba3d775af6", false, null,
            "Ende des 13. Jahrhunderts ermorden britische Besatzer die geliebte Frau des in Schottland lebenden William Wallace. Von dieser Tat aufgestachelt und blind vo...",
            "https://images.justwatch.com/poster/169031264/s332/Braveheart.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt7888964", "Nobody", 2021, 7.4, 407360,
            83, 78.5,
            "Action & Abenteuer, Krimi, Drama", 91, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.0907847b-059f-413c-b8fa-f4ebbc831522", false, null,
            "Hutch Mansell (Bob Odenkirk) ist ein unterschätzter und übersehener Familienvater, der die Demütigungen des Lebens stillschweigend erträgt. Als zwei Einbrech...",
            "https://images.justwatch.com/poster/331376212/s332/Nobody-2021.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt4873118", "Guy Ritchie's Der Pakt", 2023, 7.5, 218507,
            82, 78.5,
            "Action & Abenteuer, Drama, Mystery & Thriller", 123, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.dc30b29b-865b-4735-83d9-8541e0391569", false, null,
            "Noch eine Mission muss Sgt. John Kinley absolvieren, dann kann er nach Hause. Doch dann gerät er mit seiner Einheit in einen Hinterhalt. Nahezu alle Mitglied...",
            "https://images.justwatch.com/poster/306901758/s332/the-covenant-2024.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt5929776", "Leonardo DiCaprio: Before the Flood", 2016, 8.1, 29816,
            76, 78.5,
            "Dokumentationen", 93, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-24ae91e9-d14d-4dca-82d7-7b1fc10df1d0", false, null,
            "Zu Beginn des Films ist lediglich der Schauspieler Leonardo DiCaprio als Erzähler zu hören, der detailliert Hieronymus Boschs Triptychon Der Garten der Lüste...",
            "https://images.justwatch.com/poster/206906864/s332/Before-the-Flood.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt0208092", "Snatch - Schweine und Diamanten", 2000, 8.2, 988580,
            74, 78.0,
            "Komödien, Krimi", 103, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/60003388", false, null,
            "Diamantendieb und -kurier Franky Four Fingers, der im Auftrag des New Yorker Gangsters Avi in Antwerpen einen großen Stein geraubt hat, macht auf seinem Weg ...",
            "https://images.justwatch.com/poster/307291636/s332/Snatch-Schweine-und-Diamanten.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1454029", "The Help", 2011, 8.1, 534276,
            75, 78.0,
            "Drama", 146, StreamingProvider.DISNEY_PLUS,
            "https://www.disneyplus.com/browse/entity-45859bf8-a327-4118-8789-2d02c7d96db4", false, null,
            "1962 kehrt die junge Skeeter von ihrem Collegestudium zurück in ihre Südstaaten-Heimatstadt. Ihre alten Freundinnen haben viel Freizeit. Denn ihre Babys und ...",
            "https://images.justwatch.com/poster/175585438/s332/The-Help.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt14152756", "Seaspiracy", 2021, 8.1, 35978,
            75, 78.0,
            "Krimi, Dokumentationen", 89, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81014008", false, null,
            "Ein vom Meer begeisterter Filmemacher entdeckt bei seinen Nachforschungen über die Auswirkungen der Menschen auf die Meeresbewohner eine Verschwörung globale...",
            "https://images.justwatch.com/poster/243656680/s332/Seaspiracy.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt9851854", "Major", 2022, 8.1, 34710,
            75, 78.0,
            "Action & Abenteuer, Drama, Historisch", 149, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81242575", false, null,
            "Diese Filmbiografie handelt vom indischen Armee-Major Sandeep Unnikrishnan – von seiner Kindheit bis hin zu seinen heldenhaften Taten während der Anschläge i...",
            "https://images.justwatch.com/poster/242799060/s332/Pradhana.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt8236336", "The Report", 2019, 7.2, 57987,
            83, 77.5,
            "Krimi, Drama, Historisch", 119, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.5a02d77c-cb58-4c76-863d-f9fa902ec95e", false, null,
            "In den USA greift die CIA nach den Anschlägen vom 11. September auf extreme Verhörtechniken zurück – als notwendig gerechtfertigt, sind sie in Wahrheit bruta...",
            "https://images.justwatch.com/poster/144144233/s332/The-Report.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0119174", "The Game - Das Geschenk seines Lebens", 1997, 7.7, 479232,
            77, 77.0,
            "Drama, Mystery & Thriller", 129, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.5aa9f7b0-8c98-6039-4790-74b4e9fbcfbf", false, null,
            "Nicholas Van Orton ist reich, intelligent und erfolgreich. Ein knallharter Geschäftsmann mit Prinzipien und Selbstdisziplin. Er hat die absolute Kontrolle üb...",
            "https://images.justwatch.com/poster/178686095/s332/The-Game-Das-Geschenk-seines-Lebens.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0083190", "Der Einzelgänger", 1981, 7.4, 52131,
            80, 77.0,
            "Action & Abenteuer, Krimi, Drama", 123, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.25967a91-073e-44cf-a831-9ee8aee90867", false, null,
            "Der Meisterdieb Frank möchte nur noch ein Ding drehen und sich dann in den Ruhestand verabschieden, um mit seiner Freundin Jessie eine Familie zu gründen. Do...",
            "https://images.justwatch.com/poster/257647255/s332/Der-Einzelgaenger-1981.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt8367814", "The Gentlemen", 2020, 7.8, 470833,
            75, 76.5,
            "Action & Abenteuer, Komödien, Krimi", 113, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81178299", false, null,
            "Smart, knallhart und mit genialem Gespür fürs Geschäft hat sich der Exil-Amerikaner Mickey Pearson über die Jahre ein millionenschweres Marihuana-Imperium in...",
            "https://images.justwatch.com/poster/345861998/s332/The-Gentlemen.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0117571", "Scream - Schrei!", 1996, 7.5, 456310,
            78, 76.5,
            "Krimi, Horror, Mystery & Thriller", 112, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/939827", false, null,
            "Ein Killer geht um in einer kleinen amerikanischen Provinzgemeinde. Die Schülerin Casey, die einfach nur daheim vor dem Fernseher sitzt, wird sein erstes Opf...",
            "https://images.justwatch.com/poster/50700641/s332/Scream-Schrei-des-Todes.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt12908150", "The Life of Chuck", 2025, 7.3, 80625,
            80, 76.5,
            "Drama, Fantasy, Science-Fiction", 111, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.8b30230a-5df8-452a-9fe6-86693afcef13", false, null,
            "Wer ist Chuck? Die Welt geht unter, Kalifornien versinkt im Meer, das Internet bricht zusammen – doch in einer amerikanischen Kleinstadt herrscht vor allem D...",
            "https://images.justwatch.com/poster/333543206/s332/the-life-of-chuck.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1817273", "The Place Beyond the Pines", 2013, 7.3, 313785,
            79, 76.0,
            "Krimi, Drama, Mystery & Thriller", 140, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/70241757", false, null,
            "Normalerweise lebt Luke Glanton von seinen Motorradstunts auf dem Jahrmarkt und hält eher Abstand von zwischenmenschlichen Beziehungen. Eines Tages trifft er...",
            "https://images.justwatch.com/poster/201980741/s332/The-Place-Beyond-the-Pines.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0375611", "Black", 2005, 8.1, 37822,
            71, 76.0,
            "Drama", 124, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/70031975", false, null,
            "Michelle McNallys (Rani Mukerji) Welt ist schwarz. Das junge Mädchen ist taubblind, kann weder hören noch sehen. Ihr exzentrischer Lehrer Debraj Sahai (Amita...",
            "https://images.justwatch.com/poster/306448812/s332/Black.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt0117665", "Sleepers", 1996, 7.5, 259515,
            76, 75.5,
            "Krimi, Drama, Mystery & Thriller", 147, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/973057", false, null,
            "Eine Gruppe Jugendlicher wächst in “Hell’s Kitchen”, einem Arbeiterviertel von New York, auf. Ihre einzige Vertrauensperson ist der Pater Bobby, dem sie auch...",
            "https://images.justwatch.com/poster/225771/s332/Sleepers.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt12361974", "Zack Snyders Justice League", 2021, 7.9, 481878,
            71, 75.0,
            "Action & Abenteuer, Dokumentationen, Fantasy", 242, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.f8bc14e4-a5f7-1061-7f38-2c97334c9f22", false, null,
            "Batman und Wonder Woman stellen ein Team aus Superhelden zusammen, um die Erde vor der apokalyptischen Bedrohung durch einen mächtigen Superschurken zu retten.",
            "https://images.justwatch.com/poster/335464093/s332/Zack-Snyders-Justice-League.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt4550098", "Nocturnal Animals", 2016, 7.5, 317456,
            74, 74.5,
            "Drama, Mystery & Thriller", 116, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80113783", false, null,
            "Die Kunsthändlerin Susan Morrow führt in Los Angeles ein privilegiertes, aber unerfülltes Leben mit ihrem neuen Ehemann. Als dieser erneut zu einer seiner za...",
            "https://images.justwatch.com/poster/313344132/s332/Nocturnal-Animals.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1677720", "Ready Player One", 2018, 7.4, 553642,
            71, 72.5,
            "Action & Abenteuer, Science-Fiction", 140, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.78b680ff-76d5-f11d-300c-65263d116eaa", false, null,
            "Im Jahr 2045 steht die ökologisch ausgebeutete Welt am Rande des endgültigen Zusammenbruchs. Doch die Menschen finden Erlösung in dem gigantischen virtuellen...",
            "https://images.justwatch.com/poster/77965553/s332/Ready-Player-One.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt0461936", "Don - Das Spiel beginnt", 2006, 7.2, 45143,
            73, 72.5,
            "Action & Abenteuer, Krimi, Drama", 178, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/70059026", false, null,
            "Um die malaysische Drogenmafia zu zerschlagen, bringt der indische Geheimdienst einen Doppelgänger des skrupellosen Gang-Chefs Don ins Spiel. Der unbescholte...",
            "https://images.justwatch.com/poster/251035279/s332/Don-Das-Spiel-beginnt.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt0988045", "Sherlock Holmes", 2009, 7.5, 713012,
            69, 72.0,
            "Action & Abenteuer, Krimi, Mystery & Thriller", 129, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.0ca9f7b3-e736-04fb-969a-5ea812e1efcc", false, null,
            "Der Ball rollt. Der exzentrische Kommissar jagt mithilfe seines Partners einen gewieften Feind. Körpereinsatz und Grips sind eines jeden Rätsels Lösung.",
            "https://images.justwatch.com/poster/8730713/s332/Sherlock-Holmes-2009.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt6723592", "Tenet", 2020, 7.3, 607128,
            70, 71.5,
            "Action & Abenteuer, Mystery & Thriller, Science-Fiction", 150, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81198930", false, null,
            "Um einen Krieg der Zeiten zu verhindern, der die ganze Welt zu zerstören droht, muss ein Undercover-Agent eine rätselhafte und schlagkräftige Waffe in seine ...",
            "https://images.justwatch.com/poster/239195311/s332/Tenet.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0903624", "Der Hobbit: Eine unerwartete Reise", 2012, 7.8, 939572,
            64, 71.0,
            "Action & Abenteuer, Fantasy", 169, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.d8a9f6fd-58c9-64dd-15d0-365bdf846db4", false, null,
            "In diesem Prequel der „Herr der Ringe“-Trilogie will Bilbo Beutlin mit einer kühnen Truppe Zwerge trotz aller Gefahren deren verlorenes Königreich zurückerob...",
            "https://images.justwatch.com/poster/176254660/s332/Der-Hobbit-Eine-unerwartete-Reise.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt0936501", "96 Hours", 2008, 7.7, 680060,
            60, 68.5,
            "Action & Abenteuer, Krimi, Mystery & Thriller", 94, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.56a9f6d9-841b-4b2c-de0e-84dcfb29e270", false, null,
            "Bryan Mills hat seinen Dienst als Agent der US-Regierung quittiert, um in der Nähe seiner 17-jährigen Tochter leben zu können. Die verwöhnte Kim wohnt bei Br...",
            "https://images.justwatch.com/poster/324608594/s332/96-Hours.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt1515091", "Sherlock Holmes: Spiel im Schatten", 2011, 7.4, 506460,
            60, 67.0,
            "Action & Abenteuer, Mystery & Thriller", 129, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.1ea9f7c7-589c-6fed-96c6-e7d96e94e09b", false, null,
            "Sherlock Holmes und Dr. Watson schrecken auch vor diesem Abenteuer nicht zurück. Werden sie mithilfe einer rätselhaften Frau einem mörderischen Verbrecher zu...",
            "https://images.justwatch.com/poster/258718998/s332/Sherlock-Holmes-Spiel-im-Schatten.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt7830574", "Ricky Gervais: Humanity", 2018, 8.0, 25431,
            47, 63.5,
            "Komödien, Dokumentationen", 79, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/80189653", false, null,
            "In Ricky Gervais' erstem Special seit sieben Jahren geht es dem Ruhm, der Sterblichkeit und einer Gesellschaft an den Kragen, die ständig alles persönlich ni...",
            "https://images.justwatch.com/poster/355001158/s332/Ricky-Gervais-Humanity.jpg",
            false, true
        ));
        masterList.add(new Movie(
            "tt10810424", "Dave Chappelle: Sticks & Stones", 2019, 8.3, 29143,
            42, 62.5,
            "Komödien", 65, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/81140577", false, null,
            "In diesem gewagten Stand-up-Special in Atlanta geht Dave Chappelle der amerikanischen Waffenkultur, der Opioidkrise und skandalträchtigen Stars an den Kragen.",
            "https://images.justwatch.com/poster/146132880/s332/Dave-Chappelle-Sticks-und-Stones.jpg",
            false, false
        ));
        masterList.add(new Movie(
            "tt39139925", "Dhurandhar: The Revenge", 2026, 8.2, 73886,
            36, 59.0,
            "Action & Abenteuer, Krimi, Mystery & Thriller", 235, StreamingProvider.NETFLIX,
            "https://www.netflix.com/title/82813021", false, null,
            "Als rivalisierende Banden, korrupte Beamte und der skrupellose Major Iqbal immer näher kommen, verwandelt sich Hamzas Mission für sein Land in einen blutigen...",
            "https://images.justwatch.com/poster/344235396/s332/dhurandhar-the-revenge.jpg",
            true, false
        ));
        masterList.add(new Movie(
            "tt1014762", "Home - Die Geschichte einer Reise", 2009, 8.5, 23393,
            17, 51.0,
            "Dokumentationen, Drama, Kinder & Familie", 95, StreamingProvider.PRIME_VIDEO,
            "https://watch.amazon.de/detail?gti=amzn1.dv.gti.5ebb9c14-575c-4378-891a-ef1b648076de", false, null,
            "Der Film besteht zum Großteil aus Luftaufnahmen aus verschiedenen Gebieten der Erde – vom tropischen Regenwald über die arktische See bis hin zu Großstädten ...",
            "https://images.justwatch.com/poster/11130023/s332/Home.jpg",
            false, true
        ));
    }

    public List<Movie> getFilteredList(
            Set<StreamingProvider> activeProviders,
            String specificProviderId,
            String selectedGenre,
            boolean excludeDoku,
            boolean excludeBollywood,
            int minYear,
            int maxYear,
            int sortMode,
            String searchQuery,
            int maxResults) {

        List<Movie> results = new ArrayList<>();
        String queryLower = searchQuery != null ? searchQuery.toLowerCase().trim() : "";

        for (Movie m : masterList) {
            // 1. Provider Filter
            if (specificProviderId != null && !specificProviderId.equalsIgnoreCase("all")) {
                if (!m.getProvider().getId().equalsIgnoreCase(specificProviderId)) {
                    continue;
                }
            } else if (activeProviders != null && !activeProviders.isEmpty()) {
                if (!activeProviders.contains(m.getProvider())) {
                    continue;
                }
            }

            // 2. Year Filter
            if (minYear > 0 && m.getYear() < minYear) {
                continue;
            }
            if (maxYear > 0 && m.getYear() > maxYear) {
                continue;
            }

            // 3. Exclusion: Dokumentation
            if (excludeDoku && m.isDoku()) {
                continue;
            }

            // 4. Exclusion: Bollywood / Indien
            if (excludeBollywood && m.isBollywood()) {
                continue;
            }

            // 5. Genre Filter (Include)
            if (selectedGenre != null && !selectedGenre.equalsIgnoreCase("Alle") && !selectedGenre.isEmpty()) {
                if (!m.getGenres().toLowerCase().contains(selectedGenre.toLowerCase())) {
                    continue;
                }
            }

            // 6. Search Query
            if (!queryLower.isEmpty()) {
                boolean matchTitle = m.getTitle().toLowerCase().contains(queryLower);
                boolean matchGenre = m.getGenres().toLowerCase().contains(queryLower);
                if (!matchTitle && !matchGenre) {
                    continue;
                }
            }

            results.add(m);
        }

        // Sorting
        Collections.sort(results, new Comparator<Movie>() {
            @Override
            public int compare(Movie a, Movie b) {
                if (sortMode == SORT_ROTTEN_TOMATOES) {
                    int c = Integer.compare(b.getRottenTomatoes(), a.getRottenTomatoes());
                    if (c != 0) return c;
                    return Double.compare(b.getImdbRating(), a.getImdbRating());
                } else if (sortMode == SORT_PERLE) {
                    int c = Double.compare(b.getCombinedScore(), a.getCombinedScore());
                    if (c != 0) return c;
                    return Integer.compare(b.getImdbVotes(), a.getImdbVotes());
                } else if (sortMode == SORT_YEAR) {
                    int c = Integer.compare(b.getYear(), a.getYear());
                    if (c != 0) return c;
                    return Double.compare(b.getImdbRating(), a.getImdbRating());
                } else {
                    // Default: SORT_IMDB
                    int c = Double.compare(b.getImdbRating(), a.getImdbRating());
                    if (c != 0) return c;
                    return Integer.compare(b.getImdbVotes(), a.getImdbVotes());
                }
            }
        });

        if (maxResults > 0 && results.size() > maxResults) {
            return new ArrayList<>(results.subList(0, maxResults));
        }
        return results;
    }

    public List<Movie> getLeavingSoon(Set<StreamingProvider> activeProviders) {
        List<Movie> results = new ArrayList<>();
        for (Movie m : masterList) {
            if (m.isLeavingSoon()) {
                if (activeProviders == null || activeProviders.isEmpty() || activeProviders.contains(m.getProvider())) {
                    results.add(m);
                }
            }
        }
        Collections.sort(results, (a, b) -> Double.compare(b.getImdbRating(), a.getImdbRating()));
        return results;
    }

    public List<Movie> getWatchlistMatches(Set<StreamingProvider> activeProviders, WatchlistManager watchlistManager) {
        List<Movie> results = new ArrayList<>();
        for (Movie m : masterList) {
            if (watchlistManager.isWatchlisted(m.getId()) || watchlistManager.isWatchlisted(m.getTitle())) {
                if (activeProviders == null || activeProviders.isEmpty() || activeProviders.contains(m.getProvider())) {
                    results.add(m);
                }
            }
        }
        Collections.sort(results, (a, b) -> Double.compare(b.getImdbRating(), a.getImdbRating()));
        return results;
    }

    public synchronized int addMovies(List<Movie> newMovies) {
        if (newMovies == null || newMovies.isEmpty()) return 0;
        int added = 0;
        for (Movie nm : newMovies) {
            boolean exists = false;
            for (Movie em : masterList) {
                if (em.getId().equals(nm.getId()) || em.getTitle().equalsIgnoreCase(nm.getTitle())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                masterList.add(nm);
                added++;
            }
        }
        return added;
    }

    public synchronized List<Movie> getFilteredList(FilterCriteria criteria, Set<StreamingProvider> activeProviders, WatchedManager watchedManager) {
        List<Movie> results = new ArrayList<>();
        for (Movie m : masterList) {
            if (criteria.matches(m, activeProviders, watchedManager)) {
                results.add(m);
            }
        }

        final int sortMode = criteria.sortMode;
        Collections.sort(results, new Comparator<Movie>() {
            @Override
            public int compare(Movie a, Movie b) {
                if (sortMode == SORT_ROTTEN_TOMATOES) {
                    int c = Integer.compare(b.getRottenTomatoes(), a.getRottenTomatoes());
                    if (c != 0) return c;
                    return Double.compare(b.getImdbRating(), a.getImdbRating());
                } else if (sortMode == SORT_PERLE) {
                    int c = Double.compare(b.getCombinedScore(), a.getCombinedScore());
                    if (c != 0) return c;
                    return Integer.compare(b.getImdbVotes(), a.getImdbVotes());
                } else if (sortMode == SORT_YEAR) {
                    int c = Integer.compare(b.getYear(), a.getYear());
                    if (c != 0) return c;
                    return Double.compare(b.getImdbRating(), a.getImdbRating());
                } else {
                    int c = Double.compare(b.getImdbRating(), a.getImdbRating());
                    if (c != 0) return c;
                    return Integer.compare(b.getImdbVotes(), a.getImdbVotes());
                }
            }
        });

        if (criteria.targetCount > 0 && results.size() > criteria.targetCount) {
            return new ArrayList<>(results.subList(0, criteria.targetCount));
        }
        return results;
    }

    public synchronized List<Movie> getFilteredList(FilterCriteria criteria, Set<StreamingProvider> activeProviders) {
        return getFilteredList(criteria, activeProviders, WatchedManager.getInstance());
    }

    public int getTotalCount() {
        return masterList.size();
    }
}
