// server/index.js
const express = require('express');
const bodyParser = require('body-parser');
const app = express();
app.use(bodyParser.json());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));



let users = []; // In-memory storage for users
let messages = {}; // In-memory storage for messages (by username)
let loggedInUsers = new Set(); // Track logged-in users

app.use((err, req, res, next) => {
    console.error(err.stack);
    res.status(400).send({ error: 'Bad Request' });
});

// Register a user
app.post('/register', (req, res) => {
    res.setHeader('Content-Type', 'application/json');
    const { firstName, lastName, password,  profilePicture} = req.body;

    if (!firstName || !lastName || !password || !profilePicture) {
        return res.status(400).send('All fields are required.');
    }

    const userExists = users.find(user => user.firstName === firstName && user.lastName === lastName);
    if (userExists) {
        return res.status(400).send('User already exists.');
    }

    users.push({ firstName, lastName, password, profilePicture });
    messages[`${firstName} ${lastName}`] = []; // Initialize an empty message list
    res.status(201).send('User registered successfully.');
});

// Login a user
app.post('/login', (req, res) => {
    const { firstName, lastName, password } = req.body;

    const user = users.find(user => user.firstName === firstName && user.lastName === lastName && user.password === password);
    if (!user) {
        return res.status(400).send('Invalid credentials.');
    }

    if (loggedInUsers.has(`${firstName} ${lastName}`)) {
        return res.status(400).send('User already logged in.');
    }

    loggedInUsers.add(`${firstName} ${lastName}`);
    res.status(200).send('Login successful.');
});

// Fetch users for search
app.get('/users', (req, res) => {
    res.json(users.map(user => `${user.firstName} ${user.lastName}`));
});

// Handle messaging
app.post('/message', (req, res) => {
    const { sender, receiver, message } = req.body;

    if (!sender || !receiver || !message) {
        return res.status(400).send('Invalid message.');
    }

    if (!messages[receiver]) {
        return res.status(404).send('Receiver not found.');
    }

    messages[receiver].push({ sender, message });
    res.status(200).send('Message sent.');
});

app.listen(3000, () => console.log('Server running on port 3000'));
