package com.hotel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Gestió de reserves d'un hotel.
 */
public class App {

    // --------- CONSTANTS I VARIABLES GLOBALS ---------

    // Tipus d'habitació
    public static final String TIPUS_ESTANDARD = "Estàndard";
    public static final String TIPUS_SUITE = "Suite";
    public static final String TIPUS_DELUXE = "Deluxe";

    // Serveis addicionals
    public static final String SERVEI_ESMORZAR = "Esmorzar";
    public static final String SERVEI_GIMNAS = "Gimnàs";
    public static final String SERVEI_SPA = "Spa";
    public static final String SERVEI_PISCINA = "Piscina";

    // Capacitat inicial
    public static final int CAPACITAT_ESTANDARD = 30;
    public static final int CAPACITAT_SUITE = 20;
    public static final int CAPACITAT_DELUXE = 10;

    // IVA
    public static final float IVA = 0.21f;

    // Scanner únic
    public static Scanner sc = new Scanner(System.in);

    // HashMaps de consulta
    public static HashMap<String, Float> preusHabitacions = new HashMap<String, Float>();
    public static HashMap<String, Integer> capacitatInicial = new HashMap<String, Integer>();
    public static HashMap<String, Float> preusServeis = new HashMap<String, Float>();

    // HashMaps dinàmics
    public static HashMap<String, Integer> disponibilitatHabitacions = new HashMap<String, Integer>();
    public static HashMap<Integer, ArrayList<String>> reserves = new HashMap<Integer, ArrayList<String>>();

    // Generador de nombres aleatoris per als codis de reserva
    public static Random random = new Random();

    // --------- MÈTODE MAIN ---------

    /**
     * Mètode principal. Mostra el menú en un bucle i gestiona l'opció triada
     * fins que l'usuari decideix eixir.
     */
    public static void main(String[] args) {
        inicialitzarPreus();

        int opcio = 0;
        do {
            mostrarMenu();
            opcio = llegirEnter("Seleccione una opció: ");
            gestionarOpcio(opcio);
        } while (opcio != 6);

        System.out.println("Eixint del sistema... Gràcies per utilitzar el gestor de reserves!");
    }

    // --------- MÈTODES DEMANATS ---------

    /**
     * Configura els preus de les habitacions, serveis addicionals i
     * les capacitats inicials en els HashMaps corresponents.
     */
    public static void inicialitzarPreus() {
        // Preus habitacions
        preusHabitacions.put(TIPUS_ESTANDARD, 50f);
        preusHabitacions.put(TIPUS_SUITE, 100f);
        preusHabitacions.put(TIPUS_DELUXE, 150f);

        // Capacitats inicials
        capacitatInicial.put(TIPUS_ESTANDARD, CAPACITAT_ESTANDARD);
        capacitatInicial.put(TIPUS_SUITE, CAPACITAT_SUITE);
        capacitatInicial.put(TIPUS_DELUXE, CAPACITAT_DELUXE);

        // Disponibilitat inicial (comença igual que la capacitat)
        disponibilitatHabitacions.put(TIPUS_ESTANDARD, CAPACITAT_ESTANDARD);
        disponibilitatHabitacions.put(TIPUS_SUITE, CAPACITAT_SUITE);
        disponibilitatHabitacions.put(TIPUS_DELUXE, CAPACITAT_DELUXE);

        // Preus serveis
        preusServeis.put(SERVEI_ESMORZAR, 10f);
        preusServeis.put(SERVEI_GIMNAS, 15f);
        preusServeis.put(SERVEI_SPA, 20f);
        preusServeis.put(SERVEI_PISCINA, 25f);
    }

    /**
     * Mostra el menú principal amb les opcions disponibles per a l'usuari.
     */
    public static void mostrarMenu() {
        System.out.println("\n===== MENÚ PRINCIPAL =====");
        System.out.println("1. Reservar una habitació");
        System.out.println("2. Alliberar una habitació");
        System.out.println("3. Consultar disponibilitat");
        System.out.println("4. Llistar reserves per tipus");
        System.out.println("5. Obtindre una reserva");
        System.out.println("6. Ixir");
    }

    /**
     * Processa l'opció seleccionada per l'usuari i crida el mètode corresponent.
     */
    public static void gestionarOpcio(int opcio) {
        if (opcio == 1)
            reservarHabitacio();
        else if (opcio == 2)
            alliberarHabitacio();
        else if (opcio == 3)
            consultarDisponibilitat();
        else if (opcio == 4)
            obtindreReservaPerTipus();
        else if (opcio == 5)
            obtindreReserva();
        else if (opcio == 6)
            System.out.println("Adeu");
        else
            System.out.println("Opció incorrecta!");

    }

    /**
     * Gestiona tot el procés de reserva: selecció del tipus d'habitació,
     * serveis addicionals, càlcul del preu total i generació del codi de reserva.
     */
    public static void reservarHabitacio() {
        System.out.println("\n===== RESERVAR HABITACIÓ =====");
        String tipus = seleccionarTipusHabitacioDisponible();
        disponibilitatHabitacions.put(tipus, disponibilitatHabitacions.get(tipus) - 1);
        ArrayList<String> serveisSeleccionats = seleccionarServeis();
        float preu = calcularPreuTotal(tipus, serveisSeleccionats);
        int codi = generarCodiReserva();
        ArrayList<String> reserva = new ArrayList<String>();
        reserva.add(tipus);
        for (int i = 0; i < serveisSeleccionats.size(); i++)
            reserva.add(serveisSeleccionats.get(i));
        reserva.add(Float.toString(preu));
        reserves.put(codi, reserva);
        System.out.println(String.format("Habitació %s reservada amb codi %d", tipus, codi));
    }

    /**
     * Pregunta a l'usuari un tipus d'habitació en format numèric i
     * retorna el nom del tipus.
     */
    public static String seleccionarTipusHabitacio() {
        System.out.print("1. ");
        mostrarInfoTipus(TIPUS_ESTANDARD);
        System.out.print("2. ");
        mostrarInfoTipus(TIPUS_SUITE);
        System.out.print("3. ");
        mostrarInfoTipus(TIPUS_DELUXE);
        int tipus = llegirEnter("Seleccione una opció: ");
        if (tipus == 1) {
            return TIPUS_ESTANDARD;
        } else if (tipus == 2)
            return TIPUS_SUITE;
        else if (tipus == 3)
            return TIPUS_DELUXE;
        else {
            System.out.println("Opció incorrecta");
            return seleccionarTipusHabitacio();
        }
    }

    /**
     * Mostra la disponibilitat i el preu de cada tipus d'habitació,
     * demana a l'usuari un tipus i només el retorna si encara hi ha
     * habitacions disponibles. En cas contrari, retorna null.
     */
    public static String seleccionarTipusHabitacioDisponible() {
        System.out.println("\nTipus d'habitació disponibles:");
        System.out.print("1. ");
        mostrarInfoTipus(TIPUS_ESTANDARD);
        System.out.print("2. ");
        mostrarInfoTipus(TIPUS_SUITE);
        System.out.print("3. ");
        mostrarInfoTipus(TIPUS_DELUXE);
        int tipus = llegirEnter("Seleccione una opció: ");
        if (tipus == 1) {
            if (disponibilitatHabitacions.get(TIPUS_ESTANDARD) == 0)
                System.out.println("No disponibles");
            else
                return TIPUS_ESTANDARD;
        } else if (tipus == 2)
            if (disponibilitatHabitacions.get(TIPUS_SUITE) == 0)
                System.out.println("No disponibles");
            else
                return TIPUS_SUITE;
        else if (tipus == 3)
            if (disponibilitatHabitacions.get(TIPUS_DELUXE) == 0)
                System.out.println("No disponibles");
            else
                return TIPUS_DELUXE;

        System.out.println("Opció incorrecta");
        return seleccionarTipusHabitacio();
    }

    /**
     * Permet triar serveis addicionals (entre 0 i 4, sense repetir) i
     * els retorna en un ArrayList de String.
     */
    public static ArrayList<String> seleccionarServeis() {
        int servei;
        ArrayList<String> serveisSeleccionats = new ArrayList<String>();
        System.out.println("Aquestos son els serveis disponibles:");
        do {
            System.out.println(String.format("1.- %s", SERVEI_ESMORZAR));
            System.out.println(String.format("2.- %s", SERVEI_GIMNAS));
            System.out.println(String.format("3.- %s", SERVEI_SPA));
            System.out.println(String.format("4.- %s", SERVEI_PISCINA));
            System.out.println("5.- Cap");
            servei = llegirEnter("Seleccione una opció: ");
            if (servei < 1 || servei > 5)
                System.out.println("Opció Incorrecta");
            else if (servei == 1 && !serveisSeleccionats.contains(SERVEI_ESMORZAR))
                serveisSeleccionats.add(SERVEI_ESMORZAR);
            else if (servei == 2 && !serveisSeleccionats.contains(SERVEI_GIMNAS))
                serveisSeleccionats.add(SERVEI_GIMNAS);
            else if (servei == 3 && !serveisSeleccionats.contains(SERVEI_SPA))
                serveisSeleccionats.add(SERVEI_SPA);
            else if (servei == 4 && !serveisSeleccionats.contains(SERVEI_PISCINA))
                serveisSeleccionats.add(SERVEI_PISCINA);
            else if (servei == 5)
                return serveisSeleccionats;
        } while (true);

    }

    /**
     * Calcula i retorna el cost total de la reserva, incloent l'habitació,
     * els serveis seleccionats i l'IVA.
     */
    public static float calcularPreuTotal(String tipusHabitacio, ArrayList<String> serveisSeleccionats) {
        float preu = preusHabitacions.get(tipusHabitacio);
        for (int i = 0; i < serveisSeleccionats.size(); i++) {
            preu += preusServeis.get(serveisSeleccionats.get(i));
        }
        return preu + (preu * IVA);
    }

    /**
     * Genera i retorna un codi de reserva únic de tres xifres
     * (entre 100 i 999) que no estiga repetit.
     */
    public static int generarCodiReserva() {
        int min = 100;
        int max = 999;
        int codi = 0;
        do {
            codi = random.nextInt(max - min + 1) + min;
        } while (reserves.containsKey(codi));
        return codi;

    }

    /**
     * Permet alliberar una habitació utilitzant el codi de reserva
     * i actualitza la disponibilitat.
     */
    public static void alliberarHabitacio() {
        System.out.println("\n===== ALLIBERAR HABITACIÓ =====");
        int codi = llegirEnter("Introdueix el codi de reserva:\n");
        if (reserves.containsKey(codi)) {
            ArrayList<String> reserva = reserves.get(codi);
            String tipusHabitacio = reserva.get(0);
            disponibilitatHabitacions.put(tipusHabitacio, disponibilitatHabitacions.get(tipusHabitacio) + 1);
            reserves.remove(codi);
        } else {
            System.out.println("Reserva no encontrada");
        }
    }

    /**
     * Mostra la disponibilitat actual de les habitacions (lliures i ocupades).
     */
    public static void consultarDisponibilitat() {
        System.out.println("TIPUS\t\tLLIURES\tOCUPADES");
        mostrarDisponibilitatTipus(TIPUS_ESTANDARD);
        mostrarDisponibilitatTipus(TIPUS_SUITE);
        mostrarDisponibilitatTipus(TIPUS_DELUXE);
    }

    /**
     * Funció recursiva. Mostra les dades de totes les reserves
     * associades a un tipus d'habitació.
     */
    public static void llistarReservesPerTipus(int[] codis, String tipus) {
        if (reserves.get(codis[0]).get(0) == tipus) {
            mostrarDadesReserva(codis[0]);
        }
        if (codis.length > 1) {
            int[] newCodis = new int[codis.length - 1];
            System.arraycopy(codis, 1, newCodis, 0, newCodis.length);
            llistarReservesPerTipus(newCodis, tipus);
        }
    }

    /**
     * Permet consultar els detalls d'una reserva introduint el codi.
     */
    public static void obtindreReserva() {
        System.out.println("\n===== CONSULTAR RESERVA =====");
        int codi = llegirEnter("Indrodueix codi de reserva: \n");
        mostrarDadesReserva(codi);
    }

    /**
     * Mostra totes les reserves existents per a un tipus d'habitació
     * específic.
     */
    public static void obtindreReservaPerTipus() {
        System.out.println("\n===== CONSULTAR RESERVES PER TIPUS =====");
        String tipus = seleccionarTipusHabitacio();
        int[] codis = new int[reserves.size()];
        int i = 0;
        for (Map.Entry<Integer, ArrayList<String>> reserva : reserves.entrySet()) {
            codis[i] = reserva.getKey();
            i++;
        }
        llistarReservesPerTipus(codis, tipus);
    }

    /**
     * Consulta i mostra en detall la informació d'una reserva.
     */
    public static void mostrarDadesReserva(int codi) {
        ArrayList<String> reserva = reserves.get(codi);
        System.out.println("Tipus: " + reserva.get(0));
        System.out.println("Serveis: ");
        for (int i = 1; i < reserva.size() - 1; i++)
            System.out.println("\t" + reserva.get(i));
        System.out.println("Preu: " + reserva.get(reserva.size() - 1) + "€");

    }

    // --------- MÈTODES AUXILIARS (PER MILLORAR LEGIBILITAT) ---------

    /**
     * Llig un enter per teclat mostrant un missatge i gestiona possibles
     * errors d'entrada.
     */
    static int llegirEnter(String missatge) {
        int valor = 0;
        boolean correcte = false;
        while (!correcte) {
            System.out.print(missatge);
            valor = sc.nextInt();
            correcte = true;
        }
        return valor;
    }

    /**
     * Mostra per pantalla informació d'un tipus d'habitació: preu i
     * habitacions disponibles.
     */
    static void mostrarInfoTipus(String tipus) {
        int disponibles = disponibilitatHabitacions.get(tipus);
        int capacitat = capacitatInicial.get(tipus);
        float preu = preusHabitacions.get(tipus);
        System.out.println("- " + tipus + " (" + disponibles + " disponibles de " + capacitat + ") - " + preu + "€");
    }

    /**
     * Mostra la disponibilitat (lliures i ocupades) d'un tipus d'habitació.
     */
    static void mostrarDisponibilitatTipus(String tipus) {
        int lliures = disponibilitatHabitacions.get(tipus);
        int capacitat = capacitatInicial.get(tipus);
        int ocupades = capacitat - lliures;

        String etiqueta = tipus;
        if (etiqueta.length() < 8) {
            etiqueta = etiqueta + "\t"; // per a quadrar la taula
        }

        System.out.println(etiqueta + "\t" + lliures + "\t" + ocupades);
    }
}
