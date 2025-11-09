--alt+Insert: insert getter-setter Androind studio

-- 1. TẠO CƠ SỞ DỮ LIỆU
CREATE DATABASE FinalProjectPRM392;
GO

-- 2. CHUYỂN SANG SỬ DỤNG DATABASE MỚI
USE FinalProjectPRM392;
GO

-- 3. TẠO CÁC BẢNG KHÔNG CÓ KHÓA NGOẠI (HOẶC CHỈ TỰ THAM CHIẾU)

CREATE TABLE Roles (
    RoleID INT PRIMARY KEY IDENTITY(1,1),
    RoleName NVARCHAR(50) NOT NULL UNIQUE
);
GO


CREATE TABLE Campuses (
    CampusID INT PRIMARY KEY IDENTITY(1,1),
    CampusName NVARCHAR(100) NOT NULL,
    Address NVARCHAR(255)
);
GO 

-- 'Faculties' (Khoa)
CREATE TABLE Faculties (
    FacultyID INT PRIMARY KEY IDENTITY(1,1),
    FacultyName NVARCHAR(100) NOT NULL -- Ví dụ: 'Khoa Kỹ thuật phần mềm'
);
GO

-- Bảng 'Majors' (Chuyên ngành) bây giờ sẽ liên kết với 'Faculties'
CREATE TABLE Majors (
    MajorID INT PRIMARY KEY IDENTITY(1,1),
    MajorName NVARCHAR(100) NOT NULL,
    -- Khóa ngoại liên kết đến bảng Faculties thay vì Departments
    FacultyID INT NOT NULL FOREIGN KEY REFERENCES Faculties(FacultyID) 
);
GO

CREATE TABLE Semesters (
    SemesterID INT PRIMARY KEY IDENTITY(1,1),
    SemesterName NVARCHAR(50) NOT NULL UNIQUE,
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    RegistrationStartDate DATE NOT NULL,
    RegistrationEndDate DATE NOT NULL
);
GO

CREATE TABLE TimeSlots (
    SlotID INT PRIMARY KEY IDENTITY(1,1),
    StartTime TIME NOT NULL,
    EndTime TIME NOT NULL
);
GO
 

-- 4. TẠO CÁC BẢNG CÓ KHÓA NGOẠI (PHỤ THUỘC)

CREATE TABLE Users (
    UserCode VARCHAR(20) PRIMARY KEY, -- Khóa chính, không tự tăng
    FullName NVARCHAR(100) NOT NULL, -- Bổ sung trường tên đầy đủ
    Username VARCHAR(100) NOT NULL UNIQUE,
	Gmail VARCHAR(255) NOT NULL UNIQUE,
	CampusID INT NULL,
    HashedPassword NVARCHAR(MAX) NOT NULL,
    RoleID INT NOT NULL FOREIGN KEY REFERENCES Roles(RoleID),
    DateOfBirth DATE,
    PhoneNumber VARCHAR(15),
    Address NVARCHAR(255)
);
GO

CREATE TABLE Courses (
    CourseID INT PRIMARY KEY IDENTITY(1,1),
    CourseCode VARCHAR(10) NOT NULL UNIQUE,
    CourseName NVARCHAR(100) NOT NULL,
    Credits INT NOT NULL,
    MajorID INT NOT NULL FOREIGN KEY REFERENCES Majors(MajorID),
    PrerequisiteCourseID INT NULL -- Sẽ thêm FK ở bước sau
);
GO

CREATE TABLE Classes (
    ClassID INT PRIMARY KEY IDENTITY(1,1),
    CourseID INT NOT NULL FOREIGN KEY REFERENCES Courses(CourseID),
    SemesterID INT NOT NULL FOREIGN KEY REFERENCES Semesters(SemesterID),
    LecturerID VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES Users(UserCode),
    CampusID INT NOT NULL FOREIGN KEY REFERENCES Campuses(CampusID),
    MaxSize INT
);
GO

CREATE TABLE Schedules (
    ScheduleID INT PRIMARY KEY IDENTITY(1,1),
    ClassID INT NOT NULL FOREIGN KEY REFERENCES Classes(ClassID),
    DayOfWeek TINYINT NOT NULL,
    SlotID INT NOT NULL FOREIGN KEY REFERENCES TimeSlots(SlotID),
    RoomNumber VARCHAR(20)
);
GO

CREATE TABLE Enrollments (
    EnrollmentID INT PRIMARY KEY IDENTITY(1,1),
    StudentID VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES Users(UserCode),
    ClassID INT NOT NULL FOREIGN KEY REFERENCES Classes(ClassID),
    RegistrationDate DATETIME DEFAULT GETDATE(),
    Status NVARCHAR(20),
    CONSTRAINT UQ_Student_Class UNIQUE (StudentID, ClassID) -- Đảm bảo SV không đăng ký 1 lớp 2 lần
);
GO

CREATE TABLE GradeComponents (
    ComponentID INT PRIMARY KEY IDENTITY(1,1),
    ClassID INT NOT NULL FOREIGN KEY REFERENCES Classes(ClassID),
    ComponentName NVARCHAR(100) NOT NULL,
    Weight DECIMAL(5, 2) NOT NULL -- Ví dụ: 0.10 (10%), 0.30 (30%)
);
GO

CREATE TABLE Grades (
    GradeID INT PRIMARY KEY IDENTITY(1,1),
    StudentID VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES Users(UserCode),
    ComponentID INT NOT NULL FOREIGN KEY REFERENCES GradeComponents(ComponentID),
    Score DECIMAL(5, 2) NOT NULL,
    CONSTRAINT UQ_Student_Component UNIQUE (StudentID, ComponentID) -- Mỗi SV chỉ có 1 điểm cho 1 thành phần
);
GO

CREATE TABLE Attendance (
    AttendanceID INT PRIMARY KEY IDENTITY(1,1),
    StudentID VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES Users(UserCode),
    ClassID INT NOT NULL FOREIGN KEY REFERENCES Classes(ClassID),
    SessionDate DATE NOT NULL,
    Status NVARCHAR(10) NOT NULL, -- 'Present', 'Absent', 'Not Yet'
    CONSTRAINT UQ_Student_Class_Date UNIQUE (StudentID, ClassID, SessionDate) -- Mỗi SV 1 trạng thái/ngày/lớp
);
GO

CREATE TABLE ExamSchedules (
    ExamID INT PRIMARY KEY IDENTITY(1,1),
    ClassID INT NOT NULL FOREIGN KEY REFERENCES Classes(ClassID),
    ExamDate DATE NOT NULL,
    StartTime TIME NOT NULL,
    EndTime TIME NOT NULL,
    RoomNumber VARCHAR(20),
    InvigilatorID VARCHAR(20) NULL FOREIGN KEY REFERENCES Users(UserCode) -- Cán bộ coi thi
);
GO


---------
CREATE TABLE ApplicationTypes (
    AppTypeID INT PRIMARY KEY IDENTITY(1,1),
    TypeName NVARCHAR(100) NOT NULL UNIQUE
);
GO
-------
CREATE TABLE Announcements (
    AnnouncementID INT PRIMARY KEY IDENTITY(1,1),
    Title NVARCHAR(200) NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    PublishDate DATETIME DEFAULT GETDATE(),
    AuthorID VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES Users(UserCode),
    Category NVARCHAR(50) -- 'Academic', 'Exam', 'General'
);
GO

------
CREATE TABLE Applications (
    ApplicationID INT PRIMARY KEY IDENTITY(1,1),
    StudentID VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES Users(UserCode),
    AppTypeID INT NOT NULL FOREIGN KEY REFERENCES ApplicationTypes(AppTypeID),
    SubmissionDate DATETIME DEFAULT GETDATE(),
    Content NVARCHAR(MAX),
    Status NVARCHAR(20) NOT NULL DEFAULT 'Pending', -- 'Pending', 'Approved', 'Rejected'
    HandlerID VARCHAR(20) NULL FOREIGN KEY REFERENCES Users(UserCode), -- Người xử lý
    ResponseContent NVARCHAR(MAX)
);
GO

-- 5. BỔ SUNG KHÓA NGOẠI TỰ THAM CHIẾU CHO BẢNG COURSES
ALTER TABLE Courses
ADD CONSTRAINT FK_Courses_Prerequisite
FOREIGN KEY (PrerequisiteCourseID) REFERENCES Courses(CourseID);
GO

ALTER TABLE Users
ADD CONSTRAINT FK_Users_Campuses
FOREIGN KEY (CampusID) REFERENCES Campuses(CampusID);
GO

PRINT 'Database and tables created successfully.';

-- 1️⃣ Chọn database cần thao tác
USE master;
GO

-- 2️⃣ Đặt database về chế độ SINGLE_USER và rollback tất cả kết nối khác
ALTER DATABASE [FinalProjectPRM392] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
GO

-- 3️⃣ Xóa database
DROP DATABASE [FinalProjectPRM392];
GO
