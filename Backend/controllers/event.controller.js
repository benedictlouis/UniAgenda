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

// Get event by event ID
exports.getEventById = async (req, res) => {
    const { event_id } = req.params;
    try {
        const result = await pool.query("SELECT * FROM event WHERE event_id = $1", [event_id]);
        if (result.rows.length === 0) {
            return res.status(404).send("Event not found");
        }
        res.status(200).json(result.rows[0]);
    } catch (error) {
        console.error("Error while fetching event:", error);
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

// Update an event by event ID and account ID
exports.updateEvent = async (req, res) => {
    const { event_id } = req.params;
    const { event_title, event_date, event_description, location, account_id } = req.body;
    try {
        const result = await pool.query(
            // add update to accomodate account_id
            "UPDATE event SET event_title = $1, event_date = $2, event_description = $3, location = $4 WHERE event_id = $5 AND account_id = $6",
            [event_title, event_date, event_description, location, event_id, account_id]
        );
        if (result.rowCount === 0) {
            return res.status(404).send("Event not found");
        }
       
        const updatedEvent = await pool.query("SELECT * FROM event WHERE event_id = $1 AND account_id = $2", [event_id, account_id]);
        res.status(200).json(updatedEvent.rows[0]);
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

