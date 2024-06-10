const express = require('express');
const router = express.Router();
const taskController = require('../controllers/task.controller.js');

router.get('/getAllTask', taskController.getAllTasks);
router.get('/getTaskById/:task_id', taskController.getTaskById);
router.post('/addTask', taskController.addTask);
router.put('/updateTask/:task_id', taskController.updateTask);
router.delete('/deleteTask/:task_id', taskController.deleteTask);

module.exports = router;
