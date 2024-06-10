const { pool } = require("../config/db.config.js");

// Get all tasks
exports.getAllTasks = async (req, res) => {
    try {
        const result = await pool.query("SELECT * FROM task");
        res.status(200).json(result.rows);
    } catch (error) {
        console.error("Error while fetching tasks:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Get a single task by ID
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

// Get task by account ID
exports.getTaskByUserId = async (req, res) => {
    const { account_id } = req.params;
    try {
        const result = await pool.query("SELECT * FROM task WHERE account_id = $1", [account_id]);
        res.status(200).json(result.rows);
    } catch (error) {
        console.error("Error while fetching tasks:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Add a new task
exports.addTask = async (req, res) => {
    const { task_title, course, task_description, task_deadline, task_status, task_type, account_id } = req.body;
    try {
        await pool.query(
            "INSERT INTO task (task_title, course, task_description, task_deadline, task_status, task_type, account_id) VALUES ($1, $2, $3, $4, $5, $6, $7)",
            [task_title, course, task_description, task_deadline, task_status, task_type, account_id]
        );
        res.status(201).send("Task created successfully");
    } catch (error) {
        console.error("Error while creating task:", error);
        res.status(500).send("Internal Server Error");
    }
};


// Update a task by ID
exports.updateTask = async (req, res) => {
    const { task_id } = req.params;
    const { task_title, course, task_description, task_deadline, task_status, task_type, account_id } = req.body;
    try {
        const result = await pool.query(
            "UPDATE task SET task_title = $1, course = $2, task_description = $3, task_deadline = $4, task_status = $5, task_type = $6 WHERE task_id = $7 AND account_id = $8",
            [task_title, course, task_description, task_deadline, task_status, task_type, task_id, account_id]
        );
        if (result.rowCount === 0) {
            return res.status(404).send("Task not found or you do not have permission to update this task");
        }
        // if update is successful, return the updated task
        const updatedTask = await pool.query("SELECT * FROM task WHERE task_id = $1 AND account_id = $2", [task_id, account_id]);
        res.status(200).json(updatedTask.rows[0]);
    } catch (error) {
        console.error("Error while updating task:", error);
        res.status(500).send("Internal Server Error");
    }
};


// Delete a task by ID
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
