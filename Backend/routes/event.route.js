const express = require('express');
const router = express.Router();
const eventController = require('../controllers/event.controller.js');

router.get('/getEventByUserId/:account_id', eventController.getEventByUserId);
router.get('/getEventById/:event_id', eventController.getEventById);
router.post('/addEvent', eventController.addEvent);
router.put('/updateEvent/:event_id', eventController.updateEvent);
router.delete('/deleteEvent/:event_id', eventController.deleteEvent);
router.get('/getAllEvent', eventController.getAllEvent);

module.exports = router;