const { pool } = require("../config/db.config.js");
const { v4: uuidv4 } = require('uuid');

// Get all courses
exports.getAllCourse = async (req, res) => {
    try {
        const result = await pool.query("SELECT * FROM course");
        res.status(200).json(result.rows);
    } catch (error) {
        console.error("Error while fetching courses:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Get a single course by ID
exports.getCourseById = async (req, res) => {
    const { course_id } = req.params;
    try {
        const result = await pool.query("SELECT * FROM course WHERE course_id = $1", [course_id]);
        if (result.rows.length === 0) {
            return res.status(404).send("Course not found");
        }
        res.status(200).json(result.rows[0]);
    } catch (error) {
        console.error("Error while fetching course:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Add a new course
exports.addCourse = async (req, res) => {
    const { course_name, course_code, lecturer } = req.body;
    const course_id = uuidv4(); // Generate UUID for the new course
    try {
        await pool.query(
            "INSERT INTO course (course_id, course_name, course_code, lecturer) VALUES ($1, $2, $3, $4)",
            [course_id, course_name, course_code, lecturer]
        );
        res.status(201).send("Course created successfully");
    } catch (error) {
        console.error("Error while creating course:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Update a course by ID
exports.updateCourse = async (req, res) => {
    const { course_id } = req.params;
    const { course_name, course_code, lecturer } = req.body;
    try {
        const result = await pool.query(
            "UPDATE course SET course_name = $1, course_code = $2, lecturer = $3, updated_at = CURRENT_TIMESTAMP WHERE course_id = $4",
            [course_name, course_code, lecturer, course_id]
        );
        if (result.rowCount === 0) {
            return res.status(404).send("Course not found");
        }
        res.status(200).send("Course updated successfully");
    } catch (error) {
        console.error("Error while updating course:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Delete a course by ID
exports.deleteCourse = async (req, res) => {
    const { course_id } = req.params;
    try {
        const result = await pool.query("DELETE FROM course WHERE course_id = $1", [course_id]);
        if (result.rowCount === 0) {
            return res.status(404).send("Course not found");
        }
        res.status(200).send("Course deleted successfully");
    } catch (error) {
        console.error("Error while deleting course:", error);
        res.status(500).send("Internal Server Error");
    }
};
