package szakdolgozat.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsoatmeroOptimalizalo {

	// Nyomásesés 100 méteren
	// Az elsõ kulcs a vízfogyasztás értéke (l/perc), az ehhez tartozó mapban
	// tárolódik, hogy
	// milyen átmérõjû csõ használatánál mekkora a nyomásesés
	// forrás: http://ozonviz.hu/ontozo/nyomaseses.pdf
	private static final Map<Double, Map<String, Double>> nyomasEses = new HashMap<Double, Map<String, Double>>();
	static {
		nyomasEses.put(3.3, new HashMap<String, Double>());
		nyomasEses.get(3.3).put("20", 0.06);
		nyomasEses.get(3.3).put("25", 0.03);
		nyomasEses.get(3.3).put("32", 0.01);
		nyomasEses.get(3.3).put("40", 0.0);

		nyomasEses.put(6.7, new HashMap<String, Double>());
		nyomasEses.get(6.7).put("20", 0.3);
		nyomasEses.get(6.7).put("25", 0.09);
		nyomasEses.get(6.7).put("32", 0.03);
		nyomasEses.get(6.7).put("40", 0.01);
		nyomasEses.get(6.7).put("50", 0.0);

		nyomasEses.put(10.0, new HashMap<String, Double>());
		nyomasEses.get(10.0).put("20", 0.63);
		nyomasEses.get(10.0).put("25", 0.19);
		nyomasEses.get(10.0).put("32", 0.06);
		nyomasEses.get(10.0).put("40", 0.02);
		nyomasEses.get(10.0).put("50", 0.01);
		nyomasEses.get(10.0).put("63", 0.0);

		nyomasEses.put(13.3, new HashMap<String, Double>());
		nyomasEses.get(13.3).put("20", 1.07);
		nyomasEses.get(13.3).put("25", 0.33);
		nyomasEses.get(13.3).put("32", 0.1);
		nyomasEses.get(13.3).put("40", 0.03);
		nyomasEses.get(13.3).put("50", 0.01);
		nyomasEses.get(13.3).put("63", 0.0);

		nyomasEses.put(16.7, new HashMap<String, Double>());
		nyomasEses.get(16.7).put("25", 0.49);
		nyomasEses.get(16.7).put("32", 0.15);
		nyomasEses.get(16.7).put("40", 0.05);
		nyomasEses.get(16.7).put("50", 0.02);
		nyomasEses.get(16.7).put("63", 0.01);
		nyomasEses.get(16.7).put("75", 0.00);

		nyomasEses.put(20.0, new HashMap<String, Double>());
		nyomasEses.get(20.0).put("25", 0.69);
		nyomasEses.get(20.0).put("32", 0.21);
		nyomasEses.get(20.0).put("40", 0.07);
		nyomasEses.get(20.0).put("50", 0.02);
		nyomasEses.get(20.0).put("63", 0.01);
		nyomasEses.get(20.0).put("75", 0.00);

		nyomasEses.put(23.3, new HashMap<String, Double>());
		nyomasEses.get(23.3).put("25", 0.92);
		nyomasEses.get(23.3).put("32", 0.27);
		nyomasEses.get(23.3).put("40", 0.09);
		nyomasEses.get(23.3).put("50", 0.03);
		nyomasEses.get(23.3).put("63", 0.01);
		nyomasEses.get(23.3).put("75", 0.00);

		nyomasEses.put(26.7, new HashMap<String, Double>());
		nyomasEses.get(26.7).put("32", 0.35);
		nyomasEses.get(26.7).put("40", 0.12);
		nyomasEses.get(26.7).put("50", 0.04);
		nyomasEses.get(26.7).put("63", 0.01);
		nyomasEses.get(26.7).put("75", 0.01);
		nyomasEses.get(26.7).put("90", 0.00);

		nyomasEses.put(30.0, new HashMap<String, Double>());
		nyomasEses.get(30.0).put("32", 0.43);
		nyomasEses.get(30.0).put("40", 0.15);
		nyomasEses.get(30.0).put("50", 0.05);
		nyomasEses.get(30.0).put("63", 0.02);
		nyomasEses.get(30.0).put("75", 0.01);
		nyomasEses.get(30.0).put("90", 0.00);

		nyomasEses.put(33.3, new HashMap<String, Double>());
		nyomasEses.get(33.3).put("32", 0.53);
		nyomasEses.get(33.3).put("40", 0.18);
		nyomasEses.get(33.3).put("50", 0.06);
		nyomasEses.get(33.3).put("63", 0.02);
		nyomasEses.get(33.3).put("75", 0.01);
		nyomasEses.get(33.3).put("90", 0.00);

		nyomasEses.put(36.7, new HashMap<String, Double>());
		nyomasEses.get(36.7).put("32", 0.63);
		nyomasEses.get(36.7).put("40", 0.21);
		nyomasEses.get(36.7).put("50", 0.07);
		nyomasEses.get(36.7).put("63", 0.02);
		nyomasEses.get(36.7).put("75", 0.01);
		nyomasEses.get(36.7).put("90", 0.00);

		nyomasEses.put(40.0, new HashMap<String, Double>());
		nyomasEses.get(40.0).put("32", 0.74);
		nyomasEses.get(40.0).put("40", 0.25);
		nyomasEses.get(40.0).put("50", 0.08);
		nyomasEses.get(40.0).put("63", 0.03);
		nyomasEses.get(40.0).put("75", 0.01);
		nyomasEses.get(40.0).put("90", 0.00);

		nyomasEses.put(43.3, new HashMap<String, Double>());
		nyomasEses.get(43.3).put("32", 0.86);
		nyomasEses.get(43.3).put("40", 0.29);
		nyomasEses.get(43.3).put("50", 0.10);
		nyomasEses.get(43.3).put("63", 0.03);
		nyomasEses.get(43.3).put("75", 0.01);
		nyomasEses.get(43.3).put("90", 0.01);
		nyomasEses.get(43.3).put("110", 0.00);

		nyomasEses.put(46.7, new HashMap<String, Double>());
		nyomasEses.get(46.7).put("32", 0.99);
		nyomasEses.get(46.7).put("40", 0.33);
		nyomasEses.get(46.7).put("50", 0.11);
		nyomasEses.get(46.7).put("63", 0.04);
		nyomasEses.get(46.7).put("75", 0.02);
		nyomasEses.get(46.7).put("90", 0.01);
		nyomasEses.get(46.7).put("110", 0.00);

		nyomasEses.put(50.0, new HashMap<String, Double>());
		nyomasEses.get(50.0).put("40", 0.38);
		nyomasEses.get(50.0).put("50", 0.13);
		nyomasEses.get(50.0).put("63", 0.04);
		nyomasEses.get(50.0).put("75", 0.02);
		nyomasEses.get(50.0).put("90", 0.01);
		nyomasEses.get(50.0).put("110", 0.00);

		nyomasEses.put(53.3, new HashMap<String, Double>());
		nyomasEses.get(53.3).put("40", 0.42);
		nyomasEses.get(53.3).put("50", 0.14);
		nyomasEses.get(53.3).put("63", 0.05);
		nyomasEses.get(53.3).put("75", 0.02);
		nyomasEses.get(53.3).put("90", 0.01);
		nyomasEses.get(53.3).put("110", 0.00);

		nyomasEses.put(56.7, new HashMap<String, Double>());
		nyomasEses.get(56.7).put("40", 0.47);
		nyomasEses.get(56.7).put("50", 0.16);
		nyomasEses.get(56.7).put("63", 0.05);
		nyomasEses.get(56.7).put("75", 0.02);
		nyomasEses.get(56.7).put("90", 0.01);
		nyomasEses.get(56.7).put("110", 0.01);

		nyomasEses.put(60.0, new HashMap<String, Double>());
		nyomasEses.get(60.0).put("40", 0.53);
		nyomasEses.get(60.0).put("50", 0.18);
		nyomasEses.get(60.0).put("63", 0.06);
		nyomasEses.get(60.0).put("75", 0.02);
		nyomasEses.get(60.0).put("90", 0.01);
		nyomasEses.get(60.0).put("110", 0.00);

		nyomasEses.put(63.3, new HashMap<String, Double>());
		nyomasEses.get(63.3).put("40", 0.58);
		nyomasEses.get(63.3).put("50", 0.20);
		nyomasEses.get(63.3).put("63", 0.06);
		nyomasEses.get(63.3).put("75", 0.03);
		nyomasEses.get(63.3).put("90", 0.01);
		nyomasEses.get(63.3).put("110", 0.00);

		nyomasEses.put(66.7, new HashMap<String, Double>());
		nyomasEses.get(66.7).put("40", 0.64);
		nyomasEses.get(66.7).put("50", 0.22);
		nyomasEses.get(66.7).put("63", 0.07);
		nyomasEses.get(66.7).put("75", 0.03);
		nyomasEses.get(66.7).put("90", 0.01);
		nyomasEses.get(66.7).put("110", 0.00);

		nyomasEses.put(75.0, new HashMap<String, Double>());
		nyomasEses.get(75.0).put("40", 0.80);
		nyomasEses.get(75.0).put("50", 0.27);
		nyomasEses.get(75.0).put("63", 0.09);
		nyomasEses.get(75.0).put("75", 0.04);
		nyomasEses.get(75.0).put("90", 0.02);
		nyomasEses.get(75.0).put("110", 0.01);

		nyomasEses.put(83.3, new HashMap<String, Double>());
		nyomasEses.get(83.3).put("40", 0.97);
		nyomasEses.get(83.3).put("50", 0.33);
		nyomasEses.get(83.3).put("63", 0.11);
		nyomasEses.get(83.3).put("75", 0.05);
		nyomasEses.get(83.3).put("90", 0.02);
		nyomasEses.get(83.3).put("110", 0.01);

		nyomasEses.put(91.7, new HashMap<String, Double>());
		nyomasEses.get(91.7).put("50", 0.39);
		nyomasEses.get(91.7).put("63", 0.13);
		nyomasEses.get(91.7).put("75", 0.05);
		nyomasEses.get(91.7).put("90", 0.02);
		nyomasEses.get(91.7).put("110", 0.01);

		nyomasEses.put(100.0, new HashMap<String, Double>());
		nyomasEses.get(100.0).put("50", 0.46);
		nyomasEses.get(100.0).put("63", 0.15);
		nyomasEses.get(100.0).put("75", 0.06);
		nyomasEses.get(100.0).put("90", 0.03);
		nyomasEses.get(100.0).put("110", 0.01);

		nyomasEses.put(108.3, new HashMap<String, Double>());
		nyomasEses.get(108.3).put("50", 0.53);
		nyomasEses.get(108.3).put("63", 0.17);
		nyomasEses.get(108.3).put("75", 0.07);
		nyomasEses.get(108.3).put("90", 0.03);
		nyomasEses.get(108.3).put("110", 0.01);

		nyomasEses.put(116.7, new HashMap<String, Double>());
		nyomasEses.get(116.7).put("50", 0.61);
		nyomasEses.get(116.7).put("63", 0.20);
		nyomasEses.get(116.7).put("75", 0.09);
		nyomasEses.get(116.7).put("90", 0.04);
		nyomasEses.get(116.7).put("110", 0.01);

		nyomasEses.put(125.0, new HashMap<String, Double>());
		nyomasEses.get(125.0).put("50", 0.69);
		nyomasEses.get(125.0).put("63", 0.23);
		nyomasEses.get(125.0).put("75", 0.10);
		nyomasEses.get(125.0).put("90", 0.04);
		nyomasEses.get(125.0).put("110", 0.02);

		nyomasEses.put(133.3, new HashMap<String, Double>());
		nyomasEses.get(133.3).put("50", 0.78);
		nyomasEses.get(133.3).put("63", 0.25);
		nyomasEses.get(133.3).put("75", 0.11);
		nyomasEses.get(133.3).put("90", 0.05);
		nyomasEses.get(133.3).put("110", 0.02);

		nyomasEses.put(141.7, new HashMap<String, Double>());
		nyomasEses.get(141.7).put("50", 0.87);
		nyomasEses.get(141.7).put("63", 0.28);
		nyomasEses.get(141.7).put("75", 0.12);
		nyomasEses.get(141.7).put("90", 0.05);
		nyomasEses.get(141.7).put("110", 0.02);

		nyomasEses.put(150.0, new HashMap<String, Double>());
		nyomasEses.get(150.0).put("50", 0.97);
		nyomasEses.get(150.0).put("63", 0.32);
		nyomasEses.get(150.0).put("75", 0.14);
		nyomasEses.get(150.0).put("90", 0.06);
		nyomasEses.get(150.0).put("110", 0.02);

		nyomasEses.put(158.3, new HashMap<String, Double>());
		nyomasEses.get(158.3).put("63", 0.35);
		nyomasEses.get(158.3).put("75", 0.14);
		nyomasEses.get(158.3).put("90", 0.06);
		nyomasEses.get(158.3).put("110", 0.02);

		nyomasEses.put(166.7, new HashMap<String, Double>());
		nyomasEses.get(166.7).put("63", 0.38);
		nyomasEses.get(166.7).put("75", 0.17);
		nyomasEses.get(166.7).put("90", 0.07);
		nyomasEses.get(166.7).put("110", 0.03);

		nyomasEses.put(183.3, new HashMap<String, Double>());
		nyomasEses.get(183.3).put("63", 0.46);
		nyomasEses.get(183.3).put("75", 0.20);
		nyomasEses.get(183.3).put("90", 0.08);
		nyomasEses.get(183.3).put("110", 0.03);

		nyomasEses.put(200.0, new HashMap<String, Double>());
		nyomasEses.get(200.0).put("63", 0.54);
		nyomasEses.get(200.0).put("75", 0.23);
		nyomasEses.get(200.0).put("90", 0.10);
		nyomasEses.get(200.0).put("110", 0.04);

		nyomasEses.put(216.7, new HashMap<String, Double>());
		nyomasEses.get(216.7).put("63", 0.63);
		nyomasEses.get(216.7).put("75", 0.27);
		nyomasEses.get(216.7).put("90", 0.11);
		nyomasEses.get(216.7).put("110", 0.04);

		nyomasEses.put(233.3, new HashMap<String, Double>());
		nyomasEses.get(233.3).put("63", 0.72);
		nyomasEses.get(233.3).put("75", 0.31);
		nyomasEses.get(233.3).put("90", 0.13);
		nyomasEses.get(233.3).put("110", 0.05);

		nyomasEses.put(250.0, new HashMap<String, Double>());
		nyomasEses.get(250.0).put("63", 0.82);
		nyomasEses.get(250.0).put("75", 0.35);
		nyomasEses.get(250.0).put("90", 0.14);
		nyomasEses.get(250.0).put("110", 0.05);

		nyomasEses.put(266.7, new HashMap<String, Double>());
		nyomasEses.get(266.7).put("75", 0.39);
		nyomasEses.get(266.7).put("90", 0.16);
		nyomasEses.get(266.7).put("110", 0.06);

		nyomasEses.put(283.3, new HashMap<String, Double>());
		nyomasEses.get(283.3).put("75", 0.44);
		nyomasEses.get(283.3).put("90", 0.18);
		nyomasEses.get(283.3).put("110", 0.07);

		nyomasEses.put(300.0, new HashMap<String, Double>());
		nyomasEses.get(300.0).put("75", 0.49);
		nyomasEses.get(300.0).put("90", 0.20);
		nyomasEses.get(300.0).put("110", 0.08);

		nyomasEses.put(316.7, new HashMap<String, Double>());
		nyomasEses.get(316.7).put("75", 0.54);
		nyomasEses.get(316.7).put("90", 0.22);
		nyomasEses.get(316.7).put("110", 0.08);

		nyomasEses.put(333.3, new HashMap<String, Double>());
		nyomasEses.get(333.3).put("75", 0.60);
		nyomasEses.get(333.3).put("90", 0.25);
		nyomasEses.get(333.3).put("110", 0.09);

		nyomasEses.put(366.7, new HashMap<String, Double>());
		nyomasEses.get(366.7).put("90", 0.29);
		nyomasEses.get(366.7).put("110", 0.11);

		nyomasEses.put(400.0, new HashMap<String, Double>());
		nyomasEses.get(400.0).put("90", 0.35);
		nyomasEses.get(400.0).put("110", 0.13);

		nyomasEses.put(433.3, new HashMap<String, Double>());
		nyomasEses.get(433.3).put("90", 0.40);
		nyomasEses.get(433.3).put("110", 0.15);

		nyomasEses.put(466.7, new HashMap<String, Double>());
		nyomasEses.get(466.7).put("90", 0.46);
		nyomasEses.get(466.7).put("110", 0.17);

		nyomasEses.put(500.0, new HashMap<String, Double>());
		nyomasEses.get(500.0).put("90", 0.52);
		nyomasEses.get(500.0).put("110", 0.20);

		nyomasEses.put(533.3, new HashMap<String, Double>());
		nyomasEses.get(533.3).put("110", 0.22);

		nyomasEses.put(566.7, new HashMap<String, Double>());
		nyomasEses.get(566.7).put("110", 0.25);

		nyomasEses.put(600.0, new HashMap<String, Double>());
		nyomasEses.get(600.0).put("110", 0.28);

		nyomasEses.put(633.3, new HashMap<String, Double>());
		nyomasEses.get(633.3).put("110", 0.30);

		nyomasEses.put(666.7, new HashMap<String, Double>());
		nyomasEses.get(666.7).put("110", 0.33);

		nyomasEses.put(750.0, new HashMap<String, Double>());
		nyomasEses.get(750.0).put("110", 0.42);
	}

	private double nearestKey(Map<Double, Map<String, Double>> map, double target) {
		double minDiff = Double.MAX_VALUE;
		double nearest = 0;
		for (double key : map.keySet()) {
			double diff = Math.abs(target - key);
			if (diff < minDiff) {
				nearest = key;
				minDiff = diff;
			}
		}
		return nearest;
	}

	public List<String> optimalisCsovek(double kezdetiNyomas, ArrayList<Double> csoSzakaszok,
			ArrayList<Double> kumulaltVizfogyasztas, ArrayList<Double> minimalisNyomas) {

		double[][] data = new double[csoSzakaszok.size() * 2 + 1][csoSzakaszok.size() + 1];
		for (int i = 0; i < csoSzakaszok.size() * 2 + 1; i++) {
			for (int j = 0; j < csoSzakaszok.size() * 3 + 1; j++) {

				// elsõ négyzet
				if (i >= j && i < csoSzakaszok.size() && j < csoSzakaszok.size()) {
					data[i][j] = 1;
				} else if (i < j && i < csoSzakaszok.size() && j < csoSzakaszok.size()) {
					data[i][j] = 0;
				}
				// elsõ négyzet végén oszlop
				else if (i < csoSzakaszok.size() && j == csoSzakaszok.size()) {
					data[i][j] = kezdetiNyomas - minimalisNyomas.get(i);
				}
				// második négyzet
				else if (i - csoSzakaszok.size() == j && i < csoSzakaszok.size() * 2 && i >= csoSzakaszok.size()
						&& j < csoSzakaszok.size()) {
					data[i][j] = 1;
				} else if (i - csoSzakaszok.size() != j && i < csoSzakaszok.size() * 2 && i >= csoSzakaszok.size()
						&& j < csoSzakaszok.size()) {
					data[i][j] = 0;
				}
				// második négyzet végén oszlop
				else if (i < csoSzakaszok.size() * 2 && i >= csoSzakaszok.size() && j == csoSzakaszok.size()) {
					int n = i - csoSzakaszok.size();
					double viznyom = nearestKey(nyomasEses, kumulaltVizfogyasztas.get(n));
					data[i][j] = Collections.max(nyomasEses.get(viznyom).values()) * csoSzakaszok.get(n) / 100;
				}
				// célfüggvény együtthatók
				else if (i == csoSzakaszok.size() * 2 && j < csoSzakaszok.size()) {
					data[i][j] = -1;
				} else if (i == csoSzakaszok.size() * 2 && j == csoSzakaszok.size()) {
					data[i][j] = 0;
				}
			}
		}
		
		Szimplex szimplex = new Szimplex(csoSzakaszok.size()*2, csoSzakaszok.size(), data);
		
		
		// javítani!
		return null;
	}

	private class Szimplex {
		private int sorokSzama;
		private int oszlopokSzama;
		private double[][] szimplexTabla;
		private int[] bazisValtozok;
		private int[] dontesiValtozok;
		private double[] megoldas;

		public Szimplex(int sorokSzama, int oszlopokSzama, double[][] szimplexTabla) {
			
			
			
			this.sorokSzama = sorokSzama + 1;
			this.oszlopokSzama = oszlopokSzama + 1;
			this.szimplexTabla = szimplexTabla;
			
			/*
			for (int i = 0; i <= sorokSzama; i++) {
				for (int j = 0; j <=  oszlopokSzama; j++) {
					System.out.printf("%7.2f ", this.szimplexTabla[i][j]);
				}
				System.out.println();
			}
			*/
			
			megoldas = new double[sorokSzama/2];
			
			bazisValtozok = new int[sorokSzama];
			for (int i = 0; i < sorokSzama; i++) {
				bazisValtozok[i] = i;
				//System.out.println("bazisv" + bazisValtozok[i]);
			}
			dontesiValtozok = new int[oszlopokSzama];
			for (int i = 0; i < oszlopokSzama ; i++) {
				dontesiValtozok[i] = sorokSzama + i;
				//System.out.println("dontesiv" + dontesiValtozok[i]);
			}
			szimplexAlgoritmus();
			/*
			for (int i = 0; i < (sorokSzama)/2; i++) {
				System.out.println(megoldas[i]);
			}
			*/
		}

		/*
		 * klasszikus szimplex algoritmussal, a célfüggvény együtthatók minimumát
		 * vesszük (a szimplexTabla[][] utolsó sora, kivéve az utolsó oszlop), azon
		 * együtthatók közül, amik felveszik ezt az értéket, a legkisebb indexû
		 */
		private int generaloElemOszlopa() {
			int hanyadik = -1;
			double min = 0;
			for (int i = 0; i < oszlopokSzama - 1; i++) {
				//System.out.println(szimplexTabla[sorokSzama-1][i]);
				if (szimplexTabla[sorokSzama - 1][i] < 0 && szimplexTabla[sorokSzama - 1][i] < min) {
					min = szimplexTabla[sorokSzama - 1][i];
					hanyadik = i;
				} 
			}
			//System.out.println(hanyadik);
			return hanyadik;
		}

		/*
		 * Klasszikus szimplex algoritmus sorválasztás
		 */
		private int generaloElemSora(int oszlop) {
			if(oszlop == -1 ) {
				
				return -2;
			}
			int hanyadik = -1;
			double min = Double.MAX_VALUE;
			for (int i = 0; i < sorokSzama - 1; i++) {
				if (szimplexTabla[i][oszlop] > 0
						&& szimplexTabla[i][oszlopokSzama - 1] / szimplexTabla[i][oszlop] < min) {
					min = szimplexTabla[i][oszlopokSzama - 1] / szimplexTabla[i][oszlop];
					hanyadik = i;
				}
			}
			return hanyadik;
		}

		private void bazisvaltoztatas(int sor, int oszlop) {
			// 1. generáló elem sorában és oszlopában a változók helyet cserélnek
			
			//System.out.println("ge sor " + sor);
			//System.out.println("ge oszlop " + oszlop);
			//for (int i = 0 ; i < sorokSzama-1; i++) 
				//System.out.println("bazis " + bazisValtozok[i]);
			bazisValtozok[sor] = dontesiValtozok[oszlop];
		
			

			// 2. generáló elem során és oszlopán kívül az összes elem
			for (int i = 0; i < sorokSzama; i++) {
				for (int j = 0; j < oszlopokSzama; j++) 
					if (i != sor && j != oszlop)
						szimplexTabla[i][j] -= szimplexTabla[sor][j] * szimplexTabla[i][oszlop]
								/ szimplexTabla[sor][oszlop];
			}

			// 3. generáló elem sorában az összes elemet (kiv. generáló elemet) elosztjuk a
			// generáló elemmel
			for (int i = 0; i < oszlopokSzama; i++)
				if (i != oszlop)
					szimplexTabla[sor][i] = szimplexTabla[sor][i] / szimplexTabla[sor][oszlop];

			// 4. generáló elem oszlopában az összes elemet (kiv. generáló elemet) elosztjuk
			// a generáló elem -1-szeresével
			for (int i = 0; i < sorokSzama; i++)
				if (i != sor)
					szimplexTabla[i][oszlop] = -1 * szimplexTabla[i][oszlop] / szimplexTabla[sor][oszlop];

			// 5. generáló elem helyére a reciproka kerül
			szimplexTabla[sor][oszlop] = 1 / szimplexTabla[sor][oszlop];
		}
		
		private void szimplexAlgoritmus() {
			while(true) {
				
				/*
				for (int i = 0; i < sorokSzama; i++) {
					for (int j = 0; j <  oszlopokSzama; j++) {
						System.out.printf("%7.2f ", szimplexTabla[i][j]);
					}
					System.out.println();
				}
				*/
				
				int oszlop = generaloElemOszlopa();
				int sor = generaloElemSora(oszlop);
				if (oszlop == -1) {
					System.out.println("optimális megoldás");
					break;
				}
				if (sor == -1) {
					System.out.println("nincs optimális megoldás"); //ez elõfordulhat egyáltalán?
					break;
				}
				bazisvaltoztatas(sor, oszlop);
			}
			for (int i = 0; i < sorokSzama-1; i++) {
				//System.out.println(bazisValtozok[i]);
				if (bazisValtozok[i] >= sorokSzama-2) {
					megoldas[bazisValtozok[i]-sorokSzama+1] = szimplexTabla[i][oszlopokSzama-1];
					
				}
			}
		}
	}
}
