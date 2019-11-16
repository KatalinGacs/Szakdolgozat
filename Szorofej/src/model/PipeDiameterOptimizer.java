package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.bean.SprinklerShape;

public class PipeDiameterOptimizer {

	private static Simplex simplex;
	public static double remainingPressure;

	public static ArrayList<String> optimalPipes(double beginningPressure, ArrayList<Double> pipeLengths,
			ArrayList<SprinklerShape> sprinklers, double totalWaterFlow) {
		System.out.println("optimalPipes called");
		System.out.println("begin press: " + beginningPressure);
		System.out.println("pipelength: " + pipeLengths);
		System.out.println("Sprinklers: " + sprinklers);
		System.out.println("totalwf: " + totalWaterFlow);

		ArrayList<String> result = new ArrayList<String>();

		if (sprinklers.isEmpty()) {
			double wf = nearestKey(pressureLossTable, totalWaterFlow);
			double pressureLoss = Collections.max(pressureLossTable.get(wf).values()) * pipeLengths.get(0) / 100;
			result.add(getDiameter(wf, pressureLoss));
			return result;
		}

		double[][] data = new double[pipeLengths.size() * 2 + 1][pipeLengths.size() * 3 + 1];
		ArrayList<Double> grossWaterFlow = calculateGrossWaterFlow(sprinklers, totalWaterFlow, pipeLengths.size());
		ArrayList<Double> minPressureOfSprinklers = minPressure(sprinklers, pipeLengths.size());
		System.out.println("grossWaterFlow: " + grossWaterFlow);
		System.out.println("minpress" + minPressureOfSprinklers);

		for (int i = 0; i < pipeLengths.size() * 2 + 1; i++) {
			for (int j = 0; j < pipeLengths.size() * 3 + 1; j++) {
				if (i >= j && i < pipeLengths.size() && j < pipeLengths.size()) {
					data[i][j] = 1;
				} else if (i < j && i < pipeLengths.size() && j < pipeLengths.size()) {
					data[i][j] = 0;
				} else if (i == j - pipeLengths.size() && i < pipeLengths.size() && j < pipeLengths.size() * 2
						&& j >= pipeLengths.size()) {
					data[i][j] = 1;
				} else if (i != j - pipeLengths.size() && i < pipeLengths.size() && j < pipeLengths.size() * 2
						&& j >= pipeLengths.size()) {
					data[i][j] = 0;
				} else if (i < pipeLengths.size() && j < pipeLengths.size() * 3 && j >= pipeLengths.size() * 2) {
					data[i][j] = 0;
				} else if (i < pipeLengths.size() && j == pipeLengths.size() * 3) {
					data[i][j] = beginningPressure - minPressureOfSprinklers.get(i);
				} else if (i - pipeLengths.size() == j && i < pipeLengths.size() * 2 && i >= pipeLengths.size()
						&& j < pipeLengths.size()) {
					data[i][j] = 1;
				} else if (i - pipeLengths.size() != j && i < pipeLengths.size() * 2 && i >= pipeLengths.size()
						&& j < pipeLengths.size()) {
					data[i][j] = 0;
				} else if (i < pipeLengths.size() * 2 && i >= pipeLengths.size() && j < pipeLengths.size() * 2
						&& j >= pipeLengths.size()) {
					data[i][j] = 0;
				} else if (i - pipeLengths.size() == j - pipeLengths.size() * 2 && i < pipeLengths.size() * 2
						&& i >= pipeLengths.size() && j < pipeLengths.size() * 3 && j >= pipeLengths.size() * 2) {
					data[i][j] = 1;
				} else if (i - pipeLengths.size() != j - pipeLengths.size() * 2 && i < pipeLengths.size() * 2
						&& i >= pipeLengths.size() && j < pipeLengths.size() * 3 && j >= pipeLengths.size() * 2) {
					data[i][j] = 0;
				} else if (i < pipeLengths.size() * 2 && i >= pipeLengths.size() && j == pipeLengths.size() * 3) {
					int n = i - pipeLengths.size();
					double wf = nearestKey(pressureLossTable, grossWaterFlow.get(n));
					double ertek = Collections.max(pressureLossTable.get(wf).values()) * pipeLengths.get(n) / 100;
					data[i][j] = ertek;
				} else if (i - pipeLengths.size() != j - pipeLengths.size() && i < pipeLengths.size() * 2
						&& i >= pipeLengths.size() && j < pipeLengths.size() * 3 && j >= pipeLengths.size() * 2) {
					data[i][j] = 0;
				} else if (i == pipeLengths.size() * 2 && j < pipeLengths.size()) {
					data[i][j] = -1;
				}
			}
		}
		simplex = new Simplex(data, pipeLengths.size() * 2, pipeLengths.size(), false);

		ArrayList<Double> solution = new ArrayList<Double>(); // simplex.resultList();

		for (int i = 0; i < simplex.resultList().size(); i++) {
			solution.add(simplex.resultList().get(i) / (pipeLengths.get(i)) * 100);
		}

		remainingPressure = beginningPressure;
		for (double d : solution) {
			remainingPressure -= d;
			result.add(getDiameter(grossWaterFlow.get(solution.indexOf(d)), d));
		}
		return result;

	}

	private static ArrayList<Double> calculateGrossWaterFlow(ArrayList<SprinklerShape> sprinklers,
			double totalWaterFlow, int arraySize) {
		ArrayList<Double> result = new ArrayList<>();
		double sum = totalWaterFlow;

		for (SprinklerShape s : sprinklers) {
			result.add(sum);
			sum -= s.getFlowRate();
		}
		if (sprinklers.size() < arraySize) {
			result.add(sum);
		}
		return result;
	}

	private static ArrayList<Double> minPressure(ArrayList<SprinklerShape> sprinklers, int arraySize) {
		ArrayList<Double> result = new ArrayList<>();
		for (SprinklerShape s : sprinklers) {
			result.add(s.getSprinkler().getMinPressure());
		}
		if (sprinklers.size() < arraySize) {
			result.add(0.0);
		}
		return result;
	}

	private static double nearestKey(Map<Double, Map<String, Double>> map, double target) {
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

	private static String getDiameter(double waterFlow, double pressureLoss) {
		double wf = nearestKey(pressureLossTable, waterFlow);

		double closestPressureLossInTable = 0;
		double diff = Double.MAX_VALUE;
		for (double d : pressureLossTable.get(wf).values()) {
			if (Math.abs(d - pressureLoss) < diff) {
				diff = Math.abs(d - pressureLoss);
				closestPressureLossInTable = d;
			}
		}

		int minDiameter = Integer.MAX_VALUE;
		String res = null;
		for (String s : pressureLossTable.get(wf).keySet()) {
			if (pressureLossTable.get(wf).get(s) == closestPressureLossInTable)
				if (Integer.parseInt(s) < minDiameter) {
					minDiameter = Integer.parseInt(s);
					res = s;
				}
		}

		return res;
	}

	// Nyomásesés 100 méteren
	// Az első kulcs a vízfogyasztás értéke, az ehhez tartozó mapban tárolódik, hogy
	// mekkora nyomásesénél milyen cső használatos
	private static final Map<Double, Map<String, Double>> pressureLossTable = new HashMap<Double, Map<String, Double>>();
	static {
		pressureLossTable.put(3.3, new HashMap<String, Double>());
		pressureLossTable.get(3.3).put("20", 0.06);
		pressureLossTable.get(3.3).put("25", 0.03);
		pressureLossTable.get(3.3).put("32", 0.01);
		pressureLossTable.get(3.3).put("40", 0.0);

		pressureLossTable.put(6.7, new HashMap<String, Double>());
		pressureLossTable.get(6.7).put("20", 0.3);
		pressureLossTable.get(6.7).put("25", 0.09);
		pressureLossTable.get(6.7).put("32", 0.03);
		pressureLossTable.get(6.7).put("40", 0.01);
		pressureLossTable.get(6.7).put("50", 0.0);

		pressureLossTable.put(10.0, new HashMap<String, Double>());
		pressureLossTable.get(10.0).put("20", 0.63);
		pressureLossTable.get(10.0).put("25", 0.19);
		pressureLossTable.get(10.0).put("32", 0.06);
		pressureLossTable.get(10.0).put("40", 0.02);
		pressureLossTable.get(10.0).put("50", 0.01);
		pressureLossTable.get(10.0).put("63", 0.0);

		pressureLossTable.put(13.3, new HashMap<String, Double>());
		pressureLossTable.get(13.3).put("20", 1.07);
		pressureLossTable.get(13.3).put("25", 0.33);
		pressureLossTable.get(13.3).put("32", 0.1);
		pressureLossTable.get(13.3).put("40", 0.03);
		pressureLossTable.get(13.3).put("50", 0.01);
		pressureLossTable.get(13.3).put("63", 0.0);

		pressureLossTable.put(16.7, new HashMap<String, Double>());
		pressureLossTable.get(16.7).put("25", 0.49);
		pressureLossTable.get(16.7).put("32", 0.15);
		pressureLossTable.get(16.7).put("40", 0.05);
		pressureLossTable.get(16.7).put("50", 0.02);
		pressureLossTable.get(16.7).put("63", 0.01);
		pressureLossTable.get(16.7).put("75", 0.00);

		pressureLossTable.put(20.0, new HashMap<String, Double>());
		pressureLossTable.get(20.0).put("25", 0.69);
		pressureLossTable.get(20.0).put("32", 0.21);
		pressureLossTable.get(20.0).put("40", 0.07);
		pressureLossTable.get(20.0).put("50", 0.02);
		pressureLossTable.get(20.0).put("63", 0.01);
		pressureLossTable.get(20.0).put("75", 0.00);

		pressureLossTable.put(23.3, new HashMap<String, Double>());
		pressureLossTable.get(23.3).put("25", 0.92);
		pressureLossTable.get(23.3).put("32", 0.27);
		pressureLossTable.get(23.3).put("40", 0.09);
		pressureLossTable.get(23.3).put("50", 0.03);
		pressureLossTable.get(23.3).put("63", 0.01);
		pressureLossTable.get(23.3).put("75", 0.00);

		pressureLossTable.put(26.7, new HashMap<String, Double>());
		pressureLossTable.get(26.7).put("32", 0.35);
		pressureLossTable.get(26.7).put("40", 0.12);
		pressureLossTable.get(26.7).put("50", 0.04);
		pressureLossTable.get(26.7).put("63", 0.01);
		pressureLossTable.get(26.7).put("75", 0.01);
		pressureLossTable.get(26.7).put("90", 0.00);

		pressureLossTable.put(30.0, new HashMap<String, Double>());
		pressureLossTable.get(30.0).put("32", 0.43);
		pressureLossTable.get(30.0).put("40", 0.15);
		pressureLossTable.get(30.0).put("50", 0.05);
		pressureLossTable.get(30.0).put("63", 0.02);
		pressureLossTable.get(30.0).put("75", 0.01);
		pressureLossTable.get(30.0).put("90", 0.00);

		pressureLossTable.put(33.3, new HashMap<String, Double>());
		pressureLossTable.get(33.3).put("32", 0.53);
		pressureLossTable.get(33.3).put("40", 0.18);
		pressureLossTable.get(33.3).put("50", 0.06);
		pressureLossTable.get(33.3).put("63", 0.02);
		pressureLossTable.get(33.3).put("75", 0.01);
		pressureLossTable.get(33.3).put("90", 0.00);

		pressureLossTable.put(36.7, new HashMap<String, Double>());
		pressureLossTable.get(36.7).put("32", 0.63);
		pressureLossTable.get(36.7).put("40", 0.21);
		pressureLossTable.get(36.7).put("50", 0.07);
		pressureLossTable.get(36.7).put("63", 0.02);
		pressureLossTable.get(36.7).put("75", 0.01);
		pressureLossTable.get(36.7).put("90", 0.00);

		pressureLossTable.put(40.0, new HashMap<String, Double>());
		pressureLossTable.get(40.0).put("32", 0.74);
		pressureLossTable.get(40.0).put("40", 0.25);
		pressureLossTable.get(40.0).put("50", 0.08);
		pressureLossTable.get(40.0).put("63", 0.03);
		pressureLossTable.get(40.0).put("75", 0.01);
		pressureLossTable.get(40.0).put("90", 0.00);

		pressureLossTable.put(43.3, new HashMap<String, Double>());
		pressureLossTable.get(43.3).put("32", 0.86);
		pressureLossTable.get(43.3).put("40", 0.29);
		pressureLossTable.get(43.3).put("50", 0.10);
		pressureLossTable.get(43.3).put("63", 0.03);
		pressureLossTable.get(43.3).put("75", 0.01);
		pressureLossTable.get(43.3).put("90", 0.01);
		pressureLossTable.get(43.3).put("110", 0.00);

		pressureLossTable.put(46.7, new HashMap<String, Double>());
		pressureLossTable.get(46.7).put("32", 0.99);
		pressureLossTable.get(46.7).put("40", 0.33);
		pressureLossTable.get(46.7).put("50", 0.11);
		pressureLossTable.get(46.7).put("63", 0.04);
		pressureLossTable.get(46.7).put("75", 0.02);
		pressureLossTable.get(46.7).put("90", 0.01);
		pressureLossTable.get(46.7).put("110", 0.00);

		pressureLossTable.put(50.0, new HashMap<String, Double>());
		pressureLossTable.get(50.0).put("40", 0.38);
		pressureLossTable.get(50.0).put("50", 0.13);
		pressureLossTable.get(50.0).put("63", 0.04);
		pressureLossTable.get(50.0).put("75", 0.02);
		pressureLossTable.get(50.0).put("90", 0.01);
		pressureLossTable.get(50.0).put("110", 0.00);

		pressureLossTable.put(53.3, new HashMap<String, Double>());
		pressureLossTable.get(53.3).put("40", 0.42);
		pressureLossTable.get(53.3).put("50", 0.14);
		pressureLossTable.get(53.3).put("63", 0.05);
		pressureLossTable.get(53.3).put("75", 0.02);
		pressureLossTable.get(53.3).put("90", 0.01);
		pressureLossTable.get(53.3).put("110", 0.00);

		pressureLossTable.put(56.7, new HashMap<String, Double>());
		pressureLossTable.get(56.7).put("40", 0.47);
		pressureLossTable.get(56.7).put("50", 0.16);
		pressureLossTable.get(56.7).put("63", 0.05);
		pressureLossTable.get(56.7).put("75", 0.02);
		pressureLossTable.get(56.7).put("90", 0.01);
		pressureLossTable.get(56.7).put("110", 0.01);

		pressureLossTable.put(60.0, new HashMap<String, Double>());
		pressureLossTable.get(60.0).put("40", 0.53);
		pressureLossTable.get(60.0).put("50", 0.18);
		pressureLossTable.get(60.0).put("63", 0.06);
		pressureLossTable.get(60.0).put("75", 0.02);
		pressureLossTable.get(60.0).put("90", 0.01);
		pressureLossTable.get(60.0).put("110", 0.00);

		pressureLossTable.put(63.3, new HashMap<String, Double>());
		pressureLossTable.get(63.3).put("40", 0.58);
		pressureLossTable.get(63.3).put("50", 0.20);
		pressureLossTable.get(63.3).put("63", 0.06);
		pressureLossTable.get(63.3).put("75", 0.03);
		pressureLossTable.get(63.3).put("90", 0.01);
		pressureLossTable.get(63.3).put("110", 0.00);

		pressureLossTable.put(66.7, new HashMap<String, Double>());
		pressureLossTable.get(66.7).put("40", 0.64);
		pressureLossTable.get(66.7).put("50", 0.22);
		pressureLossTable.get(66.7).put("63", 0.07);
		pressureLossTable.get(66.7).put("75", 0.03);
		pressureLossTable.get(66.7).put("90", 0.01);
		pressureLossTable.get(66.7).put("110", 0.00);

		pressureLossTable.put(75.0, new HashMap<String, Double>());
		pressureLossTable.get(75.0).put("40", 0.80);
		pressureLossTable.get(75.0).put("50", 0.27);
		pressureLossTable.get(75.0).put("63", 0.09);
		pressureLossTable.get(75.0).put("75", 0.04);
		pressureLossTable.get(75.0).put("90", 0.02);
		pressureLossTable.get(75.0).put("110", 0.01);

		pressureLossTable.put(83.3, new HashMap<String, Double>());
		pressureLossTable.get(83.3).put("40", 0.97);
		pressureLossTable.get(83.3).put("50", 0.33);
		pressureLossTable.get(83.3).put("63", 0.11);
		pressureLossTable.get(83.3).put("75", 0.05);
		pressureLossTable.get(83.3).put("90", 0.02);
		pressureLossTable.get(83.3).put("110", 0.01);

		pressureLossTable.put(91.7, new HashMap<String, Double>());
		pressureLossTable.get(91.7).put("50", 0.39);
		pressureLossTable.get(91.7).put("63", 0.13);
		pressureLossTable.get(91.7).put("75", 0.05);
		pressureLossTable.get(91.7).put("90", 0.02);
		pressureLossTable.get(91.7).put("110", 0.01);

		pressureLossTable.put(100.0, new HashMap<String, Double>());
		pressureLossTable.get(100.0).put("50", 0.46);
		pressureLossTable.get(100.0).put("63", 0.15);
		pressureLossTable.get(100.0).put("75", 0.06);
		pressureLossTable.get(100.0).put("90", 0.03);
		pressureLossTable.get(100.0).put("110", 0.01);

		pressureLossTable.put(108.3, new HashMap<String, Double>());
		pressureLossTable.get(108.3).put("50", 0.53);
		pressureLossTable.get(108.3).put("63", 0.17);
		pressureLossTable.get(108.3).put("75", 0.07);
		pressureLossTable.get(108.3).put("90", 0.03);
		pressureLossTable.get(108.3).put("110", 0.01);

		pressureLossTable.put(116.7, new HashMap<String, Double>());
		pressureLossTable.get(116.7).put("50", 0.61);
		pressureLossTable.get(116.7).put("63", 0.20);
		pressureLossTable.get(116.7).put("75", 0.09);
		pressureLossTable.get(116.7).put("90", 0.04);
		pressureLossTable.get(116.7).put("110", 0.01);

		pressureLossTable.put(125.0, new HashMap<String, Double>());
		pressureLossTable.get(125.0).put("50", 0.69);
		pressureLossTable.get(125.0).put("63", 0.23);
		pressureLossTable.get(125.0).put("75", 0.10);
		pressureLossTable.get(125.0).put("90", 0.04);
		pressureLossTable.get(125.0).put("110", 0.02);

		pressureLossTable.put(133.3, new HashMap<String, Double>());
		pressureLossTable.get(133.3).put("50", 0.78);
		pressureLossTable.get(133.3).put("63", 0.25);
		pressureLossTable.get(133.3).put("75", 0.11);
		pressureLossTable.get(133.3).put("90", 0.05);
		pressureLossTable.get(133.3).put("110", 0.02);

		pressureLossTable.put(141.7, new HashMap<String, Double>());
		pressureLossTable.get(141.7).put("50", 0.87);
		pressureLossTable.get(141.7).put("63", 0.28);
		pressureLossTable.get(141.7).put("75", 0.12);
		pressureLossTable.get(141.7).put("90", 0.05);
		pressureLossTable.get(141.7).put("110", 0.02);

		pressureLossTable.put(150.0, new HashMap<String, Double>());
		pressureLossTable.get(150.0).put("50", 0.97);
		pressureLossTable.get(150.0).put("63", 0.32);
		pressureLossTable.get(150.0).put("75", 0.14);
		pressureLossTable.get(150.0).put("90", 0.06);
		pressureLossTable.get(150.0).put("110", 0.02);

		pressureLossTable.put(158.3, new HashMap<String, Double>());
		pressureLossTable.get(158.3).put("63", 0.35);
		pressureLossTable.get(158.3).put("75", 0.14);
		pressureLossTable.get(158.3).put("90", 0.06);
		pressureLossTable.get(158.3).put("110", 0.02);

		pressureLossTable.put(166.7, new HashMap<String, Double>());
		pressureLossTable.get(166.7).put("63", 0.38);
		pressureLossTable.get(166.7).put("75", 0.17);
		pressureLossTable.get(166.7).put("90", 0.07);
		pressureLossTable.get(166.7).put("110", 0.03);

		pressureLossTable.put(183.3, new HashMap<String, Double>());
		pressureLossTable.get(183.3).put("63", 0.46);
		pressureLossTable.get(183.3).put("75", 0.20);
		pressureLossTable.get(183.3).put("90", 0.08);
		pressureLossTable.get(183.3).put("110", 0.03);

		pressureLossTable.put(200.0, new HashMap<String, Double>());
		pressureLossTable.get(200.0).put("63", 0.54);
		pressureLossTable.get(200.0).put("75", 0.23);
		pressureLossTable.get(200.0).put("90", 0.10);
		pressureLossTable.get(200.0).put("110", 0.04);

		pressureLossTable.put(216.7, new HashMap<String, Double>());
		pressureLossTable.get(216.7).put("63", 0.63);
		pressureLossTable.get(216.7).put("75", 0.27);
		pressureLossTable.get(216.7).put("90", 0.11);
		pressureLossTable.get(216.7).put("110", 0.04);

		pressureLossTable.put(233.3, new HashMap<String, Double>());
		pressureLossTable.get(233.3).put("63", 0.72);
		pressureLossTable.get(233.3).put("75", 0.31);
		pressureLossTable.get(233.3).put("90", 0.13);
		pressureLossTable.get(233.3).put("110", 0.05);

		pressureLossTable.put(250.0, new HashMap<String, Double>());
		pressureLossTable.get(250.0).put("63", 0.82);
		pressureLossTable.get(250.0).put("75", 0.35);
		pressureLossTable.get(250.0).put("90", 0.14);
		pressureLossTable.get(250.0).put("110", 0.05);

		pressureLossTable.put(266.7, new HashMap<String, Double>());
		pressureLossTable.get(266.7).put("75", 0.39);
		pressureLossTable.get(266.7).put("90", 0.16);
		pressureLossTable.get(266.7).put("110", 0.06);

		pressureLossTable.put(283.3, new HashMap<String, Double>());
		pressureLossTable.get(283.3).put("75", 0.44);
		pressureLossTable.get(283.3).put("90", 0.18);
		pressureLossTable.get(283.3).put("110", 0.07);

		pressureLossTable.put(300.0, new HashMap<String, Double>());
		pressureLossTable.get(300.0).put("75", 0.49);
		pressureLossTable.get(300.0).put("90", 0.20);
		pressureLossTable.get(300.0).put("110", 0.08);

		pressureLossTable.put(316.7, new HashMap<String, Double>());
		pressureLossTable.get(316.7).put("75", 0.54);
		pressureLossTable.get(316.7).put("90", 0.22);
		pressureLossTable.get(316.7).put("110", 0.08);

		pressureLossTable.put(333.3, new HashMap<String, Double>());
		pressureLossTable.get(333.3).put("75", 0.60);
		pressureLossTable.get(333.3).put("90", 0.25);
		pressureLossTable.get(333.3).put("110", 0.09);

		pressureLossTable.put(366.7, new HashMap<String, Double>());
		pressureLossTable.get(366.7).put("90", 0.29);
		pressureLossTable.get(366.7).put("110", 0.11);

		pressureLossTable.put(400.0, new HashMap<String, Double>());
		pressureLossTable.get(400.0).put("90", 0.35);
		pressureLossTable.get(400.0).put("110", 0.13);

		pressureLossTable.put(433.3, new HashMap<String, Double>());
		pressureLossTable.get(433.3).put("90", 0.40);
		pressureLossTable.get(433.3).put("110", 0.15);

		pressureLossTable.put(466.7, new HashMap<String, Double>());
		pressureLossTable.get(466.7).put("90", 0.46);
		pressureLossTable.get(466.7).put("110", 0.17);

		pressureLossTable.put(500.0, new HashMap<String, Double>());
		pressureLossTable.get(500.0).put("90", 0.52);
		pressureLossTable.get(500.0).put("110", 0.20);

		pressureLossTable.put(533.3, new HashMap<String, Double>());
		pressureLossTable.get(533.3).put("110", 0.22);

		pressureLossTable.put(566.7, new HashMap<String, Double>());
		pressureLossTable.get(566.7).put("110", 0.25);

		pressureLossTable.put(600.0, new HashMap<String, Double>());
		pressureLossTable.get(600.0).put("110", 0.28);

		pressureLossTable.put(633.3, new HashMap<String, Double>());
		pressureLossTable.get(633.3).put("110", 0.30);

		pressureLossTable.put(666.7, new HashMap<String, Double>());
		pressureLossTable.get(666.7).put("110", 0.33);

		pressureLossTable.put(750.0, new HashMap<String, Double>());
		pressureLossTable.get(750.0).put("110", 0.42);
	}
}
