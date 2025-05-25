const express = require('express');
const mongoose = require('mongoose');
const bcrypt = require('bcrypt');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const axios = require('axios');
const crypto = require('crypto'); // Import the crypto module
const Artist = require('./src/models/artist.model.js');


const Client_id = 'afbf103ccf453fadcf90' ;
const Client_secret = 'dab3534aa4df422dd0011cf6ab9e5e8e' ;
const authenticate_url =  'https://api.artsy.net/api/tokens/xapp_token';
const search_url = 'https://api.artsy.net/api/search';
const artist_details_url = 'https://api.artsy.net/api/artists';
const categories_url = 'https://api.artsy.net/api/genes'

// mongoose.connect('mongodb+srv://paavani:12345@cluster0.huywk.mongodb.net/HW3?retryWrites=true&w=majority&appName=Cluster0')
//   .then(() => console.log('Connected to MongoDB'))
//   .catch(error => console.error('Could not connect to MongoDB', error));

mongoose.connect(process.env.MONGODB_URI || 'mongodb+srv://paavani:12345@cluster0.huywk.mongodb.net/HW3?retryWrites=true&w=majority&appName=Cluster0')
  .then(() => console.log('Connected to MongoDB'))
  .catch(error => console.error('Could not connect to MongoDB', error));


const User = require('./src/models/user.model.js');
const { search } = require('./src/routes/auth.routes.js');
const path = require('path'); // Ensure this is imported at the top of your file


const app = express();
app.use(express.json());
// app.use(cors({
//   origin: 'http://localhost:4200', // Allow requests from this origin
//   methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
//   allowedHeaders: 'Content-Type,Authorization',
//   preflightContinue: false
// }));
// Replace your current CORS setup with this
app.use(cors({
  origin: "https://frontend-dot-paavs-artsy-assignment3.wl.r.appspot.com",
  methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
  allowedHeaders: 'Content-Type,Authorization',
  preflightContinue: false
}));

const secretKey = 'your-secret-key'; // Change this to a secure secret key

app.post('/api/auth/register', async (req, res) => {
  try {
    const { name, email, password } = req.body;

    if (!name || !email || !password) {
      return res.status(400).send('Please provide all required fields');
    }

    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(400).json({ field: 'email', message: 'User with this email already exists.' });
    }

    // Hash password
    const salt = await bcrypt.genSalt(10);
    // const hashedPassword = await bcrypt.hash(password, salt);

    // Calculate SHA256 hash of email
    const emailHash = crypto
      .createHash('sha256')
      .update(email.trim().toLowerCase())
      .digest('hex');

    // Generate Gravatar URL
    const gravatarUrl = `https://www.gravatar.com/avatar/${emailHash}?d=identicon`;

    // Create new user
    const user = new User({ name, email, password, profileImageUrl: gravatarUrl, favourites: []});
    await user.save();

    // Generate JWT token
    const token = jwt.sign({ userId: user._id, name: user.name, email: user.email }, secretKey, { expiresIn: '1h' });
    res.json({ token });
  } catch (error) {
    console.error('Error registering user:', error);
    if (error.name === 'ValidationError') {
      res.status(400).json({ message: 'Validation error', error: error.message });
    } else {
      res.status(500).json({ message: 'Failed to register user', error: error.message });
    }
  }
});

app.post('/api/auth/login', async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).send('Please provide all required fields');
    }

    const user = await User.findOne({ email });
    if (!user) {
      return res.status(401).json({ message: 'Error 1' });
    }

    console.log('Plain Text Password:', password);
    console.log('Hashed Password:', user.password);

    const salt = await bcrypt.genSalt(10);
    const isValidPassword = await bcrypt.compare(password, user.password);
    // const isValidPassword = password == user.password
    console.log(bcrypt.hash(password, salt))
    if (!isValidPassword) {
      return res.status(401).json({ message: 'Error 2' });
    }

    // Generate JWT token
    const token = jwt.sign({ userId: user._id, name: user.name, email: user.email }, secretKey, { expiresIn: '1h' });
    res.json({ token });
  } catch (error) {
    console.error('Error logging in user:', error);
    res.status(500).json({ message: 'Failed to login user', error: error.message });
  }
});

app.post('/api/auth/logout', (req, res) => {
  // If you are using cookies, clear them here.
  // res.clearCookie('authToken');
  res.json({ message: 'Logged out successfully' });
});

const verifyToken = (req, res, next) => {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    return res.status(401).json({ message: 'Unauthorized' });
  }

  jwt.verify(token, 'your-secret-key', (err, decoded) => {
    if (err) {
      return res.status(401).json({ message: 'Invalid token' });
    }
    req.userId = decoded.userId; // Store user ID in request object
    next();
  });
};

// Delete Account Endpoint
app.delete('/api/auth/delete-account', verifyToken, async (req, res) => {
  try {
    // Delete user from the database
    await User.findByIdAndDelete(req.userId);
    res.json({ message: 'Account deleted successfully' });
  } catch (error) {
    console.error('Error deleting account:', error);
    res.status(500).json({ message: 'Failed to delete account' });
  }
});


app.get('/api/auth/user-profile', verifyToken, async (req, res) => {
  try {
    const user = await User.findById(req.userId);
    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    res.json({
      name: user.name,
      email: user.email,
      profileImageUrl: user.profileImageUrl, // Retrieve Gravatar URL from database
    });
  } catch (error) {
    console.error('Error fetching user profile:', error);
    res.status(500).json({ message: 'Failed to fetch user profile' });
  }
});

// ... other middleware and routes

app.post('/api/auth/favourites/:artistId', verifyToken, async (req, res) => {
  console.log("Hi, Inside post favs");
  try {
    const userId = req.userId;
    const artistId = req.params.artistId;
    const { name, nationality, years, imageUrl } = req.body; // Get data from request body
    console.log("Imageurl = ",imageUrl);

    // Check if the artist already exists in the local database
    let artist = await Artist.findOne({ artistId: artistId, userId: userId });

    if (!artist) {
      // Create a new artist document with the data from the request body
      artist = new Artist({
        artistId: artistId,
        userId: userId,
        name: name,
        nationality: nationality,
        years: years,
        imageUrl: imageUrl,
        created_at: new Date()
      });
      await artist.save();
    }

    // Check if the user exists
    const user = await User.findById(userId);
    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    // Check if the artist is already in favorites
    if (user.favourites.includes(artist._id)) {
      return res.status(400).json({ message: 'Artist already in favorites' });
    }

    // Add artist to favorites
    user.favourites.push(artist._id);
    await user.save();

    res.status(201).json({
      message: 'Artist added to favorites',
      created_at: artist.created_at // Include the timestamp here
    });
  } catch (error) {
    console.error('Error adding to favorites:', error);
    res.status(500).json({ message: 'Failed to add to favorites' });
  }
});

// ... other routes and server setup
// Remove artist from favorites
app.delete('/api/auth/favourites/:artistId', verifyToken, async (req, res) => {
  try {
    const userId = req.userId;
    const artistId = req.params.artistId;

    // Check if the user exists
    const user = await User.findById(userId);
    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    // Find the artist in the Artist collection by artistId and userId
    const artist = await Artist.findOne({ artistId: artistId, userId: userId });

    if (!artist) {
      return res.status(404).json({ message: 'Artist not found in your favorites' });
    }

    // Check if the artist is in favorites
    if (!user.favourites.includes(artist._id)) {
      return res.status(400).json({ message: 'Artist not in favorites' });
    }

    // Remove artist from favorites in the User collection
    user.favourites = user.favourites.filter((id) => id.toString() !== artist._id.toString());
    await user.save();
    await Artist.findOneAndDelete({ artistId: artistId, userId: userId });

    res.json({ message: 'Artist removed from favorites' });
  } catch (error) {
    console.error('Error removing from favorites:', error);
    res.status(500).json({ message: 'Failed to remove from favorites' });
  }
});

app.get('/api/auth/favourites', verifyToken, async (req, res) => {
  try {
    const userId = req.userId;

    // Find the user and populate the favourites array
    const user = await User.findById(userId).populate({
      path: 'favourites',
      model: 'Artist',
      select: 'artistId name nationality years imageUrl created_at', // Select the fields you need
    });

    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }
    console.log("USER=", user.favourites);

    res.json(user.favourites); // Send the populated favourites array
  } catch (error) {
    console.error('Error fetching favorites:', error);
    res.status(500).json({ message: 'Failed to fetch favorites' });
  }
});

app.get('/api/auth/favourites_array', verifyToken, async (req, res) => {
  try {
    const userId = req.userId;

    // Find the user and get the favourites array
    const user = await User.findById(userId);

    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    res.json(user.favourites); // Send the favourites array
  } catch (error) {
    console.error('Error fetching favorites:', error);
    res.status(500).json({ message: 'Failed to fetch favorites' });
  }
});

function authenticateToken(req, res, next) {
  const token = req.header('Authorization');
  if (!token) return res.status(401).send('Access denied. No token provided.');

  try {
    const decoded = jwt.verify(token, secretKey);
    req.user = decoded;
    next();
  } catch (ex) {
    res.status(400).send('Invalid token.');
  }
}

// Example protected route
app.get('/protected', authenticateToken, (req, res) => {
  res.send('Hello, ' + req.user.name);
});

app.use((err, req, res, next) => {
  console.error('Error:', err);
  res.status(500).send({ message: 'Internal Server Error', error: err.message });
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}`);
});
// const PORT = process.env.PORT || 8080;
// app.listen(PORT, () => console.log(`Server running on port ${PORT}`));


//Artsy api Code

function getToken() {
  return axios.post(authenticate_url, {
    client_id: Client_id,
    client_secret: Client_secret
  }).then(response => response.data.token);
}

app.get('/search', async (req, res) => {
  try {
    const xAppToken = await getToken(); // Await the promise
    console.log('X-App-Token:', xAppToken);

    const headers = {
      'X-XAPP-Token': xAppToken // Include token in headers
    };

    const params = {
      q: req.query.q, // Search query
      size: 10, // Limit results to 10
      type: 'artist' // Search for artists only
    };

    const searchResponse = await axios.get(search_url, { headers, params });
    res.json(searchResponse.data);
  } catch (error) {
    console.error('Error searching for artist:', error);
    res.status(500).json({ message: 'Failed to search for artist', error: error.message });
  }
});

app.get('/artist_details/:artist_id', async (req, res) => {
  try {
    const artistId = req.params.artist_id;
    const xAppToken = await getToken(); // Await the promise
    console.log('Hi, Artist ID:', artistId);

    const headers = {
      'X-XAPP-Token': xAppToken // Include token in headers
    };

    const url = `${artist_details_url}/${artistId}`;
    console.log(url);

    const detailsResponse = await axios.get(url, { headers });
    res.json(detailsResponse.data);
  } catch (error) {
    console.error('Error searching for artist:', error);
    res.status(500).json({ message: 'Failed to search for artist', error: error.message });
  }

});

app.get('/artworks', async (req, res) => {
  try {
    const artistId = req.query.artist_id;
    const xAppToken = await getToken(); // Await the promise
    console.log('Hi, Artist ID:', artistId);

    const headers = {
      'X-XAPP-Token': xAppToken // Include token in headers
    };

    const url = `https://api.artsy.net/api/artworks?artist_id=${artistId}&size=10`
    console.log(url);

    const artworkResponse = await axios.get(url, { headers});
    res.json(artworkResponse.data);
  } catch (error) {
    console.error('Error searching for artist:', error);
    res.status(500).json({ message: 'Failed to search for artist', error: error.message });
  }

});

app.get('/categories', async (req, res) => {
  try{
    const artworkId = req.query.artwork_id;
    const xAppToken = await getToken();

    const headers = {
      'X-XAPP-Token': xAppToken // Include token in headers
    };

    const params = {
      artwork_id: artworkId // Required parameter
    }

    const gene_Response = await axios.get(categories_url, { headers, params });
    res.json(gene_Response.data);
  } catch (error) {
    console.error('Error searching for artist:', error);
    res.status(500).json({ message: 'Failed to search for artist', error: error.message });
  }
  
});

app.get('/test', (req, res) => {
  console.log("Test route hit!");
  res.send("Server is working!");
});

// app.get('/similar_artists/:abc_id', (req, res) => {
//   console.log("Inside server.js - similar artists route hit!");
//   res.json({ message: "This route is working!" });
// });



app.get('/similar_artists/:abc_id', async (req, res) => {
  console.log("Inside server.js");
  try {
    const artistId = req.params.abc_id;
    const xAppToken = await getToken();

    const headers = {
      'X-XAPP-Token': xAppToken
    };

    const url = `https://api.artsy.net/api/artists?similar_to_artist_id=${artistId}`;
    const response = await axios.get(url, { headers });

    res.json(response.data);
  } catch (error) {
    console.error('Error fetching similar artists:', error.message);
    res.status(500).json({ message: 'Failed to fetch similar artists', error: error.message });
  }
});

app.use((err, req, res, next) => {
  console.error('Error:', err);
  res.status(500).json({ message: 'Internal Server Error', error: err.message });
});

