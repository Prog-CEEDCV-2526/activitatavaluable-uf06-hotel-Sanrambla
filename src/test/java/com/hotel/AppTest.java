package com.hotel;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AppTest {

    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        originalIn = System.in;

        // Netejar estat global
        App.preusHabitacions.clear();
        App.capacitatInicial.clear();
        App.preusServeis.clear();
        App.disponibilitatHabitacions.clear();
        App.reserves.clear();

        // Re-inicialitzar preus i disponibilitats
        App.inicialitzarPreus();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        App.sc = new Scanner(System.in);
    }

    // ---------- HELPERS ----------

    private void setInput(String data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        System.setIn(in);
        App.sc = new Scanner(System.in);
    }

    private String captureOutput(Runnable runnable) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        runnable.run();
        System.out.flush();
        return baos.toString();
    }

    // ---------- TEST PER A CADA MÈTODE ----------

    // 1) main(String[])

    @Test
    void testMainBucleIxit() {
        // Simulem que l’alumne tria directament l’opció 6 (Ixir)
        setInput("6\n");

        String out = captureOutput(() -> App.main(new String[] {}));

        assertTrue(out.contains("MENÚ PRINCIPAL"));
        assertTrue(out.contains("Eixint del sistema"));
    }

    // 2) inicialitzarPreus()

    @Test
    void testInicialitzarPreus() {
        App.preusHabitacions.clear();
        App.capacitatInicial.clear();
        App.preusServeis.clear();
        App.disponibilitatHabitacions.clear();

        App.inicialitzarPreus();

        // Habitacions
        assertEquals(3, App.preusHabitacions.size());
        assertEquals(50f, App.preusHabitacions.get(App.TIPUS_ESTANDARD), 0.01f);
        assertEquals(100f, App.preusHabitacions.get(App.TIPUS_SUITE), 0.01f);
        assertEquals(150f, App.preusHabitacions.get(App.TIPUS_DELUXE), 0.01f);

        // Capacitat i disponibilitat
        assertEquals(App.CAPACITAT_ESTANDARD, App.capacitatInicial.get(App.TIPUS_ESTANDARD));
        assertEquals(App.CAPACITAT_SUITE, App.capacitatInicial.get(App.TIPUS_SUITE));
        assertEquals(App.CAPACITAT_DELUXE, App.capacitatInicial.get(App.TIPUS_DELUXE));

        assertEquals(App.CAPACITAT_ESTANDARD, App.disponibilitatHabitacions.get(App.TIPUS_ESTANDARD));
        assertEquals(App.CAPACITAT_SUITE, App.disponibilitatHabitacions.get(App.TIPUS_SUITE));
        assertEquals(App.CAPACITAT_DELUXE, App.disponibilitatHabitacions.get(App.TIPUS_DELUXE));

        // Serveis
        assertEquals(10f, App.preusServeis.get(App.SERVEI_ESMORZAR), 0.01f);
        assertEquals(15f, App.preusServeis.get(App.SERVEI_GIMNAS), 0.01f);
        assertEquals(20f, App.preusServeis.get(App.SERVEI_SPA), 0.01f);
        assertEquals(25f, App.preusServeis.get(App.SERVEI_PISCINA), 0.01f);
    }

    // 3) mostrarMenu()

    @Test
    void testMostrarMenu() {
        String out = captureOutput(() -> App.mostrarMenu());

        assertTrue(out.contains("MENÚ PRINCIPAL"));
        assertTrue(out.contains("1. Reservar una habitació"));
        assertTrue(out.contains("6. Ixir"));
    }

    // 4) gestionarOpcio(int opcio)

    @Test
    void testGestionarOpcioInvalida() {
        String out = captureOutput(() -> App.gestionarOpcio(99));
        assertTrue(out.contains("Opció no vàlida"));
    }

    // 5) reservarHabitacio()

    @Test
    void testReservarHabitacioCreaReservaIDisminueixDisponibilitat() {
        int abans = App.disponibilitatHabitacions.get(App.TIPUS_ESTANDARD);

        // Flux esperat segons la solució:
        // seleccionarTipusHabitacioDisponible() -> escollim 1 (Estàndard)
        // seleccionarServeis() -> no volem serveis
        // Per a seleccionarServeis, segons la implementació de mostra,
        // fa un nextLine buid i després "n"
        setInput("1\n\nn\n");

        String out = captureOutput(() -> App.reservarHabitacio());

        // Ha de crear almenys una reserva
        assertFalse(App.reserves.isEmpty(), "No s'ha creat cap reserva");
        assertTrue(out.contains("Codi de reserva"), "No s'ha mostrat el codi de reserva");

        int després = App.disponibilitatHabitacions.get(App.TIPUS_ESTANDARD);
        assertEquals(abans - 1, després);
    }

    // 6) seleccionarTipusHabitacio()

    @Test
    void testSeleccionarTipusHabitacioSuite() {
        setInput("2\n");
        String tipus = App.seleccionarTipusHabitacio();
        assertEquals(App.TIPUS_SUITE, tipus);
    }

    // 7) seleccionarTipusHabitacioDisponible()

    @Test
    void testSeleccionarTipusHabitacioDisponibleAmbDisponibilitat() {
        // Ja està tot inicialitzat en @BeforeEach
        setInput("1\n");
        String tipus = App.seleccionarTipusHabitacioDisponible();
        assertEquals(App.TIPUS_ESTANDARD, tipus);
    }

    @Test
    void testSeleccionarTipusHabitacioDisponibleSenseDisponibilitat() {
        // Deixem el tipus Suite a 0 disponibles
        App.disponibilitatHabitacions.put(App.TIPUS_SUITE, 0);

        setInput("2\n");
        String tipus = App.seleccionarTipusHabitacioDisponible();
        assertNull(tipus);
    }

    // 8) seleccionarServeis()

    @Test
    void testSeleccionarServeisSenseCapServei() {
        // Segons la solució: primer nextLine buid, després resposta "n"
        setInput("\n" + "n\n");
        ArrayList<String> serveis = App.seleccionarServeis();
        assertNotNull(serveis);
        assertEquals(0, serveis.size());
    }

    // 9) calcularPreuTotal(String, ArrayList<String>)

    @Test
    void testCalcularPreuTotalHabitacioMesServei() {
        ArrayList<String> serveis = new ArrayList<>();
        serveis.add(App.SERVEI_ESMORZAR); // 10€

        float total = App.calcularPreuTotal(App.TIPUS_ESTANDARD, serveis);
        // 50 + 10 = 60; amb IVA 21% -> 72.6
        assertEquals(72.6f, total, 0.05f);
    }

    // 10) generarCodiReserva()

    @Test
    void testGenerarCodiReservaRangIUnic() {
        Set<Integer> codis = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            int codi = App.generarCodiReserva();
            assertTrue(codi >= 100 && codi <= 999, "Codi fora de rang: " + codi);
            assertFalse(codis.contains(codi), "Codi repetit: " + codi);
            codis.add(codi);
            // Guardem reserva buida per a obligar a generar un altre codi
            App.reserves.put(codi, new ArrayList<>());
        }
    }

    // 11) alliberarHabitacio()

    @Test
    void testAlliberarHabitacioIncrementaDisponibilitatIEliminaReserva() {
        int codi = 123;
        ArrayList<String> dades = new ArrayList<>();
        dades.add(App.TIPUS_ESTANDARD);
        dades.add("72.60");
        App.reserves.put(codi, dades);

        App.disponibilitatHabitacions.put(App.TIPUS_ESTANDARD, App.CAPACITAT_ESTANDARD - 1);

        setInput(codi + "\n");

        captureOutput(() -> App.alliberarHabitacio());

        assertFalse(App.reserves.containsKey(codi));
        assertEquals(App.CAPACITAT_ESTANDARD,
                App.disponibilitatHabitacions.get(App.TIPUS_ESTANDARD).intValue());
    }

    // 12) consultarDisponibilitat()

    @Test
    void testConsultarDisponibilitatMostraTaula() {
        String out = captureOutput(() -> App.consultarDisponibilitat());
        assertTrue(out.contains("Tipus"));
        assertTrue(out.contains(App.TIPUS_ESTANDARD));
        assertTrue(out.contains(App.TIPUS_SUITE));
        assertTrue(out.contains(App.TIPUS_DELUXE));
    }

    // 13) llistarReservesPerTipus(int[] codis, String tipus)

    @Test
    void testLlistarReservesPerTipusFiltraCorrectament() {
        // Muntem dues reserves: una Estàndard i una Suite
        ArrayList<String> r1 = new ArrayList<>();
        r1.add(App.TIPUS_ESTANDARD);
        r1.add("70.0");
        App.reserves.put(111, r1);

        ArrayList<String> r2 = new ArrayList<>();
        r2.add(App.TIPUS_SUITE);
        r2.add("120.0");
        App.reserves.put(222, r2);

        int[] codis = { 111, 222 };

        String out = captureOutput(() -> App.llistarReservesPerTipus(codis, App.TIPUS_ESTANDARD));

        // Esperem que només aparega la reserva 111
        assertTrue(out.contains("111"));
        assertFalse(out.contains("222"));
    }

    // 14) obtindreReserva()

    @Test
    void testObtindreReservaMostraReserva() {
        int codi = 321;
        ArrayList<String> dades = new ArrayList<>();
        dades.add(App.TIPUS_DELUXE);
        dades.add("200.0");
        App.reserves.put(codi, dades);

        setInput(codi + "\n");

        String out = captureOutput(() -> App.obtindreReserva());

        assertTrue(out.contains(String.valueOf(codi)));
        assertTrue(out.contains(App.TIPUS_DELUXE));
    }

    // 15) obtindreReservaPerTipus()

    @Test
    void testObtindreReservaPerTipusMostraNomésEixeTipus() {
        // Una reserva Estàndard i una Suite
        ArrayList<String> r1 = new ArrayList<>();
        r1.add(App.TIPUS_ESTANDARD);
        r1.add("70.0");
        App.reserves.put(400, r1);

        ArrayList<String> r2 = new ArrayList<>();
        r2.add(App.TIPUS_SUITE);
        r2.add("120.0");
        App.reserves.put(500, r2);

        // seleccionarTipusHabitacio() -> triem 1 (Estàndard)
        setInput("1\n");

        String out = captureOutput(() -> App.obtindreReservaPerTipus());

        assertTrue(out.contains("400"));
        assertFalse(out.contains("500"));
    }

    // 16) mostrarDadesReserva(int codi)

    @Test
    void testMostrarDadesReserva() {
        int codi = 777;
        ArrayList<String> dades = new ArrayList<>();
        dades.add(App.TIPUS_SUITE);
        dades.add("150.0");
        dades.add(App.SERVEI_SPA); // posició 2
        dades.add(""); // posició 3
        dades.add(""); // posició 4
        dades.add(""); // posició 5
        App.reserves.put(codi, dades);

        String out = captureOutput(() -> App.mostrarDadesReserva(codi));

        assertTrue(out.contains(String.valueOf(codi)), "Codi no encontrado: " + codi);
        assertTrue(out.contains(App.TIPUS_SUITE), "TIPUS_SUITE no encontrado");
        assertTrue(out.contains("150"), "Precio no encontrado");
        assertTrue(out.contains(App.SERVEI_SPA), "SERVEI_SPA no encontrado");
    }
}
