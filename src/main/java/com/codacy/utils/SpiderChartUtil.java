package com.codacy.utils;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;


public class SpiderChartUtil {

    public static OutputStream createSpiderChart(String title, double[][] data, String[] categories, String[] seriesNames) throws IOException {

        Font labelFont = new Font("Helvetica", Font.PLAIN, 4);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < seriesNames.length; i++) {
            for (int j = 0; j < categories.length; j++) {
                dataset.addValue(data[i][j], seriesNames[i], categories[j]);
            }
        }

        SpiderWebPlot plot = new SpiderWebPlot(dataset);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        chart.setBackgroundPaint(Color.white);
        chart.setBorderVisible(false);
        

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        ChartUtils.writeScaledChartAsPNG(out, chart, 600, 600,1,1);

        return out;

    }
}
