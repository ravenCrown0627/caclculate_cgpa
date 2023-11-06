# calculate_cgpa
This is my advance programming assignment. Basically, the porgam can handle mutiple CSV files, parse and summarize the result of student in semester detail and overall result in term of CGPA.
The flowchart for the main() is shown in the figure below.

### Flowchart for main()
![Flowchart](flowchart/CalculateCGPA-main().png)

## Environment
- **IDE**: IntelliJ IDEA 2023.2.3 Ultimate Edition
- **JDK (Java Development Kit)**: Version 20.0.2
- **Build Tool**: Maven

## Dependencies
The following external libraries are included in this repository:
- [OpenCSV](https://github.com/opencsv/opencsv) - Version 5.8
- [Commons IO](https://commons.apache.org/proper/commons-io/) - Version 2.14.0

### Usage of Included Libraries
These libraries have been included in the `lib` or a similar directory within this repository, allowing you to use them without the need for external downloads or configurations. You can find these libraries in the following directories:

- `lib/opencsv-5.8.jar` for OpenCSV.
- `lib/commons-io-2.14.0.jar` for Commons IO.

### Maven Configuration (Optional)
If you prefer to manage these libraries using Maven, you can update your `pom.xml` as follows:

```xml
<dependencies>
    <!-- OpenCSV -->
    <dependency>
        <groupId>com.opencsv</groupId>
        <artifactId>opencsv</artifactId>
        <version>5.8</version>
    </dependency>
    
    <!-- Commons IO -->
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>2.14.0</version>
    </dependency>
</dependencies>
```

### :warning: Test Case Coverage Notice :warning:

**Please Note:** The current test case coverage for this project is relatively low. While I have made efforts to ensure the code's correctness and functionality, there may be untested scenarios or edge cases.

I strongly recommend that you use the code with caution, especially in production or critical environments. You may want to consider the following options:

1. **Testing:** If you plan to use this code in a production setting, I encourage you to expand and improve the test coverage. Feel free to contribute by adding more test cases to help ensure the code's reliability.

2. **Modification:** You are welcome to make your own modifications and enhancements as needed. I encourage users to tailor the code to their specific use cases and requirements.

3. **Bug Reporting:** If you encounter any issues or unexpected behavior, please report them in the project's issue tracker. Your feedback and contributions are valuable in improving the codebase.

Your understanding and contributions to improving the test coverage are greatly appreciated. Thank you for using this project!
