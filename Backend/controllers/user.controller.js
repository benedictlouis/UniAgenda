const { pool } = require("../config/db.config.js");
const { v4: uuidv4 } = require('uuid');
const bcrypt = require('bcrypt');

exports.login = async function (req, res) {
    const { email, password } = req.body;
    try {
        const result = await pool.query("SELECT * FROM account WHERE email = $1", [email]);
        
        if (result.rows.length === 0) {
            return res.status(404).send("Email tidak ditemukan");
        }
        
        const storedPassword = result.rows[0].password;
        const accountId = result.rows[0].account_id;

        const passwordMatch = await bcrypt.compare(password, storedPassword);
        if (!passwordMatch) {
            return res.status(401).send("Password salah");
        }
                
        res.status(200).json({ message: "Login berhasil", account_id: accountId });
    } catch (error) {
        console.error(error);
        res.status(500).send("Internal Server Error");
    }
}

exports.signup = async function (req, res) {
    const { username, email, password } = req.body;
    try {
        const hashedPassword = await bcrypt.hash(password, 10); // Hash password dengan salt rounds 10
        const accountId = uuidv4(); // Generate UUID

        await pool.query("INSERT INTO account (account_id, username, email, password) VALUES ($1, $2, $3, $4)", [accountId, username, email, hashedPassword]);
        res.status(201).send("Sukses signup");
    } catch (error) {
        console.log(error);
        res.status(500).send("Internal Server Error");
    }
}

exports.getAllUser = async function (req, res){
    try {
        const result = await pool.query("SELECT * FROM account");
        res.status(200).json(result.rows);
    } catch (error) {
        console.error("Error while fetching users:", error);
        res.status(500).send("Internal Server Error");
    }
}

exports.updateUser = async function (req, res) {
    const accountId = req.params.account_id;
    const { username, email, password } = req.body;
    try {
        const userResult = await pool.query("SELECT * FROM account WHERE account_id = $1", [accountId]);
        
        if (userResult.rows.length === 0) {
            return res.status(404).send("User tidak ditemukan");
        }

        let updateFields = [];
        let updateValues = [];
        let valueCounter = 1;

        if (username) {
            updateFields.push(`username = $${valueCounter}`);
            updateValues.push(username);
            valueCounter++;
        }
        
        if (email) {
            updateFields.push(`email = $${valueCounter}`);
            updateValues.push(email);
            valueCounter++;
        }
        
        if (password) {
            const hashedPassword = await bcrypt.hash(password, 10);
            updateFields.push(`password = $${valueCounter}`);
            updateValues.push(hashedPassword);
            valueCounter++;
        }

        if (updateFields.length === 0) {
            return res.status(400).send("Tidak ada data untuk diperbarui");
        }

        updateValues.push(accountId);
        const updateQuery = `UPDATE account SET ${updateFields.join(', ')} WHERE account_id = $${valueCounter}`;

        await pool.query(updateQuery, updateValues);

        res.status(200).send("User updated successfully");
    } catch (error) {
        console.error("Error while updating user:", error);
        res.status(500).send("Internal Server Error");
    }
}

exports.deleteUser = async function (req, res) {
    const accountId = req.params.account_id;
    try {
        const result = await pool.query("DELETE FROM account WHERE account_id = $1", [accountId]);
        res.status(200).send("User deleted successfully");
    } catch (error) {
        console.error("Error while deleting user:", error);
        res.status(500).send("Internal Server Error");
    }
}
