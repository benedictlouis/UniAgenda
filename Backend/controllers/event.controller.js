const { pool } = require("../config/db.config.js");
const { v4: uuidv4 } = require('uuid');

// Get all event
exports.getAllEvent = async (req, res) => {
    try {
        const result = await pool.query("SELECT * FROM event");
        res.status(200).json(result.rows);
    } catch (error) {
        console.error("Error while fetching events:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Get event by account ID
exports.getEventByUserId = async (req, res) => {
    const { account_id } = req.params;
    try {
        const result = await pool.query("SELECT * FROM event WHERE account_id = $1", [account_id]);
        res.status(200).json(result.rows);
    } catch (error) {
        console.error("Error while fetching events:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Add a new event
exports.addEvent = async (req, res) => {
    const { event_title, event_date, event_description, location, account_id } = req.body;
    try {
        await pool.query(
            "INSERT INTO event (event_title, event_date, event_description, location, account_id) VALUES ($1, $2, $3, $4, $5)",
            [event_title, event_date, event_description, location, account_id]
        );
        res.status(201).send("Event created successfully");
    } catch (error) {
        console.error("Error while creating event:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Update an event by ID
exports.updateEvent = async (req, res) => {
    const { event_id } = req.params;
    const { event_title, event_date, event_description, location, account_id } = req.body;
    try {
        const result = await pool.query(
            "UPDATE event SET event_title = $1, event_date = $2, event_description = $3, location = $4, updated_at = CURRENT_TIMESTAMP WHERE event_id = $5",
            [event_title, event_date, event_description, location, event_id]
        );
        if (result.rowCount === 0) {
            return res.status(404).send("Event not found");
        }
        res.status(200).send("Event updated successfully");
    } catch (error) {
        console.error("Error while updating event:", error);
        res.status(500).send("Internal Server Error");
    }
};

// Delete an event by ID
exports.deleteEvent = async (req, res) => {
    const { event_id } = req.params;
    try {
        const result = await pool.query("DELETE FROM event WHERE event_id = $1", [event_id]);
        if (result.rowCount === 0) {
            return res.status(404).send("Event not found");
        }
        res.status(200).send("Event deleted successfully");
    } catch (error) {
        console.error("Error while deleting event:", error);
        res.status(500).send("Internal Server Error");
    }
};

