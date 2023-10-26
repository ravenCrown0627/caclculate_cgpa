import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;


public class CalculateCGPA {
    static String error_code;
    static String error_msg;
    static final int SEMESTER_NUM   = 8;
    static final int MAX_COURSE_NUM = 10;

    static String [][] course_code   = new String[SEMESTER_NUM][MAX_COURSE_NUM];
    static String [][] course_credit = new String[SEMESTER_NUM][MAX_COURSE_NUM];
    static float [][] student_result = new float[SEMESTER_NUM][MAX_COURSE_NUM];
    static float [] student_gpa = new float[SEMESTER_NUM];
    static float [] sum_grade = new float[SEMESTER_NUM];
    static int [] semester_total_credit = new int[SEMESTER_NUM];
    static float student_cgpa = 0;
    static String [] student_name;
    static String [] file_path = {
            // Path of course code
            "D:\\OneDrive - Universiti Putra Malaysia\\UPM Sem 7\\ECC4208 Advanced Programming\\Assignment\\CalculateCGPA\\csv\\course_code.csv",
            // Path of course credit
            "D:\\OneDrive - Universiti Putra Malaysia\\UPM Sem 7\\ECC4208 Advanced Programming\\Assignment\\CalculateCGPA\\csv\\course_credit_hour.csv",
            // Path of student result
            "D:\\OneDrive - Universiti Putra Malaysia\\UPM Sem 7\\ECC4208 Advanced Programming\\Assignment\\CalculateCGPA\\csv\\student_course_result.csv",
            // Path of student name list
    };

    public static void main(String [] args) {
        if (parse_info(file_path)) {
            calc_gpa();
            calc_cgpa();

            print_result();
            summary_result();

            System.out.println("Success");
        }
        else {
            System.out.println(error_msg);
        }
    }

    private static boolean is_csv_file(String file_name) {
        return file_name != null && file_name.toLowerCase().endsWith(".csv");
    }

    private static boolean check_file_path(String [] file_path) {
        boolean status = true;

        for (String file : file_path) {
            if (!is_csv_file(file)) {
                error_module("E001");
                status = false;
            }
        }

        return status;
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

    public static boolean parse_info(String [] file_path) {
        boolean status = check_file_path(file_path);

        if (status) {
            parse_course_info(file_path[0], course_code);
            parse_course_info(file_path[1], course_credit);
            // Additional care for this function call due to String to float conversion
            status = parse_student_result(file_path[2]);
        }

        return status;
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

    public static void calc_cgpa() {
        float sum_grade_result = 0;
        int sum_credit_result = 0;

        for (int i = 0; i < SEMESTER_NUM; i++) {
            sum_grade_result += sum_grade[i];
            sum_credit_result += semester_total_credit[i];
        }

        student_cgpa = sum_grade_result / sum_credit_result;
    }

    public static void print_result() {
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
    }

    public static void summary_result() {
        System.out.println("==============================================");
        System.out.printf("%-12s %-12s %-10s %-10s%n", "Semester", "Credit Hour", "Grade", "GPA");
        System.out.println("==============================================");

        for (int i = 0; i < SEMESTER_NUM; i++) {
            System.out.printf("%-12s %-12s %-10s %.2f%n", (i + 1), semester_total_credit[i], generate_grade(student_gpa[i]), student_gpa[i]);
        }

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.printf("%-31s CGPA:%.2f%n", "" ,student_cgpa);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++\n");
    }
}