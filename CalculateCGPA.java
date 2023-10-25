import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;

public class CalculateCGPA {
    static final int SEMESTER_NUM = 8;
    static final int MAX_COURSE_NUM = 10;
    static String [][] course_code = new String[SEMESTER_NUM][MAX_COURSE_NUM];
    static String [][] course_credit = new String[SEMESTER_NUM][MAX_COURSE_NUM];
    static String [] student_name;
    static float [][] student_gpa = new float[SEMESTER_NUM][MAX_COURSE_NUM];
    static String [] file_path = {
            // Path of course code
            "D:\\OneDrive - Universiti Putra Malaysia\\UPM Sem 7\\ECC4208 Advanced Programming\\Assignment\\CalculateCGPA\\csv\\course_code.csv",
            // Path of course credit
            "D:\\OneDrive - Universiti Putra Malaysia\\UPM Sem 7\\ECC4208 Advanced Programming\\Assignment\\CalculateCGPA\\csv\\course_credit_hour.csv"
            // Path of student name list
            // Path of student result
    };

    public static void main(String [] args) {
        parse_info(file_path);
//        if (parse_info()) {
//            calc_gpa();
//            calc_cgpa();
//
//            print_result();
//            summary_result();
//        }
    }

    public static void parse_info(String [] file_path) {
        parse_course_info(file_path[0], course_code);
        parse_course_info(file_path[1], course_credit);
//        parse_student_info();
    }

    public static void parse_course_info(String course_info_csv_path, String [][] course_container) {
        try (CSVReader reader = new CSVReader(new FileReader(course_info_csv_path))) {
            String[] nextLine;
            int semester_idx = 0;
            int course_idx = 0;

            while ((nextLine = reader.readNext()) != null) {
                // nextLine is an array of values from the line
                for (String val : nextLine) {
                    course_container[semester_idx][course_idx] = val;
                    ++course_idx;
                }
                ++semester_idx;
                course_idx = 0;
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void parse_student_info(String student_csv_path) {

    }

    public static void calc_gpa() {

    }

    public static void calc_cgpa() {

    }

    public static void print_result() {

    }

    public static void summary_result() {

    }
}


