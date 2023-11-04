import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.Objects;

public class CalculateCGPA {
    static String error_code;
    static String error_msg;
    static final int SEMESTER_NUM   = 8;
    static final int MAX_COURSE_NUM = 10;
    static final int STUDENT_NUM    = 5;

    static String [][] course_code = new String[SEMESTER_NUM][MAX_COURSE_NUM];
    static String [][] course_credit = new String[SEMESTER_NUM][MAX_COURSE_NUM];
    static float [][] student_result = new float[SEMESTER_NUM][MAX_COURSE_NUM];
    static String [] student_name = new String[STUDENT_NUM];
    static float [] student_gpa = new float[SEMESTER_NUM];
    static float [] sum_grade = new float[SEMESTER_NUM];
    static int [] semester_total_credit = new int[SEMESTER_NUM];
    static float [] student_cgpa = new float [STUDENT_NUM];

    static String course_info_file_path = "csv\\course_info.csv";
    static String [] student_result_file_path = {
        "csv\\zhilin_result.csv",
        "csv\\yewy_result.csv",
        "csv\\tabina_result.csv",
        "csv\\shisilia_result.csv",
        "csv\\hasif_result.csv"
    };

    public static void main(String [] args) {
        if (check_file_path()) {
            parse_course_info(course_info_file_path);

            for (int student_idx = 0; student_idx < STUDENT_NUM; student_idx++) {
                if (parse_student_result(student_result_file_path[student_idx], student_idx)) {
                    calc_gpa();
                    calc_cgpa(student_idx);

                    print_result(student_idx);
                    print_summary_result(student_idx);
                }
                else
                    break;
            }
        }

        if (error_code != null)
            System.out.println(error_msg);
    }

    private static boolean is_csv_file(String file_path) {
        boolean status = file_path != null && file_path.toLowerCase().endsWith(".csv");

        if (!status)
            error_module("E001");

        return status;
    }

    private static boolean check_file_path() {
        boolean status = is_csv_file(course_info_file_path);

        if (status) {
            for (String file_path : student_result_file_path) {
                if (!is_csv_file(file_path)) {
                    status = false;
                    break;
                }
            }
        }

        return status;
    }

    private static int [] convert_str_to_int(String [][] array, int row_idx) {
        int [] int_array = new int[array[row_idx].length];

        // Convert String to int
        for (int k = 0; k < array[row_idx].length; k++) {
            String temp_str = array[row_idx][k];

            if (temp_str != null)
                int_array[k] = Integer.parseInt(temp_str);
            else
                break;
        }

        return int_array;
    }

    private static String generate_grade(float grade_val) {
        String grade;

        if (grade_val > 3.750 && grade_val <= 4.000) {
            grade = "A";
        } else if (grade_val > 3.500 && grade_val <= 3.750) {
            grade = "A-";
        } else if (grade_val > 3.000 && grade_val <= 3.500) {
            grade = "B+";
        } else if (grade_val > 2.750 && grade_val <= 3.00) {
            grade = "B";
        } else if (grade_val > 2.500 && grade_val <= 2.750) {
            grade = "B-";
        } else if (grade_val > 2.000 && grade_val <= 2.500) {
            grade = "C+";
        } else if (grade_val > 1.750 && grade_val <= 2.000) {
            grade = "C";
        } else if (grade_val > 1.500 && grade_val <= 1.750) {
            grade = "C-";
        } else if (grade_val > 1.000 && grade_val <= 1.500) {
            grade = "D+";
        } else if (grade_val == 1.000) {
            grade = "D";
        } else {
            grade = "F";
        }

        return grade;
    }

    private static void error_module(String code) {
        error_code = code;

        if (Objects.equals(error_code, "E001")) {
            error_msg = "There is an error file type";
        }
        else if (Objects.equals(error_code, "E002")) {
            error_msg = "The data in the student result has invalid data (Check for the BOM elimination)";
        }
    }

    public static void parse_course_info(String course_info_csv_path) {
        String[] next_line;
        String [][] course_container = course_code;
        int semester_idx = 0;
        int course_idx = 0;
        int line_idx = 0;

        try (CSVReader reader = new CSVReader(new FileReader(course_info_csv_path))) {
            try {
                while ((next_line = reader.readNext()) != null) {
                    // switching the container to store the course code and course credit interleave
                    if (line_idx % 2 == 0) {
                        course_container = course_code;
                    }
                    else {
                        course_container = course_credit;
                    }

                    // next_line is an array of values from the line
                    for (String val : next_line) {
                        if (val.startsWith("\uFEFF")) {
                            val = val.substring(1); // Remove the BOM
                        }

                        course_container[semester_idx][course_idx] = val;
                        ++course_idx;
                    }

                    if (line_idx % 2 != 0)
                        ++semester_idx;

                    ++line_idx;
                    course_idx = 0;
                }
            } catch (IOException | CsvValidationException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean parse_student_result(String student_result_csv_path, final int student_idx) {
        boolean status = true;
        String[] next_line;
        int semester_idx = 0;
        int course_idx = 0;

        try (CSVReader reader = new CSVReader(new FileReader(student_result_csv_path))) {
            try {
                while ((next_line = reader.readNext()) != null) {
                    // Only the first line has BOM char and contain the student name
                    if (next_line[0].startsWith("\uFEFF")) {
                        student_name[student_idx] = next_line[0].substring(1);
                        continue;
                    }

                    // next_line is an array of values from the line
                    for (String val : next_line) {
                        try {
                            student_result[semester_idx][course_idx] = Float.parseFloat(val);

                            // Modify the point to 0.0 if it is less than 1.000
                            if (student_result[semester_idx][course_idx] < 1.000)
                                student_result[semester_idx][course_idx] = 0;
                        } catch (NumberFormatException e ) {
                            status = false;
                            error_module("E002");
                        }

                        if (status)
                            ++course_idx;
                        else
                            break;
                    }

                    if (status) {
                        ++semester_idx;
                        course_idx = 0;
                    }
                    else
                        break;
                }
            } catch (IOException | CsvValidationException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return status;
    }

    public static void calc_gpa() {
        for (int i = 0; i < SEMESTER_NUM; i++) {
            float temp_sum_grade = 0;
            int sum_credit = 0;
            int [] credit;

            credit = convert_str_to_int(course_credit, i);

            for (int j = 0; j < student_result[i].length; j++) {
                temp_sum_grade += credit[j] * student_result[i][j];
                sum_credit += credit[j];
            }

            sum_grade[i] = temp_sum_grade;
            student_gpa[i] =  temp_sum_grade / sum_credit;
            semester_total_credit[i] = sum_credit;
        }
    }

    public static void calc_cgpa(int student_idx) {
        float sum_grade_result = 0;
        int sum_credit_result = 0;

        for (int i = 0; i < SEMESTER_NUM; i++) {
            sum_grade_result += sum_grade[i];
            sum_credit_result += semester_total_credit[i];
        }

        student_cgpa[student_idx] = sum_grade_result / sum_credit_result;
    }

    public static void print_result(int student_idx) {
        String output_file_path = "csv//output//" + student_name[student_idx] + "_semester_detail_output.txt";

        try {
            // Create a FileOutputStream to write to the file
            FileOutputStream fos = new FileOutputStream(output_file_path);

            // Redirect standard output to the file
            PrintStream console_out = System.out;
            PrintStream file_out = new PrintStream(fos);
            System.setOut(file_out);

            // Display on console
            console_out.println("Name: " + student_name[student_idx]);
            // Print to file
            file_out.println("Name: " + student_name[student_idx]);

            for (int i = 0; i < SEMESTER_NUM; i++) {
                // Display on console
                console_out.println("Semester " + (i + 1));
                console_out.println("==========================================================");
                console_out.printf("%-15s %-15s %-10s %-15s%n", "Course Code", "Credit Hour", "Grade", "Grade Value");
                console_out.println("==========================================================");
                // Print to file
                file_out.println("Semester " + (i + 1));
                file_out.println("==========================================================");
                file_out.printf("%-15s %-15s %-10s %-15s%n", "Course Code", "Credit Hour", "Grade", "Grade Value");
                file_out.println("==========================================================");

                for (int j = 0; j < course_code.length; j++) {
                    if (course_code[i][j] != null) {
                        // Display on console
                        console_out.printf("%-15s %-15s %-10s %.3f%n", course_code[i][j], course_credit[i][j], generate_grade(student_result[i][j]), student_result[i][j]);
                        // Print to file
                        file_out.printf("%-15s %-15s %-10s %.3f%n", course_code[i][j], course_credit[i][j], generate_grade(student_result[i][j]), student_result[i][j]);
                    }
                }

                // Display on console
                console_out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                console_out.printf("%-22s    GPA:  %-10s %.3f%n", "" , generate_grade(student_gpa[i]), student_gpa[i]);
                console_out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
                // Print to file
                file_out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                file_out.printf("%-22s    GPA:  %-10s %.3f%n", "", generate_grade(student_gpa[i]), student_gpa[i]);
                file_out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            }

            fos.close();
            // Close the file of PrintStream
            file_out.close();
            // Reset the System.out ro original PrintStream for terminal
            System.setOut(console_out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void print_summary_result(int student_idx) {
        String output_file_path = "csv//output//" + student_name[student_idx] + "_semester_detail_output.txt";

        try {
            // Create a FileOutputStream to write to the file
            FileOutputStream fos = new FileOutputStream(output_file_path, true);

            // Redirect standard output to the file
            PrintStream console_out = System.out;
            PrintStream file_out = new PrintStream(fos);
            System.setOut(file_out);

            // Display on console
            console_out.println("==============================================");
            console_out.printf("%-12s %-12s %-10s %-10s%n", "Semester", "Credit Hour", "Grade", "GPA");
            console_out.println("==============================================");
            // Print to file
            file_out.println("==============================================");
            file_out.printf("%-12s %-12s %-10s %-10s%n", "Semester", "Credit Hour", "Grade", "GPA");
            file_out.println("==============================================");

            for (int i = 0; i < SEMESTER_NUM; i++) {
                // Display on console
                console_out.printf("%-12s %-12s %-10s %.3f%n", (i + 1), semester_total_credit[i], generate_grade(student_gpa[i]), student_gpa[i]);
                // Print to file
                file_out.printf("%-12s %-12s %-10s %.3f%n", (i + 1), semester_total_credit[i], generate_grade(student_gpa[i]), student_gpa[i]);
            }

            // Display on console
            console_out.println("++++++++++++++++++++++++++++++++++++++++++++++");
            console_out.printf("%-16s   CGPA:  %-10s %.3f%n", "", generate_grade(student_cgpa[student_idx]), student_cgpa[student_idx]);
            console_out.println("++++++++++++++++++++++++++++++++++++++++++++++\n");
            // Print to file
            file_out.println("++++++++++++++++++++++++++++++++++++++++++++++");
            file_out.printf("%-16s   CGPA:  %-10s %.3f%n", "", generate_grade(student_cgpa[student_idx]), student_cgpa[student_idx]);
            file_out.println("++++++++++++++++++++++++++++++++++++++++++++++");

            fos.close();
            // Close the file of PrintStream
            file_out.close();
            // Reset the System.out ro original PrintStream for terminal
            System.setOut(console_out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}