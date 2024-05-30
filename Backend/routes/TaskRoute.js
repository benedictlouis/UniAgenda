const express = require('express');
const router = express.Router();
const taskRepository = require('../repositories/task.repository.js');

router.get('/getAllTask', taskRepository.getAllTasks);
router.get('/getTaskById/:task_id', taskRepository.getTaskById);
router.post('/addTask', taskRepository.addTask);
router.put('/updateTask/:task_id', taskRepository.updateTask);
router.delete('/deleteTask/:task_id', taskRepository.deleteTask);

module.exports = router;
