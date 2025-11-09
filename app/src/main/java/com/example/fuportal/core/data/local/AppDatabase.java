package com.example.fuportal.core.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

// Import tất cả các DAO
import com.example.fuportal.core.data.local.dao.ApplicationDao;
import com.example.fuportal.core.data.local.dao.ApplicationTypeDao;
import com.example.fuportal.core.data.local.dao.AttendanceDao;
import com.example.fuportal.core.data.local.dao.ClassDao;
import com.example.fuportal.core.data.local.dao.CourseDao;
import com.example.fuportal.core.data.local.dao.EnrollmentDao;
import com.example.fuportal.core.data.local.dao.ExamScheduleDao;
import com.example.fuportal.core.data.local.dao.FacultyDao;
import com.example.fuportal.core.data.local.dao.GradeComponentDao;
import com.example.fuportal.core.data.local.dao.GradeDao;
import com.example.fuportal.core.data.local.dao.MajorDao;
import com.example.fuportal.core.data.local.dao.RoleDao;
import com.example.fuportal.core.data.local.dao.ScheduleDao;
import com.example.fuportal.core.data.local.dao.SemesterDao;
import com.example.fuportal.core.data.local.dao.TimeSlotDao;
import com.example.fuportal.core.data.local.dao.UserDao;
import com.example.fuportal.core.data.local.dao.CampusDao;

// Import tất cả các Entity
import com.example.fuportal.core.data.model.AcademicClass;
import com.example.fuportal.core.data.model.Application;
import com.example.fuportal.core.data.model.ApplicationType;
import com.example.fuportal.core.data.model.Attendance;
import com.example.fuportal.core.data.model.Course;
import com.example.fuportal.core.data.model.Enrollment;
import com.example.fuportal.core.data.model.ExamSchedule;
import com.example.fuportal.core.data.model.Faculty;
import com.example.fuportal.core.data.model.Grade;
import com.example.fuportal.core.data.model.GradeComponent;
import com.example.fuportal.core.data.model.Major;
import com.example.fuportal.core.data.model.Schedule;
import com.example.fuportal.core.data.model.Semester;
import com.example.fuportal.core.data.model.TimeSlot;
import com.example.fuportal.core.data.model.User;
import com.example.fuportal.core.data.model.Role;
import com.example.fuportal.core.data.model.Campus;
import com.example.fuportal.core.util.DateHelper;
//import com.example.fuportal.core.data.model.Faculty;
//import com.example.fuportal.core.data.model.Major;
//import com.example.fuportal.core.data.model.Semester;
//import com.example.fuportal.core.data.model.TimeSlot;
//import com.example.fuportal.core.data.model.ApplicationType;
//import com.example.fuportal.core.data.model.Course;
 //import com.example.fuportal.core.data.model.Schedule;
//import com.example.fuportal.core.data.model.Enrollment;
//import com.example.fuportal.core.data.model.GradeComponent;
//import com.example.fuportal.core.data.model.Grade;
//import com.example.fuportal.core.data.model.Attendance;
//import com.example.fuportal.core.data.model.ExamSchedule;
//import com.example.fuportal.core.data.model.Announcement;
//import com.example.fuportal.core.data.model.Application;
// Import BCrypt
import org.mindrot.jbcrypt.BCrypt;
import java.util.concurrent.Executors;
// 1. Khai báo tất cả các Entity
@Database(entities = {
        Role.class, Campus.class,User.class, Course.class,
        Faculty.class, Major.class,
        Semester.class, AcademicClass.class,
        TimeSlot.class, Schedule.class, Enrollment.class, Attendance.class, GradeComponent.class,
        Grade.class,  ExamSchedule.class,
        ApplicationType.class,
//            Announcement.class,
        Application.class
}, version = 1, exportSchema = false) // Tăng 'version' mỗi khi bạn thay đổi CSDL

public abstract class AppDatabase extends RoomDatabase {

    // 2. Khai báo các DAO (abstract method)
    public abstract UserDao userDao();
    public abstract RoleDao roleDao();
    public abstract CampusDao campusDao(); // <-- Thêm dòng này
    public abstract CourseDao courseDao();
    public abstract FacultyDao facultyDao(); // <-- THÊM DÒNG NÀY
    public abstract MajorDao majorDao();
    public abstract SemesterDao semesterDao();
    public abstract ClassDao classDao();
    public abstract TimeSlotDao timeSlotDao();
    public abstract ScheduleDao scheduleDao();
    public abstract EnrollmentDao enrollmentDao();
    public abstract AttendanceDao attendanceDao();
    public abstract GradeComponentDao gradeComponentDao();
    public abstract GradeDao gradeDao();
    public abstract ExamScheduleDao examScheduleDao(); // <-- THÊM DAO MỚI
    public abstract ApplicationTypeDao applicationTypeDao(); // <-- THÊM DAO MỚI
    public abstract ApplicationDao applicationDao(); // <-- THÊM DAO MỚI


    // 3. Tạo Singleton để đảm bảo chỉ có 1 instance của CSDL
    private static volatile AppDatabase INSTANCE;
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            Executors.newSingleThreadExecutor().execute(() -> {
                // Lấy các DAO
                RoleDao roleDao = INSTANCE.roleDao();
                CampusDao campusDao = INSTANCE.campusDao();
                UserDao userDao = INSTANCE.userDao();
                FacultyDao facultyDao = INSTANCE.facultyDao();
                MajorDao majorDao = INSTANCE.majorDao();
                SemesterDao semesterDao = INSTANCE.semesterDao();
                CourseDao courseDao = INSTANCE.courseDao();
                ClassDao classDao = INSTANCE.classDao();
                TimeSlotDao timeSlotDao = INSTANCE.timeSlotDao();
                ScheduleDao scheduleDao = INSTANCE.scheduleDao();
                EnrollmentDao enrollmentDao = INSTANCE.enrollmentDao();
                AttendanceDao attendanceDao = INSTANCE.attendanceDao();
                GradeComponentDao gradeComponentDao = INSTANCE.gradeComponentDao();
                GradeDao gradeDao = INSTANCE.gradeDao();
                ExamScheduleDao examScheduleDao = INSTANCE.examScheduleDao(); // <-- THÊM DAO MỚI
                ApplicationTypeDao applicationTypeDao = INSTANCE.applicationTypeDao();

                //

                DateHelper helper = new DateHelper();

                // === BƯỚC 1: CHÈN CÁC BẢNG KHÔNG PHỤ THUỘC ===

                // 1.1. Chèn Roles (ID: 1-5)
                roleDao.insertRole(new Role("Student"));
                roleDao.insertRole(new Role("Lecturer"));
                roleDao.insertRole(new Role("AcademicAffairs"));
                roleDao.insertRole(new Role("ExaminationDept"));
                roleDao.insertRole(new Role("Admin"));

                // 1.2. Chèn Campuses (ID: 1-5)
                campusDao.insertCampus(new Campus("Hà Nội", "Khu CNC Hòa Lạc, Thạch Thất, Hà Nội"));
                campusDao.insertCampus(new Campus("TP.HCM", "Lô E2a-7, Đường D1, Khu CNC, TP. Thủ Đức"));
                campusDao.insertCampus(new Campus("Đà Nẵng", "Khu đô thị FPT City, Phường Hòa Hải"));
                campusDao.insertCampus(new Campus("Cần Thơ", "Đường Nguyễn Văn Cừ, Phường An Bình"));
                campusDao.insertCampus(new Campus("Quy Nhơn", "Khu đô thị mới An Phú Thịnh"));

                // 1.3. Chèn Faculties (ID: 1-2)
                facultyDao.insertFaculty(new Faculty("Khoa Kỹ thuật phần mềm"));
                facultyDao.insertFaculty(new Faculty("Khoa Quản trị kinh doanh"));

                // 1.4. Chèn TimeSlots (Ca học) (ID: 1-6)
                timeSlotDao.insertTimeSlot(new TimeSlot("07:30", "09:00")); // Slot 1
                timeSlotDao.insertTimeSlot(new TimeSlot("09:10", "10:40")); // Slot 2
                timeSlotDao.insertTimeSlot(new TimeSlot("10:50", "12:20")); // Slot 3
                timeSlotDao.insertTimeSlot(new TimeSlot("12:50", "14:20")); // Slot 4
                timeSlotDao.insertTimeSlot(new TimeSlot("14:30", "16:00")); // Slot 5
                timeSlotDao.insertTimeSlot(new TimeSlot("16:10", "17:40")); // Slot 6

                // 1.5. Chèn Semesters (ID: 1-2)
                semesterDao.insertSemester(new Semester("Summer 2025", helper.dateToLong("2025-05-01"), helper.dateToLong("2025-08-31"), helper.dateToLong("2025-04-20"), helper.dateToLong("2025-04-25")));
                semesterDao.insertSemester(new Semester("Fall 2025", helper.dateToLong("2025-09-04"), helper.dateToLong("2025-12-05"), helper.dateToLong("2025-08-20"), helper.dateToLong("2025-08-25")));

                // === BƯỚC 2: CHÈN CÁC BẢNG PHỤ THUỘC (CẤP 1) ===

                // 2.1. Chèn Majors (phụ thuộc Faculty) (ID: 1-3)
                majorDao.insertMajor(new Major("Kỹ thuật phần mềm (SE)", 1));
                majorDao.insertMajor(new Major("An toàn thông tin (IA)", 1));
                majorDao.insertMajor(new Major("Quản trị kinh doanh (IB)", 2));

                // 2.2. Chèn Users (phụ thuộc Role, Campus)
                String defaultPassHash = BCrypt.hashpw("123456", BCrypt.gensalt());
                userDao.insertUser(new User("AD001", "Admin System", "admin", "admin@fpt.edu.vn", defaultPassHash, helper.dateToLong("1990-01-01"), "0123456789", "FPT System Office", 5, null,true));
                userDao.insertUser(new User("SE180001", "Sinh Viên An", "student1", "student1@fpt.edu.vn", defaultPassHash, helper.dateToLong("2004-01-01"), "0930000001", "KTX Hà Nội", 1, 1,true));
                userDao.insertUser(new User("SE180002", "Sinh Viên B", "student2", "student2@fpt.edu.vn", defaultPassHash, helper.dateToLong("2004-01-01"), "0930000001", "KTX Hà Nội", 1, 1,true));
                userDao.insertUser(new User("LEC001", "Trần Văn A", "lecturer1", "lecturer1@fpt.edu.vn", defaultPassHash, helper.dateToLong("1985-01-01"), "0900000001", "Hà Nội", 2, 1,true));
                userDao.insertUser(new User("ACA001", "Phòng Đào Tạo 1", "academic1", "academic1@fpt.edu.vn", defaultPassHash, helper.dateToLong("1991-01-01"), "0910000001", "VP Hà Nội", 3, 1,true));
                userDao.insertUser(new User("EXAM001", "Phòng Khảo Thí 1", "exam1", "exam1@fpt.edu.vn", defaultPassHash, helper.dateToLong("1994-01-01"), "0920000001", "VP Hà Nội", 4, 1,true));

                // === BƯỚC 3: CHÈN CÁC BẢNG PHỤ THUỘC (CẤP 2) ===

                // 3.1. Chèn Courses (phụ thuộc Major) (ID: 1-4)
                courseDao.insertCourse(new Course("SWP391", "Software Project", 3, 1, null));          // CourseID=1 (SE)
                courseDao.insertCourse(new Course("SWT301", "Software Testing", 3, 1, null));          // CourseID=2 (SE)
                courseDao.insertCourse(new Course("PRJ301", "Java Web Application", 3, 1, null));      // CourseID=3 (SE)
                courseDao.insertCourse(new Course("MKT101", "Marketing Principles", 3, 3, null));     // CourseID=4 (IB)

                // 3.2. Chèn Classes (phụ thuộc Course, Semester, User, Campus) (ID: 1)
                classDao.insertClass(new AcademicClass(1, 2, "LEC001", 1, 50)); // ClassID=1 (Môn SWP391, kỳ Fall 2025)

                // === BƯỚC 4: CHÈN CÁC BẢNG PHỤ THUỘC (CẤP 3) ===

                // 4.1. Chèn Schedules (phụ thuộc Class, TimeSlot)
                scheduleDao.insertSchedule(new Schedule(1, 2, 1, "BE-301")); // Lớp 1, Thứ 2, Slot 1
                scheduleDao.insertSchedule(new Schedule(1, 4, 2, "BE-302")); // Lớp 1, Thứ 4, Slot 2

                // 4.2. Chèn Enrollments (phụ thuộc User, Class)
                enrollmentDao.insertEnrollment(new Enrollment("SE180001", 1, helper.dateToLong("2025-08-23") , "Enrolled"));
                attendanceDao.insertAttendance(new Attendance("SE180001", 1, helper.dateToLong("2025-11-03"), "Present"));
                // T4, 05/11/2025
                attendanceDao.insertAttendance(new Attendance("SE180001", 1, helper.dateToLong("2025-11-05"), "Absent"));

                // Tuần 10/11/2025:
                // T2, 10/11/2025
                attendanceDao.insertAttendance(new Attendance("SE180001", 1, helper.dateToLong("2025-11-10"), "Present"));

                gradeComponentDao.insertGradeComponent(new GradeComponent(1, "Iteration 1", 0.1f));
                gradeComponentDao.insertGradeComponent(new GradeComponent(1, "Iteration 2", 0.1f));
                gradeComponentDao.insertGradeComponent(new GradeComponent(1, "Presentation", 0.3f));
                 gradeComponentDao.insertGradeComponent(new GradeComponent(1, "Final Exam (FE)", 0.5f));

                gradeDao.upsertGrade(new Grade("SE180001", 1, 8.5f));
                gradeDao.upsertGrade(new Grade("SE180001", 2, 9.0f));

                examScheduleDao.insertExamSchedule(new ExamSchedule(
                                1, // ClassID=1
                                helper.dateToLong("2025-12-15"), // ExamDate (15/12/2025)
                                "09:00", // StartTime
                                "11:00", // EndTime
                                "A201", // RoomNumber
                                "LEC001" // InvigilatorID (LEC001)
                        ));
                applicationTypeDao.insertAppType(new ApplicationType("Đơn xin bảo lưu", true));
                applicationTypeDao.insertAppType(new ApplicationType("Đơn xin phúc khảo", true));
                applicationTypeDao.insertAppType(new ApplicationType("Đơn xin học lại", true));
                applicationTypeDao.insertAppType(new ApplicationType("Đơn xin thôi học", true));

            });
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "FinalProjectPRM392.db")
                            .addCallback(roomCallback)
                            .fallbackToDestructiveMigration()
                              .build();
                }
            }
        }
        return INSTANCE;
    }
}