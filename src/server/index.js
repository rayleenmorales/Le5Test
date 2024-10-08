const express = require('express');
const app = express();
const port = 3001;

// Middleware to parse incoming JSON request bodies
app.use(express.json());

// Sample in-memory user storage
const users = [];
const messages = {};

// Register endpoint
app.post('/register', (req, res) => {
    // Set the response content type to JSON
    res.setHeader('Content-Type', 'application/json');

    // Extracting the required fields from the request body
    const { firstName, lastName, password, profilePicture } = req.body;

    // Log the received request body
    console.log('Received registration request:', req.body);

    // Validate that all fields are provided
    if (!firstName || !lastName || !password || !profilePicture) {
        return res.status(400).send('All fields are required.');
    }

    // Check if the user already exists
    const userExists = users.find(user => user.firstName === firstName && user.lastName === lastName);
    if (userExists) {
        return res.status(400).send('User already exists.');
    }

    // Add the new user to the users array
    users.push({ firstName, lastName, password, profilePicture });

    // Initialize an empty message list for the user
    messages[`${firstName} ${lastName}`] = [];

    // Respond with success
    res.status(201).send('User registered successfully.');
});

// Start the server
app.listen(port, () => {
    console.log(`Server running on http://localhost:${port}`);
});

// Login route
app.post('/login', (req, res) => {
    res.setHeader('Content-Type', 'application/json');

    // Extracting data from the request body
    const { firstName, lastName, password } = req.body;

    // Log the incoming request body to the console
    console.log('Received Login Request: ', req.body);

    // Find the user in the registered users list
    const user = users.find(user => user.firstName === firstName && user.lastName === lastName && user.password === password);
    
    if (!user) {
        return res.status(400).send('Invalid credentials.');
    }

    // Check if the user is already logged in
    if (loggedInUsers.has(`${firstName} ${lastName}`)) {
        return res.status(400).send('User already logged in.');
    }

    // Log the user in
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

app.listen(3001, () => console.log('Server running on port 3001'));

app.post('/logout', (req, res) => {
    const { firstName, lastName } = req.body;

    if (!loggedInUsers.has(`${firstName} ${lastName}`)) {
        return res.status(400).send('User is not logged in.');
    }

    loggedInUsers.delete(`${firstName} ${lastName}`);
    res.status(200).send('Logout successful.');
});


app.get('/messages', (req, res) => {
    const { firstName, lastName } = req.query;
    const user = `${firstName} ${lastName}`;

    if (!messages[user]) {
        return res.status(404).send('User not found.');
    }

    res.status(200).json(messages[user]);
});


app.delete('/deleteAccount', (req, res) => {
    const { firstName, lastName, password } = req.body;

    const userIndex = users.findIndex(user => user.firstName === firstName && user.lastName === lastName && user.password === password);
    if (userIndex === -1) {
        return res.status(400).send('User not found or wrong credentials.');
    }

    users.splice(userIndex, 1);
    delete messages[`${firstName} ${lastName}`];
    loggedInUsers.delete(`${firstName} ${lastName}`);
    res.status(200).send('User deleted successfully.');
});