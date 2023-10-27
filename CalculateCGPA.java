import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.Objects;
import org.apache.commons.io.output.TeeOutputStream;


public class CalculateCGPA {
    static String error_code;
    static String error_msg;
    static final int SEMESTER_NUM   = 8;
    static final int MAX_COURSE_NUM = 10;
    static final int STUDENT_NUM = 5;

    static String [][] course_code   = new String[SEMESTER_NUM][MAX_COURSE_NUM];
    static String [][] course_credit = new String[SEMESTER_NUM][MAX_COURSE_NUM];
    static float [][] student_result = new float[SEMESTER_NUM][MAX_COURSE_NUM];
    static String [] student_name = new String[STUDENT_NUM];
    static float [] student_gpa = new float[SEMESTER_NUM];
    static float [] sum_grade = new float[SEMESTER_NUM];
    static int [] semester_total_credit = new int[SEMESTER_NUM];
    static float [] student_cgpa = new float [STUDENT_NUM];

    static String student_name_file_path =  "csv\\student_name.csv";
    static String course_code_file_path = "csv\\course_code.csv";
    static String course_credit_file_path = "csv\\course_credit_hour.csv";
    static String [] student_result_file_path = {
        "csv\\zhilin_result.csv",
        "csv\\yewy_result.csv",
        "csv\\tabina_result.csv",
        "csv\\shisilia_result.csv",
        "csv\\hasif_result.csv"
    };

    public static void main(String [] args) {
        int student_idx = 0;

        if (check_file_path()) {
            parse_course_info(course_code_file_path, course_code);
            parse_course_info(course_credit_file_path, course_credit);
            parse_student_name(student_name_file_path);

            for (String student_result_file : student_result_file_path) {
                if (parse_student_result(student_result_file)) {
                    calc_gpa();
                    calc_cgpa(student_idx);

                    print_result(student_idx);
                    print_summary_result(student_idx);
                    student_idx++;
                }
                else break;
            }
        }

        if (error_code != null)
            System.out.println(error_msg);
        else
            System.out.println("Success");
    }

    private static boolean is_csv_file(String file_path) {
        boolean status = file_path != null && file_path.toLowerCase().endsWith(".csv");

        if (!status)
            error_module("E001");

        return status;
    }

    private static boolean check_file_path() {
        boolean status = true;

        status = is_csv_file(student_name_file_path);
        if (status) {
            status = is_csv_file(course_code_file_path);

            if (status) {
                status = is_csv_file(course_credit_file_path);

                if (status) {
                    for (String file_path : student_result_file_path) {
                        status = is_csv_file(file_path);

                        if (!status) break;
                    }
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

    public static void parse_course_info(String course_info_csv_path, String [][] course_container) {
        String[] nextLine;
        int semester_idx = 0;
        int course_idx = 0;

        try (CSVReader reader = new CSVReader(new FileReader(course_info_csv_path))) {
            try {
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine is an array of values from the line
                    for (String val : nextLine) {
                        if (val.startsWith("\uFEFF")) {
                            val = val.substring(1); // Remove the BOM
                        }

                        course_container[semester_idx][course_idx] = val;
                        ++course_idx;
                    }
                    ++semester_idx;
                    course_idx = 0;
                }
            } catch (IOException | CsvValidationException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void parse_student_name(String student_name_csv_path) {
        String[] nextLine;
        int student_idx = 0;

        try (CSVReader reader = new CSVReader(new FileReader(student_name_csv_path))) {
            try {
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine is an array of values from the line
                    for (String val : nextLine) {
                        if (val.startsWith("\uFEFF")) {
                            val = val.substring(1); // Remove the BOM
                        }

                        student_name[student_idx] = val;
                    }

                    ++student_idx;
                }
            } catch (IOException | CsvValidationException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean parse_student_result(String student_result_csv_path) {
        boolean status = true;
        String[] nextLine;
        int semester_idx = 0;
        int course_idx = 0;

        try (CSVReader reader = new CSVReader(new FileReader(student_result_csv_path))) {
            try {
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine is an array of values from the line
                    for (String val : nextLine) {
                        try {
                            if (val.startsWith("\uFEFF")) {
                                val = val.substring(1); // Remove the BOM
                            }

                            student_result[semester_idx][course_idx] = Float.parseFloat(val);
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
                    else {
                        break;
                    }
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
            TeeOutputStream teeOutputStream = new TeeOutputStream(System.out, fos);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);

            System.out.println("Name: " + student_name[student_idx]);

            for (int i = 0; i < SEMESTER_NUM; i++) {
                System.out.println("Semester " + (i + 1));
                System.out.println("==========================================================");
                System.out.printf("%-15s %-15s %-10s %-15s%n", "Course Code", "Credit Hour", "Grade", "Grade Value");
                System.out.println("==========================================================");

                for (int j = 0; j < course_code.length; j++) {
                    if (course_code[i][j] != null) {
                        System.out.printf("%-15s %-15s %-10s %.2f%n", course_code[i][j], course_credit[i][j], generate_grade(student_result[i][j]), student_result[i][j]);
                    }
                }
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.printf("%-38s GPA:%.2f%n", "" ,student_gpa[i]);
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            }

            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void print_summary_result(int student_idx) {
        System.out.println("==============================================");
        System.out.printf("%-12s %-12s %-10s %-10s%n", "Semester", "Credit Hour", "Grade", "GPA");
        System.out.println("==============================================");

        for (int i = 0; i < SEMESTER_NUM; i++) {
            System.out.printf("%-12s %-12s %-10s %.2f%n", (i + 1), semester_total_credit[i], generate_grade(student_gpa[i]), student_gpa[i]);
        }

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.printf("%-31s CGPA:%.2f%n", "" ,student_cgpa[student_idx]);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++\n");
    }
}