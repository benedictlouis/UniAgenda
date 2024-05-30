const express = require('express');
const router = express.Router();
const userController = require("../repositories/user.repository.js");

router.post("/login", userController.login);
router.post("/signup", userController.signup);
router.get("/getAllUser", userController.getAllUser);
router.delete("/deleteUser", userController.deleteUser);

module.exports = router;