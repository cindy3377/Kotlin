

open class Human(val name: String, var age: Int) {
    fun getOlder() {
        age += 1
    }
}

class CourseRecord(
    val name: String,
    val yearCompleted: Int,
    val credits: Int,
    val grade: Double
)

class Student(name: String, age: Int) : Human(name, age) {
    val courses = mutableListOf<CourseRecord>()

    fun addCourse(course: CourseRecord) {
        courses.add(course)
    }

    fun weightedAverage(): Double {
        val totalCredits = courses.sumBy { it.credits }
        val totalWeightedGrades = courses.sumByDouble { it.credits * it.grade }
        return if (totalCredits > 0) totalWeightedGrades / totalCredits else 0.0
    }

    fun weightedAverage(year: Int): Double {
        val filteredCourses = courses.filter { it.yearCompleted == year }
        val totalCredits = filteredCourses.sumBy { it.credits }
        val totalWeightedGrades = filteredCourses.sumByDouble { it.credits * it.grade }
        return if (totalCredits > 0) totalWeightedGrades / totalCredits else 0.0
    }

    fun minMaxGrades(): Pair<Double, Double> {
        val grades = courses.map { it.grade }
        return Pair(grades.minOrNull() ?: 0.0, grades.maxOrNull() ?: 0.0)
    }
}

class Major(val name: String) {
    private val students = mutableListOf<Student>()

    fun addStudent(student: Student) {
        students.add(student)
    }

    fun stats(): Triple<Double, Double, Double> {
        val averages = students.map { it.weightedAverage() }
        val minAverage = averages.minOrNull() ?: 0.0
        val maxAverage = averages.maxOrNull() ?: 0.0
        val avgOfAverages = if (averages.isNotEmpty()) averages.average() else 0.0
        return Triple(minAverage, maxAverage, avgOfAverages)
    }

    fun stats(courseName: String): Triple<Double, Double, Double> {
        val courseAverages = students
            .filter { student -> studentHasCourse(student, courseName) }
            .map { student -> weightedAverageForCourse(student, courseName) }

        val minAverage = courseAverages.minOrNull() ?: 0.0
        val maxAverage = courseAverages.maxOrNull() ?: 0.0
        val avgOfAverages = if (courseAverages.isNotEmpty()) courseAverages.average() else 0.0
        return Triple(minAverage, maxAverage, avgOfAverages)
    }

    private fun studentHasCourse(student: Student, courseName: String): Boolean {
        return student.courses.any { it.name == courseName }
    }

    private fun weightedAverageForCourse(student: Student, courseName: String): Double {
        val courseRecords = student.courses.filter { it.name == courseName }
        val totalCredits = courseRecords.sumBy { it.credits }
        val totalWeightedGrades = courseRecords.sumByDouble { it.credits * it.grade }
        return if (totalCredits > 0) totalWeightedGrades / totalCredits else 0.0
    }
}

