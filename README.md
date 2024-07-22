# PDF Generator

## Overview

The **PDF Generator** project is a Java application designed to generate detailed PDF audit reports using data from [Codacy](https://www.codacy.com/). This tool leverages the Codacy API to retrieve repository analysis data, issues overview, and specific issues, which it then formats and outputs into a structured PDF report.

## Features

- **Codacy Integration**: Fetches repository data, issues overview, and individual issues from Codacy.
- **PDF Generation**: Creates a detailed PDF report that includes repository summary, issues overview, and a comprehensive list of detected issues.
- **Modular Design**: Easily extendable to add more data points or change the structure of the PDF report.

## Getting Started

### Prerequisites

- **Java 11** or higher
- **Maven** for dependency management

### Setup

1. **Clone the repository**

   ```sh
   git clone https://github.com/codacy-acme/pdf-generator.git
   cd pdf-generator

2. **Build the project**

    Run the following command to install the necessary dependencies and build the project:

    ```sh
    mvn clean install

3. **Run the application**

    To generate a PDF report, execute the following command:

    ```sh
    java -jar target/pdf-generator-1.0-SNAPSHOT-jar-with-dependencies.jar <provider> <organization> <repository> <api-token>
    ```
    `<provider>`: The Git provider (e.g., gh, gl, bb).  
    `<organization>`: The organization name on the provider.    
    `<repository>`: The repository name.    
    `<api-token>`: Your Codacy API token.   

### Example Usage
    
```sh
java -jar target/pdf-generator-1.0-SNAPSHOT.jar github my-organization my-repository my-codacy-token
```
This will create a PDF report named audit_report.pdf in the current directory.

### Dependencies
The project uses the following dependencies:

* Jackson Databind: For JSON processingJUnit: For testing.
* Apache HttpClient: For making HTTP requests.
* Apache PDFBox: For creating and manipulating PDFs.
* These dependencies are defined in the pom.xml file:

## Contribution
Contributions are welcome! If you want to contribute to this project, please follow these steps:

* Fork the repository
* Create a new branch (git checkout -b feature/my-feature)
* Commit your changes (git commit -am 'Add some feature')
* Push to the branch (git push origin feature/my-feature)
* Create a new Pull Request

