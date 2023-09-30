import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmployeeProcessor {

    public static void main(String[] args) {
        String filePath = "C:\\Users\\abhig\\Downloads\\Bluejay_Delivery_Assignment.csv"; 

        try {
            processEmployees(filePath);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void processEmployees(String filePath) throws IOException, ParseException {
        FileReader fileReader = new FileReader(filePath);
        CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withHeader());

        List<EmployeeRecord> employeeRecords = new ArrayList<>();

        for (CSVRecord csvRecord : csvParser) {
            int employeeId = Integer.parseInt(csvRecord.get("Employee ID"));
            Date date = new SimpleDateFormat("MM/dd/yyyy").parse(csvRecord.get("Date"));
            int hoursWorked = Integer.parseInt(csvRecord.get("Hours"));

            employeeRecords.add(new EmployeeRecord(employeeId, date, hoursWorked));
        }

        for (int i = 0; i < employeeRecords.size(); i++) {
            EmployeeRecord currentRecord = employeeRecords.get(i);

            if (hasConsecutiveDays(employeeRecords, i, 7)) {
                System.out.println(currentRecord.getEmployeeId() + " has worked for 7 consecutive days.");
            }

            EmployeeRecord nextShift = findNextShift(employeeRecords, i);
            if (nextShift != null) {
                long timeBetweenShifts = (nextShift.getDate().getTime() - currentRecord.getDate().getTime()) / (60 * 60 * 1000);
                if (1 < timeBetweenShifts && timeBetweenShifts < 10) {
                    System.out.println(currentRecord.getEmployeeId() + " has less than 10 hours between shifts but greater than 1 hour.");
                }
            }

            if (currentRecord.getHoursWorked() > 14) {
                System.out.println(currentRecord.getEmployeeId() + " has worked for more than 14 hours in a single shift.");
            }
        }
    }

    private static boolean hasConsecutiveDays(List<EmployeeRecord> records, int currentIndex, int consecutiveDays) {
        int count = 1;
        for (int i = currentIndex + 1; i < records.size(); i++) {
            if (records.get(i).getEmployeeId() == records.get(currentIndex).getEmployeeId()) {
                long daysBetween = (records.get(i).getDate().getTime() - records.get(i - 1).getDate().getTime()) / (24 * 60 * 60 * 1000);
                if (daysBetween == 1) {
                    count++;
                    if (count == consecutiveDays) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        }
        return false;
    }

    private static EmployeeRecord findNextShift(List<EmployeeRecord> records, int currentIndex) {
        for (int i = currentIndex + 1; i < records.size(); i++) {
            if (records.get(i).getEmployeeId() == records.get(currentIndex).getEmployeeId()) {
                return records.get(i);
            }
        }
        return null;
    }
}

class EmployeeRecord {
    private final int employeeId;
    private final Date date;
    private final int hoursWorked;

    public EmployeeRecord(int employeeId, Date date, int hoursWorked) {
        this.employeeId = employeeId;
        this.date = date;
        this.hoursWorked = hoursWorked;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public Date getDate() {
        return date;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }
}
