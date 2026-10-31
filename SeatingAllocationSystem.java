import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ExamRegistration {
    String studentKey;
    String regNo;
    String name;
    String subject;
    String time;
    int seatNumber;
    int totalInRoom;

    public ExamRegistration(String studentKey, String name, String subject, String time) {
        this.studentKey = studentKey;
        this.name = name;
        this.subject = subject;
        this.time = time;
    }
}

class ExamRoom {
    String roomId;
    int capacity;
    ExamRegistration[] seats; 
    int filledSeats = 0;

    public ExamRoom(String roomId, int capacity) {
        this.roomId = roomId;
        this.capacity = capacity;
        this.seats = new ExamRegistration[capacity];
    }

    public boolean addStudent(ExamRegistration student) {
        if (filledSeats < capacity) {
            if (filledSeats > 0 && seats[filledSeats - 1].subject.equals(student.subject)) {
                return false; 
            }
            student.seatNumber = filledSeats + 1;
            seats[filledSeats] = student;
            filledSeats++;
            return true;
        }
        return false;
    }
}

public class SeatingAllocationSystem {
    private List<ExamRegistration> registrations = new ArrayList<>();
    private List<ExamRoom> rooms = new ArrayList<>();

    public void addRegistration(ExamRegistration s) { registrations.add(s); }
    public void addRoom(ExamRoom r) { rooms.add(r); }

    public void allocateSeats() {
        Map<String, String> keyToNameMap = new HashMap<>();
        for (ExamRegistration reg : registrations) {
            keyToNameMap.put(reg.studentKey, reg.name);
        }
        
        List<String> sortedKeys = new ArrayList<>(keyToNameMap.keySet());
        sortedKeys.sort((k1, k2) -> {
            int cmp = keyToNameMap.get(k1).compareTo(keyToNameMap.get(k2));
            if (cmp != 0) return cmp;
            return k1.compareTo(k2);
        });
        
        Map<String, String> keyToRegNoMap = new HashMap<>();
        for (int i = 0; i < sortedKeys.size(); i++) {
            keyToRegNoMap.put(sortedKeys.get(i), String.format("%03d", i + 1));
        }

        for (ExamRegistration reg : registrations) {
            reg.regNo = keyToRegNoMap.get(reg.studentKey);
        }

        // Shuffling all registrations guarantees seat positions randomize per exam to prevent cheating
        Collections.shuffle(registrations);
        for (ExamRegistration reg : registrations) {
            for (ExamRoom room : rooms) {
                if (room.addStudent(reg)) {
                    break;
                }
            }
        }

        // Once all allocations are done, update the 'totalInRoom' for every seated student
        for (ExamRoom room : rooms) {
            for (int i = 0; i < room.filledSeats; i++) {
                room.seats[i].totalInRoom = room.filledSeats;
            }
        }
    }

    public void generateHtmlReport() {
        try (FileWriter searchWriter = new FileWriter("Search.html")) {
            searchWriter.write("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><title>Student Portal Login</title>");
            searchWriter.write("<style>");
            searchWriter.write("body { font-family: 'Segoe UI', sans-serif; background-color: #e9ecef; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }");
            searchWriter.write(".login-box { background: white; padding: 40px; border-radius: 8px; box-shadow: 0 10px 20px rgba(0,0,0,0.1); text-align: center; width: 350px; }");
            searchWriter.write("input { width: 90%; padding: 12px; margin: 20px 0; border: 1px solid #ccc; border-radius: 4px; font-size: 16px; }");
            searchWriter.write("button { width: 100%; padding: 12px; background: #007bff; color: white; border: none; border-radius: 4px; font-size: 16px; cursor: pointer; transition: 0.2s; }");
            searchWriter.write("button:hover { background: #0056b3; }");
            searchWriter.write("</style></head><body>");
            searchWriter.write("<div class='login-box'>");
            searchWriter.write("<h2>Exam Seating Portal</h2>");
            searchWriter.write("<p style='color:#666;'>Search to securely view your schedule.</p>");
            searchWriter.write("<form action='Result.html' method='GET'>");
            searchWriter.write("<input type='text' name='q' placeholder='Enter Name or Registration No' required autocomplete='off'>");
            searchWriter.write("<button type='submit'>Find My Seat</button>");
            searchWriter.write("</form></div>");
            searchWriter.write("</body></html>");
        } catch (IOException e) {
            System.err.println("Error generating Search.html");
        }

        try (FileWriter resultWriter = new FileWriter("Result.html")) {
            resultWriter.write("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><title>Seating Allocation</title>");
            resultWriter.write("<style>");
            resultWriter.write("body { font-family: 'Segoe UI', sans-serif; background-color: #f4f7f6; padding: 40px; display: flex; justify-content: center; flex-direction: column; align-items: center; }");
            resultWriter.write(".card { background: white; padding: 30px; border-radius: 12px; box-shadow: 0 8px 16px rgba(0,0,0,0.1); max-width: 500px; width: 100%; margin-bottom: 20px; }");
            resultWriter.write(".header { border-bottom: 2px solid #007bff; padding-bottom: 15px; margin-bottom: 20px; text-align: center; }");
            resultWriter.write(".header h1 { margin: 0; color: #333; font-size: 24px; }");
            resultWriter.write(".data-row { display: flex; justify-content: space-between; margin-bottom: 15px; font-size: 18px; border-bottom: 1px dashed #eee; padding-bottom: 10px; }");
            resultWriter.write(".label { color: #6c757d; font-weight: bold; }");
            resultWriter.write(".value { color: #212529; font-weight: 600; text-transform: capitalize; text-align: right; }");
            resultWriter.write(".exam-block { background: #f8f9fa; padding: 15px; border-radius: 8px; margin-bottom: 15px; border-left: 4px solid #007bff; }");
            resultWriter.write(".exam-block .data-row { border-bottom: none; margin-bottom: 8px; padding-bottom: 0; }");
            resultWriter.write(".room-highlight { color: #28a745; font-size: 20px; }");
            resultWriter.write(".seat-highlight { color: #dc3545; font-size: 20px; }");
            resultWriter.write(".btn-back { display: block; text-align: center; margin-top: 25px; text-decoration: none; color: #007bff; font-weight: bold; }");
            resultWriter.write(".error { color: #dc3545; text-align: center; display: none; }");
            resultWriter.write("</style>");
            
            resultWriter.write("<script>");
            resultWriter.write("const database = [");
            for (ExamRoom room : rooms) {
                for (int i = 0; i < room.filledSeats; i++) {
                    ExamRegistration s = room.seats[i];
                    // Appended seat and total data directly into the database JSON
                    resultWriter.write(String.format("{reg:'%s', name:'%s', subject:'%s', time:'%s', room:'%s', seat:%d, total:%d},", 
                            s.regNo, s.name.toLowerCase(), s.subject, s.time, room.roomId, s.seatNumber, s.totalInRoom));
                }
            }
            resultWriter.write("];");
            
            resultWriter.write("window.onload = function() {");
            resultWriter.write("  const params = new URLSearchParams(window.location.search);");
            resultWriter.write("  const query = params.get('q');");
            resultWriter.write("  if (query) {");
            resultWriter.write("      const searchStr = query.toLowerCase().trim();");
            resultWriter.write("      let paddedReg = searchStr;");
            resultWriter.write("      if (!isNaN(searchStr) && searchStr.length < 3) { paddedReg = searchStr.padStart(3, '0'); }");
            
            resultWriter.write("      const studentExams = database.filter(s => s.name === searchStr || s.reg === paddedReg);");
            resultWriter.write("      if (studentExams.length > 0) {");
            
            resultWriter.write("          const grouped = {};");
            resultWriter.write("          studentExams.forEach(exam => {");
            resultWriter.write("              if(!grouped[exam.reg]) grouped[exam.reg] = [];");
            resultWriter.write("              grouped[exam.reg].push(exam);");
            resultWriter.write("          });");
            
            resultWriter.write("          let html = '';");
            resultWriter.write("          for (const regNo in grouped) {");
            resultWriter.write("              const exams = grouped[regNo];");
            resultWriter.write("              html += '<div class=\"card\">';");
            resultWriter.write("              html += '<div class=\"header\"><h1>Official Admit Card</h1></div>';");
            resultWriter.write("              html += '<div class=\"data-row\"><span class=\"label\">Student Name</span><span class=\"value\">' + exams[0].name + '</span></div>';");
            resultWriter.write("              html += '<div class=\"data-row\"><span class=\"label\">Registration No</span><span class=\"value\">' + exams[0].reg + '</span></div>';");
            resultWriter.write("              html += '<hr style=\"border: 1px solid #ddd; margin: 20px 0;\">';");
            resultWriter.write("              html += '<h3 style=\"color:#495057; font-size: 16px;\">Allocated Exams (' + exams.length + ')</h3>';");
            resultWriter.write("              exams.forEach(exam => {");
            resultWriter.write("                  html += '<div class=\"exam-block\">';");
            resultWriter.write("                  html += '<div class=\"data-row\"><span class=\"label\">Course</span><span class=\"value\">' + exam.subject + '</span></div>';");
            resultWriter.write("                  html += '<div class=\"data-row\"><span class=\"label\">Time</span><span class=\"value\">' + exam.time + '</span></div>';");
            resultWriter.write("                  html += '<div class=\"data-row\"><span class=\"label\">Room</span><span class=\"value room-highlight\">' + exam.room + '</span></div>';");

            resultWriter.write("                  html += '<div class=\"data-row\"><span class=\"label\">Seat No.</span><span class=\"value seat-highlight\">' + exam.seat + '</span></div>';");
            resultWriter.write("                  html += '<div class=\"data-row\"><span class=\"label\">Total Students</span><span class=\"value\">' + exam.total + '</span></div>';");
            resultWriter.write("                  html += '</div>';");
            resultWriter.write("              });");
            resultWriter.write("              html += '</div>';");
            resultWriter.write("          }");
            resultWriter.write("          html += '<a href=\"Search.html\" class=\"btn-back\">← Back to Search</a>';");
            resultWriter.write("          document.getElementById('result-container').innerHTML = html;");
            resultWriter.write("      } else {");
            resultWriter.write("          document.getElementById('error-msg').innerText = 'No records found for \"' + query + '\".';");
            resultWriter.write("          document.getElementById('error-msg').style.display = 'block';");
            resultWriter.write("          document.getElementById('result-container').innerHTML = '<a href=\"Search.html\" class=\"btn-back\">← Back to Search</a>';");
            resultWriter.write("      }");
            resultWriter.write("  } else {");
            resultWriter.write("      window.location.href = 'Search.html';");
            resultWriter.write("  }");
            resultWriter.write("};");
            resultWriter.write("</script>");
            
            resultWriter.write("</head><body>");
            resultWriter.write("<h2 id='error-msg' class='error'></h2>");
            resultWriter.write("<div id='result-container'></div>"); 
            resultWriter.write("</body></html>");
        } catch (IOException e) {
            System.err.println("Error generating Result.html");
        }
    }

    public static void main(String[] args) {
        SeatingAllocationSystem system = new SeatingAllocationSystem();
        
        system.addRoom(new ExamRoom("LHC 001", 20));
        system.addRoom(new ExamRoom("LHC 002", 20));
        system.addRoom(new ExamRoom("LHC 003", 20));
        system.addRoom(new ExamRoom("LHC 004", 20));
        system.addRoom(new ExamRoom("LHC 005", 20));
        system.addRoom(new ExamRoom("LHC 006", 20));
        system.addRoom(new ExamRoom("LHC 007", 20));
        system.addRoom(new ExamRoom("LHC 008", 20));
        system.addRoom(new ExamRoom("LHC 009", 20));
        system.addRoom(new ExamRoom("LHC 010", 20));

        system.addRegistration(new ExamRegistration("S1", "Aditi Sharma", "CSC2103 / RDBMS", "1:00 PM"));
        system.addRegistration(new ExamRegistration("S2", "Aditya Verma", "CSC2102 / COA", "9:00 AM"));
        system.addRegistration(new ExamRegistration("S3", "Ali Khan", "CSC2130 / DSA Lab", "3:00 PM"));
        system.addRegistration(new ExamRegistration("S4", "Armaan Malik", "CSC2101 / DSA", "9:00 AM"));
        system.addRegistration(new ExamRegistration("S4", "Armaan Malik", "CSC2104 / OOP", "1:00 PM")); 
        system.addRegistration(new ExamRegistration("S5", "Christopher Smith", "CSC2131 / RDBMS Lab", "3:00 PM"));
        system.addRegistration(new ExamRegistration("S6", "Isha Patel", "CSC2104 / OOP", "1:00 PM"));
        system.addRegistration(new ExamRegistration("S6", "Isha Patel", "CSC2131 / RDBMS Lab", "3:00 PM")); 
        system.addRegistration(new ExamRegistration("S7", "Jay Gupta", "MBA7179 / POM", "9:00 AM"));
        system.addRegistration(new ExamRegistration("S8", "Jay Gupta", "CSC2104 / OOP", "1:00 PM"));
        system.addRegistration(new ExamRegistration("S9", "Parth Desai", "CSC2104 / OOP", "1:00 PM"));
        system.addRegistration(new ExamRegistration("S10", "Ram Singh", "MAS001 / S&P", "1:00 PM"));
        system.addRegistration(new ExamRegistration("S11", "Shruthi Nair", "MAS001 / S&P", "1:00 PM"));
        system.addRegistration(new ExamRegistration("S11", "Shruthi Nair", "CSC2130 / DSA Lab", "3:00 PM")); 
        system.addRegistration(new ExamRegistration("S11", "Shruthi Nair", "CSC2102 / COA", "9:00 AM"));    
        system.addRegistration(new ExamRegistration("S12", "Siddharth Rao", "CSC2101 / DSA", "9:00 AM"));
        system.addRegistration(new ExamRegistration("S13", "Tanuj Joshi", "MBA7179 / POM", "9:00 AM"));
        system.addRegistration(new ExamRegistration("S14", "Zakia Jamal Atta", "CSC2104 / OOP", "1:00 PM"));
        system.addRegistration(new ExamRegistration("S14", "Zakia Jamal Atta", "MBA7179 / POM", "9:00 AM"));
        system.addRegistration(new ExamRegistration("S14", "Zakia Jamal Atta", "CSC2131 / RDBMS Lab", "3:00 PM"));
        system.addRegistration(new ExamRegistration("S15", "Ananya Patel", "CSC2101 / DSA", "9:00 AM"));
        system.addRegistration(new ExamRegistration("S16", "Rohan Sharma", "CSC2104 / OOP", "1:00 PM"));
        system.addRegistration(new ExamRegistration("S17", "Kavya Iyer", "MBA7179 / POM", "9:00 AM"));
        system.addRegistration(new ExamRegistration("S17", "Kavya Iyer", "CSC2130 / DSA Lab", "3:00 PM")); 
        system.addRegistration(new ExamRegistration("S18", "Vikram Singh", "CSC2102 / COA", "9:00 AM"));
        system.addRegistration(new ExamRegistration("S19", "Meera Reddy", "MAS001 / S&P", "1:00 PM"));
        system.addRegistration(new ExamRegistration("S20", "Arjun Nair", "CSC2103 / RDBMS", "1:00 PM"));
        system.addRegistration(new ExamRegistration("S21", "Diya Kapoor", "CSC2131 / RDBMS Lab", "3:00 PM"));
        system.addRegistration(new ExamRegistration("S22", "Aryan Mehta", "CSC2101 / DSA", "9:00 AM"));
        system.addRegistration(new ExamRegistration("S23", "Neha Desai", "CSC2104 / OOP", "1:00 PM"));
        system.addRegistration(new ExamRegistration("S24", "Rahul Joshi", "CSC2102 / COA", "9:00 AM"));
        system.addRegistration(new ExamRegistration("S25", "Sneha Verma", "MAS001 / S&P", "1:00 PM"));
        system.addRegistration(new ExamRegistration("S26", "Karan Ahuja", "MBA7179 / POM", "9:00 AM"));
        system.addRegistration(new ExamRegistration("S27", "Pooja Choudhary", "CSC2131 / RDBMS Lab", "3:00 PM"));

        system.allocateSeats();
        system.generateHtmlReport();
        System.out.println("Success! Seat numbers and total students added to output.");
    }
}
