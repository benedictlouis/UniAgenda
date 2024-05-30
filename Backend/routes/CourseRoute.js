const express = require('express');
const router = express.Router();
const courseController = require("../repositories/course.repository.js");

router.post("/addCourse", courseController.addCourse);
router.get("/getAllCourse", courseController.getAllCourse);
router.get("/getCourseById/:course_id", courseController.getCourseById);
router.put("/updateCourse", courseController.updateCourse);
router.delete("/deleteCourse/:course_id", courseController.deleteCourse);

module.exports = router;