const { pool } = require("../config/db.config.js");
const { v4: uuidv4 } = require('uuid');

// Function to get all tasks
exports.getAllTasks = async (req, res) => {
    try {
        const result = await pool.query("SELECT * FROM task");
        res.status(200).json(result.rows);
    } catch (error) {
        console.error("Error while fetching tasks:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Function to get a single task by ID
exports.getTaskById = async (req, res) => {
    const { task_id } = req.params;
    try {
        const result = await pool.query("SELECT * FROM task WHERE task_id = $1", [task_id]);
        if (result.rows.length === 0) {
            return res.status(404).send("Task not found");
        }
        res.status(200).json(result.rows[0]);
    } catch (error) {
        console.error("Error while fetching task:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Function to create a new task
exports.addTask = async (req, res) => {
    const { task_title, course, task_description, task_deadline, task_status, task_type } = req.body;
    const task_id = uuidv4();
    try {
        await pool.query(
            "INSERT INTO task (task_id, task_title, course, task_description, task_deadline, task_status, task_type) VALUES ($1, $2, $3, $4, $5, $6, $7)",
            [task_id, task_title, course, task_description, task_deadline, task_status, task_type]
        );
        res.status(201).send("Task created successfully");
    } catch (error) {
        console.error("Error while creating task:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Function to update a task by ID
exports.updateTask = async (req, res) => {
    const { task_id } = req.params;
    const { account_id, course_id, task_title, task_description, task_assigned, task_deadline, task_status, task_type } = req.body;
    try {
        const result = await pool.query(
            "UPDATE task SET account_id = $1, course_id = $2, task_title = $3, task_description = $4, task_assigned = $5, task_deadline = $6, task_status = $7, task_type = $8 WHERE task_id = $9",
            [account_id, course_id, task_title, task_description, task_assigned, task_deadline, task_status, task_type, task_id]
        );
        if (result.rowCount === 0) {
            return res.status(404).send("Task not found");
        }
        res.status(200).send("Task updated successfully");
    } catch (error) {
        console.error("Error while updating task:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Function to delete a task by ID
exports.deleteTask = async (req, res) => {
    const { task_id } = req.params;
    try {
        const result = await pool.query("DELETE FROM task WHERE task_id = $1", [task_id]);
        if (result.rowCount === 0) {
            return res.status(404).send("Task not found");
        }
        res.status(200).send("Task deleted successfully");
    } catch (error) {
        console.error("Error while deleting task:", error);
        res.status(500).send("Internal Server Error");
    }
};
