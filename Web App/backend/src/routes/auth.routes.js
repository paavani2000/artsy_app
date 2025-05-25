const express = require('express');
const router = express.Router();
const { registerUser, loginUser } = require('../controllers/auth.controller');
const User = require('../models/user.model');

router.post('/register', registerUser);
router.post('/login', loginUser);

module.exports = router;
