const express = require('express');
const router = express.Router();
const taskRepository = require('../controllers/event.controller.js');

router.get('/getEventByUserId/:account_id', taskRepository.getEventByUserId);
router.post('/addEvent', taskRepository.addEvent);
router.put('/updateEvent/:event_id', taskRepository.updateEvent);
router.delete('/deleteEvent/:event_id', taskRepository.deleteEvent);
router.get('/getAllEvent', taskRepository.getAllEvent);

module.exports = router;