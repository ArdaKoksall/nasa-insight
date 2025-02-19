package org.example;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDisplay extends JFrame {
    private static final String DATE_FORMAT_INPUT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DATE_FORMAT_OUTPUT = "MMM dd, yyyy HH:mm:ss";

    private static final Color PRIMARY_COLOR = new Color(60, 60, 60);
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0);
    private static final Color PANEL_BACKGROUND = new Color(0, 0, 0);
    private static final Color TEXT_COLOR = new Color(150, 150, 150);

    public WeatherDisplay(String jsonData) {
        initializeFrame();
        try {
            JSONObject weatherData = new JSONObject(jsonData);
            createAndShowGUI(weatherData);
        } catch (JSONException e) {
            handleError("Invalid JSON data format", e);
        }
    }

    private void initializeFrame() {
        setTitle("Mars Weather");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));
        setResizable(true);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Add modern look and feel with dark theme
        try {
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("OptionPane.background", BACKGROUND_COLOR);
            UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
            UIManager.put("Button.background", PANEL_BACKGROUND);
            UIManager.put("Button.foreground", TEXT_COLOR);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    private void createAndShowGUI(JSONObject weatherData) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(BACKGROUND_COLOR);
        setResizable(false);

        addComponentsToMainPanel(mainPanel, weatherData);

        WindRosePanel windRosePanel = new WindRosePanel(weatherData);
        JPanel roseContainer = createStyledPanel();
        roseContainer.setLayout(new BorderLayout());
        roseContainer.setPreferredSize(new Dimension(500, 500));  // Increased size
        roseContainer.add(windRosePanel, BorderLayout.CENTER);
        roseContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add gradient effect to the main container
        JPanel gradientPanel = getJPanel();
        gradientPanel.add(roseContainer, BorderLayout.EAST);
        gradientPanel.add(mainPanel, BorderLayout.CENTER);

        add(gradientPanel);
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 700));  // Increased minimum size
    }

    @NotNull
    private static JPanel getJPanel() {
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, BACKGROUND_COLOR,
                        0, getHeight(), BACKGROUND_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new BorderLayout());
        return gradientPanel;
    }

    private JPanel createStyledPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, PANEL_BACKGROUND,
                        0, getHeight(), BACKGROUND_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

                // Add subtle border glow effect
                g2d.setColor(new Color(PRIMARY_COLOR.getRed(),
                        PRIMARY_COLOR.getGreen(),
                        PRIMARY_COLOR.getBlue(),
                        40));
                for (int i = 0; i < 5; i++) {
                    g2d.setStroke(new BasicStroke(i + 1));
                    g2d.draw(new RoundRectangle2D.Double(i, i,
                            getWidth() - (2 * i), getHeight() - (2 * i), 20, 20));
                }
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private void addComponentsToMainPanel(JPanel mainPanel, JSONObject weatherData) {
        // Time Panel
        Optional<JPanel> timePanel = createTimePanel(weatherData);
        timePanel.ifPresent(panel -> {
            mainPanel.add(panel);
            mainPanel.add(Box.createVerticalStrut(20));
        });

        // Season Panel
        Optional<JPanel> seasonPanel = createSeasonPanel(weatherData);
        seasonPanel.ifPresent(panel -> {
            mainPanel.add(panel);
            mainPanel.add(Box.createVerticalStrut(20));
        });

        // Measurements Panel
        Optional<JPanel> measurementsPanel = createMeasurementsPanel(weatherData);
        measurementsPanel.ifPresent(mainPanel::add);
    }

    private Optional<JPanel> createTimePanel(JSONObject jsonData) {
        try {
            JPanel panel = createStyledPanel();
            panel.setLayout(new GridLayout(2, 2, 15, 10));
            panel.setBorder(createStyledTitledBorder("Time Information"));

            SimpleDateFormat inputFormat = new SimpleDateFormat(DATE_FORMAT_INPUT);
            SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_FORMAT_OUTPUT);

            Map<String, String> timeFields = new HashMap<>();
            timeFields.put("First UTC", "First_UTC");
            timeFields.put("Last UTC", "Last_UTC");

            timeFields.forEach((label, key) -> {
                try {
                    if (jsonData.has(key)) {
                        Date date = inputFormat.parse(jsonData.getString(key));
                        panel.add(createStyledLabel(label + ":", true));
                        panel.add(createStyledLabel(outputFormat.format(date), false));
                    }
                } catch (ParseException | JSONException e) {
                    panel.add(createStyledLabel(label + ":", true));
                    panel.add(createStyledLabel("N/A", false));
                }
            });

            return Optional.of(panel);
        } catch (Exception e) {
            handleError("Error creating time panel", e);
            return Optional.empty();
        }
    }

    private Optional<JPanel> createSeasonPanel(JSONObject jsonData) {
        try {
            JPanel panel = createStyledPanel();
            panel.setLayout(new GridLayout(0, 2, 15, 10));
            panel.setBorder(createStyledTitledBorder("Season Information"));

            Map<String, String> seasonFields = new HashMap<>();
            seasonFields.put("Season", "Season");
            seasonFields.put("Northern Season", "Northern_season");
            seasonFields.put("Southern Season", "Southern_season");

            seasonFields.forEach((label, key) -> {
                panel.add(createStyledLabel(label + ":", true));
                panel.add(createStyledLabel(jsonData.optString(key, "N/A"), false));
            });

            return Optional.of(panel);
        } catch (Exception e) {
            handleError("Error creating season panel", e);
            return Optional.empty();
        }
    }

    private Optional<JPanel> createMeasurementsPanel(JSONObject jsonData) {
        try {
            JPanel panel = createStyledPanel();
            panel.setLayout(new GridLayout(0, 1, 15, 15));
            panel.setBorder(createStyledTitledBorder("Measurements"));

            Map<String, String> measurements = new HashMap<>();
            measurements.put("Pressure (PRE)", "PRE");
            measurements.put("Temperature (AT)", "AT");
            measurements.put("Wind Speed (HWS)", "HWS");

            measurements.forEach((label, key) -> {
                if (jsonData.has(key)) {
                    try {
                        JSONObject measureData = jsonData.getJSONObject(key);
                        String unit = getMeasurementUnit(key);
                        panel.add(createMeasurementSubPanel(label, measureData, unit));
                    } catch (JSONException e) {
                        panel.add(createErrorPanel(label));
                    }
                }
            });

            return Optional.of(panel);
        } catch (Exception e) {
            handleError("Error creating measurements panel", e);
            return Optional.empty();
        }
    }

    private JPanel createMeasurementSubPanel(String title, JSONObject data, String unit) {
        JPanel panel = createStyledPanel();
        panel.setLayout(new GridLayout(1, 4, 10, 5));
        panel.setBorder(createStyledTitledBorder(title));

        String[] metrics = {"mn", "av", "mx", "ct"};
        String[] labels = {"Min", "Avg", "Max", "Count"};
        Color[] colors = {new Color(0,100,0), new Color(0,0, 140), new Color (140,0,0), TEXT_COLOR};

        for (int i = 0; i < metrics.length; i++) {
            if (data.has(metrics[i])) {
                if (metrics[i].equals("ct")) {
                    panel.add(createValuePanel(labels[i], data.optInt(metrics[i]), colors[i]));
                } else {
                    panel.add(createValuePanel(labels[i], data.optDouble(metrics[i]), unit, colors[i]));
                }
            } else {
                panel.add(createValuePanel(labels[i], 0.0, "N/A", colors[i]));
            }
        }

        return panel;
    }

    private JPanel createValuePanel(String label, double value, String unit, Color color) {
        JPanel panel = createStyledPanel();
        panel.setLayout(new BorderLayout(5, 5));

        JLabel labelComponent = createStyledLabel(label, true);
        labelComponent.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel valueComponent = new JLabel(String.format("%.2f%s", value, unit));
        return getjPanel(color, panel, labelComponent, valueComponent);
    }

    private JPanel createValuePanel(String label, int value, Color color) {
        JPanel panel = createStyledPanel();
        panel.setLayout(new BorderLayout(5, 5));

        JLabel labelComponent = createStyledLabel(label, true);
        labelComponent.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel valueComponent = new JLabel(String.format("%d%s", value, ""));
        return getjPanel(color, panel, labelComponent, valueComponent);
    }

    @NotNull
    private JPanel getjPanel(Color color, JPanel panel, JLabel labelComponent, JLabel valueComponent) {
        valueComponent.setHorizontalAlignment(SwingConstants.CENTER);
        valueComponent.setFont(new Font("Arial", Font.BOLD, 16));
        valueComponent.setForeground(color);

        panel.add(labelComponent, BorderLayout.NORTH);
        panel.add(valueComponent, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createStyledLabel(String text, boolean isHeader) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Arial", isHeader ? Font.BOLD : Font.PLAIN, isHeader ? 14 : 13));
        return label;
    }

    private Border createStyledTitledBorder(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                        title,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14),
                        TEXT_COLOR
                )
        );
    }

    private static class WindRosePanel extends JPanel {
        private final Map<String, Double> windDirections;
        private final double maxCount;
        private static final Color[] WIND_COLORS = {
                new Color(255, 140, 0, 200),
                new Color(255, 0, 0, 200),    // Bright Red
                new Color(255, 85, 0, 200),   // Reddish Orange// Deep Orange
                new Color(255, 200, 0, 200)   // Bright Yellow
        };

        public WindRosePanel(JSONObject weatherData) {
            this.windDirections = parseWindDirections(weatherData);
            this.maxCount = windDirections.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .max()
                    .orElse(1.0);

            setBackground(PANEL_BACKGROUND);
            setOpaque(false);
        }

        private Map<String, Double> parseWindDirections(JSONObject data) {
            Map<String, Double> directions = new HashMap<>();
            try {
                if (!data.has("WD")) return directions;

                JSONObject windData = data.getJSONObject("WD");
                for (String key : windData.keySet()) {
                    if (key.equals("most_common")) continue;
                    JSONObject windInfo = windData.getJSONObject(key);
                    String direction = windInfo.getString("compass_point");
                    double value = windInfo.optDouble("ct", 0.0);
                    directions.put(direction, value);
                }
            } catch (JSONException ignored) {}
            return directions;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double centerX = getWidth() / 2.0;
            double centerY = getHeight() / 2.0;
            double maxRadius = Math.min(centerX, centerY) - 50.0;

            // Draw background circles with glowing effect
            g2d.setColor(new Color(60, 60, 60, 30));
            for (double i = 1; i <= 4; i++) {
                double radius = maxRadius * i / 4.0;
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawOval((int) (centerX - radius), (int) (centerY - radius),
                        (int) (radius * 2), (int) (radius * 2));
            }

            String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                    "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
            double[] compassDegrees = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5,
                    180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};

            // Draw direction petals
            for (int i = 0; i < directions.length; i++) {
                String directionName = directions[i];
                double directionDegrees = compassDegrees[i];
                Double count = windDirections.get(directionName);
                double percentage = (count != null ? count : 0.0) / maxCount;
                double radius = maxRadius * percentage;
                double startAngle = directionDegrees - (22.5 / 2.0) - 90;

                if (radius > 0) {
                    Color petalColor = WIND_COLORS[i % WIND_COLORS.length];
                    g2d.setColor(petalColor);

                    Arc2D.Double arc = new Arc2D.Double(
                            centerX - radius,
                            centerY - radius,
                            radius * 2,
                            radius * 2,
                            startAngle,
                            22.5,
                            Arc2D.PIE
                    );
                    g2d.fill(arc);
                }
            }

            // Draw center point
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            for (int j = 0; j < directions.length; j++) {
                double angle = Math.toRadians(compassDegrees[j] - 90.0);
                double x = centerX + (maxRadius + 25.0) * Math.cos(angle);
                double y = centerY + (maxRadius + 25.0) * Math.sin(angle);

                String label = directions[j];
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(label);
                int labelHeight = fm.getHeight();

                // Create glowing effect for labels
                g2d.setColor(new Color(PRIMARY_COLOR.getRed(),
                        PRIMARY_COLOR.getGreen(),
                        PRIMARY_COLOR.getBlue(),
                        40));
                g2d.fillRoundRect(
                        (int) (x - (double) labelWidth /2 - 5),
                        (int) (y - (double) labelHeight /2),
                        labelWidth + 10,
                        labelHeight,
                        10,
                        10
                );

                g2d.setColor(TEXT_COLOR);
                g2d.drawString(
                        label,
                        (int) (x - (double) labelWidth /2),
                        (int) (y + (double) labelHeight /3)
                );
            }
        }
    }

    private String getMeasurementUnit(String measurementKey) {
        Map<String, String> units = new HashMap<>();
        units.put("PRE", "Pa");
        units.put("AT", "°C");
        units.put("HWS", "m/s");
        return units.getOrDefault(measurementKey, "");
    }

    private JPanel createErrorPanel(String title) {
        JPanel panel = createStyledPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(createStyledTitledBorder(title));

        JLabel errorLabel = createStyledLabel("Data unavailable", false);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setForeground(new Color(231, 76, 60)); // Error red color
        panel.add(errorLabel, BorderLayout.CENTER);

        return panel;
    }

    private void handleError(String message, Exception e) {
        JOptionPane.showMessageDialog(
                this,
                message + ": " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}