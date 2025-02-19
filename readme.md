# Mars Weather Viewer

## Description

The Mars Weather Viewer is a Java Swing application that displays the latest weather data from the NASA InSight lander on Mars. It fetches data from the NASA InSight Weather API and presents it in a user-friendly graphical interface. Users can view various weather parameters such as atmospheric temperature, pressure, wind speed, and wind direction for different Martian solar days (Sols).

This application is designed to be simple to use, allowing anyone to quickly access and understand the Martian weather conditions reported by InSight. It utilizes a visually appealing dark theme and provides a wind rose diagram for easy interpretation of wind data.

## Features

- **Real-time Mars Weather Data:** Fetches up-to-date weather information directly from the NASA InSight API.
- **User-Friendly GUI:**  Presents weather data in a clear and intuitive graphical format using Java Swing.
- **Sol Selection:** Allows users to choose a specific Martian Sol (solar day) to view weather data for.
- **Key Weather Parameters:** Displays measurements for:
    - Atmospheric Temperature (AT)
    - Atmospheric Pressure (PRE)
    - Horizontal Wind Speed (HWS)
    - Wind Direction (WD) visualized as a wind rose diagram.
- **Time and Season Information:** Provides details about the Martian time of data collection and the current Martian season.
- **Demo Mode:** Offers a 'demo' mode for users to explore the application without needing a personal NASA API key (limited usage).
- **Dark Theme Interface:** Features a modern dark theme for comfortable viewing and a visually appealing experience.

## Technologies Used

- **Java:**  The primary programming language.
- **Swing:**  Java's GUI toolkit for creating the graphical user interface.
- **OkHttp:** An efficient HTTP client for making network requests to the NASA API.
- **Jackson Databind:** A Java library for parsing JSON responses from the API.
- **JSON-Java (org.json):**  For working with JSON data within the Swing application.
- **JetBrains Annotations:** For `@NotNull` annotation to enhance code clarity.

## Setup and Installation

### Getting Started

1. **Download the Project:**
    - You can download the project as a ZIP file or clone the repository if available.

2. **Compile the Code:**
    - Navigate to the project directory in your terminal or command prompt.
    - Compile the Java source files using `javac`:
      ```bash
      javac org/example/Main.java org/example/WeatherDisplay.java
      ```
      (Ensure your `CLASSPATH` is set up correctly if needed, or compile from the root directory if your IDE has structured the project this way).
    - Alternatively, if you are using an IDE like IntelliJ IDEA or Eclipse, you can open the project and build it directly within the IDE.

3. **Run the Application:**
    - After successful compilation, run the `Main` class using:
      ```bash
      java org.example.Main
      ```
    - Or run the `Main` class directly from your IDE.

### API Key

- **NASA API Key Required:** This application requires a NASA API key to access the InSight weather data. You can obtain a free API key from the [NASA API portal](https://api.nasa.gov/).
- **Demo Mode:** For quick testing or limited usage, you can type `demo` when prompted for the API key. This uses a predefined `DEMO_KEY`, which may have usage limitations set by NASA. **It is highly recommended to obtain your own API key for regular use.**

## Usage

1. **Run the application** as described in the "Setup and Installation" section.
2. **API Key Input:**
    - Upon starting the application, a dialog box will appear prompting you to "Enter API Key or type 'demo' (limited usage):".
    - Enter your NASA API key if you have one.
    - To use the demo mode, type `demo` and click "OK".
    - Click "Cancel" to exit the application.

3. **Sol Selection:**
    - After providing a valid API key, the application will fetch the latest weather data.
    - Another dialog box will appear listing the available Martian Sols (solar days) for which weather data is available.
    - Select a Sol from the dropdown list to view its weather information.
    - Click "OK" to proceed.

4. **Weather Display:**
    - A new window will open, displaying the weather data for the selected Sol.
    - The window is divided into sections showing:
        - **Time Information:** First and Last UTC timestamps of data collection.
        - **Season Information:** Martian season and Northern/Southern hemisphere seasons.
        - **Measurements:** Detailed measurements for Pressure (PRE), Temperature (AT), and Wind Speed (HWS), including Minimum, Average, Maximum, and Count values.
        - **Wind Rose:** A graphical representation of wind direction frequency.

5. **Explore Different Sols:** To view weather data for a different Sol, you will need to restart the application and go through the Sol selection process again.



## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.